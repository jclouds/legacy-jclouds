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
import static org.jclouds.ultradns.ws.domain.Zone.Type.PRIMARY;
import static org.jclouds.ultradns.ws.predicates.ZonePredicates.typeEquals;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.*;

import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.ultradns.ws.domain.Account;
import org.jclouds.ultradns.ws.domain.Zone;
import org.jclouds.ultradns.ws.domain.ZoneProperties;
import org.jclouds.ultradns.ws.internal.BaseUltraDNSWSApiLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "ZoneApiLiveTest")
public class ZoneApiLiveTest extends BaseUltraDNSWSApiLiveTest {

   private Account account;

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      account = context.getApi().getCurrentAccount();
   }

   private void checkZone(Zone zone) {
      checkNotNull(zone.getId(), "Id cannot be null for a Zone %s", zone);
      checkNotNull(zone.getName(), "Name cannot be null for a Zone %s", zone);
      checkNotNull(zone.getType(), "Type cannot be null for a Zone %s", zone);
      assertTrue(zone.getTypeCode() > 0, "TypeCode must be positive for a Zone " + zone);
      assertEquals(zone.getTypeCode(), zone.getType().getCode());
      checkNotNull(zone.getAccountId(), "AccountId cannot be null for a Zone %s", zone);
      assertEquals(zone.getAccountId(), account.getId());
      checkNotNull(zone.getOwnerId(), "OwnerId cannot be null for a Zone %s", zone);
      checkNotNull(zone.getDNSSECStatus(), "DNSSECStatus cannot be null for a Zone %s", zone);
      checkNotNull(zone.getPrimarySrc(), "While PrimarySrc can be null for a Zone, its Optional wrapper cannot %s",
            zone);
   }

   @Test
   public void testListZonesByAccount() {
      FluentIterable<Zone> response = api().listByAccount(account.getId());

      for (Zone zone : response) {
         checkZone(zone);
      }

      if (response.anyMatch(typeEquals(PRIMARY))) {
         assertEquals(api().listByAccountAndType(account.getId(), PRIMARY).toSet(), response
               .filter(typeEquals(PRIMARY)).toSet());
      }
   }

   @Test(expectedExceptions = ResourceNotFoundException.class, expectedExceptionsMessageRegExp = "Account not found in the system. ID: AAAAAAAAAAAAAAAA")
   public void testListZonesByAccountWhenAccountIdNotFound() {
      api().listByAccount("AAAAAAAAAAAAAAAA");
   }

   @Test
   public void testGetZone() {
      for (Zone zone : api().listByAccount(account.getId())) {
         ZoneProperties zoneProperties = api().get(zone.getName());
         assertEquals(zoneProperties.getName(), zone.getName());
         assertEquals(zoneProperties.getType(), zone.getType());
         assertEquals(zoneProperties.getTypeCode(), zone.getTypeCode());
         checkNotNull(zoneProperties.getModified(), "Modified cannot be null for a Zone %s", zone);
         assertTrue(zoneProperties.getResourceRecordCount() >= 0, "ResourceRecordCount must be positive or zero for a Zone " + zone);
      }
   }

   @Test
   public void testGetZoneWhenNotFound() {
      assertNull(api().get("AAAAAAAAAAAAAAAA"));
   }

   protected ZoneApi api() {
      return context.getApi().getZoneApi();
   }
}
