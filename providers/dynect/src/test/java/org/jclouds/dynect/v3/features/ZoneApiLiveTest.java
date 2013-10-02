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
package org.jclouds.dynect.v3.features;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.logging.Logger.getAnonymousLogger;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.jclouds.JcloudsVersion;
import org.jclouds.dynect.v3.domain.Job;
import org.jclouds.dynect.v3.domain.Job.Status;
import org.jclouds.dynect.v3.domain.Zone;
import org.jclouds.dynect.v3.internal.BaseDynECTApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "ZoneApiLiveTest")
public class ZoneApiLiveTest extends BaseDynECTApiLiveTest {

   private void checkZone(Zone zone) {
      checkNotNull(zone.getFQDN(), "FQDN cannot be null for a Zone: %s", zone);
      checkNotNull(zone.getSerial(), "Serial cannot be null for a Zone: %s", zone);
   }

   @Test
   protected void testListAndGetZones() {
      ImmutableList<String> zones = api().list().toList();
      getAnonymousLogger().info("zones: " + zones.size());

      for (String fqdn : zones) {
         Zone zone = api().get(fqdn);
         checkNotNull(zone, "zone was null for Zone: %s", fqdn);
         checkZone(zone);
      }
   }

   @Test
   public void testGetZoneWhenNotFound() {
      assertNull(api().get("AAAAAAAAAAAAAAAA.foo.com"));
   }

   @Test
   public void testDeleteZoneWhenNotFound() {
      assertNull(api().delete("AAAAAAAAAAAAAAAA.foo.com"));
   }

   String fqdn = System.getProperty("user.name").replace('.', '-') + ".zone.dynecttest.jclouds.org";
   String contact = JcloudsVersion.get() + ".jclouds.org";

   @Test
   public void testCreateZone() {
      Job job = api().scheduleCreateWithContact(fqdn, contact);
      checkNotNull(job, "unable to create zone %s", fqdn);
      getAnonymousLogger().info("created zone: " + job);
      assertEquals(job.getStatus(), Status.SUCCESS);
      assertEquals(api.getJob(job.getId()), job);
   }

   @Test(dependsOnMethods = "testCreateZone")
   public void testPublishZone() {
      Zone zone = api().publish(fqdn);
      checkNotNull(zone, "unable to publish zone %s", fqdn);
      getAnonymousLogger().info("published zone: " + zone);
      checkZone(zone);
   }

   @Test(dependsOnMethods = "testPublishZone")
   public void testFreezeZone() {
      Job job = api().freeze(fqdn);
      assertEquals(job.getStatus(), Status.SUCCESS);
      assertEquals(api.getJob(job.getId()), job);
      // TODO: determine how to prove it is frozen
   }

   @Test(dependsOnMethods = "testFreezeZone")
   public void testThawZone() {
      Job job = api().thaw(fqdn);
      assertEquals(job.getStatus(), Status.SUCCESS);
      assertEquals(api.getJob(job.getId()), job);
      // TODO: determine how to prove it is thawed
   }

   @Test(dependsOnMethods = "testThawZone")
   public void testDeleteZoneChanges() {
      Job job = api().deleteChanges(fqdn);
      assertEquals(job.getStatus(), Status.SUCCESS);
      assertEquals(api.getJob(job.getId()), job);
   }

   @Test(dependsOnMethods = "testDeleteZoneChanges")
   public void testDeleteZone() {
      Job job = api().delete(fqdn);
      assertEquals(job.getStatus(), Status.SUCCESS);
      assertEquals(api.getJob(job.getId()), job);
      assertNull(api().get(fqdn), "job " + job + " didn't delete zone" + fqdn);
   }

   protected ZoneApi api() {
      return api.getZoneApi();
   }

   @Override
   @AfterClass(groups = "live", alwaysRun = true)
   protected void tearDown() {
      api().delete(fqdn);
      super.tearDown();
   }
}
