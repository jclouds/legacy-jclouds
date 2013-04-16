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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.EnumSet;
import java.util.Set;

import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.ultradns.ws.domain.DirectionalPool;
import org.jclouds.ultradns.ws.domain.DirectionalPool.RecordType;
import org.jclouds.ultradns.ws.domain.DirectionalPoolRecord;
import org.jclouds.ultradns.ws.domain.DirectionalPoolRecordDetail;
import org.jclouds.ultradns.ws.domain.IdAndName;
import org.jclouds.ultradns.ws.domain.Zone;
import org.jclouds.ultradns.ws.internal.BaseUltraDNSWSApiLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "DirectionalPoolApiLiveTest")
public class DirectionalPoolApiLiveTest extends BaseUltraDNSWSApiLiveTest {

   private IdAndName account;

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setup() {
      super.setup();
      account = api.getCurrentAccount();
   }

   @Test
   public void testListDirectionalPools() {
      for (Zone zone : api.getZoneApi().listByAccount(account.getId())) {
         for (DirectionalPool pool : api(zone.getName()).list()) {
            checkDirectional(pool);
         }
      }
   }

   private void checkDirectional(DirectionalPool pool) {
      assertNotNull(pool.getZoneId(), "ZoneId cannot be null " + pool);
      assertNotNull(pool.getId(), "Id cannot be null " + pool);
      assertNotNull(pool.getDName(), "DName cannot be null " + pool);
      assertNotNull(pool.getName(), "Name cannot be null " + pool);
      assertNotNull(pool.getType(), "Type cannot be null " + pool);
      assertNotNull(pool.getTieBreak(), "TieBreak cannot be null " + pool);
   }

   Set<IdAndName> allDirectionalGroups = Sets.newLinkedHashSet();

   @Test
   public void testListDirectionalRecords() {
      for (Zone zone : api.getZoneApi().listByAccount(account.getId())) {
         for (DirectionalPool pool : api(zone.getName()).list()) {
            for (RecordType type : EnumSet.allOf(RecordType.class)) {
               for (DirectionalPoolRecordDetail rr : api(zone.getName())
                     .listRecordsByNameAndType(pool.getDName(), type.getCode())) {
                  checkDirectionalRecordDetail(rr);
                  Iterable<IdAndName> groups = Optional.presentInstances(ImmutableSet.of(rr.getGroup(),
                        rr.getGeolocationGroup(), rr.getGeolocationGroup()));
                  assertFalse(Iterables.isEmpty(groups), "No groups " + rr);
                  for (IdAndName group : groups) {
                     allDirectionalGroups.add(group);
                     assertNotNull(group.getId(), "Id cannot be null " + group);
                     assertNotNull(group.getName(), "Name cannot be null " + group);
                  }
                  assertEquals(rr.getZoneName(), zone.getName());
                  assertEquals(rr.getName(), pool.getDName());
                  switch (pool.getType()) {
                  case GEOLOCATION:
                     assertNotNull(rr.getGeolocationGroup().or(rr.getGroup()).orNull(),
                           "GeolocationGroup or Group must be present " + rr);
                     assertNull(rr.getSourceIpGroup().orNull(), "SourceIpGroup must be absent " + rr);
                     break;
                  case SOURCEIP:
                     assertNotNull(rr.getSourceIpGroup().orNull(), "SourceIpGroup must be present " + rr);
                     assertNull(rr.getGeolocationGroup().orNull(), "GeolocationGroup must be absent " + rr);
                     break;
                  case MIXED:
                     assertNotNull(rr.getGeolocationGroup().or(rr.getSourceIpGroup()).or(rr.getGroup()).orNull(),
                           "GeolocationGroup, SourceIpGroup or Group must be present " + rr);
                     break;
                  }
               }
            }
         }
      }
   }

   static void checkDirectionalRecord(DirectionalPoolRecord rr) {
      assertNotNull(rr.getType(), "Type cannot be null " + rr);
      assertNotNull(rr.getTTL(), "TTL cannot be null " + rr);
      assertNotNull(rr.getRData(), "InfoValues cannot be null " + rr);
   }

   static void checkDirectionalRecordDetail(DirectionalPoolRecordDetail rr) {
      assertNotNull(rr.getZoneName(), "ZoneName cannot be null " + rr);
      assertNotNull(rr.getName(), "DName cannot be null " + rr);
      assertNotNull(rr.getId(), "Id cannot be null " + rr);
      assertNotNull(rr.getZoneName(), "ZoneName cannot be null " + rr);
      checkDirectionalRecord(rr.getRecord());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class, expectedExceptionsMessageRegExp = "Parent Zone does not exist in the system.")
   public void testListDirectionalsWhenZoneIdNotFound() {
      api("AAAAAAAAAAAAAAAA").list();
   }

   private DirectionalPoolApi api(String zoneName) {
      return api.getDirectionalPoolApiForZone(zoneName);
   }
}
