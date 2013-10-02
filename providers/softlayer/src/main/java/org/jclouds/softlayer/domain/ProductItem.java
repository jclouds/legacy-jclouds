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
 * The SoftLayer_Product_Item data type contains general information relating to
 * a single SoftLayer product.
 *
 * @author Adrian Cole
 * @see <a href=
"http://sldn.softlayer.com/reference/datatypes/SoftLayer_Product_Item"
/>
 */
public class ProductItem {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromProductItem(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected int id;
      protected String description;
      protected String units;
      protected Float capacity;
      protected Set<ProductItemPrice> prices = ImmutableSet.of();
      protected Set<ProductItemCategory> categories = ImmutableSet.of();

      /**
       * @see ProductItem#getId()
       */
      public T id(int id) {
         this.id = id;
         return self();
      }

      /**
       * @see ProductItem#getDescription()
       */
      public T description(String description) {
         this.description = description;
         return self();
      }

      /**
       * @see ProductItem#getUnits()
       */
      public T units(String units) {
         this.units = units;
         return self();
      }

      /**
       * @see ProductItem#getCapacity()
       */
      public T capacity(Float capacity) {
         this.capacity = capacity;
         return self();
      }

      /**
       * @see ProductItem#getPrices()
       */
      public T prices(Set<ProductItemPrice> prices) {
         this.prices = ImmutableSet.copyOf(checkNotNull(prices, "prices"));
         return self();
      }

      public T prices(ProductItemPrice... in) {
         return prices(ImmutableSet.copyOf(in));
      }
      
      /**
       * @see ProductItem#getCategories()
       */
      public T categories(Set<ProductItemCategory> categories) {
         this.categories = ImmutableSet.copyOf(checkNotNull(categories, "categories"));
         return self();
      }

      public T categories(ProductItemCategory... in) {
         return categories(ImmutableSet.copyOf(in));
      }

      public ProductItem build() {
         return new ProductItem(id, description, units, capacity, prices, categories);
      }

      public T fromProductItem(ProductItem in) {
         return this
               .id(in.getId())
               .description(in.getDescription())
               .units(in.getUnits())
               .capacity(in.getCapacity())
               .prices(in.getPrices())
               .categories(in.getCategories());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final int id;
   private final String description;
   private final String units;
   private final Float capacity;
   private final Set<ProductItemPrice> prices;
   private final Set<ProductItemCategory> categories;

   @ConstructorProperties({
         "id", "description", "units", "capacity", "prices", "categories"
   })
   protected ProductItem(int id, @Nullable String description, @Nullable String units, @Nullable Float capacity, @Nullable Set<ProductItemPrice> prices, @Nullable Set<ProductItemCategory> categories) {
      this.id = id;
      this.description = description;
      this.units = units;
      this.capacity = capacity;
      this.prices = prices == null ? ImmutableSet.<ProductItemPrice>of() : ImmutableSet.copyOf(prices);
      this.categories = categories == null ? ImmutableSet.<ProductItemCategory>of() : ImmutableSet.copyOf(categories);
   }

   /**
    * @return The unique identifier of a specific location.
    */
   public int getId() {
      return this.id;
   }

   /**
    * @return A product's description
    */
   @Nullable
   public String getDescription() {
      return this.description;
   }

   /**
    * @return The unit of measurement that a product item is measured in.
    */
   @Nullable
   public String getUnits() {
      return this.units;
   }

   /**
    * @return Some Product Items have capacity information such as RAM and
   bandwidth, and others. This provides the numerical representation
   of the capacity given in the description of this product item.
    */
   @Nullable
   public Float getCapacity() {
      return this.capacity;
   }

   /**
    * @return A product item's prices.
    */
   public Set<ProductItemPrice> getPrices() {
      return this.prices;
   }

   /**
    * @return An item's associated item categories.
    */
   public Set<ProductItemCategory> getCategories() {
      return this.categories;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ProductItem that = ProductItem.class.cast(obj);
      return Objects.equal(this.id, that.id);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("description", description).add("units", units).add("capacity", capacity).add("prices", prices).add("categories", categories);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
