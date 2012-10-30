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
package org.jclouds.vcloud.domain.network;

import static com.google.common.base.Objects.equal;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;

/**
 * The Features element defines the DHCP and firewall features of a network.
 */
public class Features {
   @Nullable
   private final DhcpService dhcpService;
   @Nullable
   private final FirewallService firewallService;
   @Nullable
   private final NatService natService;

   public Features(@Nullable DhcpService dhcpService, @Nullable FirewallService firewallService,
            @Nullable NatService natService) {
      this.dhcpService = dhcpService;
      this.firewallService = firewallService;
      this.natService = natService;
   }

   /**
    * specifies the properties of the network’s DHCP service
    * 
    * @since vcloud api 0.9, but emulated for 0.8
    */
   @Nullable
   public DhcpService getDhcpService() {
      return dhcpService;
   }

   /**
    * defines the firewall service capabilities of the network
    * 
    * @since vcloud api 0.8
    */
   @Nullable
   public FirewallService getFirewallService() {
      return firewallService;
   }

   /**
    * defines the NAT service capabilities of the network
    * 
    * @since vcloud api 0.8
    */
   @Nullable
   public NatService getNatService() {
      return natService;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      Features that = Features.class.cast(o);
      return equal(this.dhcpService, that.dhcpService) && equal(this.firewallService, that.firewallService)
            && equal(this.natService, that.natService);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(dhcpService, firewallService, natService);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").omitNullValues().add("dhcpService", dhcpService)
            .add("firewallService", firewallService).add("natService", natService).toString();
   }

}
