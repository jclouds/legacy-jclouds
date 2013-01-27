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
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.ultradns.ws.UltraDNSWSApi;
import org.jclouds.ultradns.ws.domain.Zone.Type;
import org.jclouds.ultradns.ws.internal.BaseUltraDNSWSApiExpectTest;
import org.jclouds.ultradns.ws.parse.GetGeneralPropertiesForZoneResponseTest;
import org.jclouds.ultradns.ws.parse.GetZonesOfAccountResponseTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ZoneApiExpectTest")
public class ZoneApiExpectTest extends BaseUltraDNSWSApiExpectTest {
   HttpRequest get = HttpRequest.builder().method("POST")
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader("Host", "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/get_zone.xml", "application/xml")).build();

   HttpResponse getResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType("/zoneproperties.xml", "application/xml")).build();

   public void testGetWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(get, getResponse);

      assertEquals(
            success.getZoneApi().get("jclouds.org.").toString(),
            new GetGeneralPropertiesForZoneResponseTest().expected().toString());
   }
   
   HttpResponse zoneDoesntExist = HttpResponse.builder().message("Server Error").statusCode(500)
         .payload(payloadFromResource("/zone_doesnt_exist.xml")).build();
   
   public void testGetWhenResponseError2401() {
      UltraDNSWSApi notFound = requestSendsResponse(get, zoneDoesntExist);
      assertNull(notFound.getZoneApi().get("jclouds.org."));
   }
   
   HttpRequest listByAccount = HttpRequest.builder().method("POST")
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader("Host", "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/list_zones_by_account.xml", "application/xml")).build();

   HttpResponse listByAccountResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType("/zones.xml", "application/xml")).build();

   public void testListByAccountWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(listByAccount, listByAccountResponse);

      assertEquals(
            success.getZoneApi().listByAccount("AAAAAAAAAAAAAAAA").toString(),
            new GetZonesOfAccountResponseTest().expected().toString());
   }
   
   HttpResponse accountDoesntExist = HttpResponse.builder().message("Server Error").statusCode(500)
         .payload(payloadFromResource("/account_doesnt_exist.xml")).build();
   
   @Test(expectedExceptions = ResourceNotFoundException.class, expectedExceptionsMessageRegExp = "Account not found in the system. ID: AAAAAAAAAAAAAAAA")
   public void testListByAccountWhenResponseError2401() {
      UltraDNSWSApi notFound = requestSendsResponse(listByAccount, accountDoesntExist);
      notFound.getZoneApi().listByAccount("AAAAAAAAAAAAAAAA");
   }
     
   HttpRequest listByAccountAndType = HttpRequest.builder().method("POST")
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader("Host", "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/list_zones_by_account_and_type.xml", "application/xml")).build();

   public void testListByAccountAndTypeWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(listByAccountAndType, listByAccountResponse);

      assertEquals(
            success.getZoneApi().listByAccountAndType("AAAAAAAAAAAAAAAA", Type.PRIMARY).toString(),
            new GetZonesOfAccountResponseTest().expected().toString());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class, expectedExceptionsMessageRegExp = "Account not found in the system. ID: AAAAAAAAAAAAAAAA")
   public void testListByAccountAndTypeWhenResponseError2401() {
      UltraDNSWSApi notFound = requestSendsResponse(listByAccountAndType, accountDoesntExist);
      notFound.getZoneApi().listByAccountAndType("AAAAAAAAAAAAAAAA", Type.PRIMARY);
   }
}
