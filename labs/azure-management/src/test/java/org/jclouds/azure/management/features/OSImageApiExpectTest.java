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
package org.jclouds.azure.management.features;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.azure.management.AzureManagementApi;
import org.jclouds.azure.management.domain.OSImageParams;
import org.jclouds.azure.management.domain.OSType;
import org.jclouds.azure.management.internal.BaseAzureManagementApiExpectTest;
import org.jclouds.azure.management.parse.ListOSImagesTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "OSImageApiExpectTest")
public class OSImageApiExpectTest extends BaseAzureManagementApiExpectTest {

   private static final String IMAGE_NAME = "myimage";
   
   HttpRequest list = HttpRequest.builder().method("GET")
                                 .endpoint("https://management.core.windows.net/" + subscriptionId + "/services/images")
                                 .addHeader("x-ms-version", "2012-03-01")
                                 .addHeader("Accept", "application/xml").build();
   
   public void testListWhenResponseIs2xx() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/images.xml", "application/xml")).build();

      AzureManagementApi apiWhenExist = requestSendsResponse(
            list, listResponse);

      assertEquals(apiWhenExist.getOSImageApi().list().toString(), new ListOSImagesTest().expected().toString());
   }

   public void testListWhenResponseIs404() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(404).build();

      AzureManagementApi apiWhenDontExist = requestSendsResponse(
            list, listResponse);

      assertEquals(apiWhenDontExist.getOSImageApi().list(), ImmutableSet.of());
   }

   HttpRequest add = HttpRequest.builder().method("POST")
            .endpoint("https://management.core.windows.net/" + subscriptionId + "/services/images")
            .addHeader("x-ms-version", "2012-03-01")
            .payload(payloadFromResourceWithContentType("/imageparams.xml", "application/xml")).build();
   
   public void testAddWhenResponseIs2xx() throws Exception {
      HttpResponse addResponse = HttpResponse.builder().statusCode(200).build();

      AzureManagementApi apiWhenExist = requestSendsResponse(add, addResponse);

      OSImageParams params = OSImageParams.builder().name(IMAGE_NAME).label("foo").os(OSType.LINUX)
               .mediaLink(URI.create("http://example.blob.core.windows.net/disks/mydisk.vhd")).build();
      apiWhenExist.getOSImageApi().add(params);
   }

   HttpRequest update = HttpRequest.builder().method("PUT")
            .endpoint("https://management.core.windows.net/" + subscriptionId + "/services/images/" + IMAGE_NAME)
            .addHeader("x-ms-version", "2012-03-01")
            .payload(payloadFromResourceWithContentType("/imageparams.xml", "application/xml")).build();
   
   public void testUpdateWhenResponseIs2xx() throws Exception {
      HttpResponse updateResponse = HttpResponse.builder().statusCode(200).build();

      AzureManagementApi apiWhenExist = requestSendsResponse(update, updateResponse);

      OSImageParams params = OSImageParams.builder().name(IMAGE_NAME).label("foo").os(OSType.LINUX)
               .mediaLink(URI.create("http://example.blob.core.windows.net/disks/mydisk.vhd")).build();
      apiWhenExist.getOSImageApi().update(params);
   }
   
   HttpRequest delete = HttpRequest.builder().method("DELETE")
            .endpoint("https://management.core.windows.net/" + subscriptionId + "/services/images/" + IMAGE_NAME)
            .addHeader("x-ms-version", "2012-03-01")
            .build();
   
   public void testDeleteWhenResponseIs2xx() throws Exception {
      HttpResponse deleteResponse = HttpResponse.builder().statusCode(200).build();

      AzureManagementApi apiWhenExist = requestSendsResponse(delete, deleteResponse);

      apiWhenExist.getOSImageApi().delete(IMAGE_NAME);
   }

   public void testDeleteWhenResponseIs404() throws Exception {
      HttpResponse deleteResponse = HttpResponse.builder().statusCode(404).build();

      AzureManagementApi apiWhenDontExist = requestSendsResponse(delete, deleteResponse);

      apiWhenDontExist.getOSImageApi().delete(IMAGE_NAME);
   }
}
