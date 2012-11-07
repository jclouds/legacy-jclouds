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
package org.jclouds.scriptbuilder.statements.chef;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.scriptbuilder.domain.Statements.createOrOverwriteFile;
import static org.jclouds.scriptbuilder.domain.Statements.exec;

import java.util.List;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementList;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.scriptbuilder.domain.chef.DataBag;
import org.jclouds.scriptbuilder.domain.chef.DataBag.Item;
import org.jclouds.scriptbuilder.domain.chef.Role;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

/**
 * Bootstraps a node using Chef Solo.
 * 
 * @author Ignasi Barrera
 */
public class ChefSolo implements Statement {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String cookbooksArchiveLocation;
      private String jsonAttributes;
      private List<Role> roles = Lists.newArrayList();
      private List<DataBag> databags = Lists.newArrayList();
      private List<String> runlist = Lists.newArrayList();

      public Builder cookbooksArchiveLocation(String cookbooksArchiveLocation) {
         this.cookbooksArchiveLocation = checkNotNull(cookbooksArchiveLocation, "cookbooksArchiveLocation");
         return this;
      }

      public Builder jsonAttributes(String jsonAttributes) {
         this.jsonAttributes = checkNotNull(jsonAttributes, "jsonAttributes");
         return this;
      }

      public Builder defineRole(Role role) {
         this.roles.add(checkNotNull(role, "role"));
         return this;
      }

      public Builder defineRoles(Iterable<Role> roles) {
         this.roles = ImmutableList.<Role> copyOf(checkNotNull(roles, "roles"));
         return this;
      }

      /**
       * @since Chef 0.10.4
       */
      public Builder defineDataBag(DataBag dataBag) {
         this.databags.add(checkNotNull(dataBag, "dataBag"));
         return this;
      }

      /**
       * @since Chef 0.10.4
       */
      public Builder defineDataBags(Iterable<DataBag> databags) {
         this.databags = ImmutableList.<DataBag> copyOf(checkNotNull(databags, "databags"));
         return this;
      }

      public Builder installRecipe(String recipe) {
         this.runlist.add("recipe[" + checkNotNull(recipe, "recipe") + "]");
         return this;
      }

      public Builder installRecipes(Iterable<String> recipes) {
         this.runlist.addAll(Lists.newArrayList(transform(checkNotNull(recipes, "recipes"),
               new Function<String, String>() {
                  @Override
                  public String apply(String input) {
                     return "recipe[" + input + "]";
                  }
               })));
         return this;
      }

      public Builder installRole(String role) {
         this.runlist.add("role[" + checkNotNull(role, "role") + "]");
         return this;
      }

      public Builder installRoles(Iterable<String> roles) {
         this.runlist.addAll(Lists.newArrayList(transform(checkNotNull(roles, "roles"), new Function<String, String>() {
            @Override
            public String apply(String input) {
               return "role[" + input + "]";
            }
         })));
         return this;
      }

      public ChefSolo build() {
         return new ChefSolo(cookbooksArchiveLocation, Optional.fromNullable(jsonAttributes), Optional.of(roles),
               Optional.of(databags), runlist);
      }

   }

   private String cookbooksArchiveLocation;
   private Optional<String> jsonAttributes;
   private Optional<List<Role>> roles;
   private Optional<List<DataBag>> databags;
   private List<String> runlist;
   private final InstallChefGems installChefGems = new InstallChefGems();

   public ChefSolo(String cookbooksArchiveLocation, Optional<String> jsonAttributes, Optional<List<Role>> roles,
         Optional<List<DataBag>> databags, List<String> runlist) {
      this.cookbooksArchiveLocation = checkNotNull(cookbooksArchiveLocation, "cookbooksArchiveLocation must be set");
      this.roles = checkNotNull(roles, "roles must be set");
      this.databags = checkNotNull(databags, "databags must be set");
      this.runlist = ImmutableList.copyOf(checkNotNull(runlist, "runlist must be set"));
      this.jsonAttributes = checkNotNull(jsonAttributes, "jsonAttributes must be set");
   }

   @Override
   public String render(OsFamily family) {
      if (family == OsFamily.WINDOWS) {
         throw new UnsupportedOperationException("windows not yet implemented");
      }

      ImmutableList.Builder<Statement> statements = ImmutableList.builder();
      statements.add(installChefGems);
      statements.add(exec("{md} /var/chef"));

      // The roles directory must contain one file for each role definition
      if (roles.isPresent() && !roles.get().isEmpty()) {
         statements.add(exec("{md} /var/chef/roles"));
         for (Role role : roles.get()) {
            statements.add(createOrOverwriteFile("/var/chef/roles/" + role.getName() + ".json",
                  ImmutableSet.of(role.toJsonString())));
         }
      }

      // Each data bag item must be defined in a file inside the data bag
      // directory
      if (databags.isPresent() && !databags.get().isEmpty()) {
         statements.add(exec("{md} /var/chef/data_bags"));
         for (DataBag databag : databags.get()) {
            String databagFolder = "/var/chef/data_bags/" + databag.getName();
            statements.add(exec("{md} " + databagFolder));
            for (Item item : databag.getItems()) {
               statements.add(createOrOverwriteFile(databagFolder + "/" + item.getName() + ".json",
                     ImmutableSet.of(item.getJsonData())));
            }
         }
      }

      ImmutableMap.Builder<String, String> chefSoloOptions = ImmutableMap.builder();
      chefSoloOptions.put("-N", "`hostname`");
      chefSoloOptions.put("-r", cookbooksArchiveLocation);

      if (jsonAttributes.isPresent()) {
         statements.add(createOrOverwriteFile("/var/chef/node.json", jsonAttributes.asSet()));
         chefSoloOptions.put("-j", "/var/chef/node.json");
      }

      if (!runlist.isEmpty()) {
         chefSoloOptions.put("-o", Joiner.on(',').join(runlist));
      }

      String options = Joiner.on(' ').withKeyValueSeparator(" ").join(chefSoloOptions.build());
      statements.add(Statements.exec(String.format("chef-solo %s", options)));

      return new StatementList(statements.build()).render(family);
   }

   @Override
   public Iterable<String> functionDependencies(OsFamily family) {
      return installChefGems.functionDependencies(family);
   }

}
