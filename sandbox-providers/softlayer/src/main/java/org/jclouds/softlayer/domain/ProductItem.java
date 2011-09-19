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
 * The SoftLayer_Product_Item data type contains general information relating to
 * a single SoftLayer product.
 * 
 * @author Adrian Cole
 * @see <a href=
 *      "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Product_Item"
 *      />
 */
public class ProductItem implements Comparable<ProductItem> {

   // TODO there are more elements than this.

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private long id = -1;
      private String description;
      private String units;
      private Float capacity;
      private Set<ProductItemPrice> prices = Sets.newLinkedHashSet();
      private Set<ProductItemCategory> categories = Sets.newLinkedHashSet();

      public Builder id(long id) {
         this.id = id;
         return this;
      }

      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public Builder units(String units) {
         this.units = units;
         return this;
      }

      public Builder capacity(Float capacity) {
         this.capacity = capacity;
         return this;
      }

      public Builder price(ProductItemPrice prices) {
         this.prices.add(checkNotNull(prices, "prices"));
         return this;
      }

      public Builder prices(Iterable<ProductItemPrice> prices) {
         this.prices = ImmutableSet.<ProductItemPrice> copyOf(checkNotNull(prices, "prices"));
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

      public ProductItem build() {
         return new ProductItem(id, description, units, capacity, prices, categories);
      }

      public static Builder fromProductItem(ProductItem in) {
         return ProductItem.builder().id(in.getId())
                 .description(in.getDescription())
                 .units(in.getUnits())
                 .capacity(in.getCapacity())
                 .prices(in.getPrices())
                 .categories(in.getCategories());
      }
   }

   private long id = -1;
   private String description;
   private String units;
   private Float capacity;
   private Set<ProductItemPrice> prices = Sets.newLinkedHashSet();
   private Set<ProductItemCategory> categories = Sets.newLinkedHashSet();

   // for deserializer
   ProductItem() {

   }

   public ProductItem(long id, String description, String units, Float capacity,
                      Iterable<ProductItemPrice> prices, Iterable<ProductItemCategory> categories) {
      this.id = id;
      this.description = description;
      this.units = units;
      this.capacity = capacity;
      this.prices = ImmutableSet.<ProductItemPrice> copyOf(checkNotNull(prices, "prices"));
      this.categories = ImmutableSet.<ProductItemCategory> copyOf(checkNotNull(categories, "categories"));
   }

   @Override
   public int compareTo(ProductItem arg0) {
      return new Long(id).compareTo(arg0.getId());
   }

   /**
    * @return The unique identifier of a specific location.
    */
   public long getId() {
      return id;
   }

   /**
    * @return A product's description
    */
   public String getDescription() {
      return description;
   }

   /**
    * @return The unit of measurement that a product item is measured in.
    */
   @Nullable
   public String getUnits() {
      return units;
   }

   /**
    * @return Some Product Items have capacity information such as RAM and
    *         bandwidth, and others. This provides the numerical representation
    *         of the capacity given in the description of this product item.
    */
   @Nullable
   public Float getCapacity() {
      return capacity;
   }

   /**
    * 
    * @return A product item's prices.
    */
   public Set<ProductItemPrice> getPrices() {
      return prices;
   }

   /**
    *
    * @return An item's associated item categories.
    */
   public Set<ProductItemCategory> getCategories() {
      return categories;
   }

   public Builder toBuilder() {
      return Builder.fromProductItem(this);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (id ^ (id >>> 32));
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
      ProductItem other = (ProductItem) obj;
      if (id != other.id)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "ProductItem [id=" + id + ", description=" + description + ", units=" + units + ", capacity=" + capacity
            + ", prices=" + prices + ", categories=" + categories + "]";
   }
}
