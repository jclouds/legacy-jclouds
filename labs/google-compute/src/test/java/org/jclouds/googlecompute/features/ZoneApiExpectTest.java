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

import org.jclouds.googlecompute.internal.BaseGoogleComputeApiExpectTest;
import org.jclouds.googlecompute.parse.ParseZoneListTest;
import org.jclouds.googlecompute.parse.ParseZoneTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import static org.jclouds.googlecompute.GoogleComputeConstants.COMPUTE_READONLY_SCOPE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * @author David Alves
 */
@Test(groups = "unit")
public class ZoneApiExpectTest extends BaseGoogleComputeApiExpectTest {

   public static final String ZONES_URL_PREFIX = "https://www.googleapis.com/compute/v1beta13/projects/myproject/zones";

   public static final HttpRequest GET_ZONE_REQ = HttpRequest
           .builder()
           .method("GET")
           .endpoint(ZONES_URL_PREFIX + "/us-central1-a")
           .addHeader("Accept", "application/json")
           .addHeader("Authorization", "Bearer " + TOKEN).build();

   public static final HttpRequest LIST_ZONES_REQ = HttpRequest
           .builder()
           .method("GET")
           .endpoint(ZONES_URL_PREFIX)
           .addHeader("Accept", "application/json")
           .addHeader("Authorization", "Bearer " + TOKEN).build();

   public static final HttpResponse LIST_ZONES_RESPONSE = HttpResponse.builder().statusCode(200)
           .payload(staticPayloadFromResource("/zone_list.json")).build();


   public void testGetZoneResponseIs2xx() throws Exception {


      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/zone_get.json")).build();

      ZoneApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, GET_ZONE_REQ, operationResponse).getZoneApiForProject("myproject");

      assertEquals(api.get("us-central1-a"),
              new ParseZoneTest().expected());
   }

   public void testGetZoneResponseIs4xx() throws Exception {

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      ZoneApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, GET_ZONE_REQ, operationResponse).getZoneApiForProject("myproject");

      assertNull(api.get("us-central1-a"));
   }

   public void testListZoneNoOptionsResponseIs2xx() throws Exception {

      ZoneApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, LIST_ZONES_REQ, LIST_ZONES_RESPONSE).getZoneApiForProject("myproject");

      assertEquals(api.listFirstPage().toString(),
              new ParseZoneListTest().expected().toString());
   }

   public void testListZoneWithPaginationOptionsResponseIs4xx() {

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      ZoneApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, LIST_ZONES_REQ, operationResponse).getZoneApiForProject("myproject");

      assertTrue(api.list().concat().isEmpty());
   }
}
