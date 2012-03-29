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

package org.jclouds.glesys.domain;

import java.util.Arrays;
import java.util.List;

import org.jclouds.javax.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Represents detailed information about an IP address.
 */
public class IpDetails {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      protected String datacenter;
      protected String ipversion;
      protected String ptr;
      protected String platform;
      protected String address;
      protected String netmask;
      protected String broadcast;
      protected String gateway;
      protected List<String> nameservers;

      public Builder datacenter(String datacenter) {
         this.datacenter = datacenter;
         return this;
      }

      public Builder ipversion(String ipversion) {
         this.ipversion = ipversion;
         return this;
      }

      public Builder ptr(String ptr) {
         this.ptr = ptr;
         return this;
      }

      public Builder platform(String platform) {
         this.platform = platform;
         return this;
      }

      public IpDetails build() {
         return new IpDetails(datacenter, ipversion, ptr, platform,
                 address, netmask, broadcast, gateway, nameservers);
      }

      public Builder address(String address) {
         this.address = address;
         return this;
      }

      public Builder netmask(String netmask) {
         this.netmask = netmask;
         return this;
      }

      public Builder broadcast(String broadcast) {
         this.broadcast = broadcast;
         return this;
      }

      public Builder gateway(String gateway) {
         this.gateway = gateway;
         return this;
      }

      public Builder nameServers(String... nameServers) {
         this.nameservers = Arrays.asList(nameServers);
         return this;
      }
   }

   protected String datacenter;
   protected String ipversion;
   @SerializedName("PTR")
   protected String ptr;
   protected String platform;
   protected String address;
   protected String netmask;
   protected String broadcast;
   protected String gateway;
   protected List<String> nameservers;

   public IpDetails(String datacenter, String ipversion, String ptr, String platform,
                    @Nullable String address, @Nullable String netmask,
                    @Nullable String broadcast, @Nullable String gateway,
                    @Nullable List<String> nameservers) {
      this.datacenter = datacenter;
      this.ipversion = ipversion;
      this.ptr = ptr;
      this.platform = platform;
      this.address = address;
      this.netmask = netmask;
      this.broadcast = broadcast;
      this.gateway = gateway;
      this.nameservers = nameservers;
   }

   public String getDatacenter() {
      return datacenter;
   }

   public String getIpversion() {
      return ipversion;
   }

   public String getPtr() {
      return ptr;
   }

   public String getPlatform() {
      return platform;
   }

   public String getAddress() {
      return address;
   }

   public String getNetmask() {
      return netmask;
   }

   public String getBroadcast() {
      return broadcast;
   }

   public String getGateway() {
      return gateway;
   }

   public List<String> getNameServers() {
      return nameservers;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      IpDetails ipDetails = (IpDetails) o;

      if (address != null ? !address.equals(ipDetails.address) : ipDetails.address != null) return false;
      if (broadcast != null ? !broadcast.equals(ipDetails.broadcast) : ipDetails.broadcast != null) return false;
      if (datacenter != null ? !datacenter.equals(ipDetails.datacenter) : ipDetails.datacenter != null) return false;
      if (gateway != null ? !gateway.equals(ipDetails.gateway) : ipDetails.gateway != null) return false;
      if (ipversion != null ? !ipversion.equals(ipDetails.ipversion) : ipDetails.ipversion != null) return false;
      if (netmask != null ? !netmask.equals(ipDetails.netmask) : ipDetails.netmask != null) return false;
      if (platform != null ? !platform.equals(ipDetails.platform) : ipDetails.platform != null) return false;
      if (ptr != null ? !ptr.equals(ipDetails.ptr) : ipDetails.ptr != null) return false;
      if (nameservers != null ? !nameservers.equals(ipDetails.nameservers) : ipDetails.nameservers != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = datacenter != null ? datacenter.hashCode() : 0;
      result = 31 * result + (ipversion != null ? ipversion.hashCode() : 0);
      result = 31 * result + (ptr != null ? ptr.hashCode() : 0);
      result = 31 * result + (platform != null ? platform.hashCode() : 0);
      result = 31 * result + (address != null ? address.hashCode() : 0);
      result = 31 * result + (netmask != null ? netmask.hashCode() : 0);
      result = 31 * result + (broadcast != null ? broadcast.hashCode() : 0);
      result = 31 * result + (gateway != null ? gateway.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return String.format("IpDetails[datacenter=%s, ipversion=%s, platform=%s, PTR=%s, " +
              "address=%s, netmask=%s, broadcast=%s, gateway=%s",
              datacenter, ipversion, platform, ptr, address, netmask, broadcast, gateway);
   }
}
