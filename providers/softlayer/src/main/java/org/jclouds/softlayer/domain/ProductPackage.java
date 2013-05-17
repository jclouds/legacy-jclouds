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
 * The SoftLayer_Product_Package data type contains information about packages
 * from which orders can be generated. Packages contain general information
 * regarding what is in them, where they are currently sold, availability, and
 * pricing.
 *
 * @author Adrian Cole
 * @see <a href=
"http://sldn.softlayer.com/reference/datatypes/SoftLayer_Product_Package"
/>
 */
public class ProductPackage {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromProductPackage(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected int id;
      protected String name;
      protected String description;
      protected Set<ProductItem> items = ImmutableSet.of();
      protected Set<Datacenter> locations = ImmutableSet.of();

      /**
       * @see ProductPackage#getId()
       */
      public T id(int id) {
         this.id = id;
         return self();
      }

      /**
       * @see ProductPackage#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see ProductPackage#getDescription()
       */
      public T description(String description) {
         this.description = description;
         return self();
      }

      /**
       * @see ProductPackage#getItems()
       */
      public T items(Set<ProductItem> items) {
         this.items = ImmutableSet.copyOf(checkNotNull(items, "items"));
         return self();
      }

      public T items(ProductItem... in) {
         return items(ImmutableSet.copyOf(in));
      }

      /**
       * @see ProductPackage#getDatacenters()
       */
      public T datacenters(Set<Datacenter> locations) {
         this.locations = ImmutableSet.copyOf(checkNotNull(locations, "locations"));
         return self();
      }

      public T datacenters(Datacenter... in) {
         return datacenters(ImmutableSet.copyOf(in));
      }

      public ProductPackage build() {
         return new ProductPackage(id, name, description, items, locations);
      }

      public T fromProductPackage(ProductPackage in) {
         return this
               .id(in.getId())
               .name(in.getName())
               .description(in.getDescription())
               .items(in.getItems())
               .datacenters(in.getDatacenters());
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
   private final String description;
   private final Set<ProductItem> items;
   private final Set<Datacenter> locations;

   @ConstructorProperties({
         "id", "name", "description", "items", "locations"
   })
   protected ProductPackage(int id, @Nullable String name, @Nullable String description, @Nullable Set<ProductItem> items, Set<Datacenter> locations) {
      this.id = id;
      this.name = name;
      this.description = description;
      this.items = items == null ? ImmutableSet.<ProductItem>of() : ImmutableSet.copyOf(items);
      this.locations = locations == null ? ImmutableSet.<Datacenter>of() : ImmutableSet.copyOf(locations);
   }

   /**
    * @return A package's internal identifier. Everything regarding a
   SoftLayer_Product_Package is tied back to this id.
    */
   public int getId() {
      return this.id;
   }

   /**
    * @return The description of the package. For server packages, this is
   usually a detailed description of processor type and count.
    */
   @Nullable
   public String getName() {
      return this.name;
   }

   /**
    * @return A generic description of the processor type and count. This
   includes HTML, so you may want to strip these tags if you plan to
   use it.
    */
   @Nullable
   public String getDescription() {
      return this.description;
   }

   /**
    * @return A collection of valid items available for purchase in this
   package.
    */
   public Set<ProductItem> getItems() {
      return this.items;
   }

   public Set<Datacenter> getDatacenters() {
      return this.locations;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ProductPackage that = ProductPackage.class.cast(obj);
      return Objects.equal(this.id, that.id);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("name", name).add("description", description).add("items", items).add("locations", locations);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
