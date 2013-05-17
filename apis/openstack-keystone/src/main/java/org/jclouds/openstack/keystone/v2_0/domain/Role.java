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
package org.jclouds.openstack.keystone.v2_0.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

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
/>
 */
public class Role {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromRole(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();
      protected String id;
      protected String name;
      protected String description;
      protected String serviceId;
      protected String tenantId;

      /**
       * @see Role#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see Role#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see Role#getDescription()
       */
      public T description(String description) {
         this.description = description;
         return self();
      }

      /**
       * @see Role#getServiceId()
       */
      public T serviceId(String serviceId) {
         this.serviceId = serviceId;
         return self();
      }

      /**
       * @see Role#getTenantId()
       */
      public T tenantId(String tenantId) {
         this.tenantId = tenantId;
         return self();
      }

      public Role build() {
         return new Role(id, name, description, serviceId, tenantId, null);
      }

      public T fromRole(Role in) {
         return this
               .id(in.getId())
               .name(in.getName())
               .description(in.getDescription())
               .serviceId(in.getServiceId())
               .tenantId(in.getTenantId());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final String name;
   private final String description;
   private final String serviceId;
   private final String tenantId;

   @ConstructorProperties({
         "id", "name", "description", "serviceId", "tenantId", "tenantName"
   })
   protected Role(@Nullable String id, String name, @Nullable String description, @Nullable String serviceId, @Nullable String tenantId,
                  @Nullable String tenantName) {
      this.id = id;
      this.name = checkNotNull(name, "name");
      this.description = description;
      this.serviceId = serviceId;
      this.tenantId = tenantId != null ? tenantId :  tenantName;
   }

   /**
    * When providing an ID, it is assumed that the role exists in the current OpenStack deployment
    *
    * @return the id of the role in the current OpenStack deployment
    */
   @Nullable
   public String getId() {
      return this.id;
   }

   /**
    * @return the name of the role
    */
   public String getName() {
      return this.name;
   }

   /**
    * @return the description of the role
    */
   @Nullable
   public String getDescription() {
      return this.description;
   }

   /**
    * @return the service id of the role or null, if not present
    */
   @Nullable
   public String getServiceId() {
      return this.serviceId;
   }

   /**
    * @return the tenant id of the role or null, if not present
    */
   @Nullable
   public String getTenantId() {
      return this.tenantId;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name, description, serviceId, tenantId);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Role that = Role.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.description, that.description)
            && Objects.equal(this.serviceId, that.serviceId)
            && Objects.equal(this.tenantId, that.tenantId);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues()
            .add("id", id).add("name", name).add("description", description).add("serviceId", serviceId).add("tenantId", tenantId);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
