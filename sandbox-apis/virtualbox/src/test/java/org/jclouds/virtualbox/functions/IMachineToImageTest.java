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


import com.google.common.base.Function;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.testng.annotations.Test;
import org.virtualbox_4_1.IGuestOSType;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IVirtualBox;
import org.virtualbox_4_1.VirtualBoxManager;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

@Test(groups = "unit")
public class IMachineToImageTest {

   @Test
   public void testConvert() throws Exception {

      VirtualBoxManager vbm = createNiceMock(VirtualBoxManager.class);
      IVirtualBox vBox= createNiceMock(IVirtualBox.class);
      IMachine vm = createNiceMock(IMachine.class);
      IGuestOSType guestOsType = createNiceMock(IGuestOSType.class);
      String linuxDescription = "Ubuntu 10.04";
      expect(vbm.getVBox()).andReturn(vBox).anyTimes();

      expect(vm.getOSTypeId()).andReturn("os-type").anyTimes();
      expect(vBox.getGuestOSType(eq("os-type"))).andReturn(guestOsType);
      expect(vm.getDescription()).andReturn("my-ubuntu-machine").anyTimes();
      expect(guestOsType.getDescription()).andReturn(linuxDescription).anyTimes();
      expect(guestOsType.getIs64Bit()).andReturn(true);

      replay(vbm, vBox, vm, guestOsType);

      IMachineToImage fn = new IMachineToImage(vbm);

      Image image = fn.apply(vm);

      assertEquals(image.getDescription(), "my-ubuntu-machine");
      assertEquals(image.getOperatingSystem().getDescription(), linuxDescription);
      assertTrue(image.getOperatingSystem().is64Bit());
      assertEquals(image.getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(image.getOperatingSystem().getVersion(), "10.04");

   }

   @Test
   public void testOsVersion() throws Exception {

      String osDescription = "Ubuntu 10.04";

      Function<String, String> iMachineStringFunction = IMachineToImage.osVersion();
      assertEquals("10.04", iMachineStringFunction.apply(osDescription));

   }

   @Test
   public void testUnparseableOsString() throws Exception {
      
      VirtualBoxManager vbm = createNiceMock(VirtualBoxManager.class);
      IVirtualBox vBox= createNiceMock(IVirtualBox.class);
      IMachine vm = createNiceMock(IMachine.class);
      IGuestOSType guestOsType = createNiceMock(IGuestOSType.class);

      expect(vbm.getVBox()).andReturn(vBox).anyTimes();

      String unknownOsDescription = "SomeOtherOs 2.04";
      expect(vm.getOSTypeId()).andReturn("os-type").anyTimes();
      expect(vm.getDescription()).andReturn("my-unknown-machine").anyTimes();
      expect(guestOsType.getDescription()).andReturn(unknownOsDescription).anyTimes();
      expect(guestOsType.getIs64Bit()).andReturn(true);
      expect(vBox.getGuestOSType(eq("os-type"))).andReturn(guestOsType);

      replay(vbm, vBox, vm, guestOsType);

      Image image = new IMachineToImage(vbm).apply(vm);

      assertEquals(image.getOperatingSystem().getDescription(), "SomeOtherOs 2.04");
      assertEquals(image.getOperatingSystem().getVersion(), "");

   }


}
