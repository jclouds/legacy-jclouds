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

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests possible uses of RunListBuilder
 * 
 * @author Adrian Cole
 */
public class RunListBuilderTest {

   @Test
   public void testRecipeAndRole() {
      RunListBuilder options = new RunListBuilder();
      options.addRecipe("recipe").addRole("role");
      assertEquals(options.build(),ImmutableList.of("recipe[recipe]","role[role]"));
   }

   @Test
   public void testRecipe() {
      RunListBuilder options = new RunListBuilder();
      options.addRecipe("test");
      assertEquals(options.build(),ImmutableList.of("recipe[test]"));
   }
   @Test
   public void testRecipes() {
      RunListBuilder options = new RunListBuilder();
      options.addRecipes("test", "test2");
      assertEquals(options.build(),ImmutableList.of("recipe[test]","recipe[test2]"));
   }

   @Test
   public void testRole() {
      RunListBuilder options = new RunListBuilder();
      options.addRole("test");
      assertEquals(options.build(),ImmutableList.of("role[test]"));
   }
   @Test
   public void testRoles() {
      RunListBuilder options = new RunListBuilder();
      options.addRoles("test", "test2");
      assertEquals(options.build(),ImmutableList.of("role[test]","role[test2]"));
   }

   @Test
   public void testNoneRecipe() {
      RunListBuilder options = new RunListBuilder();
      assertEquals(options.build(), ImmutableList.<String>of());
   }

}
