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
import static java.lang.String.format;
import static java.util.logging.Logger.getAnonymousLogger;
import static org.jclouds.route53.domain.Change.Status.PENDING;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Date;

import org.jclouds.JcloudsVersion;
import org.jclouds.route53.domain.Change;
import org.jclouds.route53.domain.NewHostedZone;
import org.jclouds.route53.domain.HostedZone;
import org.jclouds.route53.internal.BaseRoute53ApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "HostedZoneApiLiveTest")
public class HostedZoneApiLiveTest extends BaseRoute53ApiLiveTest {

   private void checkHostedZone(HostedZone zone) {
      getAnonymousLogger().info(format("zone %s rrs: %s", zone.getName(), zone.getResourceRecordSetCount()));

      checkNotNull(zone.getId(), "Id: HostedZone %s", zone);
      checkNotNull(zone.getName(), "Name: HostedZone %s", zone);
      checkNotNull(zone.getCallerReference(), "CallerReference: HostedZone %s", zone);
      checkNotNull(zone.getComment(), "While Comment can be null for a HostedZone, its Optional wrapper cannot %s", zone);
   }

   @Test
   protected void testListHostedZones() {
      ImmutableList<HostedZone> zones = api().list().concat().toList();
      getAnonymousLogger().info("zones: " + zones.size());

      for (HostedZone zone : zones) {
         checkHostedZone(zone);
         assertEquals(api().get(zone.getId()).getZone(), zone);
      }
   }

   @Test
   public void testGetHostedZoneWhenNotFound() {
      assertNull(api().get("AAAAAAAAAAAAAAAA"));
   }

   @Test
   public void testDeleteHostedZoneWhenNotFound() {
      assertNull(api().delete("AAAAAAAAAAAAAAAA"));
   }

   @Test
   public void testCreateAndDeleteHostedZone() {
      String name = System.getProperty("user.name").replace('.', '-') + ".zone.route53test.jclouds.org.";
      String nonce = name + " @ " + new Date();
      String comment = name + " for " + JcloudsVersion.get();
      NewHostedZone newHostedZone = api().createWithReferenceAndComment(name, nonce, comment);
      getAnonymousLogger().info("created zone: " + newHostedZone);
      try {
         checkHostedZone(newHostedZone.getZone());
         assertEquals(newHostedZone.getChange().getStatus(), PENDING, "invalid status on zone " + newHostedZone);
         assertTrue(newHostedZone.getNameServers().size() > 0, "no name servers for zone " + newHostedZone);
         assertEquals(newHostedZone.getZone().getName(), name);
         assertEquals(newHostedZone.getZone().getCallerReference(), nonce);
         assertEquals(newHostedZone.getZone().getComment().get(), comment);
         
         assertTrue(inSync.apply(newHostedZone.getChange()), "zone didn't sync " + newHostedZone);
      } finally {
         Change delete = api().delete(newHostedZone.getZone().getId());
         assertTrue(inSync.apply(delete), "delete didn't sync " + delete);
      }
   }

   protected HostedZoneApi api() {
      return api.getHostedZoneApi();
   }
}
