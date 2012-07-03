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

import java.beans.ConstructorProperties;

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Class Host
*/
public class Host {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromHost(this);
   }

   public static abstract class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected String name;
      protected String service;
   
      /** 
       * @see Host#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /** 
       * @see Host#getService()
       */
      public T service(String service) {
         this.service = service;
         return self();
      }

      public Host build() {
         return new Host(name, service);
      }
      
      public T fromHost(Host in) {
         return this
                  .name(in.getName())
                  .service(in.getService());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   @Named("host_name")
   private final String name;
   private final String service;

   @ConstructorProperties({
      "host_name", "service"
   })
   protected Host(@Nullable String name, @Nullable String service) {
      this.name = name;
      this.service = service;
   }

   @Nullable
   public String getName() {
      return this.name;
   }

   @Nullable
   public String getService() {
      return this.service;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, service);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Host that = Host.class.cast(obj);
      return Objects.equal(this.name, that.name)
               && Objects.equal(this.service, that.service);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("name", name).add("service", service);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

}
