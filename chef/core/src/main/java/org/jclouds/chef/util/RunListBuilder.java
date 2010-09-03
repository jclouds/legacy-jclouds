/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.chef.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.addAll;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

/**
 * builds a run list in the correct syntax for chef.
 * 
 * @author Adrian Cole
 */
public class RunListBuilder {
   private List<String> list = newArrayList();

   /**
    * Add the following recipe to the run list
    */
   public RunListBuilder addRecipe(String recipe) {
      return addRecipes(checkNotNull(recipe, "recipe"));
   }

   /**
    * Add the following recipes to the run list
    */
   public RunListBuilder addRecipes(String... recipes) {
      addAll(list, transform(Arrays.asList(checkNotNull(recipes, "recipes")), new Function<String, String>() {

         @Override
         public String apply(String from) {
            return "recipe[" + from + "]";
         }

      }));
      return this;
   }

   /**
    * Add the following role to the run list
    */
   public RunListBuilder addRole(String role) {
      return addRoles(checkNotNull(role, "role"));
   }

   /**
    * Add the following roles to the run list
    */
   public RunListBuilder addRoles(String... roles) {
      addAll(list, transform(Arrays.asList(checkNotNull(roles, "roles")), new Function<String, String>() {

         @Override
         public String apply(String from) {
            return "role[" + from + "]";
         }

      }));
      return this;
   }

   public List<String> build() {
      return ImmutableList.copyOf(list);
   }
}