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

import static org.jclouds.io.Payloads.newUrlEncodedFormPayload;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Set;

import org.jclouds.glesys.GleSYSClient;
import org.jclouds.glesys.domain.EmailAccount;
import org.jclouds.glesys.domain.EmailOverview;
import org.jclouds.glesys.domain.EmailOverviewDomain;
import org.jclouds.glesys.domain.EmailOverviewSummary;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.internal.BaseRestClientExpectTest;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * Tests annotation parsing of {@code EmailClient}
 *
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "EmailAsyncClientTest")
public class EmailClientExpectTest extends BaseRestClientExpectTest<GleSYSClient> {
   public EmailClientExpectTest() {
      provider = "glesys";
   }

   public void testListWhenResponseIs2xx() throws Exception {
      EmailClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/email/list/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(
                        ImmutableMultimap.<String, String>builder().put("domain", "test").build())).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/email_list.json")).build()).getEmailClient();

      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
      EmailAccount.Builder builder = EmailAccount.builder().quota("200 MB").usedQuota("0 MB").antispamLevel(3).antiVirus(true).autoRespond(false).autoRespondSaveEmail(true).autoRespondMessage("false");
      Set<EmailAccount> expected =
            ImmutableSet.of(
                  builder.account("test@adamlowe.net").created(dateFormat.parse("2011-12-22T12:13:14")).modified(dateFormat.parse("2011-12-22T12:13:35")).build(),
                  builder.account("test2@adamlowe.net").created(dateFormat.parse("2011-12-22T12:14:29")).modified(dateFormat.parse("2011-12-22T12:14:31")).build()
            );

      assertEquals(client.listAccounts("test"), expected);
   }

   public void testListWhenResponseIs404IsEmpty() throws Exception {
      EmailClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/email/list/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(
                        ImmutableMultimap.<String, String>builder().put("domain", "test").build())).build(),
            HttpResponse.builder().statusCode(404).build()).getEmailClient();

      assertTrue(client.listAccounts("test").isEmpty());
   }

   public void testOverviewWhenResponseIs2xx() throws Exception {
      EmailClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/email/overview/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/email_overview.json")).build()).getEmailClient();

      EmailOverviewSummary summary = EmailOverviewSummary.builder().accounts(2).maxAccounts(50).aliases(0).maxAliases(1000).build();
      EmailOverviewDomain domain = EmailOverviewDomain.builder().domain("adamlowe.net").accounts(2).aliases(0).build();
      EmailOverview expected = EmailOverview.builder().summary(summary).domains(domain).build();

      assertEquals(client.getEmailOverview(), expected);
   }

   public void testOverviewWhenResponseIs404ReturnsNull() throws Exception {
      EmailClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/email/overview/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .build(),
            HttpResponse.builder().statusCode(404).build()).getEmailClient();

      assertNull(client.getEmailOverview());
   }

   public void testCreateAccountWhenResponseIs2xx() throws Exception {
      EmailClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/email/createaccount/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("emailaccount", "test@jclouds.org")
                        .put("password", "newpass")
                        .build()))
                  .build(),
            HttpResponse.builder().statusCode(200).build()).getEmailClient();

      client.createAccount("test@jclouds.org", "newpass");
   }

   @Test(expectedExceptions = {ResourceNotFoundException.class})
   public void testCreateAccountWhenResponseIs4xxThrows() throws Exception {
      EmailClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/email/createaccount/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("emailaccount", "test@jclouds.org")
                        .put("password", "newpass")
                        .build()))
                  .build(),
            HttpResponse.builder().statusCode(404).build()).getEmailClient();

      client.createAccount("test@jclouds.org", "newpass");
   }

   public void testCreateAliasWhenResponseIs2xx() throws Exception {
      EmailClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/email/createalias/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("emailalias", "test2@jclouds.org")
                        .put("goto", "test@jclouds.org")
                        .build()))
                  .build(),
            HttpResponse.builder().statusCode(200).build()).getEmailClient();

      client.createAlias("test2@jclouds.org", "test@jclouds.org");
   }

   @Test(expectedExceptions = {AuthorizationException.class})
   public void testCreateAliasWhenResponseIs4xxThrows() throws Exception {
      EmailClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/email/createalias/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("emailalias", "test2@jclouds.org")
                        .put("goto", "test@jclouds.org")
                        .build()))
                  .build(),
            HttpResponse.builder().statusCode(401).build()).getEmailClient();

      client.createAlias("test2@jclouds.org", "test@jclouds.org");
   }

   public void testEditAliasWhenResponseIs2xx() throws Exception {
      EmailClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/email/editalias/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("emailalias", "test2@jclouds.org")
                        .put("goto", "test@jclouds.org")
                        .build()))
                  .build(),
            HttpResponse.builder().statusCode(200).build()).getEmailClient();

      client.editAlias("test2@jclouds.org", "test@jclouds.org");
   }

   @Test(expectedExceptions = {ResourceNotFoundException.class})
   public void testEditAliasWhenResponseIs4xxThrows() throws Exception {
      EmailClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/email/editalias/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("emailalias", "test2@jclouds.org")
                        .put("goto", "test@jclouds.org")
                        .build()))
                  .build(),
            HttpResponse.builder().statusCode(404).build()).getEmailClient();

      client.editAlias("test2@jclouds.org", "test@jclouds.org");
   }
}
