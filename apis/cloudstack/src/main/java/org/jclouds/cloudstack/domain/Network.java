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

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.annotation.Nullable;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.gson.annotations.SerializedName;

/**
 * @author Adrian Cole
 */
public class Network implements Comparable<Network> {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String id;
      private String broadcastDomainType;
      private URI broadcastURI;
      private String displayText;
      private List<String> DNS = ImmutableList.of();
      private String domain;
      private String endIP;
      private String gateway;
      private boolean isDefault;
      private boolean isShared;
      private boolean isSystem;
      private String netmask;
      private String networkDomain;
      private String networkOfferingAvailability;
      private String networkOfferingDisplayText;
      private String networkOfferingId;
      private String networkOfferingName;
      private String related;
      private String startIP;
      private String name;
      private String state;
      private GuestIPType guestIPType;
      private String VLAN;
      private TrafficType trafficType;
      private String zoneId;
      private Set<? extends NetworkService> services = ImmutableSet.<NetworkService>of();
      private String account;
      private String domainId;
      private boolean securityGroupEnabled;
      private Set<String> tags = ImmutableSet.of();


      public Builder id(String id) {
         this.id = id;
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

      public Builder broadcastDomainType(String broadcastDomainType) {
         this.broadcastDomainType = broadcastDomainType;
         return this;
      }

      public Builder broadcastURI(URI broadcastURI) {
         this.broadcastURI = broadcastURI;
         return this;
      }

      public Builder displayText(String displayText) {
         this.displayText = displayText;
         return this;
      }

      public Builder DNS(List<String> DNS) {
         this.DNS = ImmutableList.copyOf(checkNotNull(DNS, "DNS"));
         return this;
      }

      public Builder domain(String domain) {
         this.domain = domain;
         return this;
      }

      public Builder endIP(String endIP) {
         this.endIP = endIP;
         return this;
      }

      public Builder gateway(String gateway) {
         this.gateway = gateway;
         return this;
      }

      public Builder isDefault(boolean isDefault) {
         this.isDefault = isDefault;
         return this;
      }

      public Builder isShared(boolean isShared) {
         this.isShared = isShared;
         return this;
      }

      public Builder isSystem(boolean isSystem) {
         this.isSystem = isSystem;
         return this;
      }

      public Builder netmask(String netmask) {
         this.netmask = netmask;
         return this;
      }

      public Builder networkDomain(String networkDomain) {
         this.networkDomain = networkDomain;
         return this;
      }

      public Builder networkOfferingAvailability(String networkOfferingAvailability) {
         this.networkOfferingAvailability = networkOfferingAvailability;
         return this;
      }

      public Builder networkOfferingDisplayText(String networkOfferingDisplayText) {
         this.networkOfferingDisplayText = networkOfferingDisplayText;
         return this;
      }

      public Builder networkOfferingId(String networkOfferingId) {
         this.networkOfferingId = networkOfferingId;
         return this;
      }

      public Builder networkOfferingName(String networkOfferingName) {
         this.networkOfferingName = networkOfferingName;
         return this;
      }

      public Builder related(String related) {
         this.related = related;
         return this;
      }

      public Builder startIP(String startIP) {
         this.startIP = startIP;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder state(String state) {
         this.state = state;
         return this;
      }

      public Builder guestIPType(GuestIPType guestIPType) {
         this.guestIPType = guestIPType;
         return this;
      }

      public Builder VLAN(String VLAN) {
         this.VLAN = VLAN;
         return this;
      }

      public Builder trafficType(TrafficType trafficType) {
         this.trafficType = trafficType;
         return this;
      }

      public Builder zoneId(String zoneId) {
         this.zoneId = zoneId;
         return this;
      }

      public Builder services(Set<? extends NetworkService> services) {
         this.services = ImmutableSet.<NetworkService>copyOf(checkNotNull(services, "services"));
         return this;
      }

      public Builder tags(Set<String> tags) {
         this.tags = ImmutableSet.copyOf(checkNotNull(tags, "tags"));
         return this;
      }

      public Builder securityGroupEnabled(boolean securityGroupEnabled) {
         this.securityGroupEnabled = securityGroupEnabled;
         return this;
      }

      public Network build() {
         return new Network(id, broadcastDomainType, broadcastURI, displayText, DNS, domain, domainId, endIP,
               gateway, isDefault, isShared, isSystem, netmask, networkDomain, networkOfferingAvailability,
               networkOfferingDisplayText, networkOfferingId, networkOfferingName, related, startIP, name, state,
               guestIPType, VLAN, trafficType, zoneId, services, tags, securityGroupEnabled, account);
      }
   }

   private String id;
   private String account;
   @SerializedName("broadcastdomaintype")
   private String broadcastDomainType;
   @SerializedName("broadcasturi")
   private URI broadcastURI;
   @SerializedName("displaytext")
   private String displayText;
   @SerializedName("dns1")
   private String DNS1;
   @SerializedName("dns2")
   private String DNS2;
   private String domain;
   @SerializedName("domainid")
   private String domainId;
   @SerializedName("endip")
   private String endIP;
   private String gateway;
   @SerializedName("isdefault")
   private boolean isDefault;
   @SerializedName("isshared")
   private boolean isShared;
   @SerializedName("issystem")
   private boolean isSystem;
   private String netmask;
   @Nullable
   @SerializedName("networkdomain")
   private String networkDomain;
   @SerializedName("networkofferingavailability")
   private String networkOfferingAvailability;
   @SerializedName("networkofferingdisplaytext")
   private String networkOfferingDisplayText;
   @SerializedName("networkofferingid")
   private String networkOfferingId;
   @SerializedName("networkofferingname")
   private String networkOfferingName;
   private String related;
   @SerializedName("startip")
   private String startIP;
   private String name;
   private String state;
   @SerializedName("type")
   private GuestIPType guestIPType;
   @SerializedName("vlan")
   private String VLAN;
   @SerializedName("traffictype")
   private TrafficType trafficType;
   @SerializedName("zoneid")
   private String zoneId;
   private String tags;
   @SerializedName("securitygroupenabled")
   private boolean securityGroupEnabled;
   // so tests and serialization comes out expected
   @SerializedName("service")
   private SortedSet<? extends NetworkService> services = ImmutableSortedSet.<NetworkService>of();

   /**
    * present only for serializer
    */
   Network() {

   }

   public Network(String id, String broadcastDomainType, URI broadcastURI, String displayText,
                  List<String> DNS, String domain, String domainId, String endIP, String gateway, boolean isDefault,
                  boolean isShared, boolean isSystem, String netmask, String networkDomain, String networkOfferingAvailability,
                  String networkOfferingDisplayText, String networkOfferingId, String networkOfferingName, String related,
                  String startIP, String name, String state, GuestIPType type, String vLAN, TrafficType trafficType,
                  String zoneId, Set<? extends NetworkService> services, Set<String> tags, boolean securityGroupEnabled,
                  String account) {
      this.id = id;
      this.broadcastDomainType = broadcastDomainType;
      this.broadcastURI = broadcastURI;
      this.displayText = displayText;
      this.DNS1 = checkNotNull(DNS, "DNS").size() > 0 ? DNS.get(0) : null;
      this.DNS2 = DNS.size() > 1 ? DNS.get(1) : null;
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
      this.guestIPType = type;
      this.VLAN = vLAN;
      this.trafficType = trafficType;
      this.zoneId = zoneId;
      this.services = ImmutableSortedSet.copyOf(checkNotNull(services, "services"));
      this.tags = tags.size() == 0 ? null : Joiner.on(',').join(tags);
      this.securityGroupEnabled = securityGroupEnabled;
      this.account = account;
   }

   /**
    * @return network id
    */
   public String getId() {
      return id;
   }

   /**
    * @return Broadcast domain type of the network
    */
   public String getBroadcastDomainType() {
      return broadcastDomainType;
   }

   /**
    * @return broadcast uri of the network
    */
   public URI getBroadcastURI() {
      return broadcastURI;
   }

   /**
    * @return the display text of the zone
    */
   public String getDisplayText() {
      return displayText;
   }

   /**
    * @return the external DNS for the network
    */
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
   public String getDomain() {
      return domain;
   }

   /**
    * @return the domain id of the Network
    */
   public String getDomainId() {
      return domainId;
   }

   /**
    * @return the account associated with the network
    */
   public String getAccount() {
      return account;
   }

   /**
    * @return the end ip of the network
    */
   public String getEndIP() {
      return endIP;
   }

   /**
    * @return the network's gateway
    */
   public String getGateway() {
      return gateway;
   }

   /**
    * @return true if network offering is default, false otherwise
    */
   public boolean isDefault() {
      return isDefault;
   }

   /**
    * @return true if network offering is shared, false otherwise
    */
   public boolean isShared() {
      return isShared;
   }

   /**
    * @return true if network offering is system, false otherwise
    */
   public boolean isSystem() {
      return isSystem;
   }

   /**
    * @return network name
    */
   public String getName() {
      return name;
   }

   /**
    * @return the GuestIPType of the network
    */
   public GuestIPType getGuestIPType() {
      return guestIPType;
   }

   /**
    * @return state of the network
    */
   public String getState() {
      return state;
   }

   /**
    * @return the vlan range of the zone
    */
   public String getVLAN() {
      return VLAN;
   }

   /**
    * @return the traffic type for this network offering
    */
   public TrafficType getTrafficType() {
      return trafficType;
   }

   /**
    * @return the network's netmask
    */
   public String getNetmask() {
      return netmask;
   }

   /**
    * @return the network domain
    */
   public String getNetworkDomain() {
      return networkDomain;
   }

   /**
    * @return availability of the network offering the network is created from
    */
   public String getNetworkOfferingAvailability() {
      return networkOfferingAvailability;
   }

   /**
    * @return display text of the network offering the network is created from
    */
   public String getNetworkOfferingDisplayText() {
      return networkOfferingDisplayText;
   }

   /**
    * @return network offering id the network is created from
    */
   public String getNetworkOfferingId() {
      return networkOfferingId;
   }

   /**
    * @return name of the network offering the network is created from
    */
   public String getNetworkOfferingName() {
      return networkOfferingName;
   }

   /**
    * @return related to what other network configuration
    */
   public String getRelated() {
      return related;
   }

   /**
    * @return the start ip of the network
    */
   public String getStartIP() {
      return startIP;
   }

   /**
    * @return zone id of the network
    */
   public String getZoneId() {
      return zoneId;
   }

   /**
    * @return the list of services
    */
   public Set<? extends NetworkService> getServices() {
      return services;
   }

   /**
    * @return true if security group is enabled, false otherwise
    */
   public boolean isSecurityGroupEnabled() {
      return securityGroupEnabled;
   }

   /**
    * @return the tags for the Network
    */
   public Set<String> getTags() {
      return tags != null ? ImmutableSet.copyOf(Splitter.on(',').split(tags)) : ImmutableSet.<String>of();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Network that = (Network) o;

      if (!Objects.equal(DNS1, that.DNS1)) return false;
      if (!Objects.equal(DNS2, that.DNS2)) return false;
      if (!Objects.equal(VLAN, that.VLAN)) return false;
      if (!Objects.equal(broadcastDomainType, that.broadcastDomainType)) return false;
      if (!Objects.equal(broadcastURI, that.broadcastURI)) return false;
      if (!Objects.equal(displayText, that.displayText)) return false;
      if (!Objects.equal(domain, that.domain)) return false;
      if (!Objects.equal(endIP, that.endIP)) return false;
      if (!Objects.equal(gateway, that.gateway)) return false;
      if (!Objects.equal(guestIPType, that.guestIPType)) return false;
      if (!Objects.equal(id, that.id)) return false;
      if (!Objects.equal(isDefault, that.isDefault)) return false;
      if (!Objects.equal(isShared, that.isShared)) return false;
      if (!Objects.equal(isSystem, that.isSystem)) return false;
      if (!Objects.equal(name, that.name)) return false;
      if (!Objects.equal(netmask, that.netmask)) return false;
      if (!Objects.equal(networkDomain, that.networkDomain)) return false;
      if (!Objects.equal(networkOfferingAvailability, that.networkOfferingAvailability)) return false;
      if (!Objects.equal(networkOfferingDisplayText, that.networkOfferingDisplayText)) return false;
      if (!Objects.equal(networkOfferingId, that.networkOfferingId)) return false;
      if (!Objects.equal(networkOfferingName, that.networkOfferingName)) return false;
      if (!Objects.equal(related, that.related)) return false;
      if (!Objects.equal(services, that.services)) return false;
      if (!Objects.equal(startIP, that.startIP)) return false;
      if (!Objects.equal(state, that.state)) return false;
      if (!Objects.equal(trafficType, that.trafficType)) return false;
      if (!Objects.equal(zoneId, that.zoneId)) return false;
      if (!Objects.equal(tags, that.tags)) return false;
      if (!Objects.equal(domainId, that.domainId)) return false;

      return true;
   }

   @Override
   public int hashCode() {
       return Objects.hashCode(DNS1, DNS2, VLAN, broadcastDomainType, broadcastURI, displayText, domain,
                               endIP, gateway, guestIPType, id, isDefault, isShared, isSystem, name,
                               netmask, networkDomain, networkOfferingAvailability, networkOfferingDisplayText,
                               networkOfferingId, networkOfferingName, related, services, startIP, state,
                               trafficType, zoneId, tags, domainId);
   }

   @Override
   public String toString() {
      return "Network{" +
            "id=" + id +
            ", account='" + account + '\'' +
            ", broadcastDomainType='" + broadcastDomainType + '\'' +
            ", broadcastURI=" + broadcastURI +
            ", displayText='" + displayText + '\'' +
            ", DNS1='" + DNS1 + '\'' +
            ", DNS2='" + DNS2 + '\'' +
            ", domain='" + domain + '\'' +
            ", domainId=" + domainId +
            ", endIP='" + endIP + '\'' +
            ", gateway='" + gateway + '\'' +
            ", isDefault=" + isDefault +
            ", isShared=" + isShared +
            ", isSystem=" + isSystem +
            ", netmask='" + netmask + '\'' +
            ", networkDomain='" + networkDomain + '\'' +
            ", networkOfferingAvailability='" + networkOfferingAvailability + '\'' +
            ", networkOfferingDisplayText='" + networkOfferingDisplayText + '\'' +
            ", networkOfferingId=" + networkOfferingId +
            ", networkOfferingName='" + networkOfferingName + '\'' +
            ", related=" + related +
            ", startIP='" + startIP + '\'' +
            ", name='" + name + '\'' +
            ", state='" + state + '\'' +
            ", guestIPType=" + guestIPType +
            ", VLAN='" + VLAN + '\'' +
            ", trafficType=" + trafficType +
            ", zoneId=" + zoneId +
            ", tags='" + tags + '\'' +
            ", securityGroupEnabled=" + securityGroupEnabled +
            ", services=" + services +
            '}';
   }

   @Override
   public int compareTo(Network arg0) {
      return id.compareTo(arg0.getId());
   }
}
