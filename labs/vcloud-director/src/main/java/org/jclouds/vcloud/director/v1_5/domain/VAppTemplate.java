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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;


/**
 * Represents a vApp template.
 * <p/>
 * <p/>
 * <p>Java class for VAppTemplate complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
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
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "VAppTemplate")
@XmlType(propOrder = {
      "owner",
      "children",
      "sections",
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
      private List<SectionType> sections;
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
       * @see VAppTemplate#getSections()
       */
      public Builder sections(List<SectionType> sections) {
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
       * @see VAppTemplate#isOvfDescriptorUploaded()
       */
      public Builder ovfDescriptorUploaded(Boolean ovfDescriptorUploaded) {
         this.ovfDescriptorUploaded = ovfDescriptorUploaded;
         return this;
      }

      /**
       * @see VAppTemplate#isGoldMaster()
       */
      public Builder goldMaster(Boolean goldMaster) {
         this.goldMaster = goldMaster;
         return this;
      }

      @Override
      public VAppTemplate build() {
         VAppTemplate result = new VAppTemplate(href, name, owner, children, sections, vAppScopedLocalId, ovfDescriptorUploaded, goldMaster);
         result.setFiles(files);
         result.setStatus(status);
         result.setDescription(description);
         result.setTasksInProgress(tasksInProgress);
         result.setId(id);
         result.setType(type);
         result.setLinks(links);         
         return result;
      }


      @Override
      public Builder fromResourceEntityType(ResourceEntityType<VAppTemplate> in) {
         return Builder.class.cast(super.fromResourceEntityType(in));
      }

      public Builder fromVAppTemplate(VAppTemplate in) {
         return fromResourceEntityType(in)
               .owner(in.getOwner())
               .children(in.getChildren())
               .sections(in.getSections())
               .vAppScopedLocalId(in.getVAppScopedLocalId())
               .ovfDescriptorUploaded(in.isOvfDescriptorUploaded())
               .goldMaster(in.isGoldMaster());
      }


      /**
       * @see ResourceEntityType#getFiles()
       */
      @Override
      public Builder files(FilesList files) {
         super.files(files);
         return this;
      }

      /**
       * @see ResourceEntityType#getStatus()
       */
      @Override
      public Builder status(Integer status) {
         super.status(status);
         return this;
      }

      /**
       * @see EntityType#getName()
       */
      @Override
      public Builder name(String name) {
         super.name(name);
         return this;
      }

      /**
       * @see EntityType#getDescription()
       */
      @Override
      public Builder description(String description) {
         super.description(description);
         return this;
      }

      /**
       * @see EntityType#getId()
       */
      @Override
      public Builder id(String id) {
         super.id(id);
         return this;
      }

      /**
       * @see EntityType#getTasksInProgress()
       */
      @Override
      public Builder tasksInProgress(TasksInProgress tasksInProgress) {
         super.tasksInProgress(tasksInProgress);
         return this;
      }

      /**
       * @see ReferenceType#getHref()
       */
      @Override
      public Builder href(URI href) {
         super.href(href);
         return this;
      }

      /**
       * @see ReferenceType#getType()
       */
      @Override
      public Builder type(String type) {
         super.type(type);
         return this;
      }

      /**
       * @see EntityType#getLinks()
       */
      @Override
      public Builder links(Set<Link> links) {
         super.links(links);
         return this;
      }

      /**
       * @see EntityType#getLinks()
       */
      @Override
      public Builder link(Link link) {
         super.link(link);
         return this;
      }
   }

   @XmlElementRef
   protected Owner owner;
   @XmlElement(name = "Children")
   protected VAppTemplateChildren children;
   @XmlElementRef
   protected List<SectionType> sections;
   @XmlElement(name = "VAppScopedLocalId")
   protected String vAppScopedLocalId;
   @XmlAttribute
   protected Boolean ovfDescriptorUploaded;
   @XmlAttribute
   protected Boolean goldMaster;

   private VAppTemplate(URI href, String name, @Nullable Owner owner, @Nullable VAppTemplateChildren children, 
                        List<SectionType> sections, @Nullable String vAppScopedLocalId, 
                        @Nullable Boolean ovfDescriptorUploaded, @Nullable Boolean goldMaster) {
      super(href, name);
      this.sections = sections;
      this.owner = owner;
      this.children = children;
      this.vAppScopedLocalId = vAppScopedLocalId;
      this.ovfDescriptorUploaded = ovfDescriptorUploaded;
      this.goldMaster = goldMaster;
   }

   private VAppTemplate() {
      // For JAXB and builder use
   }

   /**
    * Gets the value of the owner property.
    *
    * @return possible object is
    *         {@link Owner }
    */
   public Owner getOwner() {
      return owner;
   }

   /**
    * Sets the value of the owner property.
    *
    * @param value allowed object is
    *              {@link Owner }
    */
   public void setOwner(Owner value) {
      this.owner = value;
   }

   /**
    * Gets the value of the children property.
    *
    * @return possible object is
    *         {@link VAppTemplateChildren }
    */
   public VAppTemplateChildren getChildren() {
      return children;
   }

   /**
    * Sets the value of the children property.
    *
    * @param value allowed object is
    *              {@link VAppTemplateChildren }
    */
   public void setChildren(VAppTemplateChildren value) {
      this.children = value;
   }

   /**
    * Contains ovf sections for vApp template.
    * Gets the value of the section property.
    * <p/>
    * <p/>
    * This accessor method returns a reference to the live list,
    * not a snapshot. Therefore any modification you make to the
    * returned list will be present inside the JAXB object.
    * This is why there is not a <CODE>set</CODE> method for the section property.
    * <p/>
    * <p/>
    * For example, to add a new item, do as follows:
    * <pre>
    *    getSection().add(newItem);
    * </pre>
    * <p/>
    * <p/>
    * <p/>
    * Objects of the following type(s) are allowed in the list
    * {@link JAXBElement }{@code <}{@link SectionType }{@code >}
    * {@link JAXBElement }{@code <}{@link VirtualHardwareSection }{@code >}
    * {@link JAXBElement }{@code <}{@link LeaseSettingsSection }{@code >}
    * {@link JAXBElement }{@code <}{@link EulaSection }{@code >}
    * {@link JAXBElement }{@code <}{@link RuntimeInfoSection }{@code >}
    * {@link JAXBElement }{@code <}{@link AnnotationSection }{@code >}
    * {@link JAXBElement }{@code <}{@link DeploymentOptionSection }{@code >}
    * {@link JAXBElement }{@code <}{@link StartupSection }{@code >}
    * {@link JAXBElement }{@code <}{@link ResourceAllocationSection }{@code >}
    * {@link JAXBElement }{@code <}{@link NetworkConnectionSection }{@code >}
    * {@link JAXBElement }{@code <}{@link CustomizationSection }{@code >}
    * {@link JAXBElement }{@code <}{@link ProductSection }{@code >}
    * {@link JAXBElement }{@code <}{@link GuestCustomizationSection }{@code >}
    * {@link JAXBElement }{@code <}{@link OperatingSystemSection }{@code >}
    * {@link JAXBElement }{@code <}{@link NetworkConfigSection }{@code >}
    * {@link JAXBElement }{@code <}{@link NetworkSection }{@code >}
    * {@link JAXBElement }{@code <}{@link DiskSection }{@code >}
    * {@link JAXBElement }{@code <}{@link InstallSection }{@code >}
    */
   public List<SectionType> getSections() {
      if (sections == null) {
         sections = new ArrayList<SectionType>();
      }
      return this.sections;
   }

   /**
    * Gets the value of the vAppScopedLocalId property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getVAppScopedLocalId() {
      return vAppScopedLocalId;
   }

   /**
    * Sets the value of the vAppScopedLocalId property.
    *
    * @param value allowed object is
    *              {@link String }
    */
   public void setVAppScopedLocalId(String value) {
      this.vAppScopedLocalId = value;
   }

   /**
    * Gets the value of the ovfDescriptorUploaded property.
    *
    * @return possible object is
    *         {@link Boolean }
    */
   public Boolean isOvfDescriptorUploaded() {
      return ovfDescriptorUploaded;
   }

   /**
    * Sets the value of the ovfDescriptorUploaded property.
    *
    * @param value allowed object is
    *              {@link Boolean }
    */
   public void setOvfDescriptorUploaded(Boolean value) {
      this.ovfDescriptorUploaded = value;
   }

   /**
    * Gets the value of the goldMaster property.
    *
    * @return possible object is
    *         {@link Boolean }
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
    * @param value allowed object is
    *              {@link Boolean }
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
      return super.equals(that) && 
            equal(owner, that.owner) &&
            equal(children, that.children) &&
            equal(sections, that.sections) &&
            equal(vAppScopedLocalId, that.vAppScopedLocalId) &&
            equal(ovfDescriptorUploaded, that.ovfDescriptorUploaded) &&
            equal(goldMaster, that.goldMaster);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(),
            owner,
            children,
            sections,
            vAppScopedLocalId,
            ovfDescriptorUploaded,
            goldMaster);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(super.toString())
            .add("owner", owner)
            .add("children", children)
            .add("sections", sections)
            .add("vAppScopedLocalId", vAppScopedLocalId)
            .add("ovfDescriptorUploaded", ovfDescriptorUploaded)
            .add("goldMaster", goldMaster).toString();
   }

}
