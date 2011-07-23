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
package org.jclouds.vcloud.domain.network.internal;

import java.net.URI;
import java.util.Set;

import javax.annotation.Nullable;

import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.internal.ReferenceTypeImpl;
import org.jclouds.vcloud.domain.network.FenceMode;
import org.jclouds.vcloud.domain.network.VCloudExpressNetwork;
import org.jclouds.vcloud.domain.network.firewall.FirewallRule;
import org.jclouds.vcloud.domain.network.nat.rules.PortForwardingRule;

import com.google.common.collect.Sets;

/**
 * Locations of resources in vCloud
 * 
 * @author Adrian Cole
 * 
 */
public class VCloudExpressNetworkImpl extends ReferenceTypeImpl implements VCloudExpressNetwork {

   protected final String description;
   protected final Set<String> dnsServers = Sets.newHashSet();
   protected final String gateway;
   protected final String netmask;
   protected final Set<FenceMode> fenceModes = Sets.newHashSet();
   @Nullable
   protected final Boolean dhcp;
   protected final Set<PortForwardingRule> natRules = Sets.newHashSet();
   protected final Set<FirewallRule> firewallRules = Sets.newHashSet();

   public VCloudExpressNetworkImpl(String name, String type, URI id, String description, Set<String> dnsServers,
            String gateway, String netmask, Set<FenceMode> fenceModes, Boolean dhcp, Set<PortForwardingRule> natRules,
            Set<FirewallRule> firewallRules) {
      super(name, type, id);
      this.description = description;
      this.dnsServers.addAll(dnsServers);
      this.gateway = gateway;
      this.netmask = netmask;
      this.fenceModes.addAll(fenceModes);
      this.dhcp = dhcp;
      this.natRules.addAll(natRules);
      this.firewallRules.addAll(firewallRules);
   }

   /**
    * {@inheritDoc}
    */
   public String getDescription() {
      return description;
   }

   /**
    * {@inheritDoc}
    */
   public Set<String> getDnsServers() {
      return dnsServers;
   }

   /**
    * {@inheritDoc}
    */
   public String getGateway() {
      return gateway;
   }

   /**
    * {@inheritDoc}
    */
   public String getNetmask() {
      return netmask;
   }

   /**
    * {@inheritDoc}
    */
   public Set<FenceMode> getFenceModes() {
      return fenceModes;
   }

   /**
    * {@inheritDoc}
    */
   public Boolean isDhcp() {
      return dhcp;
   }

   /**
    * {@inheritDoc}
    */
   public Set<PortForwardingRule> getNatRules() {
      return natRules;
   }

   /**
    * {@inheritDoc}
    */
   public Set<FirewallRule> getFirewallRules() {
      return firewallRules;
   }

   @Override
   public int compareTo(ReferenceType o) {
      return (this == o) ? 0 : getHref().compareTo(o.getHref());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((dhcp == null) ? 0 : dhcp.hashCode());
      result = prime * result + ((dnsServers == null) ? 0 : dnsServers.hashCode());
      result = prime * result + ((fenceModes == null) ? 0 : fenceModes.hashCode());
      result = prime * result + ((firewallRules == null) ? 0 : firewallRules.hashCode());
      result = prime * result + ((gateway == null) ? 0 : gateway.hashCode());
      result = prime * result + ((natRules == null) ? 0 : natRules.hashCode());
      result = prime * result + ((netmask == null) ? 0 : netmask.hashCode());
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
      VCloudExpressNetworkImpl other = (VCloudExpressNetworkImpl) obj;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (dhcp == null) {
         if (other.dhcp != null)
            return false;
      } else if (!dhcp.equals(other.dhcp))
         return false;
      if (dnsServers == null) {
         if (other.dnsServers != null)
            return false;
      } else if (!dnsServers.equals(other.dnsServers))
         return false;
      if (fenceModes == null) {
         if (other.fenceModes != null)
            return false;
      } else if (!fenceModes.equals(other.fenceModes))
         return false;
      if (firewallRules == null) {
         if (other.firewallRules != null)
            return false;
      } else if (!firewallRules.equals(other.firewallRules))
         return false;
      if (gateway == null) {
         if (other.gateway != null)
            return false;
      } else if (!gateway.equals(other.gateway))
         return false;
      if (natRules == null) {
         if (other.natRules != null)
            return false;
      } else if (!natRules.equals(other.natRules))
         return false;
      if (netmask == null) {
         if (other.netmask != null)
            return false;
      } else if (!netmask.equals(other.netmask))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + getHref() + ", name=" + getName() + ", type=" + getType() + ", description=" + description
               + ", dhcp=" + dhcp + ", dnsServers=" + dnsServers + ", fenceModes=" + fenceModes + ", firewallRules="
               + firewallRules + ", gateway=" + gateway + ", natRules=" + natRules + ", netmask=" + netmask + "]";
   }

}