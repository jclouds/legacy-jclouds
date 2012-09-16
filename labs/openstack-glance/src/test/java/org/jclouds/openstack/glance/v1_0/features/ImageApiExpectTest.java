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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payloads;
import org.jclouds.io.payloads.StringPayload;
import org.jclouds.openstack.glance.v1_0.GlanceApi;
import org.jclouds.openstack.glance.v1_0.functions.ParseImageDetailsFromHeadersTest;
import org.jclouds.openstack.glance.v1_0.internal.BaseGlanceApiExpectTest;
import org.jclouds.openstack.glance.v1_0.options.UpdateImageOptions;
import org.jclouds.openstack.glance.v1_0.parse.ParseImageDetailsTest;
import org.jclouds.openstack.glance.v1_0.parse.ParseImagesInDetailTest;
import org.jclouds.openstack.glance.v1_0.parse.ParseImagesTest;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "ImageApiExpectTest")
public class ImageApiExpectTest extends BaseGlanceApiExpectTest {

   public void testListWhenResponseIs2xx() throws Exception {
      HttpRequest list = HttpRequest.builder().method("GET")
            .endpoint("https://glance.jclouds.org:9292/v1.0/images")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();


      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/images.json")).build();

      GlanceApi apiWhenExist = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, list, listResponse);

      assertEquals(apiWhenExist.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1"));

      assertEquals(apiWhenExist.getImageApiForZone("az-1.region-a.geo-1").list().concat().toString(),
            new ParseImagesTest().expected().toString());
   }

   public void testListWhenReponseIs404IsEmpty() throws Exception {
      HttpRequest list = HttpRequest.builder().method("GET")
            .endpoint("https://glance.jclouds.org:9292/v1.0/images")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse listResponse = HttpResponse.builder().statusCode(404).build();

      GlanceApi apiWhenNoExist = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, list, listResponse);

      assertTrue(apiWhenNoExist.getImageApiForZone("az-1.region-a.geo-1").list().concat().isEmpty());
   }

   public void testListInDetailWhenResponseIs2xx() throws Exception {
      HttpRequest listInDetail = HttpRequest.builder().method("GET")
            .endpoint("https://glance.jclouds.org:9292/v1.0/images/detail")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();


      HttpResponse listInDetailResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/images_detail.json")).build();

      GlanceApi apiWhenExistInDetail = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, listInDetail, listInDetailResponse);

      assertEquals(apiWhenExistInDetail.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1"));

      assertEquals(apiWhenExistInDetail.getImageApiForZone("az-1.region-a.geo-1").listInDetail().concat().toString(),
            new ParseImagesInDetailTest().expected().toString());
   }

   public void testListInDetailWhenReponseIs404IsEmpty() throws Exception {
      HttpRequest listInDetail = HttpRequest.builder().method("GET")
            .endpoint("https://glance.jclouds.org:9292/v1.0/images/detail")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();


      HttpResponse listInDetailResponse = HttpResponse.builder().statusCode(404).build();

      GlanceApi apiWhenNoExistInDetail = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, listInDetail, listInDetailResponse);

      assertTrue(apiWhenNoExistInDetail.getImageApiForZone("az-1.region-a.geo-1").listInDetail().concat().isEmpty());
   }

   public void testShowWhenResponseIs2xx() throws Exception {
      HttpRequest show = HttpRequest
            .builder()
            .method("HEAD")
            .endpoint("https://glance.jclouds.org:9292/v1.0/images/fcc451d0-f6e4-4824-ad8f-70ec12326d07")
            .addHeader("X-Auth-Token", authToken).build();


      HttpResponse showResponse = new ParseImageDetailsFromHeadersTest().response;

      GlanceApi apiWhenExist = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, show, showResponse);

      assertEquals(apiWhenExist.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1"));

      assertEquals(apiWhenExist.getImageApiForZone("az-1.region-a.geo-1").get("fcc451d0-f6e4-4824-ad8f-70ec12326d07").toString(),
            new ParseImageDetailsFromHeadersTest().expected().toString());
   }

   public void testShowWhenReponseIs404IsNull() throws Exception {
      HttpRequest show = HttpRequest.builder().method("HEAD")
            .endpoint("https://glance.jclouds.org:9292/v1.0/images/fcc451d0-f6e4-4824-ad8f-70ec12326d07")
            .addHeader("X-Auth-Token", authToken).build();


      HttpResponse showResponse = HttpResponse.builder().statusCode(404).build();

      GlanceApi apiWhenNoExist = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, show, showResponse);

      assertNull(apiWhenNoExist.getImageApiForZone("az-1.region-a.geo-1").get("fcc451d0-f6e4-4824-ad8f-70ec12326d07"));
   }
   

   public void testGetAsStreamWhenResponseIs2xx() throws Exception {
      HttpRequest get = HttpRequest.builder().method("GET")
            .endpoint("https://glance.jclouds.org:9292/v1.0/images/fcc451d0-f6e4-4824-ad8f-70ec12326d07")
            .addHeader("X-Auth-Token", authToken).build();


      HttpResponse getResponse = HttpResponse.builder().statusCode(200).payload(Payloads.newStringPayload("foo")).build();
      
      GlanceApi apiWhenExist = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, get, getResponse);

      assertEquals(apiWhenExist.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1"));

      assertEquals(Strings2.toStringAndClose(apiWhenExist.getImageApiForZone("az-1.region-a.geo-1").getAsStream("fcc451d0-f6e4-4824-ad8f-70ec12326d07")),
               "foo");
   }

   public void testGetAsStreamWhenReponseIs404IsNull() throws Exception {
      HttpRequest get = HttpRequest.builder().method("GET")
            .endpoint("https://glance.jclouds.org:9292/v1.0/images/fcc451d0-f6e4-4824-ad8f-70ec12326d07")
            .addHeader("X-Auth-Token", authToken).build();


      HttpResponse getResponse = HttpResponse.builder().statusCode(404).build();

      GlanceApi apiWhenNoExist = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, get, getResponse);

      assertNull(apiWhenNoExist.getImageApiForZone("az-1.region-a.geo-1").getAsStream("fcc451d0-f6e4-4824-ad8f-70ec12326d07"));
   }

   public void testCreateWhenResponseIs2xx() throws Exception {
      HttpRequest get = HttpRequest.builder().method("POST")
            .endpoint("https://glance.jclouds.org:9292/v1.0/images")
            .addHeader("x-image-meta-name", "test")
            .addHeader("Accept", MediaType.APPLICATION_JSON)
            .addHeader("X-Auth-Token", authToken)
            .payload(payloadFromStringWithContentType("somedata", MediaType.APPLICATION_OCTET_STREAM)).build();

      HttpResponse createResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/image.json")).build();
      
      GlanceApi apiWhenExist = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, get, createResponse);

      assertEquals(apiWhenExist.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1"));

      assertEquals(apiWhenExist.getImageApiForZone("az-1.region-a.geo-1").create("test", new StringPayload("somedata")),
            new ParseImageDetailsTest().expected());
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testCreateWhenResponseIs4xx() throws Exception {
      HttpRequest get = HttpRequest.builder().method("POST")
            .endpoint("https://glance.jclouds.org:9292/v1.0/images")
            .addHeader("x-image-meta-name", "test")
            .addHeader("Accept", MediaType.APPLICATION_JSON)
            .addHeader("X-Auth-Token", authToken)
            .payload(payloadFromStringWithContentType("somedata", MediaType.APPLICATION_OCTET_STREAM)).build();

      HttpResponse createResponse = HttpResponse.builder().statusCode(401)
            .payload(payloadFromResource("/image.json")).build();

      GlanceApi apiWhenExist = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, get, createResponse);

      assertEquals(apiWhenExist.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1"));

      apiWhenExist.getImageApiForZone("az-1.region-a.geo-1").create("test", new StringPayload("somedata"));
   }

   public void testReserveWhenResponseIs2xx() throws Exception {
      HttpRequest get = HttpRequest.builder().method("POST")
            .endpoint("https://glance.jclouds.org:9292/v1.0/images")
            .addHeader("x-image-meta-name", "test")
            .addHeader("Accept", MediaType.APPLICATION_JSON)
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse createResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/image.json")).build();

      GlanceApi apiWhenExist = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, get, createResponse);

      assertEquals(apiWhenExist.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1"));

      assertEquals(apiWhenExist.getImageApiForZone("az-1.region-a.geo-1").reserve("test"), new ParseImageDetailsTest().expected());
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testReserveWhenResponseIs4xx() throws Exception {
      HttpRequest get = HttpRequest.builder().method("POST")
            .endpoint("https://glance.jclouds.org:9292/v1.0/images")
            .addHeader("x-image-meta-name", "test")
            .addHeader("Accept", MediaType.APPLICATION_JSON)
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse createResponse = HttpResponse.builder().statusCode(401)
            .payload(payloadFromResource("/image.json")).build();

      GlanceApi apiWhenExist = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, get, createResponse);

      assertEquals(apiWhenExist.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1"));

      apiWhenExist.getImageApiForZone("az-1.region-a.geo-1").reserve("test");
   }
   
   public void testUpdateMetadataWhenResponseIs2xx() throws Exception {
      HttpRequest get = HttpRequest.builder().method("PUT")
            .endpoint("https://glance.jclouds.org:9292/v1.0/images/fcc451d0-f6e4-4824-ad8f-70ec12326d07")
            .headers(
                  ImmutableMultimap.<String, String>builder()
                        .put("Accept", MediaType.APPLICATION_JSON)
                        .put("X-Image-Meta-Name", "newname")
                        .put("X-Image-Meta-Is_public", "true")
                        .put("X-Image-Meta-Protected", "true")
                        .put("X-Image-Meta-Checksum", "XXXX")
                        .put("X-Image-Meta-Location", "somewhere")
                        .put("X-Image-Meta-Min_disk", "10")
                        .put("X-Image-Meta-Min_ram", "2048")
                        .put("X-Auth-Token", authToken).build())
            .build();

      HttpResponse updateResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/image.json")).build();

      GlanceApi apiWhenExist = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, get, updateResponse);

      assertEquals(apiWhenExist.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1"));

      assertEquals(apiWhenExist.getImageApiForZone("az-1.region-a.geo-1")
            .update("fcc451d0-f6e4-4824-ad8f-70ec12326d07",
                  UpdateImageOptions.Builder.name("newname"),
                  UpdateImageOptions.Builder.isPublic(true),
                  UpdateImageOptions.Builder.isProtected(true),
                  UpdateImageOptions.Builder.checksum("XXXX"),
                  UpdateImageOptions.Builder.location("somewhere"),
                  UpdateImageOptions.Builder.minDisk(10),
                  UpdateImageOptions.Builder.minRam(2048)),
               new ParseImageDetailsTest().expected());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testUpdateMetadataWhenResponseIs4xx() throws Exception {
      HttpRequest get = HttpRequest.builder().method("PUT")
            .endpoint("https://glance.jclouds.org:9292/v1.0/images/fcc451d0-f6e4-4824-ad8f-70ec12326d07")
            .headers(
                  ImmutableMultimap.<String, String>builder()
                        .put("Accept", MediaType.APPLICATION_JSON)
                        .put("X-Image-Meta-Name", "newname")
                        .put("X-Image-Meta-Is_public", "true")
                        .put("X-Auth-Token", authToken).build())
            .build();

      HttpResponse updateResponse = HttpResponse.builder().statusCode(404).build();

      GlanceApi apiWhenExist = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, get, updateResponse);

      assertEquals(apiWhenExist.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1"));

      apiWhenExist.getImageApiForZone("az-1.region-a.geo-1")
            .update("fcc451d0-f6e4-4824-ad8f-70ec12326d07",
                  UpdateImageOptions.Builder.name("newname"),
                  UpdateImageOptions.Builder.isPublic(true));
   }

   public void testUpdateImageWhenResponseIs2xx() throws Exception {
      HttpRequest get = HttpRequest.builder().method("PUT")
            .endpoint("https://glance.jclouds.org:9292/v1.0/images/fcc451d0-f6e4-4824-ad8f-70ec12326d07")
            .addHeader("Accept", MediaType.APPLICATION_JSON)
            .addHeader("X-Auth-Token", authToken)
            .payload(payloadFromStringWithContentType("somenewdata", MediaType.APPLICATION_OCTET_STREAM))
            .build();

      HttpResponse updateResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/image.json")).build();


      GlanceApi apiWhenExist = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, get, updateResponse);

      assertEquals(apiWhenExist.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1"));

      assertEquals(apiWhenExist.getImageApiForZone("az-1.region-a.geo-1").upload("fcc451d0-f6e4-4824-ad8f-70ec12326d07",
            new StringPayload("somenewdata")), new ParseImageDetailsTest().expected());
   }

   public void testUpdateNameAndImageWhenResponseIs2xx() throws Exception {
      HttpRequest get = HttpRequest.builder().method("PUT")
            .endpoint("https://glance.jclouds.org:9292/v1.0/images/fcc451d0-f6e4-4824-ad8f-70ec12326d07")
            .headers(
                  ImmutableMultimap.<String, String>builder()
                        .put("Accept", MediaType.APPLICATION_JSON)
                        .put("X-Image-Meta-Name", "anothernewname")
                        .put("X-Auth-Token", authToken).build())
            .payload(payloadFromStringWithContentType("somenewdata", MediaType.APPLICATION_OCTET_STREAM))
            .build();

      HttpResponse updateResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/image.json")).build();


      GlanceApi apiWhenExist = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, get, updateResponse);

      assertEquals(apiWhenExist.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1"));

      assertEquals(apiWhenExist.getImageApiForZone("az-1.region-a.geo-1").upload("fcc451d0-f6e4-4824-ad8f-70ec12326d07",
            new StringPayload("somenewdata"), UpdateImageOptions.Builder.name("anothernewname")), new ParseImageDetailsTest().expected());
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testUpdateNameAndImageWhenResponseIs4xx() throws Exception {
      HttpRequest get = HttpRequest.builder().method("PUT")
            .endpoint("https://glance.jclouds.org:9292/v1.0/images/fcc451d0-f6e4-4824-ad8f-70ec12326d07")
            .headers(
                  ImmutableMultimap.<String, String>builder()
                        .put("Accept", MediaType.APPLICATION_JSON)
                        .put("X-Image-Meta-Name", "anothernewname")
                        .put("X-Auth-Token", authToken).build())
            .payload(payloadFromStringWithContentType("somenewdata", MediaType.APPLICATION_OCTET_STREAM))
            .build();

      HttpResponse updateResponse = HttpResponse.builder().statusCode(403).build();

      GlanceApi apiWhenExist = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, get, updateResponse);

      assertEquals(apiWhenExist.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1"));

      apiWhenExist.getImageApiForZone("az-1.region-a.geo-1").upload("fcc451d0-f6e4-4824-ad8f-70ec12326d07",
            new StringPayload("somenewdata"), UpdateImageOptions.Builder.name("anothernewname"));
   }

   public void testDeleteWhenResponseIs2xx() throws Exception {
      HttpRequest get = HttpRequest.builder().method("DELETE")
            .endpoint("https://glance.jclouds.org:9292/v1.0/images/fcc451d0-f6e4-4824-ad8f-70ec12326d07")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse getResponse = HttpResponse.builder().statusCode(200).build();

      GlanceApi apiWhenExist = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, get, getResponse);

      assertEquals(apiWhenExist.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1"));

      assertTrue(apiWhenExist.getImageApiForZone("az-1.region-a.geo-1").delete("fcc451d0-f6e4-4824-ad8f-70ec12326d07"));
   }

   public void testDeleteWhenResponseIs4xx() throws Exception {
      HttpRequest get = HttpRequest.builder().method("DELETE")
            .endpoint("https://glance.jclouds.org:9292/v1.0/images/fcc451d0-f6e4-4824-ad8f-70ec12326d07")
            .addHeader("X-Auth-Token", authToken).build();


      HttpResponse getResponse = HttpResponse.builder().statusCode(404).build();

      GlanceApi apiWhenExist = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, get, getResponse);

      assertEquals(apiWhenExist.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1"));

      assertFalse(apiWhenExist.getImageApiForZone("az-1.region-a.geo-1").delete("fcc451d0-f6e4-4824-ad8f-70ec12326d07"));
   }
}
