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

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Set;

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;

/**
 * Defines a security group
*/
public class SecurityGroup {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromSecurityGroup(this);
   }

   public static abstract class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected String id;
      protected String tenantId;
      protected String name;
      protected String description;
      protected Set<SecurityGroupRule> rules = ImmutableSet.of();
   
      /** 
       * @see SecurityGroup#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /** 
       * @see SecurityGroup#getTenantId()
       */
      public T tenantId(String tenantId) {
         this.tenantId = tenantId;
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
       * @see SecurityGroup#getRules()
       */
      public T rules(Set<SecurityGroupRule> rules) {
         this.rules = ImmutableSet.copyOf(checkNotNull(rules, "rules"));      
         return self();
      }

      public T rules(SecurityGroupRule... in) {
         return rules(ImmutableSet.copyOf(in));
      }

      public SecurityGroup build() {
         return new SecurityGroup(id, tenantId, name, description, rules);
      }
      
      public T fromSecurityGroup(SecurityGroup in) {
         return this
                  .id(in.getId())
                  .tenantId(in.getTenantId())
                  .name(in.getName())
                  .description(in.getDescription())
                  .rules(in.getRules());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   @Named("tenant_id")
   private final String tenantId;
   private final String name;
   private final String description;
   private final Set<SecurityGroupRule> rules;

   @ConstructorProperties({
      "id", "tenant_id", "name", "description", "rules"
   })
   protected SecurityGroup(String id, @Nullable String tenantId, @Nullable String name, @Nullable String description, Set<SecurityGroupRule> rules) {
      this.id = checkNotNull(id, "id");
      this.tenantId = tenantId;
      this.name = name;
      this.description = description;
      // if empty, leave null so this doesn't serialize to json
      this.rules = checkNotNull(rules, "rules").size() == 0 ? null : ImmutableSet.copyOf(rules);
   }

   public String getId() {
      return this.id;
   }

   @Nullable
   public String getTenantId() {
      return this.tenantId;
   }

   @Nullable
   public String getName() {
      return this.name;
   }

   @Nullable
   public String getDescription() {
      return this.description;
   }

   public Set<SecurityGroupRule> getRules() {
      return this.rules;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, tenantId, name, description, rules);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      SecurityGroup that = SecurityGroup.class.cast(obj);
      return Objects.equal(this.id, that.id)
               && Objects.equal(this.tenantId, that.tenantId)
               && Objects.equal(this.name, that.name)
               && Objects.equal(this.description, that.description)
               && Objects.equal(this.rules, that.rules);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("tenantId", tenantId).add("name", name).add("description", description).add("rules", rules);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

}
