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
package org.jclouds.scriptbuilder.domain.chef;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * A Role to be configured for a Chef Solo run.
 * 
 * @author Ignasi Barrera
 */
public class Role {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String name;
      private String description;
      private String jsonDefaultAttributes;
      private String jsonOverrideAttributes;
      private List<String> runlist = Lists.newArrayList();

      public Builder name(String name) {
         this.name = checkNotNull(name, "name must be set");
         return this;
      }

      public Builder description(String description) {
         this.description = checkNotNull(description, "description must be set");
         return this;
      }

      public Builder jsonDefaultAttributes(String jsonDefaultAttributes) {
         this.jsonDefaultAttributes = checkNotNull(jsonDefaultAttributes, "jsonDefaultAttributes must be set");
         return this;
      }

      public Builder jsonOverrideAttributes(String jsonOverrideAttributes) {
         this.jsonOverrideAttributes = checkNotNull(jsonOverrideAttributes, "jsonOverrideAttributes must be set");
         return this;
      }

      public Builder installRecipe(String recipe) {
         this.runlist.add("recipe[" + checkNotNull(recipe, "recipe must be set") + "]");
         return this;
      }

      public Builder installRecipes(Iterable<String> recipes) {
         this.runlist.addAll(Lists.newArrayList(transform(checkNotNull(recipes, "recipes must be set"),
               new Function<String, String>() {
                  @Override
                  public String apply(String input) {
                     return "recipe[" + input + "]";
                  }
               })));
         return this;
      }

      public Builder installRole(String role) {
         this.runlist.add("role[" + checkNotNull(role, "role must be set") + "]");
         return this;
      }

      public Builder installRoles(Iterable<String> roles) {
         this.runlist.addAll(Lists.newArrayList(transform(checkNotNull(roles, "roles must be set"),
               new Function<String, String>() {
                  @Override
                  public String apply(String input) {
                     return "role[" + input + "]";
                  }
               })));
         return this;
      }

      public Role build() {
         return new Role(name, Optional.fromNullable(description), Optional.fromNullable(jsonDefaultAttributes),
               Optional.fromNullable(jsonOverrideAttributes), runlist);
      }
   }

   private String name;
   private Optional<String> description;
   private Optional<String> jsonDefaultAttributes;
   private Optional<String> jsonOverrideAttributes;
   private List<String> runlist;

   protected Role(String name, Optional<String> description, Optional<String> jsonDefaultAttributes,
         Optional<String> jsonOverrideAttributes, List<String> runlist) {
      this.name = checkNotNull(name, "name must be set");
      this.description = checkNotNull(description, "description must be set");
      this.jsonDefaultAttributes = checkNotNull(jsonDefaultAttributes, "jsonDefaultAttributes must be set");
      this.jsonOverrideAttributes = checkNotNull(jsonOverrideAttributes, "jsonOverrideAttributes must be set");
      this.runlist = ImmutableList.<String> copyOf(checkNotNull(runlist, "runlist must be set"));
   }

   public String toJsonString() {
      StringBuilder json = new StringBuilder();
      json.append("{");
      json.append("\"name\": \"").append(name).append("\",");
      json.append("\"description\":\"").append(description.or("")).append("\",");
      json.append("\"default_attributes\":").append(jsonDefaultAttributes.or("{}")).append(",");
      json.append("\"override_attributes\":").append(jsonOverrideAttributes.or("{}")).append(",");
      json.append("\"json_class\":\"Chef::Role\",");
      json.append("\"chef_type\":\"role\",");
      json.append("\"run_list\":[").append(runlistToJsonString(runlist)).append("]");
      json.append("}");
      return json.toString();
   }

   public String getName() {
      return name;
   }

   public Optional<String> getDescription() {
      return description;
   }

   public Optional<String> getJsonDefaultAttributes() {
      return jsonDefaultAttributes;
   }

   public Optional<String> getJsonOverrideAttributes() {
      return jsonOverrideAttributes;
   }

   public List<String> getRunlist() {
      return runlist;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      Role other = Role.class.cast(obj);
      return Objects.equal(name, other.name);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("name", name).add("description", description.orNull())
            .toString();
   }

   private static String runlistToJsonString(List<String> runlist) {
      return Joiner.on(',').join(transform(runlist, new Function<String, String>() {
         @Override
         public String apply(String input) {
            return "\"" + input + "\"";
         }
      }));
   }

}
