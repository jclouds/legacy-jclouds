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
package org.jclouds.ultradns.ws;
import static com.google.common.net.HttpHeaders.HOST;
import static javax.ws.rs.HttpMethod.POST;
import static javax.ws.rs.core.Response.Status.OK;
import static org.testng.Assert.assertEquals;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.ultradns.ws.internal.BaseUltraDNSWSApiExpectTest;
import org.jclouds.ultradns.ws.parse.GetAccountsListOfUserResponseTest;
import org.jclouds.ultradns.ws.parse.GetAvailableRegionsResponseTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "UltraDNSWSApiExpectTest")
public class UltraDNSWSApiExpectTest extends BaseUltraDNSWSApiExpectTest {

   HttpRequest getCurrentAccount = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/get_current_account.xml", "application/xml")).build();

   HttpResponse getCurrentAccountResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/account.xml", "application/xml")).build();

   public void testGetCurrentAccountWhenResponseIs2xx() {

      UltraDNSWSApi success = requestSendsResponse(getCurrentAccount, getCurrentAccountResponse);

      assertEquals(
            success.getCurrentAccount().toString(),
            new GetAccountsListOfUserResponseTest().expected().toString());
   }

   HttpRequest getRegionsById = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/list_regions.xml", "application/xml")).build();

   HttpResponse getRegionsByIdResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType("/regions.xml", "application/xml")).build();

   public void testGetRegionsByIdWhenResponseIs2xx() {

      UltraDNSWSApi success = requestSendsResponse(getRegionsById, getRegionsByIdResponse);

      assertEquals(
            success.getRegionsByIdAndName().toString(),
            new GetAvailableRegionsResponseTest().expected().toString());
   }
}
