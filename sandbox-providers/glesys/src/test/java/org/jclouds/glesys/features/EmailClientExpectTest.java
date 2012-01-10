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

import com.google.common.collect.ImmutableSet;
import org.jclouds.glesys.GleSYSClient;
import org.jclouds.glesys.domain.Email;
import org.jclouds.glesys.domain.EmailOverview;
import org.jclouds.glesys.domain.EmailOverviewDomain;
import org.jclouds.glesys.domain.EmailOverviewSummary;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * Tests annotation parsing of {@code ArchiveAsyncClient}
 * 
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "EmailAsyncClientTest")
public class EmailClientExpectTest extends BaseGleSYSClientExpectTest<EmailClient> {
   public EmailClientExpectTest() {
      remoteServicePrefix = "email";
   }

   public void testList() throws Exception {
      EmailClient client =  createMock("list", "POST", 200, "/email_list.json", entry("domain", "test"));

      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
      Email.Builder builder = Email.builder().quota("200 MB").usedQuota("0 MB").antispamLevel(3).antiVirus(true).autoRespond(false).autoRespondSaveEmail(true).autoRespondMessage("false");
      Set<Email> expected =
            ImmutableSet.of(
                  builder.account("test@adamlowe.net").created(dateFormat.parse("2011-12-22T12:13:14")).modified(dateFormat.parse("2011-12-22T12:13:35")).build(),
                  builder.account("test2@adamlowe.net").created(dateFormat.parse("2011-12-22T12:14:29")).modified(dateFormat.parse("2011-12-22T12:14:31")).build()
            );
      assertEquals(client.listAccounts("test"), expected);

      // check not found response
      client = createMock("list", "POST", 404, "Domain not found", entry("domain", "test"));
      assertTrue(client.listAccounts("test").isEmpty());
   }

   public void testOverview() throws Exception {
      EmailClient client = createMock("overview", "POST", 200, "/email_overview.json");

      EmailOverviewSummary summary = EmailOverviewSummary.builder().accounts(2).maxAccounts(50).aliases(0).maxAliases(1000).build();
      EmailOverviewDomain domain = EmailOverviewDomain.builder().domain("adamlowe.net").accounts(2).aliases(0).build();
      EmailOverview expected = EmailOverview.builder().summary(summary).domains(domain).build();
      
      assertEquals(client.getEmailOverview(), expected);
      
      assertNull(createMock("overview", "POST", 404, "Not found").getEmailOverview());
   }

   public void testCreateAccount() throws Exception {
      createMock("createaccount", "POST", 200, null,
            entry("emailaccount", "test@jclouds.org"), entry("password", "newpass")).createAccount("test@jclouds.org", "newpass");
   }

   @Test(expectedExceptions = {ResourceNotFoundException.class})
   public void testCreateAccountDomainNotFound() throws Exception {
      createMock("createaccount", "POST", 404, null,
            entry("emailaccount", "test@jclouds.org"), entry("password", "newpass")).createAccount("test@jclouds.org", "newpass");
   }

   
   public void testCreateAlias() throws Exception {
      createMock("createalias", "POST", 200, null,
            entry("emailalias", "test2@jclouds.org"), entry("goto", "test@jclouds.org")).createAlias("test2@jclouds.org", "test@jclouds.org");
   }

   @Test(expectedExceptions = {ResourceNotFoundException.class})
   public void testCreateAliasNotFound() throws Exception {
      createMock("createalias", "POST", 404, null,
            entry("emailalias", "test2@jclouds.org"), entry("goto", "test@jclouds.org")).createAlias("test2@jclouds.org", "test@jclouds.org");
   }

   public void testEditAlias() throws Exception {
      createMock("editalias", "POST", 200, null,
            entry("emailalias", "test2@jclouds.org"), entry("goto", "test1@jclouds.org")).editAlias("test2@jclouds.org", "test1@jclouds.org");
   }
   
   @Test(expectedExceptions = {ResourceNotFoundException.class})
   public void testEditAliasNotFound() throws Exception {
      createMock("editalias", "POST", 404, null,
            entry("emailalias", "test2@jclouds.org"), entry("goto", "test1@jclouds.org")).editAlias("test2@jclouds.org", "test1@jclouds.org");
   }

   @Override
   protected EmailClient getClient(GleSYSClient gleSYSClient) {
      return gleSYSClient.getEmailClient();
   }
}
