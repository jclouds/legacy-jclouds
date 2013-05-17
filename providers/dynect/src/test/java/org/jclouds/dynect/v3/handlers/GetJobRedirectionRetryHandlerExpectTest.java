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
package org.jclouds.dynect.v3.handlers;
import static com.google.common.net.HttpHeaders.ACCEPT;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static com.google.common.net.HttpHeaders.LOCATION;
import static javax.ws.rs.HttpMethod.GET;
import static javax.ws.rs.HttpMethod.PUT;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.TEMPORARY_REDIRECT;

import org.jclouds.dynect.v3.DynECTApi;
import org.jclouds.dynect.v3.internal.BaseDynECTApiExpectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "GetJobRedirectionRetryHandlerExpectTest")
public class GetJobRedirectionRetryHandlerExpectTest extends BaseDynECTApiExpectTest {

   public void testRedirectOnJobLocationSwitchesToGETAndNoPayload() {

      HttpRequest thaw = HttpRequest.builder().method(PUT)
                                    .endpoint("https://api2.dynect.net/REST/Zone/jclouds.org")
                                    .addHeader("API-Version", "3.3.8")
                                    .addHeader(ACCEPT, APPLICATION_JSON)
                                    .addHeader("Auth-Token", authToken)
                                    .payload(stringPayload("{\"thaw\":true}"))
                                    .build();

      HttpResponse redirectResponse = HttpResponse.builder() 
                                                  .statusCode(TEMPORARY_REDIRECT.getStatusCode())
                                                  .addHeader(LOCATION, "https://api2.dynect.net/REST/Job/1234")
                                                  .build();

      HttpRequest job = HttpRequest.builder().method(GET)
                                   .endpoint("https://api2.dynect.net/REST/Job/1234")
                                   .addHeader("API-Version", "3.3.8")
                                   .addHeader(ACCEPT, APPLICATION_JSON)
                                   .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                                   .addHeader("Auth-Token", authToken).build();

      HttpResponse success = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/delete_zone.json", APPLICATION_JSON)).build();

      DynECTApi apiThatRedirects = requestsSendResponses(createSession, createSessionResponse, thaw, redirectResponse,
            job, success);
      
      apiThatRedirects.getZoneApi().thaw("jclouds.org");

   }
}
