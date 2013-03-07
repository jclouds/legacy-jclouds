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
import static org.testng.Assert.assertTrue;

import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.ultradns.ws.domain.Account;
import org.jclouds.ultradns.ws.domain.LBPool;
import org.jclouds.ultradns.ws.domain.LBPool.Type;
import org.jclouds.ultradns.ws.domain.PoolRecord;
import org.jclouds.ultradns.ws.domain.Zone;
import org.jclouds.ultradns.ws.internal.BaseUltraDNSWSApiLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "LBPoolApiLiveTest")
public class LBPoolApiLiveTest extends BaseUltraDNSWSApiLiveTest {

   private Account account;

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      account = context.getApi().getCurrentAccount();
   }

   private void checkLBPool(LBPool pool) {
      checkNotNull(pool.getZoneId(), "ZoneId cannot be null for a LBPool %s", pool);
      checkNotNull(pool.getId(), "Id cannot be null for a LBPool %s", pool);
      checkNotNull(pool.getName(), "Name cannot be null for a LBPool %s", pool);
      checkNotNull(pool.getType(), "Type cannot be null for a LBPool %s", pool);
      checkNotNull(pool.getResponseMethod(), "ResponseMethod cannot be null for a LBPool %s", pool);
   }

   private void checkPoolRecord(PoolRecord record) {
      checkNotNull(record.getPoolId(), "PoolId cannot be null for a PoolRecord %s", record);
      checkNotNull(record.getId(), "Id cannot be null for a PoolRecord %s", record);
      checkNotNull(record.getDescription(), "Description cannot be null for a PoolRecord %s", record);
      checkNotNull(record.getPointsTo(), "PointsTo cannot be null for a PoolRecord %s", record);
      checkNotNull(record.getType(), "Type cannot be null for a PoolRecord %s", record);
   }

   @Test
   public void testListLBPools() {
      for (Zone zone : context.getApi().getZoneApi().listByAccount(account.getId())) {
         for (LBPool pool : api(zone.getName()).list()) {
            checkLBPool(pool);
         }
      }
   }

   @Test
   public void testListLBPoolsByType() {
      for (Zone zone : context.getApi().getZoneApi().listByAccount(account.getId())) {
         for (LBPool pool : api(zone.getName()).list()) {
            assertTrue(api(zone.getName()).listByType(pool.getType()).contains(pool));
            break;
         }
      }
   }

   @Test
   public void testListLBPoolRecords() {
      for (Zone zone : context.getApi().getZoneApi().listByAccount(account.getId())) {
         for (LBPool pool : api(zone.getName()).listByType(Type.RR)) {
            for (PoolRecord record : api(zone.getName()).listRecords(pool.getId())) {
               checkPoolRecord(record);
            }
         }
      }
   }

   @Test(expectedExceptions = ResourceNotFoundException.class, expectedExceptionsMessageRegExp = "Zone does not exist in the system.")
   public void testListLBPoolsWhenZoneIdNotFound() {
      api("AAAAAAAAAAAAAAAA").list();
   }

   private LBPoolApi api(String zoneName) {
      return context.getApi().getLBPoolApiForZone(zoneName);
   }
}
