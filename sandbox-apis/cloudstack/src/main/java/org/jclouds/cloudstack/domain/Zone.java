/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.cloudstack.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Adrian Cole
 */
public class Zone {
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
   private String status;
   @SerializedName("vlan")
   private String VLAN;

   /**
    * present only for serializer
    * 
    */
   Zone() {

   }

   public Zone(String id, String description, String displayText, List<String> DNS, String domain, String domainId,
            String guestCIDRAddress, List<String> internalDNS, String name, NetworkType networkType, String status,
            String vLAN) {
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
   }

   /**
    * 
    * @return Zone id
    */
   public String getId() {
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
      Builder<String> builder = ImmutableList.builder();
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
   public String getDomainId() {
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
      Builder<String> builder = ImmutableList.builder();
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
      result = prime * result + ((domainId == null) ? 0 : domainId.hashCode());
      result = prime * result + ((guestCIDRAddress == null) ? 0 : guestCIDRAddress.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((internalDNS1 == null) ? 0 : internalDNS1.hashCode());
      result = prime * result + ((internalDNS2 == null) ? 0 : internalDNS2.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((networkType == null) ? 0 : networkType.hashCode());
      result = prime * result + ((status == null) ? 0 : status.hashCode());
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
      if (domainId == null) {
         if (other.domainId != null)
            return false;
      } else if (!domainId.equals(other.domainId))
         return false;
      if (guestCIDRAddress == null) {
         if (other.guestCIDRAddress != null)
            return false;
      } else if (!guestCIDRAddress.equals(other.guestCIDRAddress))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
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
      if (status == null) {
         if (other.status != null)
            return false;
      } else if (!status.equals(other.status))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + id + ", status=" + status + ", name=" + name + ", description=" + description + ", displayText="
               + displayText + ", domain=" + domain + ", domainId=" + domainId + ", networkType=" + networkType
               + ", guestCIDRAddress=" + guestCIDRAddress + ", VLAN=" + VLAN + ", DNS=" + getDNS() + ", internalDNS="
               + getInternalDNS() + "]";
   }
}
