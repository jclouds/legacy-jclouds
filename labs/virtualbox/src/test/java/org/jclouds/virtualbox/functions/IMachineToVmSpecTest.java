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

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import org.jclouds.virtualbox.domain.HardDisk;
import org.jclouds.virtualbox.domain.IsoImage;
import org.jclouds.virtualbox.domain.StorageController;
import org.jclouds.virtualbox.domain.VmSpec;
import org.testng.annotations.Test;
import org.virtualbox_4_2.DeviceType;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.IMedium;
import org.virtualbox_4_2.IMediumAttachment;
import org.virtualbox_4_2.IStorageController;
import org.virtualbox_4_2.StorageBus;
import org.virtualbox_4_2.VirtualBoxManager;

import com.google.common.collect.Lists;

@Test(groups = "unit")
public class IMachineToVmSpecTest {

   private static final String PATH_TO_DVD = "/path/to/dvd";
   private static final String PATH_TO_HD = "/path/to/hd";
   private static final StorageBus CONTROLLER_BUS = StorageBus.IDE;
   private static final long MEMORY_SIZE = 512L;
   private static final String OS_TYPE_ID = "ubuntu";
   private static final String VM_NAME = "test";
   private static final String CONTROLLER_NAME = "IDE Controller";
   private static final String VM_ID = "test";

   @Test
   public void testConvert() throws Exception {

      VirtualBoxManager vbm = createNiceMock(VirtualBoxManager.class);
      IStorageController iStorageController = createNiceMock(IStorageController.class);
      IMediumAttachment iMediumAttachment = createNiceMock(IMediumAttachment.class);
      IMedium hd = createNiceMock(IMedium.class);
      IMedium dvd = createNiceMock(IMedium.class);
      IMachine vm = createNiceMock(IMachine.class);

      expect(vm.getStorageControllers()).andReturn(Lists.newArrayList(iStorageController)).anyTimes();
      expect(iStorageController.getName()).andReturn(CONTROLLER_NAME).anyTimes();
      expect(iStorageController.getBus()).andReturn(CONTROLLER_BUS).anyTimes();
      expect(vm.getMediumAttachmentsOfController(CONTROLLER_NAME)).andReturn(Lists.newArrayList(iMediumAttachment)).anyTimes();
      expect(iMediumAttachment.getPort()).andReturn(0).once();
      expect(iMediumAttachment.getDevice()).andReturn(0).once();

      expect(iMediumAttachment.getMedium()).andReturn(hd);
      expect(hd.getDeviceType()).andReturn(DeviceType.HardDisk).once();
      expect(hd.getLocation()).andReturn(PATH_TO_HD).once();

      expect(iMediumAttachment.getMedium()).andReturn(dvd);
      expect(dvd.getDeviceType()).andReturn(DeviceType.DVD).once();
      expect(dvd.getLocation()).andReturn(PATH_TO_DVD).once();

      expect(vm.getName()).andReturn(VM_NAME).anyTimes();
      expect(vm.getId()).andReturn(VM_ID).anyTimes();
      expect(vm.getOSTypeId()).andReturn(OS_TYPE_ID).anyTimes();
      expect(vm.getMemorySize()).andReturn(MEMORY_SIZE).anyTimes();

      replay(vbm, iStorageController, iMediumAttachment, hd, dvd, vm);

      VmSpec vmSpec = new IMachineToVmSpec().apply(vm);

      assertEquals(vmSpec.getVmName(), VM_NAME);
      assertEquals(vmSpec.getVmId(), VM_ID);
      assertEquals(vmSpec.getMemory(), MEMORY_SIZE);
      for(StorageController controller : vmSpec.getControllers()) {
         assertEquals(controller.getName(), CONTROLLER_NAME);
         assertEquals(controller.getBus(), CONTROLLER_BUS);
         for (HardDisk hardDisk : controller.getHardDisks()) {
            assertEquals(hardDisk.getDiskPath(), PATH_TO_HD);
         }
         for (IsoImage iso : controller.getIsoImages()) {
            assertEquals(iso.getSourcePath(), PATH_TO_DVD);
         }
      }
   }
}
