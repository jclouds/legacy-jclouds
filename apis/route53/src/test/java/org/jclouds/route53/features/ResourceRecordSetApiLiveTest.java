/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICE"NS"E-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIO"NS" OF ANY
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
import static org.jclouds.route53.predicates.HostedZonePredicates.nameEquals;
import static org.jclouds.route53.predicates.ResourceRecordSetPredicates.typeEquals;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Set;

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
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
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

   @Test
   private void testListRRSs() {
      for (HostedZone zone : zones().concat()) {
         checkAllRRs(zone.getId());
      }
   }

   private void checkAllRRs(String zoneId) {
      HostedZone zone = context.getApi().getHostedZoneApi().get(zoneId).getZone();
      List<ResourceRecordSet> records = api(zone.getId()).list().concat().toImmutableList();
      assertEquals(zone.getResourceRecordSetCount(), records.size());

      for (ResourceRecordSet rrs : records) {
         checkRRS(rrs);
      }
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
      clearAndDeleteHostedZonesNamed(name);

      ImmutableList<ResourceRecordSet> records = ImmutableList.<ResourceRecordSet> builder()
            .add(ResourceRecordSet.builder().name("dom1." + name).type("TXT").add("\"somehow\" \" somewhere\"").build())
            .add(ResourceRecordSet.builder().name("dom2." + name).type("TXT").add("\"goodies\"").build()).build();

      String nonce = name + " @ " + new Date();
      String comment = name + " for " + JcloudsVersion.get();
      NewHostedZone newHostedZone = context.getApi().getHostedZoneApi().createWithReferenceAndComment(name, nonce, comment);
      String zoneId = newHostedZone.getZone().getId();
      getAnonymousLogger().info("created zone: " + newHostedZone);
      try {
         assertTrue(inSync.apply(newHostedZone.getChange()), "zone didn't sync " + newHostedZone);
         sync(api(zoneId).apply(createAll(records)));

         checkAllRRs(zoneId);

         sync(api(zoneId).apply(deleteAll(records)));

         PagedIterable<ResourceRecordSet> refreshed = refresh(zoneId);
         assertTrue(refreshed.concat().filter(not(requiredRRTypes)).isEmpty(), "zone still has optional records: "
               + refreshed);

      } finally {
         clearAndDeleteHostedZonesNamed(name);
      }
   }

   private void clearAndDeleteHostedZonesNamed(String name) {
      for (HostedZone zone : context.getApi().getHostedZoneApi().list().concat().filter(nameEquals(name))) {
         getAnonymousLogger().info("clearing and deleting zone: " + zone);
         Set<ResourceRecordSet> remaining = refresh(zone.getId()).concat().filter(not(requiredRRTypes)).toImmutableSet();
         if (!remaining.isEmpty())
            sync(api(zone.getId()).apply(deleteAll(remaining)));
         sync(context.getApi().getHostedZoneApi().delete(zone.getId()));
      }
   }

   private void sync(Change job) {
      assertTrue(inSync.apply(job), "job didn't sync " + job);
   }

   private PagedIterable<ResourceRecordSet> refresh(String zoneId) {
      return api(zoneId).list();
   }

   private PagedIterable<HostedZone> zones() {
      PagedIterable<HostedZone> zones = context.getApi().getHostedZoneApi().list();
      if (zones.get(0).isEmpty())
         throw new SkipException("no zones in context: " + context);
      return zones;
   }

   private ResourceRecordSetApi api(String zoneId) {
      return context.getApi().getResourceRecordSetApiForHostedZone(zoneId);
   }
}
