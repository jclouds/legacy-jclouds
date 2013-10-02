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
package org.jclouds.ultradns.ws.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ForwardingMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

/**
 * A region is a set of territory names.
 * 
 * @author Adrian Cole
 */
public class DirectionalGroup extends ForwardingMultimap<String, String> {

   private final String name;
   private final Optional<String> description;
   private final Multimap<String, String> regionToTerritories;

   private DirectionalGroup(String name, Optional<String> description,
         Multimap<String, String> regionToTerritories) {
      this.name = checkNotNull(name, "name");
      this.description = checkNotNull(description, "description of %s", name);
      this.regionToTerritories = checkNotNull(regionToTerritories, "regionToTerritories of %s", name);
   }

   public String getName() {
      return name;
   }

   public Optional<String> getDescription() {
      return description;
   }

   public Multimap<String, String> getRegionToTerritories() {
      return regionToTerritories;
   }

   @Override
   protected Multimap<String, String> delegate() {
      return regionToTerritories;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, regionToTerritories);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      DirectionalGroup that = DirectionalGroup.class.cast(obj);
      return equal(this.name, that.name) && equal(this.regionToTerritories, that.regionToTerritories);
   }

   @Override
   public String toString() {
      return toStringHelper(this).omitNullValues().add("name", name).add("description", description.orNull())
            .add("regionToTerritories", regionToTerritories).toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }

   public static final class Builder {
      private String name;
      private Optional<String> description = Optional.absent();
      private ImmutableMultimap.Builder<String, String> regionToTerritories = ImmutableMultimap
            .<String, String> builder();

      /**
       * @see DirectionalGroup#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see DirectionalGroup#getDescription()
       */
      public Builder description(String description) {
         this.description = Optional.fromNullable(description);
         return this;
      }

      /**
       * adds to current regionToTerritories
       * 
       * @see DirectionalGroup#getRegionToTerritories()
       */
      public Builder mapRegionToTerritories(String region, Iterable<String> territories) {
         this.regionToTerritories.putAll(region, territories);
         return this;
      }

      /**
       * adds to current regionToTerritories
       * 
       * @see DirectionalGroup#getRegionToTerritories()
       */
      public Builder mapRegionToTerritory(String region, String territory) {
         this.regionToTerritories.put(region, territory);
         return this;
      }

      /**
       * adds to current regionToTerritories
       * 
       * @see DirectionalGroup#getRegionToTerritories()
       */
      public Builder mapRegion(String region) {
         this.regionToTerritories.put(region, "all");
         return this;
      }

      /**
       * replaces current regionToTerritories
       * 
       * @see DirectionalGroup#getRegionToTerritories()
       */
      public Builder regionToTerritories(Multimap<String, String> regionToTerritories) {
         this.regionToTerritories = ImmutableMultimap.<String, String> builder().putAll(regionToTerritories);
         return this;
      }

      public DirectionalGroup build() {
         return new DirectionalGroup(name, description, regionToTerritories.build());
      }

      public Builder from(DirectionalGroup in) {
         return name(in.getName()).regionToTerritories(in.getRegionToTerritories());
      }
   }
}
