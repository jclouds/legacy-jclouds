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
import static java.util.logging.Logger.getAnonymousLogger;
import static org.jclouds.ultradns.ws.domain.ResourceRecord.rrBuilder;
import static org.jclouds.ultradns.ws.domain.RoundRobinPool.RecordType.A;
import static org.jclouds.ultradns.ws.domain.RoundRobinPool.RecordType.AAAA;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.ultradns.ws.UltraDNSWSExceptions.ResourceAlreadyExistsException;
import org.jclouds.ultradns.ws.domain.ResourceRecord;
import org.jclouds.ultradns.ws.domain.ResourceRecordDetail;
import org.jclouds.ultradns.ws.domain.RoundRobinPool;
import org.jclouds.ultradns.ws.domain.Zone;
import org.jclouds.ultradns.ws.internal.BaseUltraDNSWSApiLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "RoundRobinPoolApiLiveTest")
public class RoundRobinPoolApiLiveTest extends BaseUltraDNSWSApiLiveTest {

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setup() {
      super.setup();
      createZone();
   }

   private void checkRRPool(RoundRobinPool pool) {
      assertNotNull(pool.getZoneId(), "ZoneId cannot be null for " + pool);
      assertNotNull(pool.getId(), "Id cannot be null for " + pool);
      assertNotNull(pool.getName(), "Name cannot be null for " + pool);
      assertNotNull(pool.getDName(), "DName cannot be null for " + pool);
   }

   @Test
   public void testListRRPools() {
      for (Zone zone : api.getZoneApi().listByAccount(account.getId())) {
         for (RoundRobinPool pool : api(zone.getName()).list()) {
            checkRRPool(pool);
         }
      }
   }

   @Test
   public void testListRRPoolRecords() {
      for (Zone zone : api.getZoneApi().listByAccount(account.getId())) {
         for (RoundRobinPool pool : api(zone.getName()).list()) {
            for (ResourceRecordDetail record : api(zone.getName()).listRecords(pool.getId())) {
               ResourceRecordApiLiveTest.checkResourceRecordMetadata(record);
            }
         }
      }
   }

   @Test(expectedExceptions = ResourceNotFoundException.class, expectedExceptionsMessageRegExp = "Zone does not exist in the system.")
   public void testListRRPoolsWhenZoneIdNotFound() {
      api("AAAAAAAAAAAAAAAA").list();
   }

   @Test
   public void testDeleteWhenNotFound() {
      api(zoneName).delete("06063D9C54C5AE09");
   }

   String dname = "www.rrpool." + zoneName;
   String aPoolId;

   @Test
   public void testCreateAPool() {
      aPoolId = api(zoneName).createForDNameAndType("A pool", dname, A.getCode());
      getAnonymousLogger().info("created A rr pool: " + aPoolId);
      try {
         api(zoneName).createForDNameAndType("A pool", dname, A.getCode());
         fail();
      } catch (ResourceAlreadyExistsException e) {

      }
      Optional<RoundRobinPool> aPool = getPoolById(aPoolId);
      assertTrue(aPool.isPresent());
      assertEquals(aPool.get().getName(), "A pool");
      assertEquals(aPool.get().getDName(), dname);
   }

   String aRecord1;
   String aRecord2;

   @Test(dependsOnMethods = "testCreateAPool")
   public void addARecordToPool() {
      aRecord1 = api(zoneName).addARecordWithAddressAndTTL(aPoolId, "1.2.3.4", 1);

      getAnonymousLogger().info("created A record: " + aRecord1);

      assertTrue(listRRs(aPoolId).anyMatch(
            equalTo(rrBuilder().name(dname).type(1).ttl(1).rdata("1.2.3.4").build())));

      aRecord2 = api(zoneName).addARecordWithAddressAndTTL(aPoolId, "3.4.5.6", 1);

      assertTrue(listRRs(aPoolId).anyMatch(
            equalTo(rrBuilder().name(dname).type(1).ttl(1).rdata("3.4.5.6").build())));

      getAnonymousLogger().info("created A record: " + aRecord1);
      try {
         api(zoneName).addARecordWithAddressAndTTL(aPoolId, "1.2.3.4", 1);
         fail();
      } catch (ResourceAlreadyExistsException e) {

      }
   }

   @Test(dependsOnMethods = "addARecordToPool")
   public void testUpdateRecord() {
      api(zoneName).updateRecordWithAddressAndTTL(aPoolId, aRecord1, "1.1.1.1", 0);
      assertTrue(listRRs(aPoolId).anyMatch(
            equalTo(rrBuilder().name(dname).type(1).ttl(0).rdata("1.1.1.1").build())));
   }

   @Test(dependsOnMethods = "testUpdateRecord")
   public void testDeleteRecord() {
      api(zoneName).deleteRecord(aRecord2);
      assertTrue(listRRs(aPoolId).anyMatch(
            equalTo(rrBuilder().name(dname).type(1).ttl(0).rdata("1.1.1.1").build())));

      assertFalse(listRRs(aPoolId).anyMatch(
            equalTo(rrBuilder().name(dname).type(1).ttl(1).rdata("3.4.5.6").build())));
   }

   @Test(dependsOnMethods = "testDeleteRecord")
   public void testDeleteAPool() {
      api(zoneName).delete(aPoolId);
      assertFalse(getPoolById(aPoolId).isPresent());
   }

   private String aaaaPoolId;

   @Test
   public void testCreateAAAAPool() {
      aaaaPoolId = api(zoneName).createForDNameAndType("AAAA pool", dname, AAAA.getCode());
      getAnonymousLogger().info("created AAAA rr pool: " + aaaaPoolId);
      try {
         api(zoneName).createForDNameAndType("AAAA pool", dname, AAAA.getCode());
         fail();
      } catch (ResourceAlreadyExistsException e) {

      }
      Optional<RoundRobinPool> aPool = getPoolById(aaaaPoolId);
      assertTrue(aPool.isPresent());
      assertEquals(aPool.get().getName(), "AAAA pool");
      assertEquals(aPool.get().getDName(), dname);
   }

   String aaaaRecord1;
   String aaaaRecord2;

   @Test(dependsOnMethods = "testCreateAAAAPool")
   public void addAAAARecordToPool() {
      aaaaRecord1 = api(zoneName).addAAAARecordWithAddressAndTTL(aaaaPoolId, "2001:0DB8:85A3:0000:0000:8A2E:0370:7334",
            1);

      getAnonymousLogger().info("created AAAA record: " + aaaaRecord1);

      assertTrue(listRRs(aaaaPoolId).anyMatch(
            equalTo(rrBuilder().name(dname)
                               .type(28)
                               .ttl(1)
                               .rdata("2001:0DB8:85A3:0000:0000:8A2E:0370:7334")
                               .build())));

      aaaaRecord2 = api(zoneName).addAAAARecordWithAddressAndTTL(aaaaPoolId, "2002:0DB8:85A3:0000:0000:8A2E:0370:7334",
            1);

      assertTrue(listRRs(aaaaPoolId).anyMatch(
            equalTo(rrBuilder().name(dname)
                               .type(28)
                               .ttl(1)
                               .rdata("2002:0DB8:85A3:0000:0000:8A2E:0370:7334")
                               .build())));

      getAnonymousLogger().info("created AAAA record: " + aaaaRecord1);
      try {
         api(zoneName).addAAAARecordWithAddressAndTTL(aaaaPoolId, "2001:0DB8:85A3:0000:0000:8A2E:0370:7334", 1);
         fail();
      } catch (ResourceAlreadyExistsException e) {

      }
   }

   @Test(dependsOnMethods = "addAAAARecordToPool")
   public void testDeleteAAAAPool() {
      api(zoneName).delete(aaaaPoolId);
      assertFalse(getPoolById(aaaaPoolId).isPresent());
   }

   protected Optional<RoundRobinPool> getPoolById(final String poolId) {
      return api(zoneName).list().firstMatch(new Predicate<RoundRobinPool>() {
         public boolean apply(RoundRobinPool in) {
            return in.getId().equals(poolId);
         }
      });
   }

   private FluentIterable<ResourceRecord> listRRs(String poolId) {
      return api(zoneName).listRecords(poolId).transform(ResourceRecordApiLiveTest.toRecord);
   }

   private RoundRobinPoolApi api(String zoneName) {
      return api.getRoundRobinPoolApiForZone(zoneName);
   }
}
