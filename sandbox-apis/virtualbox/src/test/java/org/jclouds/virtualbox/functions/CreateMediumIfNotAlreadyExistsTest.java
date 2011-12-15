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

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.easymock.EasyMock.anyLong;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.testng.Assert.assertNotSame;

import org.jclouds.virtualbox.domain.DeviceDetails;
import org.jclouds.virtualbox.domain.HardDisk;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.virtualbox_4_1.DeviceType;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IMedium;
import org.virtualbox_4_1.IProgress;
import org.virtualbox_4_1.IVirtualBox;
import org.virtualbox_4_1.VBoxException;
import org.virtualbox_4_1.VirtualBoxManager;

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
      IMachine machine = createMock(IMachine.class);
      IVirtualBox vBox = createMock(IVirtualBox.class);
      IMedium medium = createMock(IMedium.class);
      IProgress progress = createNiceMock(IProgress.class);

      StringBuilder errorBuilder = new StringBuilder();
      errorBuilder.append("org.virtualbox_4_1.VBoxException: VirtualBox error: ");
      errorBuilder.append("Could not find an open hard disk with location ");
      errorBuilder.append("'/Users/johndoe/jclouds-virtualbox-test/testadmin.vdi' (0x80BB0001)");
      String errorMessage = errorBuilder.toString();

      expect(manager.getVBox()).andReturn(vBox).anyTimes();

      VBoxException notFoundException = new VBoxException(createNiceMock(Throwable.class), errorMessage);
      expect(vBox.findMedium(eq(adminDiskPath), eq(DeviceType.HardDisk))).andThrow(notFoundException);
      expect(vBox.createHardDisk(diskFormat, adminDiskPath)).andReturn(medium);
      expect(medium.createBaseStorage(anyLong(), anyLong())).andReturn(progress);

      replay(manager, machine, vBox, medium);

      new CreateMediumIfNotAlreadyExists(manager, true).apply(hardDisk);

      verify(machine, vBox);

   }

   @Test
   public void testDeleteAndCreateNewStorageWhenMediumExistsAndUsingOverwrite() throws Exception {
      HardDisk hardDisk = createTestHardDisk();

      VirtualBoxManager manager = createNiceMock(VirtualBoxManager.class);
      IMachine machine = createMock(IMachine.class);
      IVirtualBox vBox = createMock(IVirtualBox.class);
      IMedium medium = createMock(IMedium.class);
      IMedium newHardDisk = createMock(IMedium.class);
      IProgress progress = createNiceMock(IProgress.class);

      expect(manager.getVBox()).andReturn(vBox).anyTimes();
      expect(vBox.findMedium(adminDiskPath, DeviceType.HardDisk)).andReturn(medium);

      expect(medium.deleteStorage()).andReturn(progress);
      expect(vBox.createHardDisk(diskFormat, adminDiskPath)).andReturn(newHardDisk);
      expect(newHardDisk.createBaseStorage(anyLong(), anyLong())).andReturn(progress);

      replay(manager, machine, vBox, medium, newHardDisk, progress);

      IMedium newDisk = new CreateMediumIfNotAlreadyExists(manager, true).apply(hardDisk);

      verify(machine, vBox, medium);
      assertNotSame(newDisk, medium);
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testFailWhenMediumExistsAndNotUsingOverwrite() throws Exception {
      HardDisk hardDisk = createTestHardDisk();

      VirtualBoxManager manager = createNiceMock(VirtualBoxManager.class);
      IMachine machine = createMock(IMachine.class);
      IVirtualBox vBox = createMock(IVirtualBox.class);
      IMedium medium = createMock(IMedium.class);
      IMedium newHardDisk = createMock(IMedium.class);
      IProgress progress = createNiceMock(IProgress.class);

      expect(manager.getVBox()).andReturn(vBox).anyTimes();
      expect(vBox.findMedium(adminDiskPath, DeviceType.HardDisk)).andReturn(medium);

      replay(manager, machine, vBox, medium, newHardDisk, progress);

      new CreateMediumIfNotAlreadyExists(manager, false).apply(hardDisk);
   }

   @Test(expectedExceptions = VBoxException.class)
   public void testFailOnOtherVBoxException() throws Exception {

      HardDisk hardDisk = createTestHardDisk();

      VirtualBoxManager manager = createNiceMock(VirtualBoxManager.class);
      IMachine machine = createMock(IMachine.class);
      IVirtualBox vBox = createMock(IVirtualBox.class);
      IMedium medium = createMock(IMedium.class);
      IProgress progress = createNiceMock(IProgress.class);

      String errorMessage = "VirtualBox error: Some other VBox error";

      expect(manager.getVBox()).andReturn(vBox).anyTimes();

      VBoxException notFoundException = new VBoxException(createNiceMock(Throwable.class), errorMessage);
      expect(vBox.findMedium(adminDiskPath, DeviceType.HardDisk)).andThrow(notFoundException);
      expect(vBox.createHardDisk(diskFormat, adminDiskPath)).andReturn(medium);
      expect(medium.createBaseStorage(anyLong(), anyLong())).andReturn(progress);

      replay(manager, machine, vBox, medium);

      new CreateMediumIfNotAlreadyExists(manager, true).apply(hardDisk);
   }

   private HardDisk createTestHardDisk() {
      return new HardDisk(new DeviceDetails(0, 0, DeviceType.HardDisk), adminDiskPath, diskFormat);
   }

}
