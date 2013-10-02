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

/**
 * Class Customer
 * 
 * @author Oleksiy Yarmula
*/
public class Customer {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromCustomer(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected long id;
      protected String name;
   
      /** 
       * @see Customer#getId()
       */
      public T id(long id) {
         this.id = id;
         return self();
      }

      /** 
       * @see Customer#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      public Customer build() {
         return new Customer(id, name);
      }
      
      public T fromCustomer(Customer in) {
         return this
                  .id(in.getId())
                  .name(in.getName());
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

   @ConstructorProperties({
      "id", "name"
   })
   protected Customer(long id, String name) {
      this.id = id;
      this.name = checkNotNull(name, "name");
   }

   public long getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Customer that = Customer.class.cast(obj);
      return Objects.equal(this.id, that.id)
               && Objects.equal(this.name, that.name);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("name", name);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

}
