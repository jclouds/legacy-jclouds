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
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
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
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      IpScope that = IpScope.class.cast(o);
      return equal(this.inherited, that.inherited) && equal(this.gateway, that.gateway)
            && equal(this.netmask, that.netmask) && equal(this.dns1, that.dns1) && equal(this.dns2, that.dns2)
            && equal(this.dnsSuffix, that.dnsSuffix) && equal(this.ipRanges, ipRanges) && equal(this.allocatedIpAddresses, allocatedIpAddresses);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(inherited, gateway, netmask, dns1, dns2, dnsSuffix, ipRanges, allocatedIpAddresses);
   }

   @Override
   public String toString() {
      ToStringHelper helper = Objects.toStringHelper("").omitNullValues().add("inherited", inherited).add("gateway", gateway)
            .add("netmask", netmask).add("dns1", dns1).add("dns2", dns2).add("dnsSuffix", dnsSuffix);
      if (ipRanges.size() >0)
         helper.add("ipRanges", ipRanges);
      if (allocatedIpAddresses.size() >0)
         helper.add("allocatedIpAddresses", allocatedIpAddresses);
      return helper.toString();
   }
}
