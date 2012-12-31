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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.UUID;

import org.jclouds.virtualbox.BaseVirtualBoxClientLiveTest;
import org.jclouds.virtualbox.domain.ErrorCode;
import org.jclouds.virtualbox.domain.HardDisk;
import org.jclouds.virtualbox.domain.IsoSpec;
import org.jclouds.virtualbox.domain.MasterSpec;
import org.jclouds.virtualbox.domain.NetworkAdapter;
import org.jclouds.virtualbox.domain.NetworkInterfaceCard;
import org.jclouds.virtualbox.domain.NetworkSpec;
import org.jclouds.virtualbox.domain.StorageController;
import org.jclouds.virtualbox.domain.VmSpec;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.Test;
import org.virtualbox_4_2.CleanupMode;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.NetworkAttachmentType;
import org.virtualbox_4_2.StorageBus;
import org.virtualbox_4_2.VBoxException;

import com.google.inject.Injector;

/**
 * @author Mattias Holmqvist
 */
@Test(groups = "live", singleThreaded = true, testName = "CreateAndRegisterMachineFromIsoIfNotAlreadyExistsLiveTest")
public class CreateAndRegisterMachineFromIsoIfNotAlreadyExistsLiveTest extends BaseVirtualBoxClientLiveTest {

   private String ideControllerName;
   private CleanupMode mode;
   private String vmName = "";

   @Override
   public void setupContext() {
      super.setupContext();
      ideControllerName = "IDE Controller";
      mode = CleanupMode.Full;
   }

   @Test
   public void testCreateNewMachine() throws Exception {
      vmName = "jclouds-test-create-1-node";
      String vmId = UUID.randomUUID().toString();

      StorageController ideController = StorageController.builder().name(ideControllerName).bus(StorageBus.IDE)
               .attachISO(0, 0, operatingSystemIso)
               .attachHardDisk(HardDisk.builder().diskpath(adminDisk(vmName)).controllerPort(0).deviceSlot(1).build())
               .attachISO(1, 1, guestAdditionsIso).build();

      VmSpec vmSpec = VmSpec.builder().id(vmId).name(vmName).memoryMB(512).controller(ideController).cleanUpMode(mode)
               .osTypeId("Debian").forceOverwrite(true).build();

      NetworkAdapter networkAdapter = NetworkAdapter.builder().networkAttachmentType(NetworkAttachmentType.NAT)
               .tcpRedirectRule("127.0.0.1", 2222, "", 22).build();
      NetworkInterfaceCard networkInterfaceCard = NetworkInterfaceCard.builder().addNetworkAdapter(networkAdapter)
               .build();

      NetworkSpec networkSpec = NetworkSpec.builder().addNIC(networkInterfaceCard).build();

      MasterSpec machineSpec = MasterSpec.builder()
               .iso(IsoSpec.builder().sourcePath(operatingSystemIso).installationScript("").build()).vm(vmSpec)
               .network(networkSpec).build();
      IMachine debianNode = view.utils().injector()
               .getInstance(CreateAndRegisterMachineFromIsoIfNotAlreadyExists.class).apply(machineSpec);
      IMachine machine = manager.get().getVBox().findMachine(vmName);
      assertEquals(debianNode.getName(), machine.getName());
      undoVm(vmName);

   }

   @Test
   public void testCreateNewMachineWithBadOsType() throws Exception {
      vmName = "jclouds-test-create-2-node";
      String vmId = UUID.randomUUID().toString();

      StorageController ideController = StorageController.builder().name(ideControllerName).bus(StorageBus.IDE)
               .attachISO(0, 0, operatingSystemIso)
               .attachHardDisk(HardDisk.builder().diskpath(adminDisk(vmName)).controllerPort(0).deviceSlot(1).build())
               .attachISO(1, 1, guestAdditionsIso).build();

      VmSpec vmSpec = VmSpec.builder().id(vmId).name(vmName).memoryMB(512).controller(ideController).cleanUpMode(mode)
               .osTypeId("SomeWeirdUnknownOs").forceOverwrite(true).build();
      IsoSpec isoSpec = IsoSpec.builder().sourcePath(operatingSystemIso).installationScript("").build();
      NetworkSpec networkSpec = NetworkSpec.builder().build();
      MasterSpec machineSpec = MasterSpec.builder().iso(isoSpec).vm(vmSpec).network(networkSpec).build();
      try {
         Injector injector = view.utils().injector();
         injector.getInstance(CreateAndRegisterMachineFromIsoIfNotAlreadyExists.class).apply(machineSpec);
         fail();
      } catch (VBoxException e) {
         ErrorCode errorCode = ErrorCode.valueOf(e);
         // According to the documentation VBOX_E_OBJECT_NOT_FOUND
         // if osTypeId is not found.
         assertEquals(errorCode, ErrorCode.VBOX_E_OBJECT_NOT_FOUND);
      }
      undoVm(vmName);
   }
   
   @AfterGroups(groups = "live")
   @Override
   protected void tearDownContext() {
      super.tearDownContext();
   }

}
