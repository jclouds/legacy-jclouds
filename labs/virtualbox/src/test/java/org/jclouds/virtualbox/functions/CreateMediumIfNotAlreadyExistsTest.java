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

import static org.easymock.EasyMock.anyLong;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertNotSame;

import org.jclouds.virtualbox.domain.HardDisk;
import org.jclouds.virtualbox.util.MachineUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.virtualbox_4_2.AccessMode;
import org.virtualbox_4_2.DeviceType;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.IMedium;
import org.virtualbox_4_2.IMediumAttachment;
import org.virtualbox_4_2.IProgress;
import org.virtualbox_4_2.ISession;
import org.virtualbox_4_2.IVirtualBox;
import org.virtualbox_4_2.LockType;
import org.virtualbox_4_2.VBoxException;
import org.virtualbox_4_2.VirtualBoxManager;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;

/**
 * @author Mattias Holmqvist
 */
public class CreateMediumIfNotAlreadyExistsTest {
   
   private String adminDiskPath;
   private String diskFormat;

   @BeforeMethod
   public void setUp() throws Exception {
      adminDiskPath = "/Users/johndoe/jclouds-virtualbox-images/admin.vdi";
      diskFormat = "vdi";
   }

   @Test
   public void testCreateMediumWhenDiskDoesNotExists() throws Exception {

      HardDisk hardDisk = createTestHardDisk();

      VirtualBoxManager manager = createNiceMock(VirtualBoxManager.class);
      MachineUtils machineUtils = createMock(MachineUtils.class);

      IMachine machine = createMock(IMachine.class);
      IVirtualBox vBox = createMock(IVirtualBox.class);
      IMedium medium = createMock(IMedium.class);
      IProgress progress = createNiceMock(IProgress.class);

      StringBuilder errorBuilder = new StringBuilder();
      errorBuilder.append("org.virtualbox_4_2.VBoxException: VirtualBox error: ");
      errorBuilder.append("Could not find file for the medium ");
      errorBuilder.append("'/Users/johndoe/jclouds-virtualbox-test/testadmin.vdi' (0x80BB0001)");
      String errorMessage = errorBuilder.toString();
      expect(manager.getVBox()).andReturn(vBox).anyTimes();

      VBoxException notFoundException = new VBoxException(createNiceMock(Throwable.class), errorMessage);
      expect(vBox.openMedium(adminDiskPath, DeviceType.HardDisk, AccessMode.ReadWrite, false)).andThrow(notFoundException);
      expect(vBox.createHardDisk(diskFormat, adminDiskPath)).andReturn(medium);
      expect(medium.createBaseStorage(anyLong(), anyLong())).andReturn(progress);
      //expect(machineUtils.writeLockMachineAndApply(anyString(), new DetachDistroMediumFromMachine(anyString(), anyInt() , anyInt()))).andReturn().anyTimes();

      replay(manager, machine, vBox, medium, machineUtils);

      new CreateMediumIfNotAlreadyExists(Suppliers.ofInstance(manager), machineUtils, true).apply(hardDisk);

      verify(machine, vBox);

   }

   @Test
   public void testDeleteAndCreateNewStorageWhenMediumExistsAndUsingOverwrite() throws Exception {
      HardDisk hardDisk = createTestHardDisk();

      VirtualBoxManager manager = createNiceMock(VirtualBoxManager.class);
      MachineUtils machineUtils = createMock(MachineUtils.class);

      IMachine machine = createMock(IMachine.class);
      IVirtualBox vBox = createMock(IVirtualBox.class);
      IMedium medium = createMock(IMedium.class);
      IMedium newHardDisk = createMock(IMedium.class);
      IProgress progress = createNiceMock(IProgress.class);

      expect(manager.getVBox()).andReturn(vBox).anyTimes();
      expect(vBox.openMedium(adminDiskPath, DeviceType.HardDisk, AccessMode.ReadWrite, false)).andReturn(medium);

      expect(medium.deleteStorage()).andReturn(progress);
      expect(vBox.createHardDisk(diskFormat, adminDiskPath)).andReturn(newHardDisk);
      expect(newHardDisk.createBaseStorage(anyLong(), anyLong())).andReturn(progress);

      //expect(machineUtils.writeLockMachineAndApply(anyString(), new DetachDistroMediumFromMachine(anyString(), anyInt() , anyInt()))).andReturn(v).anyTimes();

      replay(manager, machine, vBox, medium, newHardDisk, progress, machineUtils);

      IMedium newDisk =  new CreateMediumIfNotAlreadyExists(Suppliers.ofInstance(manager), machineUtils, true).apply(hardDisk);

      verify(machine, vBox, medium);
      assertNotSame(newDisk, medium);
   }

   @Test(enabled = false)
   public void testDeleteAndCreateNewStorageWhenMediumExistsAndUsingOverwriteAndStillAttachedDetachesOldThing()
            throws Exception {
      HardDisk hardDisk = createTestHardDisk();

      VirtualBoxManager manager = createNiceMock(VirtualBoxManager.class);
      MachineUtils machineUtils = createMock(MachineUtils.class);

      IMachine machine = createMock(IMachine.class);
      IVirtualBox vBox = createMock(IVirtualBox.class);
      IMedium medium = createMock(IMedium.class);

      IMedium newHardDisk = createMock(IMedium.class);
      IProgress progress = createNiceMock(IProgress.class);

      expect(manager.getVBox()).andReturn(vBox).anyTimes();
      expect(vBox.openMedium(adminDiskPath, DeviceType.HardDisk, AccessMode.ReadWrite, false)).andReturn(medium);

      String oldMachineId = "a1e03931-29f3-4370-ada3-9547b1009212";
      String oldMachineName = "oldMachine";
      IMachine oldMachine = createMock(IMachine.class);
      IMediumAttachment oldAttachment = createMock(IMediumAttachment.class);
      String oldAttachmentController = "oldAttachmentController";
      int oldAttachmentDevice = 1;
      int oldAttachmentPort = 2;
      IMedium oldMedium = createMock(IMedium.class);
      String oldMediumId = "oldMediumId";
      ISession detachSession = createNiceMock(ISession.class);

      StringBuilder errorBuilder = new StringBuilder();
      errorBuilder.append("org.virtualbox_4_2.VBoxException: VirtualBox error: ");
      errorBuilder.append("Cannot delete storage: medium '/Users/adriancole/jclouds-virtualbox-test/testadmin.vdi ");
      errorBuilder.append("is still attached to the following 1 virtual machine(s): ");
      errorBuilder.append(oldMachineId + " (0x80BB000C)");
      String errorMessage = errorBuilder.toString();

      VBoxException stillAttached = new VBoxException(createNiceMock(Throwable.class), errorMessage);
      expect(medium.deleteStorage()).andThrow(stillAttached);

      expect(vBox.findMachine(oldMachineId)).andReturn(oldMachine);
      expect(oldMachine.getMediumAttachments()).andReturn(ImmutableList.of(oldAttachment));
      expect(oldAttachment.getMedium()).andReturn(oldMedium);
      expect(oldMedium.getId()).andReturn(oldMediumId);
      // in this case, they are the same medium, so safe to detach
      expect(medium.getId()).andReturn(oldMediumId);
      expect(oldMachine.getName()).andReturn(oldMachineName);
      expect(oldAttachment.getController()).andReturn(oldAttachmentController);
      expect(oldAttachment.getDevice()).andReturn(oldAttachmentDevice);
      expect(oldAttachment.getPort()).andReturn(oldAttachmentPort);
      // TODO: is this ok that we searched by ID last time?
      expect(vBox.findMachine(oldMachineName)).andReturn(oldMachine);
      expect(manager.getSessionObject()).andReturn(detachSession);
      oldMachine.lockMachine(detachSession, LockType.Write);
      expect(detachSession.getMachine()).andReturn(oldMachine);
      oldMachine.detachDevice(oldAttachmentController, oldAttachmentPort, oldAttachmentDevice);
      oldMachine.saveSettings();

      expect(medium.deleteStorage()).andReturn(progress);
      expect(vBox.createHardDisk(diskFormat, adminDiskPath)).andReturn(newHardDisk);
      expect(newHardDisk.createBaseStorage(anyLong(), anyLong())).andReturn(progress);

      replay(manager, oldMachine, oldAttachment, oldMedium, detachSession, machine, vBox, medium, newHardDisk, progress, machineUtils);

      IMedium newDisk = new CreateMediumIfNotAlreadyExists(Suppliers.ofInstance(manager), machineUtils, true).apply(hardDisk);

      verify(machine, oldMachine, oldAttachment, detachSession, oldMedium, vBox, medium);
      assertNotSame(newDisk, medium);
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testFailWhenMediumExistsAndNotUsingOverwrite() throws Exception {
      HardDisk hardDisk = createTestHardDisk();

      VirtualBoxManager manager = createNiceMock(VirtualBoxManager.class);
      MachineUtils machineUtils = createMock(MachineUtils.class);

      IMachine machine = createMock(IMachine.class);
      IVirtualBox vBox = createMock(IVirtualBox.class);
      IMedium medium = createMock(IMedium.class);
      IMedium newHardDisk = createMock(IMedium.class);
      IProgress progress = createNiceMock(IProgress.class);

      expect(manager.getVBox()).andReturn(vBox).anyTimes();
      expect(vBox.openMedium(adminDiskPath, DeviceType.HardDisk, AccessMode.ReadWrite, false)).andReturn(medium);

      replay(manager, machine, vBox, medium, newHardDisk, progress, machineUtils);

      new CreateMediumIfNotAlreadyExists(Suppliers.ofInstance(manager), machineUtils, false).apply(hardDisk);
   }

   @Test(expectedExceptions = VBoxException.class)
   public void testFailOnOtherVBoxException() throws Exception {

      HardDisk hardDisk = createTestHardDisk();

      VirtualBoxManager manager = createNiceMock(VirtualBoxManager.class);
      MachineUtils machineUtils = createMock(MachineUtils.class);

      IMachine machine = createMock(IMachine.class);
      IVirtualBox vBox = createMock(IVirtualBox.class);
      IMedium medium = createMock(IMedium.class);
      IProgress progress = createNiceMock(IProgress.class);

      String errorMessage = "VirtualBox error: Some other VBox error";

      expect(manager.getVBox()).andReturn(vBox).anyTimes();

      VBoxException notFoundException = new VBoxException(createNiceMock(Throwable.class), errorMessage);
      expect(vBox.openMedium(adminDiskPath, DeviceType.HardDisk, AccessMode.ReadWrite, false)).andThrow(notFoundException);
      expect(vBox.createHardDisk(diskFormat, adminDiskPath)).andReturn(medium);
      expect(medium.createBaseStorage(anyLong(), anyLong())).andReturn(progress);

      replay(manager, machine, vBox, medium, machineUtils);

      new CreateMediumIfNotAlreadyExists(Suppliers.ofInstance(manager), machineUtils, true).apply(hardDisk);
   }

   private HardDisk createTestHardDisk() {
      return HardDisk.builder().diskpath(adminDiskPath).controllerPort(0).deviceSlot(0).build();
   }

}
