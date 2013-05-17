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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.jclouds.cloudstack.CloudStackGlobalClient;
import org.jclouds.cloudstack.domain.Account;
import org.jclouds.cloudstack.internal.BaseCloudStackClientLiveTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code GlobalAccountClient}
 *
 * @author Adrian Cole, Andrei Savu
 */
@Test(groups = "live", singleThreaded = true, testName = "GlobalAccountClientLiveTest")
public class GlobalAccountClientLiveTest extends BaseCloudStackClientLiveTest {

   public static Account createTestAccount(CloudStackGlobalClient client, String prefix) {
      return client.getAccountClient().createAccount(prefix + "-account", Account.Type.USER, "dummy@example.com",
            "First", "Last", base16().lowerCase().encode(md5().hashString("password", UTF_8).asBytes()));
   }

   @Test
   public void testCreateAndRemoveAccount() {
      skipIfNotGlobalAdmin();

      Account account = null;
      try {
         account = createTestAccount(globalAdminClient, prefix);

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
