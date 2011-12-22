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
import org.jclouds.glesys.options.EmailEditOptions;
import org.jclouds.predicates.RetryablePredicate;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.*;

/**
* Tests behavior of {@code EmailClient}
*
* @author Adam Lowe
*/
@Test(groups = "live", testName = "EmailClientLiveTest")
public class EmailClientLiveTest extends BaseGleSYSClientLiveTest {

   @BeforeGroups(groups = {"live"})
   public void setupClient() {
      super.setupClient();
      client = context.getApi().getEmailClient();

      try {
         client.delete("test@" + testDomain);
         context.getApi().getDomainClient().deleteDomain(testDomain);

      } catch(Exception e) {
      }

      serverId = createServer("test-email-jclouds").getServerId();
      createDomain(testDomain);

      domainCounter = new RetryablePredicate<Integer>(
            new Predicate<Integer>() {
               public boolean apply(Integer value) {
                  return client.listAccounts(testDomain).size() == value;
               }
            }, 30, 1, TimeUnit.SECONDS);
      
   }
   

   @AfterGroups(groups = {"live"})
   public void tearDown() {
      client.delete("test@" + testDomain);
      context.getApi().getDomainClient().deleteDomain(testDomain);
      context.getApi().getServerClient().destroyServer(serverId, 0);
      super.tearDown();
   }
   
   private EmailClient client;
   private String serverId;
   private final String testDomain = "email-test.jclouds.org";
   private RetryablePredicate<Integer> domainCounter;
   
   @Test
   public void createEmail() {
      int before = client.listAccounts(testDomain).size();
      client.createAccount("test@" + testDomain, "password");
      assertTrue(domainCounter.apply(before + 1));
   }

   @Test(dependsOnMethods = "createEmail")
   public void testOverview() throws Exception {
      EmailOverview overview = client.emailOverview();
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
      client.editAccount("test@" + testDomain, EmailEditOptions.Builder.antiVirus(false));
      Set<Email> accounts = client.listAccounts(testDomain);
      for(Email account : accounts) {
         if (account.getAccount().equals("test@" + testDomain)) {
            assertFalse(account.getAntiVirus());
         }
      }
   }
}
