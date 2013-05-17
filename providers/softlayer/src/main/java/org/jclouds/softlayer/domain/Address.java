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
import static com.google.common.base.Strings.emptyToNull;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Class Address
 *
 * @author Jason King
 * @see <a href= "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Account_Address"
/>
 */
public class Address {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromAddress(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected int id;
      protected String country;
      protected String state;
      protected String description;

      /**
       * @see Address#getId()
       */
      public T id(int id) {
         this.id = id;
         return self();
      }

      /**
       * @see Address#getCountry()
       */
      public T country(String country) {
         this.country = country;
         return self();
      }

      /**
       * @see Address#getState()
       */
      public T state(String state) {
         this.state = state;
         return self();
      }

      /**
       * @see Address#getDescription()
       */
      public T description(String description) {
         this.description = description;
         return self();
      }

      public Address build() {
         return new Address(id, country, state, description);
      }

      public T fromAddress(Address in) {
         return this
               .id(in.getId())
               .country(in.getCountry())
               .state(in.getState())
               .description(in.getDescription());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final int id;
   private final String country;
   private final String state;
   private final String description;

   @ConstructorProperties({
         "id", "country", "state", "description"
   })
   protected Address(int id, String country, @Nullable String state, @Nullable String description) {
      this.id = id;
      this.country = checkNotNull(emptyToNull(country),"country cannot be null or empty:"+country);
      this.state = state;
      this.description = description;
   }

   /**
    * @return The unique id of the address.
    */
   public int getId() {
      return this.id;
   }

   /**
    * @return The country of the address.
    */
   public String getCountry() {
      return this.country;
   }

   /**
    * @return The state of the address.
    */
   @Nullable
   public String getState() {
      return this.state;
   }

   /**
    * @return The description of the address.
    */
   @Nullable
   public String getDescription() {
      return this.description;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Address that = Address.class.cast(obj);
      return Objects.equal(this.id, that.id);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("country", country).add("state", state).add("description", description);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
