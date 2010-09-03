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

package org.jclouds.chef.predicates;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.get;

import org.jclouds.chef.domain.CookbookVersion;
import org.jclouds.chef.domain.Resource;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

/**
 * Container for cookbook filters (predicates).
 * 
 * This class has static methods that create customized predicates to use with
 * {@link org.jclouds.chef.ChefService}.
 * 
 * @author Adrian Cole
 */
public class CookbookVersionPredicates {
   /**
    * @see #containsRecipes
    */
   public static Predicate<CookbookVersion> containsRecipe(String recipe) {
      return containsRecipes(checkNotNull(recipe, "recipe must be defined"));
   }

   /**
    * Note that the default recipe of a cookbook is its name. Otherwise, you prefix the recipe with
    * the name of the cookbook. ex. {@code apache2} will be the default recipe where {@code
    * apache2::mod_proxy} is a specific one in the cookbook.
    * 
    * @param recipes
    *           names of the recipes.
    * @return true if the cookbook version contains a recipe in the list.
    */
   public static Predicate<CookbookVersion> containsRecipes(String... recipes) {
      checkNotNull(recipes, "recipes must be defined");
      final Multimap<String, String> search = LinkedListMultimap.create();
      for (String recipe : recipes) {
         if (recipe.indexOf("::") != -1) {
            Iterable<String> nameRecipe = Splitter.on("::").split(recipe);
            search.put(get(nameRecipe, 0), get(nameRecipe, 1) + ".rb");
         } else {
            search.put(recipe, "default.rb");
         }
      }
      return new Predicate<CookbookVersion>() {
         @Override
         public boolean apply(final CookbookVersion cookbookVersion) {
            return search.containsKey(cookbookVersion.getCookbookName())
                     && any(search.get(cookbookVersion.getCookbookName()), new Predicate<String>() {

                        @Override
                        public boolean apply(final String recipeName) {
                           return any(cookbookVersion.getRecipes(), new Predicate<Resource>() {

                              @Override
                              public boolean apply(Resource resource) {
                                 return resource.getName().equals(recipeName);
                              }

                           });
                        }

                     });
         }

         @Override
         public String toString() {
            return "containsRecipes(" + search + ")";
         }
      };
   }
}
