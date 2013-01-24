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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_IMAGE_PREFIX;
import static org.testng.Assert.assertEquals;

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
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.virtualbox_4_2.CleanupMode;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.NetworkAttachmentType;
import org.virtualbox_4_2.StorageBus;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;

/**
 * @author Andrea Turli
 */
@Test(groups = "live", singleThreaded = true, testName = "CloneAndRegisterMachineFromIMachineIfNotAlreadyExistsLiveTest")
public class CloneAndRegisterMachineFromIMachineIfNotAlreadyExistsLiveTest extends BaseVirtualBoxClientLiveTest {

   private MasterSpec machineSpec;
   private String instanceName;

   @Override
   @BeforeClass(groups = "live")
   public void setupContext() {
      super.setupContext();
      instanceName = VIRTUALBOX_IMAGE_PREFIX
               + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, getClass().getSimpleName());

      StorageController ideController = StorageController.builder()
                                                         .name("IDE Controller")
                                                         .bus(StorageBus.IDE)
                                                         .attachISO(0, 0, operatingSystemIso)
                                                         .attachHardDisk(HardDisk.builder()
                                                                                 .diskpath(adminDisk(instanceName))
                                                                                 .controllerPort(0)
                                                                                 .deviceSlot(1)
                                                                                 .autoDelete(true)
                                                                                 .build())
                                                         .attachISO(1, 1, guestAdditionsIso)
                                                         .build();

      VmSpec instanceVmSpec = VmSpec.builder()
                                    .id(instanceName)
                                    .name(instanceName)
                                    .osTypeId("")
                                    .memoryMB(512)
                                    .cleanUpMode(CleanupMode.Full)
                                    .controller(ideController)
                                    .forceOverwrite(true)
                                    .build();

      IsoSpec isoSpec = IsoSpec.builder()
                               .sourcePath(operatingSystemIso)
                               .installationScript(keystrokeSequence)
                               .build();

      NetworkAdapter networkAdapter = NetworkAdapter.builder()
                                                    .networkAttachmentType(NetworkAttachmentType.NAT)
                                                    .tcpRedirectRule("127.0.0.1", 2222, "", 22).build();
      NetworkInterfaceCard networkInterfaceCard = NetworkInterfaceCard.builder()
                                                                      .addNetworkAdapter(networkAdapter)
                                                                      .build();

      NetworkSpec networkSpec = NetworkSpec.builder()
                                           .addNIC(networkInterfaceCard)
                                           .build();
      machineSpec = MasterSpec.builder()
                              .iso(isoSpec)
                              .vm(instanceVmSpec)
                              .network(networkSpec)
                              .build();
   }
   
   @Test
   public void testCloneMachineFromAnotherMachine() {
      IMachine source = getVmWithGuestAdditionsInstalled();
      CloneSpec cloneSpec = CloneSpec.builder().vm(machineSpec.getVmSpec()).network(machineSpec.getNetworkSpec())
               .master(source).linked(true).build();
      IMachine clone = checkNotNull(
              new CloneAndRegisterMachineFromIMachineIfNotAlreadyExists(manager, workingDir, machineUtils)
                                .apply(cloneSpec), "clone");
      assertEquals(clone.getName(), cloneSpec.getVmSpec().getVmName());

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
   
   @AfterGroups(groups = "live")
   @Override
   protected void tearDownContext() {
      for (String vmName : ImmutableSet.of(instanceName)) {
         undoVm(vmName);
      }
      super.tearDownContext();
   }

}
