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
package org.jclouds.openstack.nova.v2_0.extensions;

import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.openstack.nova.v2_0.domain.Volume;
import org.jclouds.openstack.nova.v2_0.domain.VolumeAttachment;
import org.jclouds.openstack.nova.v2_0.domain.VolumeSnapshot;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiLiveTest;
import org.jclouds.openstack.nova.v2_0.options.CreateVolumeOptions;
import org.jclouds.openstack.nova.v2_0.options.CreateVolumeSnapshotOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of VolumeApi
 * 
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "VolumeApiLiveTest", singleThreaded = true)
public class VolumeApiLiveTest extends BaseNovaApiLiveTest {

   private Optional<? extends VolumeApi> volumeOption;
   private String zone;

   private Volume testVolume;
   private VolumeSnapshot testSnapshot;

   @BeforeClass(groups = {"integration", "live"})
   @Override
   public void setup() {
      super.setup();
      zone = Iterables.getLast(api.getConfiguredZones(), "nova");
      volumeOption = api.getVolumeExtensionForZone(zone);
   }

   @AfterClass(groups = { "integration", "live" })
   @Override
   protected void tearDown() {
      if (volumeOption.isPresent()) {
         if (testSnapshot != null) {
            final String snapshotId = testSnapshot.getId();
            assertTrue(volumeOption.get().deleteSnapshot(snapshotId));
            assertTrue(retry(new Predicate<VolumeApi>() {
               public boolean apply(VolumeApi volumeApi) {
                  return volumeOption.get().getSnapshot(snapshotId) == null;
               }
            }, 30 * 1000L).apply(volumeOption.get()));
         }
         if (testVolume != null) {
            final String volumeId = testVolume.getId();
            assertTrue(volumeOption.get().delete(volumeId));
            assertTrue(retry(new Predicate<VolumeApi>() {
               public boolean apply(VolumeApi volumeApi) {
                  return volumeOption.get().get(volumeId) == null;
               }
            }, 180 * 1000L).apply(volumeOption.get()));
         }
      }
      super.tearDown();
   }

   public void testCreateVolume() {
      if (volumeOption.isPresent()) {
         testVolume = volumeOption.get().create(
               1,
               CreateVolumeOptions.Builder.name("jclouds-test-volume").description("description of test volume")
                     .availabilityZone(zone));
         assertTrue(retry(new Predicate<VolumeApi>() {
            public boolean apply(VolumeApi volumeApi) {
               return volumeOption.get().get(testVolume.getId()).getStatus() == Volume.Status.AVAILABLE;
            }
         }, 180 * 1000L).apply(volumeOption.get()));
      }
   }

   @Test(dependsOnMethods = "testCreateVolume")
   public void testListVolumes() {
      if (volumeOption.isPresent()) {
         Set<? extends Volume> volumes = volumeOption.get().list().toSet();
         assertNotNull(volumes);
         boolean foundIt = false;
         for (Volume vol : volumes) {
            Volume details = volumeOption.get().get(vol.getId());
            assertNotNull(details);
            if (Objects.equal(details.getId(), testVolume.getId())) {
               foundIt = true;
            }
         }
         assertTrue(foundIt, "Failed to find the volume we created in list() response");
      }
   }

   @Test(dependsOnMethods = "testCreateVolume")
   public void testListVolumesInDetail() {
      if (volumeOption.isPresent()) {
         Set<? extends Volume> volumes = volumeOption.get().listInDetail().toSet();
         assertNotNull(volumes);
         boolean foundIt = false;
         for (Volume vol : volumes) {
            Volume details = volumeOption.get().get(vol.getId());
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
   }

   @Test(dependsOnMethods = "testCreateVolume")
   public void testCreateSnapshot() {
      if (volumeOption.isPresent()) {
         testSnapshot = volumeOption.get().createSnapshot(
                  testVolume.getId(),
                  CreateVolumeSnapshotOptions.Builder.name("jclouds-live-test").description(
                           "jclouds live test snapshot").force());
         assertNotNull(testSnapshot);
         assertNotNull(testSnapshot.getId());
         final String snapshotId = testSnapshot.getId();
         assertNotNull(testSnapshot.getStatus());
         assertTrue(testSnapshot.getSize() > -1);
         assertNotNull(testSnapshot.getCreated());

         assertTrue(retry(new Predicate<VolumeApi>() {
            public boolean apply(VolumeApi volumeApi) {
               return volumeOption.get().getSnapshot(snapshotId).getStatus() == Volume.Status.AVAILABLE;
            }
         }, 30 * 1000L).apply(volumeOption.get()));
      }
   }

   @Test(dependsOnMethods = "testCreateSnapshot")
   public void testListSnapshots() {
      if (volumeOption.isPresent()) {
         Set<? extends VolumeSnapshot> snapshots = volumeOption.get().listSnapshots().toSet();
         assertNotNull(snapshots);
         boolean foundIt = false;
         for (VolumeSnapshot snap : snapshots) {
            VolumeSnapshot details = volumeOption.get().getSnapshot(snap.getId());
            if (Objects.equal(snap.getVolumeId(), testVolume.getId())) {
               foundIt = true;
            }
            assertNotNull(details);
            assertEquals(details.getId(), snap.getId());
            assertEquals(details.getVolumeId(), snap.getVolumeId());
         }
         assertTrue(foundIt, "Failed to find the snapshot we previously created in listSnapshots() response");
      }
   }

   @Test(dependsOnMethods = "testCreateSnapshot")
   public void testListSnapshotsInDetail() {
      if (volumeOption.isPresent()) {
         Set<? extends VolumeSnapshot> snapshots = volumeOption.get().listSnapshotsInDetail().toSet();
         assertNotNull(snapshots);
         boolean foundIt = false;
         for (VolumeSnapshot snap : snapshots) {
            VolumeSnapshot details = volumeOption.get().getSnapshot(snap.getId());
            if (Objects.equal(snap.getVolumeId(), testVolume.getId())) {
               foundIt = true;
               assertSame(details, testSnapshot);
            }
            assertSame(details, snap);
         }

         assertTrue(foundIt, "Failed to find the snapshot we created in listSnapshotsInDetail() response");
      }
   }

   private void assertSame(VolumeSnapshot a, VolumeSnapshot b) {
      assertNotNull(a);
      assertNotNull(b);
      assertEquals(a.getId(), b.getId());
      assertEquals(a.getDescription(), b.getDescription());
      assertEquals(a.getName(), b.getName());
      assertEquals(a.getVolumeId(), b.getVolumeId());
   }

   @Test(dependsOnMethods = "testCreateVolume")
   public void testAttachments() {
      if (volumeOption.isPresent()) {
         String server_id = null;
         try {
            final String serverId = server_id = createServerInZone(zone).getId();

            Set<? extends VolumeAttachment> attachments = volumeOption.get().listAttachmentsOnServer(serverId).toSet();
            assertNotNull(attachments);
            final int before = attachments.size();

            VolumeAttachment testAttachment = volumeOption.get().attachVolumeToServerAsDevice(testVolume.getId(),
                     serverId, "/dev/vdf");
            assertNotNull(testAttachment.getId());
            assertEquals(testAttachment.getVolumeId(), testVolume.getId());

            assertTrue(retry(new Predicate<VolumeApi>() {
               public boolean apply(VolumeApi volumeApi) {
                  return volumeOption.get().listAttachmentsOnServer(serverId).size() > before;
               }
            }, 60 * 1000L).apply(volumeOption.get()));

            attachments = volumeOption.get().listAttachmentsOnServer(serverId).toSet();
            assertNotNull(attachments);
            assertEquals(attachments.size(), before + 1);

            assertEquals(volumeOption.get().get(testVolume.getId()).getStatus(), Volume.Status.IN_USE);

            boolean foundIt = false;
            for (VolumeAttachment att : attachments) {
               VolumeAttachment details = volumeOption.get()
                        .getAttachmentForVolumeOnServer(att.getVolumeId(), serverId);
               assertNotNull(details);
               assertNotNull(details.getId());
               assertNotNull(details.getServerId());
               assertNotNull(details.getVolumeId());
               if (Objects.equal(details.getVolumeId(), testVolume.getId())) {
                  foundIt = true;
                  assertEquals(details.getDevice(), "/dev/vdf");
                  assertEquals(details.getServerId(), serverId);
               }
            }

            assertTrue(foundIt, "Failed to find the attachment we created in listAttachments() response");

            volumeOption.get().detachVolumeFromServer(testVolume.getId(), serverId);
            assertTrue(retry(new Predicate<VolumeApi>() {
               public boolean apply(VolumeApi volumeApi) {
                  return volumeOption.get().listAttachmentsOnServer(serverId).size() == before;
               }
            }, 60 * 1000L).apply(volumeOption.get()));

         } finally {
            if (server_id != null)
               api.getServerApiForZone(zone).delete(server_id);
         }

      }
   }
}
