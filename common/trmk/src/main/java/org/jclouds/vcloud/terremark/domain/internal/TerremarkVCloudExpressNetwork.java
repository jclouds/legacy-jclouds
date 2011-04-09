/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.vcloud.terremark.domain.internal;

import java.net.URI;
import java.util.Set;

import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.network.FenceMode;
import org.jclouds.vcloud.domain.network.firewall.FirewallRule;
import org.jclouds.vcloud.domain.network.internal.VCloudExpressNetworkImpl;
import org.jclouds.vcloud.domain.network.nat.rules.PortForwardingRule;

/**
 * Locations of resources in vCloud
 * 
 * @author Adrian Cole
 * 
 */
public class TerremarkVCloudExpressNetwork extends VCloudExpressNetworkImpl {
   private final ReferenceType networkExtension;
   private final ReferenceType ips;

   public TerremarkVCloudExpressNetwork(String name, String type, URI id, String description, Set<String> dnsServers,
            String gateway, String netmask, Set<FenceMode> fenceModes, Boolean dhcp, Set<PortForwardingRule> natRules,
            Set<FirewallRule> firewallRules, ReferenceType networkExtension, ReferenceType ips) {
      super(name, type, id, description, dnsServers, gateway, netmask, fenceModes, dhcp, natRules, firewallRules);
      this.networkExtension = networkExtension;
      this.ips = ips;
   }

   public ReferenceType getNetworkExtension() {
      return networkExtension;
   }

   public ReferenceType getIps() {
      return ips;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((ips == null) ? 0 : ips.hashCode());
      result = prime * result + ((networkExtension == null) ? 0 : networkExtension.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      TerremarkVCloudExpressNetwork other = (TerremarkVCloudExpressNetwork) obj;
      if (ips == null) {
         if (other.ips != null)
            return false;
      } else if (!ips.equals(other.ips))
         return false;
      if (networkExtension == null) {
         if (other.networkExtension != null)
            return false;
      } else if (!networkExtension.equals(other.networkExtension))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + getHref() + ", name=" + getName() + ", type=" + getType() + ", description=" + description
               + ", dhcp=" + dhcp + ", dnsServers=" + dnsServers + ", fenceModes=" + fenceModes + ", firewallRules="
               + firewallRules + ", gateway=" + gateway + ", natRules=" + natRules + ", netmask=" + netmask + ",ips="
               + ips + ", networkExtension=" + networkExtension + "]";
   }
}