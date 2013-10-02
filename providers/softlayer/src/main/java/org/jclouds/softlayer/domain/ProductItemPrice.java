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
 * The SoftLayer_Product_Item_Price data type contains general information
 * relating to a single SoftLayer product item prices. You can find out what
 * packages each prices is in as well as which category under which this prices is
 * sold. All prices are returned in Floating point values measured in US Dollars
 * ($USD).
 *
 * @author Adrian Cole
 * @see <a href=
"http://sldn.softlayer.com/reference/datatypes/SoftLayer_Product_Item_Price"
/>
 */
public class ProductItemPrice {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromProductItemPrice(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected int id;
      protected long itemId;
      protected Float recurringFee;
      protected Float hourlyRecurringFee;
      protected ProductItem item;
      protected Set<ProductItemCategory> categories = ImmutableSet.of();

      /**
       * @see ProductItemPrice#getId()
       */
      public T id(int id) {
         this.id = id;
         return self();
      }

      /**
       * @see ProductItemPrice#getItemId()
       */
      public T itemId(long itemId) {
         this.itemId = itemId;
         return self();
      }

      /**
       * @see ProductItemPrice#getRecurringFee()
       */
      public T recurringFee(Float recurringFee) {
         this.recurringFee = recurringFee;
         return self();
      }

      /**
       * @see ProductItemPrice#getHourlyRecurringFee()
       */
      public T hourlyRecurringFee(Float hourlyRecurringFee) {
         this.hourlyRecurringFee = hourlyRecurringFee;
         return self();
      }

      /**
       * @see ProductItemPrice#getItem()
       */
      public T item(ProductItem item) {
         this.item = item;
         return self();
      }

      /**
       * @see ProductItemPrice#getCategories()
       */
      public T categories(Set<ProductItemCategory> categories) {
         this.categories = ImmutableSet.copyOf(checkNotNull(categories, "categories"));
         return self();
      }

      public T categories(ProductItemCategory... in) {
         return categories(ImmutableSet.copyOf(in));
      }

      public ProductItemPrice build() {
         return new ProductItemPrice(id, itemId, recurringFee, hourlyRecurringFee, item, categories);
      }

      public T fromProductItemPrice(ProductItemPrice in) {
         return this
               .id(in.getId())
               .itemId(in.getItemId())
               .recurringFee(in.getRecurringFee())
               .hourlyRecurringFee(in.getHourlyRecurringFee())
               .item(in.getItem())
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
   private final long itemId;
   private final Float recurringFee;
   private final Float hourlyRecurringFee;
   private final ProductItem item;
   private final Set<ProductItemCategory> categories;

   @ConstructorProperties({
         "id", "itemId", "recurringFee", "hourlyRecurringFee", "item", "categories"
   })
   protected ProductItemPrice(int id, long itemId, @Nullable Float recurringFee, @Nullable Float hourlyRecurringFee, @Nullable ProductItem item, @Nullable Set<ProductItemCategory> categories) {
      this.id = id;
      this.itemId = itemId;
      this.recurringFee = recurringFee;
      this.hourlyRecurringFee = hourlyRecurringFee;
      this.item = item;
      this.categories = categories == null ? ImmutableSet.<ProductItemCategory>of() : ImmutableSet.copyOf(categories);
   }

   /**
    * @return The unique identifier of a Product Item Price.
    */
   public int getId() {
      return this.id;
   }

   /**
    * @return The unique identifier for a product Item
    */
   public long getItemId() {
      return this.itemId;
   }

   /**
    * @return A recurring fee is a fee that happens every billing period. This
   fee is represented as a Floating point decimal in US dollars
   ($USD).
    */
   @Nullable
   public Float getRecurringFee() {
      return this.recurringFee;
   }

   /**
    * @return The hourly prices for this item, should this item be part of an
   hourly pricing package.
    */
   @Nullable
   public Float getHourlyRecurringFee() {
      return this.hourlyRecurringFee;
   }

   /**
    * @return The product item a prices is tied to.
    */
   @Nullable
   public ProductItem getItem() {
      return this.item;
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
      ProductItemPrice that = ProductItemPrice.class.cast(obj);
      return Objects.equal(this.id, that.id);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("itemId", itemId).add("recurringFee", recurringFee).add("hourlyRecurringFee", hourlyRecurringFee).add("item", item).add("categories", categories);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
