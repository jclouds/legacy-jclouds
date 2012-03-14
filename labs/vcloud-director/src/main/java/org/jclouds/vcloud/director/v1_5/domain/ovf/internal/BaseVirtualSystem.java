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
package org.jclouds.vcloud.director.v1_5.domain.ovf.internal;

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.*;
import static com.google.common.base.Objects.*;
import static com.google.common.base.Preconditions.*;

import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.director.v1_5.domain.ovf.OperatingSystemSection;
import org.jclouds.vcloud.director.v1_5.domain.ovf.ProductSection;
import org.jclouds.vcloud.director.v1_5.domain.ovf.SectionType;
import org.jclouds.vcloud.director.v1_5.domain.ovf.VirtualHardwareSection;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public abstract class BaseVirtualSystem<T extends BaseVirtualSystem<T>> extends SectionType<T> {

   public static abstract class Builder<T extends BaseVirtualSystem<T>> extends SectionType.Builder<T> {

      protected String id;
      protected String name;
      protected OperatingSystemSection operatingSystem;
      protected Set<VirtualHardwareSection> virtualHardwareSections = Sets.newLinkedHashSet();
      protected Set<ProductSection> productSections = Sets.newLinkedHashSet();
      protected Set<SectionType<?>> additionalSections = Sets.newLinkedHashSet();

      /**
       * @see BaseVirtualSystem#getName
       */
      public Builder<T> name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see BaseVirtualSystem#getId
       */
      public Builder<T> id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see BaseVirtualSystem#getOperatingSystemSection
       */
      public Builder<T> operatingSystemSection(OperatingSystemSection operatingSystem) {
         this.operatingSystem = operatingSystem;
         return this;
      }

      /**
       * @see BaseVirtualSystem#getVirtualHardwareSections
       */
      public Builder<T> virtualHardwareSection(VirtualHardwareSection virtualHardwareSection) {
         this.virtualHardwareSections.add(checkNotNull(virtualHardwareSection, "virtualHardwareSection"));
         return this;
      }

      /**
       * @see BaseVirtualSystem#getVirtualHardwareSections
       */
      public Builder<T> virtualHardwareSections(Iterable<? extends VirtualHardwareSection> virtualHardwareSections) {
         this.virtualHardwareSections = ImmutableSet.<VirtualHardwareSection> copyOf(checkNotNull(virtualHardwareSections,
                  "virtualHardwareSections"));
         return this;
      }

      /**
       * @see BaseVirtualSystem#getProductSections
       */
      public Builder<T> productSection(ProductSection productSection) {
         this.productSections.add(checkNotNull(productSection, "productSection"));
         return this;
      }

      /**
       * @see BaseVirtualSystem#getProductSections
       */
      public Builder<T> productSections(Iterable<? extends ProductSection> productSections) {
         this.productSections = ImmutableSet.<ProductSection> copyOf(checkNotNull(productSections, "productSections"));
         return this;
      }

      /**
       * @see BaseVirtualSystem#getAdditionalSections
       */
      public Builder<T> additionalSection(SectionType<?> additionalSection) {
         this.additionalSections.add(checkNotNull(additionalSection, "additionalSection"));
         return this;
      }

      /**
       * @see BaseVirtualSystem#getAdditionalSections
       */
      public Builder<T> additionalSections(Set<SectionType<?>> additionalSections) {
         this.additionalSections = checkNotNull(additionalSections, "additionalSections");
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public abstract BaseVirtualSystem<T> build();

      public Builder<T> fromVirtualSystem(BaseVirtualSystem<T> in) {
         return fromSectionType(in).id(in.getId()).name(in.getName())
                  .operatingSystemSection(in.getOperatingSystemSection())
                  .virtualHardwareSections(in.getVirtualHardwareSections())
                  .productSections(in.getProductSections())
                  .additionalSections(in.getAdditionalSections());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder<T> fromSectionType(SectionType<T> in) {
         return (Builder<T>) super.fromSectionType(in);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder<T> info(String info) {
         return (Builder<T>) super.info(info);
      }

   }

   @XmlAttribute(namespace = VCLOUD_OVF_NS)
   private String id;
   @XmlElement(name = "Name")
   private String name;
   @XmlElement(name = "OperatingSystemSection")
   private OperatingSystemSection operatingSystem;
   @XmlElement(name = "VirtualHardwareSection")
   private Set<VirtualHardwareSection> virtualHardwareSections;
   @XmlElement(name = "ProductSection")
   private Set<ProductSection> productSections;

   // NOTE what is the right annotation here?
   private Set<SectionType<?>> additionalSections;

   protected BaseVirtualSystem(String id, String info, @Nullable Boolean required, String name, OperatingSystemSection operatingSystem,
            Iterable<? extends VirtualHardwareSection> virtualHardwareSections,
            Iterable<? extends ProductSection> productSections, Iterable<? extends SectionType<?>> additionalSections) {
      super(info, required);
      this.id = id;
      this.name = name;
      this.operatingSystem = checkNotNull(operatingSystem, "operatingSystem");
      this.virtualHardwareSections = ImmutableSet.copyOf(checkNotNull(virtualHardwareSections, "virtualHardwareSections"));
      this.productSections = ImmutableSet.copyOf(checkNotNull(productSections, "productSections"));
      this.additionalSections = ImmutableSet.copyOf(checkNotNull(additionalSections, "additionalSections"));
   }

   protected BaseVirtualSystem() {
      // For JAXB      
   }
   

   public String getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public OperatingSystemSection getOperatingSystemSection() {
      return operatingSystem;
   }

   /**
    * Each VirtualSystem element may contain one or more VirtualHardwareSection elements, each of
    * which describes the virtual virtualHardwareSections required by the virtual system.
    */
   public Set<? extends VirtualHardwareSection> getVirtualHardwareSections() {
      return virtualHardwareSections;
   }

   /**
    * Specifies product-information for a package, such as product name and version, along with a
    * set of properties that can be configured
    */
   public Set<? extends ProductSection> getProductSections() {
      return productSections;
   }

   public Set<SectionType<?>> getAdditionalSections() {
      return additionalSections;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), id, name, operatingSystem, virtualHardwareSections, productSections, additionalSections);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;

      BaseVirtualSystem<?> other = (BaseVirtualSystem<?>) obj;
      return super.equals(other) 
            && equal(id, other.id)
            && equal(name, other.name)
            && equal(operatingSystem, other.operatingSystem)
            && equal(virtualHardwareSections, other.virtualHardwareSections)
            && equal(productSections, other.productSections)
            && equal(additionalSections, other.additionalSections);
   }

   @Override
   protected Objects.ToStringHelper string() {
      return super.string().add("id", id).add("name", name)
            .add("operatingSystem", operatingSystem).add("virtualHardwareSections", virtualHardwareSections)
            .add("productSections", productSections).add("additionalSections", additionalSections)
            .add("additionalSections", additionalSections);
   }
}