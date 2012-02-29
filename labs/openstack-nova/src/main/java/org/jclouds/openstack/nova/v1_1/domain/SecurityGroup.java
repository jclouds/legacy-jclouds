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

import static com.google.common.base.Objects.toStringHelper;

import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

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
      private Set<SecurityGroupRule> rules;

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
      
      public Builder rules(Set<SecurityGroupRule> rules) {
         this.rules = rules;
         return this;
      }

      public SecurityGroup build() {
         return new SecurityGroup(id, tenantId, name, description, rules);
      }

      
      public Builder fromSecurityGroup(SecurityGroup in) {
         return id(in.getId()).tenantId(in.getTenantId()).name(in.getName())
               .description(in.getDescription()).rules(in.getRules());
      }
      
   }
   
   protected String id;
   @SerializedName("tenant_id")
   protected String tenantId;
   protected String name;
   protected String description;
   protected Set<SecurityGroupRule> rules;
   
   protected SecurityGroup(String id, String tenantId, @Nullable String name,
         @Nullable String description, Set<SecurityGroupRule> rules) {
      this.id = id;
      this.tenantId = tenantId;
      this.name = name;
      this.description = description;
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
      return this.rules;
   }
   
   
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result
            + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((rules == null) ? 0 : rules.hashCode());
      result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      SecurityGroup other = (SecurityGroup) obj;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (rules == null) {
         if (other.rules != null)
            return false;
      } else if (!rules.equals(other.rules))
         return false;
      if (tenantId == null) {
         if (other.tenantId != null)
            return false;
      } else if (!tenantId.equals(other.tenantId))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return toStringHelper("").add("id", id).add("name", name)
            .add("tenantId", tenantId).add("description", description).add("rules", rules)
            .toString();
   }

}