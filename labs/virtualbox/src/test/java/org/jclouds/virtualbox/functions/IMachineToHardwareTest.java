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

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.predicates.ImagePredicates;
import org.testng.annotations.Test;
import org.virtualbox_4_2.IGuestOSType;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.IVirtualBox;
import org.virtualbox_4_2.VirtualBoxManager;

import com.google.common.base.Suppliers;

@Test(groups = "unit")
public class IMachineToHardwareTest {

   @Test
   public void testConvert() throws Exception {
      VirtualBoxManager vbm = createNiceMock(VirtualBoxManager.class);
      IVirtualBox vBox = createNiceMock(IVirtualBox.class);
      IMachine vm = createNiceMock(IMachine.class);
      IGuestOSType guestOsType = createNiceMock(IGuestOSType.class);

      String linuxDescription = "Ubuntu Linux 10.04";
      String machineName = "hw-machineId";

      expect(vm.getOSTypeId()).andReturn("os-type").anyTimes();
      expect(vm.getName()).andReturn(machineName).anyTimes();

      expect(vm.getDescription()).andReturn(linuxDescription).anyTimes();

      expect(vBox.getGuestOSType(eq("os-type"))).andReturn(guestOsType);
      expect(vbm.getVBox()).andReturn(vBox);
      expect(guestOsType.getIs64Bit()).andReturn(true);

      replay(vbm, vBox, vm, guestOsType);

      Hardware hardware = new IMachineToHardware(Suppliers.ofInstance(vbm)).apply(vm);

      assertEquals(hardware.getId(), machineName);
      assertEquals(hardware.getProviderId(), machineName);
      // for starters assume 1-to-1 relationship hardware to image (which
      // correlate to a single source IMachine)
      assertEquals(hardware.supportsImage().toString(), ImagePredicates.idEquals(machineName).toString());

   }

}
