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

import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 
 * @author Jason King
 * @see <a href= "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Account_Address"
 *      />
 */
public class Address implements Comparable<Address> {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private long id = -1;
      private String country;
      private String state;
      private String description;

      public Builder id(long id) {
         this.id = id;
         return this;
      }

      public Builder country(String country) {
         this.country = country;
         return this;
      }

      public Builder state(String state) {
         this.state = state;
         return this;
      }

      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public Address build() {
         return new Address(id, country, state, description);
      }

      public static Builder fromAddress(Address in) {
         return Address.builder().id(in.getId())
                                 .country(in.getCountry())
                                 .state(in.getState())
                                 .description(in.getDescription());
      }
   }

   private long id = -1;
   private String country;
   private String state;
   private String description;

   // for deserializer
   Address() {

   }

   public Address(long id, String country, String state, String description) {
      this.id = id;
      this.country = checkNotNull(emptyToNull(country),"country cannot be null or empty:"+country);
      this.state = state;
      this.description = description;
   }

   @Override
   public int compareTo(Address arg0) {
      return new Long(id).compareTo(arg0.getId());
   }

   /**
    * @return The unique id of the address.
    */
   public long getId() {
      return id;
   }

   /**
    * @return The country of the address.
    */
   public String getCountry() {
      return country;
   }

   /**
    * @return The state of the address.
    */
   public String getState() {
      return state;
   }

   /**
    * @return The description of the address.
    */
   public String getDescription() {
      return description;
   }

   public Builder toBuilder() {
      return Builder.fromAddress(this);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (id ^ (id >>> 32));
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Address other = (Address) obj;
      if (id != other.id)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + id + ", country=" + country + ", state=" + state + ", description=" + description + "]";
   }
   
   
}
