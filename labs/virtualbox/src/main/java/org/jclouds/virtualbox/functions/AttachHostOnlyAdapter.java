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

import org.jclouds.virtualbox.domain.NetworkInterfaceCard;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.INetworkAdapter;
import org.virtualbox_4_2.NetworkAdapterType;
import org.virtualbox_4_2.NetworkAttachmentType;

import com.google.common.base.Function;

/**
 * @author dralves
 */
public class AttachHostOnlyAdapter implements Function<IMachine, Void> {

   private NetworkInterfaceCard networkInterfaceCard;

   public AttachHostOnlyAdapter(NetworkInterfaceCard networkInterfaceCard) {
      this.networkInterfaceCard = networkInterfaceCard;
   }

   @Override
   public Void apply(IMachine machine) {
      INetworkAdapter iNetworkAdapter = machine.getNetworkAdapter(networkInterfaceCard.getSlot());
      iNetworkAdapter.setAttachmentType(NetworkAttachmentType.HostOnly);
      iNetworkAdapter.setMACAddress(networkInterfaceCard.getNetworkAdapter().getMacAddress());
      iNetworkAdapter.setHostOnlyInterface(networkInterfaceCard.getHostInterfaceName());
      iNetworkAdapter.setEnabled(networkInterfaceCard.isEnabled());
      machine.saveSettings();
      return null;
   }

}
