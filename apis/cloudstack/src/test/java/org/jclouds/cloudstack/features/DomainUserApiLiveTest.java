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
package org.jclouds.cloudstack.features;

import static org.jclouds.cloudstack.features.GlobalAccountApiLiveTest.createTestAccount;
import static org.jclouds.cloudstack.features.GlobalUserApiLiveTest.createTestUser;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.cloudstack.domain.Account;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.AsyncJob;
import org.jclouds.cloudstack.domain.User;
import org.jclouds.cloudstack.internal.BaseCloudStackApiLiveTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code DomainUserApi}
 */
@Test(groups = "live", singleThreaded = true, testName = "DomainUserApiLiveTest")
public class DomainUserApiLiveTest extends BaseCloudStackApiLiveTest {

   @Test
   public void testListUsers() {
      skipIfNotDomainAdmin();

      Set<User> users = domainAdminClient.getUserClient().listUsers();

      assert users.size() > 0;
      assert users.contains(user); // contains the current user

      for (User user : users) {
         checkUser(user);
      }
   }

   private void checkUser(User user) {
      assert user.getId() != null;
      assert user.getAccount() != null;
      assert user.getDomain() != null;
   }

   @Test
   public void testEnableDisableUser() {
      skipIfNotGlobalAdmin();

      Account testAccount = null;
      User testUser = null;
      try {
         testAccount = createTestAccount(globalAdminClient, prefix);
         testUser = createTestUser(globalAdminClient, testAccount, prefix);

         AsyncCreateResponse response = domainAdminClient.getUserClient().disableUser(testUser.getId());
         assertNotNull(response);
         assertTrue(adminJobComplete.apply(response.getJobId()));

         AsyncJob<User> job = domainAdminClient.getAsyncJobApi().getAsyncJob(response.getJobId());
         assertNotNull(job);
         assertEquals(job.getResult().getState(), User.State.DISABLED);

         User updated = domainAdminClient.getUserClient().enableUser(testUser.getId());
         assertNotNull(updated);
         assertEquals(updated.getState(), User.State.ENABLED);

      } finally {
         if (testUser != null) {
            globalAdminClient.getUserClient().deleteUser(testUser.getId());
         }
         if (testAccount != null) {
            globalAdminClient.getAccountApi().deleteAccount(testAccount.getId());
         }
      }
   }
}
