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
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Tests behavior of {@code GlobalAccountClient}
 *
 * @author Adrian Cole, Andrei Savu
 */
@Test(groups = "live", singleThreaded = true, testName = "GlobalAccountClientLiveTest")
public class GlobalAccountClientLiveTest extends BaseCloudStackClientLiveTest {

   @Test
   public void testCreateAndRemoveAccount() {
      Account account = null;
      try {
         account = globalAdminClient.getAccountClient().createAccount(
            prefix + "-account", Account.Type.USER, "dummy@example.com",
            "First", "Last", "hashed-password");

         assertNotNull(account);
         assertEquals(account.getName(), prefix + "-account");
         assertEquals(account.getType(), Account.Type.USER);

         Account updated = globalAdminClient.getAccountClient().updateAccount(
            account.getName(), account.getDomainId(), prefix + "-account-2");

         assertNotNull(updated);
         assertEquals(updated.getName(), prefix + "-account-2");

      } finally {
         if (account != null) {
            globalAdminClient.getAccountClient().deleteAccount(account.getId());
         }
      }

   }

}
