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
import javax.xml.bind.annotation.XmlType;

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
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Represents a base type for VAppType and VmType.
 *
 * <pre>
 * &lt;complexType name="AbstractVAppType" &gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlType(name = "AbstractVAppType")
public abstract class AbstractVAppType extends ResourceEntity {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromResourceEntityType(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> extends ResourceEntity.Builder<B> {

      private Boolean deployed;
      private Reference vAppParent;
      private Set<SectionType> sections = Sets.newLinkedHashSet();

      /**
       * @see AbstractVAppType#isDeployed()
       */
      public B isDeployed(Boolean deployed) {
         this.deployed = deployed;
         return self();
      }

      /**
       * @see AbstractVAppType#isDeployed()
       */
      public B deployed() {
         this.deployed = Boolean.TRUE;
         return self();
      }

      /**
       * @see AbstractVAppType#isDeployed()
       */
      public B notDeployed() {
         this.deployed = Boolean.FALSE;
         return self();
      }

      /**
       * @see AbstractVAppType#getVAppParent()
       */
      public B parent(Reference vAppParent) {
         this.vAppParent = vAppParent;
         return self();
      }

      /**
       * @see AbstractVAppType#getSections()
       */
      public B sections(Iterable<? extends SectionType> sections) {
         this.sections = Sets.newLinkedHashSet(checkNotNull(sections, "sections"));
         return self();
      }

      /**
       * @see AbstractVAppType#getSections()
       */
      public B section(SectionType section) {
         this.sections.add(checkNotNull(section, "section"));
         return self();
      }

      public B fromAbstractVAppType(AbstractVAppType in) {
         return fromResourceEntityType(in).parent(vAppParent).sections(sections).isDeployed(deployed);
      }
   }

   @XmlElement(name = "VAppParent")
   private Reference vAppParent;
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
   @XmlAttribute
   private Boolean deployed;

   protected AbstractVAppType() {
      // for JAXB and Builders
   }

   protected AbstractVAppType(Builder<?> builder) {
      super(builder);
      this.vAppParent = builder.vAppParent;
      this.sections = builder.sections.isEmpty() ? null : ImmutableSet.copyOf(builder.sections);
      this.deployed = builder.deployed;
   }

   /**
    * Gets the value of the vAppParent property.
    */
   public Reference getVAppParent() {
      return vAppParent;
   }

   /**
    * Specific {@code ovf:Section} with additional information for the vApp.
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
    * Gets the value of the deployed property.
    */
   public Boolean isDeployed() {
      return deployed;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      AbstractVAppType that = AbstractVAppType.class.cast(o);
      return super.equals(that)
            && equal(this.vAppParent, that.vAppParent)
            && equal(this.sections, that.sections)
            && equal(this.deployed, that.deployed);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), vAppParent, sections, deployed);
   }

   @Override
   public ToStringHelper string() {
      return super.string()
            .add("vAppParent", vAppParent)
            .add("sections", sections)
            .add("deployed", deployed);
   }
}
