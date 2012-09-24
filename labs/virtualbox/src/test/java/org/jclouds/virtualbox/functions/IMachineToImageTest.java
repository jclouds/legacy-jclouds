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
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.Map;

import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.jclouds.virtualbox.config.VirtualBoxComputeServiceContextModule;
import org.jclouds.virtualbox.config.VirtualBoxConstants;
import org.testng.annotations.Test;
import org.virtualbox_4_2.IGuestOSType;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.IVirtualBox;
import org.virtualbox_4_2.MachineState;
import org.virtualbox_4_2.VirtualBoxManager;

import com.google.common.base.Suppliers;
import com.google.inject.Guice;

@Test(groups = "unit", testName = "IMachineToImageTest")
public class IMachineToImageTest {

   Map<OsFamily, Map<String, String>> map = new BaseComputeServiceContextModule() {
   }.provideOsVersionMap(new ComputeServiceConstants.ReferenceData(), Guice.createInjector(new GsonModule())
            .getInstance(Json.class));

   @Test
   public void testConvert() throws Exception {

      VirtualBoxManager vbm = createNiceMock(VirtualBoxManager.class);
      IVirtualBox vBox = createNiceMock(IVirtualBox.class);
      IMachine vm = createNiceMock(IMachine.class);
      IGuestOSType guestOsType = createNiceMock(IGuestOSType.class);
      String linuxDescription = "Ubuntu 10.04";
      expect(vbm.getVBox()).andReturn(vBox).anyTimes();

      expect(vm.getOSTypeId()).andReturn("os-type").anyTimes();
      expect(vm.getName()).andReturn(VirtualBoxConstants.VIRTUALBOX_IMAGE_PREFIX + "my-vm-id").anyTimes();
      expect(vBox.getGuestOSType(eq("os-type"))).andReturn(guestOsType);
      expect(vm.getDescription()).andReturn("my-ubuntu-machine").anyTimes();
      expect(guestOsType.getDescription()).andReturn(linuxDescription).anyTimes();
      expect(guestOsType.getIs64Bit()).andReturn(true);
      expect(vm.getState()).andReturn(MachineState.PoweredOff);

      replay(vbm, vBox, vm, guestOsType);

      IMachineToImage fn = new IMachineToImage(VirtualBoxComputeServiceContextModule.toPortableImageStatus, Suppliers
               .ofInstance(vbm), map);

      Image image = fn.apply(vm);

      assertEquals(image.getDescription(), "my-ubuntu-machine");
      assertEquals(image.getOperatingSystem().getDescription(), linuxDescription);
      assertTrue(image.getOperatingSystem().is64Bit());
      assertEquals(image.getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(image.getOperatingSystem().getVersion(), "10.04");
      assertEquals(image.getId(), "my-vm-id");
      assertEquals(image.getStatus(), Image.Status.AVAILABLE);

   }

   @Test
   public void testConvert1() throws Exception {

      VirtualBoxManager vbm = createNiceMock(VirtualBoxManager.class);
      IVirtualBox vBox = createNiceMock(IVirtualBox.class);
      IMachine vm = createNiceMock(IMachine.class);
      IGuestOSType guestOsType = createNiceMock(IGuestOSType.class);
      String guestOsDescription = "ubuntu 11.04 server (i386)";
      String vmDescription = "ubuntu-11.04-server-i386";
      expect(vbm.getVBox()).andReturn(vBox).anyTimes();

      expect(vm.getName()).andReturn(VirtualBoxConstants.VIRTUALBOX_IMAGE_PREFIX + "my-vm-id").anyTimes();
      expect(vm.getOSTypeId()).andReturn("os-type").anyTimes();
      expect(vBox.getGuestOSType(eq("os-type"))).andReturn(guestOsType);
      expect(vm.getDescription()).andReturn(vmDescription).anyTimes();
      expect(guestOsType.getDescription()).andReturn(guestOsDescription).anyTimes();
      expect(guestOsType.getIs64Bit()).andReturn(true);
      expect(vm.getState()).andReturn(MachineState.Running);

      replay(vbm, vBox, vm, guestOsType);

      IMachineToImage fn = new IMachineToImage(VirtualBoxComputeServiceContextModule.toPortableImageStatus, Suppliers
               .ofInstance(vbm), map);

      Image image = fn.apply(vm);

      assertEquals(image.getDescription(), vmDescription);
      assertEquals(image.getOperatingSystem().getDescription(), guestOsDescription);
      assertTrue(image.getOperatingSystem().is64Bit());
      assertEquals(image.getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(image.getOperatingSystem().getVersion(), "11.04");
      assertEquals(image.getId(), "my-vm-id");
      assertEquals(image.getStatus(), Image.Status.PENDING);

   }

   @Test
   public void testUnparseableOsString() throws Exception {

      VirtualBoxManager vbm = createNiceMock(VirtualBoxManager.class);
      IVirtualBox vBox = createNiceMock(IVirtualBox.class);
      IMachine vm = createNiceMock(IMachine.class);
      IGuestOSType guestOsType = createNiceMock(IGuestOSType.class);

      expect(vbm.getVBox()).andReturn(vBox).anyTimes();

      expect(vm.getName()).andReturn(VirtualBoxConstants.VIRTUALBOX_IMAGE_PREFIX + "my-vm-id").anyTimes();
      String unknownOsDescription = "SomeOtherOs 2.04";
      expect(vm.getOSTypeId()).andReturn("os-type").anyTimes();
      expect(vm.getDescription()).andReturn("my-unknown-machine").anyTimes();
      expect(guestOsType.getDescription()).andReturn(unknownOsDescription).anyTimes();
      expect(guestOsType.getIs64Bit()).andReturn(true);
      expect(vBox.getGuestOSType(eq("os-type"))).andReturn(guestOsType);
      expect(vm.getState()).andReturn(MachineState.PoweredOff);

      replay(vbm, vBox, vm, guestOsType);

      IMachineToImage fn = new IMachineToImage(VirtualBoxComputeServiceContextModule.toPortableImageStatus, Suppliers
               .ofInstance(vbm), map);

      Image image = fn.apply(vm);

      assertEquals(image.getOperatingSystem().getDescription(), "SomeOtherOs 2.04");
      assertEquals(image.getOperatingSystem().getVersion(), "");
      assertEquals(image.getId(), "my-vm-id");

   }

}
