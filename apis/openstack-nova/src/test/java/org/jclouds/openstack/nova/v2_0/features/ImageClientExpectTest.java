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
package org.jclouds.openstack.nova.v2_0.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.NovaClient;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaClientExpectTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseImageListTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseImageTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * Tests annotation parsing of {@code ImageAsyncClient}
 * 
 * @author Michael Arnold
 */
@Test(groups = "unit", testName = "ImageAsyncClientTest")
public class ImageClientExpectTest extends BaseNovaClientExpectTest {
   public void testListImagesWhenResponseIs2xx() throws Exception {
      HttpRequest listImages = HttpRequest
            .builder()
            .method("GET")
            .endpoint(URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/images"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build()).build();

      HttpResponse listImagesResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/image_list.json")).build();

      NovaClient clientWhenImagesExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, listImages, listImagesResponse);

      assertEquals(clientWhenImagesExist.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1"));

      assertEquals(clientWhenImagesExist.getImageClientForZone("az-1.region-a.geo-1").listImages().toString(),
            new ParseImageListTest().expected().toString());
   }

   public void testListImagesWhenReponseIs404IsEmpty() throws Exception {
      HttpRequest listImages = HttpRequest
            .builder()
            .method("GET")
            .endpoint(URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/images"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build()).build();

      HttpResponse listImagesResponse = HttpResponse.builder().statusCode(404).build();

      NovaClient clientWhenNoServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, listImages, listImagesResponse);

      assertTrue(clientWhenNoServersExist.getImageClientForZone("az-1.region-a.geo-1").listImages().isEmpty());
   }

   public void testGetImageWhenResponseIs2xx() throws Exception {

      HttpRequest getImage = HttpRequest
            .builder()
            .method("GET")
            .endpoint(URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/images/52415800-8b69-11e0-9b19-734f5736d2a2"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build()).build();

      HttpResponse getImageResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/image_details.json")).build();

      NovaClient clientWhenImagesExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, getImage, getImageResponse);

      assertEquals(
            clientWhenImagesExist.getImageClientForZone("az-1.region-a.geo-1").getImage("52415800-8b69-11e0-9b19-734f5736d2a2")
                  .toString(), new ParseImageTest().expected().toString());
   }

   public void testGetImageWhenResponseIs404() throws Exception {
      HttpRequest getImage = HttpRequest
            .builder()
            .method("GET")
            .endpoint(URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/images/52415800-8b69-11e0-9b19-734f5736d2a2"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build()).build();

      HttpResponse getImageResponse = HttpResponse.builder().statusCode(404).build();

      NovaClient clientWhenNoImagesExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, getImage, getImageResponse);

      assertNull(clientWhenNoImagesExist.getImageClientForZone("az-1.region-a.geo-1").getImage(
            "52415800-8b69-11e0-9b19-734f5736d2a2"));

   }

}
