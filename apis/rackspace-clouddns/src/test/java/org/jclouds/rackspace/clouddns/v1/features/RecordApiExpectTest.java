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
package org.jclouds.rackspace.clouddns.v1.features;

import static javax.ws.rs.HttpMethod.POST;
import static javax.ws.rs.HttpMethod.PUT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpResponse;
import org.jclouds.rackspace.clouddns.v1.CloudDNSApi;
import org.jclouds.rackspace.clouddns.v1.domain.Job;
import org.jclouds.rackspace.clouddns.v1.domain.Record;
import org.jclouds.rackspace.clouddns.v1.domain.RecordDetail;
import org.jclouds.rackspace.clouddns.v1.internal.BaseCloudDNSApiExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * @author Everett Toews
 */
@Test(groups = "unit")
public class RecordApiExpectTest extends BaseCloudDNSApiExpectTest<CloudDNSApi> {
   private static final String JCLOUDS_EXAMPLE = "jclouds-example.com";

   public void testAddRecord() {
      URI endpoint = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/domains/3650908/records");
      RecordApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey, 
            responseWithAccess,
            authenticatedGET()
               .method(POST)
               .payload(payloadFromResource("/record-create.json"))
               .endpoint(endpoint)
               .build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/record-create-response.json")).build())
            .getRecordApiForDomain(3650908);

      Record createMXRecord = Record.builder()
            .type("MX")
            .name(JCLOUDS_EXAMPLE)
            .data("mail." + JCLOUDS_EXAMPLE)
            .comment("MX Record")
            .priority(11235)
            .build();
      
      Record createARecord = Record.builder()
            .type("A")
            .name(JCLOUDS_EXAMPLE)
            .data("10.0.0.1")
            .build();
      
      List<Record> createRecords = ImmutableList.of(createMXRecord, createARecord);      
      Job<Set<RecordDetail>> job = api.create(createRecords);
      
      assertEquals(job.getStatus(), Job.Status.COMPLETED);
      assertTrue(job.getResource().isPresent());
      
      Set<RecordDetail> records = job.getResource().get();
      Date now = new Date();
      RecordDetail mxRecord = null;
      RecordDetail aRecord = null;
      
      for (RecordDetail record: records) {
         if (record.getType().equals("MX")) {
            mxRecord = record;
         } else if (record.getType().equals("A")) {
            aRecord = record;
         }
      }
      
      assertNotNull(mxRecord.getId());
      assertEquals(mxRecord.getType(), "MX");
      assertEquals(mxRecord.getName(), JCLOUDS_EXAMPLE);
      assertEquals(mxRecord.getPriority().intValue(), 11235);
      assertEquals(mxRecord.getComment(), "MX Record");
      assertEquals(mxRecord.getTTL(), 60000);
      assertTrue(mxRecord.getCreated().before(now));
      assertTrue(mxRecord.getUpdated().before(now));
      
      assertNotNull(aRecord.getId());
      assertEquals(aRecord.getType(), "A");
      assertEquals(aRecord.getName(), JCLOUDS_EXAMPLE);
      assertNull(aRecord.getPriority());
      assertEquals(aRecord.getTTL(), 60000);
      assertTrue(aRecord.getCreated().before(now));
      assertTrue(aRecord.getUpdated().before(now));
   }

   public void testListRecords() {
      URI endpoint = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/domains/3650908/records");
      RecordApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey, 
            responseWithAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/record-list.json")).build())
            .getRecordApiForDomain(3650908);

      ImmutableList<RecordDetail> records = api.list().concat().toList();
      assertEquals(records.size(), 4); // 2 created above + 2 nameserver (NS) records
      
      for (RecordDetail record: records) {
         assertTrue(record.getName().contains(JCLOUDS_EXAMPLE));
      }
   }

   public void testListRecordsPagination() {
      URI endpointPage1 = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/domains/3650908/records");
      URI endpointPage2 = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/domains/3650908/records?limit=4&offset=4");
      RecordApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey, 
            responseWithAccess,
            authenticatedGET().endpoint(endpointPage1).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/record-list-page1.json")).build(),
            authenticatedGET().endpoint(endpointPage2).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/record-list-page2.json")).build())
         .getRecordApiForDomain(3650908);

      ImmutableList<RecordDetail> records = api.list().concat().toList();
      assertEquals(records.size(), 8);
      
      for (RecordDetail record: records) {
         assertTrue(record.getName().contains(JCLOUDS_EXAMPLE));
      }
   }

   public void testListByType() {
      URI endpoint = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/domains/3650908/records?type=A");
      RecordApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey, 
            responseWithAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/record-list-with-filter.json")).build())
            .getRecordApiForDomain(3650908);

      ImmutableList<RecordDetail> records = api.listByType("A").concat().toList();
      Date now = new Date();
      
      assertEquals(records.size(), 1);
      assertEquals(records.get(0).getId(), "A-9846146");
      assertEquals(records.get(0).getName(), JCLOUDS_EXAMPLE);
      assertEquals(records.get(0).getType(), "A");
      assertEquals(records.get(0).getData(), "10.0.1.0");
      assertEquals(records.get(0).getTTL(), 60000);
      assertTrue(records.get(0).getCreated().before(now));
      assertTrue(records.get(0).getUpdated().before(now));
   }

   public void testListByTypeAndData() {
      URI endpoint = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/domains/3650908/records?type=A&data=10.0.1.0");
      RecordApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey, 
            responseWithAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/record-list-with-filter.json")).build())
            .getRecordApiForDomain(3650908);

      ImmutableList<RecordDetail> records = api.listByTypeAndData("A", "10.0.1.0").concat().toList();
      Date now = new Date();
      
      assertEquals(records.size(), 1);
      assertEquals(records.get(0).getId(), "A-9846146");
      assertEquals(records.get(0).getName(), JCLOUDS_EXAMPLE);
      assertEquals(records.get(0).getType(), "A");
      assertEquals(records.get(0).getData(), "10.0.1.0");
      assertEquals(records.get(0).getTTL(), 60000);
      assertTrue(records.get(0).getCreated().before(now));
      assertTrue(records.get(0).getUpdated().before(now));
   }

   public void testListByNameAndType() {
      URI endpoint = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/domains/3650908/records?name=jclouds-example.com&type=A");
      RecordApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey, 
            responseWithAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/record-list-with-filter.json")).build())
            .getRecordApiForDomain(3650908);

      ImmutableList<RecordDetail> records = api.listByNameAndType(JCLOUDS_EXAMPLE, "A").concat().toList();
      Date now = new Date();
      
      assertEquals(records.size(), 1);
      assertEquals(records.get(0).getId(), "A-9846146");
      assertEquals(records.get(0).getName(), JCLOUDS_EXAMPLE);
      assertEquals(records.get(0).getType(), "A");
      assertEquals(records.get(0).getData(), "10.0.1.0");
      assertEquals(records.get(0).getTTL(), 60000);
      assertTrue(records.get(0).getCreated().before(now));
      assertTrue(records.get(0).getUpdated().before(now));
   }

   public void testGetByNameAndTypeAndData() {
      URI endpoint = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/domains/3650908/records?name=jclouds-example.com&type=A&data=10.0.1.0");
      RecordApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey, 
            responseWithAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/record-list-with-filter.json")).build())
            .getRecordApiForDomain(3650908);

      RecordDetail record = api.getByNameAndTypeAndData(JCLOUDS_EXAMPLE, "A", "10.0.1.0");
      Date now = new Date();
      
      assertEquals(record.getId(), "A-9846146");
      assertEquals(record.getName(), JCLOUDS_EXAMPLE);
      assertEquals(record.getType(), "A");
      assertEquals(record.getData(), "10.0.1.0");
      assertEquals(record.getTTL(), 60000);
      assertTrue(record.getCreated().before(now));
      assertTrue(record.getUpdated().before(now));
   }

   public void testGetRecord() {
      URI endpoint = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/domains/3650908/records/A-9846146");
      RecordApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey, 
            responseWithAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/record-get.json")).build())
            .getRecordApiForDomain(3650908);

      RecordDetail record = api.get("A-9846146");
      Date now = new Date();

      assertEquals(record.getId(), "A-9846146");
      assertEquals(record.getName(), JCLOUDS_EXAMPLE);
      assertEquals(record.getType(), "A");
      assertEquals(record.getData(), "10.0.1.0");
      assertEquals(record.getTTL(), 60000);
      assertTrue(record.getCreated().before(now));
      assertTrue(record.getUpdated().before(now));
   }
   
   public void testUpdateRecord() {
      URI endpoint = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/domains/3650908/records/SRV-21858");
      RecordApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey, 
            responseWithAccess,
            authenticatedGET()
               .method(PUT)
               .payload(payloadFromResourceWithContentType("/record-update.json", MediaType.APPLICATION_JSON))
               .endpoint(endpoint)
               .build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/record-update-response.json")).build())
            .getRecordApiForDomain(3650908);

      Record record = Record.builder()
            .name("_sip._udp." + JCLOUDS_EXAMPLE)
            .ttl(86401)
            .data("1 3444 sip." + JCLOUDS_EXAMPLE) // weight port target
            .priority(12358)
            .comment("Updated Protocol to UDP")
            .build();

      Job<Void> job = api.update("SRV-21858", record);
      
      assertEquals(job.getStatus(), Job.Status.COMPLETED);
   }
   
   public void testUpdateRecords() {
      URI endpoint = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/domains/3650908/records");
      RecordApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey, 
            responseWithAccess,
            authenticatedGET()
               .method(PUT)
               .payload(payloadFromResourceWithContentType("/records-update.json", MediaType.APPLICATION_JSON))
               .endpoint(endpoint)
               .build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/records-update-response.json")).build())
            .getRecordApiForDomain(3650908);

      Record updateARecord = Record.builder()
            .comment("Multi-record Update")
            .build();

      Record updateMXRecord = Record.builder()
            .comment("Multi-record Update")
            .build();

      Map<String, Record> updateRecords = ImmutableMap.<String, Record> of(
            "A-9846146", updateARecord,
            "MX-9846146", updateMXRecord);
      
      Job<?> job = api.update(updateRecords);
      
      assertEquals(job.getStatus(), Job.Status.COMPLETED);
   }

   public void testDeleteRecord() {
      URI endpoint = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/domains/3650908/records/A-9846146");
      RecordApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey, 
            responseWithAccess,
            authenticatedGET().method("DELETE").replaceHeader("Accept", MediaType.WILDCARD).endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/record-delete.json")).build())
            .getRecordApiForDomain(3650908);

      Job<?> job = api.delete("A-9846146");

      assertEquals(job.getStatus(), Job.Status.COMPLETED);
   }

   public void testDeleteRecords() {
      URI endpoint = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/domains/3650908/records?id=A-9846146&id=MX-9846146");
      RecordApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey, 
            responseWithAccess,
            authenticatedGET().method("DELETE").replaceHeader("Accept", MediaType.WILDCARD).endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/records-delete.json")).build())
            .getRecordApiForDomain(3650908);

      List<String> recordIds = ImmutableList.<String> of("A-9846146", "MX-9846146");      
      Job<?> job = api.delete(recordIds);

      assertEquals(job.getStatus(), Job.Status.COMPLETED);
   }
}
