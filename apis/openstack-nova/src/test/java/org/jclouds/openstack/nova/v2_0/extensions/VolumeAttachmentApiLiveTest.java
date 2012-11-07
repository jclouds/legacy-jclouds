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
package org.jclouds.openstack.nova.v2_0.extensions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.openstack.nova.v2_0.domain.Volume;
import org.jclouds.openstack.nova.v2_0.domain.VolumeAttachment;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiLiveTest;
import org.jclouds.openstack.nova.v2_0.options.CreateVolumeOptions;
import org.jclouds.predicates.RetryablePredicate;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of Volume Attachment API
 * 
 * @author Everett Toews
 */
@Test(groups = "live", testName = "VolumeAttachmentApiLiveTest", singleThreaded = true)
public class VolumeAttachmentApiLiveTest extends BaseNovaApiLiveTest {

   private Optional<? extends VolumeApi> volumeApi;
   private Optional<? extends VolumeAttachmentApi> volumeAttachmentApi;
   
   private String zone;
   private Volume testVolume;

   @BeforeClass(groups = {"integration", "live"})
   @Override
   public void setupContext() {
      super.setupContext();
      zone = Iterables.getLast(novaContext.getApi().getConfiguredZones(), "nova");
      volumeApi = novaContext.getApi().getVolumeExtensionForZone(zone);
      volumeAttachmentApi = novaContext.getApi().getVolumeAttachmentExtensionForZone(zone);
   }

   @AfterClass(groups = { "integration", "live" })
   @Override
   protected void tearDownContext() {
      if (volumeApi.isPresent()) {
         if (testVolume != null) {
            final String volumeId = testVolume.getId();
            assertTrue(volumeApi.get().delete(volumeId));
            assertTrue(new RetryablePredicate<VolumeApi>(new Predicate<VolumeApi>() {
               @Override
               public boolean apply(VolumeApi volumeApi) {
                  return volumeApi.get(volumeId) == null;
               }
            }, 180 * 1000L).apply(volumeApi.get()));
         }
      }

      super.tearDownContext();
   }

   public void testCreateVolume() {
      if (volumeApi.isPresent()) {
         CreateVolumeOptions options = CreateVolumeOptions.Builder
               .name("jclouds-test-volume")
               .description("description of test volume")
               .availabilityZone(zone);

         testVolume = volumeApi.get().create(1, options);
         assertTrue(new RetryablePredicate<VolumeApi>(new Predicate<VolumeApi>() {
            @Override
            public boolean apply(VolumeApi volumeApi) {
               return volumeApi.get(testVolume.getId()).getStatus() == Volume.Status.AVAILABLE;
            }
         }, 180 * 1000L).apply(volumeApi.get()));
      }
   }

   @Test(dependsOnMethods = "testCreateVolume")
   public void testAttachments() {
      if (volumeApi.isPresent()) {
         String server_id = null;
         try {
            final String serverId = server_id = createServerInZone(zone).getId();

            Set<? extends VolumeAttachment> attachments = 
                  volumeAttachmentApi.get().listAttachmentsOnServer(serverId).toImmutableSet();
            assertNotNull(attachments);
            final int before = attachments.size();

            VolumeAttachment testAttachment = volumeAttachmentApi.get().attachVolumeToServerAsDevice(
                  testVolume.getId(), serverId, "/dev/vdf");
            assertNotNull(testAttachment.getId());
            assertEquals(testAttachment.getVolumeId(), testVolume.getId());

            assertTrue(new RetryablePredicate<VolumeAttachmentApi>(new Predicate<VolumeAttachmentApi>() {
               @Override
               public boolean apply(VolumeAttachmentApi volumeAttachmentApi) {
                  return volumeAttachmentApi.listAttachmentsOnServer(serverId).size() > before;
               }
            }, 60 * 1000L).apply(volumeAttachmentApi.get()));

            attachments = volumeAttachmentApi.get().listAttachmentsOnServer(serverId).toImmutableSet();
            assertNotNull(attachments);
            assertEquals(attachments.size(), before + 1);

            assertEquals(volumeApi.get().get(testVolume.getId()).getStatus(), Volume.Status.IN_USE);

            boolean foundIt = false;
            for (VolumeAttachment att : attachments) {
               VolumeAttachment details = volumeAttachmentApi.get()
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

            volumeAttachmentApi.get().detachVolumeFromServer(testVolume.getId(), serverId);
            assertTrue(new RetryablePredicate<VolumeAttachmentApi>(new Predicate<VolumeAttachmentApi>() {
               @Override
               public boolean apply(VolumeAttachmentApi volumeAttachmentApi) {
                  return volumeAttachmentApi.listAttachmentsOnServer(serverId).size() == before;
               }
            }, 60 * 1000L).apply(volumeAttachmentApi.get()));

         } finally {
            if (server_id != null)
               novaContext.getApi().getServerApiForZone(zone).delete(server_id);
         }

      }
   }
}