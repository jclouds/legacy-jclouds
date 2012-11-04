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

import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_IMAGE_PREFIX;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_INSTALLATION_KEY_SEQUENCE;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertFalse;

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;

import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.config.ValueOfConfigurationKeyOrNull;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.jclouds.location.Provider;
import org.jclouds.ssh.SshClient;
import org.jclouds.virtualbox.BaseVirtualBoxClientLiveTest;
import org.jclouds.virtualbox.domain.CloneSpec;
import org.jclouds.virtualbox.domain.HardDisk;
import org.jclouds.virtualbox.domain.IsoSpec;
import org.jclouds.virtualbox.domain.MasterSpec;
import org.jclouds.virtualbox.domain.NetworkAdapter;
import org.jclouds.virtualbox.domain.NetworkInterfaceCard;
import org.jclouds.virtualbox.domain.NetworkSpec;
import org.jclouds.virtualbox.domain.StorageController;
import org.jclouds.virtualbox.domain.VmSpec;
import org.jclouds.virtualbox.predicates.SshResponds;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.virtualbox_4_1.CleanupMode;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.NetworkAttachmentType;
import org.virtualbox_4_1.StorageBus;

import com.google.common.base.CaseFormat;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Andrea Turli, Mattias Holmqvist
 */
@Test(groups = "live", singleThreaded = true, testName = "CreateAndInstallVmLiveTest")
public class CreateAndInstallVmLiveTest extends BaseVirtualBoxClientLiveTest {

   Map<OsFamily, Map<String, String>> map = new BaseComputeServiceContextModule() {
   }.provideOsVersionMap(new ComputeServiceConstants.ReferenceData(), Guice.createInjector(new GsonModule())
            .getInstance(Json.class));

   private Injector injector;
   private Function<IMachine, SshClient> sshClientForIMachine;
   private Predicate<SshClient> sshResponds;
   
   private MasterSpec machineSpec;
   private String instanceName;
   
   @Inject 
   @Provider
   protected Supplier<URI> providerSupplier;

   @Override
   @BeforeClass(groups = "live")
   public void setupContext() {
      super.setupContext();
      instanceName = VIRTUALBOX_IMAGE_PREFIX
               + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, getClass().getSimpleName());

      StorageController ideController = StorageController
               .builder()
               .name("IDE Controller")
               .bus(StorageBus.IDE)
               .attachISO(0, 0, operatingSystemIso)
               .attachHardDisk(
                        HardDisk.builder().diskpath(adminDisk(instanceName)).controllerPort(0).deviceSlot(1)
                                 .autoDelete(true).build()).attachISO(1, 1, guestAdditionsIso).build();

      VmSpec instanceVmSpec = VmSpec.builder().id(instanceName).name(instanceName).osTypeId("").memoryMB(512)
               .cleanUpMode(CleanupMode.Full).controller(ideController).forceOverwrite(true).build();

      injector = view.utils().injector();
      Function<String, String> configProperties = injector.getInstance(ValueOfConfigurationKeyOrNull.class);
      IsoSpec isoSpec = IsoSpec
               .builder()
               .sourcePath(operatingSystemIso)
               .installationScript(
                        configProperties.apply(VIRTUALBOX_INSTALLATION_KEY_SEQUENCE).replace("HOSTNAME",
                                 instanceVmSpec.getVmName())).build();

      NetworkAdapter networkAdapter = NetworkAdapter.builder().networkAttachmentType(NetworkAttachmentType.HostOnly)
               .build();
      NetworkInterfaceCard networkInterfaceCard =  NetworkInterfaceCard.builder().addNetworkAdapter(networkAdapter)
            .addHostInterfaceName("vboxnet0").slot(0L).build();
      NetworkSpec networkSpec = NetworkSpec.builder().addNIC(networkInterfaceCard).build();
      machineSpec = MasterSpec.builder().iso(isoSpec).vm(instanceVmSpec).network(networkSpec).build();
   }

   @Test
   public void testGuestAdditionsAreInstalled() throws Exception {
      try {
         IMachine machine = cloneFromMaster();
         machineController.ensureMachineIsLaunched(machine.getName());

         sshClientForIMachine = injector.getInstance(IMachineToSshClient.class);
         SshClient client = sshClientForIMachine.apply(machine);

         sshResponds = injector.getInstance(SshResponds.class);
         checkState(sshResponds.apply(client), "timed out waiting for guest %s to be accessible via ssh",
                  machine.getName());

         String version = machine.getGuestPropertyValue("/VirtualBox/GuestAdd/Version");
         
         assertNotNull(version);
         assertFalse(version.isEmpty());
      } finally {
         for (VmSpec spec : ImmutableSet.of(machineSpec.getVmSpec())) {
            machineController.ensureMachineIsShutdown(spec.getVmName());
         }
      }
   }

   private IMachine cloneFromMaster() {
      IMachine source = getVmWithGuestAdditionsInstalled();
      CloneSpec cloneSpec = CloneSpec.builder().vm(machineSpec.getVmSpec()).network(machineSpec.getNetworkSpec())
               .master(source).linked(true).build();
      return new CloneAndRegisterMachineFromIMachineIfNotAlreadyExists(manager, workingDir, machineUtils)
               .apply(cloneSpec);
   }
   
   private IMachine getVmWithGuestAdditionsInstalled() {
      MasterSpec masterSpecForTest = super.getMasterSpecForTest();
      try {
         Injector injector = view.utils().injector();
         return injector.getInstance(CreateAndInstallVm.class).apply(masterSpecForTest);
      } catch (IllegalStateException e) {
         return manager.get().getVBox().findMachine(masterSpecForTest.getVmSpec().getVmId());
      }
   }

   @Override
   @AfterClass(groups = "live")
   protected void tearDown() throws Exception {
      for (String vmName : ImmutableSet.of(instanceName)) {
         undoVm(vmName);
      }
      super.tearDown();
   }
}
