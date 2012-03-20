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
package org.jclouds.vcloud.director.v1_5.domain.cim;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.VCLOUD_1_5_NS;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.VCLOUD_CIM_RASD_NS;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.VCLOUD_OVF_NS;

import java.math.BigInteger;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.vcloud.director.v1_5.domain.Link;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * The ResourceAllocationSettingData class represents settings specifically
 * related to an allocated resource that are outside the scope of the CIM class
 * typically used to represent the resource itself.
 *
 * These settings include information specific to the allocation that may not
 * be visible to the consumer of the resource itself. For example, a virtual
 * processor may look like a 2 GHz processor to the consumer (virtual computer
 * system), however the virtualization system may use time-slicing to schedule
 * the the virtual processor to only allow it to use 1 GHz.
 * 
 * @author Adrian Cole
 * @author grkvlt@apache.org
 * @see http://schemas.dmtf.org/wbem/wscim/1/cim-schema/2.22.0/CIM_ResourceAllocationSettingData.xsd
 */
@XmlRootElement(name = "Item", namespace = VCLOUD_1_5_NS)
@XmlType(name = "CIM_ResourceAllocationSettingData_Type", namespace = VCLOUD_OVF_NS,
   propOrder = {
      "address",
      "addressOnParent",
      "allocationUnits",
      "automaticAllocation",
      "automaticDeallocation",
      "caption",
      "connections",
      "consumerVisibility",
      "description",
      "elementName",
      "hostResources",
      "instanceID",
      "limit",
      "mappingBehavior",
      "otherResourceType",
      "parent",
      "poolID",
      "reservation",
      "resourceSubType",
      "resourceType",
      "virtualQuantity",
      "virtualQuantityUnits",
      "weight",
      "links"
   }
)
public class ResourceAllocationSettingData {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromResourceAllocationSettingData(this);
   }

   public static class Builder {

      private String elementName;
      private String instanceID;
      private String caption;
      private String description;
      private String address;
      private String addressOnParent;
      private String allocationUnits;
      private Boolean automaticAllocation;
      private Boolean automaticDeallocation;
      private ConsumerVisibility consumerVisibility;
      private BigInteger limit;
      private MappingBehavior mappingBehavior;
      private String otherResourceType;
      private String parent;
      private String poolID;
      private BigInteger reservation;
      private String resourceSubType;
      private ResourceType resourceType;
      private BigInteger virtualQuantity;
      private String virtualQuantityUnits;
      private Long weight;
      private List<CimString> connections = Lists.newArrayList();
      private List<CimString> hostResources = Lists.newArrayList();
      private URI href;
      private String type;
      private Set<Link> links = Sets.newLinkedHashSet();

      /**
       * @see ResourceAllocationSettingData#getElementName()
       */
      public Builder elementName(String elementName) {
         this.elementName = elementName;
         return this;
      }

      /**
       *@see ResourceAllocationSettingData#getInstanceId()
       */
      public Builder instanceID(String instanceID) {
         this.instanceID = instanceID;
         return this;
      }

      /**
       * @see ResourceAllocationSettingData#getCaption()
       */
      public Builder caption(String caption) {
         this.caption = caption;
         return this;
      }

      /**
       * @see ResourceAllocationSettingData#getDescription()
       */
      public Builder description(String description) {
         this.description = description;
         return this;
      }

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
       * @see ResourceAllocationSettingData#isAutomaticAllocation()
       */
      public Builder automaticAllocation(Boolean automaticAllocation) {
         this.automaticAllocation = automaticAllocation;
         return this;
      }

      /**
       * @see ResourceAllocationSettingData#isAutomaticDeallocation()
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
      public Builder limit(BigInteger limit) {
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
      public Builder reservation(BigInteger reservation) {
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
      public Builder virtualQuantity(BigInteger virtualQuantity) {
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
      public Builder weight(Long weight) {
         this.weight = weight;
         return this;
      }

      /**
       * @see ResourceAllocationSettingData#getConnections()
       */
      public Builder connection(CimString connection) {
         this.connections.add(checkNotNull(connection, "connection"));
         return this;
      }

      /**
       * @see ResourceAllocationSettingData#getConnections
       */
      public Builder connections(List<CimString> connections) {
         this.connections = Lists.newArrayList(checkNotNull(connections, "connections"));
         return this;
      }

      /**
       * @see ResourceAllocationSettingData#getHostResources()
       */
      public Builder hostResource(CimString hostResource) {
         this.hostResources.add(checkNotNull(hostResource, "hostResource"));
         return this;
      }

      /**
       * @see ResourceAllocationSettingData#getHostResources
       */
      public Builder hostResources(List<CimString> hostResources) {
         this.hostResources = Lists.newArrayList(checkNotNull(hostResources, "hostResources"));
         return this;
      }

      /**
       * @see ResourceAllocationSettingData#getType()
       */
      public Builder type(String type) {
         this.type = type;
         return this;
      }

      /**
       * @see ResourceAllocationSettingData#getHref()
       */
      public Builder href(URI href) {
         this.href = href;
         return this;
      }

      /**
       * @see ResourceAllocationSettingData#getLinks()
       */
      public Builder links(Set<Link> links) {
         this.links = Sets.newLinkedHashSet(checkNotNull(links, "links"));
         return this;
      }

      /**
       * @see ResourceAllocationSettingData#getLinks()
       */
      public Builder link(Link link) {
         if (links == null)
            links = Sets.newLinkedHashSet();
         this.links.add(checkNotNull(link, "link"));
         return this;
      }

      public ResourceAllocationSettingData build() {
         return new ResourceAllocationSettingData(elementName, instanceID, caption, description, address,
               addressOnParent, allocationUnits, automaticAllocation, automaticDeallocation, consumerVisibility, limit,
               mappingBehavior, otherResourceType, parent, poolID, reservation, resourceSubType, resourceType,
               virtualQuantity, virtualQuantityUnits, weight, connections, hostResources, type, href, links);
      }

      public Builder fromResourceAllocationSettingData(ResourceAllocationSettingData in) {
         return elementName(in.getElementName())
               .instanceID(in.getInstanceID())
               .caption(in.getCaption())
               .description(in.getDescription())
               .address(in.getAddress())
               .addressOnParent(in.getAddressOnParent())
               .allocationUnits(in.getAllocationUnits())
               .automaticAllocation(in.isAutomaticAllocation())
               .automaticDeallocation(in.isAutomaticDeallocation())
               .consumerVisibility(in.getConsumerVisibility())
               .limit(in.getLimit())
               .mappingBehavior(in.getMappingBehavior())
               .otherResourceType(in.getOtherResourceType())
               .parent(in.getParent())
               .poolID(in.getPoolID())
               .reservation(in.getReservation())
               .resourceSubType(in.getResourceSubType())
               .resourceType(in.getResourceType())
               .virtualQuantity(in.getVirtualQuantity())
               .virtualQuantityUnits(in.getVirtualQuantityUnits())
               .weight(in.getWeight())
               .connections(in.getConnections())
               .hostResources(in.getHostResources())
               .type(in.getType())
               .href(in.getHref())
               .links(Sets.newLinkedHashSet(in.getLinks()));
      }
   }

   /**
    * The type of resource this allocation setting represents.
    */
   @XmlType
   @XmlEnum(Integer.class)
   public static enum ResourceType {
      @XmlEnumValue("1") OTHER(1),
      @XmlEnumValue("2") COMPUTER_SYSTEM(2),
      @XmlEnumValue("3") PROCESSOR(3),
      @XmlEnumValue("4") MEMORY(4),
      @XmlEnumValue("5") IDE_CONTROLLER(5),
      @XmlEnumValue("6") PARALLEL_SCSI_HBA(6),
      @XmlEnumValue("7") FC_HBA(7),
      @XmlEnumValue("8") ISCSI_HBA(8),
      @XmlEnumValue("9") IB_HCA(9),
      @XmlEnumValue("10") ETHERNET_ADAPTER(10),
      @XmlEnumValue("11") OTHER_NETWORK_ADAPTER(11),
      @XmlEnumValue("12") IO_SLOT(12),
      @XmlEnumValue("13") IO_DEVICE(13),
      @XmlEnumValue("14") FLOPPY_DRIVE(14),
      @XmlEnumValue("15") CD_DRIVE(15),
      @XmlEnumValue("16") DVD_DRIVE(16),
      @XmlEnumValue("17") DISK_DRIVE(17),
      @XmlEnumValue("18") TAPE_DRIVE(18),
      @XmlEnumValue("19") STORAGE_EXTENT(19),
      @XmlEnumValue("20") OTHER_STORAGE_DEVICE(20),
      @XmlEnumValue("21") SERIAL_PORT(21),
      @XmlEnumValue("22") PARALLEL_PORT(22),
      @XmlEnumValue("23") USB_CONTROLLER(23),
      @XmlEnumValue("24") GRAPHICS_CONTROLLER(24),
      @XmlEnumValue("25") IEEE_1394_CONTROLLER(25),
      @XmlEnumValue("26") PARTITIONABLE_UNIT(26),
      @XmlEnumValue("27") BASE_PARTITIONABLE_UNIT(27),
      @XmlEnumValue("28") POWER(28),
      @XmlEnumValue("29") COOLING_CAPACITY(29),
      @XmlEnumValue("30") ETHERNET_SWITCH_PORT(30),
      @XmlEnumValue("31") LOGICAL_DISK(31),
      @XmlEnumValue("32") STORAGE_VOLUME(32),
      @XmlEnumValue("33") ETHERNET_CONNECTION(33),
      @XmlEnumValue("32768") DMTF_RESERVED(Integer.valueOf("8000", 16)),
      @XmlEnumValue("65535") VENDOR_RESERVED(Integer.valueOf("FFFF", 16));

      protected final int code;

      ResourceType(int code) {
         this.code = code;
      }

      public String value() {
         return Integer.toString(code);
      }

      protected final static Map<Integer, ResourceType> RESOURCE_TYPE_BY_ID = Maps.uniqueIndex(
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
   @XmlType
   @XmlEnum(Integer.class)
   public static enum ConsumerVisibility {
      @XmlEnumValue("0") UNKNOWN(0),

      /**
       * indicates the underlying or host resource is utilized and passed
       * through to the consumer, possibly using partitioning. At least one item
       * shall be present in the HostResource property.
       */
      @XmlEnumValue("2") PASSED_THROUGH(2),

      /**
       * indicates the resource is virtualized and may not map directly to an
       * underlying/host resource. Some implementations may support specific
       * assignment for virtualized resources, in which case the host
       * resource(s) are exposed using the HostResource property.
       */
      @XmlEnumValue("3") VIRTUALIZED(3),

      /**
       * indicates a representation of the resource does not exist within the
       * context of the resource consumer.
       */
      @XmlEnumValue("4") NOT_REPRESENTED(4),
      @XmlEnumValue("32768") DMTF_RESERVED(Integer.valueOf("8000", 16)),
		@XmlEnumValue("65535") VENDOR_RESERVED(Integer.valueOf("FFFF", 16));

      protected final int code;

      ConsumerVisibility(int code) {
         this.code = code;
      }

      public String value() {
         return Integer.toString(code);
      }

      protected final static Map<Integer, ConsumerVisibility> MAPPING_BEHAVIOR_BY_ID = Maps.uniqueIndex(
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
   @XmlType
   @XmlEnum(Integer.class)
   public static enum MappingBehavior {
      @XmlEnumValue("0") UNKNOWN(0),
      @XmlEnumValue("2") NOT_SUPPORTED(2),
      @XmlEnumValue("3") DEDICATED(3),
      @XmlEnumValue("4") SOFT_AFFINITY(4),
      @XmlEnumValue("5") HARD_AFFINITY(5),
      @XmlEnumValue("32768") DMTF_RESERVED(Integer.valueOf("8000", 16)),
		@XmlEnumValue("65535") VENDOR_RESERVED(Integer.valueOf("FFFF", 16));

      protected final int code;

      MappingBehavior(int code) {
         this.code = code;
      }

      public String value() {
         return Integer.toString(code);
      }

      protected final static Map<Integer, MappingBehavior> MAPPING_BEHAVIOR_BY_ID = Maps.uniqueIndex(
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
   
   // <rasd:Elementname>foo</rasd:ElementName>

   @XmlElement(name = "ElementName", namespace = VCLOUD_CIM_RASD_NS)
   private String elementName;
   @XmlElement(name = "InstanceID", namespace = VCLOUD_CIM_RASD_NS)
   private String instanceID;
   @XmlElement(name = "Caption", namespace = VCLOUD_CIM_RASD_NS)
   private String caption;
   @XmlElement(name = "Description", namespace = VCLOUD_CIM_RASD_NS)
   private String description;
   @XmlElement(name = "Address", namespace = VCLOUD_CIM_RASD_NS)
   private String address;
   @XmlElement(name = "AddressOnParent", namespace = VCLOUD_CIM_RASD_NS)
   private String addressOnParent;
   @XmlElement(name = "AllocationUnits", namespace = VCLOUD_CIM_RASD_NS)
   private String allocationUnits;
   @XmlElement(name = "AutomaticAllocation", namespace = VCLOUD_CIM_RASD_NS)
   private Boolean automaticAllocation;
   @XmlElement(name = "AutomaticDeallocation", namespace = VCLOUD_CIM_RASD_NS)
   private Boolean automaticDeallocation;
   @XmlElement(name = "ConsumerVisibility", namespace = VCLOUD_CIM_RASD_NS)
   private ConsumerVisibility consumerVisibility;
   @XmlElement(name = "Limit", namespace = VCLOUD_CIM_RASD_NS)
   private BigInteger limit;
   @XmlElement(name = "MappingBehavior", namespace = VCLOUD_CIM_RASD_NS)
   private MappingBehavior mappingBehavior;
   @XmlElement(name = "OtherResourceType", namespace = VCLOUD_CIM_RASD_NS)
   private String otherResourceType;
   @XmlElement(name = "Parent", namespace = VCLOUD_CIM_RASD_NS)
   private String parent;
   @XmlElement(name = "PoolID", namespace = VCLOUD_CIM_RASD_NS)
   private String poolID;
   @XmlElement(name = "Reservation", namespace = VCLOUD_CIM_RASD_NS)
   private BigInteger reservation;
   @XmlElement(name = "ResourceSubType", namespace = VCLOUD_CIM_RASD_NS)
   private String resourceSubType;
   @XmlElement(name = "ResourceType", namespace = VCLOUD_CIM_RASD_NS)
   private ResourceType resourceType;
   @XmlElement(name = "VirtualQuantity", namespace = VCLOUD_CIM_RASD_NS)
   private BigInteger virtualQuantity;
   @XmlElement(name = "VirtualQuantityUnits", namespace = VCLOUD_CIM_RASD_NS)
   private String virtualQuantityUnits;
   @XmlElement(name = "Weight", namespace = VCLOUD_CIM_RASD_NS)
   private Long weight;
   @XmlElement(name = "Connection", namespace = VCLOUD_CIM_RASD_NS)
   private List<CimString> connections = Lists.newArrayList();
   @XmlElement(name = "HostResource", namespace = VCLOUD_CIM_RASD_NS)
   private List<CimString> hostResources = Lists.newArrayList();
   @XmlAttribute(name = "type", namespace = VCLOUD_1_5_NS)
   private String type;
   @XmlAttribute(name = "href", namespace = VCLOUD_1_5_NS)
   private URI href;
   @XmlElement(name = "Link", namespace = VCLOUD_1_5_NS)
   private Set<Link> links = Sets.newLinkedHashSet();

   private ResourceAllocationSettingData(String elementName, String instanceID, String caption, String description,
         String address, String addressOnParent, String allocationUnits, Boolean automaticAllocation,
         Boolean automaticDeallocation, ConsumerVisibility consumerVisibility, BigInteger limit,
         MappingBehavior mappingBehavior, String otherResourceType, String parent, String poolID, BigInteger reservation,
         String resourceSubType, ResourceType resourceType, BigInteger virtualQuantity, String virtualQuantityUnits,
         Long weight, List<CimString> connections, List<CimString> hostResources, String type, URI href, Set<Link> links) {
      this.elementName = elementName;
      this.instanceID = instanceID;
      this.caption = caption;
      this.description = description;
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
      this.type = type;
      this.href = href;
      this.links = links != null ? ImmutableSet.copyOf(links) : Collections.<Link>emptySet();
   }

   private ResourceAllocationSettingData() {
      // for JAXB
   }

   /**
    * The user-friendly name for this instance of SettingData. In addition, the user-friendly name
    * can be used as an index property for a search or query. (Note: The name does not have to be
    * unique within a namespace.)
    */
   public String getElementName() {
      return elementName;
   }

   /**
    * Within the scope of the instantiating Namespace, InstanceID opaquely and uniquely identifies
    * an instance of this class.
    */
   public String getInstanceID() {
      return instanceID;
   }

   /**
    * The Caption property is a short textual description (one- line string) of the object.
    */
   public String getCaption() {
      return caption;
   }

   /**
    * The Description property provides a textual description of the object.
    */
   public String getDescription() {
      return description;
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
   public BigInteger getLimit() {
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
   public BigInteger getReservation() {
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
   public BigInteger getVirtualQuantity() {
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
   public Long getWeight() {
      return weight;
   }

   /**
    * The thing to which this resource is connected. For example, a named
    * network or switch port.
    */
   public List<CimString> getConnections() {
      return Collections.unmodifiableList(connections);
   }

   /**
    * This property exposes specific assignment of resources. Each non-null
    * value of the HostResource property shall be formated as a URI per RFC3986.
    * If this resource is modeled then a value should be a WBEM URI (DSP0207).
    * If the resource is not modeled then see the appropriate profile. Profiles
    * may further constrain the type of URI. A NULL value or empty array
    * requests the implementation decide the kind of host resource. If the
    * virtual resource is mapped to more than oneunderlying resource, this
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
   public List<CimString> getHostResources() {
      return Collections.unmodifiableList(hostResources);
   }

   public String getType() {
      return type;
   }

   public URI getHref() {
      return href;
   }

   /**
    * Set of optional links to an entity or operation associated with this object.
    */
   public Set<Link> getLinks() {
      return links != null ? ImmutableSet.copyOf(links) : Collections.<Link>emptySet();
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("elementname", elementName)
            .add("instanceId", instanceID)
            .add("caption", caption)
            .add("description", description)
            .add("address", address)
            .add("addressOnParent", addressOnParent)
            .add("allocationUnits", allocationUnits)
            .add("automaticAllocation", automaticAllocation)
            .add("automaticDeallocation", automaticDeallocation)
            .add("connections", connections)
            .add("consumerVisibility", consumerVisibility)
            .add("hostResources", hostResources)
            .add("limit", limit)
            .add("mappingBehavior", mappingBehavior)
            .add("otherResourceType", otherResourceType)
            .add("parent", parent)
            .add("poolID", poolID)
            .add("reservation", reservation)
            .add("resourceSubType", resourceSubType)
            .add("resourceType", resourceType)
            .add("virtualQuantity", virtualQuantity)
            .add("virtualQuantityUnits", virtualQuantityUnits)
            .add("weight", weight)
            .add("type", type)
            .add("href", href)
            .add("links", links)
            .toString();
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(elementName, instanceID, caption, description,
            address, addressOnParent, allocationUnits,
            automaticAllocation, automaticDeallocation, connections,
            consumerVisibility, hostResources, limit, mappingBehavior,
            otherResourceType, parent, poolID, reservation, resourceSubType,
            resourceType, virtualQuantity, virtualQuantityUnits, weight, type, href, links);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      ResourceAllocationSettingData that = ResourceAllocationSettingData.class.cast(obj);
      return equal(this.elementName, that.elementName) &&
            equal(this.instanceID, that.instanceID) &&
            equal(this.caption, that.caption) &&
            equal(this.description, that.description) &&
            equal(this.address, that.address) &&
            equal(this.addressOnParent, that.addressOnParent) &&
            equal(this.allocationUnits, that.allocationUnits) &&
            equal(this.automaticAllocation, that.automaticAllocation) &&
            equal(this.automaticDeallocation, that.automaticDeallocation) &&
            equal(this.connections, that.connections) &&
            equal(this.consumerVisibility, that.consumerVisibility) &&
            equal(this.hostResources, that.hostResources) &&
            equal(this.limit, that.limit) &&
            equal(this.mappingBehavior, that.mappingBehavior) &&
            equal(this.otherResourceType, that.otherResourceType) &&
            equal(this.parent, that.parent) &&
            equal(this.poolID, that.poolID) &&
            equal(this.reservation, that.reservation) &&
            equal(this.resourceSubType, that.resourceSubType) &&
            equal(this.resourceType, that.resourceType) &&
            equal(this.virtualQuantity, that.virtualQuantity) &&
            equal(this.virtualQuantityUnits, that.virtualQuantityUnits) &&
            equal(this.weight, that.weight) &&
            equal(this.type, that.type) &&
            equal(this.href, that.href) &&
            equal(this.links, that.links);
   }

}