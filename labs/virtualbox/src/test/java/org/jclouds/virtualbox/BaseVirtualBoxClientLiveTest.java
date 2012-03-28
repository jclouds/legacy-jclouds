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

package org.jclouds.virtualbox;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_INSTALLATION_KEY_SEQUENCE;

import java.io.File;
import java.net.URI;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.BaseVersionedServiceLiveTest;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.strategy.PrioritizeCredentialsFromTemplate;
import org.jclouds.concurrent.MoreExecutors;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.config.ValueOfConfigurationKeyOrNull;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.jclouds.virtualbox.config.VirtualBoxConstants;
import org.jclouds.virtualbox.domain.HardDisk;
import org.jclouds.virtualbox.domain.IsoSpec;
import org.jclouds.virtualbox.domain.Master;
import org.jclouds.virtualbox.domain.MasterSpec;
import org.jclouds.virtualbox.domain.NetworkAdapter;
import org.jclouds.virtualbox.domain.NetworkInterfaceCard;
import org.jclouds.virtualbox.domain.NetworkSpec;
import org.jclouds.virtualbox.domain.StorageController;
import org.jclouds.virtualbox.domain.VmSpec;
import org.jclouds.virtualbox.functions.IMachineToVmSpec;
import org.jclouds.virtualbox.functions.admin.UnregisterMachineIfExistsAndDeleteItsMedia;
import org.jclouds.virtualbox.util.MachineController;
import org.jclouds.virtualbox.util.MachineUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.virtualbox_4_1.CleanupMode;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.NetworkAttachmentType;
import org.virtualbox_4_1.SessionState;
import org.virtualbox_4_1.StorageBus;
import org.virtualbox_4_1.VBoxException;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Tests behavior of {@code VirtualBoxClient}
 * 
 * @author Adrian Cole, David Alves
 */
@Test(groups = "live", singleThreaded = true, testName = "BaseVirtualBoxClientLiveTest")
public class BaseVirtualBoxClientLiveTest extends BaseVersionedServiceLiveTest {
   public BaseVirtualBoxClientLiveTest() {
      provider = "virtualbox";
   }
   
   protected ComputeServiceContext context;
   
   @Inject
   protected MachineController machineController;

   @Inject
   protected Supplier<VirtualBoxManager> manager;

   @Inject
   void eagerlyStartManager(Supplier<VirtualBoxManager> manager) {
      this.manager = manager;
      manager.get();
   }

   @Inject
   protected MachineUtils machineUtils;

   // this will eagerly startup Jetty, note the impl will shut itself down
   @Inject
   @Preconfiguration
   protected LoadingCache<IsoSpec, URI> preconfigurationUri;

   protected String hostVersion;
   protected String operatingSystemIso;
   protected String guestAdditionsIso;
   @Inject
   @Named(VirtualBoxConstants.VIRTUALBOX_WORKINGDIR)
   protected String workingDir;
   protected String isosDir;
   @Inject
   protected Supplier<NodeMetadata> host;
   @Inject
   protected PrioritizeCredentialsFromTemplate prioritizeCredentialsFromTemplate;
   @Inject
   protected LoadingCache<Image, Master> mastersCache;
   
   private final ExecutorService singleThreadExec = MoreExecutors.sameThreadExecutor(); 

   @Override
   protected void setupCredentials() {
      // default behavior is to bomb when no user is configured, but we know the
      // default user of
      // vbox
      ensureIdentityPropertyIsSpecifiedOrTakeFromDefaults();
      super.setupCredentials();
   }

   protected void ensureIdentityPropertyIsSpecifiedOrTakeFromDefaults() {
      if (!System.getProperties().containsKey("test." + provider + ".identity"))
         System.setProperty("test." + provider + ".identity", "administrator");
   }

   @BeforeClass(groups = "live")
   public void setupClient() {
      setupCredentials();
      Properties overrides = new VirtualBoxPropertiesBuilder(setupProperties()).build();

      context = new ComputeServiceContextFactory().createContext(provider, identity, credential, ImmutableSet
               .<Module> of(new SLF4JLoggingModule(), new SshjSshClientModule(), new ExecutorServiceModule(
                        singleThreadExec, singleThreadExec)), overrides);
      
      context.utils().injector().injectMembers(this);

      imageId = "ubuntu-11.04-server-i386";
      isosDir = workingDir + File.separator + "isos";

      hostVersion = Iterables.get(Splitter.on('r').split(context.getProviderSpecificContext().getBuildVersion()), 0);
      operatingSystemIso = String.format("%s/%s.iso", isosDir, imageId);
      guestAdditionsIso = String.format("%s/VBoxGuestAdditions_%s.iso", isosDir, hostVersion);
      
      // try and get a master from the cache, this will initialize the config/download isos and
      // prepare everything IF a master is not available, subsequent calls should be pretty fast
      Template template = context.getComputeService().templateBuilder().build();
      checkNotNull(mastersCache.apply(template.getImage()));
   }

   protected void undoVm(String vmNameOrId) {
      IMachine vm = null;
      try {
         vm = manager.get().getVBox().findMachine(vmNameOrId);
         VmSpec vmSpec = new IMachineToVmSpec().apply(vm);
         int attempts = 0;
         while (attempts < 10 && !vm.getSessionState().equals(SessionState.Unlocked)) {
            attempts++;
            try {
               Thread.sleep(200l);
            } catch (InterruptedException e) {
            }
         }
         machineUtils.applyForMachine(vmNameOrId, new UnregisterMachineIfExistsAndDeleteItsMedia(vmSpec));

      } catch (VBoxException e) {
         if (e.getMessage().contains("Could not find a registered machine named"))
            return;
      }
   }

   public String adminDisk(String vmName) {
      return workingDir + File.separator + vmName + ".vdi";
   }

   public MasterSpec getMasterSpecForTest() {
      String masterName = "jclouds-image-0x0-default-ubuntu-11.04-i386";
      StorageController ideController = StorageController
               .builder()
               .name("IDE Controller")
               .bus(StorageBus.IDE)
               .attachISO(0, 0, operatingSystemIso)
               .attachHardDisk(
                        HardDisk.builder().diskpath(adminDisk(masterName)).controllerPort(0).deviceSlot(1)
                                 .autoDelete(true).build()).attachISO(1, 0, guestAdditionsIso).build();

            VmSpec sourceVmSpec = VmSpec.builder().id(masterName).name(masterName)
                        .osTypeId("").memoryMB(512).cleanUpMode(CleanupMode.Full)
                        .controller(ideController).forceOverwrite(true).build();

            Injector injector = context.utils().injector();
            Function<String, String> configProperties = injector
                        .getInstance(ValueOfConfigurationKeyOrNull.class);
            IsoSpec isoSpec = IsoSpec
                        .builder()
                        .sourcePath(operatingSystemIso)
                        .installationScript(
                                    configProperties.apply(
                                                VIRTUALBOX_INSTALLATION_KEY_SEQUENCE).replace(
                                                "HOSTNAME", sourceVmSpec.getVmName())).build();
            
            NetworkAdapter networkAdapter = NetworkAdapter.builder()
                        .networkAttachmentType(NetworkAttachmentType.NAT)
                        .tcpRedirectRule("127.0.0.1", 2222, "", 22).build();
            NetworkInterfaceCard networkInterfaceCard = NetworkInterfaceCard
                        .builder().addNetworkAdapter(networkAdapter).build();

            NetworkSpec networkSpec = NetworkSpec.builder()
                        .addNIC(networkInterfaceCard).build();
            return MasterSpec.builder().iso(isoSpec).vm(sourceVmSpec)
                        .network(networkSpec).build();
   }
   
   @AfterClass(groups = "live")
   protected void tearDown() throws Exception {
      if (context != null)
         context.close();
   }

}
