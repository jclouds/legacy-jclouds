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

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.jclouds.io.Payloads.newUrlEncodedFormPayload;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;
import static org.testng.collections.Sets.newHashSet;

import java.net.URI;
import java.util.Set;

import org.jclouds.glesys.GleSYSClient;
import org.jclouds.glesys.domain.IpDetails;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.BaseRestClientExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * Allows us to test a client via its side effects.
 *
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "IpClientExpectTest")
public class IpClientExpectTest extends BaseRestClientExpectTest<GleSYSClient> {
   public IpClientExpectTest() {
      provider = "glesys";
   }

   public void testGetIpDetailsWhenResponseIs2xx() {

      IpClient client = requestSendsResponse(
              HttpRequest.builder().method("GET").endpoint(
                      URI.create("https://api.glesys.com/ip/details/ipaddress/31.192.227.37/format/json")).headers(
                      ImmutableMultimap.<String, String>builder().put("Accept", "application/json").put(
                              "Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build()).build(),
              HttpResponse.builder().statusCode(200).payload(payloadFromResource("/ip_get_details.json")).build())
              .getIpClient();

      assertEquals(client.getIpDetails("31.192.227.37"), IpDetails.builder().datacenter("Falkenberg").ipversion("4")
              .platform("OpenVZ").ptr("31-192-227-37-static.serverhotell.net.").build());

   }

   public void testGetIpDetailsWhenResponseIs4xxReturnsNull() {

      IpClient client = requestSendsResponse(
              HttpRequest.builder().method("GET").endpoint(
                      URI.create("https://api.glesys.com/ip/details/ipaddress/31.192.227.37/format/json")).headers(
                      ImmutableMultimap.<String, String>builder().put("Accept", "application/json").put(
                              "Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build()).build(),
              HttpResponse.builder().statusCode(404).build()).getIpClient();

      assertEquals(client.getIpDetails("31.192.227.37"), null);

   }

   public void testTakeWhenResponseIs2xx() {
      IpClient client = requestSendsResponse(
              HttpRequest.builder().method("POST").endpoint(
                      URI.create("https://api.glesys.com/ip/take/format/json"))
                      .headers(ImmutableMultimap.<String, String>builder()
                              .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                      .payload(newUrlEncodedFormPayload(
                              ImmutableMultimap.<String, String>builder().put("ipaddress", "46.21.105.186").build()
                      )).build(),
              HttpResponse.builder().statusCode(200).payload(payloadFromResource("/ip_take.json")).build())
              .getIpClient();

      client.take("46.21.105.186");
   }

   public void testTakeWhenResponseIs4xxThrowsResponseException() {
      IpClient client = requestSendsResponse(
              HttpRequest.builder().method("POST").endpoint(
                      URI.create("https://api.glesys.com/ip/take/format/json"))
                      .headers(ImmutableMultimap.<String, String>builder()
                              .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                      .payload(newUrlEncodedFormPayload(
                              ImmutableMultimap.<String, String>builder().put("ipaddress", "46.21.105.186").build()
                      )).build(),
              HttpResponse.builder().statusCode(400).build())
              .getIpClient();

      try {
         client.take("46.21.105.186");
         fail();
      } catch (HttpResponseException e) {
         // Expected
      }
   }

   public void testReleaseWhenResponseIs2xx() {
      IpClient client = requestSendsResponse(
              HttpRequest.builder().method("POST").endpoint(
                      URI.create("https://api.glesys.com/ip/release/format/json"))
                      .headers(ImmutableMultimap.<String, String>builder()
                              .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                      .payload(newUrlEncodedFormPayload(
                              ImmutableMultimap.<String, String>builder().put("ipaddress", "46.21.105.186").build()
                      )).build(),
              HttpResponse.builder().statusCode(200).payload(payloadFromResource("/ip_release.json")).build())
              .getIpClient();

      client.release("46.21.105.186");
   }

   public void testReleaseWhenResponseIs4xxThrowsResponseException() {
      IpClient client = requestSendsResponse(
              HttpRequest.builder().method("POST").endpoint(
                      URI.create("https://api.glesys.com/ip/release/format/json"))
                      .headers(ImmutableMultimap.<String, String>builder()
                              .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                      .payload(newUrlEncodedFormPayload(
                              ImmutableMultimap.<String, String>builder().put("ipaddress", "46.21.105.186").build()
                      )).build(),
              HttpResponse.builder().statusCode(400).build())
              .getIpClient();

      try {
         client.release("46.21.105.186");
         fail();
      } catch (HttpResponseException e) {
         // Expected
      }
   }

   public void testListFreeWhenResponseIs2xx() {
      IpClient client = requestSendsResponse(
              HttpRequest.builder().method("GET").endpoint(
                      URI.create("https://api.glesys.com/ip/listfree/ipversion/4/datacenter/Falkenberg/platform/OpenVZ/format/json"))
                      .headers(ImmutableMultimap.<String, String>builder().put("Accept", "application/json").put(
                              "Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build()).build(),
              HttpResponse.builder().statusCode(200).payload(payloadFromResource("/ip_list_free.json")).build())
              .getIpClient();

      Set<Object> expectedIps = newHashSet();
      expectedIps.addAll(asList("31.192.226.131", "31.192.226.133"));
      assertEquals(client.listFree("4", "Falkenberg", "OpenVZ"), expectedIps);
   }

   public void testListFreeWhenResponseIs404ReturnsEmptySet() {
      IpClient client = requestSendsResponse(
              HttpRequest.builder().method("GET").endpoint(
                      URI.create("https://api.glesys.com/ip/listfree/ipversion/4/datacenter/Falkenberg/platform/OpenVZ/format/json"))
                      .headers(ImmutableMultimap.<String, String>builder().put("Accept", "application/json").put(
                              "Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build()).build(),
              HttpResponse.builder().statusCode(404).build())
              .getIpClient();

      assertEquals(client.listFree("4", "Falkenberg", "OpenVZ"), emptySet());
   }

   public void testAddWhenResponseIs2xx() {
      IpClient client = requestSendsResponse(
              HttpRequest.builder().method("POST").endpoint(
                      URI.create("https://api.glesys.com/ip/add/format/json"))
                      .headers(ImmutableMultimap.<String, String>builder().put(
                              "Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                      .payload(newUrlEncodedFormPayload(
                              ImmutableMultimap.<String, String>builder()
                                      .put("ipaddress", "31.192.227.37")
                                      .put("serverid", "vz1946889").build())).build(),
              HttpResponse.builder().statusCode(200).build())
              .getIpClient();

      client.addIpToServer("31.192.227.37", "vz1946889");
   }

   public void testAddWhenResponseIs4xxThrowsHttpException() {
      IpClient client = requestSendsResponse(
              HttpRequest.builder().method("POST").endpoint(
                      URI.create("https://api.glesys.com/ip/add/format/json"))
                      .headers(ImmutableMultimap.<String, String>builder().put(
                              "Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                      .payload(newUrlEncodedFormPayload(
                              ImmutableMultimap.<String, String>builder()
                                      .put("ipaddress", "31.192.227.37")
                                      .put("serverid", "vz1946889")
                                      .build())).build(),
              HttpResponse.builder().statusCode(400).build())
              .getIpClient();

      try {
         client.addIpToServer("31.192.227.37", "vz1946889");
         fail();
      } catch (HttpResponseException e) {
         // Expected
      }
   }

   public void testRemoveWhenResponseIs2xx() {
      IpClient client = requestSendsResponse(
              HttpRequest.builder().method("POST").endpoint(
                      URI.create("https://api.glesys.com/ip/remove/format/json"))
                      .headers(ImmutableMultimap.<String, String>builder().put(
                              "Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                      .payload(newUrlEncodedFormPayload(
                              ImmutableMultimap.<String, String>builder()
                                      .put("ipaddress", "31.192.227.37")
                                      .put("serverid", "vz1946889").build())).build(),
              HttpResponse.builder().statusCode(200).build())
              .getIpClient();

      client.removeIpFromServer("31.192.227.37", "vz1946889");
   }

   public void testRemoveWhenResponseIs4xxThrowsHttpException() {
      IpClient client = requestSendsResponse(
              HttpRequest.builder().method("POST").endpoint(
                      URI.create("https://api.glesys.com/ip/remove/format/json"))
                      .headers(ImmutableMultimap.<String, String>builder().put(
                              "Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                      .payload(newUrlEncodedFormPayload(
                              ImmutableMultimap.<String, String>builder()
                                      .put("ipaddress", "31.192.227.37")
                                      .put("serverid", "vz1946889").build())).build(),
              HttpResponse.builder().statusCode(400).build())
              .getIpClient();

      try {
         client.removeIpFromServer("31.192.227.37", "vz1946889");
         fail();
      } catch (HttpResponseException e) {
         // Expected
      }
   }

}