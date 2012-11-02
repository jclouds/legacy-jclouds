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

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Defines a security group
 * 
 * @author chamerling
*/
public class SecurityGroup {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromSecurityGroup(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected int id;
      protected String name;
      protected String description;
      protected String tenantId;
   
      /** 
       * @see SecurityGroup#getId()
       */
      public T id(int id) {
         this.id = id;
         return self();
      }

      /** 
       * @see SecurityGroup#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /** 
       * @see SecurityGroup#getDescription()
       */
      public T description(String description) {
         this.description = description;
         return self();
      }

      /** 
       * @see SecurityGroup#getTenantId()
       */
      public T tenantId(String tenantId) {
         this.tenantId = tenantId;
         return self();
      }

      public SecurityGroup build() {
         return new SecurityGroup(id, name, description, tenantId);
      }
      
      public T fromSecurityGroup(SecurityGroup in) {
         return this
                  .id(in.getId())
                  .name(in.getName())
                  .description(in.getDescription())
                  .tenantId(in.getTenantId());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final int id;
   private final String name;
   private final String description;
   private final String tenantId;

   @ConstructorProperties({
      "id", "name", "description", "tenant_id"
   })
   protected SecurityGroup(int id, String name, @Nullable String description, @Nullable String tenantId) {
      this.id = id;
      this.name = checkNotNull(name, "name");
      this.description = description;
      this.tenantId = tenantId;
   }

   /**
    * @return the id
    */
   public int getId() {
      return this.id;
   }

   /**
    * @return the name
    */
   public String getName() {
      return this.name;
   }

   /**
    * @return the description
    */
   @Nullable
   public String getDescription() {
      return this.description;
   }

   /**
    * @return the tenantId
    */
   @Nullable
   public String getTenantId() {
      return this.tenantId;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name, description, tenantId);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      SecurityGroup that = SecurityGroup.class.cast(obj);
      return Objects.equal(this.id, that.id)
               && Objects.equal(this.name, that.name)
               && Objects.equal(this.description, that.description)
               && Objects.equal(this.tenantId, that.tenantId);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("name", name).add("description", description).add("tenantId", tenantId);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

}
