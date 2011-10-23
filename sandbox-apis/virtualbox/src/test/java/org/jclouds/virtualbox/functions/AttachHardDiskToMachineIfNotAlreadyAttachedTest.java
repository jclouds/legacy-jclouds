/*
 * *
 *  * Licensed to jclouds, Inc. (jclouds) under one or more
 *  * contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  jclouds licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.jclouds.virtualbox.functions;

import org.testng.annotations.Test;
import org.virtualbox_4_1.*;

import static org.easymock.classextension.EasyMock.*;

/**
 * @author Mattias Holmqvist
 */
@Test(groups = "unit", testName = "AttachHardDiskToMachineIfNotAlreadyAttachedTest")
public class AttachHardDiskToMachineIfNotAlreadyAttachedTest {

   @Test
   public void testAttachHardDiskIfNotAttached() throws Exception {

      String controllerIDE = "IDE Controller";
      String adminDiskPath = "/Users/johndoe/jclouds-virtualbox-images/admin.vdi";
      String diskFormat = "vdi";
      int controllerPort = 0;
      int device = 1;

      VirtualBoxManager manager = createNiceMock(VirtualBoxManager.class);
      IMachine machine = createMock(IMachine.class);
      IVirtualBox vBox = createMock(IVirtualBox.class);
      IMedium hardDisk = createNiceMock(IMedium.class);
      IProgress progress = createNiceMock(IProgress.class);

      expect(manager.getVBox()).andReturn(vBox).anyTimes();
      expect(vBox.createHardDisk(diskFormat, adminDiskPath)).andReturn(hardDisk);
      expect(hardDisk.createBaseStorage(anyLong(), anyLong())).andReturn(progress);

      machine.attachDevice(controllerIDE, controllerPort, device, DeviceType.HardDisk, hardDisk);
      machine.saveSettings();
      replay(manager, machine, vBox, hardDisk);

      new AttachHardDiskToMachineIfNotAlreadyAttached(controllerIDE, hardDisk, manager).apply(machine);

      verify(machine);

   }

   @Test
   public void testDoNothingIfAlreadyAttachedAttachHardDisk() throws Exception {

      String controllerIDE = "IDE Controller";
      int controllerPort = 0;
      int device = 1;

      VirtualBoxManager manager = createNiceMock(VirtualBoxManager.class);
      IMachine machine = createMock(IMachine.class);
      IVirtualBox vBox = createMock(IVirtualBox.class);
      IMedium hardDisk = createNiceMock(IMedium.class);

      final StringBuilder errorBuilder = new StringBuilder();
      errorBuilder.append("VirtualBox error: ");
      errorBuilder.append("Medium '/Users/mattias/jclouds-virtualbox-test/testadmin.vdi' ");
      errorBuilder.append("is already attached to port 0, device 1 of controller 'IDE Controller' ");
      errorBuilder.append("of this virtual machine (0x80BB000C)");
      String isoAlreadyAttachedException = errorBuilder.toString();

      VBoxException isoAttachedException = new VBoxException(createNiceMock(Throwable.class), isoAlreadyAttachedException);
      machine.attachDevice(controllerIDE, controllerPort, device, DeviceType.HardDisk, hardDisk);
      expectLastCall().andThrow(isoAttachedException);

      replay(manager, machine, vBox, hardDisk);

      new AttachHardDiskToMachineIfNotAlreadyAttached(controllerIDE, hardDisk, manager).apply(machine);

      verify(machine);

   }

   @Test(expectedExceptions = VBoxException.class)
   public void testFailOnOtherVBoxError() throws Exception {

      String controllerIDE = "IDE Controller";
      int controllerPort = 0;
      int device = 1;

      VirtualBoxManager manager = createNiceMock(VirtualBoxManager.class);
      IMachine machine = createMock(IMachine.class);
      IVirtualBox vBox = createMock(IVirtualBox.class);
      IMedium hardDisk = createNiceMock(IMedium.class);

      final StringBuilder errorBuilder = new StringBuilder();
      errorBuilder.append("VirtualBox error: ");
      errorBuilder.append("Some other VBox error");
      String isoAlreadyAttachedException = errorBuilder.toString();

      VBoxException isoAttachedException = new VBoxException(createNiceMock(Throwable.class), isoAlreadyAttachedException);
      machine.attachDevice(controllerIDE, controllerPort, device, DeviceType.HardDisk, hardDisk);
      expectLastCall().andThrow(isoAttachedException);

      replay(manager, machine, vBox, hardDisk);

      new AttachHardDiskToMachineIfNotAlreadyAttached(controllerIDE, hardDisk, manager).apply(machine);

   }

}
