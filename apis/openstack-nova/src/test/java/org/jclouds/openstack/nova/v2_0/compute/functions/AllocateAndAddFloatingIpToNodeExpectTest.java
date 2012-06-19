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
package org.jclouds.openstack.nova.v2_0.compute.functions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.compute.functions.AllocateAndAddFloatingIpToNode;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaComputeServiceExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * Tests the compute service abstraction of the nova client.
 * 
 * @author Matt Stephenson
 */
@Test(groups = "unit", testName = "AllocateAndAddFloatingIpToNodeTest")
public class AllocateAndAddFloatingIpToNodeExpectTest extends BaseNovaComputeServiceExpectTest {
   final Location provider = new LocationBuilder().scope(LocationScope.PROVIDER).id("openstack-nova").description(
            "openstack-nova").build();
   final Location zone = new LocationBuilder().id("az-1.region-a.geo-1").description("az-1.region-a.geo-1").scope(
            LocationScope.ZONE).parent(provider).build();
   final Location host = new LocationBuilder().scope(LocationScope.HOST).id("hostId").description("hostId")
            .parent(zone).build();
   final NodeMetadata node = new NodeMetadataBuilder().id("az-1.region-a.geo-1/71592").providerId("71592").location(
            host).name("Server 71592").status(Status.RUNNING).privateAddresses(ImmutableSet.of("10.4.27.237"))
            .credentials(LoginCredentials.builder().password("foo").build()).build();

   HttpRequest allocateFloatingIP = HttpRequest.builder().method("POST").endpoint(
            URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-floating-ips")).headers(
            ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                     authToken).build()).payload(payloadFromStringWithContentType("{}", "application/json")).build();

   HttpResponse addFloatingIPResponse = HttpResponse.builder().statusCode(200).build();

   public void testAllocateWhenAllocationReturnsIpIsAddedToServerAndUpdatesNodeMetadataButSavesCredentials() throws Exception {
      HttpResponse allocateFloatingIPResponse = HttpResponse.builder().statusCode(200).payload(
               payloadFromResource("/floatingip_details.json")).build();

      HttpRequest addFloatingIPRequest = addFloatingIPForAddress("10.0.0.3");

      AllocateAndAddFloatingIpToNode fn = requestsSendResponses(
               ImmutableMap.<HttpRequest, HttpResponse> builder().put(keystoneAuthWithUsernameAndPasswordAndTenantName,
                        responseWithKeystoneAccess).put(extensionsOfNovaRequest, extensionsOfNovaResponse).put(
                        allocateFloatingIP, allocateFloatingIPResponse)
                        .put(addFloatingIPRequest, addFloatingIPResponse).build()).getContext().utils().injector()
               .getInstance(AllocateAndAddFloatingIpToNode.class);

      AtomicReference<NodeMetadata> nodeRef = new AtomicReference<NodeMetadata>(node);
      fn.apply(nodeRef);
      NodeMetadata node1 = nodeRef.get();
      assertNotNull(node1);
      assertEquals(node1.getPublicAddresses(), ImmutableSet.of("10.0.0.3"));
      assertEquals(node1.getCredentials(), node.getCredentials());

   }

   private HttpRequest addFloatingIPForAddress(String address) {
      HttpRequest addFloatingIPRequest = HttpRequest.builder().method("POST").endpoint(
               URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/71592/action"))
               .headers(
                        ImmutableMultimap.<String, String> builder().put("Accept", "*/*")
                                 .put("X-Auth-Token", authToken).build()).payload(
                        payloadFromStringWithContentType("{\"addFloatingIp\":{\"address\":\"" + address + "\"}}",
                                 "application/json")).build();
      return addFloatingIPRequest;
   }

   public void testAllocateWhenAllocationFailsLookupUnusedIpAddToServerAndUpdatesNodeMetadata() throws Exception {
      HttpResponse allocateFloatingIPResponse = HttpResponse
               .builder()
               .statusCode(400)
               .payload(
                        payloadFromStringWithContentType(
                                 "{\"badRequest\": {\"message\": \"AddressLimitExceeded: Address quota exceeded. You cannot allocate any more addresses\", \"code\": 400}}",
                                 "application/json")).build();

      HttpRequest listFloatingIPs = HttpRequest.builder().method("GET").endpoint(
               URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-floating-ips")).headers(
               ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                        authToken).build()).build();

      HttpResponse listFloatingIPsResponseForUnassigned = HttpResponse.builder().statusCode(200).payload(
               payloadFromResource("/floatingip_list.json")).build();

      HttpRequest addFloatingIPRequest = addFloatingIPForAddress("10.0.0.5");

      AllocateAndAddFloatingIpToNode fn = requestsSendResponses(
               ImmutableMap.<HttpRequest, HttpResponse> builder().put(keystoneAuthWithUsernameAndPasswordAndTenantName,
                        responseWithKeystoneAccess).put(extensionsOfNovaRequest, extensionsOfNovaResponse).put(
                        allocateFloatingIP, allocateFloatingIPResponse)
                        .put(addFloatingIPRequest, addFloatingIPResponse).put(listFloatingIPs,
                                 listFloatingIPsResponseForUnassigned).build()).getContext().utils().injector()
               .getInstance(AllocateAndAddFloatingIpToNode.class);

      AtomicReference<NodeMetadata> nodeRef = new AtomicReference<NodeMetadata>(node);
      fn.apply(nodeRef);
      NodeMetadata node1 = nodeRef.get();
      assertNotNull(node1);
      assertEquals(node1.getPublicAddresses(), ImmutableSet.of("10.0.0.5"));

   }
}
