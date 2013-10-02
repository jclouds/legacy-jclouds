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
package org.jclouds.cim;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 
 * The ResourceAllocationSettingData class represents settings specifically
 * related to an allocated resource that are outside the scope of the CIM class
 * typically used to represent the resource itself. These settings include
 * information specific to the allocation that may not be visible to the
 * consumer of the resource itself. For example, a virtual processor may look
 * like a 2 ghz processor to the consumer (virtual computer system), however the
 * virtualization system may use time-slicing to schedule the the virtual
 * processor to only allow it to use 1 ghz.
 * 
 * @author Adrian Cole
 * @see <a href=
 *      "http://dmtf.org/sites/default/files/cim/cim_schema_v2280/cim_schema_2.28.0Final-Doc.zip"
 *      />
 * 
 */
public class ResourceAllocationSettingData extends ManagedElement {

   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return builder().fromResourceAllocationSettingData(this);
   }

   public static class Builder extends ManagedElement.Builder {

      protected String address;
      protected String addressOnParent;
      protected String allocationUnits;
      protected Boolean automaticAllocation;
      protected Boolean automaticDeallocation;
      protected ConsumerVisibility consumerVisibility;
      protected Long limit;
      protected MappingBehavior mappingBehavior;
      protected String otherResourceType;
      protected String parent;
      protected String poolID;
      protected Long reservation;
      protected String resourceSubType;
      protected ResourceType resourceType;
      protected Long virtualQuantity;
      protected String virtualQuantityUnits;
      protected Integer weight;
      protected List<String> connections = Lists.newArrayList();
      protected List<String> hostResources = Lists.newArrayList();

      /**
       * @see ResourceAllocationSettingData#getAddress
       */
      public Builder address(String address) {
         this.address = address;
         return this;
      }

      /**
       * @see ResourceAllocationSettingData#getAddressOnParent
       */
      public Builder addressOnParent(String addressOnParent) {
         this.addressOnParent = addressOnParent;
         return this;
      }

      /**
       * @see ResourceAllocationSettingData#getAllocationUnits
       */
      public Builder allocationUnits(String allocationUnits) {
         this.allocationUnits = allocationUnits;
         return this;
      }

      /**
       * @see ResourceAllocationSettingData#getAutomaticAllocation
       */
      public Builder automaticAllocation(Boolean automaticAllocation) {
         this.automaticAllocation = automaticAllocation;
         return this;
      }

      /**
       * @see ResourceAllocationSettingData#getAutomaticDeallocation
       */
      public Builder automaticDeallocation(Boolean automaticDeallocation) {
         this.automaticDeallocation = automaticDeallocation;
         return this;
      }

      /**
       * @see ResourceAllocationSettingData#getConsumerVisibility
       */
      public Builder consumerVisibility(ConsumerVisibility consumerVisibility) {
         this.consumerVisibility = consumerVisibility;
         return this;
      }

      /**
       * @see ResourceAllocationSettingData#getLimit
       */
      public Builder limit(Long limit) {
         this.limit = limit;
         return this;
      }

      /**
       * @see ResourceAllocationSettingData#getMappingBehavior
       */
      public Builder mappingBehavior(MappingBehavior mappingBehavior) {
         this.mappingBehavior = mappingBehavior;
         return this;
      }

      /**
       * @see ResourceAllocationSettingData#getOtherResourceType
       */
      public Builder otherResourceType(String otherResourceType) {
         this.otherResourceType = otherResourceType;
         return this;
      }

      /**
       * @see ResourceAllocationSettingData#getParent
       */
      public Builder parent(String parent) {
         this.parent = parent;
         return this;
      }

      /**
       * @see ResourceAllocationSettingData#getPoolID
       */
      public Builder poolID(String poolID) {
         this.poolID = poolID;
         return this;
      }

      /**
       * @see ResourceAllocationSettingData#getReservation
       */
      public Builder reservation(Long reservation) {
         this.reservation = reservation;
         return this;
      }

      /**
       * @see ResourceAllocationSettingData#getResourceSubType
       */
      public Builder resourceSubType(String resourceSubType) {
         this.resourceSubType = resourceSubType;
         return this;
      }

      /**
       * @see ResourceAllocationSettingData#getResourceType
       */
      public Builder resourceType(ResourceType resourceType) {
         this.resourceType = resourceType;
         return this;
      }

      /**
       * @see ResourceAllocationSettingData#getVirtualQuantity
       */
      public Builder virtualQuantity(Long virtualQuantity) {
         this.virtualQuantity = virtualQuantity;
         return this;
      }

      /**
       * @see ResourceAllocationSettingData#getVirtualQuantityUnits
       */
      public Builder virtualQuantityUnits(String virtualQuantityUnits) {
         this.virtualQuantityUnits = virtualQuantityUnits;
         return this;
      }

      /**
       * @see ResourceAllocationSettingData#getWeight
       */
      public Builder weight(Integer weight) {
         this.weight = weight;
         return this;
      }

      /**
       * @see ResourceAllocationSettingData#getConnection
       */
      public Builder connection(String connection) {
         this.connections.add(checkNotNull(connection, "connection"));
         return this;
      }

      /**
       * @see ResourceAllocationSettingData#getHostResource
       */
      public Builder hostResource(String hostResource) {
         this.hostResources.add(checkNotNull(hostResource, "hostResource"));
         return this;
      }

      /**
       * @see ResourceAllocationSettingData#getConnections
       */
      public Builder connections(List<String> connections) {
         this.connections.addAll(checkNotNull(connections, "connections"));
         return this;
      }

      /**
       * @see ResourceAllocationSettingData#getHostResources
       */
      public Builder hostResources(List<String> hostResources) {
         this.hostResources.addAll(checkNotNull(hostResources, "hostResources"));
         return this;
      }

      public ResourceAllocationSettingData build() {
         return new ResourceAllocationSettingData(elementName, instanceID, caption, description, address,
               addressOnParent, allocationUnits, automaticAllocation, automaticDeallocation, consumerVisibility, limit,
               mappingBehavior, otherResourceType, parent, poolID, reservation, resourceSubType, resourceType,
               virtualQuantity, virtualQuantityUnits, weight, connections, hostResources);
      }

      public Builder fromResourceAllocationSettingData(ResourceAllocationSettingData in) {
         return fromManagedElement(in).address(in.getAddress()).addressOnParent(in.getAddressOnParent())
               .allocationUnits(in.getAllocationUnits()).automaticAllocation(in.isAutomaticAllocation())
               .automaticDeallocation(in.isAutomaticDeallocation()).consumerVisibility(in.getConsumerVisibility())
               .limit(in.getLimit()).mappingBehavior(in.getMappingBehavior())
               .otherResourceType(in.getOtherResourceType()).parent(in.getParent()).poolID(in.getPoolID())
               .reservation(in.getReservation()).resourceSubType(in.getResourceSubType())
               .resourceType(in.getResourceType()).virtualQuantity(in.getVirtualQuantity())
               .virtualQuantityUnits(in.getVirtualQuantityUnits()).weight(in.getWeight())
               .connections(in.getConnections()).hostResources(in.getHostResources());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromManagedElement(ManagedElement in) {
         return Builder.class.cast(super.fromManagedElement(in));
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

   }

   /**
    * The type of resource this allocation setting represents.
    */
   public static enum ResourceType {

      OTHER(1), COMPUTER_SYSTEM(2), PROCESSOR(3), MEMORY(4), IDE_CONTROLLER(5), PARALLEL_SCSI_HBA(6), FC_HBA(7), ISCSI_HBA(
            8), IB_HCA(9), ETHERNET_ADAPTER(10), OTHER_NETWORK_ADAPTER(11), IO_SLOT(12), IO_DEVICE(13), FLOPPY_DRIVE(14), CD_DRIVE(
            15), DVD_DRIVE(16), DISK_DRIVE(17), TAPE_DRIVE(18), STORAGE_EXTENT(19), OTHER_STORAGE_DEVICE(20), SERIAL_PORT(
            21), PARALLEL_PORT(22), USB_CONTROLLER(23), GRAPHICS_CONTROLLER(24), IEEE_1394_CONTROLLER(25), PARTITIONABLE_UNIT(
            26), BASE_PARTITIONABLE_UNIT(27), POWER(28), COOLING_CAPACITY(29), ETHERNET_SWITCH_PORT(30), LOGICAL_DISK(
            31), STORAGE_VOLUME(32), ETHERNET_CONNECTION(33), DMTF_RESERVED(Integer.valueOf("8000", 16)), VENDOR_RESERVED(
            Integer.valueOf("FFFF", 16));

      protected final int code;

      ResourceType(int code) {
         this.code = code;
      }

      public String value() {
         return code + "";
      }

      protected static final Map<Integer, ResourceType> RESOURCE_TYPE_BY_ID = Maps.uniqueIndex(
            ImmutableSet.copyOf(ResourceType.values()), new Function<ResourceType, Integer>() {

               @Override
               public Integer apply(ResourceType input) {
                  return input.code;
               }

            });

      public static ResourceType fromValue(String type) {
         return RESOURCE_TYPE_BY_ID.get(Integer.valueOf(checkNotNull(type, "type")));
      }
   }

   /**
    * Describes the consumers visibility to the allocated resource.
    */
   public static enum ConsumerVisibility {
      UNKNOWN(0),
      /**
       * indicates the underlying or host resource is utilized and passed
       * through to the consumer, possibly using partitioning. At least one item
       * shall be present in the HostResource property.
       */
      PASSED_THROUGH(2),
      /**
       * indicates the resource is virtualized and may not map directly to an
       * underlying/host resource. Some implementations may support specific
       * assignment for virtualized resources, in which case the host
       * resource(s) are exposed using the HostResource property.
       */
      VIRTUALIZED(3),
      /**
       * indicates a representation of the resource does not exist within the
       * context of the resource consumer.
       */
      NOT_REPRESENTED(4), DMTF_RESERVED(32767), VENDOR_RESERVED(65535);

      protected final int code;

      ConsumerVisibility(int code) {
         this.code = code;
      }

      public String value() {
         return code + "";
      }

      protected static final Map<Integer, ConsumerVisibility> MAPPING_BEHAVIOR_BY_ID = Maps.uniqueIndex(
            ImmutableSet.copyOf(ConsumerVisibility.values()), new Function<ConsumerVisibility, Integer>() {

               @Override
               public Integer apply(ConsumerVisibility input) {
                  return input.code;
               }

            });

      public static ConsumerVisibility fromValue(String behavior) {
         return MAPPING_BEHAVIOR_BY_ID.get(Integer.valueOf(checkNotNull(behavior, "behavior")));
      }
   }

   /**
    * Specifies how this resource maps to underlying resourcesIf the
    * HostResource array contains any entries, this property reflects how the
    * resource maps to those specific resources.
    */
   public static enum MappingBehavior {
      UNKNOWN(0), NOT_SUPPORTED(2), DEDICATED(3), SOFT_AFFINITY(4), HARD_AFFINITY(5), DMTF_RESERVED(32767), VENDOR_RESERVED(
            65535);

      protected final int code;

      MappingBehavior(int code) {
         this.code = code;
      }

      public String value() {
         return code + "";
      }

      protected static final Map<Integer, MappingBehavior> MAPPING_BEHAVIOR_BY_ID = Maps.uniqueIndex(
            ImmutableSet.copyOf(MappingBehavior.values()), new Function<MappingBehavior, Integer>() {

               @Override
               public Integer apply(MappingBehavior input) {
                  return input.code;
               }

            });

      public static MappingBehavior fromValue(String behavior) {
         return MAPPING_BEHAVIOR_BY_ID.get(Integer.valueOf(checkNotNull(behavior, "behavior")));
      }
   }

   protected final String address;
   protected final String addressOnParent;
   protected final String allocationUnits;
   protected final Boolean automaticAllocation;
   protected final Boolean automaticDeallocation;
   protected final ConsumerVisibility consumerVisibility;
   protected final Long limit;
   protected final MappingBehavior mappingBehavior;
   protected final String otherResourceType;
   protected final String parent;
   protected final String poolID;
   protected final Long reservation;
   protected final String resourceSubType;
   protected final ResourceType resourceType;
   protected final Long virtualQuantity;
   protected final String virtualQuantityUnits;
   protected final Integer weight;
   protected final List<String> connections;
   protected final List<String> hostResources;

   public ResourceAllocationSettingData(String elementName, String instanceID, String caption, String description,
         String address, String addressOnParent, String allocationUnits, Boolean automaticAllocation,
         Boolean automaticDeallocation, ConsumerVisibility consumerVisibility, Long limit,
         MappingBehavior mappingBehavior, String otherResourceType, String parent, String poolID, Long reservation,
         String resourceSubType, ResourceType resourceType, Long virtualQuantity, String virtualQuantityUnits,
         Integer weight, List<String> connections, List<String> hostResources) {
      super(elementName, instanceID, caption, description);
      this.address = address;
      this.addressOnParent = addressOnParent;
      this.allocationUnits = allocationUnits;
      this.automaticAllocation = automaticAllocation;
      this.automaticDeallocation = automaticDeallocation;
      this.consumerVisibility = consumerVisibility;
      this.limit = limit;
      this.mappingBehavior = mappingBehavior;
      this.otherResourceType = otherResourceType;
      this.parent = parent;
      this.poolID = poolID;
      this.reservation = reservation;
      this.resourceSubType = resourceSubType;
      this.resourceType = resourceType;
      this.virtualQuantity = virtualQuantity;
      this.virtualQuantityUnits = virtualQuantityUnits;
      this.weight = weight;
      this.connections = ImmutableList.copyOf(connections);
      this.hostResources = ImmutableList.copyOf(hostResources);
   }

   /**
    * The address of the resource. For example, the MAC address of a Ethernet
    * port.
    */
   public String getAddress() {
      return address;
   }

   /**
    * Describes the address of this resource in the context of the Parent. The
    * Parent/AddressOnParent properties are used to describe the controller
    * relationship as well the ordering of devices on a controller.For example,
    * if the parent is a PCI Controller, this property would specify the PCI
    * slot of this child device.
    */
   public String getAddressOnParent() {
      return addressOnParent;
   }

   /**
    * This property specifies the units of allocation used by the Reservation
    * and Limit properties. For example, when ResourceType=Processor,
    * AllocationUnits may be set to hertz*10^6 or percent. When
    * ResourceType=Memory, AllocationUnits may be set to bytes*10^3. It is
    * expected that profiles constrain the units that apply in context of
    * particular resource types. The value of this property shall be a legal
    * value of the Programmatic Units qualifier as defined in Annex C.1 of
    * DSP0004 V2.5 or later.
    */
   public String getAllocationUnits() {
      return allocationUnits;
   }

   /**
    * This property specifies if the resource will be automatically allocated.
    * For example when set to true, when the consuming virtual computer system
    * is powered on, this resource would be allocated. A value of false
    * indicates the resource must be explicitly allocated. For example, the
    * setting may represent removable media (cdrom, floppy, etc.) where at power
    * on time, the media is not present. An explicit operation is required to
    * allocate the resource.
    */
   public Boolean isAutomaticAllocation() {
      return automaticAllocation;
   }

   /**
    * This property specifies if the resource will be automatically
    * de-allocated. For example, when set to true, when the consuming virtual
    * computer system is powered off, this resource would be de-allocated. When
    * set to false, the resource will remain allocated and must be explicitly
    * de-allocated.
    */
   public Boolean isAutomaticDeallocation() {
      return automaticDeallocation;
   }

   /**
    * Describes the consumers visibility to the allocated resource.
    */
   public ConsumerVisibility getConsumerVisibility() {
      return consumerVisibility;
   }

   /**
    * This property specifies the upper bound, or maximum amount of resource
    * that will be granted for this allocation. For example, a system which
    * supports memory paging may support setting the Limit of a Memory
    * allocation below that of the VirtualQuantity, thus forcing paging to occur
    * for this allocation. The value of the Limit property is expressed in the
    * unit specified by the value of the AllocationUnits property.
    */
   public Long getLimit() {
      return limit;
   }

   /**
    * Specifies how this resource maps to underlying resourcesIf the
    * HostResource array contains any entries, this property reflects how the
    * resource maps to those specific resources.
    */
   public MappingBehavior getMappingBehavior() {
      return mappingBehavior;
   }

   /**
    * A string that describes the resource type when a well defined value is not
    * available and ResourceType has the value "Other".
    */
   public String getOtherResourceType() {
      return otherResourceType;
   }

   /**
    * The Parent of the resource. For example, a controller for the current
    * allocation
    */
   public String getParent() {
      return parent;
   }

   /**
    * This property specifies which ResourcePool the resource is currently
    * allocated from, or which ResourcePool the resource will be allocated from
    * when the allocation occurs.
    */
   public String getPoolID() {
      return poolID;
   }

   /**
    * This property specifies the amount of resource guaranteed to be available
    * for this allocation. On system which support over-commitment of resources,
    * this value is typically used for admission control to prevent an an
    * allocation from being accepted thus preventing starvation. The value of
    * the Reservation property is expressed in the unit specified by the value
    * of the AllocationUnits property.
    */
   public Long getReservation() {
      return reservation;
   }

   /**
    * A string describing an implementation specific sub-type for this resource.
    * F
    */
   public String getResourceSubType() {
      return resourceSubType;
   }

   /**
    * The type of resource this allocation setting represents.
    */
   public ResourceType getResourceType() {
      return resourceType;
   }

   /**
    * This property specifies the quantity of resources presented to the
    * consumer. For example, when ResourceType=Processor, this property would
    * reflect the number of discrete Processors presented to the virtual
    * computer system. When ResourceType=Memory, this property could reflect the
    * number of MB reported to the virtual computer system. The value of the
    * VirtualQuantity property should be expressed in units as defined by the
    * value of the VirtualQuantityUnits property.
    */
   public Long getVirtualQuantity() {
      return virtualQuantity;
   }

   /**
    * This property specifies the units used by the VirtualQuantity property.
    * For example - if ResourceType=Processor, the value of the
    * VirtualQuantityUnits property may be set to "count", indicating that the
    * value of the VirtualQuantity property is expressed as a count. - if
    * ResourceType=Memory, the value of the VirtualQuantityUnits property may be
    * set to "bytes*10^3", indicating that the value of the VirtualQuantity
    * property is expressed in kilobyte. It is expected that profiles constrain
    * the units that apply in context of particular resource types. The value of
    * this property shall be a legal value of the Programmatic Units qualifier
    * as defined in Annex C.1 of DSP0004 V2.5 or later.
    */
   public String getVirtualQuantityUnits() {
      return virtualQuantityUnits;
   }

   /**
    * This property specifies a relative priority for this allocation in
    * relation to other allocations from the same ResourcePool. This property
    * has no unit of measure, and is only relevant when compared to other
    * allocations vying for the same host resources.
    */
   public Integer getWeight() {
      return weight;
   }

   /**
    * The thing to which this resource is connected. For example, a named
    * network or switch port.
    */
   public List<String> getConnections() {
      return connections;
   }

   /**
    * This property exposes specific assignment of resources. Each non-null
    * value of the HostResource property shall be formatted as a URI per RFC3986.
    * If this resource is modeled then a value should be a WBEM URI (DSP0207).
    * If the resource is not modeled then see the appropriate profile. Profiles
    * may further constrain the type of URI. A NULL value or empty array
    * requests the implementation decide the kind of host resource. If the
    * virtual resource is mapped to more than one underlying resource, this
    * property may be left NULL. If NULL, the DeviceAllocatedFromPool or
    * ResourceAllocationFromPool associations may be used to determine the pool
    * of host resources this virtual resource may use. If specific assignment is
    * utilized, all underlying resources used by this virtual resource should be
    * listed.The kind of dependency is specified by the ConsumerVisibility and
    * the MappingBehavior properties. Typically the array contains one item,
    * however multiple host resources may be specified. A client may set the
    * value(s) to indicate that the requested virtual resource allocation be
    * based on host resources that are identified by element values.
    */
   public List<String> getHostResources() {
      return hostResources;
   }

   @Override
   public String toString() {
      return String
            .format(
                  "[elementName=%s, instanceID=%s, caption=%s, description=%s, address=%s, addressOnParent=%s, allocationUnits=%s, automaticAllocation=%s, automaticDeallocation=%s, connections=%s, consumerVisibility=%s, hostResources=%s, limit=%s, mappingBehavior=%s, otherResourceType=%s, parent=%s, poolID=%s, reservation=%s, resourceSubType=%s, resourceType=%s, virtualQuantity=%s, virtualQuantityUnits=%s, weight=%s]",
                  elementName, instanceID, caption, description, address, addressOnParent, allocationUnits,
                  automaticAllocation, automaticDeallocation, connections, consumerVisibility, hostResources, limit,
                  mappingBehavior, otherResourceType, parent, poolID, reservation, resourceSubType, resourceType,
                  virtualQuantity, virtualQuantityUnits, weight);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((address == null) ? 0 : address.hashCode());
      result = prime * result + ((addressOnParent == null) ? 0 : addressOnParent.hashCode());
      result = prime * result + ((resourceSubType == null) ? 0 : resourceSubType.hashCode());
      result = prime * result + ((resourceType == null) ? 0 : resourceType.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      ResourceAllocationSettingData other = (ResourceAllocationSettingData) obj;
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
      return true;
   }

}
