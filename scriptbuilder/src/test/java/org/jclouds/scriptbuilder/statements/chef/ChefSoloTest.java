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

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.ShellToken;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;

@Test(groups = "unit", testName = "ChefSoloTest")
public class ChefSoloTest {

   @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "cookbooksArchiveLocation must be set")
   public void testChefSoloWithoutCookbooksLocation() {
      ChefSolo.builder().build();
   }

   @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "recipes must be set")
   public void testChefSoloWithoutRecipes() {
      new ChefSolo("/tmp/foo", null);
   }

   @Test(expectedExceptions = UnsupportedOperationException.class, expectedExceptionsMessageRegExp = "windows not yet implemented")
   public void testChefSoloInWindows() {
      ChefSolo.builder().cookbooksArchiveLocation("/tmp/cookbooks").build().render(OsFamily.WINDOWS);
   }

   public void testChefSoloWithCookbooksLocation() throws IOException {
      String script = ChefSolo.builder().cookbooksArchiveLocation("/tmp/cookbooks").build().render(OsFamily.UNIX);
      assertEquals(
            script,
            Resources.toString(Resources.getResource("test_install_ruby." + ShellToken.SH.to(OsFamily.UNIX)),
                  Charsets.UTF_8) + "installChefGems || return 1\nchef-solo -N `hostname` -r /tmp/cookbooks\n");
   }

   public void testChefSoloWithCookbooksLocationAndSingleRecipe() throws IOException {
      String script = ChefSolo.builder().cookbooksArchiveLocation("/tmp/cookbooks").installRecipe("apache2").build()
            .render(OsFamily.UNIX);
      assertEquals(
            script,
            Resources.toString(Resources.getResource("test_install_ruby." + ShellToken.SH.to(OsFamily.UNIX)),
                  Charsets.UTF_8)
                  + "installChefGems || return 1\nchef-solo -N `hostname` -r /tmp/cookbooks -o recipe[apache2]\n");
   }

   public void testChefSoloWithCookbooksLocationAndMultipleRecipes() throws IOException {
      String script = ChefSolo.builder().cookbooksArchiveLocation("/tmp/cookbooks").installRecipe("apache2")
            .installRecipe("mysql").build().render(OsFamily.UNIX);
      assertEquals(
            script,
            Resources.toString(Resources.getResource("test_install_ruby." + ShellToken.SH.to(OsFamily.UNIX)),
                  Charsets.UTF_8)
                  + "installChefGems || return 1\nchef-solo -N `hostname` -r /tmp/cookbooks -o recipe[apache2],recipe[mysql]\n");
   }

   public void testChefSoloWithCookbooksLocationAndMultipleRecipesInList() throws IOException {
      String script = ChefSolo.builder().cookbooksArchiveLocation("/tmp/cookbooks")
            .installRecipes(ImmutableList.<String> of("apache2", "mysql")).build().render(OsFamily.UNIX);
      assertEquals(
            script,
            Resources.toString(Resources.getResource("test_install_ruby." + ShellToken.SH.to(OsFamily.UNIX)),
                  Charsets.UTF_8)
                  + "installChefGems || return 1\nchef-solo -N `hostname` -r /tmp/cookbooks -o recipe[apache2],recipe[mysql]\n");
   }

}
