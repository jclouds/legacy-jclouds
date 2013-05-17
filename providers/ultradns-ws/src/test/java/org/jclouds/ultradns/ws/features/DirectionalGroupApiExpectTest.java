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
import static javax.ws.rs.core.Response.Status.OK;
import static org.testng.Assert.assertEquals;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.ultradns.ws.UltraDNSWSApi;
import org.jclouds.ultradns.ws.domain.DirectionalGroupCoordinates;
import org.jclouds.ultradns.ws.internal.BaseUltraDNSWSApiExpectTest;
import org.jclouds.ultradns.ws.parse.GetAccountLevelDirectionalGroupsResponseTest;
import org.jclouds.ultradns.ws.parse.GetAvailableGroupsResponseTest;
import org.jclouds.ultradns.ws.parse.GetDirectionalDNSGroupDetailsResponseTest;
import org.jclouds.ultradns.ws.parse.GetDirectionalDNSRecordsForHostResponseTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "DirectionalGroupApiExpectTest")
public class DirectionalGroupApiExpectTest extends BaseUltraDNSWSApiExpectTest {

   HttpRequest listGroupNamesByRecordNameAndType = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/list_directionalgroup_names.xml", "application/xml")).build();

   HttpResponse listGroupNamesByRecordNameAndTypeResponse = HttpResponse.builder().statusCode(OK.getStatusCode())

         .payload(payloadFromResourceWithContentType("/directionalgroup_names.xml", "application/xml")).build();
   
   public void testListGroupNamesByRecordNameAndTypeWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(listGroupNamesByRecordNameAndType, listGroupNamesByRecordNameAndTypeResponse);

      assertEquals(success.getDirectionalGroupApiForAccount("accountid").listGroupNamesByDNameAndType("www.jclouds.org.", 1).toString(),
            new GetAvailableGroupsResponseTest().expected().toString());
   }

   HttpRequest listRecordsByGroupCoordinates = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/list_directionalgroup_records.xml", "application/xml")).build();

   HttpResponse listRecordsByGroupCoordinatesResponse = HttpResponse.builder().statusCode(OK.getStatusCode())

         .payload(payloadFromResourceWithContentType("/directionalrecords.xml", "application/xml")).build();

   public void testListRecordsByGroupCoordinatesWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(listRecordsByGroupCoordinates, listRecordsByGroupCoordinatesResponse);

      DirectionalGroupCoordinates group = DirectionalGroupCoordinates.builder()
                                                                     .zoneName("jclouds.org.")
                                                                     .recordName("www.jclouds.org.")
                                                                     .recordType(1)
                                                                     .groupName("EU-www.jclouds.org.").build();
      assertEquals(
            success.getDirectionalGroupApiForAccount("accountid").listRecordsByGroupCoordinates(group).toString(),
            new GetDirectionalDNSRecordsForHostResponseTest().expected().toString());
   }

   HttpRequest get = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/get_directionalgroup.xml", "application/xml")).build();

   HttpResponse getResponse = HttpResponse.builder().statusCode(OK.getStatusCode())

         .payload(payloadFromResourceWithContentType("/directionalgroup.xml", "application/xml")).build();
   
   public void testGetWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(get, getResponse);

      assertEquals(success.getDirectionalGroupApiForAccount("accountid").get("0000000000A").toString(),
            new GetDirectionalDNSGroupDetailsResponseTest().expected().toString());
   }

   HttpRequest listAccountLevelGroups = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/list_accountlevelgroups.xml", "application/xml")).build();

   HttpResponse listAccountLevelGroupsResponse = HttpResponse.builder().statusCode(OK.getStatusCode())

         .payload(payloadFromResourceWithContentType("/accountlevelgroups.xml", "application/xml")).build();
   
   public void testListAccountLevelGroupsWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(listAccountLevelGroups, listAccountLevelGroupsResponse);

      assertEquals(success.getDirectionalGroupApiForAccount("accountid").listAccountLevelGroups().toString(),
            new GetAccountLevelDirectionalGroupsResponseTest().expected().toString());
   }

   HttpRequest listRecordsByAccountLevelGroup = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/list_accountlevelgroup_records.xml", "application/xml")).build();

   HttpResponse listRecordsByAccountLevelGroupResponse = HttpResponse.builder().statusCode(OK.getStatusCode())

         .payload(payloadFromResourceWithContentType("/directionalrecords.xml", "application/xml")).build();

   public void testListRecordsByAccountLevelGroupWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(listRecordsByAccountLevelGroup, listRecordsByAccountLevelGroupResponse);

      assertEquals(
            success.getDirectionalGroupApiForAccount("accountid").listRecordsByAccountLevelGroup("000000000000000A").toString(),
            new GetDirectionalDNSRecordsForHostResponseTest().expected().toString());
   }
}
