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

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.NovaClient;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaClientExpectTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseFloatingIPListTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseFloatingIPTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * Tests annotation parsing of {@code FloatingIPAsyncClient}
 * 
 * @author Michael Arnold
 */
@Test(groups = "unit", testName = "FloatingIPClientExpectTest")
public class FloatingIPClientExpectTest extends BaseNovaClientExpectTest {
   public void testWhenNamespaceInExtensionsListFloatingIpPresent() throws Exception {

      NovaClient clientWhenExtensionNotInList = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse);

      assertEquals(clientWhenExtensionNotInList.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1"));

      assertTrue(clientWhenExtensionNotInList.getFloatingIPExtensionForZone("az-1.region-a.geo-1").isPresent());

   }

   public void testWhenNamespaceNotInExtensionsListFloatingIpNotPresent() throws Exception {

      NovaClient clientWhenExtensionNotInList = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, unmatchedExtensionsOfNovaResponse);

      assertEquals(clientWhenExtensionNotInList.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1"));

      assertFalse(clientWhenExtensionNotInList.getFloatingIPExtensionForZone("az-1.region-a.geo-1").isPresent());

   }

   public void testListFloatingIPsWhenResponseIs2xx() throws Exception {
      HttpRequest listFloatingIPs = HttpRequest
            .builder()
            .method("GET")
            .endpoint(URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-floating-ips"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build()).build();

      HttpResponse listFloatingIPsResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/floatingip_list.json")).build();

      NovaClient clientWhenFloatingIPsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, listFloatingIPs, listFloatingIPsResponse);

      assertEquals(clientWhenFloatingIPsExist.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1"));

      assertEquals(clientWhenFloatingIPsExist.getFloatingIPExtensionForZone("az-1.region-a.geo-1").get().listFloatingIPs()
            .toString(), new ParseFloatingIPListTest().expected().toString());
   }

   public void testListFloatingIPsWhenResponseIs404() throws Exception {
      HttpRequest listFloatingIPs = HttpRequest
            .builder()
            .method("GET")
            .endpoint(URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-floating-ips"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build()).build();

      HttpResponse listFloatingIPsResponse = HttpResponse.builder().statusCode(404).build();

      NovaClient clientWhenNoServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, listFloatingIPs, listFloatingIPsResponse);

      assertTrue(clientWhenNoServersExist.getFloatingIPExtensionForZone("az-1.region-a.geo-1").get().listFloatingIPs().isEmpty());
   }

   public void testGetFloatingIPWhenResponseIs2xx() throws Exception {
      HttpRequest getFloatingIP = HttpRequest
            .builder()
            .method("GET")
            .endpoint(URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-floating-ips/1"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build()).build();

      HttpResponse getFloatingIPResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/floatingip_details.json")).build();

      NovaClient clientWhenFloatingIPsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, getFloatingIP, getFloatingIPResponse);

      assertEquals(clientWhenFloatingIPsExist.getFloatingIPExtensionForZone("az-1.region-a.geo-1").get().getFloatingIP("1")
            .toString(), new ParseFloatingIPTest().expected().toString());
   }

   public void testGetFloatingIPWhenResponseIs404() throws Exception {
      HttpRequest getFloatingIP = HttpRequest
            .builder()
            .method("GET")
            .endpoint(URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-floating-ips/1"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build()).build();

      HttpResponse getFloatingIPResponse = HttpResponse.builder().statusCode(404).build();

      NovaClient clientWhenNoServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, getFloatingIP, getFloatingIPResponse);

      assertNull(clientWhenNoServersExist.getFloatingIPExtensionForZone("az-1.region-a.geo-1").get().getFloatingIP("1"));
   }

   public void testAllocateWhenResponseIs2xx() throws Exception {
      HttpRequest allocateFloatingIP = HttpRequest
            .builder()
            .method("POST")
            .endpoint(URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-floating-ips"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build())
            .payload(payloadFromStringWithContentType("{}", "application/json")).build();

      HttpResponse allocateFloatingIPResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/floatingip_details.json")).build();

      NovaClient clientWhenFloatingIPsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, allocateFloatingIP,
            allocateFloatingIPResponse);

      assertEquals(clientWhenFloatingIPsExist.getFloatingIPExtensionForZone("az-1.region-a.geo-1").get().allocate().toString(),
            new ParseFloatingIPTest().expected().toString());

   }

   public void testAllocateWhenResponseIs404() throws Exception {
      HttpRequest allocateFloatingIP = HttpRequest
            .builder()
            .method("POST")
            .endpoint(URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-floating-ips"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build())
            .payload(payloadFromStringWithContentType("{}", "application/json")).build();

      HttpResponse allocateFloatingIPResponse = HttpResponse.builder().statusCode(404).build();

      NovaClient clientWhenNoServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, allocateFloatingIP,
            allocateFloatingIPResponse);

      assertNull(clientWhenNoServersExist.getFloatingIPExtensionForZone("az-1.region-a.geo-1").get().allocate());
   }

}