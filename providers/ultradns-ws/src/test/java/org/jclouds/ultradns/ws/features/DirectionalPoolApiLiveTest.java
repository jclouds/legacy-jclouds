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
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static java.util.logging.Logger.getAnonymousLogger;
import static org.jclouds.ultradns.ws.domain.DirectionalPool.RecordType.IPV4;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.EnumSet;
import java.util.Set;

import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.ultradns.ws.UltraDNSWSExceptions.DirectionalGroupOverlapException;
import org.jclouds.ultradns.ws.UltraDNSWSExceptions.ResourceAlreadyExistsException;
import org.jclouds.ultradns.ws.domain.DirectionalGroup;
import org.jclouds.ultradns.ws.domain.DirectionalPool;
import org.jclouds.ultradns.ws.domain.DirectionalPool.RecordType;
import org.jclouds.ultradns.ws.domain.DirectionalPool.TieBreak;
import org.jclouds.ultradns.ws.domain.DirectionalPool.Type;
import org.jclouds.ultradns.ws.domain.DirectionalPoolRecord;
import org.jclouds.ultradns.ws.domain.DirectionalPoolRecordDetail;
import org.jclouds.ultradns.ws.domain.IdAndName;
import org.jclouds.ultradns.ws.domain.Zone;
import org.jclouds.ultradns.ws.internal.BaseDirectionalApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "DirectionalPoolApiLiveTest")
public class DirectionalPoolApiLiveTest extends BaseDirectionalApiLiveTest {

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

   Set<IdAndName> allDirectionalGroups = newLinkedHashSet();

   @Test
   public void testListDirectionalRecords() {
      for (Zone zone : api.getZoneApi().listByAccount(account.getId())) {
         for (DirectionalPool pool : api(zone.getName()).list()) {
            for (RecordType type : EnumSet.allOf(RecordType.class)) {
               for (DirectionalPoolRecordDetail rr : api(zone.getName())
                     .listRecordsByDNameAndType(pool.getDName(), type.getCode())) {
                  checkDirectionalRecordDetail(rr);
                  Iterable<IdAndName> groups = Optional.presentInstances(ImmutableSet.of(rr.getGroup(),
                        rr.getGeolocationGroup(), rr.getGeolocationGroup()));
                  assertFalse(isEmpty(groups), "No groups " + rr);
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

   static DirectionalPoolRecordDetail checkDirectionalRecordDetail(DirectionalPoolRecordDetail rr) {
      assertNotNull(rr.getZoneName(), "ZoneName cannot be null " + rr);
      assertNotNull(rr.getName(), "DName cannot be null " + rr);
      assertNotNull(rr.getId(), "Id cannot be null " + rr);
      assertNotNull(rr.getZoneName(), "ZoneName cannot be null " + rr);
      checkDirectionalRecord(rr.getRecord());
      return rr;
   }

   @Test(expectedExceptions = ResourceNotFoundException.class, expectedExceptionsMessageRegExp = "Parent Zone does not exist in the system.")
   public void testListDirectionalsWhenZoneIdNotFound() {
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

   @Test(expectedExceptions = ResourceNotFoundException.class, expectedExceptionsMessageRegExp = "Directional Pool Record does not exist in the system")
   public void testUpdateRecordWhenNotFound() {
      api(zoneName).updateRecord("06063D9C54C5AE09", cnameRecordCanary);
   }

   @Test
   public void testCreateCNAMEPool() {
      cnamePoolId = api(zoneName).createForDNameAndType("Geo pool", dname, IPV4.getCode());
      getAnonymousLogger().info("created Geo pool: " + cnamePoolId);
      Optional<DirectionalPool> ipv4Pool = getPoolById(cnamePoolId);
      assertTrue(ipv4Pool.isPresent());
      assertEquals(ipv4Pool.get().getZoneId(), zoneId);
      assertEquals(ipv4Pool.get().getName().get(), "Geo pool");
      assertEquals(ipv4Pool.get().getDName(), dname);
      assertEquals(ipv4Pool.get().getType(), Type.GEOLOCATION);
      assertEquals(ipv4Pool.get().getTieBreak(), TieBreak.GEOLOCATION);
   }

   @Test(dependsOnMethods = "testCreateCNAMEPool", expectedExceptions = ResourceAlreadyExistsException.class)
   public void testDuplicateCreateCNAMEPool() {
      api(zoneName).createForDNameAndType("Geo pool", dname, IPV4.getCode());
   }

   @Test(dependsOnMethods = "testDuplicateCreateCNAMEPool")
   public void addCNAMERecordsToPool() {
      cnameEU = api(zoneName).addRecordIntoNewGroup(cnamePoolId, cnameRecordEU, eu);
      getAnonymousLogger().info("created CNAME record in ipv4 pool: " + cnameEU);
      checkRecordConsistent(dname, cnameEU, cnameRecordEU, eu);

      cnameUS = api(zoneName).addRecordIntoNewGroup(cnamePoolId, cnameRecordUS, us);
      getAnonymousLogger().info("created CNAME record in ipv4 pool: " + cnameUS);
      checkRecordConsistent(dname, cnameUS, cnameRecordUS, us);

      cnameCanary = api(zoneName).addRecordIntoNewGroup(cnamePoolId, cnameRecordCanary, nebraska);
      getAnonymousLogger().info("created CNAME record in ipv4 pool: " + cnameCanary);
      checkRecordConsistent(dname, cnameCanary, cnameRecordCanary, nebraska);
   }

   @Test(dependsOnMethods = "addCNAMERecordsToPool", expectedExceptions = ResourceAlreadyExistsException.class)
   public void testDuplicateAddCNAMERecordsToPool() {
      api(zoneName).addRecordIntoNewGroup(cnamePoolId, cnameRecordEU, eu);
   }

   @Test(dependsOnMethods = "testDuplicateAddCNAMERecordsToPool")
   public void testUpdateRecordTTL() {
      cnameRecordCanary = cnameRecordCanary.toBuilder().ttl(180).build();
      api(zoneName).updateRecord(cnameCanary, cnameRecordCanary);
      getAnonymousLogger().info("updated CNAME record TTL in ipv4 pool: " + cnameCanary);
      checkRecordConsistent(dname, cnameCanary, cnameRecordCanary, nebraska);
   }
   
   @Test(dependsOnMethods = "testUpdateRecordTTL", expectedExceptions = DirectionalGroupOverlapException.class)
   public void testUpdateGroupWithOverlappingTerritories() {
      DirectionalGroup withUtah = nebraska.toBuilder().mapRegionToTerritory(REGION_US, "Utah").build();
      checkGroupByDNameAndIdContainsTerritory(dname, cnameUS, "Utah");
      try {
         api(zoneName).updateRecordAndGroup(cnameCanary, cnameRecordCanary, withUtah);
      } finally {
         checkRecordConsistent(dname, cnameCanary, cnameRecordCanary, nebraska);
      }
   }

   @Test(dependsOnMethods = "testUpdateGroupWithOverlappingTerritories")
   public void testUpdateGroupWithLessTerritories() {
      Multimap<String, String> minusUtah = ImmutableMultimap.<String, String>builder()
                  .putAll("United States (US)", filter(us.get(REGION_US), not(equalTo("Utah"))))
                  .build();
      
      api(zoneName).updateRecordAndGroup(cnameUS, cnameRecordUS, us.toBuilder()
                                                                   .regionToTerritories(minusUtah)
                                                                   .build());

      checkGroupByDNameAndIdDoesntContainTerritory(dname, cnameUS, "Utah");
   }

   @Test(dependsOnMethods = "testUpdateGroupWithLessTerritories")
   public void testUpdateGroupWithMoreTerritories() {
      DirectionalGroup withUtah = nebraska.toBuilder().mapRegionToTerritory(REGION_US, "Utah").build();
      api(zoneName).updateRecordAndGroup(cnameCanary, cnameRecordCanary, withUtah);
      getAnonymousLogger().info("update CNAME record in ipv4 pool: " + cnameCanary);

      checkRecordConsistent(dname, cnameCanary, cnameRecordCanary, withUtah);

      checkGroupByDNameAndIdContainsTerritory(dname, cnameCanary, "Nebraska");
      checkGroupByDNameAndIdContainsTerritory(dname, cnameCanary, "Utah");
   }

   @Test(dependsOnMethods = "testUpdateGroupWithMoreTerritories")
   public void testAddRecordIntoGeoGroup() {
      String geoGroupId = getRecordByDNameAndId(dname, cnameCanary).get().getGeolocationGroup().get().getId();
      cname2Canary = api(zoneName).addRecordIntoExistingGroup(cnamePoolId, cname2RecordCanary, geoGroupId);
      getAnonymousLogger().info("created CNAME record in ipv4 pool: " + cname2Canary);

      DirectionalPoolRecordDetail detail = 
            checkRecordConsistentInNonConfiguredGroup(dname, cname2Canary, cname2RecordCanary);
      assertEquals(detail.getGroup().get().getId(), geoGroupId);
   }

   @Test(dependsOnMethods = "testAddRecordIntoGeoGroup")
   public void testDeleteRecord() {
      api(zoneName).deleteRecord(cnameEU);
      assertFalse(getRecordByDNameAndId(dname, cnameEU).isPresent());
      assertTrue(getRecordByDNameAndId(dname, cnameUS).isPresent());
   }

   @Test
   public void addRecordsWithUnconfiguredGroupToPool() {
      aPoolId = api(zoneName).createForDNameAndType("Geo pool", "a-" + dname, IPV4.getCode());
      getAnonymousLogger().info("created Geo pool: " + aPoolId);
      a1Prod = api(zoneName).addFirstRecordInNonConfiguredGroup(aPoolId, a1RecordProd);
      getAnonymousLogger().info("created A record in ipv4 pool: " + a1Prod);

      checkRecordConsistentInNonConfiguredGroup("a-" + dname, a1Prod, a1RecordProd);
      checkGroupByDNameAndIdContainsTerritory("a-" + dname, a1Prod, "Nebraska");

      a1Canary = api(zoneName).addRecordIntoNewGroup(aPoolId, a1RecordCanary, nebraska);
      getAnonymousLogger().info("created A record in ipv4 pool: " + a1Canary);

      checkRecordConsistent("a-" + dname, a1Canary, a1RecordCanary, nebraska);
      checkGroupByDNameAndIdContainsTerritory("a-" + dname, a1Canary, "Nebraska");
      checkGroupByDNameAndIdDoesntContainTerritory("a-" + dname, a1Prod, "Nebraska");
   }

   @Test(dependsOnMethods = "addRecordsWithUnconfiguredGroupToPool", expectedExceptions = ResourceAlreadyExistsException.class)
   public void addDuplicateFirstRecordInNonConfiguredGroup() {
      api(zoneName).addFirstRecordInNonConfiguredGroup(aPoolId, a1RecordProd);
   }

   @Test(dependsOnMethods = "addRecordsWithUnconfiguredGroupToPool", expectedExceptions = ResourceAlreadyExistsException.class)
   public void addDuplicateRecordIntoNewGroup() {
      api(zoneName).addRecordIntoNewGroup(aPoolId, a1RecordCanary, nebraska);
   }

   @Test(dependsOnMethods = { "addDuplicateFirstRecordInNonConfiguredGroup", "addRecordsWithUnconfiguredGroupToPool" })
   public void testRemovingAddsTerritoriesBackIntoNonConfiguredGroup() {
      api(zoneName).deleteRecord(a1Canary);
      assertFalse(getRecordByDNameAndId("a-" + dname, a1Canary).isPresent());
      checkGroupByDNameAndIdContainsTerritory("a-" + dname, a1Prod, "Nebraska");
   }

   @Test(dependsOnMethods = { "testDeleteRecord", "testRemovingAddsTerritoriesBackIntoNonConfiguredGroup" })
   public void testDeletePool() {
      api(zoneName).delete(cnamePoolId);
      assertFalse(getPoolById(cnamePoolId).isPresent());
      api(zoneName).delete(aPoolId);
      assertFalse(getPoolById(aPoolId).isPresent());
   }

   private DirectionalPoolRecordDetail checkRecordConsistent(String dname, String recordId,
         DirectionalPoolRecord record, DirectionalGroup group) {
      DirectionalPoolRecordDetail recordDetail = getRecordByDNameAndId(dname, recordId).get();
      checkDirectionalRecordDetail(recordDetail);
      IdAndName rGroup = recordDetail.getGeolocationGroup().get();
      assertEquals(rGroup.getName(), group.getName());
      // TODO: look up each key with all and do a comparison
      if (!group.containsValue("all"))
         assertEquals(groupApi().get(rGroup.getId()), group);
      assertFalse(recordDetail.getGroup().isPresent());
      assertFalse(recordDetail.getSourceIpGroup().isPresent());
      assertEquals(recordDetail.getName(), dname);
      assertEquals(recordDetail.getZoneName(), zoneName);
      assertEquals(recordDetail.getRecord(), record);
      return recordDetail;
   }

   private DirectionalPoolRecordDetail checkRecordConsistentInNonConfiguredGroup(String dname, String recordId,
         DirectionalPoolRecord record) {
      DirectionalPoolRecordDetail recordDetail = getRecordByDNameAndId(dname, recordId).get();
      checkDirectionalRecordDetail(recordDetail);
      IdAndName rGroup = recordDetail.getGroup().get();
      assertEquals(rGroup.getName(), "All Non-Configured Regions");
      DirectionalGroup allNonConfigured = groupApi().get(rGroup.getId());
      assertEquals(allNonConfigured.getName(), "All Non-Configured Regions");
      assertEquals(allNonConfigured.size(), 323);
      assertFalse(recordDetail.getGeolocationGroup().isPresent());
      assertFalse(recordDetail.getSourceIpGroup().isPresent());
      assertEquals(recordDetail.getZoneName(), zoneName);
      assertEquals(recordDetail.getRecord(), record);
      return recordDetail;
   }

   private DirectionalPoolApi api(String zoneName) {
      return api.getDirectionalPoolApiForZone(zoneName);
   }

   private DirectionalGroupApi groupApi() {
      return api.getDirectionalGroupApiForAccount(account.getId());
   }
}
