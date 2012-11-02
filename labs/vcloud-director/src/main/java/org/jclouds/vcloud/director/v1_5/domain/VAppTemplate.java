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

import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.dmtf.ovf.DeploymentOptionSection;
import org.jclouds.dmtf.ovf.DiskSection;
import org.jclouds.dmtf.ovf.NetworkSection;
import org.jclouds.dmtf.ovf.ProductSection;
import org.jclouds.dmtf.ovf.SectionType;
import org.jclouds.dmtf.ovf.StartupSection;
import org.jclouds.vcloud.director.v1_5.domain.section.CustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.section.GuestCustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.section.LeaseSettingsSection;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConfigSection;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConnectionSection;
import org.jclouds.vcloud.director.v1_5.domain.section.OperatingSystemSection;
import org.jclouds.vcloud.director.v1_5.domain.section.RuntimeInfoSection;
import org.jclouds.vcloud.director.v1_5.domain.section.VirtualHardwareSection;

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
public class VAppTemplate extends ResourceEntity {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromVAppTemplate(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> extends ResourceEntity.Builder<B> {
      private Owner owner;
      private Set<Vm> children = Sets.newLinkedHashSet();
      private Set<SectionType> sections = Sets.newLinkedHashSet();
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
      public B children(Iterable<Vm> children) {
         this.children = Sets.newLinkedHashSet(checkNotNull(children, "children"));
         return self();
      }

      /**
       * @see VAppTemplate#getSections()
       */
      public B sections(Iterable<? extends SectionType> sections) {
         this.sections = Sets.newLinkedHashSet(checkNotNull(sections, "sections"));
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
   @XmlElementWrapper(name = "Children")
   @XmlElement(name = "Vm")
   private Set<Vm> children = Sets.newLinkedHashSet();
   @XmlElementRefs({
      @XmlElementRef(type = VirtualHardwareSection.class),
      @XmlElementRef(type = LeaseSettingsSection.class),
//      @XmlElementRef(type = EulaSection.class),
      @XmlElementRef(type = RuntimeInfoSection.class),
//      @XmlElementRef(type = AnnotationSection.class),
      @XmlElementRef(type = DeploymentOptionSection.class),
      @XmlElementRef(type = StartupSection.class),
//      @XmlElementRef(type = ResourceAllocationSection.class),
      @XmlElementRef(type = NetworkConnectionSection.class),
      @XmlElementRef(type = CustomizationSection.class),
      @XmlElementRef(type = ProductSection.class),
      @XmlElementRef(type = GuestCustomizationSection.class),
      @XmlElementRef(type = OperatingSystemSection.class),
      @XmlElementRef(type = NetworkConfigSection.class),
      @XmlElementRef(type = NetworkSection.class),
//      @XmlElementRef(type = InstallSection.class),
      @XmlElementRef(type = DiskSection.class)
   })
   private Set<SectionType> sections = Sets.newLinkedHashSet();
   @XmlElement(name = "VAppScopedLocalId")
   private String vAppScopedLocalId;
   @XmlAttribute
   private Boolean ovfDescriptorUploaded;
   @XmlAttribute
   private Boolean goldMaster;

   protected VAppTemplate(Builder<?> builder) {
      super(builder);
      this.owner = builder.owner;
      this.children = builder.children.isEmpty() ? ImmutableSet.<Vm>of() : ImmutableSet.copyOf(builder.children);
      this.sections = builder.sections.isEmpty() ? null : ImmutableSet.copyOf(builder.sections);
      this.vAppScopedLocalId = builder.vAppScopedLocalId;
      this.ovfDescriptorUploaded = builder.ovfDescriptorUploaded;
      this.goldMaster = builder.goldMaster;
   }

   protected VAppTemplate() {
      // For JAXB
   }

   /**
    * Gets the value of the owner property.
    */
   public Owner getOwner() {
      return owner;
   }

   /**
    * Gets the value of the children property.
    */
   public Set<Vm> getChildren() {
      return children;
   }

   /**
    * Contains ovf sections for vApp template.
    *
    * Objects of the following type(s) are allowed in the list:
    * <ul>
    * <li>{@link VirtualHardwareSectionType}
    * <li>{@link LeaseSettingsSectionType}
    * <li>{@link EulaSectionType}
    * <li>{@link RuntimeInfoSectionType}
    * <li>{@link AnnotationSectionType}
    * <li>{@link DeploymentOptionSectionType}
    * <li>{@link StartupSectionType}
    * <li>{@link ResourceAllocationSectionType}
    * <li>{@link NetworkConnectionSectionType}
    * <li>{@link CustomizationSectionType}
    * <li>{@link ProductSectionType}
    * <li>{@link GuestCustomizationSectionType}
    * <li>{@link OperatingSystemSectionType}
    * <li>{@link NetworkConfigSectionType}
    * <li>{@link NetworkSectionType}
    * <li>{@link DiskSectionType}
    * <li>{@link InstallSectionType}
    * </ul>
    */
   public Set<SectionType> getSections() {
      return sections != null ? ImmutableSet.copyOf(sections) : ImmutableSet.<SectionType>of();
   }

   /**
    * Gets the value of the vAppScopedLocalId property.
    */
   public String getVAppScopedLocalId() {
      return vAppScopedLocalId;
   }

   /**
    * Gets the value of the ovfDescriptorUploaded property.
    */
   public Boolean isOvfDescriptorUploaded() {
      return ovfDescriptorUploaded;
   }

   /**
    * Gets the value of the goldMaster property.
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
