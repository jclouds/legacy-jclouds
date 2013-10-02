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
import static org.jclouds.ultradns.ws.domain.RoundRobinPool.RecordType.A;
import static org.jclouds.ultradns.ws.domain.RoundRobinPool.RecordType.AAAA;
import static org.testng.Assert.assertEquals;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.ultradns.ws.UltraDNSWSApi;
import org.jclouds.ultradns.ws.UltraDNSWSExceptions.ResourceAlreadyExistsException;
import org.jclouds.ultradns.ws.internal.BaseUltraDNSWSApiExpectTest;
import org.jclouds.ultradns.ws.parse.GetRRLoadBalancingPoolsByZoneResponseTest;
import org.jclouds.ultradns.ws.parse.GetResourceRecordsOfResourceRecordResponseTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "RoundRobinPoolApiExpectTest")
public class RoundRobinPoolApiExpectTest extends BaseUltraDNSWSApiExpectTest {
   HttpRequest createA = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/create_rrpool_a.xml", "application/xml")).build();

   HttpRequest createAAAA = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/create_rrpool_aaaa.xml", "application/xml")).build();

   HttpResponse createResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/rrpool_created.xml", "application/xml")).build();

   public void testCreateAWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(createA, createResponse);
      assertEquals(
            success.getRoundRobinPoolApiForZone("jclouds.org.").createForDNameAndType("www.jclouds.org.", "foo",
                  A.getCode()), "060339AA04175655");
   }

   public void testCreateAAAAWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(createAAAA, createResponse);
      assertEquals(
            success.getRoundRobinPoolApiForZone("jclouds.org.").createForDNameAndType("www.jclouds.org.", "foo",
                  AAAA.getCode()), "060339AA04175655");
   }

   HttpResponse alreadyCreated = HttpResponse.builder().statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
         .payload(payloadFromResourceWithContentType("/lbpool_already_exists.xml", "application/xml")).build();

   @Test(expectedExceptions = ResourceAlreadyExistsException.class, expectedExceptionsMessageRegExp = "Pool already created for this host name : www.rrpool.adrianc.rrpool.ultradnstest.jclouds.org.")
   public void testCreateWhenResponseError2912() {
      UltraDNSWSApi already = requestSendsResponse(createA, alreadyCreated);
      already.getRoundRobinPoolApiForZone("jclouds.org.").createForDNameAndType("www.jclouds.org.", "foo",
            A.getCode());
   }

   HttpRequest list = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/list_rrpools.xml", "application/xml")).build();

   HttpResponse listResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/rrpools.xml", "application/xml")).build();
   
   public void testListWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(list, listResponse);

      assertEquals(
            success.getRoundRobinPoolApiForZone("jclouds.org.").list().toString(),
            new GetRRLoadBalancingPoolsByZoneResponseTest().expected().toString());
   }

   HttpRequest listRecords = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/list_rrrecords.xml", "application/xml")).build();

   HttpResponse listRecordsResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/records.xml", "application/xml")).build();

   public void testListRecordsWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(listRecords, listRecordsResponse);

      assertEquals(
            success.getRoundRobinPoolApiForZone("jclouds.org.").listRecords("04053D8E57C7931F").toString(),
            new GetResourceRecordsOfResourceRecordResponseTest().expected().toString());
   }

   HttpRequest delete = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/delete_lbpool.xml", "application/xml")).build();

   HttpResponse deleteResponse = HttpResponse.builder().statusCode(404)
         .payload(payloadFromResourceWithContentType("/lbpool_deleted.xml", "application/xml")).build();

   public void testDeleteWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(delete, deleteResponse);
      success.getRoundRobinPoolApiForZone("jclouds.org.").delete("04053D8E57C7931F");
   }

   HttpResponse poolDoesntExist = HttpResponse.builder().message("Server Epoolor").statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
         .payload(payloadFromResource("/lbpool_doesnt_exist.xml")).build();
   
   public void testDeleteWhenResponseRRNotFound() {
      UltraDNSWSApi notFound = requestSendsResponse(delete, poolDoesntExist);
      notFound.getRoundRobinPoolApiForZone("jclouds.org.").delete("04053D8E57C7931F");
   }
}
