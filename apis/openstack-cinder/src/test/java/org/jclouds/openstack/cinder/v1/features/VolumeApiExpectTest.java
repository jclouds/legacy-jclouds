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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.cinder.v1.domain.Volume;
import org.jclouds.openstack.cinder.v1.domain.VolumeAttachment;
import org.jclouds.openstack.cinder.v1.internal.BaseCinderApiExpectTest;
import org.jclouds.openstack.cinder.v1.options.CreateVolumeOptions;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Tests VolumeApi Guice wiring and parsing
 *
 * @author Everett Toews
 */
@Test(groups = "unit", testName = "VolumeApiExpectTest")
public class VolumeApiExpectTest extends BaseCinderApiExpectTest {
   private DateService dateService = new SimpleDateFormatDateService();

   public void testListVolumes() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/50cdb4c60374463198695d9f798fa34d/volumes");
      VolumeApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, 
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/volume_list_simple.json")).build()
      ).getVolumeApiForZone("RegionOne");

      Set<? extends Volume> volumes = api.list().toSet();
      assertEquals(volumes, ImmutableSet.of(testVolume()));
   }
   
   public void testListVolumesFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/50cdb4c60374463198695d9f798fa34d/volumes");
      VolumeApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, 
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).build()
      ).getVolumeApiForZone("RegionOne");

      Set<? extends Volume> volumes = api.list().toSet();
      assertTrue(volumes.isEmpty());
   }

   public void testListVolumesInDetail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/50cdb4c60374463198695d9f798fa34d/volumes/detail");
      VolumeApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/volume_list_details.json")).build()
      ).getVolumeApiForZone("RegionOne");

      Set<? extends Volume> volumes = api.listInDetail().toSet();
      assertEquals(volumes, ImmutableSet.of(testVolume()));
   }

   public void testListVolumesInDetailFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/50cdb4c60374463198695d9f798fa34d/volumes/detail");
      VolumeApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).build()
      ).getVolumeApiForZone("RegionOne");

      Set<? extends Volume> volumes = api.listInDetail().toSet();
      assertTrue(volumes.isEmpty());
   }

   public void testCreateVolume() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/50cdb4c60374463198695d9f798fa34d/volumes");
      VolumeApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint)
                  .method("POST")
                  .payload(payloadFromResourceWithContentType("/volume_create.json", MediaType.APPLICATION_JSON))
                  .build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/volume_create_response.json")).build()
      ).getVolumeApiForZone("RegionOne");

      CreateVolumeOptions options = CreateVolumeOptions.Builder
            .name("jclouds-test-volume")
            .description("description of test volume");
      Volume volume = api.create(1, options);
      assertEquals(volume, testVolumeCreate());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testCreateVolumeFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/50cdb4c60374463198695d9f798fa34d/volumes");
      VolumeApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint)
               .endpoint(endpoint)
               .method("POST")
                  .payload(payloadFromResourceWithContentType("/volume_create.json", MediaType.APPLICATION_JSON))
               .build(),
            HttpResponse.builder().statusCode(404).payload(payloadFromResource("/volume_create_response.json")).build()
      ).getVolumeApiForZone("RegionOne");

      CreateVolumeOptions options = CreateVolumeOptions.Builder
            .name("jclouds-test-volume")
            .description("description of test volume");
      api.create(1, options);
   }

   public void testGetVolume() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/50cdb4c60374463198695d9f798fa34d/volumes/60761c60-0f56-4499-b522-ff13e120af10");
      VolumeApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/volume_get.json")).build()
      ).getVolumeApiForZone("RegionOne");

      Volume volume = api.get("60761c60-0f56-4499-b522-ff13e120af10");
      assertEquals(volume, testVolume());
      // double-check equals()
      assertEquals(volume.getName(), "test");
      assertEquals(volume.getZone(), "nova");
      assertEquals(volume.getStatus(), Volume.Status.IN_USE);
      assertEquals(volume.getDescription(), "This is a test volume");
      assertEquals(Iterables.getOnlyElement(volume.getAttachments()), testAttachment());
   }

   public void testGetVolumeFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/50cdb4c60374463198695d9f798fa34d/volumes/60761c60-0f56-4499-b522-ff13e120af10");
      VolumeApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).build()
      ).getVolumeApiForZone("RegionOne");

      assertNull(api.get("60761c60-0f56-4499-b522-ff13e120af10"));
   }

   public void testDeleteVolume() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/50cdb4c60374463198695d9f798fa34d/volumes/60761c60-0f56-4499-b522-ff13e120af10");
      VolumeApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).method("DELETE").build(),
            HttpResponse.builder().statusCode(202).build()
      ).getVolumeApiForZone("RegionOne");

      assertTrue(api.delete("60761c60-0f56-4499-b522-ff13e120af10"));
   }

   public void testDeleteVolumeFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/50cdb4c60374463198695d9f798fa34d/volumes/60761c60-0f56-4499-b522-ff13e120af10");
      VolumeApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).method("DELETE").build(),
            HttpResponse.builder().statusCode(404).build()
      ).getVolumeApiForZone("RegionOne");

      assertFalse(api.delete("60761c60-0f56-4499-b522-ff13e120af10"));
   }
   
   protected Volume testVolumeCreate() {
      return Volume.builder()
            .id("60761c60-0f56-4499-b522-ff13e120af10")
            .size(1)
            .name("jclouds-test-volume")
            .zone("nova")
            .status(Volume.Status.CREATING)
            .volumeType("None")
            .description("description of test volume")
            .created(dateService.iso8601DateParse("2012-10-29T20:53:28.000000"))
            .build();
   }

   protected Volume testVolume() {
      return Volume.builder()
            .id("60761c60-0f56-4499-b522-ff13e120af10")
            .size(1)
            .name("test")
            .zone("nova")
            .status(Volume.Status.IN_USE)
            .volumeType("None")
            .description("This is a test volume")
            .attachments(ImmutableSet.of(testAttachment()))
            .created(dateService.iso8601DateParse("2012-10-29T20:53:28.000000"))
            .build();
   }

   protected VolumeAttachment testAttachment() {
      return VolumeAttachment.builder()
            .id("60761c60-0f56-4499-b522-ff13e120af10")
            .volumeId("60761c60-0f56-4499-b522-ff13e120af10")
            .serverId("0229a1c1-d54a-4836-8527-2ab28b42e2bb")
            .device("/dev/vdc")
            .build();
   }
}
