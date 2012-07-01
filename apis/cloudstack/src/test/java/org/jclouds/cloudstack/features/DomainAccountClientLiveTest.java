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

import static org.jclouds.cloudstack.features.GlobalAccountClientLiveTest.createTestAccount;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.cloudstack.domain.Account;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.AsyncJob;
import org.jclouds.cloudstack.internal.BaseCloudStackClientLiveTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code DomainAccountClient}
 *
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "DomainAccountClientLiveTest")
public class DomainAccountClientLiveTest extends BaseCloudStackClientLiveTest {

   @Test
   public void testEnableDisableAccount() {
      skipIfNotGlobalAdmin();

      Account testAccount = null;
      try {
         testAccount = createTestAccount(globalAdminClient, prefix);

         AsyncCreateResponse response = domainAdminClient.getAccountClient()
            .disableAccount(testAccount.getName(), testAccount.getDomainId(), false);
         assertNotNull(response);
         assertTrue(jobComplete.apply(response.getJobId()));

         AsyncJob<Account> job = domainAdminClient.getAsyncJobClient().getAsyncJob(response.getJobId());
         assertEquals(job.getResult().getState(), Account.State.DISABLED);

         Account updated = domainAdminClient.getAccountClient()
            .enableAccount(testAccount.getName(), testAccount.getDomainId());
         assertNotNull(updated);
         assertEquals(updated.getState(), Account.State.ENABLED);

      } finally {
         if (testAccount != null) {
            globalAdminClient.getAccountClient().deleteAccount(testAccount.getId());
         }
      }
   }

}
