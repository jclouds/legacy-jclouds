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
import static com.google.common.base.Preconditions.checkNotNull;

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
public class VAppTemplate extends ResourceEntityType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromVAppTemplate(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static abstract class Builder<B extends Builder<B>> extends ResourceEntityType.Builder<B> {
      private Owner owner;
      private Set<VAppTemplate> children = Sets.newLinkedHashSet();
      private Set<? extends SectionType> sections = Sets.newLinkedHashSet();
      private String vAppScopedLocalId;
      private Boolean ovfDescriptorUploaded;
      private Boolean goldMaster;

      /**
       * @see VAppTemplate#getOwner()
       */
      public B owner(Owner owner) {
         this.owner = owner;
         return self();
      }

      /**
       * @see VAppTemplate#getChildren()
       */
      public B children(Set<VAppTemplate> children) {
         this.children = checkNotNull(children, "children");
         return self();
      }

      /**
       * @see VAppTemplate#getSections()
       */
      public B sections(Set<? extends SectionType> sections) {
         this.sections = checkNotNull(sections, "sections");
         return self();
      }

      /**
       * @see VAppTemplate#getVAppScopedLocalId()
       */
      public B vAppScopedLocalId(String vAppScopedLocalId) {
         this.vAppScopedLocalId = vAppScopedLocalId;
         return self();
      }

      /**
       * @see VAppTemplate#isOvfDescriptorUploaded()
       */
      public B ovfDescriptorUploaded(Boolean ovfDescriptorUploaded) {
         this.ovfDescriptorUploaded = ovfDescriptorUploaded;
         return self();
      }

      /**
       * @see VAppTemplate#isGoldMaster()
       */
      public B goldMaster(Boolean goldMaster) {
         this.goldMaster = goldMaster;
         return self();
      }

      @Override
      public VAppTemplate build() {
         return new VAppTemplate(this);
      }

      public B fromVAppTemplate(VAppTemplate in) {
         return fromResourceEntityType(in)
               .owner(in.getOwner())
               .children(in.getChildren())
               .sections(in.getSections())
               .vAppScopedLocalId(in.getVAppScopedLocalId())
               .ovfDescriptorUploaded(in.isOvfDescriptorUploaded())
               .goldMaster(in.isGoldMaster());
      }
   }

   @XmlElement(name = "Owner")
   private Owner owner;
   @XmlElement(name = "Children")
   private VAppTemplateChildren children = VAppTemplateChildren.builder().build();
   @XmlElementRef
   private Set<? extends SectionType> sections = Sets.newLinkedHashSet();
   @XmlElement(name = "VAppScopedLocalId")
   private String vAppScopedLocalId;
   @XmlAttribute
   private Boolean ovfDescriptorUploaded;
   @XmlAttribute
   private Boolean goldMaster;

   protected VAppTemplate(Builder<?> builder) {
      super(builder);
      this.owner = builder.owner;
      this.children = VAppTemplateChildren.builder().vms(builder.children).build();
      this.sections = ImmutableSet.copyOf(builder.sections);
      this.vAppScopedLocalId = builder.vAppScopedLocalId;
      this.ovfDescriptorUploaded = builder.ovfDescriptorUploaded;
      this.goldMaster = builder.goldMaster;
   }

   protected VAppTemplate() {
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
   public Set<? extends SectionType> getSections() {
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
