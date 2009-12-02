/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.vcloud.domain;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Adrian Cole
 */
public class TerremarkResourceAllocation implements Comparable<TerremarkResourceAllocation> {
   private final Integer address;
   private final Integer addressOnParent;
   private final String allocationUnits;
   private final String automaticAllocation;
   private final String automaticDeallocation;
   private final String caption;
   private final String consumerVisibility;
   private final String description;
   private final String elementName;
   private final String hostResource;
   private final int instanceID;
   private final String limit;
   private final String mappingBehavior;
   private final String otherResourceType;
   private final Integer parent;
   private final String poolID;
   private final String reservation;
   private final String resourceSubType;
   private final ResourceType resourceType;
   private final long virtualQuantity;
   private final String virtualQuantityUnits;
   private final String weight;

   public TerremarkResourceAllocation(Integer address, Integer addressOnParent, String allocationUnits,
            String automaticAllocation, String automaticDeallocation, String caption,
            String consumerVisibility, String description, String elementName, String hostResource,
            int instanceID, String limit, String mappingBehavior, String otherResourceType,
            Integer parent, String poolID, String reservation, String resourceSubType,
            ResourceType resourceType, long virtualQuantity, String virtualQuantityUnits,
            String weight) {
      this.address = address;
      this.addressOnParent = addressOnParent;
      this.allocationUnits = allocationUnits;
      this.automaticAllocation = automaticAllocation;
      this.automaticDeallocation = automaticDeallocation;
      this.caption = caption;
      this.consumerVisibility = consumerVisibility;
      this.description = description;
      this.elementName = elementName;
      this.hostResource = hostResource;
      this.instanceID = checkNotNull(instanceID, "instanceID");
      this.limit = limit;
      this.mappingBehavior = mappingBehavior;
      this.otherResourceType = otherResourceType;
      this.parent = parent;
      this.poolID = poolID;
      this.reservation = reservation;
      this.resourceSubType = resourceSubType;
      this.resourceType = checkNotNull(resourceType, "resourceType");
      this.virtualQuantity = virtualQuantity;
      this.virtualQuantityUnits = virtualQuantityUnits;
      this.weight = weight;
   }

   public Integer getAddress() {
      return address;
   }

   public Integer getAddressOnParent() {
      return addressOnParent;
   }

   public String getAllocationUnits() {
      return allocationUnits;
   }

   public String getAutomaticAllocation() {
      return automaticAllocation;
   }

   public String getAutomaticDeallocation() {
      return automaticDeallocation;
   }

   public String getCaption() {
      return caption;
   }

   public String getConsumerVisibility() {
      return consumerVisibility;
   }

   public String getDescription() {
      return description;
   }

   public String getElementName() {
      return elementName;
   }

   public int getInstanceID() {
      return instanceID;
   }

   public String getLimit() {
      return limit;
   }

   public String getMappingBehavior() {
      return mappingBehavior;
   }

   public String getOtherResourceType() {
      return otherResourceType;
   }

   public Integer getParent() {
      return parent;
   }

   public String getPoolID() {
      return poolID;
   }

   public String getReservation() {
      return reservation;
   }

   public String getResourceSubType() {
      return resourceSubType;
   }

   public ResourceType getResourceType() {
      return resourceType;
   }

   public long getVirtualQuantity() {
      return virtualQuantity;
   }

   public String getVirtualQuantityUnits() {
      return virtualQuantityUnits;
   }

   public String getWeight() {
      return weight;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((address == null) ? 0 : address.hashCode());
      result = prime * result + ((addressOnParent == null) ? 0 : addressOnParent.hashCode());
      result = prime * result + ((allocationUnits == null) ? 0 : allocationUnits.hashCode());
      result = prime * result
               + ((automaticAllocation == null) ? 0 : automaticAllocation.hashCode());
      result = prime * result
               + ((automaticDeallocation == null) ? 0 : automaticDeallocation.hashCode());
      result = prime * result + ((caption == null) ? 0 : caption.hashCode());
      result = prime * result + ((consumerVisibility == null) ? 0 : consumerVisibility.hashCode());
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((elementName == null) ? 0 : elementName.hashCode());
      result = prime * result + instanceID;
      result = prime * result + ((limit == null) ? 0 : limit.hashCode());
      result = prime * result + ((mappingBehavior == null) ? 0 : mappingBehavior.hashCode());
      result = prime * result + ((otherResourceType == null) ? 0 : otherResourceType.hashCode());
      result = prime * result + ((parent == null) ? 0 : parent.hashCode());
      result = prime * result + ((poolID == null) ? 0 : poolID.hashCode());
      result = prime * result + ((reservation == null) ? 0 : reservation.hashCode());
      result = prime * result + ((resourceSubType == null) ? 0 : resourceSubType.hashCode());
      result = prime * result + ((resourceType == null) ? 0 : resourceType.hashCode());
      result = prime * result + (int) (virtualQuantity ^ (virtualQuantity >>> 32));
      result = prime * result
               + ((virtualQuantityUnits == null) ? 0 : virtualQuantityUnits.hashCode());
      result = prime * result + ((weight == null) ? 0 : weight.hashCode());
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
      TerremarkResourceAllocation other = (TerremarkResourceAllocation) obj;
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
      if (allocationUnits == null) {
         if (other.allocationUnits != null)
            return false;
      } else if (!allocationUnits.equals(other.allocationUnits))
         return false;
      if (automaticAllocation == null) {
         if (other.automaticAllocation != null)
            return false;
      } else if (!automaticAllocation.equals(other.automaticAllocation))
         return false;
      if (automaticDeallocation == null) {
         if (other.automaticDeallocation != null)
            return false;
      } else if (!automaticDeallocation.equals(other.automaticDeallocation))
         return false;
      if (caption == null) {
         if (other.caption != null)
            return false;
      } else if (!caption.equals(other.caption))
         return false;
      if (consumerVisibility == null) {
         if (other.consumerVisibility != null)
            return false;
      } else if (!consumerVisibility.equals(other.consumerVisibility))
         return false;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (elementName == null) {
         if (other.elementName != null)
            return false;
      } else if (!elementName.equals(other.elementName))
         return false;
      if (instanceID != other.instanceID)
         return false;
      if (limit == null) {
         if (other.limit != null)
            return false;
      } else if (!limit.equals(other.limit))
         return false;
      if (mappingBehavior == null) {
         if (other.mappingBehavior != null)
            return false;
      } else if (!mappingBehavior.equals(other.mappingBehavior))
         return false;
      if (otherResourceType == null) {
         if (other.otherResourceType != null)
            return false;
      } else if (!otherResourceType.equals(other.otherResourceType))
         return false;
      if (parent == null) {
         if (other.parent != null)
            return false;
      } else if (!parent.equals(other.parent))
         return false;
      if (poolID == null) {
         if (other.poolID != null)
            return false;
      } else if (!poolID.equals(other.poolID))
         return false;
      if (reservation == null) {
         if (other.reservation != null)
            return false;
      } else if (!reservation.equals(other.reservation))
         return false;
      if (resourceSubType == null) {
         if (other.resourceSubType != null)
            return false;
      } else if (!resourceSubType.equals(other.resourceSubType))
         return false;
      if (resourceType == null) {
         if (other.resourceType != null)
            return false;
      } else if (!resourceType.equals(other.resourceType))
         return false;
      if (virtualQuantity != other.virtualQuantity)
         return false;
      if (virtualQuantityUnits == null) {
         if (other.virtualQuantityUnits != null)
            return false;
      } else if (!virtualQuantityUnits.equals(other.virtualQuantityUnits))
         return false;
      if (weight == null) {
         if (other.weight != null)
            return false;
      } else if (!weight.equals(other.weight))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "ResourceAllocation [address=" + address + ", addressOnParent=" + addressOnParent
               + ", allocationUnits=" + allocationUnits + ", automaticAllocation="
               + automaticAllocation + ", automaticDeallocation=" + automaticDeallocation
               + ", caption=" + caption + ", consumerVisibility=" + consumerVisibility
               + ", description=" + description + ", elementName=" + elementName + ", instanceID="
               + instanceID + ", limit=" + limit + ", mappingBehavior=" + mappingBehavior
               + ", otherResourceType=" + otherResourceType + ", parent=" + parent + ", poolID="
               + poolID + ", reservation=" + reservation + ", resourceSubType=" + resourceSubType
               + ", resourceType=" + resourceType + ", virtualQuantity=" + virtualQuantity
               + ", virtualQuantityUnits=" + virtualQuantityUnits + ", weight=" + weight + "]";
   }

   public int compareTo(TerremarkResourceAllocation that) {
      final int BEFORE = -1;
      final int EQUAL = 0;
      final int AFTER = 1;

      if (this == that)
         return EQUAL;

      int comparison = this.resourceType.compareTo(that.resourceType);
      if (comparison != EQUAL)
         return comparison;

      if (this.instanceID < that.instanceID)
         return BEFORE;
      if (this.instanceID > that.instanceID)
         return AFTER;
      return EQUAL;
   }

   public String getHostResource() {
      return hostResource;
   }
}