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
package org.jclouds.glesys.features;

import static java.util.Collections.emptySet;
import static org.jclouds.io.Payloads.newUrlEncodedFormPayload;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.jclouds.glesys.domain.Cost;
import org.jclouds.glesys.domain.IpDetails;
import org.jclouds.glesys.internal.BaseGleSYSClientExpectTest;
import org.jclouds.glesys.parse.ParseIpAddressFromResponseTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * Allows us to test a client via its side effects.
 *
 * @author Adrian Cole, Adam Lowe
 */
@Test(groups = "unit", testName = "IpClientExpectTest")
public class IpClientExpectTest extends BaseGleSYSClientExpectTest {

   public void testListIpsWhenResponseIs2xx() {
      IpClient client = requestSendsResponse(
            HttpRequest.builder().method("GET").endpoint(
                  URI.create("https://api.glesys.com/ip/listown/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder().put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build()).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/ip_list_own.json")).build())
            .getIpClient();

      IpDetails.Builder builder = IpDetails.builder().datacenter("Falkenberg").version4().reserved(true)
            .platform("OpenVZ")
            .nameServers("79.99.4.100", "79.99.4.101")
            .cost(Cost.builder().amount(2.0).currency("EUR").timePeriod("month").build());

      assertEquals(client.listIps().toString(), ImmutableSet.of(
            builder.ptr("31-192-230-68-static.serverhotell.net.").address("31.192.230.68").serverId(null).build(),
            builder.ptr("31-192-231-148-static.serverhotell.net.").address("31.192.231.148").serverId("vz1609110").build()).toString());
   }

   public void testListIpsWhenResponseIs4xxReturnsEmpty() {
      IpClient client = requestSendsResponse(
            HttpRequest.builder().method("GET").endpoint(
                  URI.create("https://api.glesys.com/ip/listown/format/json")).headers(
                  ImmutableMultimap.<String, String>builder().put("Accept", "application/json").put(
                        "Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build()).build(),
            HttpResponse.builder().statusCode(404).build()).getIpClient();

      assertTrue(client.listIps().isEmpty());
   }

   public void testGetIpDetailsWhenResponseIs2xx() {
      IpClient client = requestSendsResponse(
            HttpRequest.builder().method("GET").endpoint(
                  URI.create("https://api.glesys.com/ip/details/ipaddress/31.192.227.113/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder().put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build()).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/ip_get_details.json")).build())
            .getIpClient();

      assertEquals(client.getIp("31.192.227.113"), getIpInIpDetails());
   }

   protected IpDetails getIpInIpDetails() {
      return IpDetails.builder().datacenter("Falkenberg").version4()
            .platform("OpenVZ").ptr("31-192-227-113-static.serverhotell.net.")
            .nameServers("79.99.4.100", "79.99.4.101")
            .address("31.192.227.113")
            .cost(Cost.builder().amount(2.0).currency("EUR").timePeriod("month").build()).build();
   }

   public void testGetIpDetailsWhenResponseIs4xxReturnsNull() {

      IpClient client = requestSendsResponse(
            HttpRequest.builder().method("GET").endpoint(
                  URI.create("https://api.glesys.com/ip/details/ipaddress/31.192.227.37/format/json")).headers(
                  ImmutableMultimap.<String, String>builder().put("Accept", "application/json").put(
                        "Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build()).build(),
            HttpResponse.builder().statusCode(404).build()).getIpClient();

      assertEquals(client.getIp("31.192.227.37"), null);
   }

   public void testTakeWhenResponseIs2xx() {
      IpClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(
                  URI.create("https://api.glesys.com/ip/take/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder().put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(
                        ImmutableMultimap.<String, String>builder().put("ipaddress", "46.21.105.186").build()
                  )).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/ip_take.json")).build())
            .getIpClient();

      client.take("46.21.105.186");
   }

   @Test(expectedExceptions = HttpResponseException.class)
   public void testTakeWhenResponseIs4xxThrowsResponseException() {
      IpClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(
                  URI.create("https://api.glesys.com/ip/take/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder().put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(
                        ImmutableMultimap.<String, String>builder().put("ipaddress", "46.21.105.186").build()
                  )).build(),
            HttpResponse.builder().statusCode(400).build()).getIpClient();
      client.take("46.21.105.186");
   }

   public void testReleaseWhenResponseIs2xx() {
      IpClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(
                  URI.create("https://api.glesys.com/ip/release/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder().put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(
                        ImmutableMultimap.<String, String>builder().put("ipaddress", "46.21.105.186").build()
                  )).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/ip_release.json")).build())
            .getIpClient();

      client.release("46.21.105.186");
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testReleaseWhenResponseIs4xxThrowsResponseException() {
      IpClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(
                  URI.create("https://api.glesys.com/ip/release/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder().put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(
                        ImmutableMultimap.<String, String>builder().put("ipaddress", "46.21.105.186").build()
                  )).build(),
            HttpResponse.builder().statusCode(404).build())
            .getIpClient();

      client.release("46.21.105.186");
   }

   public void testListFreeWhenResponseIs2xx() {
      IpClient client = requestSendsResponse(
            HttpRequest.builder().method("GET").endpoint(
                  URI.create("https://api.glesys.com/ip/listfree/ipversion/4/datacenter/Falkenberg/platform/OpenVZ/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder().put("Accept", "application/json").put(
                        "Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build()).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/ip_list_free.json")).build())
            .getIpClient();

      assertEquals(client.listFree(4, "Falkenberg", "OpenVZ"), ParseIpAddressFromResponseTest.EXPECTED_IPS);
   }

   public void testListFreeWhenResponseIs404ReturnsEmptySet() {
      IpClient client = requestSendsResponse(
            HttpRequest.builder().method("GET").endpoint(
                  URI.create("https://api.glesys.com/ip/listfree/ipversion/6/datacenter/Falkenberg/platform/OpenVZ/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder().put("Accept", "application/json").put(
                        "Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build()).build(),
            HttpResponse.builder().statusCode(404).build())
            .getIpClient();

      assertEquals(client.listFree(6, "Falkenberg", "OpenVZ"), emptySet());
   }

   public void testAddWhenResponseIs2xx() {
      IpClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(
                  URI.create("https://api.glesys.com/ip/add/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder().put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(
                        ImmutableMultimap.<String, String>builder()
                              .put("ipaddress", "31.192.227.37")
                              .put("serverid", "vz1946889").build())).build(),
            HttpResponse.builder().statusCode(200).build())
            .getIpClient();

      client.addIpToServer("31.192.227.37", "vz1946889");
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testAddWhenResponseIs4xxThrowsHttpException() {
      IpClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(
                  URI.create("https://api.glesys.com/ip/add/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder().put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(
                        ImmutableMultimap.<String, String>builder()
                              .put("ipaddress", "31.192.227.37")
                              .put("serverid", "vz1946889")
                              .build())).build(),
            HttpResponse.builder().statusCode(401).build())
            .getIpClient();
      client.addIpToServer("31.192.227.37", "vz1946889");
   }

   public void testRemoveWhenResponseIs2xx() {
      IpClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(
                  URI.create("https://api.glesys.com/ip/remove/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder().put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(
                        ImmutableMultimap.<String, String>builder()
                              .put("ipaddress", "31.192.227.37")
                              .put("serverid", "vz1946889").build())).build(),
            HttpResponse.builder().statusCode(200).build())
            .getIpClient();

      client.removeIpFromServer("31.192.227.37", "vz1946889");
   }

   @Test(expectedExceptions = HttpResponseException.class)
   public void testRemoveWhenResponseIs4xxThrowsHttpException() {
      IpClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(
                  URI.create("https://api.glesys.com/ip/remove/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder().put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(
                        ImmutableMultimap.<String, String>builder()
                              .put("ipaddress", "31.192.227.37")
                              .put("serverid", "vz1946889").build())).build(),
            HttpResponse.builder().statusCode(400).build())
            .getIpClient();

      client.removeIpFromServer("31.192.227.37", "vz1946889");
   }

   public void testRemoveAndReleaseWhenResponseIs2xx() {
      IpClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(
                  URI.create("https://api.glesys.com/ip/remove/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder().put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(
                        ImmutableMultimap.<String, String>builder()
                              .put("release", "true")
                              .put("ipaddress", "31.192.227.37")
                              .put("serverid", "vz1946889").build())).build(),
            HttpResponse.builder().statusCode(200).build())
            .getIpClient();

      client.removeIpFromServerAndRelease("31.192.227.37", "vz1946889");
   }

   @Test(expectedExceptions = HttpResponseException.class)
   public void testRemoveAndReleaseWhenResponseIs4xxThrowsHttpException() {
      IpClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(
                  URI.create("https://api.glesys.com/ip/remove/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder().put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(
                        ImmutableMultimap.<String, String>builder()
                              .put("release", "true")
                              .put("ipaddress", "31.192.227.37")
                              .put("serverid", "vz1946889").build())).build(),
            HttpResponse.builder().statusCode(400).build())
            .getIpClient();

      client.removeIpFromServerAndRelease("31.192.227.37", "vz1946889");
   }

   public void testSetPrtWhenResponseIs2xx() {
      IpClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(
                  URI.create("https://api.glesys.com/ip/setptr/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder().put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(
                        ImmutableMultimap.<String, String>builder()
                              .put("ipaddress", "31.192.227.37")
                              .put("data", "sommeptr.").build())).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResourceWithContentType("/ip_get_details.json", MediaType.APPLICATION_JSON)).build())
            .getIpClient();

      assertEquals(client.setPtr("31.192.227.37", "sommeptr."), getIpInIpDetails());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testSetPtrWhenResponseIs4xxThrowsHttpException() {
      IpClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(
                  URI.create("https://api.glesys.com/ip/setptr/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder().put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(
                        ImmutableMultimap.<String, String>builder()
                              .put("ipaddress", "31.192.227.37")
                              .put("data", "sommeptr.").build())).build(),
            HttpResponse.builder().statusCode(404).build())
            .getIpClient();

      client.setPtr("31.192.227.37", "sommeptr.");
   }

   public void testResetPrtWhenResponseIs2xx() {
      IpClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(
                  URI.create("https://api.glesys.com/ip/resetptr/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder().put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(
                        ImmutableMultimap.<String, String>builder()
                              .put("ipaddress", "31.192.227.37").build())).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResourceWithContentType("/ip_get_details.json", MediaType.APPLICATION_JSON)).build())
            .getIpClient();

      assertEquals(client.resetPtr("31.192.227.37"), getIpInIpDetails());
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testResetPtrWhenResponseIs4xxThrowsHttpException() {
      IpClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(
                  URI.create("https://api.glesys.com/ip/resetptr/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder().put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(
                        ImmutableMultimap.<String, String>builder()
                              .put("ipaddress", "31.192.227.37").build())).build(),
            HttpResponse.builder().statusCode(401).build())
            .getIpClient();

      client.resetPtr("31.192.227.37");
   }
}