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

import com.google.common.base.Objects;

/**
 * A container used to group or isolate resources and/or identity objects. Depending on the service
 * operator, a tenant may map to a customer, account, organization, or project.
 * 
 * @author Adrian Cole
 * @see <a href="http://docs.openstack.org/api/openstack-identity-service/2.0/content/Identity-Service-Concepts-e1362.html"
 *      />
 */
public class Tenant implements Comparable<Tenant> {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromTenant(this);
   }

   public static class Builder {
      protected String id;
      protected String name;

      /**
       * @see Tenant#getId()
       */
      public Builder id(String id) {
         this.id = checkNotNull(id, "id");
         return this;
      }

      /**
       * @see Tenant#getName()
       */
      public Builder name(String name) {
         this.name = checkNotNull(name, "name");
         return this;
      }

      public Tenant build() {
         return new Tenant(id, name);
      }

      public Builder fromTenant(Tenant from) {
         return id(from.getId()).name(from.getName());
      }
   }

   protected final String id;
   protected final String name;

   public Tenant(String id, String name) {
      this.id = checkNotNull(id, "id");
      this.name = checkNotNull(name, "name");
   }

   /**
    * When providing an ID, it is assumed that the tenant exists in the current OpenStack deployment
    * 
    * @return the id of the tenant in the current OpenStack deployment
    */
   public String getId() {
      return id;
   }

   /**
    * @return the name of the tenant
    */
   public String getName() {
      return name;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Tenant) {
         final Tenant other = Tenant.class.cast(object);
         return equal(id, other.id) && equal(name, other.name);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name);
   }

   @Override
   public String toString() {
      return toStringHelper("").add("id", id).add("name", name).toString();
   }

   @Override
   public int compareTo(Tenant that) {
      if (that == null)
         return 1;
      if (this == that)
         return 0;
      return this.id.compareTo(that.id);
   }

}
