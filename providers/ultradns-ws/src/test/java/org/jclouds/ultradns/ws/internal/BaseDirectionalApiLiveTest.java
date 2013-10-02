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
package org.jclouds.ultradns.ws.internal;

import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Multimaps.filterKeys;
import static org.jclouds.ultradns.ws.domain.DirectionalPool.RecordType.IPV4;
import static org.jclouds.ultradns.ws.predicates.DirectionalPoolPredicates.recordIdEqualTo;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.jclouds.ultradns.ws.domain.DirectionalGroup;
import org.jclouds.ultradns.ws.domain.DirectionalPool;
import org.jclouds.ultradns.ws.domain.DirectionalPoolRecord;
import org.jclouds.ultradns.ws.domain.DirectionalPoolRecordDetail;
import org.jclouds.ultradns.ws.domain.IdAndName;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Multimap;

/**
 * @author Adrian Cole
 */
@Test(groups = "live")
public class BaseDirectionalApiLiveTest extends BaseUltraDNSWSApiLiveTest {

   protected String dname = "www." + zoneName;

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setup() {
      super.setup();
      setupGroups();
      createZone();
   }

   protected String cnamePoolId;
   
   protected String cnameEU;
   protected DirectionalGroup eu = null;
   protected DirectionalPoolRecord cnameRecordEU = DirectionalPoolRecord.drBuilder()
                                                                        .type("CNAME")
                                                                        .ttl(8600)
                                                                        .rdata("eu." + dname).build();

   /**
    * lower ttl as plan to accept territories from canary
    */
   protected String cnameUS;
   protected DirectionalGroup us = null;
   protected DirectionalPoolRecord cnameRecordUS = DirectionalPoolRecord.drBuilder()
                                                                        .type("CNAME")
                                                                        .ttl(300)
                                                                        .rdata("us." + dname).build();

   /**
    * lower ttl as plan to move territories into recordUS
    */
   /**
    * a test state.
    */
   protected String cnameCanary;
   protected DirectionalGroup nebraska = null;
   protected DirectionalPoolRecord cnameRecordCanary = DirectionalPoolRecord.drBuilder()
                                                                            .type("CNAME")
                                                                            .ttl(300)
                                                                            .rdata("canary." + dname).build();
   protected String cname2Canary;
   protected DirectionalPoolRecord cname2RecordCanary = cnameRecordCanary.toBuilder().rdata("parrot." + dname).build();
   
   protected String aPoolId;

   /**
    * Uses all non-configured group to support all clients
    */
   protected String a1Prod;
   protected DirectionalPoolRecord a1RecordProd = DirectionalPoolRecord.drBuilder()
                                                                       .type("A")
                                                                       .ttl(300)
                                                                       .rdata("1.1.0.1").build();
   /**
    * contains territories currently being tested.
    */
   protected String a1Canary;
   protected DirectionalPoolRecord a1RecordCanary = DirectionalPoolRecord.drBuilder()
                                                                         .type("A")
                                                                         .ttl(300)
                                                                         .rdata("1.1.1.1").build();

   public static final String REGION_US = "United States (US)";

   void setupGroups() {
      // all territories in EU
      eu = DirectionalGroup.builder()
                           .name("EU")
                           .description("Clients we classify as being in Europe")
                           .mapRegion("Europe").build();
      nebraska = DirectionalGroup.builder()
                               .name("Canary")
                               .description("Clients who are testing our service")
                               .mapRegionToTerritory(REGION_US, "Nebraska").build();

      // in order to pick certain territories, we need to know what they are
      Multimap<IdAndName, String> availableRegions = api.getRegionsByIdAndName();

      // find the us Territories
      Iterable<String> usTerritories = filterKeys(availableRegions, 
            IdAndName.nameEqualTo(REGION_US)).values();

      us = DirectionalGroup.builder()
                           .name("US")
                           .description("Clients we classify as being in US")
                           .mapRegionToTerritories(REGION_US, 
                                 filter(usTerritories, not(equalTo("Nebraska")))).build();
   }

   protected void checkGroupByDNameAndIdContainsTerritory(String dname, String poolRecordId, String territory) {
      DirectionalGroup regions = getGroup(dname, poolRecordId);
      assertTrue(regions.values().contains(territory), poolRecordId + " doesn't contain " + territory);
   }

   protected void checkGroupByDNameAndIdDoesntContainTerritory(String dname, String poolRecordId, String territory) {
      DirectionalGroup regions = getGroup(dname, poolRecordId);
      assertFalse(regions.values().contains(territory), poolRecordId + " contains " + territory);
   }

   /**
    * gets the geo group or the non-configured group.
    */
   private DirectionalGroup getGroup(String dname, String poolRecordId) {
      DirectionalPoolRecordDetail record = getRecordByDNameAndId(dname, poolRecordId).get();
      return api.getDirectionalGroupApiForAccount(account.getId())
                .get(record.getGeolocationGroup().or(record.getGroup()).get().getId());
   }

   protected Optional<DirectionalPool> getPoolById(final String cnamePoolId) {
      return api.getDirectionalPoolApiForZone(zoneName)
                .list()
                .firstMatch(new Predicate<DirectionalPool>() {
                     public boolean apply(DirectionalPool in) {
                        return in.getId().equals(cnamePoolId);
                     }
                  });
   }

   protected Optional<DirectionalPoolRecordDetail> getRecordByDNameAndId(String dname, String recordId) {
      return api.getDirectionalPoolApiForZone(zoneName)
                .listRecordsByDNameAndType(dname, IPV4.getCode())
                .firstMatch(recordIdEqualTo(recordId));
   }

   @Override
   @AfterClass(groups = { "integration", "live" })
   protected void tearDown() {
      if (cnamePoolId != null)
         api.getDirectionalPoolApiForZone(zoneName).delete(cnamePoolId);
      if (aPoolId != null)
         api.getDirectionalPoolApiForZone(zoneName).delete(aPoolId);
      super.tearDown();
   }
}
