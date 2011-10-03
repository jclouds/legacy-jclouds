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

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;

/**
 * 
 * @author Jason King
 * @see <a href= "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Container_Product_Order_Virtual_Guest"
 *      />
 */
public class ProductOrder {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private int packageId = -1;
      private Set<ProductItemPrice> prices = Sets.newLinkedHashSet();
      private Set<VirtualGuest> virtualGuests = Sets.newLinkedHashSet();
      private String location;
      private int quantity;
      private boolean useHourlyPricing;

      public Builder packageId(int packageId) {
         this.packageId = packageId;
         return this;
      }

      /**
       * Adds a price to the order
       * All that is required to send in is the price ID of each item desired to be ordered.
       * @param prices
       *                The Prices of the item desired to be ordered
       */
      public Builder price(ProductItemPrice prices) {
         this.prices.add(checkNotNull(prices, "prices"));
         return this;
      }

      /**
       * Adds multiple prices to the order, overwriting any existing ones
       * All that is required to send in is the price ID of each item desired to be ordered.
       * @param prices
       *                The Prices of the items desired to be ordered
       */
      public Builder prices(Iterable<ProductItemPrice> prices) {
         this.prices = ImmutableSet.<ProductItemPrice> copyOf(checkNotNull(prices, "prices"));
         return this;
      }

       /**
       * Adds a virtualGuest to the order
       * @param virtualGuest
       *                The virtualGuest to add. Needs domain and hostname.
       */
      public Builder virtualGuest(VirtualGuest virtualGuest) {
         this.virtualGuests.add(checkNotNull(virtualGuest, "virtualGuest"));
         return this;
      }

      public Builder virtualGuests(Iterable<VirtualGuest> virtualGuests) {
         this.virtualGuests = ImmutableSet.<VirtualGuest> copyOf(checkNotNull(virtualGuests, "virtualGuests"));
         return this;
      }

      public Builder location(String location) {
         this.location = location;
         return this;
      }

      public Builder quantity(int quantity) {
         this.quantity = quantity;
         return this;
      }

      public Builder useHourlyPricing(Boolean useHourlyPricing) {
         this.useHourlyPricing = useHourlyPricing;
         return this;
      }

      public ProductOrder build() {
         return new ProductOrder(packageId, location,prices, virtualGuests, quantity, useHourlyPricing);
      }

      public static Builder fromProductOrder(ProductOrder in) {
         return ProductOrder.builder().packageId(in.getPackageId())
                                 .location(in.getLocation())
                                 .prices(in.getPrices())
                                 .virtualGuests(in.getVirtualGuests())
                                 .quantity(in.getQuantity())
                                 .useHourlyPricing(in.getUseHourlyPricing());
      }
   }

   private int packageId = -1;
   private String location;
   private Set<ProductItemPrice> prices = Sets.newLinkedHashSet();
   private Set<VirtualGuest> virtualGuests = Sets.newLinkedHashSet();
   private int quantity;
   private boolean useHourlyPricing;

   // for deserializer
   ProductOrder() {

   }

   public ProductOrder(int packageId, String location, Iterable<ProductItemPrice> prices, Iterable<VirtualGuest> virtualGuest, int quantity, boolean useHourlyPricing) {
      this.packageId = packageId;
      this.location = location;
      this.prices = ImmutableSet.<ProductItemPrice> copyOf(checkNotNull(prices, "prices"));
      this.virtualGuests = ImmutableSet.<VirtualGuest> copyOf(checkNotNull(virtualGuest, "virtualGuest"));
      this.quantity = quantity;
      this.useHourlyPricing = useHourlyPricing;
   }

   /**
    * @return The package id of an order. This is required.
    */
   public int getPackageId() {
      return packageId;
   }

   /**
    * @return The region keyname or specific location keyname where the order should be provisioned.
    */
   public String getLocation() {
      return location;
   }

   /**
    * Gets the item prices in this order.
    * All that is required to be present is the price ID
    * @return the prices.
    */
   public Set<ProductItemPrice> getPrices() {
      return prices;
   }

   /**
    * Gets the virtual guests in this order.
    * @return the the virtual guests.
    */
   public Set<VirtualGuest> getVirtualGuests() {
      return virtualGuests;
   }

   public int getQuantity() {
      return quantity;
   }

   public boolean getUseHourlyPricing() {
      return useHourlyPricing;
   }

   public Builder toBuilder() {
      return Builder.fromProductOrder(this);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ProductOrder that = (ProductOrder) o;

      if (packageId != that.packageId) return false;
      if (quantity != that.quantity) return false;
      if (useHourlyPricing != that.useHourlyPricing) return false;
      if (location != null ? !location.equals(that.location) : that.location != null)
         return false;
      if (prices != null ? !prices.equals(that.prices) : that.prices != null)
         return false;
      if (virtualGuests != null ? !virtualGuests.equals(that.virtualGuests) : that.virtualGuests != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = (packageId ^ (packageId >>> 32));
      result = 31 * result + (location != null ? location.hashCode() : 0);
      result = 31 * result + (prices != null ? prices.hashCode() : 0);
      result = 31 * result + (virtualGuests != null ? virtualGuests.hashCode() : 0);
      result = 31 * result + (quantity ^ (quantity >>> 32));
      result = 31 * result + (useHourlyPricing ? 1 : 0);
      return result;
   }


   public String toString() {
      return "[packageId=" + packageId + ", location=" + location + ", prices=" + prices
           + ", virtualGuests=" + virtualGuests +", quantity=" + quantity + ", useHourlyPricing=" + useHourlyPricing + "]";
   }
}
