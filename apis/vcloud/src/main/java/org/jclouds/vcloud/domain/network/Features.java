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

import org.jclouds.javax.annotation.Nullable;

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
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((dhcpService == null) ? 0 : dhcpService.hashCode());
      result = prime * result + ((firewallService == null) ? 0 : firewallService.hashCode());
      result = prime * result + ((natService == null) ? 0 : natService.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Features other = (Features) obj;
      if (dhcpService == null) {
         if (other.dhcpService != null)
            return false;
      } else if (!dhcpService.equals(other.dhcpService))
         return false;
      if (firewallService == null) {
         if (other.firewallService != null)
            return false;
      } else if (!firewallService.equals(other.firewallService))
         return false;
      if (natService == null) {
         if (other.natService != null)
            return false;
      } else if (!natService.equals(other.natService))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[dhcpService=" + dhcpService + ", firewallService=" + firewallService + ", natService=" + natService
               + "]";
   }

}