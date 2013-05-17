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
package org.jclouds.trmk.vcloud_0_8.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

/**
 * @author Adrian Cole
 */
public class NetworkExtendedInfo implements Comparable<NetworkExtendedInfo> {
   public enum Type {
      INTERNAL, DMZ, UNRECOGNIZED;
      public static Type fromValue(String type) {
         try {
            return valueOf(checkNotNull(type, "type").toUpperCase());
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }

   }

   private final String id;
   private final URI href;
   private final String name;
   private final String rnatAddress;
   private final String address;
   private final String broadcastAddress;
   private final String gatewayAddress;
   private final Type networkType;
   private final String vlan;
   private final String friendlyName;

   public NetworkExtendedInfo(String id, URI href, String name, String rnatAddress, String address,
            String broadcastAddress, String gatewayAddress, Type networkType, String vlan, String friendlyName) {
      this.id = id;
      this.href = href;
      this.name = name;
      this.rnatAddress = rnatAddress;
      this.address = address;
      this.broadcastAddress = broadcastAddress;
      this.gatewayAddress = gatewayAddress;
      this.networkType = networkType;
      this.vlan = vlan;
      this.friendlyName = friendlyName;
   }

   public int compareTo(NetworkExtendedInfo that) {
      return (this == that) ? 0 : getHref().compareTo(that.getHref());
   }

   public String getId() {
      return id;
   }

   public URI getHref() {
      return href;
   }

   public String getName() {
      return name;
   }

   public String getRnatAddress() {
      return rnatAddress;
   }

   public String getAddress() {
      return address;
   }

   public String getBroadcastAddress() {
      return broadcastAddress;
   }

   public String getGatewayAddress() {
      return gatewayAddress;
   }

   public Type getNetworkType() {
      return networkType;
   }

   public String getVlan() {
      return vlan;
   }

   public String getFriendlyName() {
      return friendlyName;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((address == null) ? 0 : address.hashCode());
      result = prime * result + ((broadcastAddress == null) ? 0 : broadcastAddress.hashCode());
      result = prime * result + ((friendlyName == null) ? 0 : friendlyName.hashCode());
      result = prime * result + ((gatewayAddress == null) ? 0 : gatewayAddress.hashCode());
      result = prime * result + ((href == null) ? 0 : href.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((networkType == null) ? 0 : networkType.hashCode());
      result = prime * result + ((rnatAddress == null) ? 0 : rnatAddress.hashCode());
      result = prime * result + ((vlan == null) ? 0 : vlan.hashCode());
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
      NetworkExtendedInfo other = (NetworkExtendedInfo) obj;
      if (address == null) {
         if (other.address != null)
            return false;
      } else if (!address.equals(other.address))
         return false;
      if (broadcastAddress == null) {
         if (other.broadcastAddress != null)
            return false;
      } else if (!broadcastAddress.equals(other.broadcastAddress))
         return false;
      if (friendlyName == null) {
         if (other.friendlyName != null)
            return false;
      } else if (!friendlyName.equals(other.friendlyName))
         return false;
      if (gatewayAddress == null) {
         if (other.gatewayAddress != null)
            return false;
      } else if (!gatewayAddress.equals(other.gatewayAddress))
         return false;
      if (href == null) {
         if (other.href != null)
            return false;
      } else if (!href.equals(other.href))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
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
      if (rnatAddress == null) {
         if (other.rnatAddress != null)
            return false;
      } else if (!rnatAddress.equals(other.rnatAddress))
         return false;
      if (vlan == null) {
         if (other.vlan != null)
            return false;
      } else if (!vlan.equals(other.vlan))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[address=" + address + ", broadcastAddress=" + broadcastAddress + ", friendlyName=" + friendlyName
               + ", gatewayAddress=" + gatewayAddress + ", href=" + href + ", id=" + id + ", name=" + name
               + ", networkType=" + networkType + ", rnatAddress=" + rnatAddress + ", vlan=" + vlan + "]";
   }
}
