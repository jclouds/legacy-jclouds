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
package org.jclouds.openstack.nova.v1_1.compute;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import org.jclouds.compute.ComputeService;
import org.jclouds.domain.Location;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v1_1.internal.BaseNovaComputeServiceExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * Tests the compute service abstraction of the nova client.
 * 
 * @author Matt Stephenson
 */
@Test(groups = "unit", testName = "NovaComputeServiceExpectTest")
public class NovaComputeServiceExpectTest extends BaseNovaComputeServiceExpectTest {

   public void testListServersWhenResponseIs2xx() throws Exception {
      HttpRequest listImagesDetail = HttpRequest.builder().method("GET").endpoint(
               URI.create("https://compute.north.host/v1.1/3456/images/detail")).headers(
               ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                        authToken).build()).build();

      HttpResponse listImagesDetailResponse = HttpResponse.builder().statusCode(200).payload(
               payloadFromResource("/image_list_detail.json")).build();

      HttpRequest listFlavorsDetail = HttpRequest.builder().method("GET").endpoint(
               URI.create("https://compute.north.host/v1.1/3456/flavors/detail")).headers(
               ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                        authToken).build()).build();

      HttpResponse listFlavorsDetailResponse = HttpResponse.builder().statusCode(200).payload(
               payloadFromResource("/flavor_list_detail.json")).build();

      HttpRequest listFloatingIps = HttpRequest.builder().method("GET").endpoint(
               URI.create("https://compute.north.host/v1.1/3456/os-floating-ips")).headers(
               ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                        authToken).build()).build();

      HttpResponse listFloatingIpsResponse = HttpResponse.builder().statusCode(200).payload(
               payloadFromResource("/floatingip_list.json")).build();

      HttpRequest listServers = HttpRequest.builder().method("GET").endpoint(
               URI.create("https://compute.north.host/v1.1/3456/servers/detail")).headers(
               ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                        authToken).build()).build();

      HttpResponse listServersResponse = HttpResponse.builder().statusCode(200).payload(
               payloadFromResource("/server_list_details.json")).build();

      Map<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder().put(
               keystoneAuthWithAccessKeyAndSecretKey, responseWithKeystoneAccess).put(extensionsOfNovaRequest,
               extensionsOfNovaResponse).put(listFloatingIps, listFloatingIpsResponse).put(listServers,
               listServersResponse).put(listImagesDetail, listImagesDetailResponse).put(listFlavorsDetail,
               listFlavorsDetailResponse).build();

      ComputeService clientWhenServersExist = requestsSendResponses(requestResponseMap);

      Set<? extends Location> locations = clientWhenServersExist.listAssignableLocations();
      assertNotNull(locations);
      assertEquals(locations.size(), 1);
      assertEquals(locations.iterator().next().getId(), "az-1.region-a.geo-1");

      assertNotNull(clientWhenServersExist.listNodes());
      assertEquals(clientWhenServersExist.listNodes().size(), 1);
      assertEquals(clientWhenServersExist.listNodes().iterator().next().getId(),
               "az-1.region-a.geo-1/52415800-8b69-11e0-9b19-734f000004d2");
      assertEquals(clientWhenServersExist.listNodes().iterator().next().getName(), "sample-server");
   }

   public void testListServersWhenReponseIs404IsEmpty() throws Exception {
      HttpRequest listServers = HttpRequest.builder().method("GET").endpoint(
               URI.create("https://compute.north.host/v1.1/3456/servers/detail")).headers(
               ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                        authToken).build()).build();

      HttpResponse listServersResponse = HttpResponse.builder().statusCode(404).build();

      ComputeService clientWhenNoServersExist = requestsSendResponses(keystoneAuthWithAccessKeyAndSecretKey,
               responseWithKeystoneAccess, listServers, listServersResponse);

      assertTrue(clientWhenNoServersExist.listNodes().isEmpty());
   }
}
