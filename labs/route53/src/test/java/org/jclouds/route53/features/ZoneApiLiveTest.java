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
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.route53.domain.Change.Status.INSYNC;
import static org.jclouds.route53.domain.Change.Status.PENDING;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Date;
import java.util.logging.Logger;

import org.jclouds.JcloudsVersion;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.route53.domain.Change;
import org.jclouds.route53.domain.NewZone;
import org.jclouds.route53.domain.Zone;
import org.jclouds.route53.internal.BaseRoute53ApiLiveTest;
import org.jclouds.route53.options.ListZonesOptions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "ZoneApiLiveTest")
public class ZoneApiLiveTest extends BaseRoute53ApiLiveTest {
   private Predicate<Change> inSync;

   @BeforeClass(groups = "live")
   @Override
   public void setupContext() {
      super.setupContext();
      inSync = retry(new Predicate<Change>() {
         public boolean apply(Change input) {
            return context.getApi().getChange(input.getId()).getStatus() == INSYNC;
         }
      }, 600, 1, 5, SECONDS);
   }

   private void checkZone(Zone zone) {
      checkNotNull(zone.getId(), "Id cannot be null for a Zone %s", zone);
      checkNotNull(zone.getName(), "Name cannot be null for a Zone %s", zone);
      checkNotNull(zone.getCallerReference(), "CallerReference cannot be null for a Zone %s", zone);
      checkNotNull(zone.getComment(), "While Comment can be null for a Zone, its Optional wrapper cannot %s", zone);
   }

   @Test
   protected void testListZones() {
      IterableWithMarker<Zone> response = api().list().get(0);

      for (Zone zone : response) {
         checkZone(zone);
      }

      if (response.size() > 0) {
         Zone zone = response.iterator().next();
         assertEquals(api().get(zone.getId()).getZone(), zone);
      }

      // Test with a Marker, even if it's null
      response = api().list(ListZonesOptions.Builder.afterMarker(response.nextMarker().orNull()));
      for (Zone zone : response) {
         checkZone(zone);
      }
   }

   @Test
   public void testGetZoneWhenNotFound() {
      assertNull(api().get("AAAAAAAAAAAAAAAA"));
   }

   @Test
   public void testDeleteZoneWhenNotFound() {
      assertNull(api().delete("AAAAAAAAAAAAAAAA"));
   }

   @Test
   public void testCreateAndDeleteZone() {
      String name = System.getProperty("user.name").replace('.', '-') + ".zone.route53test.jclouds.org.";
      String nonce = name + " @ " + new Date();
      String comment = name + " for " + JcloudsVersion.get();
      NewZone newZone = api().createWithReferenceAndComment(name, nonce, comment);
      Logger.getAnonymousLogger().info("created zone: " + newZone);
      try {
         checkZone(newZone.getZone());
         assertEquals(newZone.getChange().getStatus(), PENDING, "invalid status on zone " + newZone);
         assertTrue(newZone.getNameServers().size() > 0, "no name servers for zone " + newZone);
         assertEquals(newZone.getZone().getName(), name);
         assertEquals(newZone.getZone().getCallerReference(), nonce);
         assertEquals(newZone.getZone().getComment().get(), comment);
         
         assertTrue(inSync.apply(newZone.getChange()), "zone didn't sync " + newZone);
      } finally {
         Change delete = api().delete(newZone.getZone().getId());
         assertTrue(inSync.apply(delete), "delete didn't sync " + delete);
      }
   }

   protected ZoneApi api() {
      return context.getApi().getZoneApi();
   }
}
