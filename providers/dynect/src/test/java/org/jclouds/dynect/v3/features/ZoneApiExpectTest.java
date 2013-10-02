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
import static com.google.common.net.HttpHeaders.ACCEPT;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.HttpMethod.DELETE;
import static javax.ws.rs.HttpMethod.GET;
import static javax.ws.rs.HttpMethod.POST;
import static javax.ws.rs.HttpMethod.PUT;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.jclouds.dynect.v3.DynECTApi;
import org.jclouds.dynect.v3.domain.CreatePrimaryZone;
import org.jclouds.dynect.v3.domain.Job;
import org.jclouds.dynect.v3.internal.BaseDynECTApiExpectTest;
import org.jclouds.dynect.v3.parse.DeleteZoneChangesResponseTest;
import org.jclouds.dynect.v3.parse.DeleteZoneResponseTest;
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
   HttpRequest get = HttpRequest.builder().method(GET)
                                .endpoint("https://api2.dynect.net/REST/Zone/jclouds.org")
                                .addHeader("API-Version", "3.3.8")
                                .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                                .addHeader("Auth-Token", authToken).build();

   HttpResponse getResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/get_zone.json", APPLICATION_JSON)).build();

   public void testGetWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, get, getResponse);
      assertEquals(success.getZoneApi().get("jclouds.org").toString(),
                   new GetZoneResponseTest().expected().toString());
   }

   HttpRequest create = HttpRequest.builder().method(POST)
         .endpoint("https://api2.dynect.net/REST/Zone/jclouds.org")
         .addHeader("API-Version", "3.3.8")
         .addHeader(ACCEPT, APPLICATION_JSON)
         .addHeader("Auth-Token", authToken)
         .payload(stringPayload("{\"rname\":\"jimmy@jclouds.org\",\"serial_style\":\"increment\",\"ttl\":3600}"))
         .build();   

   HttpResponse createResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/new_zone.json", APPLICATION_JSON)).build();

   public void testCreateWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, create, createResponse);
      assertEquals(success.getZoneApi().scheduleCreate(CreatePrimaryZone.builder()
                                                                        .fqdn("jclouds.org")
                                                                        .contact("jimmy@jclouds.org")
                                                                        .build()), Job.success(285351593l));
   }

   public void testCreateWithContactWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, create, createResponse);
      assertEquals(success.getZoneApi().scheduleCreateWithContact("jclouds.org", "jimmy@jclouds.org"), Job.success(285351593l));
   }

   public void testGetWhenResponseIs404() {
      DynECTApi fail = requestsSendResponses(createSession, createSessionResponse, get, notFound);
      assertNull(fail.getZoneApi().get("jclouds.org"));
   }

   HttpRequest list = HttpRequest.builder().method(GET)
                                 .endpoint("https://api2.dynect.net/REST/Zone")
                                 .addHeader("API-Version", "3.3.8")
                                 .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                                 .addHeader("Auth-Token", authToken).build();  

   HttpResponse listResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/list_zones.json", APPLICATION_JSON)).build();

   public void testListWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, list, listResponse);
      assertEquals(success.getZoneApi().list().toString(),
                   new ListZonesResponseTest().expected().toString());
   }

   HttpRequest deleteChanges = HttpRequest.builder().method(DELETE)
                                          .endpoint("https://api2.dynect.net/REST/ZoneChanges/jclouds.org")
                                          .addHeader("API-Version", "3.3.8")
                                          .addHeader(ACCEPT, APPLICATION_JSON)
                                          .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                                          .addHeader("Auth-Token", authToken).build();

   HttpResponse deleteChangesResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/delete_zone_changes.json", APPLICATION_JSON)).build();

   public void testDeleteChangesWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, deleteChanges, deleteChangesResponse);
      assertEquals(success.getZoneApi().deleteChanges("jclouds.org").toString(),
                   new DeleteZoneChangesResponseTest().expected().toString());
   }

   HttpRequest delete = HttpRequest.builder().method(DELETE)
                                   .endpoint("https://api2.dynect.net/REST/Zone/jclouds.org")
                                   .addHeader("API-Version", "3.3.8")
                                   .addHeader(ACCEPT, APPLICATION_JSON)
                                   .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                                   .addHeader("Auth-Token", authToken).build();

   HttpResponse deleteResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/delete_zone.json", APPLICATION_JSON)).build();

   public void testDeleteWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, delete, deleteResponse);
      assertEquals(success.getZoneApi().delete("jclouds.org").toString(),
                   new DeleteZoneResponseTest().expected().toString());
   }

   public void testDeleteWhenResponseIs404() {
      DynECTApi fail = requestsSendResponses(createSession, createSessionResponse, delete, notFound);
      assertNull(fail.getZoneApi().delete("jclouds.org"));
   }

   HttpRequest publish = HttpRequest.builder().method(PUT)
         .endpoint("https://api2.dynect.net/REST/Zone/jclouds.org")
         .addHeader("API-Version", "3.3.8")
         .addHeader("Auth-Token", authToken)
         .payload(stringPayload("{\"publish\":true}"))
         .build();   

   public void testPublishWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, publish, getResponse);
      assertEquals(success.getZoneApi().publish("jclouds.org").toString(),
                   new GetZoneResponseTest().expected().toString());
   }

   HttpRequest freeze = HttpRequest.builder().method(PUT)
         .endpoint("https://api2.dynect.net/REST/Zone/jclouds.org")
         .addHeader("API-Version", "3.3.8")
         .addHeader(ACCEPT, APPLICATION_JSON)
         .addHeader("Auth-Token", authToken)
         .payload(stringPayload("{\"freeze\":true}"))
         .build();   

   public void testFreezeWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, freeze, deleteResponse);
      assertEquals(success.getZoneApi().freeze("jclouds.org").toString(),
                   new DeleteZoneResponseTest().expected().toString());
   }

   HttpRequest thaw = HttpRequest.builder().method(PUT)
         .endpoint("https://api2.dynect.net/REST/Zone/jclouds.org")
         .addHeader("API-Version", "3.3.8")
         .addHeader(ACCEPT, APPLICATION_JSON)
         .addHeader("Auth-Token", authToken)
         .payload(stringPayload("{\"thaw\":true}"))
         .build();   

   public void testThawWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, thaw, deleteResponse);
      assertEquals(success.getZoneApi().thaw("jclouds.org").toString(),
                   new DeleteZoneResponseTest().expected().toString());
   }
}
