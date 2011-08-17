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
package org.jclouds.vcloud.domain.ovf;

import java.util.List;

import org.jclouds.cim.ResourceAllocationSettingData;

/**
 * @author Adrian Cole
 * 
 */
public class VCloudHardDisk extends ResourceAllocationSettingData {

   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return builder().fromVCloudHardDisk(this);
   }

   public static class Builder extends ResourceAllocationSettingData.Builder {
      private long capacity;
      private int busType;
      private String busSubType;

      /**
       * @see VCloudHardDisk#getCapacity
       */
      public Builder capacity(long capacity) {
         this.capacity = capacity;
         return this;
      }

      /**
       * @see VCloudHardDisk#getBusType
       */
      public Builder busType(int busType) {
         this.busType = busType;
         return this;
      }

      /**
       * @see VCloudHardDisk#getBusSubType
       */
      public Builder busSubType(String busSubType) {
         this.busSubType = busSubType;
         return this;
      }

      public VCloudHardDisk build() {
         return new VCloudHardDisk(elementName, instanceID, caption, description, address, addressOnParent,
               allocationUnits, automaticAllocation, automaticDeallocation, consumerVisibility, limit, mappingBehavior,
               otherResourceType, parent, poolID, reservation, resourceSubType, resourceType, virtualQuantity,
               virtualQuantityUnits, weight, connections, hostResources, capacity, busType, busSubType);
      }

      public Builder fromVCloudHardDisk(VCloudHardDisk in) {
         return capacity(in.getCapacity()).busType(in.getBusType()).busSubType(in.getBusSubType())
               .fromResourceAllocationSettingData(in);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder caption(String caption) {
         return Builder.class.cast(super.caption(caption));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder description(String description) {
         return Builder.class.cast(super.description(description));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder elementName(String elementName) {
         return Builder.class.cast(super.elementName(elementName));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder instanceID(String instanceID) {
         return Builder.class.cast(super.instanceID(instanceID));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder address(String address) {
         return Builder.class.cast(super.address(address));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder addressOnParent(String addressOnParent) {
         return Builder.class.cast(super.addressOnParent(addressOnParent));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder allocationUnits(String allocationUnits) {
         return Builder.class.cast(super.allocationUnits(allocationUnits));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder automaticAllocation(Boolean automaticAllocation) {
         return Builder.class.cast(super.automaticAllocation(automaticAllocation));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder automaticDeallocation(Boolean automaticDeallocation) {
         return Builder.class.cast(super.automaticDeallocation(automaticDeallocation));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder connection(String connection) {
         return Builder.class.cast(super.connection(connection));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder connections(List<String> connections) {
         return Builder.class.cast(super.connections(connections));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder consumerVisibility(ConsumerVisibility consumerVisibility) {
         return Builder.class.cast(super.consumerVisibility(consumerVisibility));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder hostResource(String hostResource) {
         return Builder.class.cast(super.hostResource(hostResource));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder hostResources(List<String> hostResources) {
         return Builder.class.cast(super.hostResources(hostResources));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder limit(Long limit) {
         return Builder.class.cast(super.limit(limit));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder mappingBehavior(MappingBehavior mappingBehavior) {
         return Builder.class.cast(super.mappingBehavior(mappingBehavior));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder otherResourceType(String otherResourceType) {
         return Builder.class.cast(super.otherResourceType(otherResourceType));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder parent(String parent) {
         return Builder.class.cast(super.parent(parent));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder poolID(String poolID) {
         return Builder.class.cast(super.poolID(poolID));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder reservation(Long reservation) {
         return Builder.class.cast(super.reservation(reservation));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder resourceSubType(String resourceSubType) {
         return Builder.class.cast(super.resourceSubType(resourceSubType));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder resourceType(org.jclouds.cim.ResourceAllocationSettingData.ResourceType resourceType) {
         return Builder.class.cast(super.resourceType(resourceType));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder virtualQuantity(Long virtualQuantity) {
         return Builder.class.cast(super.virtualQuantity(virtualQuantity));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder virtualQuantityUnits(String virtualQuantityUnits) {
         return Builder.class.cast(super.virtualQuantityUnits(virtualQuantityUnits));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder weight(Integer weight) {
         return Builder.class.cast(super.weight(weight));
      }

      @Override
      public Builder fromResourceAllocationSettingData(ResourceAllocationSettingData in) {
         return Builder.class.cast(super.fromResourceAllocationSettingData(in));
      }
   }

   private final long capacity;
   private final int busType;
   private final String busSubType;

   public VCloudHardDisk(String elementName, String instanceID, String caption, String description, String address,
         String addressOnParent, String allocationUnits, Boolean automaticAllocation, Boolean automaticDeallocation,
         ConsumerVisibility consumerVisibility, Long limit, MappingBehavior mappingBehavior, String otherResourceType,
         String parent, String poolID, Long reservation, String resourceSubType,
         org.jclouds.cim.ResourceAllocationSettingData.ResourceType resourceType, Long virtualQuantity,
         String virtualQuantityUnits, Integer weight, List<String> connections, List<String> hostResources,
         long capacity, int busType, String busSubType) {
      super(elementName, instanceID, caption, description, address, addressOnParent, allocationUnits,
            automaticAllocation, automaticDeallocation, consumerVisibility, limit, mappingBehavior, otherResourceType,
            parent, poolID, reservation, resourceSubType, resourceType, virtualQuantity, virtualQuantityUnits, weight,
            connections, hostResources);
      this.capacity = capacity;
      this.busType = busType;
      this.busSubType = busSubType;
   }

   public long getCapacity() {
      return capacity;
   }

   public int getBusType() {
      return busType;
   }

   public String getBusSubType() {
      return busSubType;
   }

   @Override
   public String toString() {
      return String
            .format(
                  "[elementName=%s, instanceID=%s, caption=%s, description=%s, address=%s, addressOnParent=%s, allocationUnits=%s, automaticAllocation=%s, automaticDeallocation=%s, connections=%s, consumerVisibility=%s, hostResources=%s, limit=%s, mappingBehavior=%s, otherResourceType=%s, parent=%s, poolID=%s, reservation=%s, resourceSubType=%s, resourceType=%s, virtualQuantity=%s, virtualQuantityUnits=%s, weight=%s, busSubType=%s, busType=%s, capacity=%s]",
                  elementName, instanceID, caption, description, address, addressOnParent, allocationUnits,
                  automaticAllocation, automaticDeallocation, connections, consumerVisibility, hostResources, limit,
                  mappingBehavior, otherResourceType, parent, poolID, reservation, resourceSubType, resourceType,
                  virtualQuantity, virtualQuantityUnits, weight, busSubType, busType, capacity);
   }
}