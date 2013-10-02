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
package org.jclouds.dynect.v3.features;

import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.HttpMethod.GET;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static org.testng.Assert.assertEquals;

import org.jclouds.dynect.v3.DynECTApi;
import org.jclouds.dynect.v3.internal.BaseDynECTApiExpectTest;
import org.jclouds.dynect.v3.parse.GetGeoServiceResponseTest;
import org.jclouds.dynect.v3.parse.ListGeoServicesResponseTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "GeoServiceApiExpectTest")
public class GeoServiceApiExpectTest extends BaseDynECTApiExpectTest {

   HttpRequest list = HttpRequest.builder().method(GET).endpoint("https://api2.dynect.net/REST/Geo")
         .addHeader("API-Version", "3.3.8").addHeader(CONTENT_TYPE, APPLICATION_JSON)
         .addHeader("Auth-Token", authToken).build();

   HttpResponse listResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/list_geo_services.json", APPLICATION_JSON)).build();

   public void testListWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, list, listResponse);
      assertEquals(success.getGeoServiceApi().list().toString(), new ListGeoServicesResponseTest().expected()
            .toString());
   }

   HttpRequest get = HttpRequest.builder().method(GET).endpoint("https://api2.dynect.net/REST/Geo/srv")
         .addHeader("API-Version", "3.3.8").addHeader(CONTENT_TYPE, APPLICATION_JSON)
         .addHeader("Auth-Token", authToken).build();

   HttpResponse getResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/get_geo_service.json", APPLICATION_JSON)).build();

   public void testGetWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, get, getResponse);
      assertEquals(success.getGeoServiceApi().get("srv").toString(), new GetGeoServiceResponseTest().expected()
            .toString());
   }
}
