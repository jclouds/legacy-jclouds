/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.scriptbuilder.statements.login;

import static java.lang.String.format;
import static org.jclouds.scriptbuilder.statements.login.AdminAccessBuilderSpec.parse;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

import java.io.File;
import java.lang.reflect.Field;

import javax.inject.Provider;

import org.jclouds.scriptbuilder.statements.login.AdminAccess.Builder;
import org.testng.annotations.Test;

import com.google.gson.Gson;

/**
 * 
 * <p/>
 * inspired by guava {@code CacheBuilderSpecTest}
 * 
 * @author David Alves
 */
@Test(testName = "AdminAccessBuilderSpecTest")
public class AdminAccessBuilderSpecTest {

   Provider<AdminAccess.Builder> adminAccessBuilders = new Provider<AdminAccess.Builder>() {

      @Override
      public Builder get() {
         return new AdminAccess.Builder();
      }

   };

   public void testParseEmpty() {
      AdminAccessBuilderSpec spec = parse("");
      assertNull(spec.adminHome);
      assertNull(spec.adminPassword);
      assertNull(spec.adminPrivateKeyFile);
      assertNull(spec.adminPublicKeyFile);
      assertNull(spec.adminUsername);
      assertNull(spec.authorizeAdminPublicKey);
      assertNull(spec.grantSudoToAdminUser);
      assertNull(spec.installAdminPrivateKey);
      assertNull(spec.lockSsh);
      assertNull(spec.loginPassword);
      assertNull(spec.resetLoginPassword);
      assertAdminAccessBuilderEquivalence(adminAccessBuilders.get(), adminAccessBuilders.get().from(spec));
   }

   public void testParseAdminUsername() {
      AdminAccessBuilderSpec spec = parse("adminUsername=superUser");
      assertEquals(spec.adminUsername, "superUser");
      assertNull(spec.adminHome);
      assertNull(spec.adminPassword);
      assertNull(spec.adminPrivateKeyFile);
      assertNull(spec.adminPublicKeyFile);
      assertNull(spec.authorizeAdminPublicKey);
      assertNull(spec.grantSudoToAdminUser);
      assertNull(spec.installAdminPrivateKey);
      assertNull(spec.lockSsh);
      assertNull(spec.loginPassword);
      assertNull(spec.resetLoginPassword);
      assertAdminAccessBuilderEquivalence(adminAccessBuilders.get().adminUsername("superUser"), adminAccessBuilders
               .get().from(spec));
   }

   public void testParseAdminHome() {
      AdminAccessBuilderSpec spec = parse("adminHome=/home/superUser");
      assertEquals(spec.getAdminHome(), "/home/superUser");
      assertNull(spec.adminUsername);
      assertNull(spec.adminPassword);
      assertNull(spec.adminPrivateKeyFile);
      assertNull(spec.adminPublicKeyFile);
      assertNull(spec.authorizeAdminPublicKey);
      assertNull(spec.grantSudoToAdminUser);
      assertNull(spec.installAdminPrivateKey);
      assertNull(spec.lockSsh);
      assertNull(spec.loginPassword);
      assertNull(spec.resetLoginPassword);
      assertAdminAccessBuilderEquivalence(adminAccessBuilders.get().adminHome("/home/superUser"), adminAccessBuilders
               .get().from(spec));
   }

   public void testParsePrivateKeyFile() {
      AdminAccessBuilderSpec spec = parse("adminPrivateKeyFile=target/test-classes/test");
      assertEquals(spec.getAdminPrivateKeyFile().getPath(), format("target%stest-classes%stest", File.separator, File.separator));
      assertNull(spec.adminHome);
      assertNull(spec.adminPassword);
      assertNull(spec.adminPublicKeyFile);
      assertNull(spec.authorizeAdminPublicKey);
      assertNull(spec.grantSudoToAdminUser);
      assertNull(spec.installAdminPrivateKey);
      assertNull(spec.lockSsh);
      assertNull(spec.loginPassword);
      assertNull(spec.resetLoginPassword);
      assertAdminAccessBuilderEquivalence(adminAccessBuilders.get().adminHome("/home/superUser"), adminAccessBuilders
               .get().from(spec));
   }

   public void testParseLockSSh() {
      AdminAccessBuilderSpec spec = parse("lockSsh=true");
      assertEquals(spec.getLockSsh(), Boolean.TRUE);
      assertNull(spec.adminUsername);
      assertNull(spec.adminHome);
      assertNull(spec.adminPassword);
      assertNull(spec.adminPrivateKeyFile);
      assertNull(spec.adminPublicKeyFile);
      assertNull(spec.authorizeAdminPublicKey);
      assertNull(spec.grantSudoToAdminUser);
      assertNull(spec.installAdminPrivateKey);
      assertNull(spec.loginPassword);
      assertNull(spec.resetLoginPassword);
      assertAdminAccessBuilderEquivalence(adminAccessBuilders.get().lockSsh(true), adminAccessBuilders.get().from(spec));
   }

   public void testParseAdminUsernameRepeated() {
      try {
         parse("adminUsername=superUser, adminUsername=notSoSuperUser");
         fail("Expected exception");
      } catch (IllegalArgumentException expected) {
         // expected
      }
   }

   public void testParseNonExistingFiles() {
      try {
         parse("adminPrivateKeyFile=nonExistingFile.txt");
         fail("Expected exception");
      } catch (IllegalArgumentException expected) {
         // expected
      }
      try {
         parse("adminPublicKeyFile=nonExistingFile.txt");
         fail("Expected exception");
      } catch (IllegalArgumentException expected) {
         // expected
      }
   }

   public void testNiceJson() {
      AdminAccessBuilderSpec spec = parse("adminUsername=nimda,adminPassword=dictionaryword");
      assertEquals(new Gson().toJson(spec), "{\"adminUsername\":\"nimda\",\"adminPassword\":\"dictionaryword\"}");
      assertEquals(new Gson().fromJson(new Gson().toJson(spec), AdminAccessBuilderSpec.class), spec);
   }

   public void testParse_unknownKey() {
      try {
         parse("foo=17");
         fail("Expected exception");
      } catch (IllegalArgumentException expected) {
         // expected
      }
   }

   public void testAdminAccessBuilderFromString() {
      AdminAccess.Builder fromString = adminAccessBuilders.get().from(
               "adminUsername=nimda,adminPassword=dictionaryword");
      AdminAccess.Builder expected = adminAccessBuilders.get().adminUsername("nimda").adminPassword("dictionaryword");
      assertAdminAccessBuilderEquivalence(expected, fromString);
   }

   private void assertAdminAccessBuilderEquivalence(AdminAccess.Builder a, AdminAccess.Builder b) {
      // Labs hack: dig into the TemplateBuilder instances, verifying all fields
      // are equal.
      for (Field f : AdminAccess.Builder.class.getFields()) {
         f.setAccessible(true);
         try {
            assertEquals(f.get(a), f.get(b), "Field " + f.getName() + " not equal");
         } catch (IllegalArgumentException e) {
            throw new AssertionError(e.getMessage());
         } catch (IllegalAccessException e) {
            throw new AssertionError(e.getMessage());
         }
      }
   }
}
