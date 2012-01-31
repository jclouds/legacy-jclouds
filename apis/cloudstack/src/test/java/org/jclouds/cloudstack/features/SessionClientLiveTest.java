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

   private LoginResponse loginResponse;

   @Test(enabled = true)
   public void testLoginWithHashedPassword() throws Exception {
      loginResponse = client.getSessionClient()
         .loginUserInDomainWithHashOfPassword(USER, DOMAIN, md5Hex(PLAIN_TEXT_PASSWORD));

      assertNotNull(loginResponse);
      assertNotNull(loginResponse.getSessionKey());
   }

   @Test(dependsOnMethods = "testLoginWithHashedPassword")
   public void testRetrieveUserInfoWithSessionKey() throws Exception {
      Account account = client.getSessionClient()
         .getAccountByNameUsingSession(loginResponse.getAccountName(), loginResponse.getSessionKey());

      assertNotNull(account);
      assertEquals(account.getName(), loginResponse.getAccountName());
      
      User currentUser = find(account.getUsers(), new Predicate<User>() {
         @Override
         public boolean apply(User user) {
            return user.getId() == loginResponse.getUserId();
         }
      });
      assertNotNull(currentUser);
      assertEquals(currentUser.getName(), loginResponse.getUserName());
      assertEquals(currentUser.getDomainId(), loginResponse.getDomainId());
   }
   
   @Test(dependsOnMethods = "testRetrieveUserInfoWithSessionKey")
   public void testLogout() throws Exception {
      client.getSessionClient().logoutUser(loginResponse.getSessionKey());
   }
}
