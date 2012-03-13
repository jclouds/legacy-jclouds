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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;


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
    AdminVdc.class
})
public class Vdc extends EntityType<Vdc> {
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }
   
   @Override
   public Builder<?> toNewBuilder() {
      return new ConcreteBuilder();
   }
   
   @Override
   public ResourceType.Builder<Vdc> toBuilder() {
      throw new UnsupportedOperationException("Use new builder");
   }
   
   public abstract static class Builder<T extends Builder<T>> extends EntityType.NewBuilder<T> {
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
      public T allocationModel(String allocationModel) {
         this.allocationModel = allocationModel;
         return self();
      }

      /**
       * @see Vdc#getStorageCapacity()
       */
      public T storageCapacity(CapacityWithUsage storageCapacity) {
         this.storageCapacity = storageCapacity;
         return self();
      }

      /**
       * @see Vdc#getComputeCapacity()
       */
      public T computeCapacity(ComputeCapacity computeCapacity) {
         this.computeCapacity = computeCapacity;
         return self();
      }

      /**
       * @see Vdc#getResourceEntities()
       */
      public T resourceEntities(ResourceEntities resourceEntities) {
         this.resourceEntities = resourceEntities;
         return self();
      }

      /**
       * @see Vdc#getAvailableNetworks()
       */
      public T availableNetworks(AvailableNetworks availableNetworks) {
         this.availableNetworks = availableNetworks;
         return self();
      }

      /**
       * @see Vdc#getCapabilities()
       */
      public T capabilities(Capabilities capabilities) {
         this.capabilities = capabilities;
         return self();
      }

      /**
       * @see Vdc#getNicQuota()
       */
      public T nicQuota(int nicQuota) {
         this.nicQuota = nicQuota;
         return self();
      }

      /**
       * @see Vdc#getNetworkQuota()
       */
      public T networkQuota(int networkQuota) {
         this.networkQuota = networkQuota;
         return self();
      }

      /**
       * @see Vdc#getVmQuota()
       */
      public T vmQuota(Integer vmQuota) {
         this.vmQuota = vmQuota;
         return self();
      }

      /**
       * @see Vdc#isEnabled()
       */
      public T isEnabled(Boolean isEnabled) {
         this.isEnabled = isEnabled;
         return self();
      }

      /**
       * @see Vdc#getStatus()
       */
      public T status(Integer status) {
         this.status = status;
         return self();
      }

      public Vdc build() {
         return new Vdc(this);
      }

      public T fromVdc(Vdc in) {
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
   
   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override protected ConcreteBuilder self() {
         return this;
      }
   }
   
   protected Vdc() {
      // For JAXB
   }

   public Vdc(Builder<?> b) {
      super(b.href, b.type, b.links, b.description, b.tasks, b.id, b.name);
      this.allocationModel = b.allocationModel;
      this.storageCapacity = b.storageCapacity;
      this.computeCapacity = b.computeCapacity;
      this.resourceEntities = b.resourceEntities;
      this.availableNetworks = b.availableNetworks;
      this.capabilities = b.capabilities;
      this.nicQuota = b.nicQuota;
      this.networkQuota = b.networkQuota;
      this.vmQuota = b.vmQuota;
      isEnabled = b.isEnabled;
      this.status = b.status;
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
      return super.equals(that) &&
            equal(allocationModel, that.allocationModel) &&
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
      return Objects.hashCode(super.hashCode(), 
            allocationModel,
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
