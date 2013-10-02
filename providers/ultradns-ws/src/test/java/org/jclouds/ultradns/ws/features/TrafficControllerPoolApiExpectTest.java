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
package org.jclouds.ultradns.ws.features;
import static com.google.common.net.HttpHeaders.HOST;
import static javax.ws.rs.HttpMethod.POST;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.OK;
import static org.jclouds.ultradns.ws.domain.TrafficControllerPool.RecordType.IPV4;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.ultradns.ws.UltraDNSWSApi;
import org.jclouds.ultradns.ws.UltraDNSWSExceptions.ResourceAlreadyExistsException;
import org.jclouds.ultradns.ws.domain.UpdatePoolRecord;
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
   HttpRequest create = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/create_tcpool.xml", "application/xml")).build();

   HttpResponse createResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/tcpool_created.xml", "application/xml")).build();

   public void testCreateWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(create, createResponse);
      assertEquals(
            success.getTrafficControllerPoolApiForZone("jclouds.org.").createForDNameAndType("foo",
                  "www.jclouds.org.", IPV4.getCode()), "060339AA0417567A");
   }

   HttpResponse alreadyCreated = HttpResponse.builder().statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
         .payload(payloadFromResourceWithContentType("/lbpool_already_exists.xml", "application/xml")).build();

   @Test(expectedExceptions = ResourceAlreadyExistsException.class, expectedExceptionsMessageRegExp = "Pool already created for this host name : www.rrpool.adrianc.rrpool.ultradnstest.jclouds.org.")
   public void testCreateWhenResponseError2912() {
      UltraDNSWSApi already = requestSendsResponse(create, alreadyCreated);
      already.getTrafficControllerPoolApiForZone("jclouds.org.").createForDNameAndType("foo", "www.jclouds.org.",
            IPV4.getCode());
   }

   HttpRequest list = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/list_tcpools.xml", "application/xml")).build();

   HttpResponse listResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/tcpools.xml", "application/xml")).build();
   
   public void testListWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(list, listResponse);

      assertEquals(success.getTrafficControllerPoolApiForZone("jclouds.org.").list().toString(),
            new GetTCLoadBalancingPoolsByZoneResponseTest().expected().toString());
   }

   HttpRequest listRecords = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/list_tcrecords.xml", "application/xml")).build();

   HttpResponse listRecordsResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/tcrecords.xml", "application/xml")).build();

   public void testListRecordsWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(listRecords, listRecordsResponse);

      assertEquals(
            success.getTrafficControllerPoolApiForZone("jclouds.org.").listRecords("04053D8E57C7931F").toString(),
            new GetTCPoolRecordsResponseTest().expected().toString());
   }

   HttpRequest getNameByDName = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/get_tcpool_by_dname.xml", "application/xml")).build();

   HttpResponse getNameByDNameResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/tcpool_name.xml", "application/xml")).build();

   public void testGetNameByDNameWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(getNameByDName, getNameByDNameResponse);
      assertEquals(success.getTrafficControllerPoolApiForZone("jclouds.org.").getNameByDName("www.foo.com."), "foo");
   }

   HttpResponse poolDoesntExist = HttpResponse.builder().message("Server Epoolor").statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
         .payload(payloadFromResource("/lbpool_doesnt_exist.xml")).build();
   
   public void testGetNameByDNameWhenResponseNotFound() {
      UltraDNSWSApi notFound = requestSendsResponse(getNameByDName, poolDoesntExist);
      assertNull(notFound.getTrafficControllerPoolApiForZone("jclouds.org.").getNameByDName("www.foo.com."));
   }

   HttpRequest delete = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/delete_lbpool.xml", "application/xml")).build();

   HttpResponse deleteResponse = HttpResponse.builder().statusCode(404)
         .payload(payloadFromResourceWithContentType("/lbpool_deleted.xml", "application/xml")).build();

   public void testDeleteWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(delete, deleteResponse);
      success.getTrafficControllerPoolApiForZone("jclouds.org.").delete("04053D8E57C7931F");
   }

   public void testDeleteWhenResponseNotFound() {
      UltraDNSWSApi notFound = requestSendsResponse(delete, poolDoesntExist);
      notFound.getTrafficControllerPoolApiForZone("jclouds.org.").delete("04053D8E57C7931F");
   }
   
   HttpRequest createRecord = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/create_tcrecord.xml", "application/xml")).build();

   HttpResponse createRecordResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/tcrecord_created.xml", "application/xml")).build();

   public void testCreateRecordWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(createRecord, createRecordResponse);
      assertEquals(success.getTrafficControllerPoolApiForZone("jclouds.org.").addRecordToPoolWithTTL("1.2.3.4", "04053D8E57C7931F", 300), "06063DAC54F8D3D9");
   }

   HttpResponse recordAlreadyCreated = HttpResponse.builder().statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
         .payload(payloadFromResourceWithContentType("/tcrecord_already_exists.xml", "application/xml")).build();

   @Test(expectedExceptions = ResourceAlreadyExistsException.class, expectedExceptionsMessageRegExp = "Resource Record of type 1 with these attributes already exists in the system.")
   public void testCreateWhenResponseError1802() {
      UltraDNSWSApi already = requestSendsResponse(createRecord, recordAlreadyCreated);
      already.getTrafficControllerPoolApiForZone("jclouds.org.").addRecordToPoolWithTTL("1.2.3.4", "04053D8E57C7931F", 300);
   }

   HttpRequest createRecordWithWeight = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/create_tcrecord_weight.xml", "application/xml")).build();

   public void testCreateRecordWithWeightWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(createRecordWithWeight, createRecordResponse);
      assertEquals(
            success.getTrafficControllerPoolApiForZone("jclouds.org.").addRecordToPoolWithTTLAndWeight("1.2.3.4",
                  "04053D8E57C7931F", 300, 0), "06063DAC54F8D3D9");
   }

   HttpRequest getRecordSpec = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/get_poolrecordspec.xml", "application/xml")).build();

   HttpResponse getRecordSpecResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/poolrecordspec.xml", "application/xml")).build();

   public void testGetRecordSpecWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(getRecordSpec, getRecordSpecResponse);
      assertEquals(success.getTrafficControllerPoolApiForZone("jclouds.org.").getRecordSpec("04053D8E57C7931F"),
            new GetPoolRecordSpecResponseTest().expected());
   }

   HttpResponse recordDoesntExist = HttpResponse.builder().message("Server Error").statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
         .payload(payloadFromResource("/tcrecord_doesnt_exist.xml")).build();

   public void testGetRecordSpecWhenResponseNotFound() {
      UltraDNSWSApi notFound = requestSendsResponse(getRecordSpec, recordDoesntExist);
      assertNull(notFound.getTrafficControllerPoolApiForZone("jclouds.org.").getRecordSpec("04053D8E57C7931F"));
   }

   UpdatePoolRecord update = UpdatePoolRecord.builder()
                                             .rdata("www.baz.com.")
                                             .mode("Normal")
                                             .weight(98)
                                             .failOverDelay(0)
                                             .threshold(1)
                                             .ttl(OK.getStatusCode()).build();

   HttpRequest updateRecord = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/update_poolrecord.xml", "application/xml")).build();

   HttpResponse updateRecordResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/poolrecord_updated.xml", "application/xml")).build();

   public void testUpdateRecordWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(updateRecord, updateRecordResponse);
      success.getTrafficControllerPoolApiForZone("jclouds.org.").updateRecord("04053D8E57C7931F", update);
   }

   @Test(expectedExceptions = ResourceNotFoundException.class, expectedExceptionsMessageRegExp = "Pool Record does not exist.")
   public void testUpdateRecordWhenResponseNotFound() {
      UltraDNSWSApi notFound = requestSendsResponse(updateRecord, recordDoesntExist);
      notFound.getTrafficControllerPoolApiForZone("jclouds.org.").updateRecord("04053D8E57C7931F", update);
   }

   HttpRequest deleteRecord = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
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
