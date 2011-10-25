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

import static org.jclouds.compute.options.RunScriptOptions.Builder.runAsRoot;

import javax.annotation.Nullable;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.options.RunScriptOptions;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IVirtualBox;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.inject.Inject;



/**
 * Get an IP address from an IMachine using arp of the host machine.
 * 
 * @author Mattias Holmqvist, Andrea Turli
 */
public class IMachineToIpAddress implements Function<IMachine, String> {

	private VirtualBoxManager manager;
   private ComputeServiceContext context;
   private String hostId;

   @Inject
	public IMachineToIpAddress(VirtualBoxManager manager, ComputeServiceContext context, String hostId) {
		this.manager = manager;
		this.context = context;
		this.hostId = hostId;
	}

	@Override
	public String apply(@Nullable IMachine machine) {
//		final String hostId = System
//				.getProperty(VirtualBoxConstants.VIRTUALBOX_HOST_ID);
      final IVirtualBox vBox = manager.getVBox();

		String macAddress = new FormatVboxMacAddressToShellMacAddress(/*isOSX(hostId)*/ true)
			.apply(machine.getNetworkAdapter(0l).getMACAddress());

		// TODO: This is both shell-dependent and hard-coded. Needs to be fixed.
		ExecResponse execResponse = runScriptOnNode(hostId,
				"for i in {1..254} ; do ping -c 1 -t 1 192.168.1.$i & done",
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
		return context.getComputeService().runScriptOnNode(nodeId, command, options);
	}
	
   public boolean isOSX(String id) {
      return context.getComputeService().getNodeMetadata(hostId).getOperatingSystem().getDescription().equals(
               "Mac OS X");
   }
}
