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
package org.jclouds.gogrid.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.primitives.Longs;

/**
 * Class BillingToken
 * 
 * @author Oleksiy Yarmula
*/
public class BillingToken implements Comparable<BillingToken> {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromBillingToken(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected long id;
      protected String name;
      protected double price;
   
      /** 
       * @see BillingToken#getId()
       */
      public T id(long id) {
         this.id = id;
         return self();
      }

      /** 
       * @see BillingToken#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /** 
       * @see BillingToken#getPrice()
       */
      public T price(double price) {
         this.price = price;
         return self();
      }

      public BillingToken build() {
         return new BillingToken(id, name, price);
      }
      
      public T fromBillingToken(BillingToken in) {
         return this
                  .id(in.getId())
                  .name(in.getName())
                  .price(in.getPrice());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final long id;
   private final String name;
   private final double price;

   @ConstructorProperties({
      "id", "name", "price"
   })
   protected BillingToken(long id, String name, double price) {
      this.id = id;
      this.name = checkNotNull(name, "name");
      this.price = price;
   }

   public long getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public double getPrice() {
      return this.price;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name, price);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      BillingToken that = BillingToken.class.cast(obj);
      return Objects.equal(this.id, that.id)
               && Objects.equal(this.name, that.name)
               && Objects.equal(this.price, that.price);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("name", name).add("price", price);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(BillingToken o) {
      return Longs.compare(id, o.getId());
   }

}
