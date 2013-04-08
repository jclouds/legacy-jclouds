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
package org.jclouds.ultradns.ws.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.ImmutableSet;

/**
 * A region is a set of territory names.
 * 
 * @author Adrian Cole
 */
public class DirectionalGroupNameAndRegions extends ForwardingSet<Region> {

   private final String name;
   private final Set<Region> regions;

   private DirectionalGroupNameAndRegions(String name, Set<Region> regions) {
      this.name = checkNotNull(name, "name");
      this.regions = checkNotNull(regions, "regions of %s", name);
   }

   public String getName() {
      return name;
   }

   public Set<Region> getRegions() {
      return regions;
   }

   @Override
   protected Set<Region> delegate() {
      return regions;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, regions);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      DirectionalGroupNameAndRegions that = DirectionalGroupNameAndRegions.class.cast(obj);
      return equal(this.name, that.name) && equal(this.regions, that.regions);
   }

   @Override
   public String toString() {
      return toStringHelper("").add("name", name).add("regions", regions).toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }

   public final static class Builder {
      private String name;
      private ImmutableSet.Builder<Region> regions = ImmutableSet.<Region> builder();

      /**
       * @see DirectionalGroupNameAndRegions#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * adds to current regions
       * 
       * @see DirectionalGroupNameAndRegions#getRegions()
       */
      public Builder addRegion(Region region) {
         this.regions.add(region);
         return this;
      }

      /**
       * replaces current regions
       * 
       * @see DirectionalGroupNameAndRegions#getRegions()
       */
      public Builder regions(Iterable<Region> regions) {
         this.regions = ImmutableSet.<Region> builder().addAll(regions);
         return this;
      }

      public DirectionalGroupNameAndRegions build() {
         return new DirectionalGroupNameAndRegions(name, regions.build());
      }

      public Builder from(DirectionalGroupNameAndRegions in) {
         return name(in.getName()).regions(in.getRegions());
      }
   }
}
