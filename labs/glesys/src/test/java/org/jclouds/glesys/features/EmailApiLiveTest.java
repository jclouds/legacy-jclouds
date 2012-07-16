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
import org.jclouds.glesys.internal.BaseGleSYSApiWithAServerLiveTest;
import org.jclouds.glesys.options.CreateAccountOptions;
import org.jclouds.glesys.options.EditAccountOptions;
import org.jclouds.predicates.RetryablePredicate;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code EmailApi}
 *
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "EmailApiLiveTest", singleThreaded = true)
public class EmailApiLiveTest extends BaseGleSYSApiWithAServerLiveTest {

   @BeforeGroups(groups = {"live"})
   public void setupDomains() {
      testDomain = identity + ".test.jclouds.org";
      api = gleContext.getApi().getEmailApi();

      createDomain(testDomain);

      emailAccountCounter = new RetryablePredicate<Integer>(
            new Predicate<Integer>() {
               public boolean apply(Integer value) {
                  return api.listAccounts(testDomain).size() == value;
               }
            }, 180, 5, TimeUnit.SECONDS);

      assertTrue(emailAccountCounter.apply(0));
      
      try {
         api.delete("test2@" + testDomain);
      } catch(Exception e) {
      }
   }

   @AfterGroups(groups = {"live"})
   public void tearDownDomains() {
      api.delete("test@" + testDomain);
      api.delete("test1@" + testDomain);
      assertTrue(emailAccountCounter.apply(0));
      gleContext.getApi().getDomainApi().deleteDomain(testDomain);
   }

   private EmailApi api;
   private String testDomain;
   private RetryablePredicate<Integer> emailAccountCounter;

   @Test
   public void testCreateEmail() {
      api.createAccount("test@" + testDomain, "password",
            CreateAccountOptions.Builder.antiVirus(true).autorespond(true).autorespondMessage("out of office"));

      assertTrue(emailAccountCounter.apply(1));

      api.createAccount("test1@" + testDomain, "password");

      assertTrue(emailAccountCounter.apply(2));
   }

   @Test(dependsOnMethods = "testCreateEmail")
   public void testAliases() {
      assertTrue(api.listAliases(testDomain).isEmpty());

      EmailAlias alias = api.createAlias("test2@" + testDomain, "test@" + testDomain);
      assertEquals(alias.getAlias(), "test2@" + testDomain);
      assertEquals(alias.getForwardTo(), "test@" + testDomain);

      EmailAlias aliasFromList = Iterables.getOnlyElement(api.listAliases(testDomain));
      assertEquals(aliasFromList, alias);
      
      EmailOverview overview = api.getEmailOverview();
      assertTrue(overview.getSummary().getAliases() == 1);

      alias = api.editAlias("test2@" + testDomain, "test1@" + testDomain);
      overview = api.getEmailOverview();
      assertTrue(overview.getSummary().getAliases() == 1);
      
      aliasFromList = Iterables.getOnlyElement(api.listAliases(testDomain));
      assertEquals(aliasFromList, alias);

      api.delete("test2@" + testDomain);
      overview = api.getEmailOverview();
      assertTrue(overview.getSummary().getAliases() == 0);
   }

   @Test(dependsOnMethods = "testCreateEmail")
   public void testOverview() throws Exception {
      EmailOverview overview = api.getEmailOverview();
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
      Set<EmailAccount> accounts = api.listAccounts(testDomain);
      assertTrue(accounts.size() >= 1);
   }

   @Test(dependsOnMethods = "testCreateEmail")
   public void testEditAccount() throws Exception {
      Set<EmailAccount> accounts = api.listAccounts(testDomain);
      for (EmailAccount account : accounts) {
         if (account.getAccount().equals("test@" + testDomain)) {
            assertTrue(account.isAntiVirus());
         }
      }

      api.editAccount("test@" + testDomain, EditAccountOptions.Builder.antiVirus(false));

      accounts = api.listAccounts(testDomain);
      for (EmailAccount account : accounts) {
         if (account.getAccount().equals("test@" + testDomain)) {
            assertFalse(account.isAntiVirus());
         }
      }
   }
}
