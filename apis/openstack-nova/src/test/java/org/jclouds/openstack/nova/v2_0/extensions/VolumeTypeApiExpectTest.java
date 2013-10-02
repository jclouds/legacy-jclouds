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
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.domain.VolumeType;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiExpectTest;
import org.jclouds.openstack.nova.v2_0.options.CreateVolumeTypeOptions;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Tests guice wiring and parsing of VolumeTypeApi
 *
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "VolumeTypeApiExpectTest")
public class VolumeTypeApiExpectTest extends BaseNovaApiExpectTest {
   private DateService dateService = new SimpleDateFormatDateService();

   public void testListVolumeTypes() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volume-types");
      VolumeTypeApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/volume_type_list.json")).build()
      ).getVolumeTypeExtensionForZone("az-1.region-a.geo-1").get();

      Set<? extends VolumeType> types = api.list().toSet();
      assertEquals(types, ImmutableSet.of(testVolumeType()));
   }

   public void testGetVolumeType() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volume-types/8");
      VolumeTypeApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/volume_type.json")).build()
      ).getVolumeTypeExtensionForZone("az-1.region-a.geo-1").get();

      VolumeType type = api.get("8");
      assertEquals(type, testVolumeType());
   }

   public void testGetVolumeTypeFailNotFound() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volume-types/8");
      VolumeTypeApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).build()
      ).getVolumeTypeExtensionForZone("az-1.region-a.geo-1").get();

      assertNull(api.get("8"));
   }

   public void testCreateVolumeType() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volume-types");
      VolumeTypeApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).method("POST")
                  .payload(payloadFromStringWithContentType("{\"volume_type\":{\"name\":\"jclouds-test-1\"}}", MediaType.APPLICATION_JSON))
                  .build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/volume_type.json")).build()
      ).getVolumeTypeExtensionForZone("az-1.region-a.geo-1").get();

      VolumeType type = api.create("jclouds-test-1");
      assertEquals(type, testVolumeType());
   }

   public void testCreateVolumeTypeWithOptsNONE() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volume-types");
      VolumeTypeApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).method("POST")
                  .payload(payloadFromStringWithContentType("{\"volume_type\":{\"name\":\"jclouds-test-1\",\"extra_specs\":{}}}", MediaType.APPLICATION_JSON))
                  .build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/volume_type.json")).build()
      ).getVolumeTypeExtensionForZone("az-1.region-a.geo-1").get();

      VolumeType type = api.create("jclouds-test-1", CreateVolumeTypeOptions.NONE);
      assertEquals(type, testVolumeType());
   }

   public void testCreateVolumeTypeWithOptsSet() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volume-types");
      VolumeTypeApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).method("POST")
                  .payload(payloadFromStringWithContentType("{\"volume_type\":{\"name\":\"jclouds-test-1\",\"extra_specs\":{\"x\": \"y\"}}}", MediaType.APPLICATION_JSON))
                  .build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/volume_type.json")).build()
      ).getVolumeTypeExtensionForZone("az-1.region-a.geo-1").get();

      VolumeType type = api.create("jclouds-test-1", CreateVolumeTypeOptions.Builder.specs(ImmutableMap.of("x", "y")));
      assertEquals(type, testVolumeType());
   }

   public void testDeleteVolumeType() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volume-types/8");
      VolumeTypeApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).method("DELETE").build(),
            HttpResponse.builder().statusCode(200).build()
      ).getVolumeTypeExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(api.delete("8"));
   }

   public void testDeleteVolumeTypeFailNotFound() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volume-types/8");
      VolumeTypeApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).method("DELETE").build(),
            HttpResponse.builder().statusCode(404).build()
      ).getVolumeTypeExtensionForZone("az-1.region-a.geo-1").get();

      assertFalse(api.delete("8"));
   }

   public void testGetAllExtraSpecs() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volume-types/9/extra_specs");
      VolumeTypeApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/volume_type_extra_specs.json")).build()
      ).getVolumeTypeExtensionForZone("az-1.region-a.geo-1").get();

      assertEquals(api.getExtraSpecs("9"), ImmutableMap.of("test", "value1"));
   }

   public void testGetAllExtraSpecsFailNotFound() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volume-types/9/extra_specs");
      VolumeTypeApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).build()
      ).getVolumeTypeExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(api.getExtraSpecs("9").isEmpty());
   }

   public void testSetAllExtraSpecs() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volume-types/9/extra_specs");
      VolumeTypeApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint)
                  .method("POST")
                  .payload(payloadFromStringWithContentType("{\"extra_specs\":{\"test1\":\"somevalue\"}}", MediaType.APPLICATION_JSON)).build(),
            HttpResponse.builder().statusCode(200).build()
      ).getVolumeTypeExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(api.updateExtraSpecs("9", ImmutableMap.of("test1", "somevalue")));
   }

   public void testSetExtraSpec() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volume-types/5/extra_specs/test1");
      VolumeTypeApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint)
                  .method("PUT")
                  .payload(payloadFromStringWithContentType("{\"test1\":\"somevalue\"}", MediaType.APPLICATION_JSON)).build(),
            HttpResponse.builder().statusCode(200).build()
      ).getVolumeTypeExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(api.updateExtraSpec("5", "test1", "somevalue"));
   }

   public void testGetExtraSpec() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volume-types/5/extra_specs/test1");
      VolumeTypeApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromStringWithContentType("{\"test1\":\"another value\"}", MediaType.APPLICATION_JSON)).build()
      ).getVolumeTypeExtensionForZone("az-1.region-a.geo-1").get();

      assertEquals(api.getExtraSpec("5", "test1"), "another value");
   }

   public void testGetExtraSpecFailNotFound() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volume-types/5/extra_specs/test1");
      VolumeTypeApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).build()
      ).getVolumeTypeExtensionForZone("az-1.region-a.geo-1").get();

      assertNull(api.getExtraSpec("5", "test1"));
   }

   public void testDeleteExtraSpec() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volume-types/5/extra_specs/test1");
      VolumeTypeApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).method("DELETE").build(),
            HttpResponse.builder().statusCode(200).build()
      ).getVolumeTypeExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(api.deleteExtraSpec("5", "test1"));
   }

   public void testDeleteExtraSpecFailNotFound() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-volume-types/5/extra_specs/test1");
      VolumeTypeApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).method("DELETE").build(),
            HttpResponse.builder().statusCode(404).build()
      ).getVolumeTypeExtensionForZone("az-1.region-a.geo-1").get();

      assertFalse(api.deleteExtraSpec("5", "test1"));
   }

   public VolumeType testVolumeType() {
      return VolumeType.builder().id("8").name("jclouds-test-1").created(dateService.iso8601SecondsDateParse("2012-05-10 12:33:06")).extraSpecs(ImmutableMap.of("test", "value1", "test1", "wibble")).build();
   }
}
