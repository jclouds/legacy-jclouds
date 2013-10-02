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
package org.jclouds.cloudservers.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Collection;
import java.util.Set;

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;

/**
 * Class Addresses
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

      protected Set<String> publicAddresses;
      protected Set<String> privateAddresses;
   
      /** 
       * @see Addresses#getPublicAddresses()
       */
      public T publicAddresses(Collection<String> publicAddresses) {
         this.publicAddresses = ImmutableSet.copyOf(checkNotNull(publicAddresses, "publicAddresses"));      
         return self();
      }

      public T publicAddresses(String... in) {
         return publicAddresses(ImmutableSet.copyOf(in));
      }

      /** 
       * @see Addresses#getPrivateAddresses()
       */
      public T privateAddresses(Collection<String> privateAddresses) {
         this.privateAddresses = ImmutableSet.copyOf(checkNotNull(privateAddresses, "privateAddresses"));      
         return self();
      }

      public T privateAddresses(String... in) {
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

   @Named("public")
   private final Set<String> publicAddresses;
   @Named("private")
   private final Set<String> privateAddresses;

   @ConstructorProperties({
      "public", "private"
   })
   protected Addresses(@Nullable Set<String> publicAddresses, @Nullable Set<String> privateAddresses) {
      this.publicAddresses = publicAddresses == null ? null : ImmutableSet.copyOf(publicAddresses);      
      this.privateAddresses = privateAddresses == null ? null : ImmutableSet.copyOf(privateAddresses);    
   }

   @Nullable
   public Set<String> getPublicAddresses() {
      return this.publicAddresses;
   }

   @Nullable
   public Set<String> getPrivateAddresses() {
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
