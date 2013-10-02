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

import static org.jclouds.rackspace.clouddns.v1.features.ReverseDNSApiExpectTest.CLOUD_SERVERS_OPEN_STACK;
import static org.jclouds.rackspace.clouddns.v1.predicates.JobPredicates.awaitComplete;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.config.ComputeServiceProperties;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.NovaAsyncApi;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.rackspace.clouddns.v1.domain.CreateDomain;
import org.jclouds.rackspace.clouddns.v1.domain.Domain;
import org.jclouds.rackspace.clouddns.v1.domain.Record;
import org.jclouds.rackspace.clouddns.v1.domain.RecordDetail;
import org.jclouds.rackspace.clouddns.v1.internal.BaseCloudDNSApiLiveTest;
import org.jclouds.rest.RestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * @author Everett Toews
 */
@Test(groups = "live", singleThreaded = true, testName = "ReverseDNSApiLiveTest")
public class ReverseDNSApiLiveTest extends BaseCloudDNSApiLiveTest {

   // just in case the username has a '.' we replace it to avoid creating subdomains
   private static final String JCLOUDS_EXAMPLE = System.getProperty("user.name").replace('.', '-')
         + "-recordtest-jclouds.org";

   private ComputeService computeService;
   private RestContext<NovaApi, NovaAsyncApi> nova;
   private String serverId;
   private URI serverURI;
   private String serverIPv4;
   private String serverIPv6;

   private int domainId;
   private String ptrRecordIPv4Id;
   private String ptrRecordIPv6Id;

   @Test
   public void testCreateServer() throws Exception {
      Properties overrides = new Properties();
      overrides.setProperty(ComputeServiceProperties.POLL_INITIAL_PERIOD, "10000");
      overrides.setProperty(ComputeServiceProperties.POLL_MAX_PERIOD, "10000");

      ComputeServiceContext context = ContextBuilder.newBuilder("rackspace-cloudservers-us")
            .credentials(identity, credential)
            .overrides(overrides)
            .buildView(ComputeServiceContext.class);
      computeService = context.getComputeService();
      nova = context.unwrap();

      Template template = computeService.templateBuilder().smallest().build();
      NodeMetadata nodeMetadata = computeService.createNodesInGroup("jclouds-reverse-dns-test", 1, template).iterator().next();
      serverId = nodeMetadata.getId();
      serverURI = nodeMetadata.getUri();

      ServerApi serverApi = nova.getApi().getServerApiForZone(nodeMetadata.getLocation().getParent().getId());
      Server server = serverApi.get(nodeMetadata.getProviderId());
      serverIPv4 = server.getAccessIPv4();
      serverIPv6 = server.getAccessIPv6();
      
      System.out.println("serverURI = " + serverURI);
      System.out.println("serverIPv4 = " + serverIPv4);
      System.out.println("serverIPv6 = " + serverIPv6);
   }

   @Test(dependsOnMethods = "testCreateServer")
   public void testCreateDomain() throws Exception {
      CreateDomain createDomain = CreateDomain.builder().name(JCLOUDS_EXAMPLE).email("jclouds@" + JCLOUDS_EXAMPLE)
            .ttl(60000).build();

      Iterable<CreateDomain> createDomains = ImmutableList.of(createDomain);
      Domain domain = awaitComplete(api, api.getDomainApi().create(createDomains)).iterator().next();

      assertEquals(domain.getName(), JCLOUDS_EXAMPLE);
      assertEquals(domain.getEmail(), "jclouds@" + JCLOUDS_EXAMPLE);
      assertTrue(domain.getRecords().isEmpty());

      domainId = domain.getId();
   }

   @Test(dependsOnMethods = "testCreateDomain")
   public void testCreateReverseDNSRecords() throws Exception {
      Record createPTRRecordIPv4 = Record.builder().type("PTR").name(JCLOUDS_EXAMPLE).data(serverIPv4).ttl(11235)
            .build();

      Record createPTRRecordIPv6 = Record.builder().type("PTR").name(JCLOUDS_EXAMPLE).data(serverIPv6)
            .comment("Hello IPv6").build();

      List<Record> createRecords = ImmutableList.of(createPTRRecordIPv4, createPTRRecordIPv6);
      Set<RecordDetail> records = awaitComplete(api,
            api.getReverseDNSApiForService(CLOUD_SERVERS_OPEN_STACK).create(serverURI, createRecords));

      Date now = new Date();
      RecordDetail ptrRecordIPv4 = null;
      RecordDetail ptrRecordIPv6 = null;

      for (RecordDetail record: records) {
         if (record.getData().equals(serverIPv4)) {
            ptrRecordIPv4 = record;
            ptrRecordIPv4Id = record.getId();
         }
         else {
            ptrRecordIPv6 = record;
            ptrRecordIPv6Id = record.getId();
         }
      }

      assertNotNull(ptrRecordIPv4.getId());
      assertEquals(ptrRecordIPv4.getType(), "PTR");
      assertEquals(ptrRecordIPv4.getName(), JCLOUDS_EXAMPLE);
      assertEquals(ptrRecordIPv4.getData(), serverIPv4);
      assertEquals(ptrRecordIPv4.getTTL(), 11235);
      assertNull(ptrRecordIPv4.getPriority());
      assertNull(ptrRecordIPv4.getComment());
      assertTrue(ptrRecordIPv4.getCreated().before(now));
      assertTrue(ptrRecordIPv4.getUpdated().before(now));

      assertNotNull(ptrRecordIPv6.getId());
      assertEquals(ptrRecordIPv6.getType(), "PTR");
      assertEquals(ptrRecordIPv6.getName(), JCLOUDS_EXAMPLE);
      // can't test equals for data as CDNS will remove leading 0s from IPv6 Addrs
      assertNotNull(ptrRecordIPv6.getData());
      assertTrue(ptrRecordIPv6.getTTL() > 0);
      assertNull(ptrRecordIPv6.getPriority());
      assertEquals(ptrRecordIPv6.getComment(), "Hello IPv6");
      assertTrue(ptrRecordIPv6.getCreated().before(now));
      assertTrue(ptrRecordIPv6.getUpdated().before(now));
   }

   @Test(dependsOnMethods = "testCreateReverseDNSRecords")
   public void testListReverseDNSRecords() throws Exception {
      Set<RecordDetail> records = api.getReverseDNSApiForService(CLOUD_SERVERS_OPEN_STACK).list(serverURI).concat()
            .toSet();
      assertEquals(records.size(), 2);
   }

   @Test(dependsOnMethods = "testListReverseDNSRecords")
   public void testUpdateAndGetReverseDNSRecords() throws Exception {
      Record updatePTRRecordIPv4 = Record.builder().type("PTR").name(JCLOUDS_EXAMPLE).data(serverIPv4).ttl(12358)
            .build();

      Map<String, Record> idsToRecords = ImmutableMap.<String, Record> of(ptrRecordIPv4Id, updatePTRRecordIPv4);

      awaitComplete(api, api.getReverseDNSApiForService(CLOUD_SERVERS_OPEN_STACK).update(serverURI, idsToRecords));

      RecordDetail record = api.getReverseDNSApiForService(CLOUD_SERVERS_OPEN_STACK).get(serverURI, ptrRecordIPv4Id);
      Date now = new Date();

      assertNotNull(record.getId());
      assertEquals(record.getType(), "PTR");
      assertEquals(record.getName(), JCLOUDS_EXAMPLE);
      assertEquals(record.getData(), serverIPv4);
      assertEquals(record.getTTL(), 12358);
      assertNull(record.getPriority());
      assertNull(record.getComment());
      assertTrue(record.getCreated().before(now));
      assertTrue(record.getUpdated().before(now));
   }

   @Test(dependsOnMethods = "testUpdateAndGetReverseDNSRecords")
   public void testDeleteReverseDNSRecord() throws Exception {
      awaitComplete(api, api.getReverseDNSApiForService(CLOUD_SERVERS_OPEN_STACK).delete(serverURI, serverIPv4));

      assertNull(api.getReverseDNSApiForService(CLOUD_SERVERS_OPEN_STACK).get(serverURI, ptrRecordIPv4Id));
   }

   @Test(dependsOnMethods = "testUpdateAndGetReverseDNSRecords")
   public void testDeleteReverseDNSRecords() throws Exception {
      awaitComplete(api, api.getReverseDNSApiForService(CLOUD_SERVERS_OPEN_STACK).deleteAll(serverURI));

      assertNull(api.getReverseDNSApiForService(CLOUD_SERVERS_OPEN_STACK).get(serverURI, ptrRecordIPv6Id));
   }

   @Override
   @AfterClass(groups = { "integration", "live" })
   protected void tearDown() {
      try {
         computeService.destroyNode(serverId);
         awaitComplete(api, api.getDomainApi().delete(ImmutableList.<Integer> of(domainId), true));
      }
      catch (TimeoutException e) {
         e.printStackTrace();
      }
      super.tearDown();
   }
}
