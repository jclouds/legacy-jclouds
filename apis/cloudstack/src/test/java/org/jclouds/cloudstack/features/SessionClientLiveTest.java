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
package org.jclouds.cloudstack.features;

import org.jclouds.cloudstack.domain.Account;
import org.jclouds.cloudstack.domain.LoginResponse;
import org.jclouds.cloudstack.domain.User;
import org.jclouds.cloudstack.util.ApiKeyPairs;
import org.jclouds.crypto.CryptoStreams;
import org.testng.annotations.Test;

import static org.jclouds.cloudstack.features.GlobalAccountClientLiveTest.createTestAccount;
import static org.jclouds.cloudstack.features.GlobalUserClientLiveTest.createTestUser;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Tests behavior of {@code SessionClient}
 *
 * @author Andrei Savu
 */
@Test(groups = "live", singleThreaded = true, testName = "SessionClientLiveTest")
public class SessionClientLiveTest extends BaseCloudStackClientLiveTest {

   @Test
   public void testCreateContextUsingUserAndPasswordAuthentication() {
      assert globalAdminEnabled;

      Account testAccount = null;
      User testUser = null;
      String prefix = this.prefix + "-session";
      try {
         testAccount = createTestAccount(globalAdminClient, prefix);
         testUser = createTestUser(globalAdminClient, testAccount, prefix);

         String expectedUsername = prefix + "-user";
         assertEquals(testUser.getName(), expectedUsername);

         checkLoginAsTheNewUser(expectedUsername);

         assertEquals(
            globalAdminClient.getUserClient().registerUserKeys(testUser.getId()),
            ApiKeyPairs.getApiKeyPairForUser(
               System.getProperty("test.cloudstack.endpoint"), prefix + "-user", "password", "")
         );

      } finally {
         if (testUser != null)
            globalAdminClient.getUserClient().deleteUser(testUser.getId());
         if (testAccount != null)
            globalAdminClient.getAccountClient().deleteAccount(testAccount.getId());
      }
   }

   private void checkLoginAsTheNewUser(String expectedUsername) {
      LoginResponse response = globalAdminClient.getSessionClient()
         .loginUserInDomainWithHashOfPassword(expectedUsername, "", CryptoStreams.md5Hex("password"));

      assertNotNull(response);
      assertNotNull(response.getSessionKey());
      assertNotNull(response.getJSessionId());

      client.getSessionClient().logoutUser(response.getSessionKey());
   }
}
