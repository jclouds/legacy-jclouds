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

import static java.util.logging.Logger.getAnonymousLogger;
import static org.jclouds.ultradns.ws.domain.TrafficControllerPool.RecordType.IPV4;
import static org.jclouds.ultradns.ws.domain.TrafficControllerPoolRecordDetail.Status.UNRECOGNIZED;
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
import org.jclouds.ultradns.ws.domain.PoolRecordSpec;
import org.jclouds.ultradns.ws.domain.TrafficControllerPool;
import org.jclouds.ultradns.ws.domain.TrafficControllerPoolRecord;
import org.jclouds.ultradns.ws.domain.TrafficControllerPoolRecordDetail;
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

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setup() {
      super.setup();
      createZone();
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
      for (Zone zone : api.getZoneApi().listByAccount(account.getId())) {
         for (TrafficControllerPool pool : api(zone.getName()).list()) {
            checkTCPool(pool);
         }
      }
   }

   @Test
   public void testListTCPoolRecords() {
      for (Zone zone : api.getZoneApi().listByAccount(account.getId())) {
         for (TrafficControllerPool pool : api(zone.getName()).list()) {
            for (TrafficControllerPoolRecordDetail record : api(zone.getName()).listRecords(pool.getId())) {
               checkPoolRecordConsistent(zone.getName(), record);
            }
         }
      }
   }

   private TrafficControllerPoolRecordDetail checkPoolRecordConsistent(String zoneName, TrafficControllerPoolRecordDetail record) {
      Optional<TrafficControllerPool> pool = getPoolByZoneAndId(zoneName, record.getPoolId());
      assertTrue(pool.isPresent(), "could not get pool for " + record);
      assertEquals(record.getDescription(), pool.get().getName());
      PoolRecordSpec spec = checkPoolRecordSpec(api(zoneName).getRecordSpec(record.getId()));
      assertEquals(record.getDescription(), spec.getDescription());
      assertEquals(record.getWeight(), spec.getWeight());
      assertEquals(record.isProbingEnabled(), spec.isProbingEnabled());
      return checkTrafficControllerPoolRecord(record);
   }

   static TrafficControllerPoolRecordDetail checkTrafficControllerPoolRecord(TrafficControllerPoolRecordDetail record) {
      assertNotNull(record.getId(), "Id cannot be null for " + record);
      assertNotNull(record.getPoolId(), "PoolId cannot be null for " + record);
      assertNotNull(record.getRecord().getRData(), "Record.RData cannot be null for " + record);
      assertNotNull(record.getRecord().getType(), "Record.Type cannot be null for " + record);
      assertTrue(record.getWeight() >= 0, "Weight must be unsigned for " + record);
      assertTrue(record.getPriority() >= 0, "Priority must be unsigned for " + record);
      assertNotNull(record.getStatus(), "Status cannot be null for " + record);
      assertTrue(record.getStatus() != UNRECOGNIZED, "unrecognized status for " + record);
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
            UpdatePoolRecord.builder().rdata("www.foo.com.").mode("Normal").build());
   }

   String dname = "www.tcpool." + zoneName;
   String poolId;

   @Test
   public void testCreatePool() {
      poolId = api(zoneName).createForDNameAndType("pool", dname, IPV4.getCode());
      getAnonymousLogger().info("created tc pool: " + poolId);
      try {
         api(zoneName).createForDNameAndType("pool", dname, IPV4.getCode());
         fail();
      } catch (ResourceAlreadyExistsException e) {

      }
      // ensure there's only one pool for a dname
      try {
         api(zoneName).createForDNameAndType("pool1", dname, IPV4.getCode());
         fail();
      } catch (ResourceAlreadyExistsException e) {

      }
      Optional<TrafficControllerPool> pool = getPoolByZoneAndId(zoneName, poolId);
      assertTrue(pool.isPresent());
      assertEquals(pool.get().getId(), poolId);
      assertEquals(pool.get().getName(), "pool");
      assertEquals(pool.get().getDName(), dname);
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
   public TrafficControllerPoolRecordDetail addRecordToPool(String rdata, String type, int ttl, Optional<Integer> weight) {
      String recordId;
      if (weight.isPresent()) {
         recordId = api(zoneName).addRecordToPoolWithTTLAndWeight(rdata, poolId, ttl, weight.get());
      } else {
         recordId = api(zoneName).addRecordToPoolWithTTL(rdata, poolId, ttl);
      }
      getAnonymousLogger().info("created " + type + " record: " + recordId);
      TrafficControllerPoolRecordDetail record = checkPoolRecordConsistent(zoneName, getRecordById(recordId).get());
      PoolRecordSpec recordSpec = checkPoolRecordSpec(api(zoneName).getRecordSpec(recordId));
      assertEquals(record.getRecord(), TrafficControllerPoolRecord.create(type, rdata));
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
                                                .rdata("www.baz.com.")
                                                .weight(98)
                                                .ttl(200).build();

      api(zoneName).updateRecord(cname2, update);

      TrafficControllerPoolRecordDetail record = getRecordById(cname2).get();
      assertEquals(record.getRecord().getRData(), "www.baz.com.");

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

   private Optional<TrafficControllerPoolRecordDetail> getRecordById(String recordId) {
      return api(zoneName).listRecords(poolId).firstMatch(recordIdEqualTo(recordId));
   }

   private Optional<TrafficControllerPool> getPoolByZoneAndId(String zoneName, final String poolId) {
      return api(zoneName).list().firstMatch(idEqualTo(poolId));
   }

   private TrafficControllerPoolApi api(String zoneName) {
      return api.getTrafficControllerPoolApiForZone(zoneName);
   }

   @Override
   @AfterClass(groups = { "integration", "live" })
   protected void tearDown() {
      if (poolId != null)
         api(zoneName).delete(poolId);
      super.tearDown();
   }
}
