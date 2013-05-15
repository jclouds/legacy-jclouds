/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
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

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.ultradns.ws.UltraDNSWSApi;
import org.jclouds.ultradns.ws.internal.BaseUltraDNSWSApiExpectTest;
import org.testng.annotations.Test;
/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ScopedTransactionExpectTest")
public class ScopedTransactionExpectTest extends BaseUltraDNSWSApiExpectTest {
   HttpRequest start = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/start_tx.xml", "application/xml")).build();

   HttpResponse startResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/tx_started.xml", "application/xml")).build();

   HttpRequest createWithTx = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/create_zone_tx.xml", "application/xml")).build();

   HttpResponse createResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/zone_created.xml", "application/xml")).build();

   public void testAddTransactionIdWhenTransactionStarted() {
      UltraDNSWSApi success = requestsSendResponses(start, startResponse, createWithTx, createResponse);

      success.getTransactionApi().start();
      success.getZoneApi().createInAccount("jclouds.org.", "AAAAAAAAAAAAAAAA");
   }
}
