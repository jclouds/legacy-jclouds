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
package org.jclouds.openstack.nova.v2_0.extensions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.jclouds.openstack.nova.v2_0.extensions.FlavorExtraSpecsClient;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaClientExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * Tests guice wiring and parsing of FlavorExtraSpecsClient
 *
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "FlavorExtraSpecsClientExpectTest")
public class FlavorExtraSpecsClientExpectTest extends BaseNovaClientExpectTest {

   public void testGetAllExtraSpecs() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/flavors/9/os-extra_specs");
      FlavorExtraSpecsClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).build(),
            standardResponseBuilder(200).payload(payloadFromResource("/volume_type_extra_specs.json")).build()
      ).getFlavorExtraSpecsExtensionForZone("az-1.region-a.geo-1").get();

      assertEquals(client.getAllExtraSpecs("9"), ImmutableMap.of("test", "value1"));
   }

   public void testGetAllExtraSpecsFailNotFound() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/flavors/9/os-extra_specs");
      FlavorExtraSpecsClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).build(),
            standardResponseBuilder(404).build()
      ).getFlavorExtraSpecsExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(client.getAllExtraSpecs("9").isEmpty());
   }

   public void testSetAllExtraSpecs() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/flavors/9/os-extra_specs");
      FlavorExtraSpecsClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint)
                  .method("POST")
                  .payload(payloadFromStringWithContentType("{\"extra_specs\":{\"test1\":\"somevalue\"}}", MediaType.APPLICATION_JSON)).build(),
            standardResponseBuilder(200).build()
      ).getFlavorExtraSpecsExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(client.setAllExtraSpecs("9", ImmutableMap.of("test1", "somevalue")));
   }

   public void testSetExtraSpec() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/flavors/5/os-extra_specs/test1");
      FlavorExtraSpecsClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint)
                  .method("PUT")
                  .payload(payloadFromStringWithContentType("{\"test1\":\"somevalue\"}", MediaType.APPLICATION_JSON)).build(),
            standardResponseBuilder(200).build()
      ).getFlavorExtraSpecsExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(client.setExtraSpec("5", "test1", "somevalue"));
   }

   public void testGetExtraSpec() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/flavors/5/os-extra_specs/test1");
      FlavorExtraSpecsClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).build(),
            standardResponseBuilder(200).payload(payloadFromStringWithContentType("{\"test1\":\"another value\"}", MediaType.APPLICATION_JSON)).build()
      ).getFlavorExtraSpecsExtensionForZone("az-1.region-a.geo-1").get();

      assertEquals(client.getExtraSpec("5", "test1"), "another value");
   }

   public void testGetExtraSpecFailNotFound() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/flavors/5/os-extra_specs/test1");
      FlavorExtraSpecsClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).build(),
            standardResponseBuilder(404).build()
      ).getFlavorExtraSpecsExtensionForZone("az-1.region-a.geo-1").get();

      assertNull(client.getExtraSpec("5", "test1"));
   }

   public void testDeleteExtraSpec() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/flavors/5/os-extra_specs/test1");
      FlavorExtraSpecsClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).method("DELETE").build(),
            standardResponseBuilder(200).build()
      ).getFlavorExtraSpecsExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(client.deleteExtraSpec("5", "test1"));
   }

   public void testDeleteExtraSpecFailNotFound() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/flavors/5/os-extra_specs/test1");
      FlavorExtraSpecsClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).method("DELETE").build(),
            standardResponseBuilder(404).build()
      ).getFlavorExtraSpecsExtensionForZone("az-1.region-a.geo-1").get();

      assertFalse(client.deleteExtraSpec("5", "test1"));
   }

}
