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

import static com.google.common.base.Objects.*;
import static com.google.common.base.Preconditions.*;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.vcloud.director.v1_5.domain.ovf.SectionType;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Represents a vApp template.
 *
 * <pre>
 * &lt;complexType name="VAppTemplate" /&gt;
 * </pre>
 */
@XmlRootElement(name = "VAppTemplate")
public class VAppTemplate extends ResourceEntityType<VAppTemplate> {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromVAppTemplate(this);
   }

   public static class Builder extends ResourceEntityType.Builder<VAppTemplate> {
      private Owner owner;
      private Set<VAppTemplate> children = Sets.newLinkedHashSet();
      private Set<? extends SectionType<?>> sections = Sets.newLinkedHashSet();
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
      public Builder children(Set<VAppTemplate> children) {
         this.children = checkNotNull(children, "children");
         return this;
      }

      /**
       * @see VAppTemplate#getSections()
       */
      public Builder sections(Set<? extends SectionType<?>> sections) {
         this.sections = checkNotNull(sections, "sections");
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
         return new VAppTemplate(href, type, links, description, tasks, id, name, files, status, owner, children, sections, vAppScopedLocalId, ovfDescriptorUploaded, goldMaster);
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
       * @see EntityType#getTasks()
       */
      @Override
      public Builder tasks(Set<Task> tasks) {
         super.tasks(tasks);
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

   @XmlElement(name = "Owner")
   protected Owner owner;
   @XmlElement(name = "Children")
   protected VAppTemplateChildren children = VAppTemplateChildren.builder().build();
   @XmlElementRef
   protected Set<? extends SectionType<?>> sections = Sets.newLinkedHashSet();
   @XmlElement(name = "VAppScopedLocalId")
   protected String vAppScopedLocalId;
   @XmlAttribute
   protected Boolean ovfDescriptorUploaded;
   @XmlAttribute
   protected Boolean goldMaster;

   private VAppTemplate(URI href, String type, Set<Link> links, String description, Set<Task> tasks,
                        String id, String name, FilesList files, Integer status, Owner owner, Set<VAppTemplate> children,
                        Set<? extends SectionType<?>> sections, String vAppScopedLocalId, Boolean ovfDescriptorUploaded, Boolean goldMaster) {
      super(href, type, links, description, tasks, id, name, files, status);
      this.owner = owner;
      this.children = VAppTemplateChildren.builder().vms(children).build();
      this.sections = ImmutableSet.copyOf(sections);
      this.vAppScopedLocalId = vAppScopedLocalId;
      this.ovfDescriptorUploaded = ovfDescriptorUploaded;
      this.goldMaster = goldMaster;
   }

   private VAppTemplate() {
      // For JAXB
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
    * Gets the value of the children property.
    *
    * @return possible object is
    *         {@link VAppTemplateChildren }
    */
   public Set<VAppTemplate> getChildren() {
      return children.getVms();
   }

   /**
    * Contains ovf sections for vApp template.
    * Gets the value of the section property.
    * <p/>
    * Objects of the following type(s) are allowed in the list
    * {@link SectionType }
    * {@link VirtualHardwareSection }
    * {@link LeaseSettingsSection }
    * {@link EulaSection }
    * {@link RuntimeInfoSection }
    * {@link AnnotationSection }
    * {@link DeploymentOptionSection }
    * {@link StartupSection }
    * {@link ResourceAllocationSection }
    * {@link NetworkConnectionSection }
    * {@link CustomizationSection }
    * {@link ProductSection }
    * {@link GuestCustomizationSection }
    * {@link OperatingSystemSection }
    * {@link NetworkConfigSection }
    * {@link NetworkSection }
    * {@link DiskSection }
    * {@link InstallSection }
    */
   public Set<? extends SectionType<?>> getSections() {
      return Collections.unmodifiableSet(this.sections);
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
    * Gets the value of the ovfDescriptorUploaded property.
    *
    * @return possible object is
    *         {@link Boolean }
    */
   public Boolean isOvfDescriptorUploaded() {
      return ovfDescriptorUploaded;
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
