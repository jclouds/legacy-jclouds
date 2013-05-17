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
package org.jclouds.route53.features;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.or;
import static java.util.logging.Logger.getAnonymousLogger;
import static org.jclouds.route53.domain.ChangeBatch.createAll;
import static org.jclouds.route53.domain.ChangeBatch.deleteAll;
import static org.jclouds.route53.predicates.HostedZonePredicates.nameEquals;
import static org.jclouds.route53.predicates.ResourceRecordSetPredicates.typeEquals;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.jclouds.JcloudsVersion;
import org.jclouds.collect.PagedIterable;
import org.jclouds.route53.domain.Change;
import org.jclouds.route53.domain.HostedZone;
import org.jclouds.route53.domain.NewHostedZone;
import org.jclouds.route53.domain.ResourceRecordSet;
import org.jclouds.route53.domain.ResourceRecordSet.RecordSubset;
import org.jclouds.route53.domain.ResourceRecordSet.RecordSubset.Latency;
import org.jclouds.route53.domain.ResourceRecordSet.RecordSubset.Weighted;
import org.jclouds.route53.internal.BaseRoute53ApiLiveTest;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "ResourceResourceRecordSetApiLiveTest")
public class ResourceRecordSetApiLiveTest extends BaseRoute53ApiLiveTest {

   private void checkRRS(ResourceRecordSet rrs) {
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
         checkNotNull(Weighted.class.cast(rrs).getWeight(), "Weight: ResourceRecordSubset %s", rrs);
      }
      if (rrs instanceof Latency) {
         checkNotNull(Latency.class.cast(rrs).getRegion(), "Region: ResourceRecordSubset %s", rrs);
      }
   }

   AtomicLong zones = new AtomicLong();

   @Test
   private void testListRRSs() {
      for (HostedZone zone : zones().concat()) {
         zones.incrementAndGet();
         checkAllRRs(zone.getId());
      }
   }

   private void checkAllRRs(String zoneId) {
      HostedZone zone = api.getHostedZoneApi().get(zoneId).getZone();
      List<ResourceRecordSet> records = api(zone.getId()).list().concat().toList();
      assertEquals(zone.getResourceRecordSetCount(), records.size());

      for (ResourceRecordSet rrs : records) {
         recordTypeCounts.getUnchecked(rrs.getType()).addAndGet(
               rrs.getAliasTarget().isPresent() ? 1 : rrs.getValues().size());
         checkRRS(rrs);
      }
   }

   LoadingCache<String, AtomicLong> recordTypeCounts = CacheBuilder.newBuilder().build(
         new CacheLoader<String, AtomicLong>() {
            public AtomicLong load(String key) throws Exception {
               return new AtomicLong();
            }
         });

   @AfterClass
   void logSummary() {
      getAnonymousLogger().info("zoneCount: " + zones);
      for (Entry<String, AtomicLong> entry : recordTypeCounts.asMap().entrySet())
         getAnonymousLogger().info(String.format("type: %s, count: %s", entry.getKey(), entry.getValue()));
   }

   @Test
   public void testDeleteRRSNotFound() {
      for (HostedZone zone : zones().concat()) {
         assertNull(api(zone.getId()).delete(
               ResourceRecordSet.builder().name("krank.foo.bar.").type("TXT").add("kranko").build()));
      }
   }

   /**
    * cannot delete a zone without at least one of each
    */
   private static final Predicate<ResourceRecordSet> requiredRRTypes = or(typeEquals("SOA"), typeEquals("NS"));

   @Test
   public void testCreateAndDeleteBulkRecords() {
      String name = System.getProperty("user.name").replace('.', '-') + ".bulk.route53test.jclouds.org.";
      try {
         String zoneId = recreateZone(name);

         ImmutableList<ResourceRecordSet> records = ImmutableList.<ResourceRecordSet> builder()
               .add(ResourceRecordSet.builder().name("dom1." + name).type("TXT").add("\"somehow\" \" somewhere\"")
                     .build())
               .add(ResourceRecordSet.builder().name("dom2." + name).type("TXT").add("\"goodies\"").build()).build();

         createAndDeleteRecordsInZone(records, zoneId);
      } finally {
         clearAndDeleteHostedZonesNamed(name);
      }
   }

   @Test
   public void testCreateAndDeleteWeightedRecords() {
      String name = System.getProperty("user.name").replace('.', '-') + ".weight.route53test.jclouds.org.";
      try {
         String zoneId = recreateZone(name);

         ImmutableList<ResourceRecordSet> records = ImmutableList.<ResourceRecordSet> builder()
               .add(Weighted.builder().id("dom1").weight(1).name("dom." + name).type("CNAME").add("dom1." + name)
                     .build())
               .add(Weighted.builder().id("dom2").weight(1).name("dom." + name).type("CNAME").add("dom2." + name)
                     .build()).build();

         createAndDeleteRecordsInZone(records, zoneId);
      } finally {
         clearAndDeleteHostedZonesNamed(name);
      }
   }

   private String recreateZone(String name) {
      clearAndDeleteHostedZonesNamed(name);
      String nonce = name + " @ " + new Date();
      String comment = name + " for " + JcloudsVersion.get();
      NewHostedZone newHostedZone = api.getHostedZoneApi()
            .createWithReferenceAndComment(name, nonce, comment);
      getAnonymousLogger().info("created zone: " + newHostedZone);
      assertTrue(inSync.apply(newHostedZone.getChange()), "zone didn't sync " + newHostedZone);
      return newHostedZone.getZone().getId();
   }

   private void createAndDeleteRecordsInZone(ImmutableList<ResourceRecordSet> records, String zoneId) {
      sync(api(zoneId).apply(createAll(records)));

      checkAllRRs(zoneId);

      sync(api(zoneId).apply(deleteAll(records)));

      PagedIterable<ResourceRecordSet> refreshed = refresh(zoneId);
      assertTrue(refreshed.concat().filter(not(requiredRRTypes)).isEmpty(), "zone still has optional records: "
            + refreshed);
   }

   private void clearAndDeleteHostedZonesNamed(String name) {
      for (HostedZone zone : api.getHostedZoneApi().list().concat().filter(nameEquals(name))) {
         getAnonymousLogger().info("clearing and deleting zone: " + zone);
         Set<ResourceRecordSet> remaining = refresh(zone.getId()).concat().filter(not(requiredRRTypes)).toSet();
         if (!remaining.isEmpty())
            sync(api(zone.getId()).apply(deleteAll(remaining)));
         sync(api.getHostedZoneApi().delete(zone.getId()));
      }
   }

   private void sync(Change job) {
      assertTrue(inSync.apply(job), "job didn't sync " + job);
   }

   private PagedIterable<ResourceRecordSet> refresh(String zoneId) {
      return api(zoneId).list();
   }

   private PagedIterable<HostedZone> zones() {
      PagedIterable<HostedZone> zones = api.getHostedZoneApi().list();
      if (zones.get(0).isEmpty())
         throw new SkipException("no zones in context: " + identity);
      return zones;
   }

   private ResourceRecordSetApi api(String zoneId) {
      return api.getResourceRecordSetApiForHostedZone(zoneId);
   }
}
