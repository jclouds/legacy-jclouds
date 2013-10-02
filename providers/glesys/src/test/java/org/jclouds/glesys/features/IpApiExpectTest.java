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
package org.jclouds.glesys.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import javax.ws.rs.core.MediaType;

import org.jclouds.glesys.domain.Cost;
import org.jclouds.glesys.domain.IpDetails;
import org.jclouds.glesys.internal.BaseGleSYSApiExpectTest;
import org.jclouds.glesys.parse.ParseIpAddressFromResponseTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Allows us to test a api via its side effects.
 *
 * @author Adrian Cole, Adam Lowe
 */
@Test(groups = "unit", testName = "IpApiExpectTest")
public class IpApiExpectTest extends BaseGleSYSApiExpectTest {

   public void testListIpsWhenResponseIs2xx() {
      IpApi api = requestSendsResponse(
            HttpRequest.builder().method("GET").endpoint("https://api.glesys.com/ip/listown/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/ip_list_own.json")).build())
            .getIpApi();

      IpDetails.Builder<?> builder = IpDetails.builder().datacenter("Falkenberg").version4().reserved(true)
            .platform("OpenVZ")
            .nameServers("79.99.4.100", "79.99.4.101")
            .cost(Cost.builder().amount(2.0).currency("EUR").timePeriod("month").build());

      assertEquals(api.list().toString(), ImmutableSet.of(
            builder.ptr("31-192-230-68-static.serverhotell.net.").address("31.192.230.68").serverId(null).build(),
            builder.ptr("31-192-231-148-static.serverhotell.net.").address("31.192.231.148").serverId("vz1609110").build()).toString());
   }

   public void testListIpsWhenResponseIs4xxReturnsEmpty() {
      IpApi api = requestSendsResponse(
            HttpRequest.builder().method("GET").endpoint("https://api.glesys.com/ip/listown/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build(),
            HttpResponse.builder().statusCode(404).build()).getIpApi();

      assertTrue(api.list().isEmpty());
   }

   public void testGetIpDetailsWhenResponseIs2xx() {
      IpApi api = requestSendsResponse(
            HttpRequest.builder().method("GET").endpoint("https://api.glesys.com/ip/details/ipaddress/31.192.227.113/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/ip_get_details.json")).build())
            .getIpApi();

      assertEquals(api.get("31.192.227.113"), getIpInIpDetails());
   }

   protected IpDetails getIpInIpDetails() {
      return IpDetails.builder().datacenter("Falkenberg").version4()
            .platform("OpenVZ").ptr("31-192-227-113-static.serverhotell.net.")
            .nameServers("79.99.4.100", "79.99.4.101")
            .address("31.192.227.113")
            .cost(Cost.builder().amount(2.0).currency("EUR").timePeriod("month").build()).build();
   }

   public void testGetIpDetailsWhenResponseIs4xxReturnsNull() {

      IpApi api = requestSendsResponse(
            HttpRequest.builder().method("GET").endpoint("https://api.glesys.com/ip/details/ipaddress/31.192.227.37/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build(),
            HttpResponse.builder().statusCode(404).build()).getIpApi();

      assertEquals(api.get("31.192.227.37"), null);
   }

   public void testTakeWhenResponseIs2xx() {
      IpApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/ip/take/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("ipaddress", "46.21.105.186").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/ip_take.json")).build())
            .getIpApi();

      api.take("46.21.105.186");
   }

   @Test(expectedExceptions = HttpResponseException.class)
   public void testTakeWhenResponseIs4xxThrowsResponseException() {
      IpApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/ip/take/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("ipaddress", "46.21.105.186").build(),
            HttpResponse.builder().statusCode(400).build()).getIpApi();
      api.take("46.21.105.186");
   }

   public void testReleaseWhenResponseIs2xx() {
      IpApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/ip/release/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("ipaddress", "46.21.105.186").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/ip_release.json")).build())
            .getIpApi();

      api.release("46.21.105.186");
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testReleaseWhenResponseIs4xxThrowsResponseException() {
      IpApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/ip/release/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("ipaddress", "46.21.105.186").build(),
            HttpResponse.builder().statusCode(404).build())
            .getIpApi();

      api.release("46.21.105.186");
   }

   public void testListFreeWhenResponseIs2xx() {
      IpApi api = requestSendsResponse(
            HttpRequest.builder().method("GET").endpoint("https://api.glesys.com/ip/listfree/ipversion/4/datacenter/Falkenberg/platform/OpenVZ/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/ip_list_free.json")).build())
            .getIpApi();

      assertEquals(api.listFree(4, "Falkenberg", "OpenVZ").toSet(), ParseIpAddressFromResponseTest.EXPECTED_IPS);
   }

   public void testListFreeWhenResponseIs404ReturnsEmptySet() {
      IpApi api = requestSendsResponse(
            HttpRequest.builder().method("GET").endpoint("https://api.glesys.com/ip/listfree/ipversion/6/datacenter/Falkenberg/platform/OpenVZ/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build(),
            HttpResponse.builder().statusCode(404).build())
            .getIpApi();

      assertEquals(api.listFree(6, "Falkenberg", "OpenVZ").toSet(), ImmutableSet.of());
   }

   public void testAddWhenResponseIs2xx() {
      IpApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/ip/add/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("ipaddress", "31.192.227.37")
                       .addFormParam("serverid", "vz1946889").build(),
            HttpResponse.builder().statusCode(200).build())
            .getIpApi();

      api.addToServer("31.192.227.37", "vz1946889");
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testAddWhenResponseIs4xxThrowsHttpException() {
      IpApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/ip/add/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("ipaddress", "31.192.227.37")
                       .addFormParam("serverid", "vz1946889").build(),
            HttpResponse.builder().statusCode(401).build())
            .getIpApi();
      api.addToServer("31.192.227.37", "vz1946889");
   }

   public void testRemoveWhenResponseIs2xx() {
      IpApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/ip/remove/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("ipaddress", "31.192.227.37")
                       .addFormParam("serverid", "vz1946889").build(),
            HttpResponse.builder().statusCode(200).build())
            .getIpApi();

      api.removeFromServer("31.192.227.37", "vz1946889");
   }

   @Test(expectedExceptions = HttpResponseException.class)
   public void testRemoveWhenResponseIs4xxThrowsHttpException() {
      IpApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/ip/remove/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("ipaddress", "31.192.227.37")
                       .addFormParam("serverid", "vz1946889").build(),
            HttpResponse.builder().statusCode(400).build())
            .getIpApi();

      api.removeFromServer("31.192.227.37", "vz1946889");
   }

   public void testRemoveAndReleaseWhenResponseIs2xx() {
      IpApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/ip/remove/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("release", "true")
                       .addFormParam("ipaddress", "31.192.227.37")
                       .addFormParam("serverid", "vz1946889").build(),
            HttpResponse.builder().statusCode(200).build())
            .getIpApi();

      api.removeFromServerAndRelease("31.192.227.37", "vz1946889");
   }

   @Test(expectedExceptions = HttpResponseException.class)
   public void testRemoveAndReleaseWhenResponseIs4xxThrowsHttpException() {
      IpApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/ip/remove/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("release", "true")
                       .addFormParam("ipaddress", "31.192.227.37")
                       .addFormParam("serverid", "vz1946889").build(),
            HttpResponse.builder().statusCode(400).build())
            .getIpApi();

      api.removeFromServerAndRelease("31.192.227.37", "vz1946889");
   }

   public void testSetPrtWhenResponseIs2xx() {
      IpApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/ip/setptr/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("ipaddress", "31.192.227.37")
                       .addFormParam("data", "sommeptr.").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResourceWithContentType("/ip_get_details.json", MediaType.APPLICATION_JSON)).build())
            .getIpApi();

      assertEquals(api.setPtr("31.192.227.37", "sommeptr."), getIpInIpDetails());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testSetPtrWhenResponseIs4xxThrowsHttpException() {
      IpApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/ip/setptr/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("ipaddress", "31.192.227.37")
                       .addFormParam("data", "sommeptr.").build(),
            HttpResponse.builder().statusCode(404).build())
            .getIpApi();

      api.setPtr("31.192.227.37", "sommeptr.");
   }

   public void testResetPrtWhenResponseIs2xx() {
      IpApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/ip/resetptr/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("ipaddress", "31.192.227.37").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResourceWithContentType("/ip_get_details.json", MediaType.APPLICATION_JSON)).build())
            .getIpApi();

      assertEquals(api.resetPtr("31.192.227.37"), getIpInIpDetails());
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testResetPtrWhenResponseIs4xxThrowsHttpException() {
      IpApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/ip/resetptr/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("ipaddress", "31.192.227.37").build(),
            HttpResponse.builder().statusCode(401).build())
            .getIpApi();

      api.resetPtr("31.192.227.37");
   }
}
