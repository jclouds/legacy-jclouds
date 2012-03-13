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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.jclouds.vcloud.director.v1_5.domain.NetworkSection;
import org.jclouds.vcloud.director.v1_5.domain.ovf.DiskSection;
import org.jclouds.vcloud.director.v1_5.domain.ovf.SectionType;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public abstract class BaseEnvelope<V extends BaseVirtualSystem<V>, E extends BaseEnvelope<V, E>> {

   public abstract Builder<V, E> toBuilder();

   public static abstract class Builder<V extends BaseVirtualSystem<V>, E extends BaseEnvelope<V, E>> {
      protected Set<DiskSection> diskSections = Sets.newLinkedHashSet();
      protected Set<NetworkSection> networkSections = Sets.newLinkedHashSet();
      protected Multimap<String, SectionType<?>> additionalSections = LinkedHashMultimap.create();
      protected V virtualSystem;

      /**
       * @see BaseEnvelope#getDiskSections
       */
      public Builder<V, E> diskSection(DiskSection diskSection) {
         this.diskSections.add(checkNotNull(diskSection, "diskSection"));
         return this;
      }

      /**
       * @see BaseEnvelope#getDiskSections
       */
      public Builder<V, E> diskSections(Iterable<? extends DiskSection> diskSections) {
         this.diskSections = ImmutableSet.<DiskSection> copyOf(checkNotNull(diskSections, "diskSections"));
         return this;
      }

      /**
       * @see BaseEnvelope#getNetworkSections
       */
      public Builder<V, E> networkSection(NetworkSection networkSection) {
         this.networkSections.add(checkNotNull(networkSection, "networkSection"));
         return this;
      }

      /**
       * @see BaseEnvelope#getNetworkSections
       */
      public Builder<V, E> networkSections(Iterable<? extends NetworkSection> networkSections) {
         this.networkSections = ImmutableSet.<NetworkSection> copyOf(checkNotNull(networkSections, "networkSections"));
         return this;
      }

      /**
       * @see BaseEnvelope#getAdditionalSections
       */
      public Builder<V, E> additionalSection(String name, SectionType<?> additionalSection) {
         this.additionalSections.put(checkNotNull(name, "name"), checkNotNull(additionalSection, "additionalSection"));
         return this;
      }

      /**
       * @see BaseEnvelope#getAdditionalSections
       */
      public Builder<V, E> additionalSections(Multimap<String, SectionType<?>> additionalSections) {
         this.additionalSections = ImmutableMultimap.<String, SectionType<?>> copyOf(checkNotNull(additionalSections,
                  "additionalSections"));
         return this;
      }

      /**
       * @see BaseEnvelope#getVirtualSystem
       */
      public Builder<V, E> virtualSystem(V virtualSystem) {
         this.virtualSystem = virtualSystem;
         return this;
      }

      public abstract E build() ;

      public Builder<V, E> fromEnvelope(BaseEnvelope<V, E> in) {
         return virtualSystem(in.getVirtualSystem()).diskSections(in.getDiskSections())
                  .networkSections(networkSections).additionalSections(in.getAdditionalSections());
      }

   }

   private Set<DiskSection> diskSections;
   private Set<NetworkSection> networkSections;
   private Multimap<String, SectionType<?>> additionalSections;
   private V virtualSystem;

   protected BaseEnvelope(Iterable<? extends DiskSection> diskSections, Iterable<? extends NetworkSection> networkSections,
            Multimap<String, SectionType<?>> additionalSections, V virtualSystem) {
      this.diskSections = ImmutableSet.copyOf(checkNotNull(diskSections, "diskSections"));
      this.networkSections = ImmutableSet.copyOf(checkNotNull(networkSections, "networkSections"));
      this.additionalSections = ImmutableMultimap.copyOf(checkNotNull(additionalSections, "additionalSections"));
      this.virtualSystem = checkNotNull(virtualSystem, "virtualSystem");
   }
   
   protected BaseEnvelope() {
      // for JAXB
   }

   public V getVirtualSystem() {
      return virtualSystem;
   }

   public Set<? extends DiskSection> getDiskSections() {
      return diskSections;
   }

   public Multimap<String, SectionType<?>> getAdditionalSections() {
      return additionalSections;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(additionalSections, diskSections, networkSections, virtualSystem);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;

      BaseEnvelope<?, ?> other = (BaseEnvelope<?, ?>) obj;
      return Objects.equal(additionalSections, other.additionalSections)
            && Objects.equal(diskSections, other.diskSections)
            && Objects.equal(networkSections, other.networkSections)
            && Objects.equal(virtualSystem, other.virtualSystem);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper("").add("diskSections", diskSections).add("networkSections", networkSections)
            .add("additionalSections", additionalSections).add("virtualSystem", virtualSystem);
   }
   
   public Set<NetworkSection> getNetworkSections() {
      return networkSections;
   }
}