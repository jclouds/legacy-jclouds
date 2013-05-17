/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudstack.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents the data object used in CloudStack's "Vlan" API.
 *
 * @author Richard Downer
 */
public class VlanIPRange implements Comparable<VlanIPRange> {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromVlanIPRange(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected String description;
      protected boolean forVirtualNetwork;
      protected String zoneId;
      protected String vlan;
      protected String account;
      protected String domainId;
      protected String domain;
      protected String podId;
      protected String podName;
      protected String gateway;
      protected String netmask;
      protected String startIP;
      protected String endIP;
      protected String networkId;

      /**
       * @see VlanIPRange#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see VlanIPRange#getDescription()
       */
      public T description(String description) {
         this.description = description;
         return self();
      }

      /**
       * @see VlanIPRange#isForVirtualNetwork()
       */
      public T forVirtualNetwork(boolean forVirtualNetwork) {
         this.forVirtualNetwork = forVirtualNetwork;
         return self();
      }

      /**
       * @see VlanIPRange#getZoneId()
       */
      public T zoneId(String zoneId) {
         this.zoneId = zoneId;
         return self();
      }

      /**
       * @see VlanIPRange#getVlan()
       */
      public T vlan(String vlan) {
         this.vlan = vlan;
         return self();
      }

      /**
       * @see VlanIPRange#getAccount()
       */
      public T account(String account) {
         this.account = account;
         return self();
      }

      /**
       * @see VlanIPRange#getDomainId()
       */
      public T domainId(String domainId) {
         this.domainId = domainId;
         return self();
      }

      /**
       * @see VlanIPRange#getDomain()
       */
      public T domain(String domain) {
         this.domain = domain;
         return self();
      }

      /**
       * @see VlanIPRange#getPodId()
       */
      public T podId(String podId) {
         this.podId = podId;
         return self();
      }

      /**
       * @see VlanIPRange#getPodName()
       */
      public T podName(String podName) {
         this.podName = podName;
         return self();
      }

      /**
       * @see VlanIPRange#getGateway()
       */
      public T gateway(String gateway) {
         this.gateway = gateway;
         return self();
      }

      /**
       * @see VlanIPRange#getNetmask()
       */
      public T netmask(String netmask) {
         this.netmask = netmask;
         return self();
      }

      /**
       * @see VlanIPRange#getStartIP()
       */
      public T startIP(String startIP) {
         this.startIP = startIP;
         return self();
      }

      /**
       * @see VlanIPRange#getEndIP()
       */
      public T endIP(String endIP) {
         this.endIP = endIP;
         return self();
      }

      /**
       * @see VlanIPRange#getNetworkId()
       */
      public T networkId(String networkId) {
         this.networkId = networkId;
         return self();
      }

      public VlanIPRange build() {
         return new VlanIPRange(id, description, forVirtualNetwork, zoneId, vlan, account, domainId, domain, podId,
               podName, gateway, netmask, startIP, endIP, networkId);
      }

      public T fromVlanIPRange(VlanIPRange in) {
         return this
               .id(in.getId())
               .description(in.getDescription())
               .forVirtualNetwork(in.isForVirtualNetwork())
               .zoneId(in.getZoneId())
               .vlan(in.getVlan())
               .account(in.getAccount())
               .domainId(in.getDomainId())
               .domain(in.getDomain())
               .podId(in.getPodId())
               .podName(in.getPodName())
               .gateway(in.getGateway())
               .netmask(in.getNetmask())
               .startIP(in.getStartIP())
               .endIP(in.getEndIP())
               .networkId(in.getNetworkId());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final String description;
   private final boolean forVirtualNetwork;
   private final String zoneId;
   private final String vlan;
   private final String account;
   private final String domainId;
   private final String domain;
   private final String podId;
   private final String podName;
   private final String gateway;
   private final String netmask;
   private final String startIP;
   private final String endIP;
   private final String networkId;

   @ConstructorProperties({
         "id", "description", "forvirtualnetwork", "zoneid", "vlan", "account", "domainid", "domain", "podid", "podname",
         "gateway", "netmask", "startip", "endip", "networkid"
   })
   protected VlanIPRange(String id, @Nullable String description, boolean forVirtualNetwork, @Nullable String zoneId,
                         @Nullable String vlan, @Nullable String account, @Nullable String domainId, @Nullable String domain,
                         @Nullable String podId, @Nullable String podName, @Nullable String gateway, @Nullable String netmask,
                         @Nullable String startIP, @Nullable String endIP, @Nullable String networkId) {
      this.id = checkNotNull(id, "id");
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
      return this.id;
   }

   @Nullable
   public String getDescription() {
      return this.description;
   }

   public boolean isForVirtualNetwork() {
      return this.forVirtualNetwork;
   }

   @Nullable
   public String getZoneId() {
      return this.zoneId;
   }

   @Nullable
   public String getVlan() {
      return this.vlan;
   }

   @Nullable
   public String getAccount() {
      return this.account;
   }

   @Nullable
   public String getDomainId() {
      return this.domainId;
   }

   @Nullable
   public String getDomain() {
      return this.domain;
   }

   @Nullable
   public String getPodId() {
      return this.podId;
   }

   @Nullable
   public String getPodName() {
      return this.podName;
   }

   @Nullable
   public String getGateway() {
      return this.gateway;
   }

   @Nullable
   public String getNetmask() {
      return this.netmask;
   }

   @Nullable
   public String getStartIP() {
      return this.startIP;
   }

   @Nullable
   public String getEndIP() {
      return this.endIP;
   }

   @Nullable
   public String getNetworkId() {
      return this.networkId;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, description, forVirtualNetwork, zoneId, vlan, account, domainId, domain, podId, podName, gateway, netmask, startIP, endIP, networkId);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      VlanIPRange that = VlanIPRange.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.description, that.description)
            && Objects.equal(this.forVirtualNetwork, that.forVirtualNetwork)
            && Objects.equal(this.zoneId, that.zoneId)
            && Objects.equal(this.vlan, that.vlan)
            && Objects.equal(this.account, that.account)
            && Objects.equal(this.domainId, that.domainId)
            && Objects.equal(this.domain, that.domain)
            && Objects.equal(this.podId, that.podId)
            && Objects.equal(this.podName, that.podName)
            && Objects.equal(this.gateway, that.gateway)
            && Objects.equal(this.netmask, that.netmask)
            && Objects.equal(this.startIP, that.startIP)
            && Objects.equal(this.endIP, that.endIP)
            && Objects.equal(this.networkId, that.networkId);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("description", description).add("forVirtualNetwork", forVirtualNetwork).add("zoneId", zoneId)
            .add("vlan", vlan).add("account", account).add("domainId", domainId).add("domain", domain).add("podId", podId)
            .add("podName", podName).add("gateway", gateway).add("netmask", netmask).add("startIP", startIP).add("endIP", endIP)
            .add("networkId", networkId);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(VlanIPRange other) {
      return this.id.compareTo(other.id);
   }

}
