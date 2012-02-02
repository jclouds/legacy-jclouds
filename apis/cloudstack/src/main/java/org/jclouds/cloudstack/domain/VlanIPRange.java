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
package org.jclouds.cloudstack.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Represents the data object used in CloudStack's "Vlan" API.
 *
 * @author Richard Downer
 */
public class VlanIPRange implements Comparable<VlanIPRange> {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private long id;
      private String description;
      private boolean forVirtualNetwork;
      private long zoneId;
      private String vlan;
      private String account;
      private long domainId;
      private String domain;
      private long podId;
      private String podName;
      private String gateway;
      private String netmask;
      private String startIP;
      private String endIP;
      private long networkId;

      public Builder id(long id) {
         this.id = id;
         return this;
      }

      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public Builder forVirtualNetwork(boolean forVirtualNetwork) {
         this.forVirtualNetwork = forVirtualNetwork;
         return this;
      }

      public Builder zoneId(long zoneId) {
         this.zoneId = zoneId;
         return this;
      }

      public Builder vlan(long vlan) {
         this.vlan = vlan+"";
         return this;
      }

      public Builder vlan(String vlan) {
         this.vlan = vlan;
         return this;
      }

      public Builder account(String account) {
         this.account = account;
         return this;
      }

      public Builder domainId(long domainId) {
         this.domainId = domainId;
         return this;
      }

      public Builder domain(String domain) {
         this.domain = domain;
         return this;
      }

      public Builder podId(long podId) {
         this.podId = podId;
         return this;
      }

      public Builder podName(String podName) {
         this.podName = podName;
         return this;
      }

      public Builder gateway(String gateway) {
         this.gateway = gateway;
         return this;
      }

      public Builder netmask(String netmask) {
         this.netmask = netmask;
         return this;
      }

      public Builder startIP(String startIP) {
         this.startIP = startIP;
         return this;
      }

      public Builder endIP(String endIP) {
         this.endIP = endIP;
         return this;
      }

      public Builder networkId(long networkId) {
         this.networkId = networkId;
         return this;
      }
      
      public VlanIPRange build() {
         return new VlanIPRange(id, description, forVirtualNetwork, zoneId, vlan, account, domainId, domain, podId, podName, gateway, netmask, startIP, endIP, networkId);
      }
   }
   
   private long id;
   private String description;
   @SerializedName("forvirtualnetwork") private boolean forVirtualNetwork;
   @SerializedName("zoneid") private long zoneId;
   private String vlan;
   private String account;
   @SerializedName("domainid") private long domainId;
   private String domain;
   @SerializedName("podid") private long podId;
   @SerializedName("podname") private String podName;
   private String gateway;
   private String netmask;
   @SerializedName("startip") private String startIP;
   @SerializedName("endip") private String endIP;
   @SerializedName("networkid") private long networkId;

   /* just for the deserializer */
   VlanIPRange() {}
   
   public VlanIPRange(long id, String description, boolean forVirtualNetwork, long zoneId, String vlan, String account, long domainId, String domain, long podId, String podName, String gateway, String netmask, String startIP, String endIP, long networkId) {
      this.id = id;
      this.description = description;
      this.forVirtualNetwork = forVirtualNetwork;
      this.zoneId = zoneId;
      this.vlan = vlan;
      this.account = account;
      this.domainId = domainId;
      this.domain = domain;
      this.podId = podId;
      this.podName = podName;
      this.gateway = gateway;
      this.netmask = netmask;
      this.startIP = startIP;
      this.endIP = endIP;
      this.networkId = networkId;
   }

   public long getId() {
      return id;
   }

   public String getDescription() {
      return description;
   }

   public boolean isForVirtualNetwork() {
      return forVirtualNetwork;
   }

   public long getZoneId() {
      return zoneId;
   }

   public String getVlan() {
      return vlan;
   }

   public String getAccount() {
      return account;
   }

   public long getDomainId() {
      return domainId;
   }

   public String getDomain() {
      return domain;
   }

   public long getPodId() {
      return podId;
   }

   public String getPodName() {
      return podName;
   }

   public String getGateway() {
      return gateway;
   }

   public String getNetmask() {
      return netmask;
   }

   public String getStartIP() {
      return startIP;
   }

   public String getEndIP() {
      return endIP;
   }

   public long getNetworkId() {
      return networkId;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      VlanIPRange that = (VlanIPRange) o;

      if (domainId != that.domainId) return false;
      if (forVirtualNetwork != that.forVirtualNetwork) return false;
      if (id != that.id) return false;
      if (networkId != that.networkId) return false;
      if (podId != that.podId) return false;
      if (zoneId != that.zoneId) return false;
      if (account != null ? !account.equals(that.account) : that.account != null) return false;
      if (description != null ? !description.equals(that.description) : that.description != null) return false;
      if (domain != null ? !domain.equals(that.domain) : that.domain != null) return false;
      if (endIP != null ? !endIP.equals(that.endIP) : that.endIP != null) return false;
      if (gateway != null ? !gateway.equals(that.gateway) : that.gateway != null) return false;
      if (netmask != null ? !netmask.equals(that.netmask) : that.netmask != null) return false;
      if (podName != null ? !podName.equals(that.podName) : that.podName != null) return false;
      if (startIP != null ? !startIP.equals(that.startIP) : that.startIP != null) return false;
      if (vlan != null ? !vlan.equals(that.vlan) : that.vlan != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = (int) (id ^ (id >>> 32));
      result = 31 * result + (description != null ? description.hashCode() : 0);
      result = 31 * result + (forVirtualNetwork ? 1 : 0);
      result = 31 * result + (int) (zoneId ^ (zoneId >>> 32));
      result = 31 * result + (vlan != null ? vlan.hashCode() : 0);
      result = 31 * result + (account != null ? account.hashCode() : 0);
      result = 31 * result + (int) (domainId ^ (domainId >>> 32));
      result = 31 * result + (domain != null ? domain.hashCode() : 0);
      result = 31 * result + (int) (podId ^ (podId >>> 32));
      result = 31 * result + (podName != null ? podName.hashCode() : 0);
      result = 31 * result + (gateway != null ? gateway.hashCode() : 0);
      result = 31 * result + (netmask != null ? netmask.hashCode() : 0);
      result = 31 * result + (startIP != null ? startIP.hashCode() : 0);
      result = 31 * result + (endIP != null ? endIP.hashCode() : 0);
      result = 31 * result + (int) (networkId ^ (networkId >>> 32));
      return result;
   }

   @Override
   public String toString() {
      return "VlanIPRange{" +
         "id=" + id +
         ", description='" + description + '\'' +
         ", forVirtualNetwork=" + forVirtualNetwork +
         ", zoneId=" + zoneId +
         ", vlan='" + vlan + '\'' +
         ", account='" + account + '\'' +
         ", domainId=" + domainId +
         ", domain='" + domain + '\'' +
         ", podId=" + podId +
         ", podName='" + podName + '\'' +
         ", gateway='" + gateway + '\'' +
         ", netmask='" + netmask + '\'' +
         ", startIP='" + startIP + '\'' +
         ", endIP='" + endIP + '\'' +
         ", networkId=" + networkId +
         '}';
   }

   @Override
   public int compareTo(VlanIPRange other) {
      return Long.valueOf(this.id).compareTo(other.id);
   }
}
