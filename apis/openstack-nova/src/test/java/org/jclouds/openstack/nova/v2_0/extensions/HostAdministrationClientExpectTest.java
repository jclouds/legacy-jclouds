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
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.domain.Host;
import org.jclouds.openstack.nova.v2_0.domain.HostResourceUsage;
import org.jclouds.openstack.nova.v2_0.extensions.HostAdministrationClient;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaClientExpectTest;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Tests HostAdministrationClient guice wiring and parsing (including the Response parsers in FieldValueResponseParsers)
 * 
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "HostAdministrationClientExpectTest")
public class HostAdministrationClientExpectTest extends BaseNovaClientExpectTest {
   
   
   public void testList() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-hosts");
      HostAdministrationClient client = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            HttpRequest.builder().method("GET").headers(ImmutableMultimap.of("Accept", MediaType.APPLICATION_JSON, "X-Auth-Token", authToken))
                  .endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/hosts_list.json")).build()).getHostAdministrationExtensionForZone("az-1.region-a.geo-1").get();
      
      Host expected = Host.builder().name("ubuntu").service("compute").build();

      Set<Host> result = client.listHosts();
      Host host = Iterables.getOnlyElement(result);
      assertEquals(host.getName(), "ubuntu");
      assertEquals(host.getService(), "compute");

      assertEquals(host, expected);
   }

   public void testGet() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-hosts/xyz");
      HostAdministrationClient client = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            HttpRequest.builder().method("GET").headers(ImmutableMultimap.of("Accept", MediaType.APPLICATION_JSON, "X-Auth-Token", authToken))
                  .endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/host.json")).build()).getHostAdministrationExtensionForZone("az-1.region-a.geo-1").get();

      Set<HostResourceUsage> expected = ImmutableSet.of(
            HostResourceUsage.builder().memoryMb(16083).project("(total)").cpu(4).diskGb(181).host("ubuntu").build(),
            HostResourceUsage.builder().memoryMb(3396).project("(used_now)").cpu(3).diskGb(5).host("ubuntu").build(),
            HostResourceUsage.builder().memoryMb(6144).project("(used_max)").cpu(3).diskGb(80).host("ubuntu").build(),
            HostResourceUsage.builder().memoryMb(6144).project("f8535069c3fb404cb61c873b1a0b4921").cpu(3).diskGb(80).host("ubuntu").build()
      );

      assertEquals(client.getHostResourceUsage("xyz"), expected);
   }
   
   public void testEnableHost() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-hosts/ubuntu");
      HostAdministrationClient client = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            HttpRequest.builder().method("PUT").headers(ImmutableMultimap.of("Accept", MediaType.APPLICATION_JSON, "X-Auth-Token", authToken))
                  .payload(payloadFromStringWithContentType("{\"status\":\"enable\"}", MediaType.APPLICATION_JSON))
                  .endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromStringWithContentType("{\"host\":\"ubuntu\",\"status\":\"enabled\"}", MediaType.APPLICATION_JSON))
                  .build()).getHostAdministrationExtensionForZone("az-1.region-a.geo-1").get();
      assertTrue(client.enableHost("ubuntu"));
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testEnableHostFailNotFound() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-hosts/ubuntu");
      HostAdministrationClient client = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            HttpRequest.builder().method("PUT").headers(ImmutableMultimap.of("Accept", MediaType.APPLICATION_JSON, "X-Auth-Token", authToken))
                  .payload(payloadFromStringWithContentType("{\"status\":\"enable\"}", MediaType.APPLICATION_JSON))
                  .endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404)
                  .build()).getHostAdministrationExtensionForZone("az-1.region-a.geo-1").get();
      client.enableHost("ubuntu");
   }

   public void testEnableHostFailNotEnabled() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-hosts/ubuntu");
      HostAdministrationClient client = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            HttpRequest.builder().method("PUT").headers(ImmutableMultimap.of("Accept", MediaType.APPLICATION_JSON, "X-Auth-Token", authToken))
                  .payload(payloadFromStringWithContentType("{\"status\":\"enable\"}", MediaType.APPLICATION_JSON))
                  .endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromStringWithContentType("{\"host\":\"ubuntu\",\"status\":\"disabled\"}", MediaType.APPLICATION_JSON))
                  .build()).getHostAdministrationExtensionForZone("az-1.region-a.geo-1").get();
      assertFalse(client.enableHost("ubuntu"));
   }

   public void testDisableHost() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-hosts/ubuntu");
      HostAdministrationClient client = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            HttpRequest.builder().method("PUT").headers(ImmutableMultimap.of("Accept", MediaType.APPLICATION_JSON, "X-Auth-Token", authToken))
                  .payload(payloadFromStringWithContentType("{\"status\":\"disable\"}", MediaType.APPLICATION_JSON))
                  .endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromStringWithContentType("{\"host\":\"ubuntu\",\"status\":\"disabled\"}", MediaType.APPLICATION_JSON))
                  .build()).getHostAdministrationExtensionForZone("az-1.region-a.geo-1").get();
      assertTrue(client.disableHost("ubuntu"));
   }

   public void testStartMaintenance() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-hosts/ubuntu");
      HostAdministrationClient client = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            HttpRequest.builder().method("PUT").headers(ImmutableMultimap.of("Accept", MediaType.APPLICATION_JSON, "X-Auth-Token", authToken))
                  .payload(payloadFromStringWithContentType("{\"maintenance_mode\":\"enable\"}", MediaType.APPLICATION_JSON))
                  .endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromStringWithContentType("{\"host\":\"ubuntu\",\"maintenance_mode\":\"on_maintenance\"}", MediaType.APPLICATION_JSON))
                  .build()).getHostAdministrationExtensionForZone("az-1.region-a.geo-1").get();
      assertTrue(client.startHostMaintenance("ubuntu"));
   }

   public void testStopMaintenance() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-hosts/ubuntu");
      HostAdministrationClient client = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            HttpRequest.builder().method("PUT").headers(ImmutableMultimap.of("Accept", MediaType.APPLICATION_JSON, "X-Auth-Token", authToken))
                  .payload(payloadFromStringWithContentType("{\"maintenance_mode\":\"disable\"}", MediaType.APPLICATION_JSON))
                  .endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromStringWithContentType("{\"host\":\"ubuntu\",\"maintenance_mode\":\"off_maintenance\"}", MediaType.APPLICATION_JSON))
                  .build()).getHostAdministrationExtensionForZone("az-1.region-a.geo-1").get();
      assertTrue(client.stopHostMaintenance("ubuntu"));
   }
   
   public void testStartupHost() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-hosts/ubuntu/startup");
      HostAdministrationClient client = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            HttpRequest.builder().method("GET").headers(ImmutableMultimap.of("Accept", MediaType.APPLICATION_JSON, "X-Auth-Token", authToken))
                  .endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromStringWithContentType("{\"host\":\"ubuntu\",\"power_action\":\"startup\"}", MediaType.APPLICATION_JSON))
                  .build()).getHostAdministrationExtensionForZone("az-1.region-a.geo-1").get();
      assertTrue(client.startupHost("ubuntu"));
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testStartupHostFailNotFound() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-hosts/ubuntu/startup");
      HostAdministrationClient client = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            HttpRequest.builder().method("GET").headers(ImmutableMultimap.of("Accept", MediaType.APPLICATION_JSON, "X-Auth-Token", authToken))
                  .endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).build()).getHostAdministrationExtensionForZone("az-1.region-a.geo-1").get();
      assertTrue(client.startupHost("ubuntu"));
   }

   public void testStartupHostFailWrongActionInProgress() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-hosts/ubuntu/startup");
      HostAdministrationClient client = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            HttpRequest.builder().method("GET").headers(ImmutableMultimap.of("Accept", MediaType.APPLICATION_JSON, "X-Auth-Token", authToken))
                  .endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromStringWithContentType("{\"host\":\"ubuntu\",\"power_action\":\"shutdown\"}", MediaType.APPLICATION_JSON))
                  .build()).getHostAdministrationExtensionForZone("az-1.region-a.geo-1").get();
      assertFalse(client.startupHost("ubuntu"));
   }
   
   public void testShutdownHost() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-hosts/ubuntu/shutdown");
      HostAdministrationClient client = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            HttpRequest.builder().method("GET").headers(ImmutableMultimap.of("Accept", MediaType.APPLICATION_JSON, "X-Auth-Token", authToken))
                  .endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromStringWithContentType("{\"host\":\"ubuntu\",\"power_action\":\"shutdown\"}", MediaType.APPLICATION_JSON))
                  .build()).getHostAdministrationExtensionForZone("az-1.region-a.geo-1").get();
      assertTrue(client.shutdownHost("ubuntu"));
   }
   
   public void testRebootHost() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-hosts/ubuntu/reboot");
      HostAdministrationClient client = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            HttpRequest.builder().method("GET").headers(ImmutableMultimap.of("Accept", MediaType.APPLICATION_JSON, "X-Auth-Token", authToken))
                  .endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromStringWithContentType("{\"host\":\"ubuntu\",\"power_action\":\"reboot\"}", MediaType.APPLICATION_JSON))
                  .build()).getHostAdministrationExtensionForZone("az-1.region-a.geo-1").get();
      assertTrue(client.rebootHost("ubuntu"));
   }
}
