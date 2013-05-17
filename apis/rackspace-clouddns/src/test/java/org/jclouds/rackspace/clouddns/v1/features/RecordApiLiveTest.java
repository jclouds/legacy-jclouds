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

import static org.jclouds.rackspace.clouddns.v1.predicates.JobPredicates.awaitComplete;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import org.jclouds.rackspace.clouddns.v1.domain.CreateDomain;
import org.jclouds.rackspace.clouddns.v1.domain.Domain;
import org.jclouds.rackspace.clouddns.v1.domain.Record;
import org.jclouds.rackspace.clouddns.v1.domain.RecordDetail;
import org.jclouds.rackspace.clouddns.v1.functions.RecordFunctions;
import org.jclouds.rackspace.clouddns.v1.internal.BaseCloudDNSApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

/**
 * @author Everett Toews
 */
@Test(groups = "live", singleThreaded = true, testName = "RecordApiLiveTest")
public class RecordApiLiveTest extends BaseCloudDNSApiLiveTest {

   // just in case the username has a '.' we replace it to avoid creating subdomains
   private static final String JCLOUDS_EXAMPLE = System.getProperty("user.name").replace('.', '-') + "-recordtest-jclouds.org";
   
   private int domainId;
   private String aRecordId;
   private String srvRecordId;
   private String mxRecordId;

   @Test
   public void testCreateDomain() throws Exception {
      CreateDomain createDomain = CreateDomain.builder()
            .name(JCLOUDS_EXAMPLE)
            .email("jclouds@" + JCLOUDS_EXAMPLE)
            .ttl(60000)
            .build();

      Iterable<CreateDomain> createDomains = ImmutableList.of(createDomain);      
      Domain domain = awaitComplete(api, api.getDomainApi().create(createDomains)).iterator().next();
      
      assertEquals(domain.getName(), JCLOUDS_EXAMPLE);
      assertEquals(domain.getEmail(), "jclouds@" + JCLOUDS_EXAMPLE);
      assertTrue(domain.getRecords().isEmpty());
      
      domainId = domain.getId();
   }   

   @Test(dependsOnMethods = "testCreateDomain")
   public void testCreateRecords() throws Exception {
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
      
      Record createSRVRecord = Record.builder()
            .type("SRV")
            .name("_sip._tcp." + JCLOUDS_EXAMPLE)
            .ttl(86400)
            .data("1 3443 sip." + JCLOUDS_EXAMPLE) // weight port target
            .priority(11235)
            .comment("Updated Protocol to UDP")
            .build();

      List<Record> createRecords = ImmutableList.of(createMXRecord, createARecord, createSRVRecord);
      Set<RecordDetail> records = awaitComplete(api, api.getRecordApiForDomain(domainId).create(createRecords));

      Thread.sleep(1000);
      Date now = new Date();
      
      RecordDetail mxRecord = null;
      RecordDetail aRecord = null;
      RecordDetail srvRecord = null;
      
      for (RecordDetail record: records) {
         if (record.getType().equals("MX")) {
            mxRecord = record;
         } else if (record.getType().equals("A")) {
            aRecord = record;
         } else if (record.getType().equals("SRV")) {
            srvRecord = record;
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
      assertEquals(aRecord.getData(), "10.0.0.1");
      assertNull(aRecord.getPriority());
      assertEquals(aRecord.getTTL(), 60000);
      assertTrue(aRecord.getCreated().before(now));
      assertTrue(aRecord.getUpdated().before(now));
      
      assertNotNull(srvRecord.getId());
      assertEquals(srvRecord.getType(), "SRV");
      assertEquals(srvRecord.getName(), "_sip._tcp." + JCLOUDS_EXAMPLE);
      assertEquals(srvRecord.getData(), "1 3443 sip." + JCLOUDS_EXAMPLE);
      assertEquals(srvRecord.getPriority().intValue(), 11235);
      assertEquals(srvRecord.getTTL(), 86400);
      assertTrue(srvRecord.getCreated().before(now));
      assertTrue(srvRecord.getUpdated().before(now));
   }

   @Test(dependsOnMethods = "testCreateRecords")
   public void testListRecords() throws Exception {
      Set<RecordDetail> records = api.getRecordApiForDomain(domainId).list().concat().toSet();
      assertEquals(records.size(), 5); // 3 created above + 2 nameserver (NS) records
   }

   @Test(dependsOnMethods = "testListRecords")
   public void testListRecordsByCriteriaMethods() throws Exception {
      List<RecordDetail> records = api.getRecordApiForDomain(domainId).listByType("SRV").concat().toList();
      assertEquals(records.size(), 1);
      
      srvRecordId = records.get(0).getId();

      records = api.getRecordApiForDomain(domainId).listByTypeAndData("A", "10.0.0.1").concat().toList();
      assertEquals(records.size(), 1);
      
      aRecordId = records.get(0).getId();

      records = api.getRecordApiForDomain(domainId).listByNameAndType(JCLOUDS_EXAMPLE, "MX").concat().toList();
      assertEquals(records.size(), 1);
      
      mxRecordId = records.get(0).getId();
   }

   @Test(dependsOnMethods = "testListRecordsByCriteriaMethods")
   public void testGetRecordByNameAndTypeAndData() throws Exception {
      RecordDetail record = api.getRecordApiForDomain(domainId).getByNameAndTypeAndData(JCLOUDS_EXAMPLE, "A", "10.0.0.1");
      Date now = new Date();
      
      assertNotNull(record.getId());
      assertEquals(record.getName(), JCLOUDS_EXAMPLE);
      assertEquals(record.getType(), "A");
      assertEquals(record.getData(), "10.0.0.1");
      assertEquals(record.getTTL(), 60000);
      assertTrue(record.getCreated().before(now));
      assertTrue(record.getUpdated().before(now));
   }

   @Test(dependsOnMethods = "testGetRecordByNameAndTypeAndData")
   public void testGetRecord() throws Exception {
      RecordDetail record = api.getRecordApiForDomain(domainId).get(aRecordId);
      Date now = new Date();
      
      assertNotNull(record.getId());
      assertEquals(record.getName(), JCLOUDS_EXAMPLE);
      assertEquals(record.getType(), "A");
      assertEquals(record.getData(), "10.0.0.1");
      assertEquals(record.getTTL(), 60000);
      assertTrue(record.getCreated().before(now));
      assertTrue(record.getUpdated().before(now));
   }

   @Test(dependsOnMethods = "testGetRecord")
   public void testUpdateRecord() throws Exception {      
      Record record = Record.builder()
            .name("_sip._udp." + JCLOUDS_EXAMPLE)
            .ttl(86401)
            .data("1 3444 sip." + JCLOUDS_EXAMPLE) // weight port target
            .priority(12358)
            .comment("Updated Protocol to UDP")
            .build();

      awaitComplete(api, api.getRecordApiForDomain(domainId).update(srvRecordId, record));

      RecordDetail srvRecord = api.getRecordApiForDomain(domainId).get(srvRecordId);
      Date now = new Date();
      
      assertNotNull(srvRecord.getId());
      assertEquals(srvRecord.getType(), "SRV");
      assertEquals(srvRecord.getName(), "_sip._udp." + JCLOUDS_EXAMPLE);
      assertEquals(srvRecord.getData(), "1 3444 sip." + JCLOUDS_EXAMPLE);
      assertEquals(srvRecord.getPriority().intValue(), 12358);
      assertEquals(srvRecord.getTTL(), 86401);
      assertEquals(srvRecord.getComment(), "Updated Protocol to UDP");
      assertTrue(srvRecord.getCreated().before(now));
      assertTrue(srvRecord.getUpdated().before(now));      
   }

   @Test(dependsOnMethods = "testUpdateRecord")
   public void testUpdateRecords() throws Exception {      
      Set<RecordDetail> recordDetails = api.getRecordApiForDomain(domainId).list().concat().toSet();
      Map<String, Record> idsToRecords = RecordFunctions.toRecordMap(recordDetails);
      Map<String, Record> updateRecords = Maps.transformValues(idsToRecords, updateTTLAndComment(35813, "New TTL")); 
            
      awaitComplete(api, api.getRecordApiForDomain(domainId).update(updateRecords));

      RecordDetail record = api.getRecordApiForDomain(domainId).get(aRecordId);
      Date now = new Date();
      
      assertNotNull(record.getId());
      assertEquals(record.getName(), JCLOUDS_EXAMPLE);
      assertEquals(record.getType(), "A");
      assertEquals(record.getData(), "10.0.0.1");
      assertEquals(record.getTTL(), 35813);
      assertEquals(record.getComment(), "New TTL");
      assertTrue(record.getCreated().before(now));
      assertTrue(record.getUpdated().before(now));
      
      recordDetails = api.getRecordApiForDomain(domainId).list().concat().toSet();
      
      for (RecordDetail recordDetail: recordDetails) {
         assertEquals(recordDetail.getTTL(), 35813);
         assertEquals(recordDetail.getComment(), "New TTL");
      }
   }

   private Function<Record, Record> updateTTLAndComment(final int ttl, final String comment) {
      return new Function<Record, Record>() {
         public Record apply(Record record) {
            return record.toBuilder().ttl(ttl).comment(comment).build();
         }
      };
   }

   @Test(dependsOnMethods = "testUpdateRecords")
   public void testDeleteRecord() throws Exception {      
      awaitComplete(api, api.getRecordApiForDomain(domainId).delete(aRecordId));
      
      assertNull(api.getRecordApiForDomain(domainId).get(aRecordId));
   }

   @Test(dependsOnMethods = "testDeleteRecord")
   public void testDeleteRecords() throws Exception {      
      List<String> recordIds = ImmutableList.<String> of(srvRecordId, mxRecordId);
      
      awaitComplete(api, api.getRecordApiForDomain(domainId).delete(recordIds));
      
      assertNull(api.getRecordApiForDomain(domainId).get(srvRecordId));
      assertNull(api.getRecordApiForDomain(domainId).get(mxRecordId));
   }

   @Override
   @AfterClass(groups = { "integration", "live" })
   protected void tearDown() {
      try {
         awaitComplete(api, api.getDomainApi().delete(ImmutableList.<Integer> of(domainId), true));
      }
      catch (TimeoutException e) {
         e.printStackTrace();
      }
      super.tearDown();
   }
}
