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
package org.jclouds.openstack.nova.domain;

import com.google.common.base.Function;
import com.google.gson.annotations.SerializedName;
import org.jclouds.javax.annotation.Nullable;

/**
 * @author Dmitri Babaev
 */
public class Address {
   @SerializedName("addr")
   private String address;
   private int version;

   //for de-serialization
   @SuppressWarnings("unused")
   private Address() {
   }

   public Address(String address, int version) {
      this.address = address;
      this.version = version;
   }

   public String getAddress() {
      return address;
   }

   public int getVersion() {
      return version;
   }

   @Override
   public String toString() {
      return address;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Address address1 = (Address) o;

      if (version != address1.version) return false;
      if (address != null ? !address.equals(address1.address) : address1.address != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = address != null ? address.hashCode() : 0;
      result = 31 * result + version;
      return result;
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
}
