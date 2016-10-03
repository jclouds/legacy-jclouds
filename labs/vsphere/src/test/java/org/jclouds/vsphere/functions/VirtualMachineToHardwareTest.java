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

package org.jclouds.vsphere.functions;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import org.jclouds.compute.domain.Hardware;
import org.testng.annotations.Test;

import com.vmware.vim25.VirtualHardware;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.mo.VirtualMachine;

@Test(groups = "unit", testName = "VirtualMachineToHardwareTest")
public class VirtualMachineToHardwareTest {

   @Test
   public void testConvert() throws Exception {
      VirtualMachine vm = createNiceMock(VirtualMachine.class);
      VirtualMachineConfigInfo info = createNiceMock(VirtualMachineConfigInfo.class);
      VirtualHardware virtualHardware = createNiceMock(VirtualHardware.class);
      String machineName = "hw-machineId";

      expect(vm.getName()).andReturn(machineName).anyTimes();
      expect(vm.getConfig()).andReturn(info).anyTimes();
      expect(info.getHardware()).andReturn(virtualHardware).anyTimes();
      expect(virtualHardware.getNumCPU()).andReturn(2).anyTimes();

      replay(vm, info, virtualHardware);

      Hardware hardware = new VirtualMachineToHardware().apply(vm);

      assertEquals(hardware.getId(), machineName);
      assertEquals(hardware.getProviderId(), machineName);
   }

}
