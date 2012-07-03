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
package org.jclouds.openstack.keystone.v2_0.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.ImmutableSet;

/**
 * An OpenStack service, such as Compute (Nova), Object Storage (Swift), or Image Service (Glance).
 * A service provides one or more endpoints through which users can access resources and perform
 * (presumably useful) operations.
 *
 * @author Adrian Cole
 * @see <a href="http://docs.openstack.org/api/openstack-typeentity-service/2.0/content/Identity-Service-Concepts-e1362.html"
/>
 */
public class Service extends ForwardingSet<Endpoint> implements Comparable<Service> {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromService(this);
   }

   public static abstract class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected String type;
      protected String name;
      protected Set<Endpoint> endpoints = ImmutableSet.of();

      /**
       * @see Service#getType()
       */
      public T type(String type) {
         this.type = type;
         return self();
      }

      /**
       * @see Service#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see Service#getEndpoints()
       */
      public T endpoints(Set<Endpoint> endpoints) {
         this.endpoints = ImmutableSet.copyOf(checkNotNull(endpoints, "endpoints"));
         return self();
      }

      public T endpoints(Endpoint... in) {
         return endpoints(ImmutableSet.copyOf(in));
      }

      public Service build() {
         return new Service(type, name, endpoints);
      }

      public T fromService(Service in) {
         return this
               .type(in.getType())
               .name(in.getName())
               .endpoints(in.getEndpoints());
      }
   }
   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String type;
   private final String name;
   private final Set<Endpoint> endpoints;

   @ConstructorProperties({
         "type", "name", "endpoints"
   })
   protected Service(String type, String name, Set<Endpoint> endpoints) {
      this.type = checkNotNull(type, "type");
      this.name = checkNotNull(name, "name");
      this.endpoints = ImmutableSet.copyOf(checkNotNull(endpoints, "endpoints"));
   }

   /**
    * such as {@code compute} (Nova), {@code object-store} (Swift), or {@code image} (Glance)
    *
    * @return the type of the service in the current OpenStack deployment
    */
   public String getType() {
      return this.type;
   }

   /**
    * @return the name of the service
    */
   public String getName() {
      return this.name;
   }

   /**
    * @return the endpoints assigned to the service
    */
   public Set<Endpoint> getEndpoints() {
      return this.endpoints;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(type, name, endpoints);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Service that = Service.class.cast(obj);
      return Objects.equal(this.type, that.type)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.endpoints, that.endpoints);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("type", type).add("name", name).add("endpoints", endpoints);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(Service that) {
      return ComparisonChain.start()
                            .compare(this.type, that.type)
                            .compare(this.name, that.name)
                            .result();
   }

   @Override
   protected Set<Endpoint> delegate() {
      return endpoints;
   }
}
