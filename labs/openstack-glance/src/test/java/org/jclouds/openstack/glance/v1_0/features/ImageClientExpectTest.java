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
 * Unles required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either expres or implied.  See the License for the
 * specific language governing permisions and limitations
 * under the License.
 */
package org.jclouds.openstack.glance.v1_0.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payloads;
import org.jclouds.openstack.glance.v1_0.GlanceClient;
import org.jclouds.openstack.glance.v1_0.functions.ParseImageDetailsFromHeadersTest;
import org.jclouds.openstack.glance.v1_0.internal.BaseGlanceClientExpectTest;
import org.jclouds.openstack.glance.v1_0.parse.ParseImagesInDetailTest;
import org.jclouds.openstack.glance.v1_0.parse.ParseImagesTest;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ImageClientExpectTest")
public class ImageClientExpectTest extends BaseGlanceClientExpectTest {

   public void testListWhenResponseIs2xx() throws Exception {
      HttpRequest list = HttpRequest
            .builder()
            .method("GET")
            .endpoint(URI.create("https://glance.jclouds.org:9292/v1.0/images"))
            .headers(
                  ImmutableMultimap.<String, String> builder()
                     .put("Accept", "application/json")
                     .put("X-Auth-Token", authToken).build()).build();

      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/images.json")).build();

      GlanceClient clientWhenExist = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, list, listResponse);

      assertEquals(clientWhenExist.getConfiguredRegions(), ImmutableSet.of("az-1.region-a.geo-1"));

      assertEquals(clientWhenExist.getImageClientForRegion("az-1.region-a.geo-1").list().toString(),
            new ParseImagesTest().expected().toString());
   }

   public void testListWhenReponseIs404IsEmpty() throws Exception {
      HttpRequest list = HttpRequest
            .builder()
            .method("GET")
            .endpoint(URI.create("https://glance.jclouds.org:9292/v1.0/images"))
            .headers(
                  ImmutableMultimap.<String, String> builder()
                     .put("Accept", "application/json")
                     .put("X-Auth-Token", authToken).build()).build();

      HttpResponse listResponse = HttpResponse.builder().statusCode(404).build();

      GlanceClient clientWhenNoExist = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, list, listResponse);

      assertTrue(clientWhenNoExist.getImageClientForRegion("az-1.region-a.geo-1").list().isEmpty());
   }

   public void testListInDetailWhenResponseIs2xx() throws Exception {
      HttpRequest listInDetail = HttpRequest
            .builder()
            .method("GET")
            .endpoint(URI.create("https://glance.jclouds.org:9292/v1.0/images/detail"))
            .headers(
                  ImmutableMultimap.<String, String> builder()
                     .put("Accept", "application/json")
                     .put("X-Auth-Token", authToken).build()).build();

      HttpResponse listInDetailResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/images_detail.json")).build();

      GlanceClient clientWhenExistInDetail = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, listInDetail, listInDetailResponse);

      assertEquals(clientWhenExistInDetail.getConfiguredRegions(), ImmutableSet.of("az-1.region-a.geo-1"));

      assertEquals(clientWhenExistInDetail.getImageClientForRegion("az-1.region-a.geo-1").listInDetail().toString(),
            new ParseImagesInDetailTest().expected().toString());
   }

   public void testListInDetailWhenReponseIs404IsEmpty() throws Exception {
      HttpRequest listInDetail = HttpRequest
            .builder()
            .method("GET")
            .endpoint(URI.create("https://glance.jclouds.org:9292/v1.0/images/detail"))
            .headers(
                  ImmutableMultimap.<String, String> builder()
                     .put("Accept", "application/json")
                     .put("X-Auth-Token", authToken).build()).build();

      HttpResponse listInDetailResponse = HttpResponse.builder().statusCode(404).build();

      GlanceClient clientWhenNoExistInDetail = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, listInDetail, listInDetailResponse);

      assertTrue(clientWhenNoExistInDetail.getImageClientForRegion("az-1.region-a.geo-1").listInDetail().isEmpty());
   }

   public void testShowWhenResponseIs2xx() throws Exception {
      HttpRequest show = HttpRequest
            .builder()
            .method("HEAD")
            .endpoint(URI.create("https://glance.jclouds.org:9292/v1.0/images/fcc451d0-f6e4-4824-ad8f-70ec12326d07"))
            .headers(
                  ImmutableMultimap.<String, String> builder()
                     .put("X-Auth-Token", authToken).build()).build();

      HttpResponse showResponse = new ParseImageDetailsFromHeadersTest().response;

      GlanceClient clientWhenExist = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, show, showResponse);

      assertEquals(clientWhenExist.getConfiguredRegions(), ImmutableSet.of("az-1.region-a.geo-1"));

      assertEquals(clientWhenExist.getImageClientForRegion("az-1.region-a.geo-1").show("fcc451d0-f6e4-4824-ad8f-70ec12326d07").toString(),
            new ParseImageDetailsFromHeadersTest().expected().toString());
   }

   public void testShowWhenReponseIs404IsNull() throws Exception {
      HttpRequest show = HttpRequest
            .builder()
            .method("HEAD")
            .endpoint(URI.create("https://glance.jclouds.org:9292/v1.0/images/fcc451d0-f6e4-4824-ad8f-70ec12326d07"))
            .headers(
                  ImmutableMultimap.<String, String> builder()
                     .put("X-Auth-Token", authToken).build()).build();

      HttpResponse showResponse = HttpResponse.builder().statusCode(404).build();

      GlanceClient clientWhenNoExist = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, show, showResponse);

      assertNull(clientWhenNoExist.getImageClientForRegion("az-1.region-a.geo-1").show("fcc451d0-f6e4-4824-ad8f-70ec12326d07"));
   }
   

   public void testGetAsStreamWhenResponseIs2xx() throws Exception {
      HttpRequest get = HttpRequest
            .builder()
            .method("GET")
            .endpoint(URI.create("https://glance.jclouds.org:9292/v1.0/images/fcc451d0-f6e4-4824-ad8f-70ec12326d07"))
            .headers(
                  ImmutableMultimap.<String, String> builder()
                     .put("X-Auth-Token", authToken).build()).build();

      HttpResponse getResponse = HttpResponse.builder().statusCode(200).payload(Payloads.newStringPayload("foo")).build();
      
      GlanceClient clientWhenExist = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, get, getResponse);

      assertEquals(clientWhenExist.getConfiguredRegions(), ImmutableSet.of("az-1.region-a.geo-1"));

      assertEquals(Strings2.toStringAndClose(clientWhenExist.getImageClientForRegion("az-1.region-a.geo-1").getAsStream("fcc451d0-f6e4-4824-ad8f-70ec12326d07")),
               "foo");
   }

   public void testGetAsStreamWhenReponseIs404IsNull() throws Exception {
      HttpRequest get = HttpRequest
            .builder()
            .method("GET")
            .endpoint(URI.create("https://glance.jclouds.org:9292/v1.0/images/fcc451d0-f6e4-4824-ad8f-70ec12326d07"))
            .headers(
                  ImmutableMultimap.<String, String> builder()
                     .put("X-Auth-Token", authToken).build()).build();

      HttpResponse getResponse = HttpResponse.builder().statusCode(404).build();

      GlanceClient clientWhenNoExist = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, get, getResponse);

      assertNull(clientWhenNoExist.getImageClientForRegion("az-1.region-a.geo-1").getAsStream("fcc451d0-f6e4-4824-ad8f-70ec12326d07"));
   }
   
}
