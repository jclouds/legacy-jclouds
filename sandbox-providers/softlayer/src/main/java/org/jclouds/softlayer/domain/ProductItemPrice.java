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

import javax.annotation.Nullable;

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
      private long id = -1;
      private long itemId = -1;
      private Float recurringFee;
      private Float hourlyRecurringFee;

      public Builder id(long id) {
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

      public ProductItemPrice build() {
         return new ProductItemPrice(id, itemId, recurringFee, hourlyRecurringFee);
      }

      public static Builder fromPrice(ProductItemPrice in) {
         return ProductItemPrice.builder().id(in.getId()).itemId(in.getItemId())
               .hourlyRecurringFee(in.getHourlyRecurringFee()).recurringFee(in.getRecurringFee());
      }
   }

   private long id = -1;
   private long itemId = -1;
   private Float recurringFee;
   private Float hourlyRecurringFee;

   // for deserializer
   ProductItemPrice() {

   }

   public ProductItemPrice(long id, long itemId, Float recurringFee, Float hourlyRecurringFee) {
      this.id = id;
      this.itemId = itemId;
      this.recurringFee = recurringFee;
      this.hourlyRecurringFee = hourlyRecurringFee;
   }

   @Override
   public int compareTo(ProductItemPrice arg0) {
      return new Long(id).compareTo(arg0.getId());
   }

   /**
    * @return The unique identifier of a Product Item Price.
    */
   public long getId() {
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

   public Builder toBuilder() {
      return Builder.fromPrice(this);
   }

   @Override
   public String toString() {
      return "[id=" + id + ", itemId=" + itemId + ", recurringFee=" + recurringFee + ", hourlyRecurringFee="
            + hourlyRecurringFee + "]";
   }

}
