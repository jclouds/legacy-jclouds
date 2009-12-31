/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud.domain;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Adrian Cole
 * 
 */
public class ResourceAllocation implements Comparable<ResourceAllocation> {

   private final int id;
   private final String name;
   private final String description;
   private final ResourceType type;
   private final String subType;
   private final String hostResource;
   private final Integer address;
   private final Integer addressOnParent;
   private final Integer parent;
   private final Boolean connected;
   private final long virtualQuantity;
   private final String virtualQuantityUnits;

   public ResourceAllocation(int id, String name, String description, ResourceType type,
            String subType, String hostResource, Integer address, Integer addressOnParent,
            Integer parent, Boolean connected, long virtualQuantity, String virtualQuantityUnits) {
      this.id = id;
      this.name = checkNotNull(name, "name");
      this.description = description;
      this.type = checkNotNull(type, "type");
      this.subType = subType;
      this.hostResource = hostResource;
      this.address = address;
      this.addressOnParent = addressOnParent;
      this.parent = parent;
      this.connected = connected;
      this.virtualQuantity = virtualQuantity;
      this.virtualQuantityUnits = virtualQuantityUnits;
   }

   public int compareTo(ResourceAllocation that) {
      final int BEFORE = -1;
      final int EQUAL = 0;
      final int AFTER = 1;

      if (this == that)
         return EQUAL;

      if (this.id < that.id)
         return BEFORE;
      if (this.id > that.id)
         return AFTER;
      if (this.addressOnParent != null && that.addressOnParent != null) {
         if (this.addressOnParent < that.addressOnParent)
            return BEFORE;
         if (this.addressOnParent > that.addressOnParent)
            return AFTER;
      }
      return 1;
   }

   public int getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public String getDescription() {
      return description;
   }

   public ResourceType getType() {
      return type;
   }

   public String getSubType() {
      return subType;
   }

   public Integer getAddress() {
      return address;
   }

   public Integer getAddressOnParent() {
      return addressOnParent;
   }

   public Integer getParent() {
      return parent;
   }

   public Boolean getConnected() {
      return connected;
   }

   public long getVirtualQuantity() {
      return virtualQuantity;
   }

   public String getVirtualQuantityUnits() {
      return virtualQuantityUnits;
   }

   public String getHostResource() {
      return hostResource;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((address == null) ? 0 : address.hashCode());
      result = prime * result + ((addressOnParent == null) ? 0 : addressOnParent.hashCode());
      result = prime * result + ((connected == null) ? 0 : connected.hashCode());
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((hostResource == null) ? 0 : hostResource.hashCode());
      result = prime * result + id;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((parent == null) ? 0 : parent.hashCode());
      result = prime * result + ((subType == null) ? 0 : subType.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      result = prime * result + (int) (virtualQuantity ^ (virtualQuantity >>> 32));
      result = prime * result
               + ((virtualQuantityUnits == null) ? 0 : virtualQuantityUnits.hashCode());
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
      ResourceAllocation other = (ResourceAllocation) obj;
      if (address == null) {
         if (other.address != null)
            return false;
      } else if (!address.equals(other.address))
         return false;
      if (addressOnParent == null) {
         if (other.addressOnParent != null)
            return false;
      } else if (!addressOnParent.equals(other.addressOnParent))
         return false;
      if (connected == null) {
         if (other.connected != null)
            return false;
      } else if (!connected.equals(other.connected))
         return false;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (hostResource == null) {
         if (other.hostResource != null)
            return false;
      } else if (!hostResource.equals(other.hostResource))
         return false;
      if (id != other.id)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (parent == null) {
         if (other.parent != null)
            return false;
      } else if (!parent.equals(other.parent))
         return false;
      if (subType == null) {
         if (other.subType != null)
            return false;
      } else if (!subType.equals(other.subType))
         return false;
      if (type == null) {
         if (other.type != null)
            return false;
      } else if (!type.equals(other.type))
         return false;
      if (virtualQuantity != other.virtualQuantity)
         return false;
      if (virtualQuantityUnits == null) {
         if (other.virtualQuantityUnits != null)
            return false;
      } else if (!virtualQuantityUnits.equals(other.virtualQuantityUnits))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "ResourceAllocation [address=" + address + ", addressOnParent=" + addressOnParent
               + ", connected=" + connected + ", description=" + description + ", hostResource="
               + hostResource + ", id=" + id + ", name=" + name + ", parent=" + parent
               + ", subType=" + subType + ", type=" + type + ", virtualQuantity=" + virtualQuantity
               + ", virtualQuantityUnits=" + virtualQuantityUnits + "]";
   }

}