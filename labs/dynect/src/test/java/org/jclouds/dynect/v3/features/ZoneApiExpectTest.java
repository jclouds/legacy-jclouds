/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, String 2.0 (the
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
package org.jclouds.dynect.v3.features;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.jclouds.dynect.v3.DynECTApi;
import org.jclouds.dynect.v3.internal.BaseDynECTApiExpectTest;
import org.jclouds.dynect.v3.parse.GetZoneResponseTest;
import org.jclouds.dynect.v3.parse.ListZonesResponseTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ZoneApiExpectTest")
public class ZoneApiExpectTest extends BaseDynECTApiExpectTest {

   HttpRequest get = HttpRequest.builder().method("GET")
         .endpoint("https://api2.dynect.net/REST/Zone/jclouds.org")
         .addHeader("API-Version", "3.3.7")
         .addHeader("Auth-Token", authToken)
         .payload(emptyJsonPayload())
         .build();   

   HttpResponse getResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType("/get_zone.json", APPLICATION_JSON)).build();

   public void testGetWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, get, getResponse);
      assertEquals(success.getZoneApi().get("jclouds.org").toString(),
                   new GetZoneResponseTest().expected().toString());
   }

   public void testGetWhenResponseError2401() {
      DynECTApi fail = requestsSendResponses(createSession, createSessionResponse, get, notFound);
      assertNull(fail.getZoneApi().get("jclouds.org"));
   }

   HttpRequest list = HttpRequest.builder().method("GET")
         .endpoint("https://api2.dynect.net/REST/Zone")
         .addHeader("API-Version", "3.3.7")
         .addHeader("Auth-Token", authToken)
         .payload(emptyJsonPayload())
         .build();   

   HttpResponse listResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType("/list_zones.json", APPLICATION_JSON)).build();

   public void testListWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, list, listResponse);
      assertEquals(success.getZoneApi().list().toString(),
                   new ListZonesResponseTest().expected().toString());
   }
}
