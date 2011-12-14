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
package org.jclouds.glesys.domain;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The Cost class contains information about the cost of a server
 *
 * @author Adam Lowe
 * @see ServerDetails
 */
public class Cost {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private double amount;
      private String currency;
      private String timePeriod;

      public Builder amount(double amount) {
         this.amount = amount;
         return this;
      }

      public Builder currency(String currency) {
         this.currency = currency;
         return this;
      }

      public Builder timePeriod(String timePeriod) {
         this.timePeriod = timePeriod;
         return this;
      }

      public Cost build() {
         return new Cost(amount, currency, timePeriod);
      }

      public Builder fromCost(Cost cost) {
         return amount(cost.getAmount()).currency(cost.getCurrency()).timePeriod(cost.getTimePeriod());
      }
   }

   private final double amount;
   private final String currency;
   @SerializedName("timeperiod")
   private final String timePeriod;

   public Cost(double amount, String currency, String timePeriod) {
      this.amount = amount;
      this.currency = checkNotNull(currency, "currency");
      this.timePeriod = timePeriod;
   }

   /**
    * @return the numeric cost in #currency / #timePeriod
    */
   public double getAmount() {
      return amount;
   }

   /**
    * @return the currency unit, e.g. "EUR" for Euro
    */
   public String getCurrency() {
      return currency;
   }

   /**
    * @return the time period for which this cost charged, e.g. "month"
    */
   public String getTimePeriod() {
      return timePeriod;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Cost) {
         Cost other = (Cost) object;
         return Objects.equal(amount, other.amount)
               && Objects.equal(currency, other.currency)
               && Objects.equal(timePeriod, other.timePeriod);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(amount, currency, timePeriod);
   }

   @Override
   public String toString() {
      return String.format(
            "[amount=%f, currency=%s, timePeriod=%s]", amount, currency, timePeriod);
   }
}
