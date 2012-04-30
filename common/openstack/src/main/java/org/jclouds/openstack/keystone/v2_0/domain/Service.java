/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Name 2.0 (the
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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

/**
 * An OpenStack service, such as Compute (Nova), Object Storage (Swift), or Image Service (Glance).
 * A service provides one or more endpoints through which users can access resources and perform
 * (presumably useful) operations.
 * 
 * @author Adrian Cole
 * @see <a href="http://docs.openstack.org/api/openstack-typeentity-service/2.0/content/Identity-Service-Concepts-e1362.html"
 *      />
 */
public class Service implements Comparable<Service> {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromService(this);
   }

   public static class Builder {
      protected String type;
      protected String name;
      protected Set<Endpoint> endpoints = ImmutableSet.of();

      /**
       * @see Service#getType()
       */
      public Builder type(String type) {
         this.type = checkNotNull(type, "type");
         return this;
      }

      /**
       * @see Service#getName()
       */
      public Builder name(String name) {
         this.name = checkNotNull(name, "name");
         return this;
      }

      /**
       * @see Service#getEndpoints()
       */
      public Builder endpoints(Endpoint... endpoints) {
         return endpoints(ImmutableSet.copyOf(checkNotNull(endpoints, "endpoints")));
      }

      /**
       * @see Service#getEndpoints()
       */
      public Builder endpoints(Set<Endpoint> endpoints) {
         this.endpoints = ImmutableSet.copyOf(checkNotNull(endpoints, "endpoints"));
         return this;
      }

      public Service build() {
         return new Service(type, name, endpoints);
      }

      public Builder fromService(Service from) {
         return type(from.getType()).name(from.getName()).endpoints(from.getEndpoints());
      }
   }

   protected final String type;
   protected final String name;
   protected final Set<Endpoint> endpoints;

   public Service(String type, String name, Set<Endpoint> endpoints) {
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
      return type;
   }

   /**
    * @return the name of the service
    */
   public String getName() {
      return name;
   }

   /**
    * @return the endpoints assigned to the service
    */
   public Set<Endpoint> getEndpoints() {
      return endpoints;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Service) {
         final Service other = Service.class.cast(object);
         return equal(type, other.type) && equal(name, other.name) && equal(endpoints, other.endpoints);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(type, name, endpoints);
   }

   @Override
   public String toString() {
      return toStringHelper("").add("type", type).add("name", name).add("endpoints", endpoints).toString();
   }

   @Override
   public int compareTo(Service that) {
      if (that == null)
         return 1;
      if (this == that)
         return 0;
      return this.type.compareTo(that.type);
   }

}
