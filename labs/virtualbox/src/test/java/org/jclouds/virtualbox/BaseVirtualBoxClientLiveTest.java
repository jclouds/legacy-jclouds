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
import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_IMAGE_PREFIX;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.callables.RunScriptOnNode.Factory;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.compute.strategy.PrioritizeCredentialsFromTemplate;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.config.ValueOfConfigurationKeyOrNull;
import org.jclouds.rest.annotations.BuildVersion;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.jclouds.util.Strings2;
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
import org.jclouds.virtualbox.functions.HardcodedHostToHostNodeMetadata;
import org.jclouds.virtualbox.functions.IMachineToVmSpec;
import org.jclouds.virtualbox.functions.admin.UnregisterMachineIfExistsAndDeleteItsMedia;
import org.jclouds.virtualbox.predicates.RetryIfSocketNotYetOpen;
import org.jclouds.virtualbox.util.MachineController;
import org.jclouds.virtualbox.util.MachineUtils;
import org.jclouds.virtualbox.util.NetworkUtils;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.virtualbox_4_2.CleanupMode;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.NetworkAttachmentType;
import org.virtualbox_4_2.SessionState;
import org.virtualbox_4_2.StorageBus;
import org.virtualbox_4_2.VBoxException;
import org.virtualbox_4_2.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;

/**
 * Tests behavior of {@code VirtualBoxClient}
 * 
 * @author Adrian Cole, David Alves
 */
@Test(groups = "live", singleThreaded = true, testName = "BaseVirtualBoxClientLiveTest")
public class BaseVirtualBoxClientLiveTest extends BaseComputeServiceContextLiveTest {

   public static final String DONT_DESTROY_MASTER = "jclouds.virtualbox.keep-test-master";

   public BaseVirtualBoxClientLiveTest() {
      provider = "virtualbox";
   }

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
   
   @Inject
   protected NetworkUtils networkUtils;

   protected String hostVersion;
   protected String operatingSystemIso;
   protected String guestAdditionsIso;
   @Inject
   @Named(VirtualBoxConstants.VIRTUALBOX_WORKINGDIR)
   protected String workingDir;
   protected String isosDir;
   protected String keystrokeSequence;
   @Inject protected Supplier<NodeMetadata> host;
   @Inject protected Factory runScriptOnNodeFactory;
   @Inject protected RetryIfSocketNotYetOpen socketTester;
   @Inject protected HardcodedHostToHostNodeMetadata hardcodedHostToHostNodeMetadata;
   @Inject
   protected PrioritizeCredentialsFromTemplate prioritizeCredentialsFromTemplate;
   @Inject
   protected LoadingCache<Image, Master> mastersCache;
   private String masterName;   

   @Override
   protected Iterable<Module> setupModules() {
      return ImmutableSet.<Module> of(getLoggingModule(), credentialStoreModule, getSshModule(),  new ExecutorServiceModule(
            sameThreadExecutor(), sameThreadExecutor()));
   }
   
   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      view.utils().injector().injectMembers(this);
      
      // try and get a master from the cache, this will initialize the config/download isos and
      // prepare everything IF a master is not available, subsequent calls should be pretty fast
      Template template = view.getComputeService().templateBuilder().build();
      checkNotNull(mastersCache.apply(template.getImage()));

      masterName = VIRTUALBOX_IMAGE_PREFIX + template.getImage().getId();
      isosDir = workingDir + File.separator + "isos";

      hostVersion = Iterables.get(Splitter.on('-').split(view.utils().injector().getInstance(Key.get(String.class, BuildVersion.class))), 0);
      operatingSystemIso = String.format("%s/%s.iso", isosDir, template.getImage().getName());
      guestAdditionsIso = String.format("%s/VBoxGuestAdditions_%s.iso", isosDir, hostVersion);
      keystrokeSequence = "";
      try {
         keystrokeSequence = Strings2.toStringAndClose(getClass().getResourceAsStream("/default-keystroke-sequence"));
      } catch (IOException e) {
         throw new RuntimeException("error reading default-keystroke-sequence file");
      }
   }

   protected void undoVm(String vmNameOrId) {
      IMachine vm = null;
      try {
         vm = manager.get().getVBox().findMachine(vmNameOrId);
         VmSpec vmSpec = new IMachineToVmSpec().apply(vm);
         int attempts = 0;
         while (attempts < 10 && !vm.getSessionState().equals(SessionState.Unlocked)) {
            attempts++;
            Uninterruptibles.sleepUninterruptibly(200, TimeUnit.MILLISECONDS);
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
      StorageController ideController = StorageController
               .builder()
               .name("IDE Controller")
               .bus(StorageBus.IDE)
               .attachISO(0, 0, operatingSystemIso)
               .attachHardDisk(
                        HardDisk.builder().diskpath(adminDisk(masterName)).controllerPort(0).deviceSlot(1)
                                 .autoDelete(true).build()).attachISO(1, 0, guestAdditionsIso).build();

      VmSpec sourceVmSpec = VmSpec.builder().id(masterName).name(masterName).osTypeId("").memoryMB(512)
               .cleanUpMode(CleanupMode.Full).controller(ideController).forceOverwrite(true).build();

      IsoSpec isoSpec = IsoSpec
               .builder()
               .sourcePath(operatingSystemIso)
               .installationScript(keystrokeSequence).build();

      NetworkAdapter networkAdapter = NetworkAdapter.builder().networkAttachmentType(NetworkAttachmentType.NAT)
               .tcpRedirectRule("127.0.0.1", 2222, "", 22).build();
      NetworkInterfaceCard networkInterfaceCard = NetworkInterfaceCard.builder().addNetworkAdapter(networkAdapter)
               .build();

      NetworkSpec networkSpec = NetworkSpec.builder().addNIC(networkInterfaceCard).build();
      return MasterSpec.builder().iso(isoSpec).vm(sourceVmSpec).network(networkSpec).build();
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

   @AfterSuite
   protected void destroyMaster() {
      if (System.getProperty(DONT_DESTROY_MASTER) == null
               || !Boolean.parseBoolean(System.getProperty(DONT_DESTROY_MASTER))) {
         undoVm(masterName);
      }
   }
}
