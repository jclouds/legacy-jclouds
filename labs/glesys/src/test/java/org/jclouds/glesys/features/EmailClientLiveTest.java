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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.glesys.domain.EmailAccount;
import org.jclouds.glesys.domain.EmailAlias;
import org.jclouds.glesys.domain.EmailOverview;
import org.jclouds.glesys.domain.EmailOverviewDomain;
import org.jclouds.glesys.internal.BaseGleSYSClientWithAServerLiveTest;
import org.jclouds.glesys.options.CreateAccountOptions;
import org.jclouds.glesys.options.EditAccountOptions;
import org.jclouds.predicates.RetryablePredicate;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code EmailClient}
 *
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "EmailClientLiveTest", singleThreaded = true)
public class EmailClientLiveTest extends BaseGleSYSClientWithAServerLiveTest {

   @BeforeGroups(groups = {"live"})
   public void setupDomains() {
      testDomain = identity + ".test.jclouds.org";
      client = gleContext.getApi().getEmailClient();

      createDomain(testDomain);

      emailAccountCounter = new RetryablePredicate<Integer>(
            new Predicate<Integer>() {
               public boolean apply(Integer value) {
                  return client.listAccounts(testDomain).size() == value;
               }
            }, 180, 5, TimeUnit.SECONDS);

      assertTrue(emailAccountCounter.apply(0));
      
      try {
         client.delete("test2@" + testDomain);
      } catch(Exception e) {
      }
   }

   @AfterGroups(groups = {"live"})
   public void tearDownDomains() {
      client.delete("test@" + testDomain);
      client.delete("test1@" + testDomain);
      assertTrue(emailAccountCounter.apply(0));
      gleContext.getApi().getDomainClient().deleteDomain(testDomain);
   }

   private EmailClient client;
   private String testDomain;
   private RetryablePredicate<Integer> emailAccountCounter;

   @Test
   public void testCreateEmail() {
      client.createAccount("test@" + testDomain, "password",
            CreateAccountOptions.Builder.antiVirus(true).autorespond(true).autorespondMessage("out of office"));

      assertTrue(emailAccountCounter.apply(1));

      client.createAccount("test1@" + testDomain, "password");

      assertTrue(emailAccountCounter.apply(2));
   }

   @Test(dependsOnMethods = "testCreateEmail")
   public void testAliases() {
      assertTrue(client.listAliases(testDomain).isEmpty());

      EmailAlias alias = client.createAlias("test2@" + testDomain, "test@" + testDomain);
      assertEquals(alias.getAlias(), "test2@" + testDomain);
      assertEquals(alias.getForwardTo(), "test@" + testDomain);

      EmailAlias aliasFromList = Iterables.getOnlyElement(client.listAliases(testDomain));
      assertEquals(aliasFromList, alias);
      
      EmailOverview overview = client.getEmailOverview();
      assertTrue(overview.getSummary().getAliases() == 1);

      alias = client.editAlias("test2@" + testDomain, "test1@" + testDomain);
      overview = client.getEmailOverview();
      assertTrue(overview.getSummary().getAliases() == 1);
      
      aliasFromList = Iterables.getOnlyElement(client.listAliases(testDomain));
      assertEquals(aliasFromList, alias);

      client.delete("test2@" + testDomain);
      overview = client.getEmailOverview();
      assertTrue(overview.getSummary().getAliases() == 0);
   }

   @Test(dependsOnMethods = "testCreateEmail")
   public void testOverview() throws Exception {
      EmailOverview overview = client.getEmailOverview();
      assertNotNull(overview.getSummary());
      assertTrue(overview.getSummary().getAccounts() > 0);
      assertTrue(overview.getSummary().getAliases() > -1);
      assertTrue(overview.getSummary().getMaxAccounts() > 0);
      assertTrue(overview.getSummary().getMaxAliases() > 0);
      assertNotNull(overview.getDomains());
      assertFalse(overview.getDomains().isEmpty());

      EmailOverviewDomain domain = EmailOverviewDomain.builder().domain(testDomain).accounts(1).build();
      assertTrue(overview.getDomains().contains(domain));
   }

   @Test(dependsOnMethods = "testCreateEmail")
   public void testListAccounts() throws Exception {
      Set<EmailAccount> accounts = client.listAccounts(testDomain);
      assertTrue(accounts.size() >= 1);
   }

   @Test(dependsOnMethods = "testCreateEmail")
   public void testEditAccount() throws Exception {
      Set<EmailAccount> accounts = client.listAccounts(testDomain);
      for (EmailAccount account : accounts) {
         if (account.getAccount().equals("test@" + testDomain)) {
            assertTrue(account.isAntiVirus());
         }
      }

      client.editAccount("test@" + testDomain, EditAccountOptions.Builder.antiVirus(false));

      accounts = client.listAccounts(testDomain);
      for (EmailAccount account : accounts) {
         if (account.getAccount().equals("test@" + testDomain)) {
            assertFalse(account.isAntiVirus());
         }
      }
   }
}
