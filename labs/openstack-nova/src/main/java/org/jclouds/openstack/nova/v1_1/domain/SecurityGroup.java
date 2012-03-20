/**
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
package org.jclouds.openstack.nova.v1_1.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.gson.annotations.SerializedName;

/**
 * Defines a security group
 * 
 */
public class SecurityGroup {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromSecurityGroup(this);
   }

   public static class Builder {

      private String id;
      private String tenantId;
      private String name;
      private String description;
      private Set<SecurityGroupRule> rules = ImmutableSet.<SecurityGroupRule> of();

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder tenantId(String tenantId) {
         this.tenantId = tenantId;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder description(String description) {
         this.description = description;
         return this;
      }

      /**
       * 
       * @see #getSecurityGroupNames
       */
      public Builder rules(SecurityGroupRule... rules) {
         return rules(ImmutableSet.copyOf(checkNotNull(rules, "rules")));
      }

      /**
       * @see #getSecurityGroupNames
       */
      public Builder rules(Iterable<SecurityGroupRule> rules) {
         this.rules = ImmutableSet.copyOf(checkNotNull(rules, "rules"));
         return this;
      }

      public Builder rules(Set<SecurityGroupRule> rules) {
         this.rules = rules;
         return this;
      }

      public SecurityGroup build() {
         return new SecurityGroup(id, tenantId, name, description, rules);
      }

      public Builder fromSecurityGroup(SecurityGroup in) {
         return id(in.getId()).tenantId(in.getTenantId()).name(in.getName()).description(in.getDescription()).rules(
                  in.getRules());
      }

   }

   protected final String id;
   @SerializedName("tenant_id")
   protected final String tenantId;
   protected final String name;
   protected final String description;
   protected final Set<SecurityGroupRule> rules;

   protected SecurityGroup(String id, String tenantId, @Nullable String name, @Nullable String description,
            Set<SecurityGroupRule> rules) {
      this.id = id;
      this.tenantId = tenantId;
      this.name = name;
      this.description = description;
      // if empty, leave null so this doesn't serialize to json
      this.rules = checkNotNull(rules, "rules").size() == 0 ? null : ImmutableSet.copyOf(rules);
   }

   public String getId() {
      return this.id;
   }

   public String getTenantId() {
      return this.tenantId;
   }

   public String getName() {
      return this.name;
   }

   public String getDescription() {
      return this.description;
   }

   public Set<SecurityGroupRule> getRules() {
      return this.rules == null ? ImmutableSet.<SecurityGroupRule> of() : rules;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof SecurityGroup) {
         final SecurityGroup other = SecurityGroup.class.cast(object);
         return equal(tenantId, other.tenantId) && equal(id, other.id) && equal(name, other.name);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(tenantId, id, name);
   }

   @Override
   public String toString() {
      return toStringHelper("").add("tenantId", getTenantId()).add("id", getId()).add("name", getName()).add(
               "description", description).add("rules", getRules()).toString();
   }

}