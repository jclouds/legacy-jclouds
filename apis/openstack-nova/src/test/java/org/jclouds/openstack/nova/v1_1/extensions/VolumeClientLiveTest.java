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
import org.jclouds.openstack.nova.v1_1.domain.Attachment;
import org.jclouds.openstack.nova.v1_1.domain.Server;
import org.jclouds.openstack.nova.v1_1.domain.SimpleTenantUsage;
import org.jclouds.openstack.nova.v1_1.domain.Snapshot;
import org.jclouds.openstack.nova.v1_1.domain.Volume;
import org.jclouds.openstack.nova.v1_1.internal.BaseNovaClientLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of SimpleTenantUsageClient
 *
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "VolumeClientLiveTest", singleThreaded = true)
public class VolumeClientLiveTest extends BaseNovaClientLiveTest {

   private VolumeClient client;
   private String zone;

   @BeforeMethod(alwaysRun = true)
   public void setUpClient() {
      zone = Iterables.getFirst(novaContext.getApi().getConfiguredZones(), "nova");
      Optional<VolumeClient> optClient = novaContext.getApi().getVolumeExtensionForZone(zone);
      if (optClient.isPresent()) {
         client = optClient.get();
      }
   }

   public void testListVolumes() throws Exception {
      if (client != null) {
         Set<Volume> volumes = client.listVolumes();
         assertNotNull(volumes);
         for (Volume vol : volumes) {
            Volume details = client.getVolume(vol.getId());
            assertNotNull(details);
         }
      }
   }

   public void testListVolumesInDetail() throws Exception {
      if (client != null) {
         Set<Volume> volumes = client.listVolumesInDetail();
         assertNotNull(volumes);
         for (Volume vol : volumes) {
            Volume details = client.getVolume(vol.getId());
            assertNotNull(details);
            assertEquals(details, vol);
            assertNotNull(details.getId());
            assertNotNull(details.getStatus());
            assertTrue(details.getSize() > -1);
            assertNotNull(details.getCreated());
         }
      }
   }

   public void testListSnapshots() throws Exception {
      if (client != null) {
         Set<Snapshot> snapshots = client.listSnapshots();
         assertNotNull(snapshots);
         for (Snapshot snap : snapshots) {
            Snapshot details = client.getSnapshot(snap.getId());
            assertNotNull(details);
            assertNotNull(details.getId());
         }
      }
   }

   public void testListSnapshotsInDetail() throws Exception {
      if (client != null) {
         Set<Snapshot> snapshots = client.listSnapshotsInDetail();
         assertNotNull(snapshots);
         for (Snapshot snap : snapshots) {
            Snapshot details = client.getSnapshot(snap.getId());
            assertNotNull(details);
            assertEquals(details, snap);
         }
      }
   }

   public void testListAttachments() throws Exception {
      if (client != null) {
         Set<Resource> servers = novaContext.getApi().getServerClientForZone(zone).listServers();
         if (!servers.isEmpty()) {
            String serverId = Iterables.getFirst(servers, null).getId();
            Set<Attachment> attachments = client.listAttachments(serverId);
            assertNotNull(attachments);
            assertTrue(attachments.size() > 0);
            for (Attachment att : attachments) {
               Attachment details = client.getAttachment(serverId, att.getId());
               assertNotNull(details);
               assertNotNull(details.getId());
               assertNotNull(details.getServerId());
               assertNotNull(details.getVolumeId());
            }
         }
      }
   }
}
