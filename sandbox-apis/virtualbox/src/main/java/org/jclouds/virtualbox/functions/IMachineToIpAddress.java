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
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.virtualbox.domain.GetIPAddressFromMAC;
import org.jclouds.virtualbox.domain.ScanNetworkWithPing;
import org.virtualbox_4_1.IMachine;

import com.google.common.base.Function;
import com.google.inject.Inject;

/**
 * Get an IP address from an IMachine using arp of the host machine.
 * 
 * @author Mattias Holmqvist, Andrea Turli
 */
public class IMachineToIpAddress implements Function<IMachine, String> {

   private ComputeServiceContext context;
   private String hostId;
   private String network;

   @Inject
   public IMachineToIpAddress(ComputeServiceContext context, String hostId,
         String network) {
      this.context = context;
      this.hostId = hostId;
      this.network = network;
   }

   @Override
   public String apply(@Nullable IMachine machine) {
      
      OsFamily osFamily = detectOsFamily();

      runScriptOnNode(hostId,
            new ScanNetworkWithPing(network).render(osFamily),
            runAsRoot(false).wrapInInitScript(false));
      
      String command = new GetIPAddressFromMAC(machine.getNetworkAdapter(0l).getMACAddress()).render(osFamily);
      String arpLine = runScriptOnNode(hostId, command,
            runAsRoot(false).wrapInInitScript(false)).getOutput();
      return arpLine.substring(arpLine.indexOf("(") + 1, arpLine.indexOf(")"));
   }

	protected OsFamily detectOsFamily() {
		OsFamily osFamily = OsFamily.UNIX;
		if(context.getComputeService().getNodeMetadata(hostId).getOperatingSystem().getFamily().name().equals(org.jclouds.compute.domain.OsFamily.WINDOWS.name()))
            osFamily = OsFamily.WINDOWS;
		return osFamily;
	}


   private ExecResponse runScriptOnNode(String nodeId, String command,
         RunScriptOptions options) {
      return context.getComputeService().runScriptOnNode(nodeId, command,
            options);
   }

}