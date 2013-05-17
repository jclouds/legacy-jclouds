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
package org.jclouds.openstack.cinder.v1.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.openstack.cinder.v1.domain.Snapshot;
import org.jclouds.openstack.cinder.v1.domain.Volume;
import org.jclouds.openstack.cinder.v1.internal.BaseCinderApiLiveTest;
import org.jclouds.openstack.cinder.v1.options.CreateSnapshotOptions;
import org.jclouds.openstack.cinder.v1.options.CreateVolumeOptions;
import org.jclouds.openstack.cinder.v1.predicates.SnapshotPredicates;
import org.jclouds.openstack.cinder.v1.predicates.VolumePredicates;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of VolumeApi
 *  
 * @author Everett Toews
 */
@Test(groups = "live", testName = "VolumeApiLiveTest", singleThreaded = true)
public class VolumeAndSnapshotApiLiveTest extends BaseCinderApiLiveTest {
   private static final String name = System.getProperty("user.name").replace('.','-').toLowerCase();
   
   private String zone;

   private VolumeApi volumeApi;
   private SnapshotApi snapshotApi;
   
   private Volume testVolume;
   private Snapshot testSnapshot;

   @BeforeClass(groups = {"integration", "live"})
   @Override
   public void setup() {
      super.setup();
      zone = Iterables.getLast(api.getConfiguredZones(), "nova");
      volumeApi = api.getVolumeApiForZone(zone);
      snapshotApi = api.getSnapshotApiForZone(zone);
   }

   @AfterClass(groups = { "integration", "live" })
   @Override
   protected void tearDown() {
      if (testSnapshot != null) {
         assertTrue(snapshotApi.delete(testSnapshot.getId()));
         assertTrue(SnapshotPredicates.awaitDeleted(snapshotApi).apply(testSnapshot));
      }

      if (testVolume != null) {
         assertTrue(volumeApi.delete(testVolume.getId()));
         assertTrue(VolumePredicates.awaitDeleted(volumeApi).apply(testVolume));
      }

      super.tearDown();
   }

   public void testCreateVolume() {
      CreateVolumeOptions options = CreateVolumeOptions.Builder
            .name(name)
            .description("description of test volume")
            .availabilityZone(zone);
      testVolume = volumeApi.create(100, options);
      
      assertTrue(VolumePredicates.awaitAvailable(volumeApi).apply(testVolume));
   }

   @Test(dependsOnMethods = "testCreateVolume")
   public void testListVolumes() {
      Set<? extends Volume> volumes = volumeApi.list().toSet();
      assertNotNull(volumes);
      boolean foundIt = false;
      for (Volume vol : volumes) {
         Volume details = volumeApi.get(vol.getId());
         assertNotNull(details);
         if (Objects.equal(details.getId(), testVolume.getId())) {
            foundIt = true;
         }
      }
      assertTrue(foundIt, "Failed to find the volume we created in list() response");
   }

   @Test(dependsOnMethods = "testCreateVolume")
   public void testListVolumesInDetail() {
      Set<? extends Volume> volumes = volumeApi.listInDetail().toSet();
      assertNotNull(volumes);
      boolean foundIt = false;
      for (Volume vol : volumes) {
         Volume details = volumeApi.get(vol.getId());
         assertNotNull(details);
         assertNotNull(details.getId());
         assertNotNull(details.getCreated());
         assertTrue(details.getSize() > -1);

         assertEquals(details.getId(), vol.getId());
         assertEquals(details.getSize(), vol.getSize());
         assertEquals(details.getName(), vol.getName());
         assertEquals(details.getDescription(), vol.getDescription());
         assertEquals(details.getCreated(), vol.getCreated());
         if (Objects.equal(details.getId(), testVolume.getId())) {
            foundIt = true;
         }
      }
      assertTrue(foundIt, "Failed to find the volume we previously created in listInDetail() response");
   }

   @Test(dependsOnMethods = "testCreateVolume")
   public void testCreateSnapshot() {
      testSnapshot = snapshotApi.create(
               testVolume.getId(),
               CreateSnapshotOptions.Builder.name("jclouds-live-test").description(
                        "jclouds live test snapshot").force());
      assertNotNull(testSnapshot);
      assertNotNull(testSnapshot.getId());
      assertNotNull(testSnapshot.getStatus());
      assertTrue(testSnapshot.getSize() > -1);
      assertNotNull(testSnapshot.getCreated());

      assertTrue(SnapshotPredicates.awaitAvailable(snapshotApi).apply(testSnapshot));
   }

   @Test(dependsOnMethods = "testCreateSnapshot")
   public void testListSnapshots() {
      Set<? extends Snapshot> snapshots = snapshotApi.list().toSet();
      assertNotNull(snapshots);
      boolean foundIt = false;
      for (Snapshot snap : snapshots) {
         Snapshot details = snapshotApi.get(snap.getId());
         if (Objects.equal(snap.getVolumeId(), testVolume.getId())) {
            foundIt = true;
         }
         assertNotNull(details);
         assertEquals(details.getId(), snap.getId());
         assertEquals(details.getVolumeId(), snap.getVolumeId());
      }
      assertTrue(foundIt, "Failed to find the snapshot we previously created in listSnapshots() response");
   }

   @Test(dependsOnMethods = "testCreateSnapshot")
   public void testListSnapshotsInDetail() {
      Set<? extends Snapshot> snapshots = snapshotApi.listInDetail().toSet();
      assertNotNull(snapshots);
      boolean foundIt = false;
      for (Snapshot snap : snapshots) {
         Snapshot details = snapshotApi.get(snap.getId());
         if (Objects.equal(snap.getVolumeId(), testVolume.getId())) {
            foundIt = true;
            assertSame(details, testSnapshot);
         }
         assertSame(details, snap);
      }

      assertTrue(foundIt, "Failed to find the snapshot we created in listSnapshotsInDetail() response");
   }

   private void assertSame(Snapshot a, Snapshot b) {
      assertNotNull(a);
      assertNotNull(b);
      assertEquals(a.getId(), b.getId());
      assertEquals(a.getDescription(), b.getDescription());
      assertEquals(a.getName(), b.getName());
      assertEquals(a.getVolumeId(), b.getVolumeId());
   }
}
