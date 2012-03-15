/*
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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents a virtual data center (vDC).
 *
 * <pre>
 * &lt;complexType name="Vdc" /&gt;
 * </pre>
 */
@XmlRootElement(name = "Vdc")
@XmlType(name = "VdcType")
public class Vdc extends EntityType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromVdc(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static abstract class Builder<B extends Builder<B>> extends EntityType.Builder<B> {
      private String allocationModel;
      private CapacityWithUsage storageCapacity;
      private ComputeCapacity computeCapacity;
      private ResourceEntities resourceEntities;
      private AvailableNetworks availableNetworks;
      private Capabilities capabilities;
      private Integer nicQuota;
      private Integer networkQuota;
      private Integer vmQuota;
      private Boolean isEnabled;
      private Integer status;

      /**
       * @see Vdc#getAllocationModel()
       */
      public B allocationModel(String allocationModel) {
         this.allocationModel = allocationModel;
         return self();
      }

      /**
       * @see Vdc#getStorageCapacity()
       */
      public B storageCapacity(CapacityWithUsage storageCapacity) {
         this.storageCapacity = storageCapacity;
         return self();
      }

      /**
       * @see Vdc#getComputeCapacity()
       */
      public B computeCapacity(ComputeCapacity computeCapacity) {
         this.computeCapacity = computeCapacity;
         return self();
      }

      /**
       * @see Vdc#getResourceEntities()
       */
      public B resourceEntities(ResourceEntities resourceEntities) {
         this.resourceEntities = resourceEntities;
         return self();
      }

      /**
       * @see Vdc#getAvailableNetworks()
       */
      public B availableNetworks(AvailableNetworks availableNetworks) {
         this.availableNetworks = availableNetworks;
         return self();
      }

      /**
       * @see Vdc#getCapabilities()
       */
      public B capabilities(Capabilities capabilities) {
         this.capabilities = capabilities;
         return self();
      }

      /**
       * @see Vdc#getNicQuota()
       */
      public B nicQuota(Integer nicQuota) {
         this.nicQuota = nicQuota;
         return self();
      }

      /**
       * @see Vdc#getNetworkQuota()
       */
      public B networkQuota(Integer networkQuota) {
         this.networkQuota = networkQuota;
         return self();
      }

      /**
       * @see Vdc#getVmQuota()
       */
      public B vmQuota(Integer vmQuota) {
         this.vmQuota = vmQuota;
         return self();
      }

      /**
       * @see Vdc#isEnabled()
       */
      public B isEnabled(Boolean isEnabled) {
         this.isEnabled = isEnabled;
         return self();
      }

      /**
       * @see Vdc#getStatus()
       */
      public B status(Integer status) {
         this.status = status;
         return self();
      }

      @Override
      public Vdc build() {
         return new Vdc(this);
      }

      public B fromVdc(Vdc in) {
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
   
   protected Vdc() {
      // For JAXB
   }

   public Vdc(Builder<?> builder) {
      super(builder);
      this.allocationModel = builder.allocationModel;
      this.storageCapacity = builder.storageCapacity;
      this.computeCapacity = builder.computeCapacity;
      this.resourceEntities = builder.resourceEntities;
      this.availableNetworks = builder.availableNetworks;
      this.capabilities = builder.capabilities;
      this.nicQuota = builder.nicQuota;
      this.networkQuota = builder.networkQuota;
      this.vmQuota = builder.vmQuota;
      this.isEnabled = builder.isEnabled;
      this.status = builder.status;
   }

   @XmlElement(name = "AllocationModel", required = true)
   private String allocationModel;
   @XmlElement(name = "StorageCapacity", required = true)
   private CapacityWithUsage storageCapacity;
   @XmlElement(name = "ComputeCapacity", required = true)
   private ComputeCapacity computeCapacity;
   @XmlElement(name = "ResourceEntities")
   private ResourceEntities resourceEntities;
   @XmlElement(name = "AvailableNetworks")
   private AvailableNetworks availableNetworks;
   @XmlElement(name = "Capabilities")
   private Capabilities capabilities;
   @XmlElement(name = "NicQuota")
   private Integer nicQuota;
   @XmlElement(name = "NetworkQuota")
   private Integer networkQuota;
   @XmlElement(name = "VmQuota")
   private Integer vmQuota;
   @XmlElement(name = "IsEnabled")
   private Boolean isEnabled;
   @XmlAttribute
   private Integer status;

   /**
    * Gets the value of the allocationModel property.
    */
   public String getAllocationModel() {
      return allocationModel;
   }

   /**
    * Gets the value of the storageCapacity property.
    */
   public CapacityWithUsage getStorageCapacity() {
      return storageCapacity;
   }

   /**
    * Gets the value of the computeCapacity property.
    */
   public ComputeCapacity getComputeCapacity() {
      return computeCapacity;
   }

   /**
    * Gets the value of the resourceEntities property.
    */
   public ResourceEntities getResourceEntities() {
      return resourceEntities;
   }

   /**
    * Gets the value of the availableNetworks property.
    */
   public AvailableNetworks getAvailableNetworks() {
      return availableNetworks;
   }

   /**
    * Gets the value of the capabilities property.
    */
   public Capabilities getCapabilities() {
      return capabilities;
   }

   /**
    * Gets the value of the nicQuota property.
    */
   public Integer getNicQuota() {
      return nicQuota;
   }

   /**
    * Gets the value of the networkQuota property.
    */
   public Integer getNetworkQuota() {
      return networkQuota;
   }

   /**
    * Gets the value of the vmQuota property.
    */
   public Integer getVmQuota() {
      return vmQuota;
   }

   /**
    * Gets the value of the isEnabled property.
    */
   public Boolean isEnabled() {
      return isEnabled;
   }

   /**
    * Gets the value of the status property.
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
      return super.equals(that) &&
            equal(this.allocationModel, that.allocationModel) &&
            equal(this.storageCapacity, that.storageCapacity) &&
            equal(this.computeCapacity, that.computeCapacity) &&
            equal(this.resourceEntities, that.resourceEntities) &&
            equal(this.availableNetworks, that.availableNetworks) &&
            equal(this.capabilities, that.capabilities) &&
            equal(this.nicQuota, that.nicQuota) &&
            equal(this.networkQuota, that.networkQuota) &&
            equal(this.vmQuota, that.vmQuota) &&
            equal(this.isEnabled, that.isEnabled) &&
            equal(this.status, that.status);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), allocationModel, storageCapacity, computeCapacity, resourceEntities, availableNetworks,
            capabilities, nicQuota, networkQuota, vmQuota, isEnabled, status);
   }

   @Override
   public ToStringHelper string() {
      return super.string()
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
            .add("status", status);
   }

}
