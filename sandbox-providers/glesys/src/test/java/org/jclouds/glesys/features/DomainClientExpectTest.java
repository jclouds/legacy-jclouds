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

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import org.jclouds.glesys.GleSYSClient;
import org.jclouds.glesys.domain.Domain;
import org.jclouds.glesys.domain.DomainRecord;
import org.jclouds.glesys.options.AddDomainOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.BaseRestClientExpectTest;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Set;

import static org.jclouds.io.Payloads.newUrlEncodedFormPayload;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Tests annotation parsing of {@code DomainAsyncClient}
 *
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "DomainClientExpectTest")
public class DomainClientExpectTest extends BaseRestClientExpectTest<GleSYSClient> {
   public DomainClientExpectTest() {
      provider = "glesys";
   }

   public void testListDomainsWhenResponseIs2xx() throws Exception {
      DomainClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/domain/list/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/domain_list.json")).build()).getDomainClient();

      Set<Domain> expected = ImmutableSet.of(
            Domain.builder().domain("adamlowe.net").createTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2011-12-20 10:58:51")).build());

      assertEquals(client.listDomains(), expected);
   }

   public void testListDomainsWhenResponseIs4xxReturnsEmpty() throws Exception {
      DomainClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/domain/list/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .build(),
            HttpResponse.builder().statusCode(404).build()).getDomainClient();

      assertTrue(client.listDomains().isEmpty());
   }

   public void testListDomainRecordsWhenResponseIs2xx() throws Exception {
      DomainClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/domain/list_records/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("domain", "adamlowe.net").build())).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/domain_list_records.json")).build()).getDomainClient();

      Set<DomainRecord> expected = ImmutableSortedSet.of(
            DomainRecord.builder().id("213227").zone("adamlowe.net").host("@").type("NS").data("ns1.namesystem.se.").ttl(3600).build(),
            DomainRecord.builder().id("213228").zone("adamlowe.net").host("@").type("NS").data("ns2.namesystem.se.").ttl(3600).build(),
            DomainRecord.builder().id("213229").zone("adamlowe.net").host("@").type("NS").data("ns3.namesystem.se.").ttl(3600).build(),
            DomainRecord.builder().id("213230").zone("adamlowe.net").host("@").type("A").data("127.0.0.1").ttl(3600).build(),
            DomainRecord.builder().id("213231").zone("adamlowe.net").host("www").type("A").data("127.0.0.1").ttl(3600).build(),
            DomainRecord.builder().id("213232").zone("adamlowe.net").host("mail").type("A").data("79.99.4.40").ttl(3600).build(),
            DomainRecord.builder().id("213233").zone("adamlowe.net").host("@").type("MX").data("mx01.glesys.se.").ttl(3600).build(),
            DomainRecord.builder().id("213234").zone("adamlowe.net").host("@").type("MX").data("mx02.glesys.se.").ttl(3600).build(),
            DomainRecord.builder().id("213235").zone("adamlowe.net").host("@").type("TXT").data("v=spf1 include:spf.glesys.se -all").ttl(3600).build()
      );
      assertEquals(client.listRecords("adamlowe.net"), expected);
   }


   public void testListDomainRecordsWhenResponseIs4xxReturnsEmpty() throws Exception {
      DomainClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/domain/list/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .build(),
            HttpResponse.builder().statusCode(404).build()).getDomainClient();

      assertTrue(client.listDomains().isEmpty());
   }

   public void testAddDomainWhenResponseIs2xx() throws Exception {
      DomainClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/domain/add/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder().put(
                        "Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("name", "cl66666_x").build())).build(),
            HttpResponse.builder().statusCode(200).build()).getDomainClient();

      client.addDomain("cl66666_x");
   }


   public void testAddDomainWithOptsWhenResponseIs2xx() throws Exception {
      DomainClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/domain/add/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder().put(
                        "Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("name", "cl66666_x")
                        .put("primary_ns", "ns1.somewhere.x")
                        .put("expire", "1")
                        .put("minimum", "1")
                        .put("refresh", "1")
                        .put("resp_person", "Tester.")
                        .put("retry", "1")
                        .put("ttl", "1")
                        .build())).build(),
            HttpResponse.builder().statusCode(200).build()).getDomainClient();
      AddDomainOptions options = (AddDomainOptions) AddDomainOptions.Builder.primaryNameServer("ns1.somewhere.x")
            .expire(1).minimum(1).refresh(1).responsiblePerson("Tester").retry(1).ttl(1);

      client.addDomain("cl66666_x", options);
   }

   public void testEditDomainWhenResponseIs2xx() throws Exception {
      DomainClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/domain/edit/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder().put(
                        "Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("domain", "x").build())).build(),
            HttpResponse.builder().statusCode(200).build()).getDomainClient();

      client.editDomain("x");
   }

   @Test(expectedExceptions = {ResourceNotFoundException.class})
   public void testEditDomainWhenResponseIs4xxThrows() throws Exception {
      DomainClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/domain/edit/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder().put(
                        "Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("domain", "x").build())).build(),
            HttpResponse.builder().statusCode(404).build()).getDomainClient();

      client.editDomain("x");
   }

   public void testDeleteDomainWhenResponseIs2xx() throws Exception {
      DomainClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/domain/delete/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder().put(
                        "Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("domain", "x").build())).build(),
            HttpResponse.builder().statusCode(200).build()).getDomainClient();

      client.deleteDomain("x");
   }

   @Test(expectedExceptions = {ResourceNotFoundException.class})
   public void testDeleteDomainWhenResponseIs4xxThrows() throws Exception {
      DomainClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/domain/delete/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder().put(
                        "Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("domain", "x").build())).build(),
            HttpResponse.builder().statusCode(404).build()).getDomainClient();

      client.deleteDomain("x");
   }
}
