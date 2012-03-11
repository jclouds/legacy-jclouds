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

package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;


/**
 * Represents a virtual data center (vDC).
 * <p/>
 * <p/>
 * <p>Java class for Vdc complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="Vdc">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}EntityType">
 *       &lt;sequence>
 *         &lt;element name="AllocationModel" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="StorageCapacity" type="{http://www.vmware.com/vcloud/v1.5}CapacityWithUsageType"/>
 *         &lt;element name="ComputeCapacity" type="{http://www.vmware.com/vcloud/v1.5}ComputeCapacityType"/>
 *         &lt;element name="ResourceEntities" type="{http://www.vmware.com/vcloud/v1.5}ResourceEntitiesType" minOccurs="0"/>
 *         &lt;element name="AvailableNetworks" type="{http://www.vmware.com/vcloud/v1.5}AvailableNetworksType" minOccurs="0"/>
 *         &lt;element name="Capabilities" type="{http://www.vmware.com/vcloud/v1.5}CapabilitiesType" minOccurs="0"/>
 *         &lt;element name="NicQuota" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="NetworkQuota" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="VmQuota" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="IsEnabled" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="status" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement(name = "Vdc")
@XmlType(propOrder = {
      "allocationModel",
      "storageCapacity",
      "computeCapacity",
      "resourceEntities",
      "availableNetworks",
      "capabilities",
      "nicQuota",
      "networkQuota",
      "vmQuota",
      "isEnabled"
})
@XmlSeeAlso({
//    AdminVdc.class
})
public class Vdc
      extends EntityType<Vdc>

{
   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromVdc(this);
   }

   public static class Builder extends EntityType.Builder<Vdc> {

      private String allocationModel;
      private CapacityWithUsage storageCapacity;
      private ComputeCapacity computeCapacity;
      private ResourceEntities resourceEntities;
      private AvailableNetworks availableNetworks;
      private Capabilities capabilities;
      private int nicQuota;
      private int networkQuota;
      private Integer vmQuota;
      private Boolean isEnabled;
      private Integer status;

      /**
       * @see Vdc#getAllocationModel()
       */
      public Builder allocationModel(String allocationModel) {
         this.allocationModel = allocationModel;
         return this;
      }

      /**
       * @see Vdc#getStorageCapacity()
       */
      public Builder storageCapacity(CapacityWithUsage storageCapacity) {
         this.storageCapacity = storageCapacity;
         return this;
      }

      /**
       * @see Vdc#getComputeCapacity()
       */
      public Builder computeCapacity(ComputeCapacity computeCapacity) {
         this.computeCapacity = computeCapacity;
         return this;
      }

      /**
       * @see Vdc#getResourceEntities()
       */
      public Builder resourceEntities(ResourceEntities resourceEntities) {
         this.resourceEntities = resourceEntities;
         return this;
      }

      /**
       * @see Vdc#getAvailableNetworks()
       */
      public Builder availableNetworks(AvailableNetworks availableNetworks) {
         this.availableNetworks = availableNetworks;
         return this;
      }

      /**
       * @see Vdc#getCapabilities()
       */
      public Builder capabilities(Capabilities capabilities) {
         this.capabilities = capabilities;
         return this;
      }

      /**
       * @see Vdc#getNicQuota()
       */
      public Builder nicQuota(int nicQuota) {
         this.nicQuota = nicQuota;
         return this;
      }

      /**
       * @see Vdc#getNetworkQuota()
       */
      public Builder networkQuota(int networkQuota) {
         this.networkQuota = networkQuota;
         return this;
      }

      /**
       * @see Vdc#getVmQuota()
       */
      public Builder vmQuota(Integer vmQuota) {
         this.vmQuota = vmQuota;
         return this;
      }

      /**
       * @see Vdc#isEnabled()
       */
      public Builder isEnabled(Boolean isEnabled) {
         this.isEnabled = isEnabled;
         return this;
      }

      /**
       * @see Vdc#getStatus()
       */
      public Builder status(Integer status) {
         this.status = status;
         return this;
      }

      public Vdc build() {
         return new Vdc(
               href, type, links, description, tasksInProgress, id, name, allocationModel, storageCapacity,
               computeCapacity, resourceEntities, availableNetworks, capabilities, nicQuota, networkQuota,
               vmQuota, isEnabled, status);
      }

      /**
       * @see EntityType#getName()
       */
      public Builder name(String name) {
         super.name(name);
         return this;
      }

      /**
       * @see EntityType#getDescription()
       */
      public Builder description(String description) {
         super.description(description);
         return this;
      }

      /**
       * @see EntityType#getId()
       */
      @Override
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see EntityType#getTasksInProgress()
       */
      @Override
      public Builder tasksInProgress(TasksInProgress tasksInProgress) {
         this.tasksInProgress = tasksInProgress;
         return this;
      }

      /**
       * @see ReferenceType#getHref()
       */
      @Override
      public Builder href(URI href) {
         this.href = href;
         return this;
      }

      /**
       * @see ReferenceType#getType()
       */
      @Override
      public Builder type(String type) {
         this.type = type;
         return this;
      }

      /**
       * @see EntityType#getLinks()
       */
      @Override
      public Builder links(Set<Link> links) {
         this.links = Sets.newLinkedHashSet(checkNotNull(links, "links"));
         return this;
      }

      /**
       * @see EntityType#getLinks()
       */
      @Override
      public Builder link(Link link) {
         this.links.add(checkNotNull(link, "link"));
         return this;
      }


      @Override
      public Builder fromEntityType(EntityType<Vdc> in) {
         return Builder.class.cast(super.fromEntityType(in));
      }

      public Builder fromVdc(Vdc in) {
         return fromEntityType(in)
               .allocationModel(in.getAllocationModel())
               .storageCapacity(in.getStorageCapacity())
               .computeCapacity(in.getComputeCapacity())
               .resourceEntities(in.getResourceEntities())
               .availableNetworks(in.getAvailableNetworks())
               .capabilities(in.getCapabilities())
               .nicQuota(in.getNicQuota())
               .networkQuota(in.getNetworkQuota())
               .vmQuota(in.getVmQuota())
               .isEnabled(in.isEnabled())
               .status(in.getStatus());
      }
   }

   public Vdc(URI href, String type, Set<Link> links, String description, TasksInProgress tasksInProgress, String id, String name, String allocationModel, CapacityWithUsage storageCapacity, ComputeCapacity computeCapacity, ResourceEntities resourceEntities, AvailableNetworks availableNetworks, Capabilities capabilities, int nicQuota, int networkQuota, Integer vmQuota, Boolean enabled, Integer status) {
      super(href, type, links, description, tasksInProgress, id, name);
      this.allocationModel = allocationModel;
      this.storageCapacity = storageCapacity;
      this.computeCapacity = computeCapacity;
      this.resourceEntities = resourceEntities;
      this.availableNetworks = availableNetworks;
      this.capabilities = capabilities;
      this.nicQuota = nicQuota;
      this.networkQuota = networkQuota;
      this.vmQuota = vmQuota;
      isEnabled = enabled;
      this.status = status;
   }

   private Vdc() {
      // For JAXB
   }


   @XmlElement(name = "AllocationModel", required = true)
   protected String allocationModel;
   @XmlElement(name = "StorageCapacity", required = true)
   protected CapacityWithUsage storageCapacity;
   @XmlElement(name = "ComputeCapacity", required = true)
   protected ComputeCapacity computeCapacity;
   @XmlElement(name = "ResourceEntities")
   protected ResourceEntities resourceEntities;
   @XmlElement(name = "AvailableNetworks")
   protected AvailableNetworks availableNetworks;
   @XmlElement(name = "Capabilities")
   protected Capabilities capabilities;
   @XmlElement(name = "NicQuota")
   protected int nicQuota;
   @XmlElement(name = "NetworkQuota")
   protected int networkQuota;
   @XmlElement(name = "VmQuota")
   protected Integer vmQuota;
   @XmlElement(name = "IsEnabled")
   protected Boolean isEnabled;
   @XmlAttribute
   protected Integer status;

   /**
    * Gets the value of the allocationModel property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getAllocationModel() {
      return allocationModel;
   }

   /**
    * Gets the value of the storageCapacity property.
    *
    * @return possible object is
    *         {@link CapacityWithUsage }
    */
   public CapacityWithUsage getStorageCapacity() {
      return storageCapacity;
   }

   /**
    * Gets the value of the computeCapacity property.
    *
    * @return possible object is
    *         {@link ComputeCapacity }
    */
   public ComputeCapacity getComputeCapacity() {
      return computeCapacity;
   }

   /**
    * Gets the value of the resourceEntities property.
    *
    * @return possible object is
    *         {@link ResourceEntities }
    */
   public ResourceEntities getResourceEntities() {
      return resourceEntities;
   }

   /**
    * Gets the value of the availableNetworks property.
    *
    * @return possible object is
    *         {@link AvailableNetworks }
    */
   public AvailableNetworks getAvailableNetworks() {
      return availableNetworks;
   }

   /**
    * Gets the value of the capabilities property.
    *
    * @return possible object is
    *         {@link Capabilities }
    */
   public Capabilities getCapabilities() {
      return capabilities;
   }

   /**
    * Gets the value of the nicQuota property.
    */
   public int getNicQuota() {
      return nicQuota;
   }

   /**
    * Gets the value of the networkQuota property.
    */
   public int getNetworkQuota() {
      return networkQuota;
   }

   /**
    * Gets the value of the vmQuota property.
    *
    * @return possible object is
    *         {@link Integer }
    */
   public Integer getVmQuota() {
      return vmQuota;
   }

   /**
    * Gets the value of the isEnabled property.
    *
    * @return possible object is
    *         {@link Boolean }
    */
   public Boolean isEnabled() {
      return isEnabled;
   }

   /**
    * Gets the value of the status property.
    *
    * @return possible object is
    *         {@link Integer }
    */
   public Integer getStatus() {
      return status;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      Vdc that = Vdc.class.cast(o);
      return equal(allocationModel, that.allocationModel) &&
            equal(storageCapacity, that.storageCapacity) &&
            equal(computeCapacity, that.computeCapacity) &&
            equal(resourceEntities, that.resourceEntities) &&
            equal(availableNetworks, that.availableNetworks) &&
            equal(capabilities, that.capabilities) &&
            equal(nicQuota, that.nicQuota) &&
            equal(networkQuota, that.networkQuota) &&
            equal(vmQuota, that.vmQuota) &&
            equal(isEnabled, that.isEnabled) &&
            equal(status, that.status);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(allocationModel,
            storageCapacity,
            computeCapacity,
            resourceEntities,
            availableNetworks,
            capabilities,
            nicQuota,
            networkQuota,
            vmQuota,
            isEnabled,
            status);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("allocationModel", allocationModel)
            .add("storageCapacity", storageCapacity)
            .add("computeCapacity", computeCapacity)
            .add("resourceEntities", resourceEntities)
            .add("availableNetworks", availableNetworks)
            .add("capabilities", capabilities)
            .add("nicQuota", nicQuota)
            .add("networkQuota", networkQuota)
            .add("vmQuota", vmQuota)
            .add("isEnabled", isEnabled)
            .add("status", status).toString();
   }

}
