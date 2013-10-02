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

import static javax.ws.rs.HttpMethod.DELETE;
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

import javax.ws.rs.core.HttpHeaders;
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
public class ReverseDNSApiExpectTest extends BaseCloudDNSApiExpectTest<CloudDNSApi> {
   public static final String CLOUD_SERVERS_OPEN_STACK = "cloudServersOpenStack";
   private static final String JCLOUDS_EXAMPLE = "jclouds-example.com";
   private static final URI SERVER_URI = URI.create("https://dfw.servers.api.rackspacecloud.com/v2/123123/servers/f5fb9334-b4f0-49d0-a2cc-57a5772dc7d1");

   public void testCreateReverseDNSRecord() {
      URI endpoint = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/rdns");
      ReverseDNSApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey, 
            responseWithAccess,
            authenticatedGET()
               .method(POST)
               .payload(payloadFromResource("/record-ptr-create.json"))
               .endpoint(endpoint)
               .build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/record-ptr-create-response.json")).build())
            .getReverseDNSApiForService(CLOUD_SERVERS_OPEN_STACK);

      Record createPTRRecordIPv4 = Record.builder()
            .type("PTR")
            .name(JCLOUDS_EXAMPLE)
            .data("166.78.146.80")
            .ttl(11235)
            .build();

      Record createPTRRecordIPv6 = Record.builder()
            .type("PTR")
            .name(JCLOUDS_EXAMPLE)
            .data("2001:4800:7812:0514:9a32:3c2a:ff04:aed2")
            .comment("Hello IPv6")
            .build();
      
      List<Record> createRecords = ImmutableList.of(createPTRRecordIPv4, createPTRRecordIPv6);      
      Job<Set<RecordDetail>> job = api.create(SERVER_URI, createRecords);
      
      assertEquals(job.getStatus(), Job.Status.COMPLETED);
      assertTrue(job.getResource().isPresent());
      
      Set<RecordDetail> records = job.getResource().get();
      Date now = new Date();
      RecordDetail ptrRecordIPv4 = null;
      RecordDetail ptrRecordIPv6 = null;
      
      for (RecordDetail record: records) {
         if (record.getData().startsWith("166")) {
            ptrRecordIPv4 = record;
         } else if (record.getData().startsWith("2001")) {
            ptrRecordIPv6 = record;
         }
      }
      
      assertNotNull(ptrRecordIPv4.getId());
      assertEquals(ptrRecordIPv4.getType(), "PTR");
      assertEquals(ptrRecordIPv4.getName(), JCLOUDS_EXAMPLE);
      assertEquals(ptrRecordIPv4.getData(), "166.78.146.80");
      assertEquals(ptrRecordIPv4.getTTL(), 11235);
      assertNull(ptrRecordIPv4.getPriority());
      assertNull(ptrRecordIPv4.getComment());
      assertTrue(ptrRecordIPv4.getCreated().before(now));
      assertTrue(ptrRecordIPv4.getUpdated().before(now));
      
      assertNotNull(ptrRecordIPv6.getId());
      assertEquals(ptrRecordIPv6.getType(), "PTR");
      assertEquals(ptrRecordIPv6.getName(), JCLOUDS_EXAMPLE);
      assertEquals(ptrRecordIPv6.getData(), "2001:4800:7812:514:9a32:3c2a:ff04:aed2"); // leading 0 in 0514 removed
      assertTrue(ptrRecordIPv6.getTTL() > 0);
      assertNull(ptrRecordIPv6.getPriority());
      assertEquals(ptrRecordIPv6.getComment(), "Hello IPv6");
      assertTrue(ptrRecordIPv6.getCreated().before(now));
      assertTrue(ptrRecordIPv6.getUpdated().before(now));
   }

   public void testListReverseDNSRecords() {
      URI endpoint = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/rdns/cloudServersOpenStack?href=https%3A//dfw.servers.api.rackspacecloud.com/v2/123123/servers/f5fb9334-b4f0-49d0-a2cc-57a5772dc7d1");
      ReverseDNSApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey, 
            responseWithAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/record-ptr-list.json")).build())
            .getReverseDNSApiForService(CLOUD_SERVERS_OPEN_STACK);

      ImmutableList<RecordDetail> records = api.list(SERVER_URI).concat().toList();
      assertEquals(records.size(), 2);
      
      for (RecordDetail record: records) {
         assertTrue(record.getType().contains("PTR"));
         assertTrue(record.getName().contains(JCLOUDS_EXAMPLE));
      }
   }

   public void testGetReverseDNSRecord() {
      URI endpoint = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/rdns/cloudServersOpenStack/PTR-557437?href=https%3A//dfw.servers.api.rackspacecloud.com/v2/123123/servers/f5fb9334-b4f0-49d0-a2cc-57a5772dc7d1");
      ReverseDNSApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey, 
            responseWithAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/record-ptr-get.json")).build())
            .getReverseDNSApiForService(CLOUD_SERVERS_OPEN_STACK);

      RecordDetail record = api.get(SERVER_URI, "PTR-557437");
      Date now = new Date();

      assertEquals(record.getId(), "PTR-557437");
      assertEquals(record.getType(), "PTR");
      assertEquals(record.getName(), JCLOUDS_EXAMPLE);
      assertEquals(record.getData(), "166.78.146.80");
      assertEquals(record.getTTL(), 11235);
      assertNull(record.getPriority());
      assertNull(record.getComment());
      assertTrue(record.getCreated().before(now));
      assertTrue(record.getUpdated().before(now));
   }
   
   public void testUpdateReverseDNSRecord() {
      URI endpoint = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/rdns");
      ReverseDNSApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey, 
            responseWithAccess,
            authenticatedGET()
               .method(PUT)
               .payload(payloadFromResourceWithContentType("/record-ptr-update.json", MediaType.APPLICATION_JSON))
               .endpoint(endpoint)
               .build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/record-ptr-update-response.json")).build())
            .getReverseDNSApiForService(CLOUD_SERVERS_OPEN_STACK);

      Record updatePTRRecordIPv4 = Record.builder()
            .type("PTR")
            .name(JCLOUDS_EXAMPLE)
            .data("166.78.146.80")
            .ttl(12358)
            .build();

      Map<String, Record> idsToRecords = ImmutableMap.<String, Record> of("PTR-557437", updatePTRRecordIPv4);

      Job<Void> job = api.update(SERVER_URI, idsToRecords);
      
      assertEquals(job.getStatus(), Job.Status.COMPLETED);
   }
   
   public void testDeleteReverseDNSRecord() {
      URI endpoint = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/rdns/cloudServersOpenStack?href=https%3A//dfw.servers.api.rackspacecloud.com/v2/123123/servers/f5fb9334-b4f0-49d0-a2cc-57a5772dc7d1&ip=166.78.146.80");
      ReverseDNSApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey, 
            responseWithAccess,
            authenticatedGET().method(DELETE).replaceHeader(HttpHeaders.ACCEPT, MediaType.WILDCARD).endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/record-ptr-delete.json")).build())
            .getReverseDNSApiForService(CLOUD_SERVERS_OPEN_STACK);

      Job<Void> job = api.delete(SERVER_URI, "166.78.146.80");

      assertEquals(job.getStatus(), Job.Status.COMPLETED);
   }

   public void testDeleteReverseDNSRecords() {
      URI endpoint = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/rdns/cloudServersOpenStack?href=https%3A//dfw.servers.api.rackspacecloud.com/v2/123123/servers/f5fb9334-b4f0-49d0-a2cc-57a5772dc7d1");
      ReverseDNSApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey, 
            responseWithAccess,
            authenticatedGET().method(DELETE).replaceHeader("Accept", MediaType.WILDCARD).endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/records-ptr-delete.json")).build())
            .getReverseDNSApiForService(CLOUD_SERVERS_OPEN_STACK);

      Job<Void> job = api.deleteAll(SERVER_URI);

      assertEquals(job.getStatus(), Job.Status.COMPLETED);
   }
}
