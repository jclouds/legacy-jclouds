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
package org.jclouds.vcloud.director.v1_5.domain.params;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;
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
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Represents a list of {@code ovf:Section} to configure for instantiating a VApp.
 *
 * @author grkvlt@apache.org
 * @see <a href="http://www.vmware.com/support/vcd/doc/rest-api-doc-1.5-html/types/InstantiationParamsType.html">
 *    vCloud REST API - InstantiationParamsType</a>
 * @since 0.9
 */
@XmlRootElement(name = "InstantiationParams")
@XmlType(name = "InstantiationParamsType")
public class InstantiationParams {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromInstantiationParams(this);
   }

   public static class Builder {
      private Set<SectionType> sections = Sets.newLinkedHashSet();

      /**
       * @see InstantiationParams#getSections()
       */
      public Builder sections(Iterable<? extends SectionType> sections) {
         this.sections = Sets.newLinkedHashSet(checkNotNull(sections, "sections"));
         return this;
      }

      /**
       * @see InstantiationParams#getSections()
       */
      public Builder section(SectionType section) {
         this.sections.add(checkNotNull(section, "section"));
         return this;
      }

      public InstantiationParams build() {
         InstantiationParams instantiationParams = new InstantiationParams(sections);
         return instantiationParams;
      }

      public Builder fromInstantiationParams(InstantiationParams in) {
         return sections(in.getSections());
      }
   }

   private InstantiationParams() {
      // for JAXB
   }

   private InstantiationParams(Set<? extends SectionType> sections) {
      this.sections = sections.isEmpty() ? null : ImmutableSet.copyOf(sections);
   }

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
   protected Set<SectionType> sections = Sets.newLinkedHashSet();

   /**
    * An {@code ovf:Section} to configure for instantiation.
    *
    * Objects of the following type(s) are allowed in the list:
    * <ul>
    * <li>{@link VirtualHardwareSection}
    * <li>{@link LeaseSettingsSection}
    * <li>{@link EulaSection}
    * <li>{@link RuntimeInfoSection}
    * <li>{@link AnnotationSection}
    * <li>{@link DeploymentOptionSection}
    * <li>{@link StartupSection}
    * <li>{@link ResourceAllocationSection}
    * <li>{@link NetworkConnectionSection}
    * <li>{@link CustomizationSection}
    * <li>{@link ProductSection}
    * <li>{@link GuestCustomizationSection}
    * <li>{@link OperatingSystemSection}
    * <li>{@link NetworkConfigSection}
    * <li>{@link NetworkSection}
    * <li>{@link DiskSection}
    * <li>{@link InstallSection}
    * </ul>
    */
   public Set<SectionType> getSections() {
      return sections != null ? ImmutableSet.copyOf(sections) : ImmutableSet.<SectionType>of();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      InstantiationParams that = InstantiationParams.class.cast(o);
      return equal(this.sections, that.sections);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(sections);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("sections", sections).toString();
   }
   
}
