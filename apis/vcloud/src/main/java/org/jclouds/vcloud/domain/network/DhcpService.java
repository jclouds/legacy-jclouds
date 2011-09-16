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
    * default duration of a DCHP address lease
    * 
    * @since vcloud api 0.9
    */
   @Nullable
   public Integer getDefaultLeaseTime() {
      return defaultLeaseTime;
   }

   /**
    * maximum duration of a DCHP address lease.
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
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((defaultLeaseTime == null) ? 0 : defaultLeaseTime.hashCode());
      result = prime * result + (enabled ? 1231 : 1237);
      result = prime * result + ((ipRange == null) ? 0 : ipRange.hashCode());
      result = prime * result + ((maxLeaseTime == null) ? 0 : maxLeaseTime.hashCode());
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
      DhcpService other = (DhcpService) obj;
      if (defaultLeaseTime == null) {
         if (other.defaultLeaseTime != null)
            return false;
      } else if (!defaultLeaseTime.equals(other.defaultLeaseTime))
         return false;
      if (enabled != other.enabled)
         return false;
      if (ipRange == null) {
         if (other.ipRange != null)
            return false;
      } else if (!ipRange.equals(other.ipRange))
         return false;
      if (maxLeaseTime == null) {
         if (other.maxLeaseTime != null)
            return false;
      } else if (!maxLeaseTime.equals(other.maxLeaseTime))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[defaultLeaseTime=" + defaultLeaseTime + ", enabled=" + enabled + ", ipRange=" + ipRange
               + ", maxLeaseTime=" + maxLeaseTime + "]";
   }
}