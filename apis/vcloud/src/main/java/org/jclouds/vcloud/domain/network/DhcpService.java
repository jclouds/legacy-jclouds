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
 * specifies the properties of the network’s DHCP service
 */
public class DhcpService {
   private final boolean enabled;
   @Nullable
   private final Integer defaultLeaseTime;
   @Nullable
   private final Integer maxLeaseTime;
   @Nullable
   private final IpRange ipRange;

   public DhcpService(boolean enabled, @Nullable Integer defaultLeaseTime, @Nullable Integer maxLeaseTime,
            @Nullable IpRange ipRange) {
      this.enabled = enabled;
      this.defaultLeaseTime = defaultLeaseTime;
      this.maxLeaseTime = maxLeaseTime;
      this.ipRange = ipRange;
   }

   /**
    * @return true if the service is enabled
    * 
    * @since vcloud api 0.8
    */
   public boolean isEnabled() {
      return enabled;
   }

   /**
    * default duration of a DHCP address lease
    * 
    * @since vcloud api 0.9
    */
   @Nullable
   public Integer getDefaultLeaseTime() {
      return defaultLeaseTime;
   }

   /**
    * maximum duration of a DHCP address lease.
    * 
    * @since vcloud api 0.9
    */
   @Nullable
   public Integer getMaxLeaseTime() {
      return maxLeaseTime;
   }

   /**
    * @return range of IP addresses available to DHCP clients
    * 
    * @since vcloud api 0.9
    */
   @Nullable
   public IpRange getIpRange() {
      return ipRange;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      DhcpService that = DhcpService.class.cast(o);
      return equal(this.enabled, that.enabled) && equal(this.defaultLeaseTime, that.defaultLeaseTime)
            && equal(this.maxLeaseTime, that.maxLeaseTime) && equal(this.ipRange, that.ipRange);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(enabled, defaultLeaseTime, maxLeaseTime, ipRange);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").omitNullValues().add("enabled", enabled)
            .add("defaultLeaseTime", defaultLeaseTime).add("maxLeaseTime", maxLeaseTime).add("ipRange", ipRange)
            .toString();
   }
}
