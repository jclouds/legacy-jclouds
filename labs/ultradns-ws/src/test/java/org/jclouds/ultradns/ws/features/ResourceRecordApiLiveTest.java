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
package org.jclouds.ultradns.ws.features;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.equalTo;
import static java.util.logging.Logger.getAnonymousLogger;
import static org.jclouds.ultradns.ws.domain.ResourceRecord.rrBuilder;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.ultradns.ws.ResourceTypeToValue;
import org.jclouds.ultradns.ws.UltraDNSWSExceptions.ResourceAlreadyExistsException;
import org.jclouds.ultradns.ws.domain.Account;
import org.jclouds.ultradns.ws.domain.ResourceRecord;
import org.jclouds.ultradns.ws.domain.ResourceRecordMetadata;
import org.jclouds.ultradns.ws.domain.Zone;
import org.jclouds.ultradns.ws.internal.BaseUltraDNSWSApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.BiMap;
import com.google.common.collect.FluentIterable;
import com.google.common.primitives.UnsignedInteger;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "ResourceRecordApiLiveTest")
public class ResourceRecordApiLiveTest extends BaseUltraDNSWSApiLiveTest {

   private String zoneName = System.getProperty("user.name").replace('.', '-') + ".rr.ultradnstest.jclouds.org.";
   private Account account;

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      context.getApi().getZoneApi().delete(zoneName);
      account = context.getApi().getCurrentAccount();
      context.getApi().getZoneApi().createInAccount(zoneName, account.getId());
   }

   @Override
   @AfterClass(groups = { "integration", "live" })
   protected void tearDownContext() {
      context.getApi().getZoneApi().delete(zoneName);
      super.tearDownContext();
   }

   private void checkResourceRecord(ResourceRecord rr) {
      checkNotNull(rr.getName(), "DName cannot be null for a ResourceRecord %s", rr);
      checkNotNull(rr.getType(), "Type cannot be null for a ResourceRecord %s", rr);
      assertTrue(rr.getType().intValue() > 0, "Type must be positive for a ResourceRecord " + rr);
      checkNotNull(rr.getType(), "Type cannot be null for a ResourceRecord %s", rr);
      checkNotNull(rr.getTTL(), "TTL cannot be null for a ResourceRecord %s", rr);
      checkNotNull(rr.getRData(), "InfoValues cannot be null for a ResourceRecord %s", rr);
   }

   private void checkResourceRecordMetadata(ResourceRecordMetadata rr) {
      checkNotNull(rr.getZoneId(), "ZoneId cannot be null for a ResourceRecordMetadata %s", rr);
      checkNotNull(rr.getGuid(), "Guid cannot be null for a ResourceRecordMetadata %s", rr);
      checkNotNull(rr.getZoneName(), "ZoneName cannot be null for a ResourceRecordMetadata %s", rr);
      checkNotNull(rr.getCreated(), "Created cannot be null for a ResourceRecordMetadata %s", rr);
      checkNotNull(rr.getModified(), "Modified cannot be null for a ResourceRecordMetadata %s", rr);
      checkResourceRecord(rr.getRecord());
   }

   AtomicLong zones = new AtomicLong();

   @Test
   public void testListResourceRecords() {
      for (Zone zone : context.getApi().getZoneApi().listByAccount(account.getId())) {
         zones.incrementAndGet();
         for (ResourceRecordMetadata rr : api(zone.getName()).list()) {
            recordTypeCounts.getUnchecked(rr.getRecord().getType()).incrementAndGet();
            checkResourceRecordMetadata(rr);
         }
      }
   }

   LoadingCache<UnsignedInteger, AtomicLong> recordTypeCounts = CacheBuilder.newBuilder().build(
         new CacheLoader<UnsignedInteger, AtomicLong>() {
            public AtomicLong load(UnsignedInteger key) throws Exception {
               return new AtomicLong();
            }
         });

   private final static BiMap<UnsignedInteger, String> valueToType = new ResourceTypeToValue().inverse();

   @AfterClass
   void logSummary() {
      getAnonymousLogger().info("zoneCount: " + zones);
      for (Entry<UnsignedInteger, AtomicLong> entry : recordTypeCounts.asMap().entrySet())
         getAnonymousLogger().info(
               String.format("type: %s, count: %s", valueToType.get(entry.getKey()), entry.getValue()));
   }

   @Test(expectedExceptions = ResourceNotFoundException.class, expectedExceptionsMessageRegExp = "Zone does not exist in the system.")
   public void testListResourceRecordsWhenZoneIdNotFound() {
      api("AAAAAAAAAAAAAAAA").list();
   }

   @Test(expectedExceptions = ResourceNotFoundException.class, expectedExceptionsMessageRegExp = "No Resource Record with GUID found in the system")
   public void testUpdateWhenNotFound() {
      api(zoneName).update("AAAAAAAAAAAAAAAA",
            rrBuilder().name("mail." + zoneName).type("MX").ttl(1800).rdata(10).rdata("maileast.jclouds.org.").build());
   }

   @Test
   public void testDeleteWhenNotFound() {
      api(zoneName).delete("AAAAAAAAAAAAAAAA");
   }

   String guid;
   ResourceRecord mx = rrBuilder().name("mail." + zoneName).type("MX").ttl(1800).rdata(10)
         .rdata("maileast.jclouds.org.").build();

   @Test
   public void testCreateRecord() {
      guid = api(zoneName).create(mx);
      getAnonymousLogger().info("created record: " + guid);
      try {
         api(zoneName).create(mx);
         fail();
      } catch (ResourceAlreadyExistsException e) {

      }
      assertTrue(listRRs(mx).anyMatch(equalTo(mx)));
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
      assertTrue(listRRs(mx).anyMatch(equalTo(mx)));
   }

   @Test(dependsOnMethods = "testUpdateRecord")
   public void testDeleteRecord() {
      api(zoneName).delete(guid);
      assertFalse(listRRs(mx).anyMatch(equalTo(mx)));
   }

   private Function<ResourceRecordMetadata, ResourceRecord> toRecord = new Function<ResourceRecordMetadata, ResourceRecord>() {
      public ResourceRecord apply(ResourceRecordMetadata in) {
         checkResourceRecordMetadata(in);
         return in.getRecord();
      }
   };

   private FluentIterable<ResourceRecord> listRRs(ResourceRecord mx) {
      return api(zoneName).list().transform(toRecord);
   }

   private ResourceRecordApi api(String zoneName) {
      return context.getApi().getResourceRecordApiForZone(zoneName);
   }
}
