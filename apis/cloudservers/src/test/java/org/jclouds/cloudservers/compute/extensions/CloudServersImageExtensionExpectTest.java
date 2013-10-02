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
package org.jclouds.cloudservers.compute.extensions;

import static org.testng.Assert.assertEquals;

import org.jclouds.cloudservers.internal.BaseCloudServersComputeServiceExpectTest;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageTemplate;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.net.HttpHeaders;
import com.google.common.util.concurrent.Futures;

/**
 * 
 * @author David Alves
 * 
 */
@Test(groups = "unit", testName = "CloudServersImageExtensionExpectTest")
public class CloudServersImageExtensionExpectTest extends BaseCloudServersComputeServiceExpectTest<ComputeService> {
   private HttpRequest getServerDetail = HttpRequest.builder().method("GET")
         .endpoint("https://lon.servers.api.rackspacecloud.com/v1.0/10001786/servers/1234?format=json&now=1257695648897")
         .addHeader(HttpHeaders.ACCEPT, "application/json")
         .addHeader("X-Auth-Token", authToken).build();

   private HttpResponse getServerResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResource("/test_get_server_detail.json")).build();

   HttpRequest createImage = HttpRequest.builder().method("POST")
         .endpoint("https://lon.servers.api.rackspacecloud.com/v1.0/10001786/images?format=json&now=1257695648897")
         .addHeader("Accept", "application/json")
         .addHeader("X-Auth-Token", authToken)
         .payload(
               payloadFromStringWithContentType(
                     "{\"image\":{\"serverId\":1234,\"name\":\"test\"}}",
                     "application/json")).build();
   
   HttpResponse createImageResponse = HttpResponse.builder().statusCode(200)
         .payload(
               payloadFromStringWithContentType(
                     "{\"image\":{\"id\":2,\"serverId\":1234,\"name\":\"test\",\"status\":\"SAVING\"}}",
                     "application/json")).build();
   
   private HttpRequest getImage = HttpRequest.builder().method("GET")
         .endpoint("https://lon.servers.api.rackspacecloud.com/v1.0/10001786/images/2?format=json&now=1257695648897")
         .addHeader(HttpHeaders.ACCEPT, "application/json")
         .addHeader("X-Auth-Token", authToken).build();

   private HttpResponse getImageResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResource("/test_get_image_active.json")).build();


   public void testCreateImage() {
      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder();
      requestResponseMap.put(initialAuth, responseWithAuth);
      requestResponseMap.put(getServerDetail, getServerResponse).build();
      requestResponseMap.put(createImage, createImageResponse).build();
      requestResponseMap.put(getImage, getImageResponse).build();

      ImageExtension apiThatCreatesImage = requestsSendResponses(requestResponseMap.build()).getImageExtension().get();
      
      ImageTemplate newImageTemplate = apiThatCreatesImage.buildImageTemplateFromNode("test", "1234");

      Image image = Futures.getUnchecked(apiThatCreatesImage.createImage(newImageTemplate));
      assertEquals(image.getId(), "2");
   }
   
   public ComputeService apply(ComputeServiceContext input) {
      return input.getComputeService();
   }
}
