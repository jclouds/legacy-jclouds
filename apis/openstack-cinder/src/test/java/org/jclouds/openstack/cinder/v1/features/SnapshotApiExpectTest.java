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
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.cinder.v1.domain.Snapshot;
import org.jclouds.openstack.cinder.v1.domain.Volume;
import org.jclouds.openstack.cinder.v1.internal.BaseCinderApiExpectTest;
import org.jclouds.openstack.cinder.v1.options.CreateSnapshotOptions;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Tests SnapshotApi Guice wiring and parsing
 *
 * @author Everett Toews
 */
@Test(groups = "unit", testName = "SnapshotApiExpectTest")
public class SnapshotApiExpectTest extends BaseCinderApiExpectTest {
   private DateService dateService = new SimpleDateFormatDateService();

   public void testListSnapshots() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/50cdb4c60374463198695d9f798fa34d/snapshots");
      SnapshotApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/snapshot_list_simple.json")).build()
      ).getSnapshotApiForZone("RegionOne");

      Set<? extends Snapshot> snapshots = api.list().toSet();
      assertEquals(snapshots, ImmutableSet.of(testSnapshot()));
   }

   public void testListSnapshotsFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/50cdb4c60374463198695d9f798fa34d/snapshots");
      SnapshotApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).build()
      ).getSnapshotApiForZone("RegionOne");

      Set<? extends Snapshot> snapshots = api.list().toSet();
      assertTrue(snapshots.isEmpty());
   }

   public void testListSnapshotsInDetail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/50cdb4c60374463198695d9f798fa34d/snapshots/detail");
      SnapshotApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/snapshot_list_details.json")).build()
      ).getSnapshotApiForZone("RegionOne");

      Set<? extends Snapshot> snapshots = api.listInDetail().toSet();
      assertEquals(snapshots, ImmutableSet.of(testSnapshot()));

      // double-check individual fields
      Snapshot snappy = Iterables.getOnlyElement(snapshots);
      assertEquals(snappy.getId(), "67d03df1-ce5d-4ba7-adbe-492ceb80170b");
      assertEquals(snappy.getVolumeId(), "ea6f70ef-2784-40b9-9d14-d7f33c507c3f");
      assertEquals(snappy.getStatus(), Volume.Status.AVAILABLE);
      assertEquals(snappy.getDescription(), "jclouds test snapshot");
      assertEquals(snappy.getName(), "jclouds-test-snapshot");
      assertEquals(snappy.getSize(), 1);
   }

   public void testListSnapshotsInDetailFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/50cdb4c60374463198695d9f798fa34d/snapshots/detail");
      SnapshotApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).build()
      ).getSnapshotApiForZone("RegionOne");

      Set<? extends Snapshot> snapshots = api.listInDetail().toSet();
      assertTrue(snapshots.isEmpty());
   }

   public void testGetSnapshot() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/50cdb4c60374463198695d9f798fa34d/snapshots/67d03df1-ce5d-4ba7-adbe-492ceb80170b");
      SnapshotApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/snapshot_get.json")).build()
      ).getSnapshotApiForZone("RegionOne");

      Snapshot snapshot = api.get("67d03df1-ce5d-4ba7-adbe-492ceb80170b");
      assertEquals(snapshot, testSnapshot());
   }

   public void testGetSnapshotFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/50cdb4c60374463198695d9f798fa34d/snapshots/67d03df1-ce5d-4ba7-adbe-492ceb80170b");
      SnapshotApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).build()
      ).getSnapshotApiForZone("RegionOne");

      assertNull(api.get("67d03df1-ce5d-4ba7-adbe-492ceb80170b"));
   }

   public void testCreateSnapshot() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/50cdb4c60374463198695d9f798fa34d/snapshots");
      SnapshotApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint)
                  .method("POST")
                  .payload(payloadFromResourceWithContentType("/snapshot_create.json", MediaType.APPLICATION_JSON))
                  .build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/snapshot_create_response.json")).build()
      ).getSnapshotApiForZone("RegionOne");
      
      CreateSnapshotOptions options = CreateSnapshotOptions.Builder
            .name("jclouds-test-snapshot")
            .description("jclouds test snapshot")
            .force();

      Snapshot snapshot = api.create("ea6f70ef-2784-40b9-9d14-d7f33c507c3f", options);
      assertEquals(snapshot, testSnapshotCreate());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testCreateSnapshotVolumeNotFoundFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/50cdb4c60374463198695d9f798fa34d/snapshots");
      SnapshotApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint)
                  .method("POST")
                  .payload(payloadFromResourceWithContentType("/snapshot_create.json", MediaType.APPLICATION_JSON))
                  .build(),
            HttpResponse.builder().statusCode(404).build()
      ).getSnapshotApiForZone("RegionOne");
      
      CreateSnapshotOptions options = CreateSnapshotOptions.Builder
            .name("jclouds-test-snapshot")
            .description("jclouds test snapshot")
            .force();

      api.create("ea6f70ef-2784-40b9-9d14-d7f33c507c3f", options);
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testCreateSnapshotVolumeIllegalStateFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/50cdb4c60374463198695d9f798fa34d/snapshots");
      SnapshotApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint)
                  .method("POST")
                  .payload(payloadFromResourceWithContentType("/snapshot_create.json", MediaType.APPLICATION_JSON))
                  .build(),
            HttpResponse.builder()
                  .statusCode(400)
                  .payload("{\"badRequest\": {\"message\": \"Invalid volume: must be available\", \"code\": 400}}")
                  .build()
      ).getSnapshotApiForZone("RegionOne");
      
      CreateSnapshotOptions options = CreateSnapshotOptions.Builder
            .name("jclouds-test-snapshot")
            .description("jclouds test snapshot")
            .force();

      api.create("ea6f70ef-2784-40b9-9d14-d7f33c507c3f", options);
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testCreateSnapshotFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/50cdb4c60374463198695d9f798fa34d/snapshots");
      SnapshotApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint)
                  .method("POST")
                  .payload(payloadFromResourceWithContentType("/snapshot_create.json", MediaType.APPLICATION_JSON))
                  .build(),
            HttpResponse.builder().statusCode(401).build()
      ).getSnapshotApiForZone("RegionOne");

      CreateSnapshotOptions options = CreateSnapshotOptions.Builder
            .name("jclouds-test-snapshot")
            .description("jclouds test snapshot")
            .force();

      api.create("ea6f70ef-2784-40b9-9d14-d7f33c507c3f", options);
   }

   public void testDeleteSnapshot() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/50cdb4c60374463198695d9f798fa34d/snapshots/67d03df1-ce5d-4ba7-adbe-492ceb80170b");
      SnapshotApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).method("DELETE").build(),
            HttpResponse.builder().statusCode(200).build()
      ).getSnapshotApiForZone("RegionOne");

      assertTrue(api.delete("67d03df1-ce5d-4ba7-adbe-492ceb80170b"));
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testDeleteSnapshotFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/50cdb4c60374463198695d9f798fa34d/snapshots/67d03df1-ce5d-4ba7-adbe-492ceb80170b");
      SnapshotApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).method("DELETE").build(),
            HttpResponse.builder().statusCode(401).build()
      ).getSnapshotApiForZone("RegionOne");

      api.delete("67d03df1-ce5d-4ba7-adbe-492ceb80170b");
   }

   public void testDeleteSnapshotNotFoundFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/50cdb4c60374463198695d9f798fa34d/snapshots/67d03df1-ce5d-4ba7-adbe-492ceb80170b");
      SnapshotApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).method("DELETE").build(),
            HttpResponse.builder().statusCode(404).build()
      ).getSnapshotApiForZone("RegionOne");

      assertFalse(api.delete("67d03df1-ce5d-4ba7-adbe-492ceb80170b"));
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testDeleteSnapshotIllegalStateFail() {
      URI endpoint = URI.create("http://172.16.0.1:8776/v1/50cdb4c60374463198695d9f798fa34d/snapshots/67d03df1-ce5d-4ba7-adbe-492ceb80170b");
      SnapshotApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint).method("DELETE").build(),
            HttpResponse.builder()
                  .statusCode(400)
                  .payload("{\"badRequest\": {\"message\": \"Invalid volume: Volume Snapshot status must be available or error\", \"code\": 400}}")
                  .build()
      ).getSnapshotApiForZone("RegionOne");

      api.delete("67d03df1-ce5d-4ba7-adbe-492ceb80170b");
   }

   protected Snapshot testSnapshotCreate() {
      return Snapshot.builder()
            .id("67d03df1-ce5d-4ba7-adbe-492ceb80170b")
            .volumeId("ea6f70ef-2784-40b9-9d14-d7f33c507c3f")
            .description("jclouds test snapshot")
            .status(Volume.Status.CREATING)
            .name("jclouds-test-snapshot")
            .size(1)
            .created(dateService.iso8601DateParse("2012-11-02T16:23:27.000000"))
            .build();
   }

   protected Snapshot testSnapshot() {
      return Snapshot.builder()
            .id("67d03df1-ce5d-4ba7-adbe-492ceb80170b")
            .volumeId("ea6f70ef-2784-40b9-9d14-d7f33c507c3f")
            .description("jclouds test snapshot")
            .status(Volume.Status.AVAILABLE)
            .name("jclouds-test-snapshot")
            .size(1)
            .created(dateService.iso8601DateParse("2012-11-02T16:23:27.000000"))
            .build();
   }
}
