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
import static org.testng.Assert.assertNull;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.ultradns.ws.UltraDNSWSApi;
import org.jclouds.ultradns.ws.UltraDNSWSExceptions.ResourceAlreadyExistsException;
import org.jclouds.ultradns.ws.internal.BaseUltraDNSWSApiExpectTest;
import org.jclouds.ultradns.ws.parse.GetPoolRecordSpecResponseTest;
import org.jclouds.ultradns.ws.parse.GetTCLoadBalancingPoolsByZoneResponseTest;
import org.jclouds.ultradns.ws.parse.GetTCPoolRecordsResponseTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "TrafficControllerPoolApiExpectTest")
public class TrafficControllerPoolApiExpectTest extends BaseUltraDNSWSApiExpectTest {
   HttpRequest create = HttpRequest.builder().method("POST")
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader("Host", "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/create_tcpool.xml", "application/xml")).build();

   HttpResponse createResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType("/tcpool_created.xml", "application/xml")).build();

   public void testCreateWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(create, createResponse);
      assertEquals(success.getTrafficControllerPoolApiForZone("jclouds.org.").createPoolForHostname("www.jclouds.org.", "foo"), "060339AA0417567A");
   }

   HttpResponse alreadyCreated = HttpResponse.builder().statusCode(500)
         .payload(payloadFromResourceWithContentType("/lbpool_already_exists.xml", "application/xml")).build();

   @Test(expectedExceptions = ResourceAlreadyExistsException.class, expectedExceptionsMessageRegExp = "Pool already created for this host name : www.rrpool.adrianc.rrpool.ultradnstest.jclouds.org.")
   public void testCreateWhenResponseError2912() {
      UltraDNSWSApi already = requestSendsResponse(create, alreadyCreated);
      already.getTrafficControllerPoolApiForZone("jclouds.org.").createPoolForHostname("www.jclouds.org.", "foo");
   }

   HttpRequest list = HttpRequest.builder().method("POST")
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader("Host", "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/list_tcpools.xml", "application/xml")).build();

   HttpResponse listResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType("/tcpools.xml", "application/xml")).build();
   
   public void testListWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(list, listResponse);

      assertEquals(success.getTrafficControllerPoolApiForZone("jclouds.org.").list().toString(),
            new GetTCLoadBalancingPoolsByZoneResponseTest().expected().toString());
   }

   HttpRequest listRecords = HttpRequest.builder().method("POST")
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader("Host", "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/list_tcrecords.xml", "application/xml")).build();

   HttpResponse listRecordsResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType("/tcrecords.xml", "application/xml")).build();

   public void testListRecordsWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(listRecords, listRecordsResponse);

      assertEquals(
            success.getTrafficControllerPoolApiForZone("jclouds.org.").listRecords("04053D8E57C7931F").toString(),
            new GetTCPoolRecordsResponseTest().expected().toString());
   }

   HttpRequest delete = HttpRequest.builder().method("POST")
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader("Host", "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/delete_lbpool.xml", "application/xml")).build();

   HttpResponse deleteResponse = HttpResponse.builder().statusCode(404)
         .payload(payloadFromResourceWithContentType("/lbpool_deleted.xml", "application/xml")).build();

   public void testDeleteWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(delete, deleteResponse);
      success.getTrafficControllerPoolApiForZone("jclouds.org.").delete("04053D8E57C7931F");
   }

   HttpResponse poolDoesntExist = HttpResponse.builder().message("Server Epoolor").statusCode(500)
         .payload(payloadFromResource("/lbpool_doesnt_exist.xml")).build();
   
   public void testDeleteWhenResponseNotFound() {
      UltraDNSWSApi notFound = requestSendsResponse(delete, poolDoesntExist);
      notFound.getTrafficControllerPoolApiForZone("jclouds.org.").delete("04053D8E57C7931F");
   }
   
   HttpRequest createRecord = HttpRequest.builder().method("POST")
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader("Host", "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/create_tcrecord.xml", "application/xml")).build();

   HttpResponse createRecordResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType("/tcrecord_created.xml", "application/xml")).build();

   public void testCreateRecordWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(createRecord, createRecordResponse);
      assertEquals(success.getTrafficControllerPoolApiForZone("jclouds.org.").addRecordToPoolWithTTL("1.2.3.4", "04053D8E57C7931F", 300), "06063DAC54F8D3D9");
   }

   HttpResponse recordAlreadyCreated = HttpResponse.builder().statusCode(500)
         .payload(payloadFromResourceWithContentType("/tcrecord_already_exists.xml", "application/xml")).build();

   @Test(expectedExceptions = ResourceAlreadyExistsException.class, expectedExceptionsMessageRegExp = "Resource Record of type 1 with these attributes already exists in the system.")
   public void testCreateWhenResponseError1802() {
      UltraDNSWSApi already = requestSendsResponse(createRecord, recordAlreadyCreated);
      already.getTrafficControllerPoolApiForZone("jclouds.org.").addRecordToPoolWithTTL("1.2.3.4", "04053D8E57C7931F", 300);
   }

   HttpRequest getRecordSpec = HttpRequest.builder().method("POST")
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader("Host", "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/get_poolrecordspec.xml", "application/xml")).build();

   HttpResponse getRecordSpecResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType("/poolrecordspec.xml", "application/xml")).build();

   public void testGetRecordSpecWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(getRecordSpec, getRecordSpecResponse);
      assertEquals(success.getTrafficControllerPoolApiForZone("jclouds.org.").getRecordSpec("04053D8E57C7931F"),
            new GetPoolRecordSpecResponseTest().expected());
   }

   HttpResponse recordDoesntExist = HttpResponse.builder().message("Server Error").statusCode(500)
         .payload(payloadFromResource("/tcrecord_doesnt_exist.xml")).build();

   public void testGetRecordSpecWhenResponseNotFound() {
      UltraDNSWSApi notFound = requestSendsResponse(getRecordSpec, recordDoesntExist);
      assertNull(notFound.getTrafficControllerPoolApiForZone("jclouds.org.").getRecordSpec("04053D8E57C7931F"));
   }

   HttpRequest deleteRecord = HttpRequest.builder().method("POST")
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader("Host", "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/delete_tcrecord.xml", "application/xml")).build();

   HttpResponse deleteRecordResponse = HttpResponse.builder().statusCode(404)
         .payload(payloadFromResourceWithContentType("/tcrecord_deleted.xml", "application/xml")).build();

   public void testDeleteRecordWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(deleteRecord, deleteRecordResponse);
      success.getTrafficControllerPoolApiForZone("jclouds.org.").deleteRecord("04053D8E57C7931F");
   }

   public void testDeleteRecordWhenResponseNotFound() {
      UltraDNSWSApi notFound = requestSendsResponse(deleteRecord, recordDoesntExist);
      notFound.getTrafficControllerPoolApiForZone("jclouds.org.").deleteRecord("04053D8E57C7931F");
   }
}
