/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.ovf.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.jclouds.ovf.OperatingSystemSection;
import org.jclouds.ovf.ProductSection;
import org.jclouds.ovf.Section;
import org.jclouds.ovf.VirtualHardwareSection;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class BaseVirtualSystem<T extends BaseVirtualSystem<T>> extends Section<T> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder<T> toBuilder() {
      return new Builder<T>().fromVirtualSystem(this);
   }

   public static class Builder<T extends BaseVirtualSystem<T>> extends Section.Builder<T> {
      protected String id;
      protected String name;
      protected OperatingSystemSection operatingSystem;
      protected Set<VirtualHardwareSection> virtualHardwareSections = Sets.newLinkedHashSet();
      protected Set<ProductSection> productSections = Sets.newLinkedHashSet();
      @SuppressWarnings("unchecked")
      protected Multimap<String, Section> additionalSections = LinkedHashMultimap.create();

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
      @SuppressWarnings("unchecked")
      public Builder<T> additionalSection(String name, Section additionalSection) {
         this.additionalSections.put(checkNotNull(name, "name"), checkNotNull(additionalSection, "additionalSection"));
         return this;
      }

      /**
       * @see BaseVirtualSystem#getAdditionalSections
       */
      @SuppressWarnings("unchecked")
      public Builder<T> additionalSections(Multimap<String, Section> additionalSections) {
         this.additionalSections = ImmutableMultimap.<String, Section> copyOf(checkNotNull(additionalSections,
                  "additionalSections"));
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public BaseVirtualSystem<T> build() {
         return new BaseVirtualSystem<T>(id, info, name, operatingSystem, virtualHardwareSections, productSections,
                  additionalSections);
      }

      public Builder<T> fromVirtualSystem(BaseVirtualSystem<T> in) {
         return fromSection(in).id(in.getId()).name(in.getName())
                  .operatingSystemSection(in.getOperatingSystemSection()).virtualHardwareSections(
                           in.getVirtualHardwareSections()).productSections(in.getProductSections())
                  .additionalSections(in.getAdditionalSections());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder<T> fromSection(Section<T> in) {
         return (Builder<T>) super.fromSection(in);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder<T> info(String info) {
         return (Builder<T>) super.info(info);
      }

   }

   protected final String id;
   protected final String name;
   protected final OperatingSystemSection operatingSystem;
   protected final Set<VirtualHardwareSection> virtualHardwareSections;
   protected final Set<ProductSection> productSections;
   @SuppressWarnings("unchecked")
   protected final Multimap<String, Section> additionalSections;

   @SuppressWarnings("unchecked")
   public BaseVirtualSystem(String id, String info, String name, OperatingSystemSection operatingSystem,
            Iterable<? extends VirtualHardwareSection> virtualHardwareSections,
            Iterable<? extends ProductSection> productSections, Multimap<String, Section> additionalSections) {
      super(info);
      this.id = id;
      this.name = name;
      this.operatingSystem = checkNotNull(operatingSystem, "operatingSystem");
      this.virtualHardwareSections = ImmutableSet.copyOf(checkNotNull(virtualHardwareSections, "virtualHardwareSections"));
      this.productSections = ImmutableSet.copyOf(checkNotNull(productSections, "productSections"));
      this.additionalSections = ImmutableMultimap.copyOf(checkNotNull(additionalSections, "additionalSections"));
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

   @SuppressWarnings("unchecked")
   public Multimap<String, Section> getAdditionalSections() {
      return additionalSections;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      BaseVirtualSystem<?> other = (BaseVirtualSystem<?>) obj;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return String
               .format(
                        "[id=%s, name=%s, info=%s, operatingSystem=%s, virtualHardwareSections=%s,  productSections=%s, additionalSections=%s]",
                        id, name, info, operatingSystem, virtualHardwareSections, productSections, additionalSections);
   }
}
