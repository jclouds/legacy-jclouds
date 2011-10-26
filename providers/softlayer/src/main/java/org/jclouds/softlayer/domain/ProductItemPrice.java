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
package org.jclouds.softlayer.domain;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.jclouds.javax.annotation.Nullable;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The SoftLayer_Product_Item_Price data type contains general information
 * relating to a single SoftLayer product item price. You can find out what
 * packages each price is in as well as which category under which this price is
 * sold. All prices are returned in Floating point values measured in US Dollars
 * ($USD).
 * 
 * @author Adrian Cole
 * @see <a href=
 *      "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Product_Item_Price"
 *      />
 */
public class ProductItemPrice implements Comparable<ProductItemPrice> {
   // TODO there are more elements than this.

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private int id = -1;
      private long itemId = -1;
      private Float recurringFee;
      private Float hourlyRecurringFee;
      private ProductItem item;
      private Set<ProductItemCategory> categories = Sets.newLinkedHashSet();

      public Builder id(int id) {
         this.id = id;
         return this;
      }

      public Builder itemId(long itemId) {
         this.itemId = itemId;
         return this;
      }

      public Builder recurringFee(Float recurringFee) {
         this.recurringFee = recurringFee;
         return this;
      }

      public Builder hourlyRecurringFee(Float hourlyRecurringFee) {
         this.hourlyRecurringFee = hourlyRecurringFee;
         return this;
      }

      public Builder item(ProductItem item) {
         this.item = item;
         return this;
      }

      public Builder category(ProductItemCategory categories) {
         this.categories.add(checkNotNull(categories, "categories"));
         return this;
      }

      public Builder categories(Iterable<ProductItemCategory> categories) {
         this.categories = ImmutableSet.<ProductItemCategory> copyOf(checkNotNull(categories, "categories"));
         return this;
      }

      public ProductItemPrice build() {
         return new ProductItemPrice(id, itemId, recurringFee, hourlyRecurringFee, item, categories);
      }

      public static Builder fromPrice(ProductItemPrice in) {
         return ProductItemPrice.builder().id(in.getId()).itemId(in.getItemId())
               .hourlyRecurringFee(in.getHourlyRecurringFee()).recurringFee(in.getRecurringFee());
      }
   }

   private int id = -1;
   private long itemId = -1;
   private Float recurringFee;
   private Float hourlyRecurringFee;
   private ProductItem item;
   private Set<ProductItemCategory> categories = Sets.newLinkedHashSet();
   
   // for deserializer
   ProductItemPrice() {

   }

   public ProductItemPrice(int id, long itemId, Float recurringFee, Float hourlyRecurringFee, ProductItem item, Iterable<ProductItemCategory> categories) {
      this.id = id;
      this.itemId = itemId;
      this.recurringFee = recurringFee;
      this.hourlyRecurringFee = hourlyRecurringFee;
      this.item = item;
      this.categories = ImmutableSet.<ProductItemCategory> copyOf(checkNotNull(categories, "categories"));
   }

   @Override
   public int compareTo(ProductItemPrice arg0) {
      return new Integer(id).compareTo(arg0.getId());
   }

   /**
    * @return The unique identifier of a Product Item Price.
    */
   public int getId() {
      return id;
   }

   /**
    * @return The unique identifier for a product Item
    */
   public long getItemId() {
      return itemId;
   }

   /**
    * @return A recurring fee is a fee that happens every billing period. This
    *         fee is represented as a Floating point decimal in US dollars
    *         ($USD).
    */
   @Nullable
   public Float getRecurringFee() {
      return recurringFee;
   }

   /**
    * @return The hourly price for this item, should this item be part of an
    *         hourly pricing package.
    */
   @Nullable
   public Float getHourlyRecurringFee() {
      return hourlyRecurringFee;
   }

   /**
    *
    * @return An item's associated item categories.
    */
   public Set<ProductItemCategory> getCategories() {
      return categories;
   }

   /**
    * @return The product item a price is tied to.
    */
   public ProductItem getItem() {
      return item;
   }

   public Builder toBuilder() {
      return Builder.fromPrice(this);
   }

   @Override
   public String toString() {
      return "[id=" + id + ", itemId=" + itemId + ", recurringFee=" + recurringFee + ", hourlyRecurringFee="
            + hourlyRecurringFee + ", item="+item+", categories="+categories+"]";
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ProductItemPrice that = (ProductItemPrice) o;

      if (id != that.id) return false;

      return true;
   }

   @Override
   public int hashCode() {
      return (id ^ (id >>> 32));
   }
}
