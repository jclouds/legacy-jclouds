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
package org.jclouds.glesys.features;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.glesys.domain.Archive;
import org.jclouds.glesys.domain.ArchiveAllowedArguments;
import org.jclouds.glesys.internal.BaseGleSYSApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

/**
 * Tests behavior of {@code ArchiveApi}
 *
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "ArchiveApiLiveTest", singleThreaded = true)
public class ArchiveApiLiveTest extends BaseGleSYSApiLiveTest {

   @BeforeClass(groups = { "integration", "live" })
   public void setup() {
      super.setup();
      
      archiveApi = api.getArchiveApi();
      archiveUser = identity.toLowerCase() + "_test9";
      archiveCounter = retry(new Predicate<Integer>() {
         public boolean apply(Integer value) {
            return archiveApi.list().size() == value.intValue();
         }
      }, 30, 1, SECONDS);
   }
   
   @AfterClass(groups = { "integration", "live" })
   protected void tearDown() {
      int before = archiveApi.list().size();
      archiveApi.delete(archiveUser);
      assertTrue(archiveCounter.apply(before - 1));

      super.tearDown();
   }

   private ArchiveApi archiveApi;
   private String archiveUser;
   private Predicate<Integer> archiveCounter;

   @Test
   public void testAllowedArguments() throws Exception {
      ArchiveAllowedArguments args = archiveApi.getAllowedArguments();
      assertNotNull(args);
      assertNotNull(args.getSizes());
      assertTrue(args.getSizes().size() > 0);
      
      for (int size : args.getSizes()) {
         assertTrue(size > 0);
      }
   }
   
   @Test
   public void testCreateArchive() throws Exception {
      try {
         archiveApi.delete(archiveUser);
      } catch(Exception ex) {
      }
      
      int before = archiveApi.list().size();
      
      archiveApi.createWithCredentialsAndSize(archiveUser, "password", 10);

      assertTrue(archiveCounter.apply(before + 1));
   }

   @Test(dependsOnMethods = "testCreateArchive")
   public void testArchiveDetails() throws Exception {
      Archive details = archiveApi.get(archiveUser);
      assertEquals(details.getUsername(), archiveUser);
   }

   @Test(dependsOnMethods = "testCreateArchive")
   public void testChangePassword() throws Exception {
      archiveApi.changePassword(archiveUser, "newpassword");      
      // TODO assert something useful!
   }

   @Test(dependsOnMethods = "testCreateArchive")
   public void testResizeArchive() throws Exception {
      archiveApi.resize(archiveUser, 20);

      assertTrue(retry(new Predicate<String>() {
         public boolean apply(String value) {
            return archiveApi.get(archiveUser) != null && value.equals(archiveApi.get(archiveUser).getTotalSize());
         }
      }, 30, 1, SECONDS).apply("20 GB"));
   }

}
