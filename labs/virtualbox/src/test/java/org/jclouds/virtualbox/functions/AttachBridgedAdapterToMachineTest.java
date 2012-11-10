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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.virtualbox_4_2.NetworkAttachmentType.Bridged;

import org.jclouds.virtualbox.domain.NetworkAdapter;
import org.jclouds.virtualbox.domain.NetworkInterfaceCard;
import org.testng.annotations.Test;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.INetworkAdapter;
import org.virtualbox_4_2.NetworkAttachmentType;

/**
 * @author Andrea Turli
 */
@Test(groups = "unit", testName = "AttachBridgedAdapterToMachineTest")
public class AttachBridgedAdapterToMachineTest {

	private String macAddress;
	private String hostInterface;

	@Test
	public void testApplyNetworkingToNonExistingAdapter() throws Exception {
		Long adapterId = 0l;
		IMachine machine = createMock(IMachine.class);
		INetworkAdapter iNetworkAdapter = createMock(INetworkAdapter.class);

		expect(machine.getNetworkAdapter(adapterId)).andReturn(iNetworkAdapter);
		iNetworkAdapter.setAttachmentType(Bridged);
		iNetworkAdapter.setMACAddress(macAddress);
		iNetworkAdapter.setBridgedInterface(hostInterface);
		iNetworkAdapter.setEnabled(true);
		machine.saveSettings();

		replay(machine, iNetworkAdapter);
		NetworkAdapter networkAdapter = NetworkAdapter.builder()
				.networkAttachmentType(NetworkAttachmentType.Bridged).build();
		NetworkInterfaceCard networkInterfaceCard = NetworkInterfaceCard
				.builder().addNetworkAdapter(networkAdapter).build();

		new AttachBridgedAdapterToMachine(networkInterfaceCard).apply(machine);

		verify(machine, iNetworkAdapter);
	}

}
