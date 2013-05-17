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
import static org.jclouds.ultradns.ws.domain.ResourceRecord.rrBuilder;
import static org.testng.Assert.assertEquals;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.ultradns.ws.UltraDNSWSApi;
import org.jclouds.ultradns.ws.UltraDNSWSExceptions.ResourceAlreadyExistsException;
import org.jclouds.ultradns.ws.domain.ResourceRecord;
import org.jclouds.ultradns.ws.internal.BaseUltraDNSWSApiExpectTest;
import org.jclouds.ultradns.ws.parse.GetResourceRecordsOfResourceRecordResponseTest;
import org.testng.annotations.Test;



/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ResourceRecordApiExpectTest")
public class ResourceRecordApiExpectTest extends BaseUltraDNSWSApiExpectTest {
   HttpRequest create = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/create_rr.xml", "application/xml")).build();

   HttpResponse createResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/rr_created.xml", "application/xml")).build();

   ResourceRecord record = rrBuilder().name("mail.jclouds.org.")
                                      .type(15)
                                      .ttl(1800)
                                      .infoValue(10)
                                      .infoValue("maileast.jclouds.org.").build();

   public void testCreateWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(create, createResponse);
      success.getResourceRecordApiForZone("jclouds.org.").create(record);
   }

   HttpResponse alreadyCreated = HttpResponse.builder().statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
         .payload(payloadFromResourceWithContentType("/rr_already_exists.xml", "application/xml")).build();

   @Test(expectedExceptions = ResourceAlreadyExistsException.class, expectedExceptionsMessageRegExp = "Resource Record of type 15 with these attributes already exists in the system.")
   public void testCreateWhenResponseError1802() {
      UltraDNSWSApi already = requestSendsResponse(create, alreadyCreated);
      already.getResourceRecordApiForZone("jclouds.org.").create(record);
   }

   HttpRequest update = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/update_rr.xml", "application/xml")).build();

   HttpResponse updateResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/rr_updated.xml", "application/xml")).build();

   public void testUpdateWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(update, updateResponse);
      success.getResourceRecordApiForZone("jclouds.org.").update("04053D8E57C7931F", record);
   }

   HttpRequest list = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/list_records.xml", "application/xml")).build();

   HttpResponse listResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/records.xml", "application/xml")).build();
   
   public void testListWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(list, listResponse);

      assertEquals(
            success.getResourceRecordApiForZone("jclouds.org.").list().toString(),
            new GetResourceRecordsOfResourceRecordResponseTest().expected().toString());
   }

   HttpResponse zoneDoesntExist = HttpResponse.builder().message("Server Error").statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
         .payload(payloadFromResource("/zone_doesnt_exist.xml")).build();
   
   @Test(expectedExceptions = ResourceNotFoundException.class, expectedExceptionsMessageRegExp = "Zone does not exist in the system.")
   public void testListWhenResponseError1801() {
      UltraDNSWSApi notFound = requestSendsResponse(list, zoneDoesntExist);
      notFound.getResourceRecordApiForZone("jclouds.org.").list();
   }

   HttpRequest listByName = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/list_records_by_name.xml", "application/xml")).build();
   
   public void testListByNameWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(listByName, listResponse);

      assertEquals(
            success.getResourceRecordApiForZone("jclouds.org.").listByName("www.jclouds.org.").toString(),
            new GetResourceRecordsOfResourceRecordResponseTest().expected().toString());
   }

   HttpRequest listByNameAndType = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/list_records_by_name_and_type.xml", "application/xml")).build();
   
   public void testListByNameAndTypeWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(listByNameAndType, listResponse);

      assertEquals(success.getResourceRecordApiForZone("jclouds.org.").listByNameAndType("www.jclouds.org.", 1)
            .toString(), new GetResourceRecordsOfResourceRecordResponseTest().expected().toString());
      
      assertEquals(
            success.getResourceRecordApiForZone("jclouds.org.")
                  .listByNameAndType("www.jclouds.org.", 1).toString(),
            new GetResourceRecordsOfResourceRecordResponseTest().expected().toString());
   }

   HttpRequest delete = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/delete_rr.xml", "application/xml")).build();

   HttpResponse deleteResponse = HttpResponse.builder().statusCode(404)
         .payload(payloadFromResourceWithContentType("/rr_deleted.xml", "application/xml")).build();

   public void testDeleteWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(delete, deleteResponse);
      success.getZoneApi().delete("04053D8E57C7931F");
   }

   HttpResponse rrDoesntExist = HttpResponse.builder().message("Server Error").statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
         .payload(payloadFromResource("/rr_doesnt_exist.xml")).build();
   
   public void testDeleteWhenResponseRRNotFound() {
      UltraDNSWSApi notFound = requestSendsResponse(delete, rrDoesntExist);
      notFound.getZoneApi().delete("04053D8E57C7931F");
   }
}
