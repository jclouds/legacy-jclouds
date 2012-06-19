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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.openstack.nova.v2_0.domain.Volume;
import org.jclouds.openstack.nova.v2_0.domain.VolumeAttachment;
import org.jclouds.openstack.nova.v2_0.domain.VolumeSnapshot;
import org.jclouds.openstack.nova.v2_0.extensions.VolumeClient;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaClientExpectTest;
import org.jclouds.openstack.nova.v2_0.options.CreateVolumeOptions;
import org.jclouds.openstack.nova.v2_0.options.CreateVolumeSnapshotOptions;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Tests VolumeClient guice wiring and parsing
 *
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "VolumeClientExpectTest")
public class VolumeClientExpectTest extends BaseNovaClientExpectTest {
   private DateService dateService = new SimpleDateFormatDateService();

   public void testListVolumes() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volumes");
      VolumeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).build(),
            standardResponseBuilder(200).payload(payloadFromResource("/volume_list.json")).build()
      ).getVolumeExtensionForZone("az-1.region-a.geo-1").get();

      Set<Volume> volumes = client.listVolumes();
      assertEquals(volumes, ImmutableSet.of(testVolume()));
   }

   public void testListVolumesFail() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volumes");
      VolumeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).build(),
            standardResponseBuilder(404).build()
      ).getVolumeExtensionForZone("az-1.region-a.geo-1").get();

      Set<Volume> volumes = client.listVolumes();
      assertTrue(volumes.isEmpty());
   }

   public void testListVolumesInDetail() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volumes/detail");
      VolumeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).build(),
            standardResponseBuilder(200).payload(payloadFromResource("/volume_list_detail.json")).build()
      ).getVolumeExtensionForZone("az-1.region-a.geo-1").get();

      Set<Volume> volumes = client.listVolumesInDetail();
      assertEquals(volumes, ImmutableSet.of(testVolume()));
   }

   public void testListVolumesInDetailFail() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volumes/detail");
      VolumeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).build(),
            standardResponseBuilder(404).build()
      ).getVolumeExtensionForZone("az-1.region-a.geo-1").get();

      Set<Volume> volumes = client.listVolumesInDetail();
      assertTrue(volumes.isEmpty());
   }
   
   public void testCreateVolume() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volumes");
      VolumeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint)
                  .method("POST")
                  .payload(payloadFromStringWithContentType("{\"volume\":{\"display_name\":\"jclouds-test-volume\",\"display_description\":\"description of test volume\",\"size\":1}}", MediaType.APPLICATION_JSON))
                  .build(),
            standardResponseBuilder(200).payload(payloadFromResource("/volume_details.json")).build()
      ).getVolumeExtensionForZone("az-1.region-a.geo-1").get();

      Volume volume = client.createVolume(1, CreateVolumeOptions.Builder.name("jclouds-test-volume").description("description of test volume"));
      assertEquals(volume, testVolume());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testCreateVolumeFail() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volumes");
      VolumeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint)
               .endpoint(endpoint)
               .method("POST")
               .payload(payloadFromStringWithContentType("{\"volume\":{\"display_name\":\"jclouds-test-volume\",\"display_description\":\"description of test volume\",\"size\":1}}", MediaType.APPLICATION_JSON))
               .build(),
            standardResponseBuilder(404).payload(payloadFromResource("/volume_details.json")).build()
      ).getVolumeExtensionForZone("az-1.region-a.geo-1").get();

      client.createVolume(1, CreateVolumeOptions.Builder.name("jclouds-test-volume").description("description of test volume"));
   }

   public void testGetVolume() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volumes/1");
      VolumeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).build(),
            standardResponseBuilder(200).payload(payloadFromResource("/volume_details.json")).build()
      ).getVolumeExtensionForZone("az-1.region-a.geo-1").get();

      Volume volume = client.getVolume("1");
      assertEquals(volume, testVolume());
      // double-check equals()
      assertEquals(volume.getStatus(), Volume.Status.IN_USE);
      assertEquals(volume.getDescription(), "This is a test volume");
      assertEquals(volume.getZone(), "nova");
      assertEquals(volume.getName(), "test");
      assertEquals(volume.getStatus(), Volume.Status.IN_USE);
      assertEquals(Iterables.getOnlyElement(volume.getAttachments()), testAttachment());
   }

   public void testGetVolumeFail() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volumes/1");
      VolumeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).build(),
            standardResponseBuilder(404).build()
      ).getVolumeExtensionForZone("az-1.region-a.geo-1").get();

      assertNull(client.getVolume("1"));
   }

   public void testDeleteVolume() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volumes/1");
      VolumeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).method("DELETE").build(),
            standardResponseBuilder(200).payload(payloadFromResource("/attachment_details.json")).build()
      ).getVolumeExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(client.deleteVolume("1"));
   }

   public void testDeleteVolumeFail() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volumes/1");
      VolumeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).method("DELETE").build(),
            standardResponseBuilder(404).build()
      ).getVolumeExtensionForZone("az-1.region-a.geo-1").get();

      assertFalse(client.deleteVolume("1"));
   }

   public void testListAttachments() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/instance-1/os-volume_attachments");
      VolumeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).build(),
            standardResponseBuilder(200).payload(payloadFromResource("/attachment_list.json")).build()
      ).getVolumeExtensionForZone("az-1.region-a.geo-1").get();

      Set<VolumeAttachment> attachments = client.listAttachmentsOnServer("instance-1");
      assertEquals(attachments, ImmutableSet.of(testAttachment()));
      // double-check individual fields
      VolumeAttachment attachment = Iterables.getOnlyElement(attachments);
      assertEquals(attachment.getDevice(), "/dev/vdc");
      assertEquals(attachment.getServerId(), "b4785058-cb80-491b-baa3-e4ee6546450e");
      assertEquals(attachment.getId(), "1");
      assertEquals(attachment.getVolumeId(), "1");
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testListAttachmentsFail() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/instance-2/os-volume_attachments");
      VolumeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).build(),
            standardResponseBuilder(401).build()
      ).getVolumeExtensionForZone("az-1.region-a.geo-1").get();

      client.listAttachmentsOnServer("instance-2");
   }
   
   public void testGetAttachment() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/instance-1/os-volume_attachments/1");
      VolumeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).build(),
            standardResponseBuilder(200).payload(payloadFromResource("/attachment_details.json")).build()
      ).getVolumeExtensionForZone("az-1.region-a.geo-1").get();

      VolumeAttachment attachment = client.getAttachmentForVolumeOnServer("1", "instance-1");
      assertEquals(attachment, testAttachment());
   }

   public void testGetAttachmentFail() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/instance-1/os-volume_attachments/1");
      VolumeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).build(),
            standardResponseBuilder(404).build()
      ).getVolumeExtensionForZone("az-1.region-a.geo-1").get();

     assertNull(client.getAttachmentForVolumeOnServer("1", "instance-1"));
   }

   public void testAttachVolume() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/instance-1/os-volume_attachments");
      VolumeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).method("POST")
                  .payload(payloadFromStringWithContentType("{\"volumeAttachment\":{\"volumeId\":\"1\",\"device\":\"/dev/vdc\"}}", MediaType.APPLICATION_JSON)).endpoint(endpoint).build(),
            standardResponseBuilder(200).payload(payloadFromResource("/attachment_details.json")).build()
      ).getVolumeExtensionForZone("az-1.region-a.geo-1").get();

      VolumeAttachment result = client.attachVolumeToServerAsDevice("1", "instance-1", "/dev/vdc");
      assertEquals(result, testAttachment());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testAttachVolumeFail() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/instance-1/os-volume_attachments");
      VolumeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).method("POST")
                  .payload(payloadFromStringWithContentType("{\"volumeAttachment\":{\"volumeId\":\"1\",\"device\":\"/dev/vdc\"}}", MediaType.APPLICATION_JSON)).endpoint(endpoint).build(),
            standardResponseBuilder(404).build()
      ).getVolumeExtensionForZone("az-1.region-a.geo-1").get();

      client.attachVolumeToServerAsDevice("1", "instance-1","/dev/vdc");
   }

   public void testDetachVolume() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/instance-1/os-volume_attachments/1");
      VolumeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).method("DELETE").build(),
            standardResponseBuilder(200).payload(payloadFromResource("/attachment_details.json")).build()
      ).getVolumeExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(client.detachVolumeFromServer("1", "instance-1"));
   }

   public void testDetachVolumeFail() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/instance-1/os-volume_attachments/1");
      VolumeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).method("DELETE").build(),
            standardResponseBuilder(404).build()
      ).getVolumeExtensionForZone("az-1.region-a.geo-1").get();

      assertFalse(client.detachVolumeFromServer("1", "instance-1"));
   }

   public void testListSnapshots() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-snapshots");
      VolumeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).build(),
            standardResponseBuilder(200).payload(payloadFromResource("/snapshot_list.json")).build()
      ).getVolumeExtensionForZone("az-1.region-a.geo-1").get();

      Set<VolumeSnapshot> snapshots = client.listSnapshots();
      assertEquals(snapshots, ImmutableSet.of(testSnapshot()));
   }

   public void testListSnapshotsFail() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-snapshots");
      VolumeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).build(),
            standardResponseBuilder(404).build()
      ).getVolumeExtensionForZone("az-1.region-a.geo-1").get();

      Set<VolumeSnapshot> snapshots = client.listSnapshots();
      assertTrue(snapshots.isEmpty());
   }

   public void testGetSnapshot() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-snapshots/1");
      VolumeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).build(),
            standardResponseBuilder(200).payload(payloadFromResource("/snapshot_details.json")).build()
      ).getVolumeExtensionForZone("az-1.region-a.geo-1").get();

      VolumeSnapshot snapshot = client.getSnapshot("1");
      assertEquals(snapshot, testSnapshot());
   }

   public void testGetSnapshotFail() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-snapshots/1");
      VolumeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).build(),
            standardResponseBuilder(404).build()
      ).getVolumeExtensionForZone("az-1.region-a.geo-1").get();

      assertNull(client.getSnapshot("1"));
   }

   public void testListSnapshotsInDetail() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-snapshots/detail");
      VolumeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).build(),
            standardResponseBuilder(200).payload(payloadFromResource("/snapshot_list_detail.json")).build()
      ).getVolumeExtensionForZone("az-1.region-a.geo-1").get();

      Set<VolumeSnapshot> snapshots = client.listSnapshotsInDetail();
      assertEquals(snapshots, ImmutableSet.of(testSnapshot()));

      // double-check individual fields
      VolumeSnapshot snappy = Iterables.getOnlyElement(snapshots);
      assertEquals(snappy.getId(), "7");
      assertEquals(snappy.getVolumeId(), "9");
      assertEquals(snappy.getStatus(), Volume.Status.AVAILABLE);
      assertEquals(snappy.getDescription(), "jclouds live test snapshot");
      assertEquals(snappy.getName(), "jclouds-live-test");
      assertEquals(snappy.getSize(), 1);
   }

   public void testListSnapshotsInDetailFail() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-snapshots/detail");
      VolumeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).build(),
            standardResponseBuilder(404).build()
      ).getVolumeExtensionForZone("az-1.region-a.geo-1").get();

      Set<VolumeSnapshot> snapshots = client.listSnapshotsInDetail();
      assertTrue(snapshots.isEmpty());
   }
   
   public void testCreateSnapshot() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-snapshots");
      VolumeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint)
                  .method("POST")
                  .payload(payloadFromStringWithContentType("{\"snapshot\":{\"display_name\":\"jclouds-live-test\",\"volume_id\":\"13\",\"display_description\":\"jclouds live test snapshot\",\"force\":\"true\"}}", MediaType.APPLICATION_JSON))
                  .build(),
            standardResponseBuilder(200).payload(payloadFromResource("/snapshot_details.json")).build()
      ).getVolumeExtensionForZone("az-1.region-a.geo-1").get();

      VolumeSnapshot snapshot = client.createSnapshot("13", CreateVolumeSnapshotOptions.Builder.name("jclouds-live-test").description("jclouds live test snapshot").force());
      assertEquals(snapshot, testSnapshot());
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testCreateSnapshotFail() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-snapshots");
      VolumeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint)
                  .method("POST")
                  .payload(payloadFromStringWithContentType("{\"snapshot\":{\"display_name\":\"jclouds-live-test\",\"volume_id\":\"13\",\"display_description\":\"jclouds live test snapshot\",\"force\":\"true\"}}", MediaType.APPLICATION_JSON))
                  .build(),
            standardResponseBuilder(401).build()
      ).getVolumeExtensionForZone("az-1.region-a.geo-1").get();

      client.createSnapshot("13", CreateVolumeSnapshotOptions.Builder.name("jclouds-live-test").description("jclouds live test snapshot").force());
   }

   public void testDeleteSnapshot() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-snapshots/1");
      VolumeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).method("DELETE").build(),
            standardResponseBuilder(200).payload(payloadFromResource("/snapshot_details.json")).build()
      ).getVolumeExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(client.deleteSnapshot("1"));
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testDeleteSnapshotFail() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-snapshots/1");
      VolumeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).method("DELETE").build(),
            standardResponseBuilder(401).build()
      ).getVolumeExtensionForZone("az-1.region-a.geo-1").get();

      client.deleteSnapshot("1");
   }
   
   protected Volume testVolume() {
      return Volume.builder().status(Volume.Status.IN_USE).description("This is a test volume").zone("nova").name("test")
            .attachments(ImmutableSet.of(testAttachment())).size(1).id("1").created(dateService.iso8601SecondsDateParse("2012-04-23 12:16:45")).build();
   }

   protected VolumeAttachment testAttachment() {
      return VolumeAttachment.builder().device("/dev/vdc").serverId("b4785058-cb80-491b-baa3-e4ee6546450e").id("1").volumeId("1").build();
   }

   protected VolumeSnapshot testSnapshot() {
      return VolumeSnapshot.builder().id("7").volumeId("9").description("jclouds live test snapshot").status(Volume.Status.AVAILABLE)
            .name("jclouds-live-test").size(1).created(dateService.iso8601SecondsDateParse("2012-04-24 13:34:42")).build();
   }
}