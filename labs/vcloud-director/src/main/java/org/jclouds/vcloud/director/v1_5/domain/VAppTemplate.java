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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.ovf.Section;

import com.google.common.base.Objects;


/**
 * 
 *                 Represents a vApp template.
 *             
 * 
 * <p>Java class for VAppTemplate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VAppTemplate">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}ResourceEntityType">
 *       &lt;sequence>
 *         &lt;element name="Owner" type="{http://www.vmware.com/vcloud/v1.5}OwnerType" minOccurs="0"/>
 *         &lt;element name="Children" type="{http://www.vmware.com/vcloud/v1.5}VAppTemplateChildrenType" minOccurs="0"/>
 *         &lt;element ref="{http://schemas.dmtf.org/ovf/envelope/1}Section" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="VAppScopedLocalId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="ovfDescriptorUploaded" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="goldMaster" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VAppTemplate", propOrder = {
    "owner",
    "children",
    "section",
    "vAppScopedLocalId"
})
public class VAppTemplate
    extends ResourceEntityType<VAppTemplate>

{
   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromVAppTemplate(this);
   }

   public static class Builder extends ResourceEntityType.Builder<VAppTemplate> {
      
      private Owner owner;
      private VAppTemplateChildren children;
      private List<JAXBElement<? extends Section<?>>> sections;
      private String vAppScopedLocalId;
      private Boolean ovfDescriptorUploaded;
      private Boolean goldMaster;

      /**
       * @see VAppTemplate#getOwner()
       */
      public Builder owner(Owner owner) {
         this.owner = owner;
         return this;
      }

      /**
       * @see VAppTemplate#getChildren()
       */
      public Builder children(VAppTemplateChildren children) {
         this.children = children;
         return this;
      }

      /**
       * @see VAppTemplate#getExtend()
       */
      public Builder extend(List<JAXBElement<? extends Section<?>>> sections) {
         this.sections = sections;
         return this;
      }

      /**
       * @see VAppTemplate#getVAppScopedLocalId()
       */
      public Builder vAppScopedLocalId(String vAppScopedLocalId) {
         this.vAppScopedLocalId = vAppScopedLocalId;
         return this;
      }

      /**
       * @see VAppTemplate#getOvfDescriptorUploaded()
       */
      public Builder ovfDescriptorUploaded(Boolean ovfDescriptorUploaded) {
         this.ovfDescriptorUploaded = ovfDescriptorUploaded;
         return this;
      }

      /**
       * @see VAppTemplate#getGoldMaster()
       */
      public Builder goldMaster(Boolean goldMaster) {
         this.goldMaster = goldMaster;
         return this;
      }


      public VAppTemplate build() {
         VAppTemplate vAppTemplate = new VAppTemplate(sections);
         vAppTemplate.setOwner(owner);
         vAppTemplate.setChildren(children);
         vAppTemplate.setVAppScopedLocalId(vAppScopedLocalId);
         vAppTemplate.setOvfDescriptorUploaded(ovfDescriptorUploaded);
         vAppTemplate.setGoldMaster(goldMaster);
         return vAppTemplate;
      }


      @Override
      public Builder fromResourceEntityType(ResourceEntityType<VAppTemplate> in) {
          return Builder.class.cast(super.fromResourceEntityType(in));
      }
      public Builder fromVAppTemplate(VAppTemplate in) {
         return fromResourceEntityType(in)
            .owner(in.getOwner())
            .children(in.getChildren())
            .extend(in.getSections())
            .vAppScopedLocalId(in.getVAppScopedLocalId())
            .ovfDescriptorUploaded(in.isOvfDescriptorUploaded())
            .goldMaster(in.isGoldMaster());
      }
   }

   private VAppTemplate() {
      // For JAXB and builder use
   }

   private VAppTemplate(List<JAXBElement<? extends Section<?>>> sections) {
      this.sections = sections;
   }


    @XmlElement(name = "Owner")
    protected Owner owner;
    @XmlElement(name = "Children")
    protected VAppTemplateChildren children;
    @XmlElementRef(name = "Section", namespace = "http://schemas.dmtf.org/ovf/envelope/1", type = JAXBElement.class)
    protected List<JAXBElement<? extends Section<?>>> sections;
    @XmlElement(name = "VAppScopedLocalId")
    protected String vAppScopedLocalId;
    @XmlAttribute
    protected Boolean ovfDescriptorUploaded;
    @XmlAttribute
    protected Boolean goldMaster;

    /**
     * Gets the value of the owner property.
     * 
     * @return
     *     possible object is
     *     {@link Owner }
     *     
     */
    public Owner getOwner() {
        return owner;
    }

    /**
     * Sets the value of the owner property.
     * 
     * @param value
     *     allowed object is
     *     {@link Owner }
     *     
     */
    public void setOwner(Owner value) {
        this.owner = value;
    }

    /**
     * Gets the value of the children property.
     * 
     * @return
     *     possible object is
     *     {@link VAppTemplateChildren }
     *     
     */
    public VAppTemplateChildren getChildren() {
        return children;
    }

    /**
     * Sets the value of the children property.
     * 
     * @param value
     *     allowed object is
     *     {@link VAppTemplateChildren }
     *     
     */
    public void setChildren(VAppTemplateChildren value) {
        this.children = value;
    }

    /**
     * 
     *                                 Contains ovf sections for vApp template.
     *                             Gets the value of the section property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the section property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSection().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link SectionType }{@code >}
     * {@link JAXBElement }{@code <}{@link VirtualHardwareSectionType }{@code >}
     * {@link JAXBElement }{@code <}{@link LeaseSettingsSectionType }{@code >}
     * {@link JAXBElement }{@code <}{@link EulaSectionType }{@code >}
     * {@link JAXBElement }{@code <}{@link RuntimeInfoSectionType }{@code >}
     * {@link JAXBElement }{@code <}{@link AnnotationSectionType }{@code >}
     * {@link JAXBElement }{@code <}{@link DeploymentOptionSectionType }{@code >}
     * {@link JAXBElement }{@code <}{@link StartupSectionType }{@code >}
     * {@link JAXBElement }{@code <}{@link ResourceAllocationSectionType }{@code >}
     * {@link JAXBElement }{@code <}{@link NetworkConnectionSectionType }{@code >}
     * {@link JAXBElement }{@code <}{@link CustomizationSectionType }{@code >}
     * {@link JAXBElement }{@code <}{@link ProductSectionType }{@code >}
     * {@link JAXBElement }{@code <}{@link GuestCustomizationSectionType }{@code >}
     * {@link JAXBElement }{@code <}{@link OperatingSystemSectionType }{@code >}
     * {@link JAXBElement }{@code <}{@link NetworkConfigSectionType }{@code >}
     * {@link JAXBElement }{@code <}{@link NetworkSectionType }{@code >}
     * {@link JAXBElement }{@code <}{@link DiskSectionType }{@code >}
     * {@link JAXBElement }{@code <}{@link InstallSectionType }{@code >}
     * 
     * 
     */
    public List<JAXBElement<? extends Section<?>>> getSections() {
        if (sections == null) {
            sections = new ArrayList<JAXBElement<? extends Section<?>>>();
        }
        return this.sections;
    }

    /**
     * Gets the value of the vAppScopedLocalId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVAppScopedLocalId() {
        return vAppScopedLocalId;
    }

    /**
     * Sets the value of the vAppScopedLocalId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVAppScopedLocalId(String value) {
        this.vAppScopedLocalId = value;
    }

    /**
     * Gets the value of the ovfDescriptorUploaded property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isOvfDescriptorUploaded() {
        return ovfDescriptorUploaded;
    }

    /**
     * Sets the value of the ovfDescriptorUploaded property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOvfDescriptorUploaded(Boolean value) {
        this.ovfDescriptorUploaded = value;
    }

    /**
     * Gets the value of the goldMaster property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isGoldMaster() {
        if (goldMaster == null) {
            return false;
        } else {
            return goldMaster;
        }
    }

    /**
     * Sets the value of the goldMaster property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setGoldMaster(Boolean value) {
        this.goldMaster = value;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      VAppTemplate that = VAppTemplate.class.cast(o);
      return equal(owner, that.owner) && 
           equal(children, that.children) && 
           equal(sections, that.sections) && 
           equal(vAppScopedLocalId, that.vAppScopedLocalId) && 
           equal(ovfDescriptorUploaded, that.ovfDescriptorUploaded) && 
           equal(goldMaster, that.goldMaster);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(owner, 
           children, 
           sections, 
           vAppScopedLocalId, 
           ovfDescriptorUploaded, 
           goldMaster);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("owner", owner)
            .add("children", children)
            .add("sections", sections)
            .add("vAppScopedLocalId", vAppScopedLocalId)
            .add("ovfDescriptorUploaded", ovfDescriptorUploaded)
            .add("goldMaster", goldMaster).toString();
   }

}
