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
import java.util.Collection;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
*/
public class Addresses {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromAddresses(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected Set<Address> publicAddresses = ImmutableSet.of();
      protected Set<Address> privateAddresses = ImmutableSet.of();
   
      /** 
       * @see Addresses#getPublicAddresses()
       */
      public T publicAddresses(Collection<Address> publicAddresses) {
         this.publicAddresses = ImmutableSet.copyOf(checkNotNull(publicAddresses, "publicAddresses"));      
         return self();
      }

      public T publicAddresses(Address... in) {
         return publicAddresses(ImmutableSet.copyOf(in));
      }

      /** 
       * @see Addresses#getPrivateAddresses()
       */
      public T privateAddresses(Collection<Address> privateAddresses) {
         this.privateAddresses = ImmutableSet.copyOf(checkNotNull(privateAddresses, "privateAddresses"));      
         return self();
      }

      public T privateAddresses(Address... in) {
         return privateAddresses(ImmutableSet.copyOf(in));
      }

      public Addresses build() {
         return new Addresses(publicAddresses, privateAddresses);
      }
      
      public T fromAddresses(Addresses in) {
         return this
                  .publicAddresses(in.getPublicAddresses())
                  .privateAddresses(in.getPrivateAddresses());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final Set<Address> publicAddresses;
   private final Set<Address> privateAddresses;

   @ConstructorProperties({
      "public", "private"
   })
   protected Addresses(Set<Address> publicAddresses, Set<Address> privateAddresses) {
      this.publicAddresses = ImmutableSet.copyOf(checkNotNull(publicAddresses, "publicAddresses"));      
      this.privateAddresses = ImmutableSet.copyOf(checkNotNull(privateAddresses, "privateAddresses"));      
   }

   public Set<Address> getPublicAddresses() {
      return this.publicAddresses;
   }

   public Set<Address> getPrivateAddresses() {
      return this.privateAddresses;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(publicAddresses, privateAddresses);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Addresses that = Addresses.class.cast(obj);
      return Objects.equal(this.publicAddresses, that.publicAddresses)
               && Objects.equal(this.privateAddresses, that.privateAddresses);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("publicAddresses", publicAddresses).add("privateAddresses", privateAddresses);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

}
