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

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiExpectTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseFloatingIPListTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseFloatingIPTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests annotation parsing of {@code FloatingIPAsyncApi}
 * 
 * @author Michael Arnold
 */
@Test(groups = "unit", testName = "FloatingIPApiExpectTest")
public class FloatingIPApiExpectTest extends BaseNovaApiExpectTest {
   public void testWhenNamespaceInExtensionsListFloatingIpPresent() throws Exception {

      NovaApi apiWhenExtensionNotInList = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse);

      assertEquals(apiWhenExtensionNotInList.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1", "az-2.region-a.geo-1", "az-3.region-a.geo-1"));

      assertTrue(apiWhenExtensionNotInList.getFloatingIPExtensionForZone("az-1.region-a.geo-1").isPresent());

   }

   public void testWhenNamespaceNotInExtensionsListFloatingIpNotPresent() throws Exception {

      NovaApi apiWhenExtensionNotInList = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, unmatchedExtensionsOfNovaResponse);

      assertEquals(apiWhenExtensionNotInList.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1", "az-2.region-a.geo-1", "az-3.region-a.geo-1"));

      assertFalse(apiWhenExtensionNotInList.getFloatingIPExtensionForZone("az-1.region-a.geo-1").isPresent());

   }

   public void testListFloatingIPsWhenResponseIs2xx() throws Exception {
      HttpRequest list = HttpRequest
            .builder()
            .method("GET")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-floating-ips")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/floatingip_list.json")).build();

      NovaApi apiWhenFloatingIPsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, list, listResponse);

      assertEquals(apiWhenFloatingIPsExist.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1", "az-2.region-a.geo-1", "az-3.region-a.geo-1"));

      assertEquals(apiWhenFloatingIPsExist.getFloatingIPExtensionForZone("az-1.region-a.geo-1").get().list()
            .toString(), new ParseFloatingIPListTest().expected().toString());
   }

   public void testListFloatingIPsWhenResponseIs404() throws Exception {
      HttpRequest list = HttpRequest
            .builder()
            .method("GET")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-floating-ips")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse listResponse = HttpResponse.builder().statusCode(404).build();

      NovaApi apiWhenNoServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, list, listResponse);

      assertTrue(apiWhenNoServersExist.getFloatingIPExtensionForZone("az-1.region-a.geo-1").get().list().isEmpty());
   }

   public void testGetFloatingIPWhenResponseIs2xx() throws Exception {
      HttpRequest get = HttpRequest
            .builder()
            .method("GET")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-floating-ips/1")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse getResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/floatingip_details.json")).build();

      NovaApi apiWhenFloatingIPsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, get, getResponse);

      assertEquals(apiWhenFloatingIPsExist.getFloatingIPExtensionForZone("az-1.region-a.geo-1").get().get("1")
            .toString(), new ParseFloatingIPTest().expected().toString());
   }

   public void testGetFloatingIPWhenResponseIs404() throws Exception {
      HttpRequest get = HttpRequest
            .builder()
            .method("GET")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-floating-ips/1")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse getResponse = HttpResponse.builder().statusCode(404).build();

      NovaApi apiWhenNoServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, get, getResponse);

      assertNull(apiWhenNoServersExist.getFloatingIPExtensionForZone("az-1.region-a.geo-1").get().get("1"));
   }

   public void testAllocateWhenResponseIs2xx() throws Exception {
      HttpRequest createFloatingIP = HttpRequest
            .builder()
            .method("POST")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-floating-ips")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken)
            .payload(payloadFromStringWithContentType("{}", "application/json")).build();

      HttpResponse createFloatingIPResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/floatingip_details.json")).build();

      NovaApi apiWhenFloatingIPsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, createFloatingIP,
            createFloatingIPResponse);

      assertEquals(apiWhenFloatingIPsExist.getFloatingIPExtensionForZone("az-1.region-a.geo-1").get().create().toString(),
            new ParseFloatingIPTest().expected().toString());

   }

   public void testAllocateWhenResponseIs404() throws Exception {
      HttpRequest createFloatingIP = HttpRequest
            .builder()
            .method("POST")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-floating-ips")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken)
            .payload(payloadFromStringWithContentType("{}", "application/json")).build();

      HttpResponse createFloatingIPResponse = HttpResponse.builder().statusCode(404).build();

      NovaApi apiWhenNoServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, createFloatingIP,
            createFloatingIPResponse);

      assertNull(apiWhenNoServersExist.getFloatingIPExtensionForZone("az-1.region-a.geo-1").get().create());
   }

   public void testAllocateWithPoolNameWhenResponseIs2xx() throws Exception {
      HttpRequest createFloatingIP = HttpRequest
            .builder()
            .method("POST")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-floating-ips")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken)
            .payload(payloadFromStringWithContentType("{\"pool\":\"myPool\"}", "application/json")).build();

      HttpResponse createFloatingIPResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/floatingip_details.json")).build();

      NovaApi apiWhenFloatingIPsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, createFloatingIP,
            createFloatingIPResponse);

      assertEquals(apiWhenFloatingIPsExist.getFloatingIPExtensionForZone("az-1.region-a.geo-1").get().allocateFromPool("myPool").toString(),
            new ParseFloatingIPTest().expected().toString());
   }
}
