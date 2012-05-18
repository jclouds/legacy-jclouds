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

import com.google.common.base.Objects;
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

      private String id;
      private String description;
      private boolean forVirtualNetwork;
      private String zoneId;
      private String vlan;
      private String account;
      private String domainId;
      private String domain;
      private String podId;
      private String podName;
      private String gateway;
      private String netmask;
      private String startIP;
      private String endIP;
      private String networkId;

      public Builder id(String id) {
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

      public Builder zoneId(String zoneId) {
         this.zoneId = zoneId;
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

      public Builder domainId(String domainId) {
         this.domainId = domainId;
         return this;
      }

      public Builder domain(String domain) {
         this.domain = domain;
         return this;
      }

      public Builder podId(String podId) {
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

      public Builder networkId(String networkId) {
         this.networkId = networkId;
         return this;
      }
      
      public VlanIPRange build() {
         return new VlanIPRange(id, description, forVirtualNetwork, zoneId, vlan, account, domainId, domain, podId, podName, gateway, netmask, startIP, endIP, networkId);
      }
   }
   
   private String id;
   private String description;
   @SerializedName("forvirtualnetwork") private boolean forVirtualNetwork;
   @SerializedName("zoneid") private String zoneId;
   private String vlan;
   private String account;
   @SerializedName("domainid") private String domainId;
   private String domain;
   @SerializedName("podid") private String podId;
   @SerializedName("podname") private String podName;
   private String gateway;
   private String netmask;
   @SerializedName("startip") private String startIP;
   @SerializedName("endip") private String endIP;
   @SerializedName("networkid") private String networkId;

   /* just for the deserializer */
   VlanIPRange() {}
   
   public VlanIPRange(String id, String description, boolean forVirtualNetwork, String zoneId, String vlan, String account, String domainId, String domain, String podId, String podName, String gateway, String netmask, String startIP, String endIP, String networkId) {
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

   public String getId() {
      return id;
   }

   public String getDescription() {
      return description;
   }

   public boolean isForVirtualNetwork() {
      return forVirtualNetwork;
   }

   public String getZoneId() {
      return zoneId;
   }

   public String getVlan() {
      return vlan;
   }

   public String getAccount() {
      return account;
   }

   public String getDomainId() {
      return domainId;
   }

   public String getDomain() {
      return domain;
   }

   public String getPodId() {
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

   public String getNetworkId() {
      return networkId;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      VlanIPRange that = (VlanIPRange) o;

      if (!Objects.equal(domainId, that.domainId)) return false;
      if (!Objects.equal(forVirtualNetwork, that.forVirtualNetwork)) return false;
      if (!Objects.equal(id, that.id)) return false;
      if (!Objects.equal(networkId, that.networkId)) return false;
      if (!Objects.equal(podId, that.podId)) return false;
      if (!Objects.equal(zoneId, that.zoneId)) return false;
      if (!Objects.equal(account, that.account)) return false;
      if (!Objects.equal(description, that.description)) return false;
      if (!Objects.equal(domain, that.domain)) return false;
      if (!Objects.equal(endIP, that.endIP)) return false;
      if (!Objects.equal(gateway, that.gateway)) return false;
      if (!Objects.equal(netmask, that.netmask)) return false;
      if (!Objects.equal(podName, that.podName)) return false;
      if (!Objects.equal(startIP, that.startIP)) return false;
      if (!Objects.equal(vlan, that.vlan)) return false;

      return true;
   }

   @Override
   public int hashCode() {
       return Objects.hashCode(domainId, forVirtualNetwork, id, networkId, podId,
                               zoneId, account, description, domain, endIP, gateway,
                               netmask, podName, startIP, vlan);
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
      return this.id.compareTo(other.id);
   }
}
