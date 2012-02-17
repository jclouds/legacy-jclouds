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
package org.jclouds.vcloud.director.v1_5.domain.vapp;

import org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType;


/**
 * Represents a base type for VAppType and VmType.
 *
 * <pre>
 * &lt;complexType name="AbstractVAppType" &gt;
 * </pre>
 * 
 * @author grkvlt@apache.org
 */
public abstract class AbstractVAppType extends ResourceEntityType<AbstractVAppType> {

//    @XmlElement(name = "VAppParent")
//    protected ReferenceType vAppParent;
//    @XmlElementRef(name = "Section", namespace = "http://schemas.dmtf.org/ovf/envelope/1", type = JAXBElement.class)
//    protected List<JAXBElement<? extends SectionType>> section;
//    @XmlAttribute
//    protected Boolean deployed;
//
//    /**
//     * Gets the value of the vAppParent property.
//     * 
//     * @return
//     *     possible object is
//     *     {@link ReferenceType }
//     *     
//     */
//    public ReferenceType getVAppParent() {
//        return vAppParent;
//    }
//
//    /**
//     * Sets the value of the vAppParent property.
//     * 
//     * @param value
//     *     allowed object is
//     *     {@link ReferenceType }
//     *     
//     */
//    public void setVAppParent(ReferenceType value) {
//        this.vAppParent = value;
//    }
//
//    /**
//     * 
//     *                                 Specific ovf:Section with additional information for the vApp.
//     *                             Gets the value of the section property.
//     * 
//     * <p>
//     * This accessor method returns a reference to the live list,
//     * not a snapshot. Therefore any modification you make to the
//     * returned list will be present inside the JAXB object.
//     * This is why there is not a <CODE>set</CODE> method for the section property.
//     * 
//     * <p>
//     * For example, to add a new item, do as follows:
//     * <pre>
//     *    getSection().add(newItem);
//     * </pre>
//     * 
//     * 
//     * <p>
//     * Objects of the following type(s) are allowed in the list
//     * {@link JAXBElement }{@code <}{@link SectionType }{@code >}
//     * {@link JAXBElement }{@code <}{@link VirtualHardwareSectionType }{@code >}
//     * {@link JAXBElement }{@code <}{@link LeaseSettingsSectionType }{@code >}
//     * {@link JAXBElement }{@code <}{@link EulaSectionType }{@code >}
//     * {@link JAXBElement }{@code <}{@link RuntimeInfoSectionType }{@code >}
//     * {@link JAXBElement }{@code <}{@link AnnotationSectionType }{@code >}
//     * {@link JAXBElement }{@code <}{@link DeploymentOptionSectionType }{@code >}
//     * {@link JAXBElement }{@code <}{@link StartupSectionType }{@code >}
//     * {@link JAXBElement }{@code <}{@link ResourceAllocationSectionType }{@code >}
//     * {@link JAXBElement }{@code <}{@link NetworkConnectionSectionType }{@code >}
//     * {@link JAXBElement }{@code <}{@link CustomizationSectionType }{@code >}
//     * {@link JAXBElement }{@code <}{@link ProductSectionType }{@code >}
//     * {@link JAXBElement }{@code <}{@link GuestCustomizationSectionType }{@code >}
//     * {@link JAXBElement }{@code <}{@link OperatingSystemSectionType }{@code >}
//     * {@link JAXBElement }{@code <}{@link NetworkConfigSectionType }{@code >}
//     * {@link JAXBElement }{@code <}{@link NetworkSectionType }{@code >}
//     * {@link JAXBElement }{@code <}{@link DiskSectionType }{@code >}
//     * {@link JAXBElement }{@code <}{@link InstallSectionType }{@code >}
//     * 
//     * 
//     */
//    public List<JAXBElement<? extends SectionType>> getSection() {
//        if (section == null) {
//            section = new ArrayList<JAXBElement<? extends SectionType>>();
//        }
//        return this.section;
//    }
//
//    /**
//     * Gets the value of the deployed property.
//     * 
//     * @return
//     *     possible object is
//     *     {@link Boolean }
//     *     
//     */
//    public Boolean isDeployed() {
//        return deployed;
//    }
//
//    /**
//     * Sets the value of the deployed property.
//     * 
//     * @param value
//     *     allowed object is
//     *     {@link Boolean }
//     *     
//     */
//    public void setDeployed(Boolean value) {
//        this.deployed = value;
//    }

}
