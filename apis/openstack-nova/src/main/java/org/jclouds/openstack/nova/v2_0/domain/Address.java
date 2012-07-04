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
package org.jclouds.openstack.nova.v2_0.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * IP address
 * 
 * @author AdrianCole
*/
public class Address {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromAddress(this);
   }

   public static Address createV4(String addr) {
      return builder().version(4).addr(addr).build();
   }

   public static Address createV6(String addr) {
      return builder().version(6).addr(addr).build();
   }

   public static abstract class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected String addr;
      protected int version;
   
      /** 
       * @see Address#getAddr()
       */
      public T addr(String addr) {
         this.addr = addr;
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
         return new Address(addr, version);
      }
      
      public T fromAddress(Address in) {
         return this
                  .addr(in.getAddr())
                  .version(in.getVersion());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String addr;
   private final int version;

   @ConstructorProperties({
      "addr", "version"
   })
   protected Address(String addr, int version) {
      this.addr = checkNotNull(addr, "addr");
      this.version = version;
   }

   /**
    * @return the ip address
    */
   public String getAddr() {
      return this.addr;
   }

   /**
    * @return the IP version, ex. 4
    */
   public int getVersion() {
      return this.version;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(addr, version);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Address that = Address.class.cast(obj);
      return Objects.equal(this.addr, that.addr)
               && Objects.equal(this.version, that.version);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("addr", addr).add("version", version);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

}
