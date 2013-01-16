/*
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

package org.jclouds.googlecompute;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecompute.domain.Image;
import org.jclouds.googlecompute.features.ImageApi;
import org.jclouds.googlecompute.internal.BaseGoogleComputeApiExpectTest;
import org.jclouds.googlecompute.options.ListOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import static org.jclouds.googlecompute.GoogleComputeConstants.COMPUTE_READONLY_SCOPE;
import static org.testng.Assert.assertSame;

/**
 * A test specifically for the paging system. The code used is common to all list() methods so we're using Images
 * but it could be anything else.
 *
 * @author David Alves
 */
@Test(groups = "unit")
public class PageSystemExpectTest extends BaseGoogleComputeApiExpectTest {

   public void testGetSinglePage() {
      HttpRequest list = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1beta13/projects/myproject/images")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/image_list_single_page.json")).build();

      ImageApi imageApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, list, operationResponse).getImageApiForProject("myproject");

      PagedIterable<Image> images = imageApi.list();

      // expect one page
      assertSame(images.size(), 1);
      // with three images
      assertSame(images.concat().size(), 3);
   }

   public void testGetMultiplePages() {
      HttpRequest list1 = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1beta13/projects/myproject/images?maxResults=3")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpRequest list2 = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1beta13/projects/myproject/images?pageToken" +
                      "=CgVJTUFHRRIbZ29vZ2xlLmNlbnRvcy02LTItdjIwMTIwNjIx&maxResults=3")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpRequest list3 = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1beta13/projects/myproject/images?pageToken" +
                      "=CgVJTUFHRRIbZ29vZ2xlLmdjZWwtMTAtMDQtdjIwMTIxMTA2&maxResults=3")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse list1response = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/image_list_multiple_page_1.json")).build();

      HttpResponse list2Response = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/image_list_multiple_page_2.json")).build();

      HttpResponse list3Response = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/image_list_single_page.json")).build();


      ImageApi imageApi = orderedRequestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, list1, list1response, list2, list2Response, list3, list3Response)
              .getImageApiForProject("myproject");

      PagedIterable<Image> images = imageApi.list(new ListOptions.Builder().maxResults(3));

      int imageCounter = 0;
      for (IterableWithMarker<Image> page : images) {
         for (Image image : page) {
            imageCounter++;
         }
      }
      assertSame(imageCounter, 9);
   }

}
