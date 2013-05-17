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
import java.util.List;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableList;

/**
 * @author Adrian Cole, Andrei Savu
 */
public class Zone implements Comparable<Zone> {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromZone(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected String description;
      protected String displayText;
      protected String DNS1;
      protected String DNS2;
      protected String domain;
      protected String domainId;
      protected String guestCIDRAddress;
      protected String internalDNS1;
      protected String internalDNS2;
      protected String name;
      protected NetworkType networkType;
      protected String VLAN;
      protected boolean securityGroupsEnabled;
      protected AllocationState allocationState;
      protected String dhcpProvider;
      protected String zoneToken;

      /**
       * @see Zone#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see Zone#getDescription()
       */
      public T description(String description) {
         this.description = description;
         return self();
      }

      /**
       * @see Zone#getDisplayText()
       */
      public T displayText(String displayText) {
         this.displayText = displayText;
         return self();
      }

      /**
       * @see Zone#getDNS()
       */
      public T DNS(List<String> DNS) {
         if (!DNS.isEmpty()) this.DNS1 = DNS.get(0);
         if (DNS.size() > 1) this.DNS2 = DNS.get(1);
         return self();
      }

      /**
       * @see Zone#getDomain()
       */
      public T domain(String domain) {
         this.domain = domain;
         return self();
      }

      /**
       * @see Zone#getDomainId()
       */
      public T domainId(String domainId) {
         this.domainId = domainId;
         return self();
      }

      /**
       * @see Zone#getGuestCIDRAddress()
       */
      public T guestCIDRAddress(String guestCIDRAddress) {
         this.guestCIDRAddress = guestCIDRAddress;
         return self();
      }

      /**
       * @see Zone#getInternalDNS()
       */
      public T internalDNS(List<String> DNS) {
         if (!DNS.isEmpty()) this.internalDNS1 = DNS.get(0);
         if (DNS.size() > 1) this.internalDNS2 = DNS.get(1);
         return self();
      }

      /**
       * @see Zone#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see Zone#getNetworkType()
       */
      public T networkType(NetworkType networkType) {
         this.networkType = networkType;
         return self();
      }

      /**
       * @see Zone#getVLAN()
       */
      public T VLAN(String VLAN) {
         this.VLAN = VLAN;
         return self();
      }

      /**
       * @see Zone#isSecurityGroupsEnabled()
       */
      public T securityGroupsEnabled(boolean securityGroupsEnabled) {
         this.securityGroupsEnabled = securityGroupsEnabled;
         return self();
      }

      /**
       * @see Zone#getAllocationState()
       */
      public T allocationState(AllocationState allocationState) {
         this.allocationState = allocationState;
         return self();
      }

      /**
       * @see Zone#getDhcpProvider()
       */
      public T dhcpProvider(String dhcpProvider) {
         this.dhcpProvider = dhcpProvider;
         return self();
      }

      /**
       * @see Zone#getZoneToken()
       */
      public T zoneToken(String zoneToken) {
         this.zoneToken = zoneToken;
         return self();
      }

      public Zone build() {
         return new Zone(id, description, displayText, DNS1, DNS2, domain, domainId, guestCIDRAddress, internalDNS1, internalDNS2,
               name, networkType, VLAN, securityGroupsEnabled, allocationState, dhcpProvider, zoneToken);
      }

      public T fromZone(Zone in) {
         return this
               .id(in.getId())
               .description(in.getDescription())
               .displayText(in.getDisplayText())
               .DNS(in.getDNS())
               .domain(in.getDomain())
               .domainId(in.getDomainId())
               .guestCIDRAddress(in.getGuestCIDRAddress())
               .internalDNS(in.getInternalDNS())
               .name(in.getName())
               .networkType(in.getNetworkType())
               .VLAN(in.getVLAN())
               .securityGroupsEnabled(in.isSecurityGroupsEnabled())
               .allocationState(in.getAllocationState())
               .dhcpProvider(in.getDhcpProvider())
               .zoneToken(in.getZoneToken());
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
   private final String displayText;
   private final String DNS1;
   private final String DNS2;
   private final String domain;
   private final String domainId;
   private final String guestCIDRAddress;
   private final String internalDNS1;
   private final String internalDNS2;
   private final String name;
   private final NetworkType networkType;
   private final String VLAN;
   private final boolean securityGroupsEnabled;
   private final AllocationState allocationState;
   private final String dhcpProvider;
   private final String zoneToken;

   @ConstructorProperties({
         "id", "description", "displaytext", "dns1", "dns2", "domain", "domainid", "guestcidraddress", "internaldns1", "internaldns2", "name", "networktype", "vlan", "securitygroupsenabled", "allocationstate", "dhcpprovider", "zonetoken"
   })
   protected Zone(String id, @Nullable String description, @Nullable String displayText, @Nullable String DNS1, @Nullable String DNS2,
                  @Nullable String domain, @Nullable String domainId, @Nullable String guestCIDRAddress, @Nullable String internalDNS1,
                  @Nullable String internalDNS2, @Nullable String name, @Nullable NetworkType networkType, @Nullable String VLAN,
                  boolean securityGroupsEnabled, @Nullable AllocationState allocationState, @Nullable String dhcpProvider,
                  @Nullable String zoneToken) {
      this.id = checkNotNull(id, "id");
      this.description = description;
      this.displayText = displayText;
      this.DNS1 = DNS1;
      this.DNS2 = DNS2;
      this.domain = domain;
      this.domainId = domainId;
      this.guestCIDRAddress = guestCIDRAddress;
      this.internalDNS1 = internalDNS1;
      this.internalDNS2 = internalDNS2;
      this.name = name;
      this.networkType = networkType;
      this.VLAN = VLAN;
      this.securityGroupsEnabled = securityGroupsEnabled;
      this.allocationState = allocationState;
      this.dhcpProvider = dhcpProvider;
      this.zoneToken = zoneToken;
   }

   /**
    * @return Zone id
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return Zone description
    */
   @Nullable
   public String getDescription() {
      return this.description;
   }

   /**
    * @return the display text of the zone
    */
   @Nullable
   public String getDisplayText() {
      return this.displayText;
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
   @Nullable
   public String getDomain() {
      return this.domain;
   }

   /**
    * @return the ID of the containing domain, null for public zones
    */
   @Nullable
   public String getDomainId() {
      return this.domainId;
   }

   /**
    * @return the guest CIDR address for the Zone
    */
   @Nullable
   public String getGuestCIDRAddress() {
      return this.guestCIDRAddress;
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
   @Nullable
   public String getName() {
      return this.name;
   }

   /**
    * @return the network type of the zone; can be Basic or Advanced
    */
   @Nullable
   public NetworkType getNetworkType() {
      return this.networkType;
   }

   /**
    * @return the vlan range of the zone
    */
   @Nullable
   public String getVLAN() {
      return this.VLAN;
   }

   /**
    * @return true if this zone has security groups enabled
    */
   public boolean isSecurityGroupsEnabled() {
      return this.securityGroupsEnabled;
   }

   /**
    * @return the allocation state of the cluster
    */
   @Nullable
   public AllocationState getAllocationState() {
      return this.allocationState;
   }

   /**
    * @return the dhcp Provider for the Zone
    */
   @Nullable
   public String getDhcpProvider() {
      return this.dhcpProvider;
   }

   /**
    * @return Zone Token
    */
   @Nullable
   public String getZoneToken() {
      return this.zoneToken;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, description, displayText, DNS1, DNS2, domain, domainId, guestCIDRAddress, internalDNS1,
            internalDNS2, name, networkType, VLAN, securityGroupsEnabled, allocationState, dhcpProvider, zoneToken);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Zone that = Zone.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.description, that.description)
            && Objects.equal(this.displayText, that.displayText)
            && Objects.equal(this.DNS1, that.DNS1)
            && Objects.equal(this.DNS2, that.DNS2)
            && Objects.equal(this.domain, that.domain)
            && Objects.equal(this.domainId, that.domainId)
            && Objects.equal(this.guestCIDRAddress, that.guestCIDRAddress)
            && Objects.equal(this.internalDNS1, that.internalDNS1)
            && Objects.equal(this.internalDNS2, that.internalDNS2)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.networkType, that.networkType)
            && Objects.equal(this.VLAN, that.VLAN)
            && Objects.equal(this.securityGroupsEnabled, that.securityGroupsEnabled)
            && Objects.equal(this.allocationState, that.allocationState)
            && Objects.equal(this.dhcpProvider, that.dhcpProvider)
            && Objects.equal(this.zoneToken, that.zoneToken);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("description", description).add("displayText", displayText).add("DNS1", DNS1).add("DNS2", DNS2)
            .add("domain", domain).add("domainId", domainId).add("guestCIDRAddress", guestCIDRAddress).add("internalDNS1", internalDNS1)
            .add("internalDNS2", internalDNS2).add("name", name).add("networkType", networkType).add("VLAN", VLAN)
            .add("securityGroupsEnabled", securityGroupsEnabled).add("allocationState", allocationState).add("dhcpProvider", dhcpProvider)
            .add("zoneToken", zoneToken);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(Zone o) {
      return id.compareTo(o.getId());
   }
}
