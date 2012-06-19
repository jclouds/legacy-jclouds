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
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.openstack.nova.v2_0.domain.VolumeType;
import org.jclouds.openstack.nova.v2_0.extensions.VolumeTypeClient;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaClientExpectTest;
import org.jclouds.openstack.nova.v2_0.options.CreateVolumeTypeOptions;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Tests guice wiring and parsing of VolumeTypeClient
 *
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "VolumeTypeClientExpectTest")
public class VolumeTypeClientExpectTest extends BaseNovaClientExpectTest {
   private DateService dateService = new SimpleDateFormatDateService();

   public void testListVolumeTypes() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volume-types");
      VolumeTypeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).build(),
            standardResponseBuilder(200).payload(payloadFromResource("/volume_type_list.json")).build()
      ).getVolumeTypeExtensionForZone("az-1.region-a.geo-1").get();

      Set<VolumeType> types = client.listVolumeTypes();
      assertEquals(types, ImmutableSet.of(testVolumeType()));
   }

   public void testGetVolumeType() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volume-types/8");
      VolumeTypeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).build(),
            standardResponseBuilder(200).payload(payloadFromResource("/volume_type.json")).build()
      ).getVolumeTypeExtensionForZone("az-1.region-a.geo-1").get();

      VolumeType type = client.getVolumeType("8");
      assertEquals(type, testVolumeType());
   }

   public void testGetVolumeTypeFailNotFound() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volume-types/8");
      VolumeTypeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).build(),
            standardResponseBuilder(404).build()
      ).getVolumeTypeExtensionForZone("az-1.region-a.geo-1").get();

      assertNull(client.getVolumeType("8"));
   }

   public void testCreateVolumeType() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volume-types");
      VolumeTypeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).method("POST")
                  .payload(payloadFromStringWithContentType("{\"volume_type\":{\"name\":\"jclouds-test-1\"}}", MediaType.APPLICATION_JSON))
                  .build(),
            standardResponseBuilder(200).payload(payloadFromResource("/volume_type.json")).build()
      ).getVolumeTypeExtensionForZone("az-1.region-a.geo-1").get();

      VolumeType type = client.createVolumeType("jclouds-test-1");
      assertEquals(type, testVolumeType());
   }

   public void testCreateVolumeTypeWithOptsNONE() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volume-types");
      VolumeTypeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).method("POST")
                  .payload(payloadFromStringWithContentType("{\"volume_type\":{\"name\":\"jclouds-test-1\",\"extra_specs\":{}}}", MediaType.APPLICATION_JSON))
                  .build(),
            standardResponseBuilder(200).payload(payloadFromResource("/volume_type.json")).build()
      ).getVolumeTypeExtensionForZone("az-1.region-a.geo-1").get();

      VolumeType type = client.createVolumeType("jclouds-test-1", CreateVolumeTypeOptions.NONE);
      assertEquals(type, testVolumeType());
   }

   public void testCreateVolumeTypeWithOptsSet() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volume-types");
      VolumeTypeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).method("POST")
                  .payload(payloadFromStringWithContentType("{\"volume_type\":{\"name\":\"jclouds-test-1\",\"extra_specs\":{\"x\": \"y\"}}}", MediaType.APPLICATION_JSON))
                  .build(),
            standardResponseBuilder(200).payload(payloadFromResource("/volume_type.json")).build()
      ).getVolumeTypeExtensionForZone("az-1.region-a.geo-1").get();

      VolumeType type = client.createVolumeType("jclouds-test-1", CreateVolumeTypeOptions.Builder.specs(ImmutableMap.of("x", "y")));
      assertEquals(type, testVolumeType());
   }

   public void testDeleteVolumeType() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volume-types/8");
      VolumeTypeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).method("DELETE").build(),
            standardResponseBuilder(200).build()
      ).getVolumeTypeExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(client.deleteVolumeType("8"));
   }

   public void testDeleteVolumeTypeFailNotFound() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volume-types/8");
      VolumeTypeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).method("DELETE").build(),
            standardResponseBuilder(404).build()
      ).getVolumeTypeExtensionForZone("az-1.region-a.geo-1").get();

      assertFalse(client.deleteVolumeType("8"));
   }

   public void testGetAllExtraSpecs() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volume-types/9/extra_specs");
      VolumeTypeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).build(),
            standardResponseBuilder(200).payload(payloadFromResource("/volume_type_extra_specs.json")).build()
      ).getVolumeTypeExtensionForZone("az-1.region-a.geo-1").get();

      assertEquals(client.getAllExtraSpecs("9"), ImmutableMap.of("test", "value1"));
   }

   public void testGetAllExtraSpecsFailNotFound() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volume-types/9/extra_specs");
      VolumeTypeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).build(),
            standardResponseBuilder(404).build()
      ).getVolumeTypeExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(client.getAllExtraSpecs("9").isEmpty());
   }

   public void testSetAllExtraSpecs() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volume-types/9/extra_specs");
      VolumeTypeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint)
                  .method("POST")
                  .payload(payloadFromStringWithContentType("{\"extra_specs\":{\"test1\":\"somevalue\"}}", MediaType.APPLICATION_JSON)).build(),
            standardResponseBuilder(200).build()
      ).getVolumeTypeExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(client.setAllExtraSpecs("9", ImmutableMap.of("test1", "somevalue")));
   }

   public void testSetExtraSpec() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volume-types/5/extra_specs/test1");
      VolumeTypeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint)
                  .method("PUT")
                  .payload(payloadFromStringWithContentType("{\"test1\":\"somevalue\"}", MediaType.APPLICATION_JSON)).build(),
            standardResponseBuilder(200).build()
      ).getVolumeTypeExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(client.setExtraSpec("5", "test1", "somevalue"));
   }

   public void testGetExtraSpec() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volume-types/5/extra_specs/test1");
      VolumeTypeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).build(),
            standardResponseBuilder(200).payload(payloadFromStringWithContentType("{\"test1\":\"another value\"}", MediaType.APPLICATION_JSON)).build()
      ).getVolumeTypeExtensionForZone("az-1.region-a.geo-1").get();

      assertEquals(client.getExtraSpec("5", "test1"), "another value");
   }

   public void testGetExtraSpecFailNotFound() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volume-types/5/extra_specs/test1");
      VolumeTypeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).build(),
            standardResponseBuilder(404).build()
      ).getVolumeTypeExtensionForZone("az-1.region-a.geo-1").get();

      assertNull(client.getExtraSpec("5", "test1"));
   }

   public void testDeleteExtraSpec() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volume-types/5/extra_specs/test1");
      VolumeTypeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).method("DELETE").build(),
            standardResponseBuilder(200).build()
      ).getVolumeTypeExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(client.deleteExtraSpec("5", "test1"));
   }

   public void testDeleteExtraSpecFailNotFound() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volume-types/5/extra_specs/test1");
      VolumeTypeClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).method("DELETE").build(),
            standardResponseBuilder(404).build()
      ).getVolumeTypeExtensionForZone("az-1.region-a.geo-1").get();

      assertFalse(client.deleteExtraSpec("5", "test1"));
   }

   public VolumeType testVolumeType() {
      return VolumeType.builder().id("8").name("jclouds-test-1").created(dateService.iso8601SecondsDateParse("2012-05-10 12:33:06")).extraSpecs(ImmutableMap.of("test", "value1", "test1", "wibble")).build();
   }
}
