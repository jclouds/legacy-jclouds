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
      private long id;
      private String account;
      private String broadcastDomainType;
      private URI broadcastURI;
      private String displayText;
      private List<String> DNS = ImmutableList.of();
      private String domain;
      private long domainId;
      private String endIP;
      private String gateway;
      private boolean isDefault;
      private boolean isShared;
      private boolean isSystem;
      private String netmask;
      private String networkDomain;
      private String networkOfferingAvailability;
      private String networkOfferingDisplayText;
      private long networkOfferingId;
      private String networkOfferingName;
      private long related;
      private String startIP;
      private String name;
      private String state;
      private GuestIPType guestIPType;
      private String VLAN;
      private TrafficType trafficType;
      private long zoneId;
      private Set<? extends NetworkService> services = ImmutableSet.<NetworkService> of();

      public Builder id(long id) {
         this.id = id;
         return this;
      }

      public Builder account(String account) {
         this.account = account;
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

      public Builder domainId(long domainId) {
         this.domainId = domainId;
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

      public Builder networkOfferingId(long networkOfferingId) {
         this.networkOfferingId = networkOfferingId;
         return this;
      }

      public Builder networkOfferingName(String networkOfferingName) {
         this.networkOfferingName = networkOfferingName;
         return this;
      }

      public Builder related(long related) {
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

      public Builder zoneId(long zoneId) {
         this.zoneId = zoneId;
         return this;
      }

      public Builder services(Set<? extends NetworkService> services) {
         this.services = ImmutableSet.<NetworkService> copyOf(checkNotNull(services, "services"));
         return this;
      }

      public Network build() {
         return new Network(id, account, broadcastDomainType, broadcastURI, displayText, DNS, domain, domainId, endIP,
               gateway, isDefault, isShared, isSystem, netmask, networkDomain, networkOfferingAvailability,
               networkOfferingDisplayText, networkOfferingId, networkOfferingName, related, startIP, name, state,
               guestIPType, VLAN, trafficType, zoneId, services);
      }
   }

   private long id;
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
   @Nullable
   @SerializedName("domainid")
   private long domainId;
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
   private long networkOfferingId;
   @SerializedName("networkofferingname")
   private String networkOfferingName;
   private long related;
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
   private long zoneId;
   @SerializedName("service")
   // so tests and serialization comes out expected
   private SortedSet<? extends NetworkService> services = ImmutableSortedSet.<NetworkService> of();

   /**
    * present only for serializer
    */
   Network() {

   }

   public Network(long id, String account, String broadcastDomainType, URI broadcastURI, String displayText,
         List<String> DNS, String domain, long domainId, String endIP, String gateway, boolean isDefault,
         boolean isShared, boolean isSystem, String netmask, String networkDomain, String networkOfferingAvailability,
         String networkOfferingDisplayText, long networkOfferingId, String networkOfferingName, long related,
         String startIP, String name, String state, GuestIPType type, String vLAN, TrafficType trafficType,
         long zoneId, Set<? extends NetworkService> services) {
      this.id = id;
      this.account = account;
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
   }

   /**
    * @return network id
    */
   public long getId() {
      return id;
   }

   /**
    * @return the name of the account to which the template beLongs
    */
   public String getAccount() {
      return account;
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
    * @return Domain name for the Vms in the zone
    */
   public String getDomain() {
      return domain;
   }

   /**
    * @return the ID of the containing domain, null for public zones
    */
   @Nullable
   public long getDomainId() {
      return domainId;
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
   public long getNetworkOfferingId() {
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
   public long getRelated() {
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
   public long getZoneId() {
      return zoneId;
   }

   /**
    * @return the list of services
    */
   public Set<? extends NetworkService> getServices() {
      return services;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((DNS1 == null) ? 0 : DNS1.hashCode());
      result = prime * result + ((DNS2 == null) ? 0 : DNS2.hashCode());
      result = prime * result + ((VLAN == null) ? 0 : VLAN.hashCode());
      result = prime * result + ((account == null) ? 0 : account.hashCode());
      result = prime * result + ((broadcastDomainType == null) ? 0 : broadcastDomainType.hashCode());
      result = prime * result + ((broadcastURI == null) ? 0 : broadcastURI.hashCode());
      result = prime * result + ((displayText == null) ? 0 : displayText.hashCode());
      result = prime * result + ((domain == null) ? 0 : domain.hashCode());
      result = prime * result + (int) (domainId ^ (domainId >>> 32));
      result = prime * result + ((endIP == null) ? 0 : endIP.hashCode());
      result = prime * result + ((gateway == null) ? 0 : gateway.hashCode());
      result = prime * result + ((guestIPType == null) ? 0 : guestIPType.hashCode());
      result = prime * result + (int) (id ^ (id >>> 32));
      result = prime * result + (isDefault ? 1231 : 1237);
      result = prime * result + (isShared ? 1231 : 1237);
      result = prime * result + (isSystem ? 1231 : 1237);
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((netmask == null) ? 0 : netmask.hashCode());
      result = prime * result + ((networkDomain == null) ? 0 : networkDomain.hashCode());
      result = prime * result + ((networkOfferingAvailability == null) ? 0 : networkOfferingAvailability.hashCode());
      result = prime * result + ((networkOfferingDisplayText == null) ? 0 : networkOfferingDisplayText.hashCode());
      result = prime * result + (int) (networkOfferingId ^ (networkOfferingId >>> 32));
      result = prime * result + ((networkOfferingName == null) ? 0 : networkOfferingName.hashCode());
      result = prime * result + (int) (related ^ (related >>> 32));
      result = prime * result + ((services == null) ? 0 : services.hashCode());
      result = prime * result + ((startIP == null) ? 0 : startIP.hashCode());
      result = prime * result + ((state == null) ? 0 : state.hashCode());
      result = prime * result + ((trafficType == null) ? 0 : trafficType.hashCode());
      result = prime * result + (int) (zoneId ^ (zoneId >>> 32));
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
      Network other = (Network) obj;
      if (DNS1 == null) {
         if (other.DNS1 != null)
            return false;
      } else if (!DNS1.equals(other.DNS1))
         return false;
      if (DNS2 == null) {
         if (other.DNS2 != null)
            return false;
      } else if (!DNS2.equals(other.DNS2))
         return false;
      if (VLAN == null) {
         if (other.VLAN != null)
            return false;
      } else if (!VLAN.equals(other.VLAN))
         return false;
      if (account == null) {
         if (other.account != null)
            return false;
      } else if (!account.equals(other.account))
         return false;
      if (broadcastDomainType == null) {
         if (other.broadcastDomainType != null)
            return false;
      } else if (!broadcastDomainType.equals(other.broadcastDomainType))
         return false;
      if (broadcastURI == null) {
         if (other.broadcastURI != null)
            return false;
      } else if (!broadcastURI.equals(other.broadcastURI))
         return false;
      if (displayText == null) {
         if (other.displayText != null)
            return false;
      } else if (!displayText.equals(other.displayText))
         return false;
      if (domain == null) {
         if (other.domain != null)
            return false;
      } else if (!domain.equals(other.domain))
         return false;
      if (domainId != other.domainId)
         return false;
      if (endIP == null) {
         if (other.endIP != null)
            return false;
      } else if (!endIP.equals(other.endIP))
         return false;
      if (gateway == null) {
         if (other.gateway != null)
            return false;
      } else if (!gateway.equals(other.gateway))
         return false;
      if (guestIPType != other.guestIPType)
         return false;
      if (id != other.id)
         return false;
      if (isDefault != other.isDefault)
         return false;
      if (isShared != other.isShared)
         return false;
      if (isSystem != other.isSystem)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (netmask == null) {
         if (other.netmask != null)
            return false;
      } else if (!netmask.equals(other.netmask))
         return false;
      if (networkDomain == null) {
         if (other.networkDomain != null)
            return false;
      } else if (!networkDomain.equals(other.networkDomain))
         return false;
      if (networkOfferingAvailability == null) {
         if (other.networkOfferingAvailability != null)
            return false;
      } else if (!networkOfferingAvailability.equals(other.networkOfferingAvailability))
         return false;
      if (networkOfferingDisplayText == null) {
         if (other.networkOfferingDisplayText != null)
            return false;
      } else if (!networkOfferingDisplayText.equals(other.networkOfferingDisplayText))
         return false;
      if (networkOfferingId != other.networkOfferingId)
         return false;
      if (networkOfferingName == null) {
         if (other.networkOfferingName != null)
            return false;
      } else if (!networkOfferingName.equals(other.networkOfferingName))
         return false;
      if (related != other.related)
         return false;
      if (services == null) {
         if (other.services != null)
            return false;
      } else if (!services.equals(other.services))
         return false;
      if (startIP == null) {
         if (other.startIP != null)
            return false;
      } else if (!startIP.equals(other.startIP))
         return false;
      if (state == null) {
         if (other.state != null)
            return false;
      } else if (!state.equals(other.state))
         return false;
      if (trafficType != other.trafficType)
         return false;
      if (zoneId != other.zoneId)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + id + ", state=" + state + ", name=" + name + ", displayText=" + displayText + ", guestIPType="
            + guestIPType + ", trafficType=" + trafficType + ", DNS=" + getDNS() + ", VLAN=" + VLAN + ", account="
            + account + ", startIP=" + startIP + ", endIP=" + endIP + ", netmask=" + netmask + ", gateway=" + gateway
            + ", broadcastDomainType=" + broadcastDomainType + ", broadcastURI=" + broadcastURI + ", services="
            + services + ", domain=" + domain + ", domainId=" + domainId + ", isDefault=" + isDefault + ", isShared="
            + isShared + ", isSystem=" + isSystem + ", related=" + related + ", zoneId=" + zoneId + ", domain="
            + networkDomain + ", networkOfferingAvailability=" + networkOfferingAvailability
            + ", networkOfferingDisplayText=" + networkOfferingDisplayText + ", networkOfferingId=" + networkOfferingId
            + ", networkOfferingName=" + networkOfferingName + "]";
   }

   @Override
   public int compareTo(Network arg0) {
      return new Long(id).compareTo(arg0.getId());
   }
}
