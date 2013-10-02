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
import java.net.URI;
import java.util.List;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

/**
 * Class Network
 *
 * @author Adrian Cole
 */
public class Network {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromNetwork(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected String account;
      protected String broadcastDomainType;
      protected URI broadcastURI;
      protected String displayText;
      protected String DNS1;
      protected String DNS2;
      protected String domain;
      protected String domainId;
      protected String endIP;
      protected String gateway;
      protected boolean isDefault;
      protected boolean isShared;
      protected boolean isSystem;
      protected String netmask;
      protected String networkDomain;
      protected String networkOfferingAvailability;
      protected String networkOfferingDisplayText;
      protected String networkOfferingId;
      protected String networkOfferingName;
      protected String related;
      protected String startIP;
      protected String name;
      protected String state;
      protected GuestIPType guestIPType;
      protected String VLAN;
      protected TrafficType trafficType;
      protected String zoneId;
      protected ImmutableSet.Builder<String> tags = ImmutableSet.<String>builder();
      protected boolean securityGroupEnabled;
      protected Set<? extends NetworkService> services = ImmutableSortedSet.of();

      /**
       * @see Network#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see Network#getAccount()
       */
      public T account(String account) {
         this.account = account;
         return self();
      }

      /**
       * @see Network#getBroadcastDomainType()
       */
      public T broadcastDomainType(String broadcastDomainType) {
         this.broadcastDomainType = broadcastDomainType;
         return self();
      }

      /**
       * @see Network#getBroadcastURI()
       */
      public T broadcastURI(URI broadcastURI) {
         this.broadcastURI = broadcastURI;
         return self();
      }

      /**
       * @see Network#getDisplayText()
       */
      public T displayText(String displayText) {
         this.displayText = displayText;
         return self();
      }

      /**
       * @return the DNS for the Network
       */
      public T DNS(List<String> DNS) {
         if (!DNS.isEmpty()) this.DNS1 = DNS.get(0);
         if (DNS.size() > 1) this.DNS2 = DNS.get(1);
         return self();
      }

      /**
       * @see Network#getDomain()
       */
      public T domain(String domain) {
         this.domain = domain;
         return self();
      }

      /**
       * @see Network#getDomainId()
       */
      public T domainId(String domainId) {
         this.domainId = domainId;
         return self();
      }

      /**
       * @see Network#getEndIP()
       */
      public T endIP(String endIP) {
         this.endIP = endIP;
         return self();
      }

      /**
       * @see Network#getGateway()
       */
      public T gateway(String gateway) {
         this.gateway = gateway;
         return self();
      }

      /**
       * @see Network#isDefault()
       */
      public T isDefault(boolean isDefault) {
         this.isDefault = isDefault;
         return self();
      }

      /**
       * @see Network#isShared()
       */
      public T isShared(boolean isShared) {
         this.isShared = isShared;
         return self();
      }

      /**
       * @see Network#isSystem()
       */
      public T isSystem(boolean isSystem) {
         this.isSystem = isSystem;
         return self();
      }

      /**
       * @see Network#getNetmask()
       */
      public T netmask(String netmask) {
         this.netmask = netmask;
         return self();
      }

      /**
       * @see Network#getNetworkDomain()
       */
      public T networkDomain(String networkDomain) {
         this.networkDomain = networkDomain;
         return self();
      }

      /**
       * @see Network#getNetworkOfferingAvailability()
       */
      public T networkOfferingAvailability(String networkOfferingAvailability) {
         this.networkOfferingAvailability = networkOfferingAvailability;
         return self();
      }

      /**
       * @see Network#getNetworkOfferingDisplayText()
       */
      public T networkOfferingDisplayText(String networkOfferingDisplayText) {
         this.networkOfferingDisplayText = networkOfferingDisplayText;
         return self();
      }

      /**
       * @see Network#getNetworkOfferingId()
       */
      public T networkOfferingId(String networkOfferingId) {
         this.networkOfferingId = networkOfferingId;
         return self();
      }

      /**
       * @see Network#getNetworkOfferingName()
       */
      public T networkOfferingName(String networkOfferingName) {
         this.networkOfferingName = networkOfferingName;
         return self();
      }

      /**
       * @see Network#getRelated()
       */
      public T related(String related) {
         this.related = related;
         return self();
      }

      /**
       * @see Network#getStartIP()
       */
      public T startIP(String startIP) {
         this.startIP = startIP;
         return self();
      }

      /**
       * @see Network#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see Network#getState()
       */
      public T state(String state) {
         this.state = state;
         return self();
      }

      /**
       * @see Network#getGuestIPType()
       */
      public T guestIPType(GuestIPType guestIPType) {
         this.guestIPType = guestIPType;
         return self();
      }

      /**
       * @see Network#getVLAN()
       */
      public T VLAN(String VLAN) {
         this.VLAN = VLAN;
         return self();
      }

      /**
       * @see Network#getTrafficType()
       */
      public T trafficType(TrafficType trafficType) {
         this.trafficType = trafficType;
         return self();
      }

      /**
       * @see Network#getZoneId()
       */
      public T zoneId(String zoneId) {
         this.zoneId = zoneId;
         return self();
      }

      /**
       * @see Network#getTags()
       */
      public T tags(Iterable<String> tags) {
         this.tags = ImmutableSet.<String>builder().addAll(tags);
         return self();
      }
      
      /**
       * @see Network#getTags()
       */
      public T tag(String tag) {
         this.tags.add(tag);
         return self();
      }
      

      /**
       * @see Network#isSecurityGroupEnabled()
       */
      public T securityGroupEnabled(boolean securityGroupEnabled) {
         this.securityGroupEnabled = securityGroupEnabled;
         return self();
      }

      /**
       * @see Network#getServices()
       */
      public T services(Set<? extends NetworkService> services) {
         this.services = services;
         return self();
      }

      public Network build() {
         return new Network(id, account, broadcastDomainType, broadcastURI, displayText, DNS1, DNS2, domain, domainId, endIP, gateway, isDefault, isShared, isSystem, netmask, networkDomain, networkOfferingAvailability, networkOfferingDisplayText, networkOfferingId, networkOfferingName, related, startIP, name, state, guestIPType, VLAN, trafficType, zoneId, tags.build(), securityGroupEnabled, services);
      }

      public T fromNetwork(Network in) {
         return this
               .id(in.getId())
               .account(in.getAccount())
               .broadcastDomainType(in.getBroadcastDomainType())
               .broadcastURI(in.getBroadcastURI())
               .displayText(in.getDisplayText())
               .DNS(in.getDNS())
               .domain(in.getDomain())
               .domainId(in.getDomainId())
               .endIP(in.getEndIP())
               .gateway(in.getGateway())
               .isDefault(in.isDefault())
               .isShared(in.isShared())
               .isSystem(in.isSystem())
               .netmask(in.getNetmask())
               .networkDomain(in.getNetworkDomain())
               .networkOfferingAvailability(in.getNetworkOfferingAvailability())
               .networkOfferingDisplayText(in.getNetworkOfferingDisplayText())
               .networkOfferingId(in.getNetworkOfferingId())
               .networkOfferingName(in.getNetworkOfferingName())
               .related(in.getRelated())
               .startIP(in.getStartIP())
               .name(in.getName())
               .state(in.getState())
               .guestIPType(in.getGuestIPType())
               .VLAN(in.getVLAN())
               .trafficType(in.getTrafficType())
               .zoneId(in.getZoneId())
               .tags(in.getTags())
               .securityGroupEnabled(in.isSecurityGroupEnabled())
               .services(in.getServices());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final String account;
   private final String broadcastDomainType;
   private final URI broadcastURI;
   private final String displayText;
   private final String DNS1;
   private final String DNS2;
   private final String domain;
   private final String domainId;
   private final String endIP;
   private final String gateway;
   private final boolean isDefault;
   private final boolean isShared;
   private final boolean isSystem;
   private final String netmask;
   private final String networkDomain;
   private final String networkOfferingAvailability;
   private final String networkOfferingDisplayText;
   private final String networkOfferingId;
   private final String networkOfferingName;
   private final String related;
   private final String startIP;
   private final String name;
   private final String state;
   private final GuestIPType guestIPType;
   private final String VLAN;
   private final TrafficType trafficType;
   private final String zoneId;
   private final Set<String> tags;
   private final boolean securityGroupEnabled;
   private final Set<? extends NetworkService> services;

   @ConstructorProperties({
         "id", "account", "broadcastdomaintype", "broadcasturi", "displaytext", "dns1", "dns2", "domain", "domainid", "endip", "gateway", "isdefault", "isshared", "issystem", "netmask", "networkdomain", "networkofferingavailability", "networkofferingdisplaytext", "networkofferingid", "networkofferingname", "related", "startip", "name", "state", "type", "vlan", "traffictype", "zoneid", "tags", "securitygroupenabled", "service"
   })
   protected Network(String id, @Nullable String account, @Nullable String broadcastDomainType, @Nullable URI broadcastURI,
                     @Nullable String displayText, @Nullable String DNS1, @Nullable String DNS2, @Nullable String domain, @Nullable String domainId,
                     @Nullable String endIP, @Nullable String gateway, boolean isDefault, boolean isShared, boolean isSystem,
                     @Nullable String netmask, @Nullable String networkDomain, @Nullable String networkOfferingAvailability,
                     @Nullable String networkOfferingDisplayText, @Nullable String networkOfferingId, @Nullable String networkOfferingName,
                     @Nullable String related, @Nullable String startIP, @Nullable String name, @Nullable String state,
                     @Nullable GuestIPType guestIPType, @Nullable String VLAN, @Nullable TrafficType trafficType,
                     @Nullable String zoneId, @Nullable Iterable<String> tags, boolean securityGroupEnabled, Set<? extends NetworkService> services) {
      this.id = checkNotNull(id, "id");
      this.account = account;
      this.broadcastDomainType = broadcastDomainType;
      this.broadcastURI = broadcastURI;
      this.displayText = displayText;
      this.DNS1 = DNS1;
      this.DNS2 = DNS2;
      this.domain = domain;
      this.domainId = domainId;
      this.endIP = endIP;
      this.gateway = gateway;
      this.isDefault = isDefault;
      this.isShared = isShared;
      this.isSystem = isSystem;
      this.netmask = netmask;
      this.networkDomain = networkDomain;
      this.networkOfferingAvailability = networkOfferingAvailability;
      this.networkOfferingDisplayText = networkOfferingDisplayText;
      this.networkOfferingId = networkOfferingId;
      this.networkOfferingName = networkOfferingName;
      this.related = related;
      this.startIP = startIP;
      this.name = name;
      this.state = state;
      this.guestIPType = guestIPType;
      this.VLAN = VLAN;
      this.trafficType = trafficType;
      this.zoneId = zoneId;
      this.tags = tags != null ? ImmutableSet.copyOf(tags) : ImmutableSet.<String> of();
      this.securityGroupEnabled = securityGroupEnabled;
      this.services = ImmutableSortedSet.copyOf(services);
   }

   /**
    * @return network id
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return the account associated with the network
    */
   @Nullable
   public String getAccount() {
      return this.account;
   }

   /**
    * @return Broadcast domain type of the network
    */
   @Nullable
   public String getBroadcastDomainType() {
      return this.broadcastDomainType;
   }

   /**
    * @return broadcast uri of the network
    */
   @Nullable
   public URI getBroadcastURI() {
      return this.broadcastURI;
   }

   /**
    * @return the display text of the zone
    */
   @Nullable
   public String getDisplayText() {
      return this.displayText;
   }

   public List<String> getDNS() {
      ImmutableList.Builder<String> builder = ImmutableList.builder();
      if (DNS1 != null && !"".equals(DNS1))
         builder.add(DNS1);
      if (DNS2 != null && !"".equals(DNS2))
         builder.add(DNS2);
      return builder.build();
   }

   /**
    * @return Domain name for the Network
    */
   @Nullable
   public String getDomain() {
      return this.domain;
   }

   /**
    * @return the domain id of the Network
    */
   @Nullable
   public String getDomainId() {
      return this.domainId;
   }

   /**
    * @return the end ip of the network
    */
   @Nullable
   public String getEndIP() {
      return this.endIP;
   }

   /**
    * @return the network's gateway
    */
   @Nullable
   public String getGateway() {
      return this.gateway;
   }

   /**
    * @return true if network offering is default, false otherwise
    */
   public boolean isDefault() {
      return this.isDefault;
   }

   /**
    * @return true if network offering is shared, false otherwise
    */
   public boolean isShared() {
      return this.isShared;
   }

   /**
    * @return true if network offering is system, false otherwise
    */
   public boolean isSystem() {
      return this.isSystem;
   }

   /**
    * @return the network's netmask
    */
   @Nullable
   public String getNetmask() {
      return this.netmask;
   }

   /**
    * @return the network domain
    */
   @Nullable
   public String getNetworkDomain() {
      return this.networkDomain;
   }

   /**
    * @return availability of the network offering the network is created from
    */
   @Nullable
   public String getNetworkOfferingAvailability() {
      return this.networkOfferingAvailability;
   }

   /**
    * @return display text of the network offering the network is created from
    */
   @Nullable
   public String getNetworkOfferingDisplayText() {
      return this.networkOfferingDisplayText;
   }

   /**
    * @return network offering id the network is created from
    */
   @Nullable
   public String getNetworkOfferingId() {
      return this.networkOfferingId;
   }

   /**
    * @return name of the network offering the network is created from
    */
   @Nullable
   public String getNetworkOfferingName() {
      return this.networkOfferingName;
   }

   /**
    * @return related to what other network configuration
    */
   @Nullable
   public String getRelated() {
      return this.related;
   }

   /**
    * @return the start ip of the network
    */
   @Nullable
   public String getStartIP() {
      return this.startIP;
   }

   /**
    * @return network name
    */
   @Nullable
   public String getName() {
      return this.name;
   }

   /**
    * @return state of the network
    */
   @Nullable
   public String getState() {
      return this.state;
   }

   /**
    * @return the GuestIPType of the network
    */
   public GuestIPType getGuestIPType() {
      return this.guestIPType;
   }

   /**
    * @return the vlan range of the zone
    */
   @Nullable
   public String getVLAN() {
      return this.VLAN;
   }

   /**
    * @return the traffic type for this network offering
    */
   @Nullable
   public TrafficType getTrafficType() {
      return this.trafficType;
   }

   /**
    * @return zone id of the network
    */
   @Nullable
   public String getZoneId() {
      return this.zoneId;
   }

   /**
    * @return the tags for the Network
    */
   public Set<String> getTags() {
      return this.tags;
   }

   /**
    * @return true if security group is enabled, false otherwise
    */
   public boolean isSecurityGroupEnabled() {
      return this.securityGroupEnabled;
   }

   /**
    * @return the list of services
    */
   public Set<? extends NetworkService> getServices() {
      return this.services;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, account, broadcastDomainType, broadcastURI, displayText, DNS1, DNS2, domain, domainId, endIP, gateway, isDefault, isShared, isSystem, netmask, networkDomain, networkOfferingAvailability, networkOfferingDisplayText, networkOfferingId, networkOfferingName, related, startIP, name, state, guestIPType, VLAN, trafficType, zoneId, tags, securityGroupEnabled, services);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Network that = Network.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.account, that.account)
            && Objects.equal(this.broadcastDomainType, that.broadcastDomainType)
            && Objects.equal(this.broadcastURI, that.broadcastURI)
            && Objects.equal(this.displayText, that.displayText)
            && Objects.equal(this.DNS1, that.DNS1)
            && Objects.equal(this.DNS2, that.DNS2)
            && Objects.equal(this.domain, that.domain)
            && Objects.equal(this.domainId, that.domainId)
            && Objects.equal(this.endIP, that.endIP)
            && Objects.equal(this.gateway, that.gateway)
            && Objects.equal(this.isDefault, that.isDefault)
            && Objects.equal(this.isShared, that.isShared)
            && Objects.equal(this.isSystem, that.isSystem)
            && Objects.equal(this.netmask, that.netmask)
            && Objects.equal(this.networkDomain, that.networkDomain)
            && Objects.equal(this.networkOfferingAvailability, that.networkOfferingAvailability)
            && Objects.equal(this.networkOfferingDisplayText, that.networkOfferingDisplayText)
            && Objects.equal(this.networkOfferingId, that.networkOfferingId)
            && Objects.equal(this.networkOfferingName, that.networkOfferingName)
            && Objects.equal(this.related, that.related)
            && Objects.equal(this.startIP, that.startIP)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.state, that.state)
            && Objects.equal(this.guestIPType, that.guestIPType)
            && Objects.equal(this.VLAN, that.VLAN)
            && Objects.equal(this.trafficType, that.trafficType)
            && Objects.equal(this.zoneId, that.zoneId)
            && Objects.equal(this.tags, that.tags)
            && Objects.equal(this.securityGroupEnabled, that.securityGroupEnabled)
            && Objects.equal(this.services, that.services);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("account", account).add("broadcastDomainType", broadcastDomainType).add("broadcastURI", broadcastURI)
            .add("displayText", displayText).add("DNS1", DNS1).add("DNS2", DNS2).add("domain", domain).add("domainId", domainId)
            .add("endIP", endIP).add("gateway", gateway).add("isDefault", isDefault).add("isShared", isShared).add("isSystem", isSystem)
            .add("netmask", netmask).add("networkDomain", networkDomain).add("networkOfferingAvailability", networkOfferingAvailability)
            .add("networkOfferingDisplayText", networkOfferingDisplayText).add("networkOfferingId", networkOfferingId)
            .add("networkOfferingName", networkOfferingName).add("related", related).add("startIP", startIP).add("name", name)
            .add("state", state).add("guestIPType", guestIPType).add("VLAN", VLAN).add("trafficType", trafficType)
            .add("zoneId", zoneId).add("tags", tags).add("securityGroupEnabled", securityGroupEnabled).add("services", services);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
