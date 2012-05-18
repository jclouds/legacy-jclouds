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

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.gson.annotations.SerializedName;

/**
 * @author Adrian Cole, Andrei Savu
 */
public class Zone implements Comparable<Zone> {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String id;
      private String description;
      private String displayText;
      private List<String> DNS = ImmutableList.of();
      private String domain;
      private String domainId;
      private String guestCIDRAddress;
      private List<String> internalDNS = ImmutableList.of();
      private String name;
      private NetworkType networkType;
      private String VLAN;
      private boolean securityGroupsEnabled;
      private AllocationState allocationState;
      private String dhcpProvider;
      private String zoneToken;

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder description(String description) {
         this.description = description;
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

      public Builder domainId(String domainId) {
         this.domainId = domainId;
         return this;
      }

      public Builder guestCIDRAddress(String guestCIDRAddress) {
         this.guestCIDRAddress = guestCIDRAddress;
         return this;
      }

      public Builder internalDNS(List<String> internalDNS) {
         this.internalDNS = ImmutableList.copyOf(checkNotNull(internalDNS, "internalDNS"));
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder networkType(NetworkType networkType) {
         this.networkType = networkType;
         return this;
      }

      public Builder VLAN(String VLAN) {
         this.VLAN = VLAN;
         return this;
      }

      public Builder securityGroupsEnabled(boolean securityGroupsEnabled) {
         this.securityGroupsEnabled = securityGroupsEnabled;
         return this;
      }


      public Builder allocationState(AllocationState allocationState) {
         this.allocationState = allocationState;
         return this;
      }

      public Builder dhcpProvider(String dhcpProvider) {
         this.dhcpProvider = dhcpProvider;
         return this;
      }

      public Builder zoneToken(String zoneToken) {
         this.zoneToken = zoneToken;
         return this;
      }

      public Zone build() {
         return new Zone(id, description, displayText, DNS, domain, domainId, guestCIDRAddress, internalDNS, name,
               networkType, VLAN, securityGroupsEnabled, allocationState, dhcpProvider, zoneToken);
      }
   }

   private String id;
   private String description;
   @SerializedName("displaytext")
   private String displayText;
   @SerializedName("dns1")
   private String DNS1;
   @SerializedName("dns2")
   private String DNS2;
   private String domain;
   @Nullable
   @SerializedName("domainid")
   private String domainId;
   @SerializedName("guestcidraddress")
   private String guestCIDRAddress;
   @SerializedName("internaldns1")
   private String internalDNS1;
   @SerializedName("internaldns2")
   private String internalDNS2;
   private String name;
   @SerializedName("networktype")
   private NetworkType networkType;
   @SerializedName("vlan")
   private String VLAN;
   @SerializedName("securitygroupsenabled")
   private boolean securityGroupsEnabled;
   @SerializedName("allocationstate")
   private AllocationState allocationState;
   @SerializedName("dhcpprovider")
   private String dhcpProvider;
   @SerializedName("zonetoken")
   private String zoneToken;

   /**
    * present only for serializer
    */
   Zone() {

   }

   public Zone(String id, String description, String displayText, List<String> DNS, String domain, String domainId,
               String guestCIDRAddress, List<String> internalDNS, String name, NetworkType networkType,
               String vLAN, boolean securityGroupsEnabled, AllocationState allocationState, String dhcpProvider, String zoneToken) {
      this.id = id;
      this.description = description;
      this.displayText = displayText;
      this.DNS1 = checkNotNull(DNS, "DNS").size() > 0 ? DNS.get(0) : null;
      this.DNS2 = DNS.size() > 1 ? DNS.get(1) : null;
      this.domain = domain;
      this.domainId = domainId;
      this.guestCIDRAddress = guestCIDRAddress;
      this.internalDNS1 = checkNotNull(internalDNS, "internalDNS").size() > 0 ? internalDNS.get(0) : null;
      this.internalDNS2 = internalDNS.size() > 1 ? internalDNS.get(1) : null;
      this.name = name;
      this.networkType = networkType;
      this.VLAN = vLAN;
      this.securityGroupsEnabled = securityGroupsEnabled;
      this.allocationState = allocationState;
      this.dhcpProvider = dhcpProvider;
      this.zoneToken = zoneToken;
   }

   /**
    * @return Zone id
    */
   public String getId() {
      return id;
   }

   /**
    * @return Zone description
    */
   public String getDescription() {
      return description;
   }

   /**
    * @return the display text of the zone
    */
   public String getDisplayText() {
      return displayText;
   }

   /**
    * @return the external DNS for the Zone
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
   public String getDomainId() {
      return domainId;
   }

   /**
    * @return the guest CIDR address for the Zone
    */
   public String getGuestCIDRAddress() {
      return guestCIDRAddress;
   }

   /**
    * @return the internal DNS for the Zone
    */
   public List<String> getInternalDNS() {
      ImmutableList.Builder<String> builder = ImmutableList.builder();
      if (internalDNS1 != null && !"".equals(internalDNS1))
         builder.add(internalDNS1);
      if (internalDNS2 != null && !"".equals(internalDNS2))
         builder.add(internalDNS2);
      return builder.build();
   }

   /**
    * @return Zone name
    */
   public String getName() {
      return name;
   }

   /**
    * @return the network type of the zone; can be Basic or Advanced
    */
   public NetworkType getNetworkType() {
      return networkType;
   }

   /**
    * @return the vlan range of the zone
    */
   public String getVLAN() {
      return VLAN;
   }

   /**
    * @return true if this zone has security groups enabled
    */
   public boolean isSecurityGroupsEnabled() {
      return securityGroupsEnabled;
   }

   /**
    * @return the allocation state of the cluster
    */
   public AllocationState getAllocationState() {
      return allocationState;
   }

   /**
    * @return the dhcp Provider for the Zone
    */
   public String getDhcpProvider() {
      return dhcpProvider;
   }

   /**
    * @return Zone Token
    */
   public String getZoneToken() {
      return zoneToken;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Zone that = (Zone) o;

      if (!Objects.equal(id, that.id)) return false;
      if (!Objects.equal(description, that.description)) return false;
      if (!Objects.equal(displayText, that.displayText)) return false;
      if (!Objects.equal(DNS1, that.DNS1)) return false;
      if (!Objects.equal(DNS2, that.DNS2)) return false;
      if (!Objects.equal(domain, that.domain)) return false;
      if (!Objects.equal(domainId, that.domainId)) return false;
      if (!Objects.equal(guestCIDRAddress, that.guestCIDRAddress)) return false;
      if (!Objects.equal(internalDNS1, that.internalDNS1)) return false;
      if (!Objects.equal(internalDNS2, that.internalDNS2)) return false;
      if (!Objects.equal(name, that.name)) return false;
      if (!Objects.equal(networkType, that.networkType)) return false;
      if (!Objects.equal(VLAN, that.VLAN)) return false;
      if (!Objects.equal(securityGroupsEnabled, that.securityGroupsEnabled)) return false;
      if (!Objects.equal(allocationState, that.allocationState)) return false;
      if (!Objects.equal(dhcpProvider, that.dhcpProvider)) return false;
      if (!Objects.equal(zoneToken, that.zoneToken)) return false;

      return true;
   }

   @Override
   public int hashCode() {
       return Objects.hashCode(id, description, displayText, DNS1, DNS2, domain, domainId,
                               guestCIDRAddress, internalDNS1, internalDNS2, name, networkType, VLAN,
                               securityGroupsEnabled, allocationState, dhcpProvider,
                               zoneToken);
   }

   @Override
   public String toString() {
      return "Zone{" +
            "id=" + id +
            ", description='" + description + '\'' +
            ", displayText='" + displayText + '\'' +
            ", DNS1='" + DNS1 + '\'' +
            ", DNS2='" + DNS2 + '\'' +
            ", domain='" + domain + '\'' +
            ", domainId=" + domainId +
            ", guestCIDRAddress='" + guestCIDRAddress + '\'' +
            ", internalDNS1='" + internalDNS1 + '\'' +
            ", internalDNS2='" + internalDNS2 + '\'' +
            ", name='" + name + '\'' +
            ", networkType=" + networkType +
            ", VLAN='" + VLAN + '\'' +
            ", securityGroupsEnabled=" + securityGroupsEnabled +
            ", allocationState='" + allocationState + '\'' +
            ", dhcpProvider='" + dhcpProvider + '\'' +
            ", zoneToken='" + zoneToken + '\'' +
            '}';
   }

   @Override
   public int compareTo(Zone arg0) {
      return id.compareTo(arg0.getId());
   }
}
