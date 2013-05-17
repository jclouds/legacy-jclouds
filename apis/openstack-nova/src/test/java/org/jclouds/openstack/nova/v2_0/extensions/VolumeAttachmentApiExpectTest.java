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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.domain.Volume;
import org.jclouds.openstack.nova.v2_0.domain.VolumeAttachment;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiExpectTest;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Tests VolumeAttachmentApi Guice wiring and parsing
 *
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "VolumeAttachmentApiExpectTest")
public class VolumeAttachmentApiExpectTest extends BaseNovaApiExpectTest {
   private DateService dateService = new SimpleDateFormatDateService();

   public void testListAttachments() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/instance-1/os-volume_attachments");
      VolumeAttachmentApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/attachment_list.json")).build()
      ).getVolumeAttachmentExtensionForZone("az-1.region-a.geo-1").get();

      Set<? extends VolumeAttachment> attachments = api.listAttachmentsOnServer("instance-1").toSet();
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
      VolumeAttachmentApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(401).build()
      ).getVolumeAttachmentExtensionForZone("az-1.region-a.geo-1").get();

      api.listAttachmentsOnServer("instance-2");
   }
   
   public void testGetAttachment() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/instance-1/os-volume_attachments/1");
      VolumeAttachmentApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/attachment_details.json")).build()
      ).getVolumeAttachmentExtensionForZone("az-1.region-a.geo-1").get();

      VolumeAttachment attachment = api.getAttachmentForVolumeOnServer("1", "instance-1");
      assertEquals(attachment, testAttachment());
   }

   public void testGetAttachmentFail() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/instance-1/os-volume_attachments/1");
      VolumeAttachmentApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).build()
      ).getVolumeAttachmentExtensionForZone("az-1.region-a.geo-1").get();

     assertNull(api.getAttachmentForVolumeOnServer("1", "instance-1"));
   }

   public void testAttachVolume() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/instance-1/os-volume_attachments");
      VolumeAttachmentApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).method("POST")
                  .payload(payloadFromStringWithContentType("{\"volumeAttachment\":{\"volumeId\":\"1\",\"device\":\"/dev/vdc\"}}", MediaType.APPLICATION_JSON)).endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/attachment_details.json")).build()
      ).getVolumeAttachmentExtensionForZone("az-1.region-a.geo-1").get();

      VolumeAttachment result = api.attachVolumeToServerAsDevice("1", "instance-1", "/dev/vdc");
      assertEquals(result, testAttachment());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testAttachVolumeFail() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/instance-1/os-volume_attachments");
      VolumeAttachmentApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).method("POST")
                  .payload(payloadFromStringWithContentType("{\"volumeAttachment\":{\"volumeId\":\"1\",\"device\":\"/dev/vdc\"}}", MediaType.APPLICATION_JSON)).endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).build()
      ).getVolumeAttachmentExtensionForZone("az-1.region-a.geo-1").get();

      api.attachVolumeToServerAsDevice("1", "instance-1","/dev/vdc");
   }

   public void testDetachVolume() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/instance-1/os-volume_attachments/1");
      VolumeAttachmentApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).method("DELETE").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/attachment_details.json")).build()
      ).getVolumeAttachmentExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(api.detachVolumeFromServer("1", "instance-1"));
   }

   public void testDetachVolumeFail() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/instance-1/os-volume_attachments/1");
      VolumeAttachmentApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).method("DELETE").build(),
            HttpResponse.builder().statusCode(404).build()
      ).getVolumeAttachmentExtensionForZone("az-1.region-a.geo-1").get();

      assertFalse(api.detachVolumeFromServer("1", "instance-1"));
   }
   
   protected Volume testVolume() {
      return Volume.builder().status(Volume.Status.IN_USE).description("This is a test volume").zone("nova").name("test")
            .attachments(ImmutableSet.of(testAttachment())).size(1).id("1").created(dateService.iso8601SecondsDateParse("2012-04-23 12:16:45")).build();
   }

   protected VolumeAttachment testAttachment() {
      return VolumeAttachment.builder().device("/dev/vdc").serverId("b4785058-cb80-491b-baa3-e4ee6546450e").id("1").volumeId("1").build();
   }
}
