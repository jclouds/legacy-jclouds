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

package org.jclouds.openstack.nova.v1_1.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;

import com.google.common.base.Objects;

/**
 * IP address
 * 
 * @author AdrianCole
 */
public class Address {

   /**
    * Relations associated with resources.
    */
   public static enum Type {
      /**
       * internet routable address
       */
      INTERNET,
      /**
       * publically routable address
       */
      PUBLIC,
      /**
       * address that is not publicly routable.
       */
      PRIVATE,
      /**
       * the value returned by the OpenStack service was not recognized.
       */
      UNRECOGNIZED;

      public String value() {
         return name().toLowerCase();
      }

      public static Type fromValue(String v) {
         try {
            return valueOf(v.toUpperCase());
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }

   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromAddress(this);
   }

   public static Address createV4(String addr) {
      return builder().version(4).addr(addr).build();
   }

   public static Address createV6(String addr) {
      return builder().version(6).addr(addr).build();
   }

   public static class Builder {
      protected String addr;
      protected int version;

      /**
       * @see Address#getVersion()
       */
      protected Builder version(int version) {
         this.version = version;
         return this;
      }

      /**
       * @see Address#getAddr()
       */
      public Builder addr(String addr) {
         this.addr = addr;
         return this;
      }

      public Address build() {
         return new Address(addr, version);
      }

      public Builder fromAddress(Address from) {
         return addr(from.getAddr()).version(from.getVersion());
      }
   }

   protected final String addr;
   protected final int version;

   public Address(String addr, int version) {
      this.addr = addr;
      this.version = version;
   }

   /**
    * @return the ip address
    */
   public String getAddr() {
      return addr;
   }

   /**
    * @return the IP version, ex. 4
    */
   public int getVersion() {
      return version;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Address) {
         final Address other = Address.class.cast(object);
         return equal(addr, other.addr) && equal(version, other.version);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(addr, version);
   }

   @Override
   public String toString() {
      return toStringHelper("").add("addr", addr).add("version", version).toString();
   }

}
