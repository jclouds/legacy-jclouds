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
package org.jclouds.openstack.nova.v2_0.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiExpectTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseCreateFlavorTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseFlavorListTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseFlavorTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests annotation parsing of {@code FlavorAsyncApi}
 * 
 * @author Jeremy Daggett, Ilja Bobkevic
 */
@Test(groups = "unit", testName = "FlavorApiExpectTest")
public class FlavorApiExpectTest extends BaseNovaApiExpectTest {

   public void testListFlavorsWhenResponseIs2xx() throws Exception {
      HttpRequest listFlavors = HttpRequest
            .builder()
            .method("GET")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/flavors")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse listFlavorsResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/flavor_list.json")).build();

      NovaApi apiWhenFlavorsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, listFlavors, listFlavorsResponse);

      assertEquals(apiWhenFlavorsExist.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1", "az-2.region-a.geo-1", "az-3.region-a.geo-1"));

      assertEquals(apiWhenFlavorsExist.getFlavorApiForZone("az-1.region-a.geo-1").list().concat().toString(),
            new ParseFlavorListTest().expected().toString());
   }

   public void testListFlavorsWhenReponseIs404IsEmpty() throws Exception {
      HttpRequest listFlavors = HttpRequest
            .builder()
            .method("GET")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/flavors")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse listFlavorsResponse = HttpResponse.builder().statusCode(404).build();

      NovaApi apiWhenNoServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, listFlavors, listFlavorsResponse);

      assertTrue(apiWhenNoServersExist.getFlavorApiForZone("az-1.region-a.geo-1").list().concat().isEmpty());
   }

   // TODO: gson deserializer for Multimap
   public void testGetFlavorWhenResponseIs2xx() throws Exception {

      HttpRequest getFlavor = HttpRequest
            .builder()
            .method("GET")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/flavors/52415800-8b69-11e0-9b19-734f1195ff37")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse getFlavorResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/flavor_details.json")).build();

      NovaApi apiWhenFlavorsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, getFlavor, getFlavorResponse);

      assertEquals(
            apiWhenFlavorsExist.getFlavorApiForZone("az-1.region-a.geo-1").get("52415800-8b69-11e0-9b19-734f1195ff37")
                  .toString(), new ParseFlavorTest().expected().toString());
   }

   public void testGetFlavorWhenResponseIs404() throws Exception {
      HttpRequest getFlavor = HttpRequest
            .builder()
            .method("GET")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/flavors/123")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse getFlavorResponse = HttpResponse.builder().statusCode(404)
            .payload(payloadFromResource("/flavor_details.json")).build();

      NovaApi apiWhenNoFlavorsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, getFlavor, getFlavorResponse);

      assertNull(apiWhenNoFlavorsExist.getFlavorApiForZone("az-1.region-a.geo-1").get("123"));

   }
   
   public void testCreateFlavor200() throws Exception {
   	ParseCreateFlavorTest parser = new ParseCreateFlavorTest();
      HttpRequest listFlavors = HttpRequest
            .builder()
            .method(HttpMethod.POST)
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/flavors")
            .addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .addHeader("X-Auth-Token", authToken)
            .payload(payloadFromResource(parser.resource())).build();
      
      HttpResponse listFlavorsResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource(parser.resource())).build();

      NovaApi api = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, listFlavors, listFlavorsResponse);

      assertEquals(
            api.getFlavorApiForZone("az-1.region-a.geo-1").create(Flavor.builder()
					.id("1cb47a44-9b84-4da4-bf81-c1976e8414ab")
					.name("128 MB Server").ram(128).vcpus(1)
					.disk(10).build())
                  .toString(), parser.expected().toString());
   }

   public void testDeleteFlavor202() throws Exception {
      String flavorId = "1cb47a44-9b84-4da4-bf81-c1976e8414ab";

      HttpRequest updateMetadata = HttpRequest
            .builder()
            .method(HttpMethod.DELETE)
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/flavors/" + flavorId)
            .addHeader(HttpHeaders.ACCEPT, MediaType.WILDCARD)
            .addHeader("X-Auth-Token", authToken)
            .build();

      HttpResponse updateMetadataResponse = HttpResponse.builder().statusCode(204).build();
      
      NovaApi api = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, updateMetadata, updateMetadataResponse);

      api.getFlavorApiForZone("az-1.region-a.geo-1").delete(flavorId);
   }
}
