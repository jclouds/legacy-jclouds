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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;


/**
 * 
 *                 Represents a virtual data center (vDC).
 *             
 * 
 * <p>Java class for Vdc complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
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
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Vdc", propOrder = {
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
   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

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
         Vdc vdc = new Vdc();
         vdc.setAllocationModel(allocationModel);
         vdc.setStorageCapacity(storageCapacity);
         vdc.setComputeCapacity(computeCapacity);
         vdc.setResourceEntities(resourceEntities);
         vdc.setAvailableNetworks(availableNetworks);
         vdc.setCapabilities(capabilities);
         vdc.setNicQuota(nicQuota);
         vdc.setNetworkQuota(networkQuota);
         vdc.setVmQuota(vmQuota);
         vdc.setIsEnabled(isEnabled);
         vdc.setStatus(status);
         return vdc;
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
       * @see ReferenceType#getLinks()
       */
      @Override
      public Builder links(Set<Link> links) {
         this.links = Sets.newLinkedHashSet(checkNotNull(links, "links"));
         return this;
      }

      /**
       * @see ReferenceType#getLinks()
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

   private Vdc() {
      // For JAXB and builder use
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
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAllocationModel() {
        return allocationModel;
    }

    /**
     * Sets the value of the allocationModel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAllocationModel(String value) {
        this.allocationModel = value;
    }

    /**
     * Gets the value of the storageCapacity property.
     * 
     * @return
     *     possible object is
     *     {@link CapacityWithUsage }
     *     
     */
    public CapacityWithUsage getStorageCapacity() {
        return storageCapacity;
    }

    /**
     * Sets the value of the storageCapacity property.
     * 
     * @param value
     *     allowed object is
     *     {@link CapacityWithUsage }
     *     
     */
    public void setStorageCapacity(CapacityWithUsage value) {
        this.storageCapacity = value;
    }

    /**
     * Gets the value of the computeCapacity property.
     * 
     * @return
     *     possible object is
     *     {@link ComputeCapacity }
     *     
     */
    public ComputeCapacity getComputeCapacity() {
        return computeCapacity;
    }

    /**
     * Sets the value of the computeCapacity property.
     * 
     * @param value
     *     allowed object is
     *     {@link ComputeCapacity }
     *     
     */
    public void setComputeCapacity(ComputeCapacity value) {
        this.computeCapacity = value;
    }

    /**
     * Gets the value of the resourceEntities property.
     * 
     * @return
     *     possible object is
     *     {@link ResourceEntities }
     *     
     */
    public ResourceEntities getResourceEntities() {
        return resourceEntities;
    }

    /**
     * Sets the value of the resourceEntities property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResourceEntities }
     *     
     */
    public void setResourceEntities(ResourceEntities value) {
        this.resourceEntities = value;
    }

    /**
     * Gets the value of the availableNetworks property.
     * 
     * @return
     *     possible object is
     *     {@link AvailableNetworks }
     *     
     */
    public AvailableNetworks getAvailableNetworks() {
        return availableNetworks;
    }

    /**
     * Sets the value of the availableNetworks property.
     * 
     * @param value
     *     allowed object is
     *     {@link AvailableNetworks }
     *     
     */
    public void setAvailableNetworks(AvailableNetworks value) {
        this.availableNetworks = value;
    }

    /**
     * Gets the value of the capabilities property.
     * 
     * @return
     *     possible object is
     *     {@link Capabilities }
     *     
     */
    public Capabilities getCapabilities() {
        return capabilities;
    }

    /**
     * Sets the value of the capabilities property.
     * 
     * @param value
     *     allowed object is
     *     {@link Capabilities }
     *     
     */
    public void setCapabilities(Capabilities value) {
        this.capabilities = value;
    }

    /**
     * Gets the value of the nicQuota property.
     * 
     */
    public int getNicQuota() {
        return nicQuota;
    }

    /**
     * Sets the value of the nicQuota property.
     * 
     */
    public void setNicQuota(int value) {
        this.nicQuota = value;
    }

    /**
     * Gets the value of the networkQuota property.
     * 
     */
    public int getNetworkQuota() {
        return networkQuota;
    }

    /**
     * Sets the value of the networkQuota property.
     * 
     */
    public void setNetworkQuota(int value) {
        this.networkQuota = value;
    }

    /**
     * Gets the value of the vmQuota property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getVmQuota() {
        return vmQuota;
    }

    /**
     * Sets the value of the vmQuota property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setVmQuota(Integer value) {
        this.vmQuota = value;
    }

    /**
     * Gets the value of the isEnabled property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Sets the value of the isEnabled property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsEnabled(Boolean value) {
        this.isEnabled = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setStatus(Integer value) {
        this.status = value;
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
