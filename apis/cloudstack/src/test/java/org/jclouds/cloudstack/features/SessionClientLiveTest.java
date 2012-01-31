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

import com.google.common.base.Predicate;
import org.jclouds.cloudstack.domain.Account;
import org.jclouds.cloudstack.domain.LoginResponse;
import org.jclouds.cloudstack.domain.User;
import org.testng.annotations.Test;

import static com.google.common.collect.Iterables.find;
import static org.jclouds.crypto.CryptoStreams.md5Hex;
import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.assertEquals;

/**
 * Tests behavior of {@code SessionClient}
 *
 * @author Andrei Savu
 */
@Test(groups = "live", singleThreaded = true, testName = "SessionClientLiveTest")
public class SessionClientLiveTest extends BaseCloudStackClientLiveTest {

   private final String USER = "jcloud";
   private final String PLAIN_TEXT_PASSWORD = "jcl0ud";
   private final String DOMAIN = "Partners/jCloud";

   private LoginResponse login;

   @Test(enabled = true)
   public void testLoginWithHashOfPassword() throws Exception {
      login = client.getSessionClient()
         .loginUserInDomainWithHashOfPassword(USER, DOMAIN, md5Hex(PLAIN_TEXT_PASSWORD));

      assertNotNull(login);
      assertNotNull(login.getSessionKey());
      assertNotNull(login.getJSessionId());
   }

   @Test(dependsOnMethods = "testLoginWithHashOfPassword")
   public void testRetrieveUserInfoWithSessionKey() throws Exception {
      Account account = client.getSessionClient().getAccountByNameUsingSession(
         login.getAccountName(), login.getSessionKey(), login.getJSessionId());

      assertNotNull(account);
      assertEquals(account.getName(), login.getAccountName());
      
      User currentUser = find(account.getUsers(), new Predicate<User>() {
         @Override
         public boolean apply(User user) {
            return user.getId() == login.getUserId();
         }
      });
      assertNotNull(currentUser);
      assertEquals(currentUser.getName(), login.getUserName());
      assertEquals(currentUser.getDomainId(), login.getDomainId());
   }

   @Test(dependsOnMethods = "testRetrieveUserInfoWithSessionKey")
   public void testLogout() throws Exception {
      client.getSessionClient().logoutUser(login.getSessionKey());
   }
}
