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
package org.jclouds.openstack.nova.v1_1.extensions;

import static org.testng.Assert.*;

import java.util.Set;

import org.jclouds.openstack.domain.Resource;
import org.jclouds.openstack.nova.v1_1.domain.VolumeAttachment;
import org.jclouds.openstack.nova.v1_1.domain.VolumeSnapshot;
import org.jclouds.openstack.nova.v1_1.domain.Volume;
import org.jclouds.openstack.nova.v1_1.internal.BaseNovaClientLiveTest;
import org.jclouds.openstack.nova.v1_1.options.CreateVolumeSnapshotOptions;
import org.jclouds.openstack.nova.v1_1.options.CreateVolumeOptions;
import org.jclouds.predicates.RetryablePredicate;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of VolumeClient
 *
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "VolumeClientLiveTest", singleThreaded = true)
public class VolumeClientLiveTest extends BaseNovaClientLiveTest {

   private VolumeClient client;
   private String zone;

   private Volume testVolume;
   private VolumeSnapshot testSnapshot;

   @BeforeGroups(groups = { "integration", "live" })
   @Override
   public void setupContext() {
      super.setupContext();
      zone = Iterables.getFirst(novaContext.getApi().getConfiguredZones(), "nova");
      Optional<VolumeClient> optClient = novaContext.getApi().getVolumeExtensionForZone(zone);
      if (optClient.isPresent()) {
         client = optClient.get();
      }
   }

   @AfterGroups(groups = "live", alwaysRun = true)
   @Override
   protected void tearDown()  {
      if (client != null) {
         if (testSnapshot != null) {
            final String snapshotId = testSnapshot.getId();
            assertTrue(client.deleteSnapshot(snapshotId));
            assertTrue(new RetryablePredicate<VolumeClient>(new Predicate<VolumeClient>() {
               @Override
               public boolean apply(VolumeClient volumeClient) {
                  return client.getSnapshot(snapshotId) == null;
               }
            }, 30 * 1000L).apply(client));
         }
         if (testVolume != null) {
            final String volumeId = testVolume.getId();
            assertTrue(client.deleteVolume(volumeId));
            assertTrue(new RetryablePredicate<VolumeClient>(new Predicate<VolumeClient>() {
               @Override
               public boolean apply(VolumeClient volumeClient) {
                  return client.getVolume(volumeId) == null;
               }
            }, 180 * 1000L).apply(client));
         }
      }
      super.tearDown();
   }

   public void testCreateVolume() throws Exception {
      testVolume = client.createVolume(1, CreateVolumeOptions.Builder.name("jclouds-test-volume").description("description of test volume").availabilityZone(zone));
      for (int i=0; i<100 && testVolume.getStatus() == Volume.Status.CREATING; i++) {
         Thread.sleep(100);
         testVolume = client.getVolume(testVolume.getId());
      }
      assertEquals(testVolume.getStatus(), Volume.Status.AVAILABLE);
   }

   @Test(dependsOnMethods = "testCreateVolume")
   public void testListVolumes() throws Exception {
      if (client != null) {
         Set<Volume> volumes = client.listVolumes();
         assertNotNull(volumes);
         boolean foundIt = false;
         for (Volume vol : volumes) {
            Volume details = client.getVolume(vol.getId());
            assertNotNull(details);
            if (Objects.equal(details.getId(), testVolume.getId())) {
               foundIt = true;
            }
         }
         assertTrue(foundIt, "Failed to find the volume we created in listVolumes() response");
      }
   }

   @Test(dependsOnMethods = "testCreateVolume")
   public void testListVolumesInDetail() throws Exception {
      if (client != null) {
         Set<Volume> volumes = client.listVolumesInDetail();
         assertNotNull(volumes);
         assertTrue(volumes.contains(testVolume));
         boolean foundIt = false;
         for (Volume vol : volumes) {
            Volume details = client.getVolume(vol.getId());
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
         assertTrue(foundIt, "Failed to find the volume we previously created in listVolumesInDetail() response");
      }
   }

   @Test(dependsOnMethods = "testCreateVolume")
   public void testCreateSnapshot() throws Exception {
      if (client != null) {
         testSnapshot = client.createSnapshot(testVolume.getId(), CreateVolumeSnapshotOptions.Builder.name("jclouds-live-test").description("jclouds live test snapshot").force());
         assertNotNull(testSnapshot);
         assertNotNull(testSnapshot.getId());
         final String snapshotId = testSnapshot.getId();
         assertNotNull(testSnapshot.getStatus());
         assertTrue(testSnapshot.getSize() > -1);
         assertNotNull(testSnapshot.getCreated());

         assertTrue(new RetryablePredicate<VolumeClient>(new Predicate<VolumeClient>() {
            @Override
            public boolean apply(VolumeClient volumeClient) {
               return client.getSnapshot(snapshotId).getStatus() == Volume.Status.AVAILABLE;
            }
         }, 30 * 1000L).apply(client));
      }
   }
   
   @Test(dependsOnMethods = "testCreateSnapshot")
   public void testListSnapshots() throws Exception {
      if (client != null) {
         Set<VolumeSnapshot> snapshots = client.listSnapshots();
         assertNotNull(snapshots);
         boolean foundIt = false;
         for (VolumeSnapshot snap : snapshots) {
            VolumeSnapshot details = client.getSnapshot(snap.getId());
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
   public void testListSnapshotsInDetail() throws Exception {
      if (client != null) {
         Set<VolumeSnapshot> snapshots = client.listSnapshotsInDetail();
         assertNotNull(snapshots);
         boolean foundIt = false;
         for (VolumeSnapshot snap : snapshots) {
            VolumeSnapshot details = client.getSnapshot(snap.getId());
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

   @Test(enabled=false, dependsOnMethods = "testCreateVolume") // disabled as it alters an existing server
   public void testAttachments() throws Exception {
      if (client != null) {
         Set<Resource> servers = novaContext.getApi().getServerClientForZone(zone).listServers();
         if (!servers.isEmpty()) {
            final String serverId = Iterables.getFirst(servers, null).getId();
            Set<VolumeAttachment> attachments = client.listAttachments(serverId);
            assertNotNull(attachments);
            final int before = attachments.size();

            VolumeAttachment testAttachment = client.attachVolume(serverId, testVolume.getId(), "/dev/vdf");
            assertNotNull(testAttachment.getId());
            assertEquals(testAttachment.getVolumeId(), testVolume.getId());
            
            assertTrue(new RetryablePredicate<VolumeClient>(new Predicate<VolumeClient>() {
               @Override
               public boolean apply(VolumeClient volumeClient) {
                  return client.listAttachments(serverId).size() == before+1;
               }
            }, 60 * 1000L).apply(client));

            attachments = client.listAttachments(serverId);
            assertNotNull(attachments);
            assertEquals(attachments.size(), before+1);
            
            assertEquals(client.getVolume(testVolume.getId()).getStatus(), Volume.Status.IN_USE);

            boolean foundIt = false;
            for (VolumeAttachment att : attachments) {
               VolumeAttachment details = client.getAttachment(serverId, att.getId());
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

            client.detachVolume(serverId, testVolume.getId());
            assertTrue(new RetryablePredicate<VolumeClient>(new Predicate<VolumeClient>() {
               @Override
               public boolean apply(VolumeClient volumeClient) {
                  return client.listAttachments(serverId).size() == before;
               }
            }, 60 * 1000L).apply(client));
         }
      }
   }
}
