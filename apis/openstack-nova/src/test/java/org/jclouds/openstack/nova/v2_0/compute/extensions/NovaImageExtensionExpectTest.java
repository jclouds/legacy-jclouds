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
package org.jclouds.openstack.nova.v2_0.compute.extensions;

import static org.testng.Assert.assertEquals;

import java.util.Properties;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageTemplate;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaComputeServiceExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.util.concurrent.Futures;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "NovaImageExtensionExpectTest")
public class NovaImageExtensionExpectTest extends BaseNovaComputeServiceExpectTest {
   
   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();
      overrides.setProperty("jclouds.zones", "az-1.region-a.geo-1");
      return overrides;
   }

   HttpRequest serverDetail = HttpRequest.builder().method("GET")
         .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/71752")
         .addHeader("Accept", "application/json")
         .addHeader("X-Auth-Token", authToken).build();

   HttpResponse serverDetailResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResource("/server_details.json")).build();

   HttpRequest createImage = HttpRequest.builder().method("POST")
         .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/71752/action")
         .addHeader("Accept", "application/json")
         .addHeader("X-Auth-Token", authToken)
         .payload(
               payloadFromStringWithContentType(
                     "{\"createImage\":{\"name\":\"test\", \"metadata\": {}}}",
                     "application/json")).build();
   
   HttpResponse createImageResponse = HttpResponse.builder().statusCode(202)
         .addHeader("Location", "https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/images/52415800-8b69-11e0-9b19-734f5736d2a2")
         .build();

   HttpRequest getImage = HttpRequest.builder().method("GET")
         .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/images/52415800-8b69-11e0-9b19-734f5736d2a2")
         .addHeader("Accept", "application/json")
         .addHeader("X-Auth-Token", authToken).build();
   
   HttpResponse getImageResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResource("/image_active.json")).build();

   public void testCreateImage() {
      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder();
      requestResponseMap.put(keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess);
      requestResponseMap.put(serverDetail, serverDetailResponse).build();
      requestResponseMap.put(createImage, createImageResponse).build();
      requestResponseMap.put(getImage, getImageResponse).build();

      ImageExtension apiThatCreatesImage = requestsSendResponses(requestResponseMap.build()).getImageExtension().get();
      
      ImageTemplate newImageTemplate = apiThatCreatesImage.buildImageTemplateFromNode("test", "az-1.region-a.geo-1/71752");

      Image image = Futures.getUnchecked(apiThatCreatesImage.createImage(newImageTemplate));
      assertEquals(image.getId(), "az-1.region-a.geo-1/52415800-8b69-11e0-9b19-734f5736d2a2");
   }
}
