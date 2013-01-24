/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.virtualbox.functions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.compute.options.RunScriptOptions.Builder.runAsRoot;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_DEFAULT_DIR;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_IMAGE_PREFIX;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_NODE_NAME_SEPARATOR;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_PRECONFIGURATION_URL;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_WORKINGDIR;
import static org.jclouds.virtualbox.util.MachineUtils.machineNotFoundException;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.callables.RunScriptOnNode.Factory;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.location.Provider;
import org.jclouds.logging.Logger;
import org.jclouds.rest.annotations.BuildVersion;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementList;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.virtualbox.domain.HardDisk;
import org.jclouds.virtualbox.domain.IsoSpec;
import org.jclouds.virtualbox.domain.Master;
import org.jclouds.virtualbox.domain.MasterSpec;
import org.jclouds.virtualbox.domain.NetworkAdapter;
import org.jclouds.virtualbox.domain.NetworkInterfaceCard;
import org.jclouds.virtualbox.domain.NetworkSpec;
import org.jclouds.virtualbox.domain.StorageController;
import org.jclouds.virtualbox.domain.VmSpec;
import org.jclouds.virtualbox.domain.YamlImage;
import org.jclouds.virtualbox.functions.admin.PreseedCfgServer;
import org.jclouds.virtualbox.predicates.RetryIfSocketNotYetOpen;
import org.jclouds.virtualbox.statements.Md5;
import org.jclouds.virtualbox.util.NetworkUtils;
import org.virtualbox_4_2.CleanupMode;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.NetworkAttachmentType;
import org.virtualbox_4_2.StorageBus;
import org.virtualbox_4_2.VBoxException;
import org.virtualbox_4_2.VirtualBoxManager;

import com.google.common.base.CaseFormat;
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.cache.AbstractLoadingCache;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * A {@link LoadingCache} for masters. If the requested master has been
 * previously created this returns it, if not it coordinates its creation
 * including downloading isos and creating cache/config directories. This also
 * implements {@link Supplier} in order to provide jetty with the current image
 * (only one master can be created at a time).
 * 
 * @author dralves, andrea turli
 * 
 */
@Singleton
public class MastersLoadingCache extends AbstractLoadingCache<Image, Master> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Map<String, Master> masters = Maps.newHashMap();
   private final Function<MasterSpec, IMachine> masterCreatorAndInstaller;
   private final Map<String, YamlImage> imageMapping;
   private final String workingDir;
   private final String isosDir;
   private final Supplier<VirtualBoxManager> manager;
   private final String version;
   private final String preconfigurationUrl;

   private final Factory runScriptOnNodeFactory;
   private final RetryIfSocketNotYetOpen socketTester;
   private final Supplier<NodeMetadata> host;
   private final Supplier<URI> providerSupplier;
   private final HardcodedHostToHostNodeMetadata hardcodedHostToHostNodeMetadata;

   @Inject
   public MastersLoadingCache(@BuildVersion String version,
         @Named(VIRTUALBOX_PRECONFIGURATION_URL) String preconfigurationUrl,
         @Named(VIRTUALBOX_WORKINGDIR) String workingDir, Function<MasterSpec, IMachine> masterLoader,
         Supplier<Map<Image, YamlImage>> yamlMapper, Supplier<VirtualBoxManager> manager,
         Factory runScriptOnNodeFactory, RetryIfSocketNotYetOpen socketTester, Supplier<NodeMetadata> host,
         @Provider Supplier<URI> providerSupplier, HardcodedHostToHostNodeMetadata hardcodedHostToHostNodeMetadata) {
      this.manager = checkNotNull(manager, "vboxmanager can't be null");
      this.masterCreatorAndInstaller = masterLoader;
      this.workingDir = workingDir == null ? VIRTUALBOX_DEFAULT_DIR : workingDir;
      this.isosDir = workingDir + File.separator + "isos";
      this.imageMapping = Maps.newLinkedHashMap();
      for (Entry<Image, YamlImage> entry : yamlMapper.get().entrySet()) {
         this.imageMapping.put(entry.getKey().getId(), entry.getValue());
      }
      this.version = Iterables.get(Splitter.on('r').split(checkNotNull(version, "version")), 0);
      this.preconfigurationUrl = preconfigurationUrl;

      this.runScriptOnNodeFactory = checkNotNull(runScriptOnNodeFactory, "runScriptOnNodeFactory");
      this.socketTester = checkNotNull(socketTester, "socketTester");
      this.socketTester.seconds(3L);
      this.host = checkNotNull(host, "host");
      this.providerSupplier = checkNotNull(providerSupplier, "endpoint to virtualbox websrvd is needed");
      this.hardcodedHostToHostNodeMetadata = hardcodedHostToHostNodeMetadata;
   }

   @PostConstruct
   public void createCacheDirStructure() {
      if (!new File(workingDir).exists()) {
         new File(workingDir, "isos").mkdirs();
      }
   }

   @Override
   public synchronized Master get(Image key) throws ExecutionException {
      // check if we have loaded this machine before
      if (masters.containsKey(key.getId())) {
         return masters.get(key.getId());
      }
      checkState(!key.getId().contains(VIRTUALBOX_NODE_NAME_SEPARATOR), "master image names cannot contain \""
            + VIRTUALBOX_NODE_NAME_SEPARATOR + "\"");
      String vmName = VIRTUALBOX_IMAGE_PREFIX + key.getId();
      IMachine masterMachine;
      Master master;
      // ready the preseed file server
      PreseedCfgServer server = new PreseedCfgServer();
      try {
         // try and find a master machine in vbox
         masterMachine = manager.get().getVBox().findMachine(vmName);
         master = Master.builder().machine(masterMachine).build();
      } catch (VBoxException e) {
         if (machineNotFoundException(e)) {
            // machine was not found try to build one from a yaml file
            YamlImage currentImage = checkNotNull(imageMapping.get(key.getId()), "currentImage");
            URI preseedServer;
            try {
               preseedServer = new URI(preconfigurationUrl);
               if (!socketTester.apply(HostAndPort.fromParts(preseedServer.getHost(), preseedServer.getPort()))) {
                  server.start(preconfigurationUrl, currentImage.preseed_cfg);
               }
            } catch (URISyntaxException e1) {
               logger.error("Cannot start the preseed server", e);
               throw e;
            }

            MasterSpec masterSpec = buildMasterSpecFromYaml(currentImage, vmName);
            masterMachine = masterCreatorAndInstaller.apply(masterSpec);
            master = Master.builder().machine(masterMachine).spec(masterSpec).build();
         } else {
            logger.error("Problem during master creation", e);
            throw e;
         }
      } finally {
         server.stop();
      }

      masters.put(key.getId(), master);
      return master;
   }

   private MasterSpec buildMasterSpecFromYaml(YamlImage currentImage, String vmName) throws ExecutionException {
      String guestAdditionsFileName = String.format("VBoxGuestAdditions_%s.iso", version);
      String guestAdditionsIso = String.format("%s/%s", isosDir, guestAdditionsFileName);
      String guestAdditionsUri = "http://download.virtualbox.org/virtualbox/" + version + "/" + guestAdditionsFileName;
      if (!new File(guestAdditionsIso).exists()) {
         getFilePathOrDownload(guestAdditionsUri, null);
      }
      // check if the iso is here, download if not
      String localIsoUrl = checkNotNull(getFilePathOrDownload(currentImage.iso, currentImage.iso_md5), "distro iso");
      String adminDisk = workingDir + File.separator + vmName + ".vdi";
      HardDisk hardDisk = HardDisk.builder().diskpath(adminDisk).autoDelete(true).controllerPort(0).deviceSlot(1)
            .build();

      StorageController ideController = StorageController.builder().name("IDE Controller").bus(StorageBus.IDE)
            .attachISO(0, 0, localIsoUrl).attachHardDisk(hardDisk).build();

      VmSpec vmSpecification = VmSpec.builder().id(currentImage.id).name(vmName).memoryMB(512)
            .osTypeId(getOsTypeId(currentImage.os_family, currentImage.os_64bit)).controller(ideController)
            .forceOverwrite(true).guestUser(currentImage.username).guestPassword(currentImage.credential)
            .cleanUpMode(CleanupMode.Full).build();

      NetworkAdapter networkAdapter = NetworkAdapter.builder().networkAttachmentType(NetworkAttachmentType.NAT)
            .tcpRedirectRule(providerSupplier.get().getHost(), NetworkUtils.MASTER_PORT, "", 22).build();

      NetworkInterfaceCard networkInterfaceCard = NetworkInterfaceCard.builder().addNetworkAdapter(networkAdapter)
            .slot(0L).build();

      NetworkSpec networkSpec = NetworkSpec.builder().addNIC(networkInterfaceCard).build();

      String installationSequence = currentImage.keystroke_sequence.replace("HOSTNAME", vmSpecification.getVmName());
      return MasterSpec.builder()
                       .vm(vmSpecification)
                       .iso(IsoSpec.builder()
                                   .sourcePath(localIsoUrl)
                                   .installationScript(installationSequence)
                                   .build())
                       .network(networkSpec)
                       .credentials(LoginCredentials.builder()
                                                    .user(currentImage.username)
                                                    .password(currentImage.credential)
                                                    .authenticateSudo(true)
                                                    .build())
                       .build();
   }

   @Override
   public synchronized Master getIfPresent(Object key) {
      checkArgument(key instanceof Image, "this cache is for entries who's keys are Images");
      Image image = Image.class.cast(key);
      if (masters.containsKey(image.getId())) {
         return masters.get(image.getId());
      }
      return null;
   }

   private String getFilePathOrDownload(String httpUrl, String expectedMd5) throws ExecutionException {
      String fileName = httpUrl.substring(httpUrl.lastIndexOf('/') + 1, httpUrl.length());
      URI provider = providerSupplier.get();
      if (!socketTester.apply(HostAndPort.fromParts(provider.getHost(), provider.getPort()))) {
         throw new RuntimeException("could not connect to virtualbox");
      }
      File file = new File(isosDir, fileName);
      List<Statement> statements = new ImmutableList.Builder<Statement>().add(
            Statements.saveHttpResponseTo(URI.create(httpUrl), isosDir, fileName)).build();
      StatementList statementList = new StatementList(statements);
      NodeMetadata hostNode = checkNotNull(hardcodedHostToHostNodeMetadata.apply(host.get()), "hostNode");
      ListenableFuture<ExecResponse> future = runScriptOnNodeFactory.submit(hostNode, statementList,
            runAsRoot(false));
      Futures.getUnchecked(future);

      if (expectedMd5 != null) {
         String filePath = isosDir + File.separator + fileName;
         ListenableFuture<ExecResponse> md5future = runScriptOnNodeFactory.submit(hostNode, new Md5(filePath),
               runAsRoot(false));

         ExecResponse responseMd5 = Futures.getUnchecked(md5future);
         assert responseMd5.getExitStatus() == 0 : hostNode.getId() + ": " + responseMd5;
         checkNotNull(responseMd5.getOutput(), "iso_md5 missing");
         String actualMd5 = responseMd5.getOutput().trim();
         checkState(actualMd5.equals(expectedMd5), "md5 of %s is %s but expected %s", filePath, actualMd5, expectedMd5);
      }
      return file.getAbsolutePath();
   }

   private String getOsTypeId(String os_family, boolean os_64bit) {
      String osFamily = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, os_family);
      return os_64bit ? osFamily + "_64" : osFamily;
   }
}
