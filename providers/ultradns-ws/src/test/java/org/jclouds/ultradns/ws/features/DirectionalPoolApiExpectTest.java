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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.ultradns.ws.features;

import static org.testng.Assert.assertEquals;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.ultradns.ws.UltraDNSWSApi;
import org.jclouds.ultradns.ws.internal.BaseUltraDNSWSApiExpectTest;
import org.jclouds.ultradns.ws.parse.GetDirectionalDNSRecordsForHostResponseTest;
import org.jclouds.ultradns.ws.parse.GetDirectionalPoolsByZoneResponseTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "DirectionalPoolApiExpectTest")
public class DirectionalPoolApiExpectTest extends BaseUltraDNSWSApiExpectTest {

   HttpRequest list = HttpRequest.builder().method("POST")
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader("Host", "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/list_directionalpools.xml", "application/xml")).build();

   HttpResponse listResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType("/directionalpools.xml", "application/xml")).build();
   
   public void testListWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(list, listResponse);

      assertEquals(success.getDirectionalPoolApiForZone("jclouds.org.").list().toString(),
            new GetDirectionalPoolsByZoneResponseTest().expected().toString());
   }

   HttpRequest listRecords = HttpRequest.builder().method("POST")
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader("Host", "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/list_directionalrecords.xml", "application/xml")).build();

   HttpResponse listRecordsResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType("/directionalrecords.xml", "application/xml")).build();

   public void testListRecordsWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(listRecords, listRecordsResponse);

      assertEquals(
            success.getDirectionalPoolApiForZone("jclouds.org.").listRecordsByNameAndType("www.jclouds.org.", 1).toString(),
            new GetDirectionalDNSRecordsForHostResponseTest().expected().toString());
   }
}
