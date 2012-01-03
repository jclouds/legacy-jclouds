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

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.glesys.GleSYSClient;
import org.jclouds.glesys.domain.IpDetails;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.BaseRestClientExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * 
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
                        ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put(
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
                        ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put(
                                 "Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build()).build(),
               HttpResponse.builder().statusCode(404).build()).getIpClient();

      assertEquals(client.getIpDetails("31.192.227.37"), null);

   }
}