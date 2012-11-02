/*
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
package org.jclouds.openstack.nova.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * @author Dmitri Babaev
*/
public class Address {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromAddress(this);
   }

   public static Function<Address, String> newAddress2StringFunction() {
      return new Function<Address, String>() {
         @Override
         public String apply(@Nullable Address input) {
            return input.getAddress();
         }
      };
   }

   public static Address valueOf(String address) {
      return new Address(address, address.startsWith("::") ? 6 : 4);
   }

   public static Function<String, Address> newString2AddressFunction() {
      return new Function<String, Address>() {
         @Override
         public Address apply(@Nullable String input) {
            return valueOf(input);
         }
      };
   }
   
   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected String address;
      protected int version;
   
      /** 
       * @see Address#getAddress()
       */
      public T address(String address) {
         this.address = address;
         return self();
      }

      /** 
       * @see Address#getVersion()
       */
      public T version(int version) {
         this.version = version;
         return self();
      }

      public Address build() {
         return new Address(address, version);
      }
      
      public T fromAddress(Address in) {
         return this
                  .address(in.getAddress())
                  .version(in.getVersion());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String address;
   private final int version;

   @ConstructorProperties({
      "addr", "version"
   })
   protected Address(String address, int version) {
      this.address = checkNotNull(address, "address");
      this.version = version;
   }

   public String getAddress() {
      return this.address;
   }

   public int getVersion() {
      return this.version;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(address, version);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Address that = Address.class.cast(obj);
      return Objects.equal(this.address, that.address)
               && Objects.equal(this.version, that.version);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("address", address).add("version", version);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

}
