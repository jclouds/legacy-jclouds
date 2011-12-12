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

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.CloudStackGlobalClient;
import org.jclouds.cloudstack.domain.Account;
import org.jclouds.cloudstack.domain.ApiKeyPair;
import org.jclouds.cloudstack.domain.User;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import java.util.Properties;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.cloudstack.features.GlobalAccountClientLiveTest.createTestAccount;
import static org.jclouds.cloudstack.options.UpdateUserOptions.Builder.userName;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Tests behavior of {@code GlobaUserClient}
 */
@Test(groups = "live", singleThreaded = true, testName = "GlobalUserClientLiveTest")
public class GlobalUserClientLiveTest extends BaseCloudStackClientLiveTest {

   public static User createTestUser(CloudStackGlobalClient client, Account account, String prefix) {
      return client.getUserClient().createUser(prefix + "-user",
            account.getName(), "dummy2@example.com", "md5-password", "First", "Last");
   }

   @Test
   public void testCreateUser() {
      assert globalAdminEnabled;

      Account testAccount = createTestAccount(globalAdminClient, prefix);
      User testUser = null;
      try {
         testUser = createTestUser(globalAdminClient, testAccount, prefix);

         assertNotNull(testUser);
         assertEquals(testUser.getName(), prefix + "-user");
         assertEquals(testUser.getAccount(), prefix + "-account");

         User updatedUser = globalAdminClient.getUserClient()
            .updateUser(testUser.getId(), userName(prefix + "-user-2"));

         assertNotNull(updatedUser);
         assertEquals(updatedUser.getName(), prefix + "-user-2");

         ApiKeyPair apiKeys = globalAdminClient.getUserClient()
            .registerUserKeys(updatedUser.getId());

         assertNotNull(apiKeys.getApiKey());
         assertNotNull(apiKeys.getSecretKey());

         checkAuthAsUser(apiKeys);

      } finally {
         if (testUser != null) {
            globalAdminClient.getUserClient().deleteUser(testUser.getId());
         }
         globalAdminClient.getAccountClient().deleteAccount(testAccount.getId());
      }
   }

   private void checkAuthAsUser(ApiKeyPair keyPair) {
      ComputeServiceContext context = new ComputeServiceContextFactory(setupRestProperties()).
         createContext(provider, ImmutableSet.<Module>of(
            new Log4JLoggingModule(), new SshjSshClientModule()), credentialsAsProperties(keyPair));

      CloudStackClient client = CloudStackClient.class.cast(context.getProviderSpecificContext().getApi());
      Set<Account> accounts = client.getAccountClient().listAccounts();

      assert accounts.size() > 0;

      context.close();
   }

   private Properties credentialsAsProperties(ApiKeyPair keyPair) {
      Properties overrides = new Properties();
      overrides.put(provider + ".identity", checkNotNull(keyPair.getApiKey()));
      overrides.put(provider + ".credential", checkNotNull(keyPair.getSecretKey()));
      return overrides;
   }

}
