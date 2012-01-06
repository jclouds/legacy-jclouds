/*
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

import static org.jclouds.virtualbox.util.MachineUtils.lockMachineAndApplyOrReturnNullIfNotRegistered;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;
import static org.virtualbox_4_1.LockType.Write;

import org.jclouds.virtualbox.BaseVirtualBoxClientLiveTest;
import org.jclouds.virtualbox.domain.ErrorCode;
import org.jclouds.virtualbox.domain.HardDisk;
import org.jclouds.virtualbox.domain.StorageController;
import org.jclouds.virtualbox.domain.VmSpec;
import org.jclouds.virtualbox.functions.admin.UnregisterMachineIfExistsAndDeleteItsMedia;
import org.testng.annotations.Test;
import org.virtualbox_4_1.CleanupMode;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.StorageBus;
import org.virtualbox_4_1.VBoxException;

/**
 * @author Mattias Holmqvist
 */
@Test(groups = "live", singleThreaded = true, testName = "CreateAndRegisterMachineFromIsoIfNotAlreadyExistsLiveTest")
public class CreateAndRegisterMachineFromIsoIfNotAlreadyExistsLiveTest extends
      BaseVirtualBoxClientLiveTest {

   private String ideControllerName;
   private CleanupMode mode;
   private StorageController ideController;

   @Override
   public void setupClient() {
      super.setupClient();
      ideControllerName = "IDE Controller";
      mode = CleanupMode.Full;
      ideController = StorageController
            .builder()
            .name(ideControllerName)
            .bus(StorageBus.IDE)
            .attachISO(0, 0, operatingSystemIso)
            .attachHardDisk(
                  HardDisk.builder().diskpath(adminDisk)
                        .controllerPort(0).deviceSlot(1).build())
            .attachISO(1, 1, guestAdditionsIso)
            .build();
   }

   @Test
   public void testCreateNewMachine() throws Exception {
      String vmName = "jclouds-test-create-1-node";
      VmSpec launchSpecification = VmSpec.builder().id(vmName).name(vmName)
            .memoryMB(512).controller(ideController).cleanUpMode(mode)
            .osTypeId("Debian").forceOverwrite(true).build();
      lockMachineAndApplyOrReturnNullIfNotRegistered(manager, Write, launchSpecification.getVmName(), new UnregisterMachineIfExistsAndDeleteItsMedia(launchSpecification));

      IMachine debianNode = new CreateAndRegisterMachineFromIsoIfNotAlreadyExists(
            manager, workingDir).apply(launchSpecification);
      IMachine machine = manager.getVBox().findMachine(vmName);
      assertEquals(debianNode.getName(), machine.getName());
      lockMachineAndApplyOrReturnNullIfNotRegistered(manager, Write, launchSpecification.getVmName(), new UnregisterMachineIfExistsAndDeleteItsMedia(launchSpecification));

   }

   @Test
   public void testCreateNewMachineWithBadOsType() throws Exception {
      String vmName = "jclouds-test-create-2-node";
      VmSpec launchSpecification = VmSpec.builder().id(vmName).name(vmName)
            .memoryMB(512).controller(ideController).cleanUpMode(mode)
            .osTypeId("SomeWeirdUnknownOs").forceOverwrite(true).build();
      lockMachineAndApplyOrReturnNullIfNotRegistered(manager, Write, launchSpecification.getVmName(), new UnregisterMachineIfExistsAndDeleteItsMedia(launchSpecification));

      try {
         new CreateAndRegisterMachineFromIsoIfNotAlreadyExists(manager, workingDir)
               .apply(launchSpecification);
         fail();
      } catch (VBoxException e) {
         ErrorCode errorCode = ErrorCode.valueOf(e);
         // According to the documentation VBOX_E_OBJECT_NOT_FOUND
         // if osTypeId is not found.
         assertEquals(errorCode, ErrorCode.VBOX_E_OBJECT_NOT_FOUND);
      }
   }

}
