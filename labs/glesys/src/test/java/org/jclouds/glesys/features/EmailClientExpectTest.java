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
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.jclouds.glesys.domain.EmailAccount;
import org.jclouds.glesys.domain.EmailAlias;
import org.jclouds.glesys.domain.EmailOverview;
import org.jclouds.glesys.domain.EmailOverviewDomain;
import org.jclouds.glesys.domain.EmailOverviewSummary;
import org.jclouds.glesys.domain.EmailQuota;
import org.jclouds.glesys.internal.BaseGleSYSClientExpectTest;
import org.jclouds.glesys.options.EditAccountOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Tests annotation parsing of {@code EmailClient}
 *
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "EmailAsyncClientTest")
public class EmailClientExpectTest extends BaseGleSYSClientExpectTest {

   public void testListWhenResponseIs2xx() throws Exception {
      EmailClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/email/list/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("domainname", "cl13016.test.jclouds.org").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/email_list.json")).build()).getEmailClient();

      EmailAccount.Builder<?> builder = EmailAccount.builder().quota(EmailQuota.builder().max(200).unit("MB").build()).antispamLevel(3).antiVirus(true).autoRespond(false).autoRespondSaveEmail(true);
      Set<EmailAccount> expected =
            ImmutableSet.of(
                  builder.account("test1@cl13016.test.jclouds.org").antispamLevel(3)
                        .created(dateService.iso8601SecondsDateParse("2012-06-24T11:53:45+02:00")).build(),
                  builder.account("test@cl13016.test.jclouds.org").antispamLevel(3)
                        .created(dateService.iso8601SecondsDateParse("2012-06-21T11:26:09+02:00"))
                        .modified(dateService.iso8601SecondsDateParse("2012-06-24T11:53:48+02:00")).build()
            );

      Set<EmailAccount> actual = client.listAccounts("cl13016.test.jclouds.org");
      assertEquals(actual, expected);
      assertEquals(Iterables.get(actual, 0).toString(), Iterables.get(expected, 0).toString());
   }

   public void testListWhenResponseIs404IsEmpty() throws Exception {
      EmailClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/email/list/format/json")
                  .addHeader("Accept", "application/json")
                  .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                  .addFormParam("domainname", "test").build(),
            HttpResponse.builder().statusCode(404).build()).getEmailClient();

      assertTrue(client.listAccounts("test").isEmpty());
   }

   public void testListAliasesWhenResponseIs2xx() throws Exception {
      EmailClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/email/list/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("domainname", "cl13016.test.jclouds.org").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/email_list.json")).build()).getEmailClient();

      EmailAlias expected = EmailAlias.builder().alias("test2@cl13016.test.jclouds.org").forwardTo("test2@cl13016.test.jclouds.org").build();
      EmailAlias actual = Iterables.getOnlyElement(client.listAliases("cl13016.test.jclouds.org"));
      assertEquals(actual, expected);
   }

   public void testListAliasesWhenResponseIs404IsEmpty() throws Exception {
      EmailClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/email/list/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("domainname", "test").build(),
            HttpResponse.builder().statusCode(404).build()).getEmailClient();

      assertTrue(client.listAliases("test").isEmpty());
   }

   public void testOverviewWhenResponseIs2xx() throws Exception {
      EmailClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/email/overview/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/email_overview.json")).build()).getEmailClient();

      EmailOverviewSummary summary = EmailOverviewSummary.builder().accounts(2).maxAccounts(50).aliases(1).maxAliases(1000).build();
      EmailOverviewDomain domain = EmailOverviewDomain.builder().domain("cl13016.test.jclouds.org").accounts(2).aliases(0).build();
      EmailOverview expected = EmailOverview.builder().summary(summary).domains(domain).build();

      assertEquals(client.getEmailOverview(), expected);
   }

   public void testOverviewWhenResponseIs404ReturnsNull() throws Exception {
      EmailClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/email/overview/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build(),
            HttpResponse.builder().statusCode(404).build()).getEmailClient();

      assertNull(client.getEmailOverview());
   }

   public void testCreateAccountWhenResponseIs2xx() throws Exception {
      EmailClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/email/createaccount/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("emailaccount", "test@jclouds.org")
                       .addFormParam("password", "newpass").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResourceWithContentType("/email_details.json", MediaType.APPLICATION_JSON)).build())
            .getEmailClient();

      assertEquals(client.createAccount("test@jclouds.org", "newpass").toString(), getEmailAccountInDetails().toString());
   }

   public void testEditAccountWhenResponseIs2xx() throws Exception {
      EmailClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/email/editaccount/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("emailaccount", "test@jclouds.org")
                       .addFormParam("password", "anotherpass").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResourceWithContentType("/email_details.json", MediaType.APPLICATION_JSON)).build())
            .getEmailClient();

      assertEquals(client.editAccount("test@jclouds.org", EditAccountOptions.Builder.password("anotherpass")).toString(), getEmailAccountInDetails().toString());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testEditAccountWhenResponseIs4xx() throws Exception {
      EmailClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/email/editaccount/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("emailaccount", "test@jclouds.org")
                       .addFormParam("password", "anotherpass").build(),
            HttpResponse.builder().statusCode(404).build())
            .getEmailClient();

      assertEquals(client.editAccount("test@jclouds.org", EditAccountOptions.Builder.password("anotherpass")).toString(), getEmailAccountInDetails().toString());
   }

   private EmailAccount getEmailAccountInDetails() {
      return EmailAccount.builder().account("test@CL13016.jclouds.org")
            .antispamLevel(3)
            .antiVirus(true)
            .autoRespondSaveEmail(true)
            .created(dateService.iso8601SecondsDateParse("2012-06-20T12:01:01+02:00"))
            .quota(EmailQuota.builder().max(200).unit("MB").build()).build();
   }

   @Test(expectedExceptions = {ResourceNotFoundException.class})
   public void testCreateAccountWhenResponseIs4xxThrows() throws Exception {
      EmailClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/email/createaccount/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("emailaccount", "test@jclouds.org")
                       .addFormParam("password", "newpass").build(),
            HttpResponse.builder().statusCode(404).build()).getEmailClient();

      client.createAccount("test@jclouds.org", "newpass");
   }

   public void testCreateAliasWhenResponseIs2xx() throws Exception {
      EmailClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/email/createalias/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("emailalias", "test2@jclouds.org")
                       .addFormParam("goto", "test@jclouds.org").build(),
            HttpResponse.builder().statusCode(200).build()).getEmailClient();

      client.createAlias("test2@jclouds.org", "test@jclouds.org");
   }

   @Test(expectedExceptions = {AuthorizationException.class})
   public void testCreateAliasWhenResponseIs4xxThrows() throws Exception {
      EmailClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/email/createalias/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("emailalias", "test2@jclouds.org")
                       .addFormParam("goto", "test@jclouds.org").build(),
            HttpResponse.builder().statusCode(401).build()).getEmailClient();

      client.createAlias("test2@jclouds.org", "test@jclouds.org");
   }

   public void testEditAliasWhenResponseIs2xx() throws Exception {
      EmailClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/email/editalias/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("emailalias", "test2@jclouds.org")
                       .addFormParam("goto", "test@jclouds.org").build(),
            HttpResponse.builder().statusCode(200).build()).getEmailClient();

      client.editAlias("test2@jclouds.org", "test@jclouds.org");
   }

   @Test(expectedExceptions = {ResourceNotFoundException.class})
   public void testEditAliasWhenResponseIs4xxThrows() throws Exception {
      EmailClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/email/editalias/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("emailalias", "test2@jclouds.org")
                       .addFormParam("goto", "test@jclouds.org").build(),
            HttpResponse.builder().statusCode(404).build()).getEmailClient();

      client.editAlias("test2@jclouds.org", "test@jclouds.org");
   }

   public void testDeleteWhenResponseIs2xx() throws Exception {
      EmailClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/email/delete/format/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("email", "test2@jclouds.org").build(),
            HttpResponse.builder().statusCode(200).build()).getEmailClient();

      client.delete("test2@jclouds.org");
   }

   @Test(expectedExceptions = {ResourceNotFoundException.class})
   public void testDeleteWhenResponseIs4xxThrows() throws Exception {
      EmailClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/email/delete/format/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("email", "test2@jclouds.org").build(),
            HttpResponse.builder().statusCode(404).build()).getEmailClient();

      client.delete("test2@jclouds.org");
   }
}
