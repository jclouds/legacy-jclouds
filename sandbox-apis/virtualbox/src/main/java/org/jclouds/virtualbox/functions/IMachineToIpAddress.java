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

import com.google.common.base.Function;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.virtualbox.config.VirtualBoxConstants;
import org.virtualbox_4_1.IGuestOSType;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.VirtualBoxManager;

import javax.annotation.Nullable;

import static org.jclouds.compute.options.RunScriptOptions.Builder.runAsRoot;

/**
 * Get an IP address from an IMachine using arp of the host machine.
 * 
 * @author Mattias Holmqvist, Andrea Turli
 */
public class IMachineToIpAddress implements Function<IMachine, String> {

	private VirtualBoxManager manager;
	private ComputeService computeService;

	public IMachineToIpAddress(VirtualBoxManager manager,
			ComputeService computeService) {
		this.manager = manager;
		this.computeService = computeService;
	}

	@Override
	public String apply(@Nullable IMachine machine) {
		final String hostId = System
				.getProperty(VirtualBoxConstants.VIRTUALBOX_HOST_ID);
		boolean isOsX = isOSX(manager.getVBox().findMachine(hostId));
		String macAddress = formatMacAddress(machine.getNetworkAdapter(0l).getMACAddress(), isOsX);

		// TODO: This is both shell-dependent and hard-coded. Needs to be fixed.
		ExecResponse execResponse = runScriptOnNode(hostId,
				"for i in {1..254} ; do ping -c 1 -t 1 192.168.2.$i & done",
				runAsRoot(false).wrapInInitScript(false));
		System.out.println(execResponse);

		String arpLine = runScriptOnNode(hostId, "arp -an | grep " + macAddress,
				runAsRoot(false).wrapInInitScript(false)).getOutput();
		String ipAddress = arpLine.substring(arpLine.indexOf("(") + 1,
				arpLine.indexOf(")"));
		System.out.println("IP address " + ipAddress);
		return ipAddress;
	}

	private ExecResponse runScriptOnNode(String nodeId, String command,
			RunScriptOptions options) {
		return computeService.runScriptOnNode(nodeId, command, options);
	}

	protected boolean isOSX(IMachine machine) {
		String osTypeId = machine.getOSTypeId();
		IGuestOSType guestOSType = manager.getVBox().getGuestOSType(osTypeId);
		return guestOSType.getFamilyDescription().equals("Other");
	}

	/**
	 * This should format virtualbox mac address xxyyzzaabbcc into a valid mac address for the different shells
	 * i.e: bash - 
	 * $ arp -an
	 * ? (172.16.1.101) at 14:fe:b5:e2:fd:ba [ether] on eth0
	 * 

	 * @param vboxMacAddress
	 * @param hostId
	 * @return
	 */
	protected String formatMacAddress(String vboxMacAddress, boolean isOSX) {
		int offset = 0, step = 2;
		for (int j = 1; j <= 5; j++) {
			vboxMacAddress = new StringBuffer(vboxMacAddress)
					.insert(j * step + offset, ":").toString().toLowerCase();
			offset++;
		}

		String macAddress = vboxMacAddress;
		if (isOSX) {
			if (macAddress.contains("00"))
				macAddress = new StringBuffer(macAddress).delete(
						macAddress.indexOf("00"), macAddress.indexOf("00") + 1)
						.toString();

			if (macAddress.contains("0"))
				if (macAddress.indexOf("0") + 1 != ':'
						&& macAddress.indexOf("0") - 1 != ':')
					macAddress = new StringBuffer(macAddress).delete(
							macAddress.indexOf("0"), macAddress.indexOf("0") + 1)
							.toString();
		}

		return macAddress;

	}

}
