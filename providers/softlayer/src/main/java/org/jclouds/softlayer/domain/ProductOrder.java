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
 * Class ProductOrder
 *
 * @author Jason King
 * @see <a href= "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Container_Product_Order_Virtual_Guest"
/>
 */
public class ProductOrder {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromProductOrder(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected int packageId;
      protected String location;
      protected Set<ProductItemPrice> prices = ImmutableSet.of();
      protected Set<VirtualGuest> virtualGuests = ImmutableSet.of();
      protected int quantity;
      protected boolean useHourlyPricing;

      /**
       * @see ProductOrder#getPackageId()
       */
      public T packageId(int packageId) {
         this.packageId = packageId;
         return self();
      }

      /**
       * @see ProductOrder#getLocation()
       */
      public T location(String location) {
         this.location = location;
         return self();
      }

      /**
       * @see ProductOrder#getPrices()
       */
      public T prices(Iterable<ProductItemPrice> prices) {
         this.prices = ImmutableSet.copyOf(checkNotNull(prices, "prices"));
         return self();
      }

      public T prices(ProductItemPrice... in) {
         return prices(ImmutableSet.copyOf(in));
      }

      /**
       * @see ProductOrder#getVirtualGuests()
       */
      public T virtualGuests(Set<VirtualGuest> virtualGuests) {
         this.virtualGuests = ImmutableSet.copyOf(checkNotNull(virtualGuests, "virtualGuests"));
         return self();
      }

      public T virtualGuests(VirtualGuest... in) {
         return virtualGuests(ImmutableSet.copyOf(in));
      }

      /**
       * @see ProductOrder#getQuantity()
       */
      public T quantity(int quantity) {
         this.quantity = quantity;
         return self();
      }

      /**
       * @see ProductOrder#getUseHourlyPricing()
       */
      public T useHourlyPricing(boolean useHourlyPricing) {
         this.useHourlyPricing = useHourlyPricing;
         return self();
      }

      public ProductOrder build() {
         return new ProductOrder(packageId, location, prices, virtualGuests, quantity, useHourlyPricing);
      }

      public T fromProductOrder(ProductOrder in) {
         return this
               .packageId(in.getPackageId())
               .location(in.getLocation())
               .prices(in.getPrices())
               .virtualGuests(in.getVirtualGuests())
               .quantity(in.getQuantity())
               .useHourlyPricing(in.getUseHourlyPricing());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final int packageId;
   private final String location;
   private final Set<ProductItemPrice> prices;
   private final Set<VirtualGuest> virtualGuests;
   private final int quantity;
   private final boolean useHourlyPricing;

   @ConstructorProperties({
         "packageId", "location", "prices", "virtualGuest", "quantity", "useHourlyPricing"
   })
   protected ProductOrder(int packageId, @Nullable String location, @Nullable Set<ProductItemPrice> prices, @Nullable Set<VirtualGuest> virtualGuests, int quantity, boolean useHourlyPricing) {
      this.packageId = packageId;
      this.location = location;
      this.prices = prices == null ? ImmutableSet.<ProductItemPrice>of() : ImmutableSet.copyOf(prices);
      this.virtualGuests = virtualGuests == null ? ImmutableSet.<VirtualGuest>of() : ImmutableSet.copyOf(virtualGuests);
      this.quantity = quantity;
      this.useHourlyPricing = useHourlyPricing;
   }

   /**
    * @return The package id of an order. This is required.
    */
   public int getPackageId() {
      return this.packageId;
   }

   /**
    * @return The region keyname or specific location keyname where the order should be provisioned.
    */
   @Nullable
   public String getLocation() {
      return this.location;
   }

   /**
    * Gets the item prices in this order.
    * All that is required to be present is the prices ID
    *
    * @return the prices.
    */
   public Set<ProductItemPrice> getPrices() {
      return this.prices;
   }

   /**
    * Gets the virtual guests in this order.
    *
    * @return the the virtual guests.
    */
   public Set<VirtualGuest> getVirtualGuests() {
      return this.virtualGuests;
   }

   public int getQuantity() {
      return this.quantity;
   }

   public boolean getUseHourlyPricing() {
      return this.useHourlyPricing;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(packageId, location, prices, virtualGuests, quantity, useHourlyPricing);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ProductOrder that = ProductOrder.class.cast(obj);
      return Objects.equal(this.packageId, that.packageId)
            && Objects.equal(this.location, that.location)
            && Objects.equal(this.prices, that.prices)
            && Objects.equal(this.virtualGuests, that.virtualGuests)
            && Objects.equal(this.quantity, that.quantity)
            && Objects.equal(this.useHourlyPricing, that.useHourlyPricing);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("packageId", packageId).add("location", location).add("prices", prices).add("virtualGuests", virtualGuests).add("quantity", quantity).add("useHourlyPricing", useHourlyPricing);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
