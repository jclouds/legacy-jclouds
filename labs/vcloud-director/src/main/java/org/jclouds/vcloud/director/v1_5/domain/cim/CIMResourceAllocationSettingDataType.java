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
package org.jclouds.vcloud.director.v1_5.domain.cim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

/**
 * Java class for CIM_ResourceAllocationSettingData_Type complex type.
 *
 * <pre>
 * &lt;complexType name="CIM_ResourceAllocationSettingData_Type" /&gt;
 * </pre>
 */
@XmlType(name = "CIM_ResourceAllocationSettingData_Type", namespace = "http://schemas.dmtf.org/wbem/wscim/1/cim-schema/2/CIM_ResourceAllocationSettingData")
public class CIMResourceAllocationSettingDataType {

   // TODO Add toString, hashCode and equals
   
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromCIMResourceAllocationSettingDataType(this);
   }

   public static class Builder<B extends Builder<B>> {
      private CimString address;
      private CimString addressOnParent;
      private CimString allocationUnits;
      private CimBoolean automaticAllocation;
      private CimBoolean automaticDeallocation;
      private ResourceAllocationCaption caption;
      private ResourceAllocationChangeableType changeableType;
      private CimString configurationName;
      private List<CimString> connection;
      private ConsumerVisibility consumerVisibility;
      private CimString description;
      private CimString elementName;
      private CimUnsignedLong generation;
      private List<CimString> hostResource;
      private CimString instanceID;
      private CimUnsignedLong limit;
      private MappingBehavior mappingBehavior;
      private CimString otherResourceType;
      private CimString parent;
      private CimString poolID;
      private CimUnsignedLong reservation;
      private CimString resourceSubType;
      private ResourceType resourceType;
      private CimUnsignedLong virtualQuantity;
      private CimString virtualQuantityUnits;
      private CimUnsignedInt weight;
      private List<Object> any;
      private Map<QName, String> otherAttributes = new HashMap<QName, String>();

      @SuppressWarnings("unchecked")
      protected B self() {
         return (B) this;
      }
      
      public B address(CimString val) {
         this.address = val;
         return self();
      }
      public B addressOnParent(CimString val) {
         this.addressOnParent = val;
         return self();
      }
      public B allocationUnits(CimString val) {
         this.allocationUnits = val;
         return self();
      }
      public B automaticAllocation(CimBoolean val) {
         this.automaticAllocation = val;
         return self();
      }
      public B automaticDeallocation(CimBoolean val) {
         this.automaticDeallocation = val;
         return self();
      }
      public B caption(ResourceAllocationCaption val) {
         this.caption = val;
         return self();
      }
      public B changeableType(ResourceAllocationChangeableType val) {
         this.changeableType = val;
         return self();
      }
      public B configurationName(CimString val) {
         this.configurationName = val;
         return self();
      }
      public B connection(List<CimString> val) {
         this.connection = val;
         return self();
      }
      public B consumerVisibility(ConsumerVisibility val) {
         this.consumerVisibility = val;
         return self();
      }
      public B description(CimString val) {
         this.description = val;
         return self();
      }
      public B elementName(CimString val) {
         this.elementName = val;
         return self();
      }
      public B generation(CimUnsignedLong val) {
         this.generation = val;
         return self();
      }
      public B hostResource(List<CimString> val) {
         this.hostResource = val;
         return self();
      }
      public B instanceID(CimString val) {
         this.instanceID = val;
         return self();
      }
      public B limit(CimUnsignedLong val) {
         this.limit = val;
         return self();
      }
      public B mappingBehavior(MappingBehavior val) {
         this.mappingBehavior = val;
         return self();
      }
      public B otherResourceType(CimString val) {
         this.otherResourceType = val;
         return self();
      }
      public B parent(CimString val) {
         this.parent = val;
         return self();
      }
      public B poolID(CimString val) {
         this.poolID = val;
         return self();
      }
      public B reservation(CimUnsignedLong val) {
         this.reservation = val;
         return self();
      }
      public B resourceSubType(CimString val) {
         this.resourceSubType = val;
         return self();
      }
      public B resourceType(ResourceType val) {
         this.resourceType = val;
         return self();
      }
      public B virtualQuantity(CimUnsignedLong val) {
         this.virtualQuantity = val;
         return self();
      }
      public B virtualQuantityUnits(CimString val) {
         this.virtualQuantityUnits = val;
         return self();
      }
      public B weight(CimUnsignedInt val) {
         this.weight = val;
         return self();
      }
      public B any(List<Object> val) {
         this.any = val;
         return self();
      }
      public B otherAttributes(Map<QName, String> val) {
         this.otherAttributes = val;
         return self();
      }
      
      public B fromCIMResourceAllocationSettingDataType(CIMResourceAllocationSettingDataType val) {
         return self().address(val.getAddress()).
                  addressOnParent(val.getAddressOnParent()).
                  allocationUnits(val.getAllocationUnits()).
                  allocationUnits(val.getAllocationUnits()).
                  automaticAllocation(val.getAutomaticAllocation()).
                  automaticDeallocation(val.getAutomaticDeallocation()).
                  caption(val.getCaption()).
                  changeableType(val.getChangeableType()).
                  configurationName(val.getConfigurationName()).
                  connection(val.getConnection()).
                  consumerVisibility(val.getConsumerVisibility()).
                  description(val.getDescription()).
                  elementName(val.getElementName()).
                  generation(val.getGeneration()).
                  hostResource(val.getHostResource()).
                  instanceID(val.getInstanceID()).
                  limit(val.getLimit()).
                  mappingBehavior(val.getMappingBehavior()).
                  otherResourceType(val.getOtherResourceType()).
                  parent(val.getParent()).
                  poolID(val.getPoolID()).
                  reservation(val.getReservation()).
                  resourceSubType(val.getResourceSubType()).
                  resourceType(val.getResourceType()).
                  virtualQuantity(val.getVirtualQuantity()).
                  virtualQuantityUnits(val.getVirtualQuantityUnits()).
                  weight(val.getWeight()).
                  any(val.getAny()).
                  otherAttributes(val.getOtherAttributes());
      }
      
      public CIMResourceAllocationSettingDataType build() {
         return new CIMResourceAllocationSettingDataType(this);
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }

    @XmlElement(name = "Address", nillable = true)
    private CimString address;
    @XmlElement(name = "AddressOnParent", nillable = true)
    private CimString addressOnParent;
    @XmlElement(name = "AllocationUnits", nillable = true)
    private CimString allocationUnits;
    @XmlElement(name = "AutomaticAllocation", nillable = true)
    private CimBoolean automaticAllocation;
    @XmlElement(name = "AutomaticDeallocation", nillable = true)
    private CimBoolean automaticDeallocation;
    @XmlElementRef(name = "Caption", namespace = "http://schemas.dmtf.org/wbem/wscim/1/cim-schema/2/CIM_ResourceAllocationSettingData", type = ResourceAllocationCaption.class)
    private ResourceAllocationCaption caption;
    @XmlElementRef(name = "ChangeableType", namespace = "http://schemas.dmtf.org/wbem/wscim/1/cim-schema/2/CIM_ResourceAllocationSettingData", type = ResourceAllocationChangeableType.class)
    private ResourceAllocationChangeableType changeableType;
    @XmlElement(name = "ConfigurationName", nillable = true)
    private CimString configurationName;
    @XmlElement(name = "Connection", nillable = true)
    private List<CimString> connection;
    @XmlElement(name = "ConsumerVisibility", nillable = true)
    private ConsumerVisibility consumerVisibility;
    @XmlElement(name = "Description", nillable = true)
    private CimString description;
    @XmlElement(name = "ElementName", required = true)
    private CimString elementName;
    @XmlElement(name = "Generation", nillable = true)
    private CimUnsignedLong generation;
    @XmlElement(name = "HostResource", nillable = true)
    private List<CimString> hostResource;
    @XmlElement(name = "InstanceID", required = true)
    private CimString instanceID;
    @XmlElement(name = "Limit", nillable = true)
    private CimUnsignedLong limit;
    @XmlElement(name = "MappingBehavior", nillable = true)
    private MappingBehavior mappingBehavior;
    @XmlElement(name = "OtherResourceType", nillable = true)
    private CimString otherResourceType;
    @XmlElement(name = "Parent", nillable = true)
    private CimString parent;
    @XmlElement(name = "PoolID", nillable = true)
    private CimString poolID;
    @XmlElement(name = "Reservation", nillable = true)
    private CimUnsignedLong reservation;
    @XmlElement(name = "ResourceSubType", nillable = true)
    private CimString resourceSubType;
    @XmlElement(name = "ResourceType", nillable = true)
    private ResourceType resourceType;
    @XmlElement(name = "VirtualQuantity", nillable = true)
    private CimUnsignedLong virtualQuantity;
    @XmlElement(name = "VirtualQuantityUnits", nillable = true)
    private CimString virtualQuantityUnits;
    @XmlElement(name = "Weight", nillable = true)
    private CimUnsignedInt weight;
    @XmlAnyElement(lax = true)
    private List<Object> any;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    protected CIMResourceAllocationSettingDataType() {
       // For JAXB
    }
    
    protected CIMResourceAllocationSettingDataType(Builder<?> builder) {
       this.address = builder.address;
       this.addressOnParent = builder.addressOnParent;
       this.allocationUnits = builder.allocationUnits;
       this.automaticAllocation = builder.automaticAllocation;
       this.automaticDeallocation = builder.automaticDeallocation;
       this.caption = builder.caption;
       this.changeableType = builder.changeableType;
       this.configurationName = builder.configurationName;
       this.connection = builder.connection;
       this.consumerVisibility = builder.consumerVisibility;
       this.description = builder.description;
       this.elementName = builder.elementName;
       this.generation = builder.generation;
       this.hostResource = builder.hostResource;
       this.instanceID = builder.instanceID;
       this.limit = builder.limit;
       this.mappingBehavior = builder.mappingBehavior;
       this.otherResourceType = builder.otherResourceType;
       this.parent = builder.parent;
       this.poolID = builder.poolID;
       this.reservation = builder.reservation;
       this.resourceSubType = builder.resourceSubType;
       this.resourceType = builder.resourceType;
       this.virtualQuantity = builder.virtualQuantity;
       this.virtualQuantityUnits = builder.virtualQuantityUnits;
       this.weight = builder.weight;
       this.any = builder.any;
       this.otherAttributes = builder.otherAttributes;
    }

    /**
     * Gets the value of the address property.
     *
     * @return
     *     possible object is
     *     {@link CimString }
     *
     */
    public CimString getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     *
     * @param value
     *     allowed object is
     *     {@link CimString }
     *
     */
    public void setAddress(CimString value) {
        this.address = value;
    }

    /**
     * Gets the value of the addressOnParent property.
     *
     * @return
     *     possible object is
     *     {@link CimString }
     *
     */
    public CimString getAddressOnParent() {
        return addressOnParent;
    }

    /**
     * Sets the value of the addressOnParent property.
     *
     * @param value
     *     allowed object is
     *     {@link CimString }
     *
     */
    public void setAddressOnParent(CimString value) {
        this.addressOnParent = value;
    }

    /**
     * Gets the value of the allocationUnits property.
     *
     * @return
     *     possible object is
     *     {@link CimString }
     *
     */
    public CimString getAllocationUnits() {
        return allocationUnits;
    }

    /**
     * Sets the value of the allocationUnits property.
     *
     * @param value
     *     allowed object is
     *     {@link CimString }
     *
     */
    public void setAllocationUnits(CimString value) {
        this.allocationUnits = value;
    }

    /**
     * Gets the value of the automaticAllocation property.
     *
     * @return
     *     possible object is
     *     {@link CimBoolean }
     *
     */
    public CimBoolean getAutomaticAllocation() {
        return automaticAllocation;
    }

    /**
     * Sets the value of the automaticAllocation property.
     *
     * @param value
     *     allowed object is
     *     {@link CimBoolean }
     *
     */
    public void setAutomaticAllocation(CimBoolean value) {
        this.automaticAllocation = value;
    }

    /**
     * Gets the value of the automaticDeallocation property.
     *
     * @return
     *     possible object is
     *     {@link CimBoolean }
     *
     */
    public CimBoolean getAutomaticDeallocation() {
        return automaticDeallocation;
    }

    /**
     * Sets the value of the automaticDeallocation property.
     *
     * @param value
     *     allowed object is
     *     {@link CimBoolean }
     *
     */
    public void setAutomaticDeallocation(CimBoolean value) {
        this.automaticDeallocation = value;
    }

    /**
     * Gets the value of the caption property.
     *
     * @return
     *     possible object is
     *     {@link ResourceAllocationCaption }
     *
     */
    public ResourceAllocationCaption getCaption() {
        return caption;
    }

    /**
     * Sets the value of the caption property.
     *
     * @param value
     *     allowed object is
     *     {@link ResourceAllocationCaption }
     *
     */
    public void setCaption(ResourceAllocationCaption value) {
        this.caption = value;
    }

    /**
     * Gets the value of the changeableType property.
     *
     * @return
     *     possible object is
     *     {@link ResourceAllocationChangeableType }
     *
     */
    public ResourceAllocationChangeableType getChangeableType() {
        return changeableType;
    }

    /**
     * Sets the value of the changeableType property.
     *
     * @param value
     *     allowed object is
     *     {@link ResourceAllocationChangeableType }
     *
     */
    public void setChangeableType(ResourceAllocationChangeableType value) {
        this.changeableType = value;
    }

    /**
     * Gets the value of the configurationName property.
     *
     * @return
     *     possible object is
     *     {@link CimString }
     *
     */
    public CimString getConfigurationName() {
        return configurationName;
    }

    /**
     * Sets the value of the configurationName property.
     *
     * @param value
     *     allowed object is
     *     {@link CimString }
     *
     */
    public void setConfigurationName(CimString value) {
        this.configurationName = value;
    }

    /**
     * Gets the value of the connection property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the connection property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConnection().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CimString }
     *
     *
     */
    public List<CimString> getConnection() {
        if (connection == null) {
            connection = new ArrayList<CimString>();
        }
        return this.connection;
    }

    /**
     * Gets the value of the consumerVisibility property.
     *
     * @return
     *     possible object is
     *     {@link ConsumerVisibility }
     *
     */
    public ConsumerVisibility getConsumerVisibility() {
        return consumerVisibility;
    }

    /**
     * Sets the value of the consumerVisibility property.
     *
     * @param value
     *     allowed object is
     *     {@link ConsumerVisibility }
     *
     */
    public void setConsumerVisibility(ConsumerVisibility value) {
        this.consumerVisibility = value;
    }

    /**
     * Gets the value of the description property.
     *
     * @return
     *     possible object is
     *     {@link CimString }
     *
     */
    public CimString getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     *
     * @param value
     *     allowed object is
     *     {@link CimString }
     *
     */
    public void setDescription(CimString value) {
        this.description = value;
    }

    /**
     * Gets the value of the elementName property.
     *
     * @return
     *     possible object is
     *     {@link CimString }
     *
     */
    public CimString getElementName() {
        return elementName;
    }

    /**
     * Sets the value of the elementName property.
     *
     * @param value
     *     allowed object is
     *     {@link CimString }
     *
     */
    public void setElementName(CimString value) {
        this.elementName = value;
    }

    /**
     * Gets the value of the generation property.
     *
     * @return
     *     possible object is
     *     {@link CimUnsignedLong }
     *
     */
    public CimUnsignedLong getGeneration() {
        return generation;
    }

    /**
     * Sets the value of the generation property.
     *
     * @param value
     *     allowed object is
     *     {@link CimUnsignedLong }
     *
     */
    public void setGeneration(CimUnsignedLong value) {
        this.generation = value;
    }

    /**
     * Gets the value of the hostResource property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the hostResource property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHostResource().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CimString }
     *
     *
     */
    public List<CimString> getHostResource() {
        if (hostResource == null) {
            hostResource = new ArrayList<CimString>();
        }
        return this.hostResource;
    }

    /**
     * Gets the value of the instanceID property.
     *
     * @return
     *     possible object is
     *     {@link CimString }
     *
     */
    public CimString getInstanceID() {
        return instanceID;
    }

    /**
     * Sets the value of the instanceID property.
     *
     * @param value
     *     allowed object is
     *     {@link CimString }
     *
     */
    public void setInstanceID(CimString value) {
        this.instanceID = value;
    }

    /**
     * Gets the value of the limit property.
     *
     * @return
     *     possible object is
     *     {@link CimUnsignedLong }
     *
     */
    public CimUnsignedLong getLimit() {
        return limit;
    }

    /**
     * Sets the value of the limit property.
     *
     * @param value
     *     allowed object is
     *     {@link CimUnsignedLong }
     *
     */
    public void setLimit(CimUnsignedLong value) {
        this.limit = value;
    }

    /**
     * Gets the value of the mappingBehavior property.
     *
     * @return
     *     possible object is
     *     {@link MappingBehavior }
     *
     */
    public MappingBehavior getMappingBehavior() {
        return mappingBehavior;
    }

    /**
     * Sets the value of the mappingBehavior property.
     *
     * @param value
     *     allowed object is
     *     {@link MappingBehavior }
     *
     */
    public void setMappingBehavior(MappingBehavior value) {
        this.mappingBehavior = value;
    }

    /**
     * Gets the value of the otherResourceType property.
     *
     * @return
     *     possible object is
     *     {@link CimString }
     *
     */
    public CimString getOtherResourceType() {
        return otherResourceType;
    }

    /**
     * Sets the value of the otherResourceType property.
     *
     * @param value
     *     allowed object is
     *     {@link CimString }
     *
     */
    public void setOtherResourceType(CimString value) {
        this.otherResourceType = value;
    }

    /**
     * Gets the value of the parent property.
     *
     * @return
     *     possible object is
     *     {@link CimString }
     *
     */
    public CimString getParent() {
        return parent;
    }

    /**
     * Sets the value of the parent property.
     *
     * @param value
     *     allowed object is
     *     {@link CimString }
     *
     */
    public void setParent(CimString value) {
        this.parent = value;
    }

    /**
     * Gets the value of the poolID property.
     *
     * @return
     *     possible object is
     *     {@link CimString }
     *
     */
    public CimString getPoolID() {
        return poolID;
    }

    /**
     * Sets the value of the poolID property.
     *
     * @param value
     *     allowed object is
     *     {@link CimString }
     *
     */
    public void setPoolID(CimString value) {
        this.poolID = value;
    }

    /**
     * Gets the value of the reservation property.
     *
     * @return
     *     possible object is
     *     {@link CimUnsignedLong }
     *
     */
    public CimUnsignedLong getReservation() {
        return reservation;
    }

    /**
     * Sets the value of the reservation property.
     *
     * @param value
     *     allowed object is
     *     {@link CimUnsignedLong }
     *
     */
    public void setReservation(CimUnsignedLong value) {
        this.reservation = value;
    }

    /**
     * Gets the value of the resourceSubType property.
     *
     * @return
     *     possible object is
     *     {@link CimString }
     *
     */
    public CimString getResourceSubType() {
        return resourceSubType;
    }

    /**
     * Sets the value of the resourceSubType property.
     *
     * @param value
     *     allowed object is
     *     {@link CimString }
     *
     */
    public void setResourceSubType(CimString value) {
        this.resourceSubType = value;
    }

    /**
     * Gets the value of the resourceType property.
     *
     * @return
     *     possible object is
     *     {@link ResourceType }
     *
     */
    public ResourceType getResourceType() {
        return resourceType;
    }

    /**
     * Sets the value of the resourceType property.
     *
     * @param value
     *     allowed object is
     *     {@link ResourceType }
     *
     */
    public void setResourceType(ResourceType value) {
        this.resourceType = value;
    }

    /**
     * Gets the value of the virtualQuantity property.
     *
     * @return
     *     possible object is
     *     {@link CimUnsignedLong }
     *
     */
    public CimUnsignedLong getVirtualQuantity() {
        return virtualQuantity;
    }

    /**
     * Sets the value of the virtualQuantity property.
     *
     * @param value
     *     allowed object is
     *     {@link CimUnsignedLong }
     *
     */
    public void setVirtualQuantity(CimUnsignedLong value) {
        this.virtualQuantity = value;
    }

    /**
     * Gets the value of the virtualQuantityUnits property.
     *
     * @return
     *     possible object is
     *     {@link CimString }
     *
     */
    public CimString getVirtualQuantityUnits() {
        return virtualQuantityUnits;
    }

    /**
     * Sets the value of the virtualQuantityUnits property.
     *
     * @param value
     *     allowed object is
     *     {@link CimString }
     *
     */
    public void setVirtualQuantityUnits(CimString value) {
        this.virtualQuantityUnits = value;
    }

    /**
     * Gets the value of the weight property.
     *
     * @return
     *     possible object is
     *     {@link CimUnsignedInt }
     *
     */
    public CimUnsignedInt getWeight() {
        return weight;
    }

    /**
     * Sets the value of the weight property.
     *
     * @param value
     *     allowed object is
     *     {@link CimUnsignedInt }
     *
     */
    public void setWeight(CimUnsignedInt value) {
        this.weight = value;
    }

    /**
     * Gets the value of the any property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * {@link Element }
     *
     *
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     *
     * <p>
     * the map is keyed by the name of the attribute and
     * the value is the string value of the attribute.
     *
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     *
     *
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
