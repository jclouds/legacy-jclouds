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
 * "AS IS" BASIS, WITHOUT WATCANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.ultradns.ws.features;

import static java.util.logging.Logger.getAnonymousLogger;
import static org.jclouds.ultradns.ws.predicates.TrafficControllerPoolPredicates.idEqualTo;
import static org.jclouds.ultradns.ws.predicates.TrafficControllerPoolPredicates.recordIdEqualTo;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.ultradns.ws.UltraDNSWSExceptions.ResourceAlreadyExistsException;
import org.jclouds.ultradns.ws.domain.Account;
import org.jclouds.ultradns.ws.domain.PoolRecordSpec;
import org.jclouds.ultradns.ws.domain.TrafficControllerPool;
import org.jclouds.ultradns.ws.domain.TrafficControllerPoolRecord;
import org.jclouds.ultradns.ws.domain.TrafficControllerPoolRecord.Status;
import org.jclouds.ultradns.ws.domain.UpdatePoolRecord;
import org.jclouds.ultradns.ws.domain.Zone;
import org.jclouds.ultradns.ws.internal.BaseUltraDNSWSApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "TrafficControllerPoolApiLiveTest")
public class TrafficControllerPoolApiLiveTest extends BaseUltraDNSWSApiLiveTest {

   private String zoneName = System.getProperty("user.name").replace('.', '-') + ".tcpool.ultradnstest.jclouds.org.";
   private Account account;

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      context.getApi().getZoneApi().delete(zoneName);
      account = context.getApi().getCurrentAccount();
      context.getApi().getZoneApi().createInAccount(zoneName, account.getId());
   }

   private void checkTCPool(TrafficControllerPool pool) {
      assertNotNull(pool.getZoneId(), "ZoneId cannot be null " + pool);
      assertNotNull(pool.getId(), "Id cannot be null " + pool);
      assertNotNull(pool.getName(), "Name cannot be null " + pool);
      assertNotNull(pool.getDName(), "DName cannot be null " + pool);
      assertEquals(api(zoneName).getNameByDName(pool.getDName()), pool.getName());
   }

   @Test
   public void testListTCPools() {
      for (Zone zone : context.getApi().getZoneApi().listByAccount(account.getId())) {
         for (TrafficControllerPool pool : api(zone.getName()).list()) {
            checkTCPool(pool);
         }
      }
   }

   @Test
   public void testListTCPoolRecords() {
      for (Zone zone : context.getApi().getZoneApi().listByAccount(account.getId())) {
         for (TrafficControllerPool pool : api(zone.getName()).list()) {
            for (TrafficControllerPoolRecord record : api(zone.getName()).listRecords(pool.getId())) {
               checkPoolRecordConsistent(zone.getName(), record);
            }
         }
      }
   }

   private TrafficControllerPoolRecord checkPoolRecordConsistent(String zoneName, TrafficControllerPoolRecord record) {
      Optional<TrafficControllerPool> pool = getPoolByZoneAndId(zoneName, record.getPoolId());
      assertTrue(pool.isPresent(), "could not get pool for " + record);
      assertEquals(record.getDescription(), pool.get().getName());
      PoolRecordSpec spec = checkPoolRecordSpec(api(zoneName).getRecordSpec(record.getId()));
      assertEquals(record.getDescription(), spec.getDescription());
      assertEquals(record.getWeight(), spec.getWeight());
      assertEquals(record.isProbingEnabled(), spec.isProbingEnabled());
      return checkTrafficControllerPoolRecord(record);
   }

   static TrafficControllerPoolRecord checkTrafficControllerPoolRecord(TrafficControllerPoolRecord record) {
      assertNotNull(record.getId(), "Id cannot be null for " + record);
      assertNotNull(record.getPoolId(), "PoolId cannot be null for " + record);
      assertNotNull(record.getPointsTo(), "PointsTo cannot be null for " + record);
      assertTrue(record.getWeight() >= 0, "Weight must be unsigned for " + record);
      assertTrue(record.getPriority() >= 0, "Priority must be unsigned for " + record);
      assertNotNull(record.getType(), "Type cannot be null for " + record);
      assertNotNull(record.getStatus(), "Status cannot be null for " + record);
      assertTrue(record.getStatus() != Status.UNRECOGNIZED, "unrecognized status for " + record);
      assertNotNull(record.getDescription(), "Description cannot be null for " + record);
      return record;
   }

   static PoolRecordSpec checkPoolRecordSpec(PoolRecordSpec record) {
      assertNotNull(record.getDescription(), "Description cannot be null for " + record);
      assertNotNull(record.getState(), "State cannot be null for " + record);
      // TODO: collect all possible states then consider enum
      assertTrue(ImmutableSet.of("Normal", "Normal-NoTest").contains(record.getState()), "Unknown State for " + record);
      assertTrue(record.getWeight() >= 0, "Weight must be unsigned for " + record);
      assertTrue(record.getFailOverDelay() >= 0, "failOverDelay must be unsigned for " + record);
      assertTrue(record.getThreshold() >= 0, "threshold must be unsigned for " + record);
      assertTrue(record.getTTL() >= 0, "ttl must be unsigned for " + record);
      return record;
   }

   @Test(expectedExceptions = ResourceNotFoundException.class, expectedExceptionsMessageRegExp = "Zone does not exist in the system.")
   public void testListTCPoolsWhenZoneIdNotFound() {
      api("AAAAAAAAAAAAAAAA").list();
   }

   @Test
   public void testDeleteWhenNotFound() {
      api(zoneName).delete("06063D9C54C5AE09");
   }

   @Test
   public void testDeleteRecordWhenNotFound() {
      api(zoneName).deleteRecord("06063D9C54C5AE09");
   }

   @Test
   public void testGetNameByDNameWhenNotFound() {
      assertNull(api(zoneName).getNameByDName("www.razzledazzle.cn."));
   }

   @Test
   public void testGetRecordSpecWhenNotFound() {
      assertNull(api(zoneName).getRecordSpec("06063D9C54C5AE09"));
   }

   @Test(expectedExceptions = ResourceNotFoundException.class, expectedExceptionsMessageRegExp = "Pool Record does not exist.")
   public void testUpdateRecordWhenNotFound() {
      api(zoneName).updateRecord("06063D9C54C5AE09",
            UpdatePoolRecord.builder().pointsTo("www.foo.com.").mode("Normal").build());
   }

   String hostname = "www.tcpool." + zoneName;
   String poolId;

   @Test
   public void testCreatePool() {
      poolId = api(zoneName).createPoolForHostname("pool", hostname);
      getAnonymousLogger().info("created tc pool: " + poolId);
      try {
         api(zoneName).createPoolForHostname("pool", hostname);
         fail();
      } catch (ResourceAlreadyExistsException e) {

      }
      // ensure there's only one pool for a hostname
      try {
         api(zoneName).createPoolForHostname("pool1", hostname);
         fail();
      } catch (ResourceAlreadyExistsException e) {

      }
      Optional<TrafficControllerPool> pool = getPoolByZoneAndId(zoneName, poolId);
      assertTrue(pool.isPresent());
      assertEquals(pool.get().getId(), poolId);
      assertEquals(pool.get().getName(), "pool");
      assertEquals(pool.get().getDName(), hostname);
      checkTCPool(pool.get());
   }

   @DataProvider(name = "records")
   public Object[][] createRecords() {
      Object[][] records = new Object[2][4];
      records[0][0] = "1.2.3.4";
      records[0][1] = "A";
      records[0][2] = 60;
      records[0][3] = Optional.of(98);
      records[1][0] = "5.6.7.8";
      records[1][1] = "A";
      records[1][2] = 60;
      records[1][3] = Optional.of(2);
      return records;
   }

   @Test(dependsOnMethods = "testCreatePool", dataProvider = "records")
   public TrafficControllerPoolRecord addRecordToPool(String pointsTo, String type, int ttl, Optional<Integer> weight) {
      String recordId;
      if (weight.isPresent()) {
         recordId = api(zoneName).addRecordToPoolWithTTLAndWeight(pointsTo, poolId, ttl, weight.get());
      } else {
         recordId = api(zoneName).addRecordToPoolWithTTL(pointsTo, poolId, ttl);
      }
      getAnonymousLogger().info("created " + type + " record: " + recordId);
      TrafficControllerPoolRecord record = checkPoolRecordConsistent(zoneName, getRecordById(recordId).get());
      PoolRecordSpec recordSpec = checkPoolRecordSpec(api(zoneName).getRecordSpec(recordId));
      assertEquals(record.getPointsTo(), pointsTo);
      assertEquals(record.getType(), type);
      assertEquals(record.getWeight(), weight.or(2).intValue());
      assertEquals(recordSpec.getTTL(), ttl);
      return record;
   }

   String cname1;
   String cname2;

   @Test(dependsOnMethods = "testCreatePool")
   public void addCNAMERecordsToPool() {
      cname1 = addRecordToPool("www.foo.com.", "CNAME", 30, Optional.<Integer> absent()).getId();

      try {
         api(zoneName).addRecordToPoolWithTTL("www.foo.com.", poolId, 30);
         fail();
      } catch (ResourceAlreadyExistsException e) {

      }

      cname2 = addRecordToPool("www.bar.com.", "CNAME", 30, Optional.<Integer> absent()).getId();
   }

   @Test(dependsOnMethods = "addCNAMERecordsToPool")
   public void testUpdateRecord() {
      PoolRecordSpec spec = api(zoneName).getRecordSpec(cname2);
      UpdatePoolRecord update = UpdatePoolRecord.builder().from(spec)
                                                .pointsTo("www.baz.com.")
                                                .weight(98)
                                                .ttl(200).build();

      api(zoneName).updateRecord(cname2, update);

      TrafficControllerPoolRecord record = getRecordById(cname2).get();
      assertEquals(record.getPointsTo(), "www.baz.com.");

      spec = api(zoneName).getRecordSpec(cname2);
      assertEquals(spec.getWeight(), 98);
      assertEquals(spec.getTTL(), 200);
   }

   @Test(dependsOnMethods = "testUpdateRecord")
   public void testDeleteRecord() {
      api(zoneName).deleteRecord(cname1);
      assertFalse(getRecordById(cname1).isPresent());
      assertTrue(getRecordById(cname2).isPresent());
   }

   @Test(dependsOnMethods = "testDeleteRecord")
   public void testDeletePool() {
      api(zoneName).delete(poolId);
      assertFalse(getPoolByZoneAndId(zoneName, poolId).isPresent());
   }

   private Optional<TrafficControllerPoolRecord> getRecordById(String recordId) {
      return api(zoneName).listRecords(poolId).firstMatch(recordIdEqualTo(recordId));
   }

   private Optional<TrafficControllerPool> getPoolByZoneAndId(String zoneName, final String poolId) {
      return api(zoneName).list().firstMatch(idEqualTo(poolId));
   }

   private TrafficControllerPoolApi api(String zoneName) {
      return context.getApi().getTrafficControllerPoolApiForZone(zoneName);
   }

   @Override
   @AfterClass(groups = { "integration", "live" })
   protected void tearDownContext() {
      if (poolId != null)
         api(zoneName).delete(poolId);
      context.getApi().getZoneApi().delete(zoneName);
      super.tearDownContext();
   }
}
