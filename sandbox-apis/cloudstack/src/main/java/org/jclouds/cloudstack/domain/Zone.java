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

import com.google.common.collect.ImmutableList;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Adrian Cole
 */
public class Zone implements Comparable<Zone> {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private long id;
      private String description;
      private String displayText;
      private List<String> DNS = ImmutableList.of();
      private String domain;
      private long domainId;
      private String guestCIDRAddress;
      private List<String> internalDNS = ImmutableList.of();
      private String name;
      private NetworkType networkType;
      private String status;
      private String VLAN;
      private boolean securityGroupsEnabled;

      public Builder id(long id) {
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

      public Builder domainId(long domainId) {
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

      public Builder status(String status) {
         this.status = status;
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

      public Zone build() {
         return new Zone(id, description, displayText, DNS, domain, domainId, guestCIDRAddress, internalDNS, name,
                  networkType, status, VLAN, securityGroupsEnabled);
      }
   }

   private long id;
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
   private long domainId;
   @SerializedName("guestcidraddress")
   private String guestCIDRAddress;
   @SerializedName("internaldns1")
   private String internalDNS1;
   @SerializedName("internaldns2")
   private String internalDNS2;
   private String name;
   @SerializedName("networktype")
   private NetworkType networkType;
   private String status;
   @SerializedName("vlan")
   private String VLAN;
   @SerializedName("securitygroupsenabled")
   private boolean securityGroupsEnabled;

   /**
    * present only for serializer
    * 
    */
   Zone() {

   }

   public Zone(long id, String description, String displayText, List<String> DNS, String domain, long domainId,
            String guestCIDRAddress, List<String> internalDNS, String name, NetworkType networkType, String status,
            String vLAN, boolean securityGroupsEnabled) {
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
      this.status = status;
      this.VLAN = vLAN;
      this.securityGroupsEnabled = securityGroupsEnabled;
   }

   /**
    * 
    * @return Zone id
    */
   public long getId() {
      return id;
   }

   /**
    * 
    * @return Zone description
    */
   public String getDescription() {
      return description;
   }

   /**
    * 
    * @return the display text of the zone
    */
   public String getDisplayText() {
      return displayText;
   }

   /**
    * 
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
    * 
    * @return Domain name for the Vms in the zone
    */
   public String getDomain() {
      return domain;
   }

   /**
    * 
    * @return the ID of the containing domain, null for public zones
    */
   @Nullable
   public long getDomainId() {
      return domainId;
   }

   /**
    * 
    * @return the guest CIDR address for the Zone
    */
   public String getGuestCIDRAddress() {
      return guestCIDRAddress;
   }

   /**
    * 
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
    * 
    * @return Zone name
    */
   public String getName() {
      return name;
   }

   /**
    * 
    * @return the network type of the zone; can be Basic or Advanced
    */
   public NetworkType getNetworkType() {
      return networkType;
   }

   /**
    * 
    * @return
    */
   public String getStatus() {
      return status;
   }

   /**
    * 
    * @return the vlan range of the zone
    */
   public String getVLAN() {
      return VLAN;
   }

   /**
    * 
    * @return true if this zone has security groups enabled
    */
   public boolean isSecurityGroupsEnabled() {
      return securityGroupsEnabled;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((DNS1 == null) ? 0 : DNS1.hashCode());
      result = prime * result + ((DNS2 == null) ? 0 : DNS2.hashCode());
      result = prime * result + ((VLAN == null) ? 0 : VLAN.hashCode());
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((displayText == null) ? 0 : displayText.hashCode());
      result = prime * result + ((domain == null) ? 0 : domain.hashCode());
      result = prime * result + (int) (domainId ^ (domainId >>> 32));
      result = prime * result + ((guestCIDRAddress == null) ? 0 : guestCIDRAddress.hashCode());
      result = prime * result + (int) (id ^ (id >>> 32));
      result = prime * result + ((internalDNS1 == null) ? 0 : internalDNS1.hashCode());
      result = prime * result + ((internalDNS2 == null) ? 0 : internalDNS2.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((networkType == null) ? 0 : networkType.hashCode());
      result = prime * result + (securityGroupsEnabled ? 1231 : 1237);
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
      Zone other = (Zone) obj;
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
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
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
      if (guestCIDRAddress == null) {
         if (other.guestCIDRAddress != null)
            return false;
      } else if (!guestCIDRAddress.equals(other.guestCIDRAddress))
         return false;
      if (id != other.id)
         return false;
      if (internalDNS1 == null) {
         if (other.internalDNS1 != null)
            return false;
      } else if (!internalDNS1.equals(other.internalDNS1))
         return false;
      if (internalDNS2 == null) {
         if (other.internalDNS2 != null)
            return false;
      } else if (!internalDNS2.equals(other.internalDNS2))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (networkType == null) {
         if (other.networkType != null)
            return false;
      } else if (!networkType.equals(other.networkType))
         return false;
      if (securityGroupsEnabled != other.securityGroupsEnabled)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + id + ", status=" + status + ", name=" + name + ", description=" + description + ", displayText="
               + displayText + ", domain=" + domain + ", domainId=" + domainId + ", networkType=" + networkType
               + ", guestCIDRAddress=" + guestCIDRAddress + ", VLAN=" + VLAN + ", DNS=" + getDNS()
               + ", securityGroupsEnabled=" + isSecurityGroupsEnabled() + ", internalDNS=" + getInternalDNS() + "]";
   }

   @Override
   public int compareTo(Zone arg0) {
      return new Long(id).compareTo(arg0.getId());
   }
}
