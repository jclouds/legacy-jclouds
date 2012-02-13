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
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Set;

import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.glesys.GleSYSClient;
import org.jclouds.glesys.domain.Domain;
import org.jclouds.glesys.domain.DomainRecord;
import org.jclouds.glesys.options.AddDomainOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.BaseRestClientExpectTest;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;

/**
 * Tests annotation parsing of {@code DomainAsyncClient}
 *
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "DomainClientExpectTest")
public class DomainClientExpectTest extends BaseRestClientExpectTest<GleSYSClient> {
   private DateService dateService = new SimpleDateFormatDateService();
   
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

      Domain expected =
            Domain.builder().domainName("testglesys.jclouds.org").createTime(dateService.iso8601SecondsDateParse("2012-01-31T12:19:03+01:00")).build();

      Domain actual = Iterables.getOnlyElement(client.listDomains());
      assertEquals(expected.getDomainName(), actual.getDomainName());
      assertEquals(expected.getCreateTime(), actual.getCreateTime());
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
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/domain/listrecords/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("domainname", "testglesys.jclouds.org").build())).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/domain_list_records.json")).build()).getDomainClient();

      Set<DomainRecord> expected = ImmutableSortedSet.of(
            DomainRecord.builder().id("224538").domainname("testglesys.jclouds.org").host("@").type("NS").data("ns1.namesystem.se.").ttl(3600).build(),
            DomainRecord.builder().id("224539").domainname("testglesys.jclouds.org").host("@").type("NS").data("ns2.namesystem.se.").ttl(3600).build(),
            DomainRecord.builder().id("224540").domainname("testglesys.jclouds.org").host("@").type("NS").data("ns3.namesystem.se.").ttl(3600).build(),
            DomainRecord.builder().id("224541").domainname("testglesys.jclouds.org").host("@").type("A").data("127.0.0.1").ttl(3600).build(),
            DomainRecord.builder().id("224542").domainname("testglesys.jclouds.org").host("www").type("A").data("127.0.0.1").ttl(3600).build(),
            DomainRecord.builder().id("224543").domainname("testglesys.jclouds.org").host("mail").type("A").data("79.99.4.40").ttl(3600).build(),
            DomainRecord.builder().id("224544").domainname("testglesys.jclouds.org").host("@").type("MX").data("10 mx01.glesys.se.").ttl(3600).build(),
            DomainRecord.builder().id("224545").domainname("testglesys.jclouds.org").host("@").type("MX").data("20 mx02.glesys.se.").ttl(3600).build(),
            DomainRecord.builder().id("224546").domainname("testglesys.jclouds.org").host("@").type("TXT").data("v=spf1 include:spf.glesys.se -all").ttl(3600).build()
      );
      
      Set<DomainRecord> actual = client.listRecords("testglesys.jclouds.org");
      
      assertEquals(actual, expected);

      for(DomainRecord result : actual) {
         for(DomainRecord expect : expected) {
            if (result.equals(expect)) {
               assertEquals(result.toString(), expect.toString(), "Deep comparison using toString() failed!");
            }
         }
      }
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
                        .put("domainname", "cl66666_x").build())).build(),
            HttpResponse.builder().statusCode(200).build()).getDomainClient();

      client.addDomain("cl66666_x");
   }


   public void testAddDomainWithOptsWhenResponseIs2xx() throws Exception {
      DomainClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/domain/add/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder().put(
                        "Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("domainname", "cl66666_x")
                        .put("primarynameserver", "ns1.somewhere.x")
                        .put("expire", "1")
                        .put("minimum", "1")
                        .put("refresh", "1")
                        .put("responsibleperson", "Tester.")
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
                        .put("domainname", "x").build())).build(),
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
                        .put("domainname", "x").build())).build(),
            HttpResponse.builder().statusCode(404).build()).getDomainClient();

      client.editDomain("x");
   }

   public void testDeleteDomainWhenResponseIs2xx() throws Exception {
      DomainClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/domain/delete/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder().put(
                        "Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("domainname", "x").build())).build(),
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
                        .put("domainname", "x").build())).build(),
            HttpResponse.builder().statusCode(404).build()).getDomainClient();

      client.deleteDomain("x");
   }
}
