/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.ovf;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class VirtualSystem extends Section<VirtualSystem> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromVirtualSystem(this);
   }

   public static class Builder extends Section.Builder<VirtualSystem> {
      protected String id;
      protected String name;
      protected OperatingSystemSection operatingSystem;
      protected Set<VirtualHardwareSection> hardwareSections = Sets.newLinkedHashSet();
      @SuppressWarnings("unchecked")
      protected Set<Section> additionalSections = Sets.newLinkedHashSet();

      /**
       * @see VirtualSystem#getName
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see VirtualSystem#getId
       */
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see VirtualSystem#getOperatingSystemSection
       */
      public Builder operatingSystemSection(OperatingSystemSection operatingSystem) {
         this.operatingSystem = operatingSystem;
         return this;
      }

      /**
       * @see VirtualSystem#getVirtualHardwareSections
       */
      public Builder hardwareSection(VirtualHardwareSection hardwareSection) {
         this.hardwareSections.add(checkNotNull(hardwareSection, "hardwareSection"));
         return this;
      }

      /**
       * @see VirtualSystem#getVirtualHardwareSections
       */
      public Builder hardwareSections(Iterable<? extends VirtualHardwareSection> hardwareSections) {
         this.hardwareSections = ImmutableSet.<VirtualHardwareSection> copyOf(checkNotNull(hardwareSections,
                  "hardwareSections"));
         return this;
      }

      /**
       * @see VirtualSystem#getAdditionalSections
       */
      @SuppressWarnings("unchecked")
      public Builder additionalSection(Section additionalSection) {
         this.additionalSections.add(checkNotNull(additionalSection, "additionalSection"));
         return this;
      }

      /**
       * @see VirtualSystem#getAdditionalSections
       */
      @SuppressWarnings("unchecked")
      public Builder additionalSections(Iterable<? extends Section> additionalSections) {
         this.additionalSections = ImmutableSet
                  .<Section> copyOf(checkNotNull(additionalSections, "additionalSections"));
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public VirtualSystem build() {
         return new VirtualSystem(id, info, name, operatingSystem, hardwareSections, additionalSections);
      }

      public Builder fromVirtualSystem(VirtualSystem in) {
         return fromSection(in).id(in.getId()).name(in.getName())
                  .operatingSystemSection(in.getOperatingSystemSection()).hardwareSections(
                           in.getVirtualHardwareSections()).additionalSections(in.getAdditionalSections());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromSection(Section<VirtualSystem> in) {
         return (Builder) super.fromSection(in);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder info(String info) {
         return (Builder) super.info(info);
      }

   }

   private final String id;
   private final String name;
   private final OperatingSystemSection operatingSystem;
   private final Set<VirtualHardwareSection> hardwareSections;
   @SuppressWarnings("unchecked")
   private final Set<? extends Section> additionalSections;

   @SuppressWarnings("unchecked")
   public VirtualSystem(String id, String info, String name, OperatingSystemSection operatingSystem,
            Iterable<? extends VirtualHardwareSection> hardwareSections, Iterable<? extends Section> additionalSections) {
      super(info);
      this.id = id;
      this.name = name;
      this.operatingSystem = checkNotNull(operatingSystem, "operatingSystem");
      this.hardwareSections = ImmutableSet.copyOf(checkNotNull(hardwareSections, "hardwareSections"));
      this.additionalSections = ImmutableSet.copyOf(checkNotNull(additionalSections, "additionalSections"));
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
    * which describes the virtual hardwareSections required by the virtual system.
    * */
   public Set<? extends VirtualHardwareSection> getVirtualHardwareSections() {
      return hardwareSections;
   }

   @SuppressWarnings("unchecked")
   public Set<? extends Section> getAdditionalSections() {
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
      VirtualSystem other = (VirtualSystem) obj;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return String.format("[id=%s, name=%s, info=%s, operatingSystem=%s, hardwareSections=%s, additionalSections=%s]",
               id, name, info, operatingSystem, hardwareSections, additionalSections);
   }
}