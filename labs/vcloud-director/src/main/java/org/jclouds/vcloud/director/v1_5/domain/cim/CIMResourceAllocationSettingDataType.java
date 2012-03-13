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

    @XmlElement(name = "Address", nillable = true)
    protected CimString address;
    @XmlElement(name = "AddressOnParent", nillable = true)
    protected CimString addressOnParent;
    @XmlElement(name = "AllocationUnits", nillable = true)
    protected CimString allocationUnits;
    @XmlElement(name = "AutomaticAllocation", nillable = true)
    protected CimBoolean automaticAllocation;
    @XmlElement(name = "AutomaticDeallocation", nillable = true)
    protected CimBoolean automaticDeallocation;
    @XmlElementRef(name = "Caption", namespace = "http://schemas.dmtf.org/wbem/wscim/1/cim-schema/2/CIM_ResourceAllocationSettingData", type = ResourceAllocationCaption.class)
    protected ResourceAllocationCaption caption;
    @XmlElementRef(name = "ChangeableType", namespace = "http://schemas.dmtf.org/wbem/wscim/1/cim-schema/2/CIM_ResourceAllocationSettingData", type = ResourceAllocationChangeableType.class)
    protected ResourceAllocationChangeableType changeableType;
    @XmlElement(name = "ConfigurationName", nillable = true)
    protected CimString configurationName;
    @XmlElement(name = "Connection", nillable = true)
    protected List<CimString> connection;
    @XmlElement(name = "ConsumerVisibility", nillable = true)
    protected ConsumerVisibility consumerVisibility;
    @XmlElement(name = "Description", nillable = true)
    protected CimString description;
    @XmlElement(name = "ElementName", required = true)
    protected CimString elementName;
    @XmlElement(name = "Generation", nillable = true)
    protected CimUnsignedLong generation;
    @XmlElement(name = "HostResource", nillable = true)
    protected List<CimString> hostResource;
    @XmlElement(name = "InstanceID", required = true)
    protected CimString instanceID;
    @XmlElement(name = "Limit", nillable = true)
    protected CimUnsignedLong limit;
    @XmlElement(name = "MappingBehavior", nillable = true)
    protected MappingBehavior mappingBehavior;
    @XmlElement(name = "OtherResourceType", nillable = true)
    protected CimString otherResourceType;
    @XmlElement(name = "Parent", nillable = true)
    protected CimString parent;
    @XmlElement(name = "PoolID", nillable = true)
    protected CimString poolID;
    @XmlElement(name = "Reservation", nillable = true)
    protected CimUnsignedLong reservation;
    @XmlElement(name = "ResourceSubType", nillable = true)
    protected CimString resourceSubType;
    @XmlElement(name = "ResourceType", nillable = true)
    protected ResourceType resourceType;
    @XmlElement(name = "VirtualQuantity", nillable = true)
    protected CimUnsignedLong virtualQuantity;
    @XmlElement(name = "VirtualQuantityUnits", nillable = true)
    protected CimString virtualQuantityUnits;
    @XmlElement(name = "Weight", nillable = true)
    protected CimUnsignedInt weight;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

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
