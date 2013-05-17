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
package org.jclouds.openstack.nova.v2_0.extensions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * Tests guice wiring and parsing of FlavorExtraSpecsApi
 *
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "FlavorExtraSpecsApiExpectTest")
public class FlavorExtraSpecsApiExpectTest extends BaseNovaApiExpectTest {

   public void testGetAllExtraSpecs() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/flavors/9/os-extra_specs");
      FlavorExtraSpecsApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/volume_type_extra_specs.json")).build()
      ).getFlavorExtraSpecsExtensionForZone("az-1.region-a.geo-1").get();

      assertEquals(api.getMetadata("9"), ImmutableMap.of("test", "value1"));
   }

   public void testGetAllExtraSpecsFailNotFound() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/flavors/9/os-extra_specs");
      FlavorExtraSpecsApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).build()
      ).getFlavorExtraSpecsExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(api.getMetadata("9").isEmpty());
   }

   public void testSetAllExtraSpecs() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/flavors/9/os-extra_specs");
      FlavorExtraSpecsApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint)
                  .method("POST")
                  .payload(payloadFromStringWithContentType("{\"extra_specs\":{\"test1\":\"somevalue\"}}", MediaType.APPLICATION_JSON)).build(),
            HttpResponse.builder().statusCode(200).build()
      ).getFlavorExtraSpecsExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(api.updateMetadata("9", ImmutableMap.of("test1", "somevalue")));
   }

   public void testSetExtraSpec() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/flavors/5/os-extra_specs/test1");
      FlavorExtraSpecsApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint)
                  .method("PUT")
                  .payload(payloadFromStringWithContentType("{\"test1\":\"somevalue\"}", MediaType.APPLICATION_JSON)).build(),
            HttpResponse.builder().statusCode(200).build()
      ).getFlavorExtraSpecsExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(api.updateMetadataEntry("5", "test1", "somevalue"));
   }

   public void testGetExtraSpec() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/flavors/5/os-extra_specs/test1");
      FlavorExtraSpecsApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromStringWithContentType("{\"test1\":\"another value\"}", MediaType.APPLICATION_JSON)).build()
      ).getFlavorExtraSpecsExtensionForZone("az-1.region-a.geo-1").get();

      assertEquals(api.getMetadataKey("5", "test1"), "another value");
   }

   public void testGetExtraSpecFailNotFound() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/flavors/5/os-extra_specs/test1");
      FlavorExtraSpecsApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).build()
      ).getFlavorExtraSpecsExtensionForZone("az-1.region-a.geo-1").get();

      assertNull(api.getMetadataKey("5", "test1"));
   }

   public void testDeleteExtraSpec() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/flavors/5/os-extra_specs/test1");
      FlavorExtraSpecsApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).method("DELETE").build(),
            HttpResponse.builder().statusCode(200).build()
      ).getFlavorExtraSpecsExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(api.deleteMetadataKey("5", "test1"));
   }

   public void testDeleteExtraSpecFailNotFound() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/flavors/5/os-extra_specs/test1");
      FlavorExtraSpecsApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).method("DELETE").build(),
            HttpResponse.builder().statusCode(404).build()
      ).getFlavorExtraSpecsExtensionForZone("az-1.region-a.geo-1").get();

      assertFalse(api.deleteMetadataKey("5", "test1"));
   }

}
