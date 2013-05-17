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
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.domain.Quota;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiExpectTest;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

/**
 * Tests HostAdministrationApi guice wiring and parsing
 *
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "QuotaApiExpectTest")
public class QuotaApiExpectTest extends BaseNovaApiExpectTest {

   public void testGetQuotas() throws Exception {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-quota-sets/demo");
      QuotaApi api = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/quotas.json")).build()).getQuotaExtensionForZone("az-1.region-a.geo-1").get();

      assertEquals(api.getByTenant("demo"), getTestQuotas());
   }

   public void testGetQuotasFailsTenantNotFound() throws Exception {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-quota-sets/demo");
      QuotaApi api = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).build()).getQuotaExtensionForZone("az-1.region-a.geo-1").get();
      assertNull(api.getByTenant("demo"));
   }

   public void testGetDefaultQuotas() throws Exception {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-quota-sets/demo/defaults");
      QuotaApi api = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/quotas.json")).build()).getQuotaExtensionForZone("az-1.region-a.geo-1").get();

      assertEquals(api.getDefaultsForTenant("demo"), getTestQuotas());
   }

   public void testGetDefaultQuotasFailsTenantNotFound() throws Exception {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-quota-sets/demo/defaults");
      QuotaApi api = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).build()).getQuotaExtensionForZone("az-1.region-a.geo-1").get();
      assertNull(api.getDefaultsForTenant("demo"));
   }


   public void testUpdateQuotas() throws Exception {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-quota-sets/demo");
      QuotaApi api = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            HttpRequest.builder().endpoint(endpoint).method("PUT")
                  .addHeader("X-Auth-Token", authToken)
                  .payload(payloadFromResourceWithContentType("/quotas.json", MediaType.APPLICATION_JSON))
                  .build(),
            HttpResponse.builder().statusCode(200).build()).getQuotaExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(api.updateQuotaOfTenant(getTestQuotas(), "demo"));
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testUpdateQuotasFailsNotFound() throws Exception {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-quota-sets/demo");
      QuotaApi api = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            HttpRequest.builder().endpoint(endpoint).method("PUT")
                  .addHeader("X-Auth-Token", authToken)
                  .payload(payloadFromResourceWithContentType("/quotas.json", MediaType.APPLICATION_JSON))
                  .build(),
            HttpResponse.builder().statusCode(404).build()).getQuotaExtensionForZone("az-1.region-a.geo-1").get();

      api.updateQuotaOfTenant(getTestQuotas(), "demo");
   }

   public static Quota getTestQuotas() {
      return Quota.builder()
            .metadataItems(128)
            .injectedFileContentBytes(10240)
            .injectedFiles(5)
            .gigabytes(1000)
            .ram(51200)
            .floatingIps(10)
            .securityGroups(10)
            .securityGroupRules(20)
            .instances(10)
            .keyPairs(100)
            .volumes(10)
            .cores(20)
            .id("demo").build();
   }

}
