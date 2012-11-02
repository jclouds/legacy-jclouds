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

import java.net.URI;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
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
import org.jclouds.vcloud.director.v1_5.domain.Reference;
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
 * Represents the parameters for capturing a vApp to a vApp template.
 *
 * <pre>
 * &lt;complexType name="CaptureVAppParams">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}ParamsType">
 *       &lt;sequence>
 *         &lt;element name="Source" type="{http://www.vmware.com/vcloud/v1.5}ReferenceType"/>
 *         &lt;element ref="{http://schemas.dmtf.org/ovf/envelope/1}Section" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlType(name = "CaptureVAppParams", propOrder = {
      "source",
      "sections"
})
@XmlRootElement(name = "CaptureVAppParams")
public class CaptureVAppParams extends ParamsType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromCaptureVAppParams(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }

   public abstract static class Builder<B extends Builder<B>> extends ParamsType.Builder<B> {

      private Reference source;
      private Set<SectionType> sections = Sets.newLinkedHashSet();

      /**
       * @see CaptureVAppParams#getSource()
       */
      public B source(Reference source) {
         this.source = source;
         return self();
      }

      /**
       * Sets source to a new Reference that uses this URI as the href.
       *
       * @see CaptureVAppParams#getSource()
       */
      public B source(URI source) {
         this.source = Reference.builder().href(source).build();
         return self();
      }

      /**
       * @see CaptureVAppParams#getSections()
       */
      public B section(SectionType section) {
         this.sections.add(checkNotNull(section, "section"));
         return self();
      }

      /**
       * @see CaptureVAppParams#getSections()
       */
      public B sections(Iterable<? extends SectionType> sections) {
         this.sections = Sets.newLinkedHashSet(checkNotNull(sections, "sections"));
         return self();
      }

      @Override
      public CaptureVAppParams build() {
         return new CaptureVAppParams(this);
      }

      public B fromCaptureVAppParams(CaptureVAppParams in) {
         return fromParamsType(in)
               .source(in.getSource())
               .sections(in.getSections());
      }
   }

   private CaptureVAppParams(Builder<?> builder) {
      super(builder);
      this.source = builder.source;
      this.sections = builder.sections.isEmpty() ? null : ImmutableSet.copyOf(builder.sections);
   }

   private CaptureVAppParams() {
      // for JAXB
   }

   private CaptureVAppParams(Set<? extends SectionType> sections) {
      this.sections = ImmutableSet.copyOf(sections);
   }

   @XmlElement(name = "Source", required = true)
   protected Reference source;
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
    * Gets the value of the source property.
    */
   public Reference getSource() {
      return source;
   }

   /**
    * An {@code ovf:Section} to configure the captured vAppTemplate.
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

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      CaptureVAppParams that = CaptureVAppParams.class.cast(o);
      return equal(source, that.source)
            && equal(sections, that.sections);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(source,
            sections);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("source", source)
            .add("sections", sections).toString();
   }

}
