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
package org.jclouds.vcloud.domain.network.nat.rules;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.domain.network.nat.NatProtocol;
import org.jclouds.vcloud.domain.network.nat.NatRule;

import com.google.common.base.Objects;

/**
 * The VmRule element describes a NAT rule that maps an IP address and port in a vApp network to an
 * external IP address and port. The external IP address, external port, and internal port are
 * specified in the element. The internal IP address is discovered by looking up the specified
 * VmReference and VmNicId.
 * 
 * @since vcloud 0.9
 * @author Adrian Cole
 */
public class VmRule implements NatRule {
   @Nullable
   private final String externalIP;
   private final int externalPort;
   @Nullable
   private final String vAppScopedLocalId;
   private final int vmNicId;
   private final int internalPort;
   private final NatProtocol protocol;

   public VmRule(@Nullable String externalIP, int externalPort, @Nullable String vAppScopedLocalId, int vmNicId,
            int internalPort, NatProtocol protocol) {
      this.externalIP = externalIP;
      this.externalPort = externalPort;
      this.vAppScopedLocalId = vAppScopedLocalId;
      this.vmNicId = vmNicId;
      this.internalPort = internalPort;
      this.protocol = checkNotNull(protocol, "protocol");
   }

   /**
    * IP address to which this NAT rule maps the IP address specified in the vAppScopedLocalId element.
    */
   @Nullable
   public String getExternalIP() {
      return externalIP;
   }

   /**
    * network port to which this NAT rule maps the port number specified in the InternalPort element
    */
   public Integer getExternalPort() {
      return externalPort;
   }

   /**
    * @return read‐only identifier created on import
    * @since vcloud 0.9
    */
   @Nullable
   public String getVAppScopedLocalId() {
      return vAppScopedLocalId;
   }

   /**
    * @return device number of the NIC on the referenced virtual machine
    * @since vcloud 0.9
    */
   public int getVmNicId() {
      return vmNicId;
   }

   /**
    * network port to which this NAT rule maps the port number specified in the ExternalPort
    * element.
    */
   public Integer getInternalPort() {
      return internalPort;
   }

   /**
    * specifies the network protocol to which this rule applies
    */
   public NatProtocol getProtocol() {
      return protocol;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      VmRule that = VmRule.class.cast(o);
      return equal(this.externalIP, that.externalIP) && equal(this.externalPort, that.externalPort)
            && equal(this.vAppScopedLocalId, that.vAppScopedLocalId) && equal(this.vmNicId, that.vmNicId)
            && equal(this.internalPort, that.internalPort) && equal(this.protocol, that.protocol);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(externalIP, externalPort, vAppScopedLocalId, vmNicId, internalPort, protocol);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").omitNullValues().add("externalIP", externalIP)
            .add("externalPort", externalPort).add("vAppScopedLocalId", vAppScopedLocalId).add("vmNicId", vmNicId)
            .add("internalPort", internalPort).add("protocol", protocol).toString();
   }

}
