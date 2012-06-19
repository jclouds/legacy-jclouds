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
package org.jclouds.openstack.nova.v2_0.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.NovaClient;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaClientExpectTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseFlavorListTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseFlavorTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * Tests annotation parsing of {@code FlavorAsyncClient}
 * 
 * @author Jeremy Daggett
 */
@Test(groups = "unit", testName = "FlavorClientExpectTest")
public class FlavorClientExpectTest extends BaseNovaClientExpectTest {

   public void testListFlavorsWhenResponseIs2xx() throws Exception {
      HttpRequest listFlavors = HttpRequest
            .builder()
            .method("GET")
            .endpoint(URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/flavors"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build()).build();

      HttpResponse listFlavorsResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/flavor_list.json")).build();

      NovaClient clientWhenFlavorsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, listFlavors, listFlavorsResponse);

      assertEquals(clientWhenFlavorsExist.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1"));

      assertEquals(clientWhenFlavorsExist.getFlavorClientForZone("az-1.region-a.geo-1").listFlavors().toString(),
            new ParseFlavorListTest().expected().toString());
   }

   public void testListFlavorsWhenReponseIs404IsEmpty() throws Exception {
      HttpRequest listFlavors = HttpRequest
            .builder()
            .method("GET")
            .endpoint(URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/flavors"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build()).build();

      HttpResponse listFlavorsResponse = HttpResponse.builder().statusCode(404).build();

      NovaClient clientWhenNoServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, listFlavors, listFlavorsResponse);

      assertTrue(clientWhenNoServersExist.getFlavorClientForZone("az-1.region-a.geo-1").listFlavors().isEmpty());
   }

   // TODO: gson deserializer for Multimap
   public void testGetFlavorWhenResponseIs2xx() throws Exception {

      HttpRequest getFlavor = HttpRequest
            .builder()
            .method("GET")
            .endpoint(URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/flavors/52415800-8b69-11e0-9b19-734f1195ff37"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build()).build();

      HttpResponse getFlavorResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/flavor_details.json")).build();

      NovaClient clientWhenFlavorsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, getFlavor, getFlavorResponse);

      assertEquals(
            clientWhenFlavorsExist.getFlavorClientForZone("az-1.region-a.geo-1").getFlavor("52415800-8b69-11e0-9b19-734f1195ff37")
                  .toString(), new ParseFlavorTest().expected().toString());
   }

   public void testGetFlavorWhenResponseIs404() throws Exception {
      HttpRequest getFlavor = HttpRequest
            .builder()
            .method("GET")
            .endpoint(URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/flavors/123"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build()).build();

      HttpResponse getFlavorResponse = HttpResponse.builder().statusCode(404)
            .payload(payloadFromResource("/flavor_details.json")).build();

      NovaClient clientWhenNoFlavorsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, getFlavor, getFlavorResponse);

      assertNull(clientWhenNoFlavorsExist.getFlavorClientForZone("az-1.region-a.geo-1").getFlavor("123"));

   }

}
