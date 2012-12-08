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

package org.jclouds.googlecompute.features;

import org.jclouds.googlecompute.GoogleComputeApi;
import org.jclouds.googlecompute.internal.BaseGoogleComputeExpectTest;
import org.jclouds.googlecompute.options.ListOptions;
import org.jclouds.googlecompute.parse.ParseOperationListTest;
import org.jclouds.googlecompute.parse.ParseOperationTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import static org.jclouds.googlecompute.GoogleComputeConstants.COMPUTE_READONLY_SCOPE;
import static org.jclouds.googlecompute.GoogleComputeConstants.COMPUTE_SCOPE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * @author David Alves
 */
@Test(groups = "unit")
public class OperationApiExpectTest extends BaseGoogleComputeExpectTest<GoogleComputeApi> {

   public void testGetOperationResponseIs2xx() throws Exception {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1beta13/projects/myproject/operations/operation-1352178598164-4cdcc9d031510" +
                      "-4aa46279")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/operation.json")).build();

      OperationApi operationApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getOperationApi();

      assertEquals(operationApi.get("myproject", "operation-1352178598164-4cdcc9d031510-4aa46279"),
              new ParseOperationTest().expected());
   }

   public void testGetOperationResponseIs4xx() throws Exception {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1beta13/projects/myproject/operations/operation-1352178598164-4cdcc9d031510" +
                      "-4aa46279")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      OperationApi operationApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getOperationApi();

      assertNull(operationApi.get("myproject", "operation-1352178598164-4cdcc9d031510-4aa46279"));
   }

   public void testDeleteOperationResponseIs2xx() throws Exception {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1beta13/projects/myproject/operations/operation-1352178598164-4cdcc9d031510" +
                      "-4aa46279")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(204).build();

      OperationApi operationApi = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, operationResponse).getOperationApi();

      operationApi.delete("myproject", "operation-1352178598164-4cdcc9d031510-4aa46279");
   }

   public void testDeleteOperationResponseIs4xx() throws Exception {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1beta13/projects/myproject/operations/operation-1352178598164-4cdcc9d031510" +
                      "-4aa46279")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      OperationApi operationApi = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, operationResponse).getOperationApi();

      operationApi.delete("myproject", "operation-1352178598164-4cdcc9d031510-4aa46279");
   }

   public void testLisOperationWithNoOptionsResponseIs2xx() {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1beta13/projects/myproject/operations")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/operation_list.json")).build();

      OperationApi operationApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getOperationApi();

      assertEquals(operationApi.list("myproject", null).toString(),
              toPagedIterable(new ParseOperationListTest().expected()).toString());
   }

   public void testLisOperationWithPaginationOptionsResponseIs2xx() {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1beta13/projects/myproject/operations?" +
                      "filter=" +
                      "status%20eq%20done&" +
                      "maxResults=3&" +
                      "pageToken=CglPUEVSQVRJT04SOzU5MDQyMTQ4Nzg1Mi5vcG" +
                      "VyYXRpb24tMTM1MjI0NDI1ODAzMC00Y2RkYmU2YTJkNmIwLWVkMzIyMzQz")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/operation_list.json")).build();

      OperationApi operationApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getOperationApi();

      assertEquals(operationApi.list("myproject",
              ListOptions.builder().filter("status eq done").maxResults(3).nextPageToken
                      ("CglPUEVSQVRJT04SOzU5MDQyMTQ4Nzg1Mi5vcGVyYXRpb24tMTM1Mj" +
                              "I0NDI1ODAzMC00Y2RkYmU2YTJkNmIwLWVkMzIyMzQz").build()).toString(),
              toPagedIterable(new ParseOperationListTest().expected()).toString());
   }

   public void testLisOperationWithPaginationOptionsResponseIs4xx() {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1beta13/projects/myproject/operations")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      OperationApi operationApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getOperationApi();

      assertTrue(operationApi.list("myproject", ListOptions.NONE).concat().isEmpty());
   }


}
