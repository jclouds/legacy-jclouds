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
package org.jclouds.ultradns.ws.features;
import static com.google.common.base.Predicates.equalTo;
import static java.lang.String.format;
import static java.util.logging.Logger.getAnonymousLogger;
import static org.jclouds.ultradns.ws.domain.ResourceRecord.rrBuilder;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.ultradns.ws.UltraDNSWSExceptions.ResourceAlreadyExistsException;
import org.jclouds.ultradns.ws.domain.ResourceRecord;
import org.jclouds.ultradns.ws.domain.ResourceRecordDetail;
import org.jclouds.ultradns.ws.domain.Zone;
import org.jclouds.ultradns.ws.internal.BaseUltraDNSWSApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.FluentIterable;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "ResourceRecordApiLiveTest")
public class ResourceRecordApiLiveTest extends BaseUltraDNSWSApiLiveTest {

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setup() {
      super.setup();
      createZone();
   }

   static void checkResourceRecord(ResourceRecord rr) {
      assertNotNull(rr.getName(), "DName cannot be null for " +  rr);
      assertNotNull(rr.getType(), "Type cannot be null for " +  rr);
      assertTrue(rr.getType() > 0, "Type must be unsigned for " + rr);
      assertNotNull(rr.getType(), "Type cannot be null for " +  rr);
      assertNotNull(rr.getTTL(), "TTL cannot be null for " +  rr);
      assertNotNull(rr.getRData(), "InfoValues cannot be null for " +  rr);
   }

   static void checkResourceRecordMetadata(ResourceRecordDetail rr) {
      assertNotNull(rr.getZoneId(), "ZoneId cannot be null for " +  rr);
      assertNotNull(rr.getGuid(), "Guid cannot be null for " +  rr);
      assertNotNull(rr.getZoneName(), "ZoneName cannot be null for " +  rr);
      assertNotNull(rr.getCreated(), "Created cannot be null for " +  rr);
      assertNotNull(rr.getModified(), "Modified cannot be null for " +  rr);
      checkResourceRecord(rr.getRecord());
   }

   AtomicLong zones = new AtomicLong();

   @Test
   public void testListResourceRecords() {
      for (Zone zone : api.getZoneApi().listByAccount(account.getId())) {
         zones.incrementAndGet();
         for (ResourceRecordDetail rr : api(zone.getName()).list()) {
            recordTypeCounts.getUnchecked(rr.getRecord().getType()).incrementAndGet();
            checkResourceRecordMetadata(rr);
         }
      }
   }

   LoadingCache<Integer, AtomicLong> recordTypeCounts = CacheBuilder.newBuilder().build(
         new CacheLoader<Integer, AtomicLong>() {
            public AtomicLong load(Integer key) throws Exception {
               return new AtomicLong();
            }
         });

   @AfterClass
   void logSummary() {
      getAnonymousLogger().info("zoneCount: " + zones);
      for (Entry<Integer, AtomicLong> entry : recordTypeCounts.asMap().entrySet())
         getAnonymousLogger().info(format("type: %s, count: %s", entry.getKey(), entry.getValue()));
   }

   @Test(expectedExceptions = ResourceNotFoundException.class, expectedExceptionsMessageRegExp = "Zone does not exist in the system.")
   public void testListResourceRecordsWhenZoneIdNotFound() {
      api("AAAAAAAAAAAAAAAA").list();
   }

   @Test(expectedExceptions = ResourceNotFoundException.class, expectedExceptionsMessageRegExp = "No Resource Record with GUID found in the system")
   public void testUpdateWhenNotFound() {
      api(zoneName).update("AAAAAAAAAAAAAAAA", mx);
   }

   @Test
   public void testDeleteWhenNotFound() {
      api(zoneName).delete("AAAAAAAAAAAAAAAA");
   }

   String guid;
   ResourceRecord mx = rrBuilder().name("mail." + zoneName)
                                  .type(15)
                                  .ttl(1800)
                                  .infoValue(10)
                                  .infoValue("maileast.jclouds.org.").build();

   @Test
   public void testCreateRecord() {
      guid = api(zoneName).create(mx);
      getAnonymousLogger().info("created record: " + guid);
      try {
         api(zoneName).create(mx);
         fail();
      } catch (ResourceAlreadyExistsException e) {

      }
      assertTrue(listRRs().anyMatch(equalTo(mx)));
   }

   @Test(dependsOnMethods = "testCreateRecord")
   public void testListResourceRecordsByName() {
      FluentIterable<ResourceRecord> byName = api(zoneName).listByName(mx.getName()).transform(toRecord);
      assertTrue(byName.anyMatch(equalTo(mx)));
   }

   @Test(dependsOnMethods = "testCreateRecord")
   public void testListResourceRecordsByNameAndType() {
      FluentIterable<ResourceRecord> byNameAndType = api(zoneName).listByNameAndType(mx.getName(), mx.getType())
            .transform(toRecord);
      assertTrue(byNameAndType.anyMatch(equalTo(mx)));
   }

   @Test(dependsOnMethods = { "testListResourceRecordsByName", "testListResourceRecordsByNameAndType" })
   public void testUpdateRecord() {
      mx = mx.toBuilder().ttl(3600).build();
      api(zoneName).update(guid, mx);
      assertTrue(listRRs().anyMatch(equalTo(mx)));
   }

   @Test(dependsOnMethods = "testUpdateRecord")
   public void testDeleteRecord() {
      api(zoneName).delete(guid);
      assertFalse(listRRs().anyMatch(equalTo(mx)));
   }

   static Function<ResourceRecordDetail, ResourceRecord> toRecord = new Function<ResourceRecordDetail, ResourceRecord>() {
      public ResourceRecord apply(ResourceRecordDetail in) {
         checkResourceRecordMetadata(in);
         return in.getRecord();
      }
   };

   private FluentIterable<ResourceRecord> listRRs() {
      return api(zoneName).list().transform(toRecord);
   }

   private ResourceRecordApi api(String zoneName) {
      return api.getResourceRecordApiForZone(zoneName);
   }
}
