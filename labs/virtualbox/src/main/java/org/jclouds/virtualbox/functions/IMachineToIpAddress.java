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

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.virtualbox.config.VirtualBoxConstants;
import org.virtualbox_4_2.IGuestOSType;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.VirtualBoxManager;

import com.google.common.base.Function;

/**
 * Get an IP address from an IMachine using arp of the host machine.
 * 
 * @author Mattias Holmqvist, Andrea Turli
 */
public class IMachineToIpAddress implements Function<IMachine, String> {

   private VirtualBoxManager manager;
   private ComputeService computeService;

   public IMachineToIpAddress(VirtualBoxManager manager, ComputeService computeService) {
      this.manager = manager;
      this.computeService = computeService;
   }

   @Override
   public String apply(IMachine machine) {

      String macAddress = machine.getNetworkAdapter(0l).getMACAddress();
      int offset = 0, step = 2;
      for (int j = 1; j <= 5; j++) {
         macAddress = new StringBuilder(macAddress).insert(j * step + offset, ":").toString().toLowerCase();
         offset++;
      }

      String simplifiedMacAddressOfClonedVM = macAddress;

      final String hostId = System.getProperty(VirtualBoxConstants.VIRTUALBOX_HOST_ID);
      IMachine hostMachine = manager.getVBox().findMachine(hostId);
      if (isOSX(hostMachine)) {
         if (simplifiedMacAddressOfClonedVM.contains("00"))
            simplifiedMacAddressOfClonedVM = new StringBuilder(simplifiedMacAddressOfClonedVM).delete(
                  simplifiedMacAddressOfClonedVM.indexOf("00"), simplifiedMacAddressOfClonedVM.indexOf("00") + 1)
                  .toString();

         if (simplifiedMacAddressOfClonedVM.contains("0"))
            if (simplifiedMacAddressOfClonedVM.indexOf("0") + 1 != ':'
                  && simplifiedMacAddressOfClonedVM.indexOf("0") - 1 != ':')
               simplifiedMacAddressOfClonedVM = new StringBuilder(simplifiedMacAddressOfClonedVM).delete(
                     simplifiedMacAddressOfClonedVM.indexOf("0"), simplifiedMacAddressOfClonedVM.indexOf("0") + 1)
                     .toString();
      }

      // TODO: This is both shell-dependent and hard-coded. Needs to be fixed.
      ExecResponse execResponse = runScriptOnNode(hostId, "for i in {1..254} ; do ping -c 1 -t 1 192.168.2.$i & done",
            runAsRoot(false).wrapInInitScript(false));
      System.out.println(execResponse);

      String arpLine = runScriptOnNode(hostId, "arp -an | grep " + simplifiedMacAddressOfClonedVM,
            runAsRoot(false).wrapInInitScript(false)).getOutput();
      String ipAddress = arpLine.substring(arpLine.indexOf("(") + 1, arpLine.indexOf(")"));
      System.out.println("IP address " + ipAddress);
      return ipAddress;
   }

   private ExecResponse runScriptOnNode(String nodeId, String command, RunScriptOptions options) {
      return computeService.runScriptOnNode(nodeId, command, options);
   }

   protected boolean isOSX(IMachine machine) {
      String osTypeId = machine.getOSTypeId();
      IGuestOSType guestOSType = manager.getVBox().getGuestOSType(osTypeId);
      return guestOSType.getFamilyDescription().equals("Other");
   }

}
