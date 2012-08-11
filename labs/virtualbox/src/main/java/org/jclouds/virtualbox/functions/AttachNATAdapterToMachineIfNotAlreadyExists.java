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

import static org.virtualbox_4_1.NetworkAttachmentType.NAT;

import org.jclouds.virtualbox.domain.NetworkInterfaceCard;
import org.jclouds.virtualbox.domain.RedirectRule;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.INetworkAdapter;
import org.virtualbox_4_1.VBoxException;

import com.google.common.base.Function;

/**
 * @author Mattias Holmqvist, Andrea Turli
 */
public class AttachNATAdapterToMachineIfNotAlreadyExists implements Function<IMachine, Void> {

   private NetworkInterfaceCard networkInterfaceCard;

   public AttachNATAdapterToMachineIfNotAlreadyExists(NetworkInterfaceCard networkInterfaceCard) {
      this.networkInterfaceCard = networkInterfaceCard;
   }

   @Override
   public Void apply(IMachine machine) {
      INetworkAdapter iNetworkAdapter = machine.getNetworkAdapter(networkInterfaceCard.getSlot());
      iNetworkAdapter.setAttachmentType(NAT);
      for (RedirectRule rule : networkInterfaceCard.getNetworkAdapter().getRedirectRules()) {
         try {
            String ruleName = String.format("%s@%s:%s->%s:%s",rule.getProtocol(), rule.getHost(), rule.getHostPort(), 
                     rule.getGuest(), rule.getGuestPort());
            iNetworkAdapter.getNatDriver().addRedirect(ruleName, rule.getProtocol(), rule.getHost(), rule.getHostPort(),
                     rule.getGuest(), rule.getGuestPort());
         } catch (VBoxException e) {
            if (!e.getMessage().contains("already exists"))
               throw e;
         }
      }
      iNetworkAdapter.setEnabled(true);
      machine.saveSettings();
      return null;
   }
}
