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
import static org.jclouds.ultradns.ws.domain.Zone.Type.PRIMARY;
import static org.jclouds.ultradns.ws.predicates.ZonePredicates.typeEqualTo;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.ultradns.ws.UltraDNSWSExceptions.ResourceAlreadyExistsException;
import org.jclouds.ultradns.ws.domain.Zone;
import org.jclouds.ultradns.ws.domain.Zone.Type;
import org.jclouds.ultradns.ws.domain.ZoneProperties;
import org.jclouds.ultradns.ws.internal.BaseUltraDNSWSApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "ZoneApiLiveTest")
public class ZoneApiLiveTest extends BaseUltraDNSWSApiLiveTest {

   private void checkZone(Zone zone) {
      assertNotNull(zone.getId(), "Id cannot be null for " + zone);
      assertNotNull(zone.getName(), "Name cannot be null for " + zone);
      assertNotNull(zone.getType(), "Type cannot be null for " + zone);
      assertNotNull(zone.getTypeCode(), "TypeCode cannot be null for " + zone);
      assertEquals(zone.getTypeCode(), zone.getType().getCode());
      assertNotNull(zone.getAccountId(), "AccountId cannot be null for " + zone);
      assertEquals(zone.getAccountId(), account.getId());
      assertNotNull(zone.getOwnerId(), "OwnerId cannot be null for " + zone);
      assertNotNull(zone.getDNSSECStatus(), "DNSSECStatus cannot be null for " + zone);
      assertNotNull(zone.getPrimarySrc(), "While PrimarySrc can be null, its Optional wrapper cannot " + zone);
   }

   @Test
   public void testListZonesByAccount() {
      FluentIterable<Zone> response = api().listByAccount(account.getId());

      for (Zone zone : response) {
         checkZone(zone);
      }

      if (response.anyMatch(typeEqualTo(PRIMARY))) {
         assertEquals(api().listByAccountAndType(account.getId(), PRIMARY).toSet(), response
               .filter(typeEqualTo(PRIMARY)).toSet());
      }
   }

   @Test
   public void testListZonesByAccountWhenAccountIdNotFound() {
      assertTrue(api().listByAccount("AAAAAAAAAAAAAAAA").isEmpty());
   }

   @Test
   public void testGetZone() {
      for (Zone zone : api().listByAccount(account.getId())) {
         ZoneProperties zoneProperties = api().get(zone.getName());
         assertEquals(zoneProperties.getName(), zone.getName());
         assertEquals(zoneProperties.getType(), zone.getType());
         assertEquals(zoneProperties.getTypeCode(), zone.getTypeCode());
         assertNotNull(zoneProperties.getModified(), "Modified cannot be null for " + zone);
         assertTrue(zoneProperties.getResourceRecordCount() >= 0,
               "ResourceRecordCount must be positive or zero for a Zone " + zone);
      }
   }

   @Test
   public void testGetZoneWhenNotFound() {
      assertNull(api().get("AAAAAAAAAAAAAAAA"));
   }

   @Test
   public void testDeleteZoneWhenNotFound() {
      api().delete("AAAAAAAAAAAAAAAA");
   }

   @Test(expectedExceptions = ResourceNotFoundException.class, expectedExceptionsMessageRegExp = "Account not found in the system. ID: AAAAAAAAAAAAAAAA")
   public void testCreateZoneBadAccountId() {
      api().createInAccount(zoneName, "AAAAAAAAAAAAAAAA");
   }

   @Test
   public void testCreateAndDeleteZone() {
      try {
         api().createInAccount(zoneName, account.getId());
         ZoneProperties newZone = api().get(zoneName);
         getAnonymousLogger().info("created zone: " + newZone);

         try {
            api().createInAccount(zoneName, account.getId());
            fail();
         } catch (ResourceAlreadyExistsException e) {

         }

         assertEquals(newZone.getName(), zoneName);
         assertEquals(newZone.getType(), Type.PRIMARY);
         assertEquals(newZone.getTypeCode(), Type.PRIMARY.getCode());
         assertNotNull(newZone.getModified(), "Modified cannot be null for " + newZone);
         assertEquals(newZone.getResourceRecordCount(), 5);
      } finally {
         api().delete(zoneName);
      }
   }

   protected ZoneApi api() {
      return api.getZoneApi();
   }
}
