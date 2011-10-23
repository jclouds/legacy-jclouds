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

import static org.easymock.EasyMock.anyLong;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.*;
import static org.testng.Assert.assertNotSame;

/**
 * @author Mattias Holmqvist
 */
public class CreateMediumIfNotAlreadyExistsTest {

   @Test
   public void testCreateMediumWhenDiskDoesNotExists() throws Exception {
      String adminDiskPath = "/Users/johndoe/jclouds-virtualbox-images/admin.vdi";
      String diskFormat = "vdi";

      VirtualBoxManager manager = createNiceMock(VirtualBoxManager.class);
      IMachine machine = createMock(IMachine.class);
      IVirtualBox vBox = createMock(IVirtualBox.class);
      IMedium hardDisk = createMock(IMedium.class);
      IProgress progress = createNiceMock(IProgress.class);

      StringBuilder errorBuilder = new StringBuilder();
      errorBuilder.append("org.virtualbox_4_1.VBoxException: VirtualBox error: ");
      errorBuilder.append("Could not find an open hard disk with location ");
      errorBuilder.append("'/Users/johndoe/jclouds-virtualbox-test/testadmin.vdi' (0x80BB0001)");
      String errorMessage = errorBuilder.toString();

      expect(manager.getVBox()).andReturn(vBox).anyTimes();

      VBoxException notFoundException = new VBoxException(createNiceMock(Throwable.class), errorMessage);
      expect(vBox.findMedium(adminDiskPath, DeviceType.HardDisk)).andThrow(notFoundException);
      expect(vBox.createHardDisk(diskFormat, adminDiskPath)).andReturn(hardDisk);
      expect(hardDisk.createBaseStorage(anyLong(), anyLong())).andReturn(progress);

      replay(manager, machine, vBox, hardDisk);

      new CreateMediumIfNotAlreadyExists(manager, diskFormat, true).apply(adminDiskPath);

      verify(machine, vBox);

   }

   @Test
   public void testDeleteAndCreateNewStorageWhenMediumExistsAndUsingOverwrite() throws Exception {
      String adminDiskPath = "/Users/johndoe/jclouds-virtualbox-images/admin.vdi";
      String diskFormat = "vdi";

      VirtualBoxManager manager = createNiceMock(VirtualBoxManager.class);
      IMachine machine = createMock(IMachine.class);
      IVirtualBox vBox = createMock(IVirtualBox.class);
      IMedium hardDisk = createMock(IMedium.class);
      IMedium newHardDisk = createMock(IMedium.class);
      IProgress progress = createNiceMock(IProgress.class);

      expect(manager.getVBox()).andReturn(vBox).anyTimes();
      expect(vBox.findMedium(adminDiskPath, DeviceType.HardDisk)).andReturn(hardDisk);

      expect(hardDisk.deleteStorage()).andReturn(progress);
      expect(vBox.createHardDisk(diskFormat, adminDiskPath)).andReturn(newHardDisk);
      expect(newHardDisk.createBaseStorage(anyLong(), anyLong())).andReturn(progress);

      replay(manager, machine, vBox, hardDisk, newHardDisk, progress);

      IMedium newDisk = new CreateMediumIfNotAlreadyExists(manager, diskFormat, true).apply(adminDiskPath);

      verify(machine, vBox, hardDisk);
      assertNotSame(newDisk, hardDisk);
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testFailWhenMediumExistsAndNotUsingOverwrite() throws Exception {
      String adminDiskPath = "/Users/johndoe/jclouds-virtualbox-images/admin.vdi";
      String diskFormat = "vdi";

      VirtualBoxManager manager = createNiceMock(VirtualBoxManager.class);
      IMachine machine = createMock(IMachine.class);
      IVirtualBox vBox = createMock(IVirtualBox.class);
      IMedium hardDisk = createMock(IMedium.class);
      IMedium newHardDisk = createMock(IMedium.class);
      IProgress progress = createNiceMock(IProgress.class);

      expect(manager.getVBox()).andReturn(vBox).anyTimes();
      expect(vBox.findMedium(adminDiskPath, DeviceType.HardDisk)).andReturn(hardDisk);

      replay(manager, machine, vBox, hardDisk, newHardDisk, progress);

      new CreateMediumIfNotAlreadyExists(manager, diskFormat, false).apply(adminDiskPath);
   }

   @Test(expectedExceptions = VBoxException.class)
   public void testFailOnOtherVBoxException() throws Exception {

      String adminDiskPath = "/Users/johndoe/jclouds-virtualbox-images/admin.vdi";
      String diskFormat = "vdi";

      VirtualBoxManager manager = createNiceMock(VirtualBoxManager.class);
      IMachine machine = createMock(IMachine.class);
      IVirtualBox vBox = createMock(IVirtualBox.class);
      IMedium hardDisk = createMock(IMedium.class);
      IProgress progress = createNiceMock(IProgress.class);

      String errorMessage = "VirtualBox error: Some other VBox error";

      expect(manager.getVBox()).andReturn(vBox).anyTimes();

      VBoxException notFoundException = new VBoxException(createNiceMock(Throwable.class), errorMessage);
      expect(vBox.findMedium(adminDiskPath, DeviceType.HardDisk)).andThrow(notFoundException);
      expect(vBox.createHardDisk(diskFormat, adminDiskPath)).andReturn(hardDisk);
      expect(hardDisk.createBaseStorage(anyLong(), anyLong())).andReturn(progress);

      replay(manager, machine, vBox, hardDisk);

      new CreateMediumIfNotAlreadyExists(manager, diskFormat, true).apply(adminDiskPath);
   }


}
