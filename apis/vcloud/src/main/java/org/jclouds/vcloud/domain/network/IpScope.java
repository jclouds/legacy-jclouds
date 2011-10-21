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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * The IpScope element defines the address range, gateway, netmask, and other properties of the
 * network.
 * 
 */
public class IpScope {
   private final boolean inherited;
   @Nullable
   private final String gateway;
   @Nullable
   private final String netmask;
   @Nullable
   private final String dns1;
   @Nullable
   private final String dns2;
   @Nullable
   private final String dnsSuffix;
   private final Set<IpRange> ipRanges = Sets.newLinkedHashSet();
   private final Set<String> allocatedIpAddresses = Sets.newLinkedHashSet();

   public IpScope(boolean inherited, @Nullable String gateway, @Nullable String netmask, @Nullable String dns1,
            @Nullable String dns2, @Nullable String dnsSuffix, Iterable<IpRange> ipRanges,
            Iterable<String> allocatedIpAddresses) {
      this.inherited = inherited;
      this.gateway = gateway;
      this.netmask = netmask;
      this.dns1 = dns1;
      this.dns2 = dns2;
      this.dnsSuffix = dnsSuffix;
      Iterables.addAll(this.ipRanges, checkNotNull(ipRanges, "ipRanges"));
      Iterables.addAll(this.allocatedIpAddresses, checkNotNull(allocatedIpAddresses, "allocatedIpAddresses"));
   }

   /**
    * @return true of the values in this IpScope element are inherited from the ParentNetwork of the
    *         containing Configuration
    * @since vcloud api 0.9
    */
   public boolean isInherited() {
      return inherited;
   }

   /**
    * @return IP address of the network gateway
    * 
    * @since vcloud api 0.8
    */
   @Nullable
   public String getGateway() {
      return gateway;
   }

   /**
    * @return netmask to apply to addresses on the network
    * 
    * @since vcloud api 0.8
    */
   @Nullable
   public String getNetmask() {
      return netmask;
   }

   /**
    * @return IP address of the primary DNS server for this network
    * 
    * @since vcloud api 0.9
    */
   @Nullable
   public String getDns1() {
      return dns1;
   }

   /**
    * @return IP address of the secondary DNS server for this network
    * 
    * @since vcloud api 0.9
    */
   @Nullable
   public String getDns2() {
      return dns2;
   }

   /**
    * @return suffix to be applied when resolving hostnames that are not fully‐qualified.
    * 
    * @since vcloud api 0.9
    */
   @Nullable
   public String getDnsSuffix() {
      return dnsSuffix;
   }

   /**
    * @return A container for IpRange elements.
    * 
    * @since vcloud api 0.9
    */
   public Set<IpRange> getIpRanges() {
      return ipRanges;
   }

   /**
    * @return A list of addresses allocated from any of the specified IpRanges
    * 
    * @since vcloud api 0.9
    */
   public Set<String> getAllocatedIpAddresses() {
      return allocatedIpAddresses;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((allocatedIpAddresses == null) ? 0 : allocatedIpAddresses.hashCode());
      result = prime * result + ((dns1 == null) ? 0 : dns1.hashCode());
      result = prime * result + ((dns2 == null) ? 0 : dns2.hashCode());
      result = prime * result + ((dnsSuffix == null) ? 0 : dnsSuffix.hashCode());
      result = prime * result + ((gateway == null) ? 0 : gateway.hashCode());
      result = prime * result + (inherited ? 1231 : 1237);
      result = prime * result + ((ipRanges == null) ? 0 : ipRanges.hashCode());
      result = prime * result + ((netmask == null) ? 0 : netmask.hashCode());
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
      IpScope other = (IpScope) obj;
      if (allocatedIpAddresses == null) {
         if (other.allocatedIpAddresses != null)
            return false;
      } else if (!allocatedIpAddresses.equals(other.allocatedIpAddresses))
         return false;
      if (dns1 == null) {
         if (other.dns1 != null)
            return false;
      } else if (!dns1.equals(other.dns1))
         return false;
      if (dns2 == null) {
         if (other.dns2 != null)
            return false;
      } else if (!dns2.equals(other.dns2))
         return false;
      if (dnsSuffix == null) {
         if (other.dnsSuffix != null)
            return false;
      } else if (!dnsSuffix.equals(other.dnsSuffix))
         return false;
      if (gateway == null) {
         if (other.gateway != null)
            return false;
      } else if (!gateway.equals(other.gateway))
         return false;
      if (inherited != other.inherited)
         return false;
      if (ipRanges == null) {
         if (other.ipRanges != null)
            return false;
      } else if (!ipRanges.equals(other.ipRanges))
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
      return "[allocatedIpAddresses=" + allocatedIpAddresses + ", dns1=" + dns1 + ", dns2=" + dns2 + ", dnsSuffix="
               + dnsSuffix + ", gateway=" + gateway + ", inherited=" + inherited + ", ipRanges=" + ipRanges
               + ", netmask=" + netmask + "]";
   }
}