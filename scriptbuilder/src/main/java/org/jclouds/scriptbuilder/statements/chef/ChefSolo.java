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

import java.util.List;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementList;
import org.jclouds.scriptbuilder.domain.Statements;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
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
      private List<String> recipes = Lists.newArrayList();

      public Builder cookbooksArchiveLocation(String cookbooksArchiveLocation) {
         this.cookbooksArchiveLocation = checkNotNull(cookbooksArchiveLocation, "cookbooksArchiveLocation");
         return this;
      }

      public Builder installRecipe(String recipe) {
         this.recipes.add(checkNotNull(recipe, "recipe"));
         return this;
      }

      public Builder installRecipes(Iterable<String> recipes) {
         this.recipes = ImmutableList.<String> copyOf(checkNotNull(recipes, "recipes"));
         return this;
      }

      public ChefSolo build() {
         return new ChefSolo(cookbooksArchiveLocation, recipes);
      }

   }

   private String cookbooksArchiveLocation;
   private List<String> recipes;
   private final InstallChefGems installChefGems = new InstallChefGems();

   public ChefSolo(String cookbooksArchiveLocation, List<String> recipes) {
      this.cookbooksArchiveLocation = checkNotNull(cookbooksArchiveLocation, "cookbooksArchiveLocation must be set");
      this.recipes = checkNotNull(recipes, "recipes must be set");
   }

   @Override
   public String render(OsFamily family) {
      if (family == OsFamily.WINDOWS) {
         throw new UnsupportedOperationException("windows not yet implemented");
      }

      ImmutableMap.Builder<String, String> chefSoloOptions = ImmutableMap.builder();
      chefSoloOptions.put("-N", "`hostname`");
      chefSoloOptions.put("-r", cookbooksArchiveLocation);
      if (!recipes.isEmpty()) {
         chefSoloOptions.put("-o", recipesToRunlistString(recipes));
      }

      String options = Joiner.on(' ').withKeyValueSeparator(" ").join(chefSoloOptions.build());

      ImmutableList.Builder<Statement> statements = ImmutableList.builder();
      statements.add(installChefGems);
      statements.add(Statements.exec(String.format("chef-solo %s", options)));

      return new StatementList(statements.build()).render(family);
   }

   @Override
   public Iterable<String> functionDependencies(OsFamily family) {
      return installChefGems.functionDependencies(family);
   }

   private static String recipesToRunlistString(List<String> recipes) {
      return Joiner.on(',').join(transform(recipes, new Function<String, String>() {
         @Override
         public String apply(String input) {
            return "recipe[" + input + "]";
         }
      }));
   }

}
