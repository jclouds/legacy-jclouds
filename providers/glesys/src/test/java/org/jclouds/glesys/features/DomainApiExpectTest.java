/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.glesys.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.jclouds.glesys.domain.Domain;
import org.jclouds.glesys.domain.DomainRecord;
import org.jclouds.glesys.internal.BaseGleSYSApiExpectTest;
import org.jclouds.glesys.options.AddDomainOptions;
import org.jclouds.glesys.options.DomainOptions;
import org.jclouds.glesys.options.UpdateRecordOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Tests annotation parsing of {@code DomainAsyncApi}
 *
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "DomainApiExpectTest")
public class DomainApiExpectTest extends BaseGleSYSApiExpectTest {
   
   public void testListDomainsWhenResponseIs2xx() throws Exception {
      DomainApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/domain/list/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/domain_list.json")).build()).getDomainApi();

      Domain expected =
            Domain.builder().domainName("testglesys.jclouds.org").createTime(dateService.iso8601SecondsDateParse("2012-01-31T12:19:03+01:00")).build();

      Domain actual = Iterables.getOnlyElement(api.list());
      assertEquals(expected.getName(), actual.getName());
      assertEquals(expected.getCreateTime(), actual.getCreateTime());
   }

   public void testListDomainsWhenResponseIs4xxReturnsEmpty() throws Exception {
      DomainApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/domain/list/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build(),
            HttpResponse.builder().statusCode(404).build()).getDomainApi();

      assertTrue(api.list().isEmpty());
   }

   public void testListDomainRecordsWhenResponseIs2xx() throws Exception {
      DomainApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/domain/listrecords/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("domainname", "testglesys.jclouds.org").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/domain_list_records.json")).build()).getDomainApi();

      Set<DomainRecord> expected = ImmutableSet.of(
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

      Set<DomainRecord> actual = api.listRecords("testglesys.jclouds.org");

      assertEquals(actual, expected);

      for (DomainRecord result : actual) {
         for (DomainRecord expect : expected) {
            if (result.equals(expect)) {
               assertEquals(result.toString(), expect.toString(), "Deep comparison using toString() failed!");
            }
         }
      }
   }

   public void testListDomainRecordsWhenResponseIs4xxReturnsEmpty() throws Exception {
      DomainApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/domain/list/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build(),
            HttpResponse.builder().statusCode(404).build()).getDomainApi();

      assertTrue(api.list().isEmpty());
   }

   public void testAddDomainRecordsWhenResponseIs2xx() throws Exception {
      DomainApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/domain/addrecord/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("domainname", "jclouds.org")
                       .addFormParam("host", "jclouds.org")
                       .addFormParam("type", "A")
                       .addFormParam("data", "").build(),
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromResourceWithContentType("/domain_record.json", MediaType.APPLICATION_JSON)).build())
            .getDomainApi();

      assertEquals(api.createRecord("jclouds.org", "jclouds.org", "A", ""), recordInDomainRecord());
   }

   protected DomainRecord recordInDomainRecord() {
      return DomainRecord.builder().id("256151").domainname("cl13016-domain.jclouds.org").host("test").type("A").data("127.0.0.1").ttl(3600).build();
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testAddDomainRecordsWhenResponseIs4xx() throws Exception {
      DomainApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/domain/addrecord/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("domainname", "jclouds.org")
                       .addFormParam("host", "jclouds.org")
                       .addFormParam("type", "A")
                       .addFormParam("data", "").build(),
            HttpResponse.builder().statusCode(404).build()).getDomainApi();

      api.createRecord("jclouds.org", "jclouds.org", "A", "");
   }

   public void testUpdateDomainRecordsWhenResponseIs2xx() throws Exception {
      DomainApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/domain/updaterecord/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("recordid", "256151")
                       .addFormParam("host", "somehost")
                       .addFormParam("ttl", "1800").build(),
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromResourceWithContentType("/domain_record.json", MediaType.APPLICATION_JSON)).build())
            .getDomainApi();

      assertEquals(api.updateRecord("256151", UpdateRecordOptions.Builder.host("somehost").ttl(1800)), recordInDomainRecord());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testUpdateDomainRecordsWhenResponseIs4xx() throws Exception {
      DomainApi api = requestSendsResponse(
               HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/domain/updaterecord/format/json")
                          .addHeader("Accept", "application/json")
                          .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                          .addFormParam("recordid", "256151")
                          .addFormParam("host", "somehost")
                          .addFormParam("ttl", "1800").build(),
            HttpResponse.builder().statusCode(404).build()).getDomainApi();

      api.updateRecord("256151", UpdateRecordOptions.Builder.host("somehost").ttl(1800));
   }

   public void testDeleteDomainRecordsWhenResponseIs2xx() throws Exception {
      DomainApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/domain/deleterecord/format/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("recordid", "256151").build(),
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromResourceWithContentType("/domain_record.json", MediaType.APPLICATION_JSON)).build())
            .getDomainApi();

      api.deleteRecord("256151");
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testDeleteDomainRecordsWhenResponseIs4xx() throws Exception {
      DomainApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/domain/deleterecord/format/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("recordid", "256151").build(),
            HttpResponse.builder().statusCode(404).build()).getDomainApi();

      api.deleteRecord("256151");
   }

   public void testGetDomainWhenResponseIs2xx() throws Exception {
      DomainApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/domain/details/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("domainname", "cl66666_x").build(),
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromResourceWithContentType("/domain_details.json", MediaType.APPLICATION_JSON)).build())
            .getDomainApi();

      assertEquals(api.get("cl66666_x"), domainInDomainDetails());
   }


   public void testGetDomainWhenResponseIs4xx() throws Exception {
      DomainApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/domain/details/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("domainname", "cl66666_x").build(),
            HttpResponse.builder().statusCode(404).build())
            .getDomainApi();

      assertNull(api.get("cl66666_x"));
   }

   protected Domain domainInDomainDetails() {
      return Domain.builder().domainName("cl13016-domain.jclouds.org").createTime(dateService.iso8601SecondsDateParse("2012-06-24T11:52:49+02:00")).recordCount(9).build();
   }

   public void testAddDomainWhenResponseIs2xx() throws Exception {
      DomainApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/domain/add/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("domainname", "cl66666_x").build(),
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromResourceWithContentType("/domain_details.json", MediaType.APPLICATION_JSON)).build())
            .getDomainApi();

      assertEquals(api.create("cl66666_x"), domainInDomainDetails());
   }

   public void testAddDomainWithOptsWhenResponseIs2xx() throws Exception {
      DomainApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/domain/add/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("domainname", "cl66666_x")
                       .addFormParam("primarynameserver", "ns1.somewhere.x")
                       .addFormParam("expire", "1")
                       .addFormParam("minimum", "1")
                       .addFormParam("refresh", "1")
                       .addFormParam("responsibleperson", "Tester.")
                       .addFormParam("retry", "1")
                       .addFormParam("ttl", "1").build(),
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromResourceWithContentType("/domain_details.json", MediaType.APPLICATION_JSON)).build())
            .getDomainApi();
      AddDomainOptions options = (AddDomainOptions) AddDomainOptions.Builder.primaryNameServer("ns1.somewhere.x")
            .expire(1).minimum(1).refresh(1).responsiblePerson("Tester").retry(1).ttl(1);

      assertEquals(api.create("cl66666_x", options), domainInDomainDetails());
   }

   public void testUpdateDomainWhenResponseIs2xx() throws Exception {
      DomainApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/domain/edit/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("domainname", "x")
                       .addFormParam("expire", "1").build(),
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromResourceWithContentType("/domain_details.json", MediaType.APPLICATION_JSON)).build())
            .getDomainApi();

      assertEquals(api.update("x", DomainOptions.Builder.expire(1)), domainInDomainDetails());
   }

   @Test(expectedExceptions = {ResourceNotFoundException.class})
   public void testUpdateDomainWhenResponseIs4xxThrows() throws Exception {
      DomainApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/domain/edit/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("domainname", "x")
                       .addFormParam("expire", "1").build(),
            HttpResponse.builder().statusCode(404).build()).getDomainApi();

      api.update("x", DomainOptions.Builder.expire(1));
   }

   public void testDeleteDomainWhenResponseIs2xx() throws Exception {
      DomainApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/domain/delete/format/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("domainname", "x").build(),
            HttpResponse.builder().statusCode(200).build()).getDomainApi();

      api.delete("x");
   }

   @Test(expectedExceptions = {ResourceNotFoundException.class})
   public void testDeleteDomainWhenResponseIs4xxThrows() throws Exception {
      DomainApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/domain/delete/format/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("domainname", "x").build(),
            HttpResponse.builder().statusCode(404).build()).getDomainApi();

      api.delete("x");
   }
}
