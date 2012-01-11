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
package org.jclouds.glesys.features;

import com.google.common.base.Predicate;
import org.jclouds.glesys.domain.*;
import org.jclouds.glesys.options.EmailCreateOptions;
import org.jclouds.glesys.options.EmailEditOptions;
import org.jclouds.glesys.options.ServerDestroyOptions;
import org.jclouds.predicates.RetryablePredicate;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.*;

/**
* Tests behavior of {@code EmailClient}
*
* @author Adam Lowe
*/
@Test(groups = "live", testName = "EmailClientLiveTest", singleThreaded = true)
public class EmailClientLiveTest extends BaseGleSYSClientLiveTest {

   @BeforeGroups(groups = {"live"})
   public void setupClient() {
      super.setupClient();
      client = context.getApi().getEmailClient();

      try {
         client.delete("test@" + testDomain);
         client.delete("test2@" + testDomain);
         context.getApi().getDomainClient().deleteDomain(testDomain);
      } catch(Exception e) {
      }

      serverId = createServer("test-email-jclouds").getServerId();
      
      createDomain(testDomain);

      emailAccountCounter = new RetryablePredicate<Integer>(
            new Predicate<Integer>() {
               public boolean apply(Integer value) {
                  return client.listAccounts(testDomain).size() == value;
               }
            }, 30, 1, TimeUnit.SECONDS);
      
   }
   

   @AfterGroups(groups = {"live"})
   public void tearDown() {
      client.delete("test@" + testDomain);
      assertTrue(emailAccountCounter.apply(0));
      context.getApi().getDomainClient().deleteDomain(testDomain);
      context.getApi().getServerClient().destroyServer(serverId, ServerDestroyOptions.Builder.discardIp());
      super.tearDown();
   }
   
   private EmailClient client;
   private String serverId;
   private final String testDomain = "email-test.jclouds.org";
   private RetryablePredicate<Integer> emailAccountCounter;
   
   @Test
   public void createEmail() {
      client.createAccount("test@" + testDomain, "password", EmailCreateOptions.Builder.antiVirus(true));
      assertTrue(emailAccountCounter.apply(1));
   }

   @Test(dependsOnMethods = "createEmail")
   public void createAlias() {
      client.createAlias("test2@" + testDomain, "test@" + testDomain);
      EmailOverview overview = client.getEmailOverview();
      assertTrue(overview.getSummary().getAliases() == 1);
      client.delete("test2@" + testDomain);
      overview = client.getEmailOverview();
      assertTrue(overview.getSummary().getAliases() == 0);
   }
   
   @Test(dependsOnMethods = "createEmail")
   public void testOverview() throws Exception {
      EmailOverview overview = client.getEmailOverview();
      assertNotNull(overview.getSummary());
      assertTrue(overview.getSummary().getAccounts() >= 1);
      assertTrue(overview.getSummary().getAliases() == 0);
      assertTrue(overview.getSummary().getMaxAccounts() > 0);
      assertTrue(overview.getSummary().getMaxAliases() > 0);
      assertNotNull(overview.getDomains());
      assertFalse(overview.getDomains().isEmpty());
      
      EmailOverviewDomain domain = EmailOverviewDomain.builder().domain(testDomain).accounts(1).build();
      assertTrue(overview.getDomains().contains(domain));
   }

   @Test(dependsOnMethods = "createEmail")
   public void testListAccounts() throws Exception {
      Set<Email> accounts = client.listAccounts(testDomain);
      assertTrue(accounts.size() >= 1);
   }

   @Test(dependsOnMethods = "createEmail")
   public void testEditAccount() throws Exception {
      Set<Email> accounts = client.listAccounts(testDomain);
      for(Email account : accounts) {
         if (account.getAccount().equals("test@" + testDomain)) {
            assertTrue(account.getAntiVirus());
         }
      }
      
      client.editAccount("test@" + testDomain, EmailEditOptions.Builder.antiVirus(false));
      
      accounts = client.listAccounts(testDomain);
      for(Email account : accounts) {
         if (account.getAccount().equals("test@" + testDomain)) {
            assertFalse(account.getAntiVirus());
         }
      }
   }
}
