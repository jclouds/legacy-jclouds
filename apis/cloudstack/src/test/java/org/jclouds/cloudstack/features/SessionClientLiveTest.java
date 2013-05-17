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

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.hash.Hashing.md5;
import static com.google.common.io.BaseEncoding.base16;
import static org.jclouds.cloudstack.features.GlobalAccountClientLiveTest.createTestAccount;
import static org.jclouds.cloudstack.features.GlobalUserClientLiveTest.createTestUser;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.net.URI;

import org.jclouds.cloudstack.domain.Account;
import org.jclouds.cloudstack.domain.ApiKeyPair;
import org.jclouds.cloudstack.domain.LoginResponse;
import org.jclouds.cloudstack.domain.User;
import org.jclouds.cloudstack.internal.BaseCloudStackClientLiveTest;
import org.jclouds.cloudstack.util.ApiKeyPairs;
import org.jclouds.rest.AuthorizationException;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code SessionClient}
 *
 * @author Andrei Savu
 */
@Test(groups = "live", singleThreaded = true, testName = "SessionClientLiveTest")
public class SessionClientLiveTest extends BaseCloudStackClientLiveTest {

   @Test
   public void testCreateContextUsingUserAndPasswordAuthentication() {
      skipIfNotGlobalAdmin();

      Account testAccount = null;
      User testUser = null;

      String prefix = this.prefix + "-session";
      try {
         testAccount = createTestAccount(globalAdminClient, prefix);
         testUser = createTestUser(globalAdminClient, testAccount, prefix);

         String expectedUsername = prefix + "-user";
         assertEquals(testUser.getName(), expectedUsername);

         checkLoginAsTheNewUser(expectedUsername);

         ApiKeyPair expected = globalAdminClient.getUserClient().registerUserKeys(testUser.getId());
         ApiKeyPair actual = ApiKeyPairs.loginToEndpointAsUsernameInDomainWithPasswordAndReturnApiKeyPair(
            URI.create(endpoint), prefix + "-user", "password", "");

         assertEquals(actual, expected);

      } finally {
         if (testUser != null)
            globalAdminClient.getUserClient().deleteUser(testUser.getId());
         if (testAccount != null)
            globalAdminClient.getAccountClient().deleteAccount(testAccount.getId());
      }
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testTryToGetApiKeypairWithWrongCredentials() {
      ApiKeyPairs.loginToEndpointAsUsernameInDomainWithPasswordAndReturnApiKeyPair(
         URI.create(endpoint), "dummy-missing-user", "with-a-wrong-password", "");
   }

   private void checkLoginAsTheNewUser(String expectedUsername) {
      LoginResponse response = globalAdminClient.getSessionClient().loginUserInDomainWithHashOfPassword(
            expectedUsername, "", base16().lowerCase().encode(md5().hashString("password", UTF_8).asBytes()));

      assertNotNull(response);
      assertNotNull(response.getSessionKey());
      assertNotNull(response.getJSessionId());

      client.getSessionClient().logoutUser(response.getSessionKey());
   }
}
