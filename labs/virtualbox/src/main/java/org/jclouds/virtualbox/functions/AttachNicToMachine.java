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

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.virtualbox.domain.NetworkInterfaceCard;
import org.jclouds.virtualbox.util.MachineUtils;
import org.virtualbox_4_2.NetworkAttachmentType;

import com.google.common.base.Function;

/**
 * @author Andrea Turli
 */
public class AttachNicToMachine implements Function<NetworkInterfaceCard, Void> {

   private final String vmName;
   private final MachineUtils machineUtils;

   public AttachNicToMachine(String vmName, MachineUtils machineUtils) {
      this.vmName = checkNotNull(vmName, "vmName can't be null");
      this.machineUtils = checkNotNull(machineUtils, "machineUtils can't be null");
   }

   @Override
   public Void apply(NetworkInterfaceCard nic) {
      if (hasNatAdapter(nic)) {
         return machineUtils.writeLockMachineAndApply(vmName, new AttachNATAdapterToMachineIfNotAlreadyExists(nic));
      } else if (hasBridgedAdapter(nic)) {
         return machineUtils.writeLockMachineAndApply(vmName, new AttachBridgedAdapterToMachine(nic));
      } else if (hasHostOnlyAdapter(nic)) {
         return machineUtils.writeLockMachineAndApply(vmName, new AttachHostOnlyAdapter(nic));
      } else
         return null;
   }

   private boolean hasNatAdapter(NetworkInterfaceCard nic) {
      return nic.getNetworkAdapter().getNetworkAttachmentType().equals(NetworkAttachmentType.NAT);
   }

   private boolean hasBridgedAdapter(NetworkInterfaceCard nic) {
      return nic.getNetworkAdapter().getNetworkAttachmentType().equals(NetworkAttachmentType.Bridged);
   }

   private boolean hasHostOnlyAdapter(NetworkInterfaceCard nic) {
      return nic.getNetworkAdapter().getNetworkAttachmentType().equals(NetworkAttachmentType.HostOnly);
   }
}
