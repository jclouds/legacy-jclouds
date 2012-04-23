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

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v1_1.domain.Attachment;
import org.jclouds.openstack.nova.v1_1.domain.Volume;
import org.jclouds.openstack.nova.v1_1.internal.BaseNovaClientExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
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
      URI endpoint = URI.create("https://compute.north.host/v1.1/3456/os-volumes");
      VolumeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            HttpRequest.builder().method("GET").headers(ImmutableMultimap.of("Accept", MediaType.APPLICATION_JSON, "X-Auth-Token", authToken))
                  .endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/volume_list.json")).build()
      ).getVolumeExtensionForZone("az-1.region-a.geo-1").get();

      Set<Volume> volumes = client.listVolumes();
      assertEquals(volumes, ImmutableSet.of(testVolume()));
   }


   public void testListVolumesInDetail() {
      URI endpoint = URI.create("https://compute.north.host/v1.1/3456/os-volumes/detail");
      VolumeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            HttpRequest.builder().method("GET").headers(ImmutableMultimap.of("Accept", MediaType.APPLICATION_JSON, "X-Auth-Token", authToken))
                  .endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/volume_list_detail.json")).build()
      ).getVolumeExtensionForZone("az-1.region-a.geo-1").get();

      Set<Volume> volumes = client.listVolumesInDetail();
      assertEquals(volumes, ImmutableSet.of(testVolume()));
   }

   public void testGetVolume() {
      URI endpoint = URI.create("https://compute.north.host/v1.1/3456/os-volumes/1");
      VolumeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            HttpRequest.builder().method("GET").headers(ImmutableMultimap.of("Accept", MediaType.APPLICATION_JSON, "X-Auth-Token", authToken))
                  .endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/volume_details.json")).build()
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

   public void testListAttachments() {
      URI endpoint = URI.create("https://compute.north.host/v1.1/3456/servers/instance-1/os-volume_attachments");
      VolumeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            HttpRequest.builder().method("GET").headers(ImmutableMultimap.of("Accept", MediaType.APPLICATION_JSON, "X-Auth-Token", authToken))
                  .endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/attachment_list.json")).build()
      ).getVolumeExtensionForZone("az-1.region-a.geo-1").get();

      Set<Attachment> attachments = client.listAttachments("instance-1");
      assertEquals(attachments, ImmutableSet.of(testAttachment()));
      // double-check equals()
      Attachment attachment = Iterables.getOnlyElement(attachments);
      assertEquals(attachment.getDevice(), "/dev/vdc");
      assertEquals(attachment.getServerId(), "b4785058-cb80-491b-baa3-e4ee6546450e");
      assertEquals(attachment.getId(), "1");
      assertEquals(attachment.getVolumeId(), "1");
   }
   
   protected Volume testVolume() {
      return Volume.builder().status(Volume.Status.IN_USE).description("This is a test volume").zone("nova").name("test")
            .attachments(ImmutableSet.of(testAttachment())).size(1).id("1").created(dateService.iso8601SecondsDateParse("2012-04-23 12:16:45")).build();
   }

   protected Attachment testAttachment() {
      return Attachment.builder().device("/dev/vdc").serverId("b4785058-cb80-491b-baa3-e4ee6546450e").id("1").volumeId("1").build();
   }

}