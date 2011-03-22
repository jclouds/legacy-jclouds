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
public class Envelope {

   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   public Builder toBuilder() {
      return new Builder().fromEnvelope(this);
   }

   public static class Builder {
      protected Set<DiskSection> diskSections = Sets.newLinkedHashSet();
      protected Set<NetworkSection> networkSections = Sets.newLinkedHashSet();
      @SuppressWarnings("unchecked")
      protected Set<Section> additionalSections = Sets.newLinkedHashSet();
      protected VirtualSystem virtualSystem;

      /**
       * @see Envelope#getDiskSections
       */
      public Builder diskSection(DiskSection diskSection) {
         this.diskSections.add(checkNotNull(diskSection, "diskSection"));
         return this;
      }

      /**
       * @see Envelope#getDiskSections
       */
      public Builder diskSections(Iterable<? extends DiskSection> diskSections) {
         this.diskSections = ImmutableSet.<DiskSection> copyOf(checkNotNull(diskSections, "diskSections"));
         return this;
      }

      /**
       * @see Envelope#getNetworkSections
       */
      public Builder networkSection(NetworkSection networkSection) {
         this.networkSections.add(checkNotNull(networkSection, "networkSection"));
         return this;
      }

      /**
       * @see Envelope#getNetworkSections
       */
      public Builder networkSections(Iterable<? extends NetworkSection> networkSections) {
         this.networkSections = ImmutableSet.<NetworkSection> copyOf(checkNotNull(networkSections, "networkSections"));
         return this;
      }

      /**
       * @see Envelope#getAdditionalSections
       */
      @SuppressWarnings("unchecked")
      public Builder additionalSection(Section additionalSection) {
         this.additionalSections.add(checkNotNull(additionalSection, "additionalSection"));
         return this;
      }

      /**
       * @see Envelope#getAdditionalSections
       */
      @SuppressWarnings("unchecked")
      public Builder additionalSections(Iterable<? extends Section> additionalSections) {
         this.additionalSections = ImmutableSet
                  .<Section> copyOf(checkNotNull(additionalSections, "additionalSections"));
         return this;
      }

      /**
       * @see Envelope#getVirtualSystem
       */
      public Builder virtualSystem(VirtualSystem virtualSystem) {
         this.virtualSystem = virtualSystem;
         return this;
      }

      /**
       * {@inheritDoc}
       */
      public Envelope build() {
         return new Envelope(diskSections, networkSections, additionalSections, virtualSystem);
      }

      public Builder fromEnvelope(Envelope in) {
         return virtualSystem(in.getVirtualSystem()).diskSections(in.getDiskSections()).networkSections(
                  networkSections).additionalSections(in.getAdditionalSections());
      }

   }

   private final Set<DiskSection> diskSections;
   private final Set<NetworkSection> networkSections;
   @SuppressWarnings("unchecked")
   private final Set<? extends Section> additionalSections;
   private final VirtualSystem virtualSystem;

   @SuppressWarnings("unchecked")
   public Envelope(Iterable<? extends DiskSection> diskSections, Iterable<? extends NetworkSection> networkSections,
            Iterable<? extends Section> additionalSections, VirtualSystem virtualSystem) {
      this.diskSections = ImmutableSet.copyOf(checkNotNull(diskSections, "diskSections"));
      this.networkSections = ImmutableSet.copyOf(checkNotNull(networkSections, "networkSections"));
      this.additionalSections = ImmutableSet.copyOf(checkNotNull(additionalSections, "additionalSections"));
      this.virtualSystem = checkNotNull(virtualSystem, "virtualSystem");
   }

   public VirtualSystem getVirtualSystem() {
      return virtualSystem;
   }

   public Set<? extends DiskSection> getDiskSections() {
      return diskSections;
   }

   @SuppressWarnings("unchecked")
   public Set<? extends Section> getAdditionalSections() {
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
      Envelope other = (Envelope) obj;
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