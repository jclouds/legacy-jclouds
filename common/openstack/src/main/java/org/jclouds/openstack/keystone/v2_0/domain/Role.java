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

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

/**
 * A personality that a user assumes when performing a specific set of operations. A role includes a
 * set of right and privileges. A user assuming that role inherits those rights and privileges.
 * <p/>
 * In Keystone, a token that is issued to a user includes the list of roles that user can assume.
 * Services that are being called by that user determine how they interpret the set of roles a user
 * has and which operations or resources each roles grants access to.
 * 
 * @author AdrianCole
 * @see <a href="http://docs.openstack.org/api/openstack-identity-service/2.0/content/Identity-Service-Concepts-e1362.html"
 *      />
 */
public class Role implements Comparable<Role> {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromRole(this);
   }

   public static class Builder {
      protected String id;
      protected String name;
      protected String serviceId;
      protected String tenantId;

      /**
       * @see Role#getId()
       */
      public Builder id(String id) {
         this.id = checkNotNull(id, "id");
         return this;
      }

      /**
       * @see Role#getName()
       */
      public Builder name(String name) {
         this.name = checkNotNull(name, "name");
         return this;
      }

      /**
       * @see Role#getServiceId()
       */
      public Builder serviceId(@Nullable String serviceId) {
         this.serviceId = serviceId;
         return this;
      }

      /**
       * @see Role#getTenantId()
       */
      public Builder tenantId(@Nullable String tenantId) {
         this.tenantId = tenantId;
         return this;
      }

      public Role build() {
         return new Role(id, name, serviceId, tenantId);
      }

      public Builder fromRole(Role from) {
         return id(from.getId()).name(from.getName()).serviceId(from.getServiceId()).tenantId(from.getTenantId());
      }
   }

   protected final String id;
   protected final String name;
   protected final String serviceId;
   // renamed half-way through
   @Deprecated
   protected String tenantName;
   protected final String tenantId;

   protected Role(String id, String name, @Nullable String serviceId, @Nullable String tenantId) {
      this.id = checkNotNull(id, "id");
      this.name = checkNotNull(name, "name");
      this.serviceId = serviceId;
      this.tenantId = tenantId;
   }

   /**
    * When providing an ID, it is assumed that the role exists in the current OpenStack deployment
    * 
    * @return the id of the role in the current OpenStack deployment
    */
   public String getId() {
      return id;
   }

   /**
    * @return the name of the role
    */
   public String getName() {
      return name;
   }

   /**
    * @return the service id of the role or null, if not present
    */
   @Nullable
   public String getServiceId() {
      return serviceId;
   }

   /**
    * @return the tenant id of the role or null, if not present
    */
   @Nullable
   public String getTenantId() {
      return tenantId != null ? tenantId : tenantName;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Role) {
         final Role other = Role.class.cast(object);
         return equal(id, other.id) && equal(name, other.name) && equal(serviceId, other.serviceId)
                  && equal(getTenantId(), other.getTenantId());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name, serviceId, getTenantId());
   }

   @Override
   public String toString() {
      return toStringHelper("").add("id", id).add("name", name).add("serviceId", serviceId).add("tenantId", getTenantId())
               .toString();
   }
   
   @Override
   public int compareTo(Role that) {
      return ComparisonChain.start().compare(this.id, that.id).result();
   }

}
