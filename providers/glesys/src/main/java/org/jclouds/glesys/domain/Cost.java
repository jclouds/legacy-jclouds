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
package org.jclouds.glesys.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * The Cost class contains information about the cost of a server
 *
 * @author Adam Lowe
 * @see ServerDetails
 */
public class Cost {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromCost(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected double amount;
      protected String currency;
      protected String timePeriod;

      /**
       * @see Cost#getAmount()
       */
      public T amount(double amount) {
         this.amount = amount;
         return self();
      }

      /**
       * @see Cost#getCurrency()
       */
      public T currency(String currency) {
         this.currency = checkNotNull(currency, "currency");
         return self();
      }

      /**
       * @see Cost#getTimePeriod()
       */
      public T timePeriod(String timePeriod) {
         this.timePeriod = checkNotNull(timePeriod, "timePeriod");
         return self();
      }

      public Cost build() {
         return new Cost(amount, currency, timePeriod);
      }

      public T fromCost(Cost in) {
         return this.amount(in.getAmount()).currency(in.getCurrency()).timePeriod(in.getTimePeriod());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final double amount;
   private final String currency;
   private final String timePeriod;

   @ConstructorProperties({
         "amount", "currency", "timeperiod"
   })
   protected Cost(double amount, String currency, String timePeriod) {
      this.amount = amount;
      this.currency = checkNotNull(currency, "currency");
      this.timePeriod = checkNotNull(timePeriod, "timePeriod");
   }

   /**
    * @return the numeric cost in #currency / #timePeriod
    */
   public double getAmount() {
      return this.amount;
   }

   /**
    * @return the currency unit, e.g. "EUR" for Euro
    */
   public String getCurrency() {
      return this.currency;
   }

   /**
    * @return the time period for which this cost charged, e.g. "month"
    */
   public String getTimePeriod() {
      return this.timePeriod;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(amount, currency, timePeriod);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Cost that = Cost.class.cast(obj);
      return Objects.equal(this.amount, that.amount)
            && Objects.equal(this.currency, that.currency)
            && Objects.equal(this.timePeriod, that.timePeriod);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("")
            .add("amount", amount).add("currency", currency).add("timePeriod", timePeriod);
   }

   @Override
   public String toString() {
      return string().toString();
   }
}
