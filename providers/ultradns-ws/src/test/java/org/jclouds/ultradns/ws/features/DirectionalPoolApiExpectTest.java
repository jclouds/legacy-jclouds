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
import static org.jclouds.ultradns.ws.domain.DirectionalPool.RecordType.IPV4;
import static org.testng.Assert.assertEquals;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.ultradns.ws.UltraDNSWSApi;
import org.jclouds.ultradns.ws.UltraDNSWSExceptions.ResourceAlreadyExistsException;
import org.jclouds.ultradns.ws.domain.DirectionalGroup;
import org.jclouds.ultradns.ws.domain.DirectionalPoolRecord;
import org.jclouds.ultradns.ws.internal.BaseUltraDNSWSApiExpectTest;
import org.jclouds.ultradns.ws.parse.GetDirectionalDNSRecordsForHostResponseTest;
import org.jclouds.ultradns.ws.parse.GetDirectionalPoolsByZoneResponseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "DirectionalPoolApiExpectTest")
public class DirectionalPoolApiExpectTest extends BaseUltraDNSWSApiExpectTest {
   HttpRequest create = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/create_directionalpool.xml", "application/xml")).build();

   HttpResponse createResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/directionalpool_created.xml", "application/xml")).build();

   public void testCreateWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(create, createResponse);
      assertEquals(
            success.getDirectionalPoolApiForZone("jclouds.org.").createForDNameAndType("foo",
                  "www.jclouds.org.", IPV4.getCode()), "06063DC355055E68");
   }

   HttpResponse alreadyCreated = HttpResponse.builder().statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
         .payload(payloadFromResourceWithContentType("/directionalpool_already_exists.xml", "application/xml"))
         .build();

   @Test(expectedExceptions = ResourceAlreadyExistsException.class, expectedExceptionsMessageRegExp = "Pool already created for this host name : www.jclouds.org.")
   public void testCreateWhenResponseError2912() {
      UltraDNSWSApi already = requestSendsResponse(create, alreadyCreated);
      already.getDirectionalPoolApiForZone("jclouds.org.").createForDNameAndType("foo", "www.jclouds.org.",
            IPV4.getCode());
   }
   
   HttpRequest addFirstRecordInNonConfiguredGroup = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/create_directionalrecord.xml", "application/xml")).build();

   HttpResponse recordCreatedResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/directionalrecord_created.xml", "application/xml")).build();

   DirectionalPoolRecord record =  DirectionalPoolRecord.drBuilder()
                                                        .type("A")
                                                        .ttl(300)
                                                        .rdata("1.1.0.1").build();

   public void testAddFirstRecordInNonConfiguredGroupWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(addFirstRecordInNonConfiguredGroup, recordCreatedResponse);
      assertEquals(
            success.getDirectionalPoolApiForZone("jclouds.org.").addFirstRecordInNonConfiguredGroup("06063DC355055E68",
                  record), "06063DC355058294");
   }

   HttpResponse recordAlreadyCreated = HttpResponse.builder().statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
         .payload(payloadFromResourceWithContentType("/directionalrecord_already_exists.xml", "application/xml"))
         .build();

   @Test(expectedExceptions = ResourceAlreadyExistsException.class, expectedExceptionsMessageRegExp = "Resource Record already exists.")
   public void testAddFirstRecordInNonConfiguredGroupWhenResponseError1802() {
      UltraDNSWSApi already = requestSendsResponse(addFirstRecordInNonConfiguredGroup, recordAlreadyCreated);
      already.getDirectionalPoolApiForZone("jclouds.org.").addFirstRecordInNonConfiguredGroup("06063DC355055E68",
            record);
   }

   HttpRequest addRecordIntoNewGroup = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/create_directionalrecord_newgroup.xml", "application/xml"))
         .build();

   DirectionalGroup group = DirectionalGroup.builder()
                                            .name("Mexas")
                                            .description("Clients we classify as being in US")
                                            .mapRegionToTerritories("United States (US)",
                                                  ImmutableSet.of("Maryland", "Texas"))
                                            .build();

   public void testAddRecordIntoNewGroupWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(addRecordIntoNewGroup, recordCreatedResponse);
      assertEquals(
            success.getDirectionalPoolApiForZone("jclouds.org.").addRecordIntoNewGroup("06063DC355055E68", record,
                  group), "06063DC355058294");
   }

   HttpRequest addRecordIntoExistingGroup = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/create_directionalrecord_existinggroup.xml", "application/xml"))
         .build();

   public void testAddRecordIntoExistingGroupWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(addRecordIntoExistingGroup, recordCreatedResponse);
      assertEquals(
            success.getDirectionalPoolApiForZone("jclouds.org.").addRecordIntoExistingGroup("06063DC355055E68",
                  record, "AAABBBCCCDDDEEE"), "06063DC355058294");
   }

   HttpRequest updateRecord = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/update_directionalrecord.xml", "application/xml")).build();

   HttpResponse updateRecordResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/directionalrecord_updated.xml", "application/xml")).build();

   public void testUpdateRecordWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(updateRecord, updateRecordResponse);
      success.getDirectionalPoolApiForZone("jclouds.org.").updateRecord("04053D8E57C7931F", record);
   }

   HttpResponse recordDoesntExist = HttpResponse.builder().message("Server Error").statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
         .payload(payloadFromResource("/directionalrecord_doesnt_exist.xml")).build();

   @Test(expectedExceptions = ResourceNotFoundException.class, expectedExceptionsMessageRegExp = "Directional Pool Record does not exist in the system")
   public void testUpdateRecordWhenResponseNotFound() {
      UltraDNSWSApi notFound = requestSendsResponse(updateRecord, recordDoesntExist);
      notFound.getDirectionalPoolApiForZone("jclouds.org.").updateRecord("04053D8E57C7931F", record);
   }

   HttpRequest updateRecordAndGroup = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/update_directionalrecord_group.xml", "application/xml")).build();

   public void testUpdateRecordAndGroupWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(updateRecordAndGroup, updateRecordResponse);
      success.getDirectionalPoolApiForZone("jclouds.org.").updateRecordAndGroup("04053D8E57C7931F", record, group);
   }

   HttpRequest deleteRecord = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/delete_directionalrecord.xml", "application/xml")).build();

   HttpResponse deleteRecordResponse = HttpResponse.builder().statusCode(404)
         .payload(payloadFromResourceWithContentType("/directionalrecord_deleted.xml", "application/xml")).build();

   public void testDeleteRecordWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(deleteRecord, deleteRecordResponse);
      success.getDirectionalPoolApiForZone("jclouds.org.").deleteRecord("04053D8E57C7931F");
   }

   public void testDeleteRecordWhenResponseNotFound() {
      UltraDNSWSApi notFound = requestSendsResponse(deleteRecord, recordDoesntExist);
      notFound.getDirectionalPoolApiForZone("jclouds.org.").deleteRecord("04053D8E57C7931F");
   }

   HttpRequest list = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/list_directionalpools.xml", "application/xml")).build();

   HttpResponse listResponse = HttpResponse.builder().statusCode(OK.getStatusCode())

         .payload(payloadFromResourceWithContentType("/directionalpools.xml", "application/xml")).build();
   
   public void testListWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(list, listResponse);

      assertEquals(success.getDirectionalPoolApiForZone("jclouds.org.").list().toString(),
            new GetDirectionalPoolsByZoneResponseTest().expected().toString());
   }

   HttpRequest listRecords = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/list_directionalrecords.xml", "application/xml")).build();

   HttpResponse listRecordsResponse = HttpResponse.builder().statusCode(OK.getStatusCode())

         .payload(payloadFromResourceWithContentType("/directionalrecords.xml", "application/xml")).build();

   public void testListRecordsWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(listRecords, listRecordsResponse);

      assertEquals(success.getDirectionalPoolApiForZone("jclouds.org.").listRecordsByDNameAndType("www.jclouds.org.", 1)
            .toString(), new GetDirectionalDNSRecordsForHostResponseTest().expected().toString());
   }
}
