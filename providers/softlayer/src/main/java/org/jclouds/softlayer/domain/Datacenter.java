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
package org.jclouds.softlayer.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;

/**
 * Class Datacenter
 *
 * @author Adrian Cole
 * @see <a href= "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Location_Datacenter"
/>
 */
public class Datacenter {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromDatacenter(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected int id;
      protected String name;
      protected String longName;
      protected Address locationAddress;
      protected Set<Region> regions = ImmutableSet.of();

      /**
       * @see Datacenter#getId()
       */
      public T id(int id) {
         this.id = id;
         return self();
      }

      /**
       * @see Datacenter#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see Datacenter#getLongName()
       */
      public T longName(String longName) {
         this.longName = longName;
         return self();
      }

      /**
       * @see Datacenter#getLocationAddress()
       */
      public T locationAddress(Address locationAddress) {
         this.locationAddress = locationAddress;
         return self();
      }

      /**
       * @see Datacenter#getRegions()
       */
      public T regions(Set<Region> regions) {
         this.regions = ImmutableSet.copyOf(checkNotNull(regions, "regions"));
         return self();
      }

      public T regions(Region... in) {
         return regions(ImmutableSet.copyOf(in));
      }

      public Datacenter build() {
         return new Datacenter(id, name, longName, locationAddress, regions);
      }

      public T fromDatacenter(Datacenter in) {
         return this
               .id(in.getId())
               .name(in.getName())
               .longName(in.getLongName())
               .locationAddress(in.getLocationAddress())
               .regions(in.getRegions());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final int id;
   private final String name;
   private final String longName;
   private final Address locationAddress;
   private final Set<Region> regions;

   @ConstructorProperties({
         "id", "name", "longName", "locationAddress", "regions"
   })
   protected Datacenter(int id, @Nullable String name, @Nullable String longName, @Nullable Address locationAddress, @Nullable Set<Region> regions) {
      this.id = id;
      this.name = name;
      this.longName = longName;
      this.locationAddress = locationAddress;
      this.regions = regions == null ? ImmutableSet.<Region>of() : ImmutableSet.copyOf(regions);
   }

   /**
    * @return The unique identifier of a specific location.
    */
   public int getId() {
      return this.id;
   }

   /**
    * @return A short location description.
    */
   @Nullable
   public String getName() {
      return this.name;
   }

   /**
    * @return A longer location description.
    */
   @Nullable
   public String getLongName() {
      return this.longName;
   }

   /**
    * @return A location's physical address (optional).
    */
   @Nullable
   public Address getLocationAddress() {
      return this.locationAddress;
   }

   /**
    * A location can be a member of 1 or more regions.
    * Sometimes the list of regions is empty, for example as a new Datacenter is being added.
    * The list of regions usually contains one with keyName=FIRST_AVAILABLE which should be ignored.
    *
    * @return The regions to which a location belongs.
    */
   public Set<Region> getRegions() {
      return this.regions;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Datacenter that = Datacenter.class.cast(obj);
      return Objects.equal(this.id, that.id);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("name", name).add("longName", longName).add("locationAddress", locationAddress).add("regions", regions);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
