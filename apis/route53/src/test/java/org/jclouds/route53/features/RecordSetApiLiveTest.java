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
package org.jclouds.route53.features;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.or;
import static java.util.logging.Logger.getAnonymousLogger;
import static org.jclouds.route53.domain.ChangeBatch.createAll;
import static org.jclouds.route53.domain.ChangeBatch.deleteAll;
import static org.jclouds.route53.domain.RecordSet.Type.NS;
import static org.jclouds.route53.domain.RecordSet.Type.SOA;
import static org.jclouds.route53.domain.RecordSet.Type.TXT;
import static org.jclouds.route53.predicates.RecordSetPredicates.typeEquals;
import static org.jclouds.route53.predicates.ZonePredicates.nameEquals;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.jclouds.JcloudsVersion;
import org.jclouds.collect.PagedIterable;
import org.jclouds.route53.domain.Change;
import org.jclouds.route53.domain.NewZone;
import org.jclouds.route53.domain.RecordSet;
import org.jclouds.route53.domain.RecordSet.RecordSubset;
import org.jclouds.route53.domain.RecordSet.RecordSubset.Latency;
import org.jclouds.route53.domain.RecordSet.RecordSubset.Weighted;
import org.jclouds.route53.domain.Zone;
import org.jclouds.route53.internal.BaseRoute53ApiLiveTest;
import org.testng.SkipException;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "ResourceRecordSetApiLiveTest")
public class RecordSetApiLiveTest extends BaseRoute53ApiLiveTest {

   private void checkRRS(RecordSet rrs) {
      checkNotNull(rrs.getName(), "Name: ResourceRecordSet %s", rrs);
      checkNotNull(rrs.getType(), "Type: ResourceRecordSet %s", rrs);
      checkNotNull(rrs.getTTL(), "TTL: ResourceRecordSet %s", rrs);
      checkNotNull(rrs.getAliasTarget(), "AliasTarget: ResourceRecordSet %s", rrs);
      if (rrs.getAliasTarget().isPresent()) {
         assertTrue(rrs.getValues().isEmpty(), "Values present on aliasTarget ResourceRecordSet: " + rrs);
      } else {
         assertTrue(!rrs.getValues().isEmpty(), "Values absent on ResourceRecordSet: " + rrs);
      }
      if (rrs instanceof RecordSubset) {
         checkNotNull(RecordSubset.class.cast(rrs).getId(), "Id: ResourceRecordSubset %s", rrs);
      }
      if (rrs instanceof Weighted) {
         assertTrue(Weighted.class.cast(rrs).getWeight() >= 0, "Weight negative: ResourceRecordSubset " + rrs);
      }
      if (rrs instanceof Latency) {
         checkNotNull(Latency.class.cast(rrs).getRegion(), "Region: ResourceRecordSubset %s", rrs);
      }
   }

   @Test
   private void testListRRSs() {
      for (Zone zone : zones().concat()) {
         checkAllRRs(zone.getId());
      }
   }

   private void checkAllRRs(String zoneId) {
      Zone zone = context.getApi().getZoneApi().get(zoneId).getZone();
      List<RecordSet> records = api(zone.getId()).list().concat().toList();
      assertEquals(zone.getResourceRecordSetCount(), records.size());

      for (RecordSet rrs : records) {
         checkRRS(rrs);
      }
   }

   @Test
   public void testDeleteRRSNotFound() {
      for (Zone zone : zones().concat()) {
         assertNull(api(zone.getId()).delete(
               RecordSet.builder().name("krank.foo.bar.").type(TXT).add("kranko").build()));
      }
   }

   /**
    * cannot delete a zone without at least one of each
    */
   private static final Predicate<RecordSet> requiredRRTypes = or(typeEquals(SOA), typeEquals(NS));

   @Test
   public void testCreateAndDeleteBulkRecords() {
      String name = System.getProperty("user.name").replace('.', '-') + ".bulk.route53test.jclouds.org.";
      clearAndDeleteZonesNamed(name);

      ImmutableList<RecordSet> records = ImmutableList.<RecordSet> builder()
            .add(RecordSet.builder().name("dom1." + name).type(TXT).add("\"somehow\" \" somewhere\"").build())
            .add(RecordSet.builder().name("dom2." + name).type(TXT).add("\"goodies\"").build()).build();

      String nonce = name + " @ " + new Date();
      String comment = name + " for " + JcloudsVersion.get();
      NewZone newZone = context.getApi().getZoneApi().createWithReferenceAndComment(name, nonce, comment);
      String zoneId = newZone.getZone().getId();
      getAnonymousLogger().info("created zone: " + newZone);
      try {
         assertTrue(inSync.apply(newZone.getChange()), "zone didn't sync " + newZone);
         sync(api(zoneId).apply(createAll(records)));

         checkAllRRs(zoneId);

         sync(api(zoneId).apply(deleteAll(records)));

         PagedIterable<RecordSet> refreshed = refresh(zoneId);
         assertTrue(refreshed.concat().filter(not(requiredRRTypes)).isEmpty(), "zone still has optional records: "
               + refreshed);

      } finally {
         clearAndDeleteZonesNamed(name);
      }
   }

   private void clearAndDeleteZonesNamed(String name) {
      for (Zone zone : context.getApi().getZoneApi().list().concat().filter(nameEquals(name))) {
         getAnonymousLogger().info("clearing and deleting zone: " + zone);
         Set<RecordSet> remaining = refresh(zone.getId()).concat().filter(not(requiredRRTypes)).toSet();
         if (!remaining.isEmpty())
            sync(api(zone.getId()).apply(deleteAll(remaining)));
         sync(context.getApi().getZoneApi().delete(zone.getId()));
      }
   }

   private void sync(Change job) {
      assertTrue(inSync.apply(job), "job didn't sync " + job);
   }

   private PagedIterable<RecordSet> refresh(String zoneId) {
      return api(zoneId).list();
   }

   private PagedIterable<Zone> zones() {
      PagedIterable<Zone> zones = context.getApi().getZoneApi().list();
      if (zones.get(0).isEmpty())
         throw new SkipException("no zones in context: " + context);
      return zones;
   }

   private RecordSetApi api(String zoneId) {
      return context.getApi().getRecordSetApiForZone(zoneId);
   }
}
