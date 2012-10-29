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

package org.jclouds.virtualbox.util;

import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_IMAGE_PREFIX;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_INSTALLATION_KEY_SEQUENCE;
import static org.testng.AssertJUnit.assertTrue;

import org.jclouds.config.ValueOfConfigurationKeyOrNull;
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
import org.jclouds.virtualbox.functions.CloneAndRegisterMachineFromIMachineIfNotAlreadyExists;
import org.jclouds.virtualbox.functions.CreateAndInstallVm;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.virtualbox_4_1.CleanupMode;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.NetworkAttachmentType;
import org.virtualbox_4_1.SessionState;
import org.virtualbox_4_1.StorageBus;

import com.google.common.base.CaseFormat;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;

@Test(groups = "live", testName = "MachineControllerLiveTest")
public class MachineControllerLiveTest extends BaseVirtualBoxClientLiveTest {

   private MasterSpec machineSpec;
   private String instanceName;

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

      Injector injector = view.utils().injector();
      Function<String, String> configProperties = injector
            .getInstance(ValueOfConfigurationKeyOrNull.class);
      IsoSpec isoSpec = IsoSpec
            .builder()
            .sourcePath(operatingSystemIso)
            .installationScript(
                  configProperties.apply(VIRTUALBOX_INSTALLATION_KEY_SEQUENCE)
                        .replace("HOSTNAME", instanceVmSpec.getVmName()))
            .build();

      NetworkAdapter networkAdapter = NetworkAdapter.builder()
            .networkAttachmentType(NetworkAttachmentType.HostOnly).build();
      NetworkInterfaceCard networkInterfaceCard = NetworkInterfaceCard
            .builder().addNetworkAdapter(networkAdapter)
            .addHostInterfaceName("vboxnet0").slot(0L).build();
      NetworkSpec networkSpec = NetworkSpec.builder()
            .addNIC(networkInterfaceCard).build();
      machineSpec = MasterSpec.builder().iso(isoSpec).vm(instanceVmSpec)
            .network(networkSpec).build();
   }

   @Test
   public void testEnsureMachineisLaunchedAndSessionIsUnlocked() {
      cloneFromMaster();
      ISession cloneMachineSession = machineController.ensureMachineIsLaunched(instanceName);
      assertTrue(cloneMachineSession.getState() == SessionState.Unlocked);
      cloneMachineSession = machineController.ensureMachineHasPowerDown(instanceName);
      assertTrue(cloneMachineSession.getState() == SessionState.Unlocked);
   }

   @Test(dependsOnMethods="testEnsureMachineisLaunchedAndSessionIsUnlocked")
   public void testEnsureMachineCanBePoweredOffMoreThanOneTimeAndSessionIsUnlocked() {
      ISession cloneMachineSession = machineController.ensureMachineHasPowerDown(instanceName);
      SessionState state = cloneMachineSession.getState();
      assertTrue(state.equals(SessionState.Unlocked));
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
         // already created
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
