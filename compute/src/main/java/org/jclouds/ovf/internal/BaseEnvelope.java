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

import org.jclouds.ovf.DiskSection;
import org.jclouds.ovf.NetworkSection;
import org.jclouds.ovf.Section;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class BaseEnvelope<V extends BaseVirtualSystem<V>, E extends BaseEnvelope<V, E>> {

   public static <V extends BaseVirtualSystem<V>, E extends BaseEnvelope<V, E>> Builder<V, E> builder() {
      return new Builder<V, E>();
   }

   /**
    * {@inheritDoc}
    */
   public Builder<V, E> toBuilder() {
      return new Builder<V, E>().fromEnvelope(this);
   }

   public static class Builder<V extends BaseVirtualSystem<V>, E extends BaseEnvelope<V, E>> {
      protected Set<DiskSection> diskSections = Sets.newLinkedHashSet();
      protected Set<NetworkSection> networkSections = Sets.newLinkedHashSet();
      @SuppressWarnings("unchecked")
      protected Multimap<String, Section> additionalSections = LinkedHashMultimap.create();
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
      @SuppressWarnings("unchecked")
      public Builder<V, E> additionalSection(String name, Section additionalSection) {
         this.additionalSections.put(checkNotNull(name, "name"), checkNotNull(additionalSection, "additionalSection"));
         return this;
      }

      /**
       * @see BaseEnvelope#getAdditionalSections
       */
      @SuppressWarnings("unchecked")
      public Builder<V, E> additionalSections(Multimap<String, Section> additionalSections) {
         this.additionalSections = ImmutableMultimap.<String, Section> copyOf(checkNotNull(additionalSections,
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

      /**
       * {@inheritDoc}
       */
      @SuppressWarnings("unchecked")
      public E build() {
         return (E) new BaseEnvelope<V, E>(diskSections, networkSections, additionalSections, virtualSystem);
      }

      public Builder<V, E> fromEnvelope(BaseEnvelope<V, E> in) {
         return virtualSystem(in.getVirtualSystem()).diskSections(in.getDiskSections())
                  .networkSections(networkSections).additionalSections(in.getAdditionalSections());
      }

   }

   private final Set<DiskSection> diskSections;
   private final Set<NetworkSection> networkSections;
   @SuppressWarnings("unchecked")
   private final Multimap<String, Section> additionalSections;
   private final V virtualSystem;

   @SuppressWarnings("unchecked")
   public BaseEnvelope(Iterable<? extends DiskSection> diskSections, Iterable<? extends NetworkSection> networkSections,
            Multimap<String, Section> additionalSections, V virtualSystem) {
      this.diskSections = ImmutableSet.copyOf(checkNotNull(diskSections, "diskSections"));
      this.networkSections = ImmutableSet.copyOf(checkNotNull(networkSections, "networkSections"));
      this.additionalSections = ImmutableMultimap.copyOf(checkNotNull(additionalSections, "additionalSections"));
      this.virtualSystem = checkNotNull(virtualSystem, "virtualSystem");
   }

   public V getVirtualSystem() {
      return virtualSystem;
   }

   public Set<? extends DiskSection> getDiskSections() {
      return diskSections;
   }

   @SuppressWarnings("unchecked")
   public Multimap<String, Section> getAdditionalSections() {
      return additionalSections;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((additionalSections == null) ? 0 : additionalSections.hashCode());
      result = prime * result + ((diskSections == null) ? 0 : diskSections.hashCode());
      result = prime * result + ((networkSections == null) ? 0 : networkSections.hashCode());
      result = prime * result + ((virtualSystem == null) ? 0 : virtualSystem.hashCode());
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
      BaseEnvelope<?, ?> other = (BaseEnvelope<?, ?>) obj;
      if (additionalSections == null) {
         if (other.additionalSections != null)
            return false;
      } else if (!additionalSections.equals(other.additionalSections))
         return false;
      if (diskSections == null) {
         if (other.diskSections != null)
            return false;
      } else if (!diskSections.equals(other.diskSections))
         return false;
      if (networkSections == null) {
         if (other.networkSections != null)
            return false;
      } else if (!networkSections.equals(other.networkSections))
         return false;
      if (virtualSystem == null) {
         if (other.virtualSystem != null)
            return false;
      } else if (!virtualSystem.equals(other.virtualSystem))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return String.format("[diskSections=%s, networkSections=%s, additionalSections=%s, virtualSystem=%s]",
               diskSections, networkSections, additionalSections, virtualSystem);
   }

   public Set<NetworkSection> getNetworkSections() {
      return networkSections;
   }
}
