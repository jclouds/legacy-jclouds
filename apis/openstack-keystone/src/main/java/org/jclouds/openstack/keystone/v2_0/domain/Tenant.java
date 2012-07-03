/*
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

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * A container used to group or isolate resources and/or identity objects. Depending on the service
 * operator, a tenant may map to a customer, account, organization, or project.
 *
 * @author Adrian Cole
 * @see <a href="http://docs.openstack.org/api/openstack-identity-service/2.0/content/Identity-Service-Concepts-e1362.html"
/>
 */
public class Tenant {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromTenant(this);
   }

   public static abstract class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected String id;
      protected String name;
      protected String description;

      /**
       * @see Tenant#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see Tenant#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see Tenant#getDescription()
       */
      public T description(String description) {
         this.description = description;
         return self();
      }

      public Tenant build() {
         return new Tenant(id, name, description);
      }

      public T fromTenant(Tenant in) {
         return this
               .id(in.getId())
               .name(in.getName())
               .description(in.getDescription());
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

   @ConstructorProperties({
         "id", "name", "description"
   })
   protected Tenant(String id, String name, @Nullable String description) {
      this.id = checkNotNull(id, "id");
      this.name = checkNotNull(name, "name");
      this.description = description;
   }

   /**
    * When providing an ID, it is assumed that the tenant exists in the current OpenStack deployment
    *
    * @return the id of the tenant in the current OpenStack deployment
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return the name of the tenant
    */
   public String getName() {
      return this.name;
   }

   /**
    * @return the description of the tenant
    */
   @Nullable
   public String getDescription() {
      return this.description;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name, description);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Tenant that = Tenant.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.description, that.description);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("name", name).add("description", description);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
