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

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import org.jclouds.compute.ComputeService;
import org.testng.annotations.Test;
import org.virtualbox_4_1.IGuestOSType;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IVirtualBox;
import org.virtualbox_4_1.VirtualBoxManager;


/**
 * Get an IP address from an IMachine using arp of the host machine.
 * 
 * @author Andrea Turli
 */
@Test(groups = "unit")
public class IMachineToIpAddressTest {

	  @Test
	  public void testFormatMacAddress() {
	      VirtualBoxManager vbm = createNiceMock(VirtualBoxManager.class);
	      ComputeService computeService = createNiceMock(ComputeService.class);

	      IVirtualBox vBox = createNiceMock(IVirtualBox.class);
	      IMachine vm = createNiceMock(IMachine.class);
	      IGuestOSType guestOsType = createNiceMock(IGuestOSType.class);
	      String linuxDescription = "Ubuntu 10.04";
	      expect(vbm.getVBox()).andReturn(vBox).anyTimes();

	      expect(vm.getOSTypeId()).andReturn("os-type").anyTimes();
	      expect(vBox.getGuestOSType(eq("os-type"))).andReturn(guestOsType);
	      expect(vm.getDescription()).andReturn("my-ubuntu-machine").anyTimes();
	      expect(guestOsType.getDescription()).andReturn(linuxDescription).anyTimes();
	      expect(guestOsType.getIs64Bit()).andReturn(true);

	      replay(vbm, computeService, vBox, vm, guestOsType);

			assertEquals(new IMachineToIpAddress(vbm, computeService).formatMacAddress("0800271A9806", false), "8:0:27:1A:98:06");

	  }
}
