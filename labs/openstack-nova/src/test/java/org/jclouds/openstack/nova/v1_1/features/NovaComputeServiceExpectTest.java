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
package org.jclouds.openstack.nova.v1_1.features;

import com.google.common.collect.ImmutableMultimap;
import org.jclouds.compute.ComputeService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.domain.Resource;
import org.jclouds.openstack.nova.v1_1.domain.Server;
import org.jclouds.openstack.nova.v1_1.internal.BaseNovaComputeServiceExpectTest;
import org.jclouds.openstack.nova.v1_1.parse.ParseServerListTest;
import org.testng.annotations.Test;
import java.net.URI;
import java.util.Set;

import static org.testng.Assert.*;

/**
 * Tests the compute service abstraction of the nova client.
 *
 * @author Matt Stephenson
 */
@Test(groups = "unit", testName = "NovaComputeServiceExpectTest")
public class NovaComputeServiceExpectTest extends BaseNovaComputeServiceExpectTest
{

   public void testListServersWhenResponseIs2xx() throws Exception
   {
      HttpRequest listServers = HttpRequest.builder().method("GET").endpoint(
         URI.create("https://compute.north.host/v1.1/3456/servers/detail")).headers(
         ImmutableMultimap.<String, String>builder().put("Accept", "application/json").put("X-Auth-Token",
            authToken).build()).build();

      HttpResponse listServersResponse = HttpResponse.builder().statusCode(200).payload(
         payloadFromResource("/server_list_details.json")).build();

      ComputeService clientWhenServersExist = requestsSendResponses(keystoneAuthWithAccessKeyAndSecretKey,
         responseWithKeystoneAccess, listServers, listServersResponse);


      assertNotNull(clientWhenServersExist.listAssignableLocations());
      assertEquals(clientWhenServersExist.listAssignableLocations().size(), 1);
      assertEquals(clientWhenServersExist.listAssignableLocations().iterator().next().getId(), "North");

      assertNotNull(clientWhenServersExist.listNodes());
      assertEquals(clientWhenServersExist.listNodes().size(), 1);
      assertEquals(clientWhenServersExist.listNodes().iterator().next().getId(), "52415800-8b69-11e0-9b19-734f000004d2");
      assertEquals(clientWhenServersExist.listNodes().iterator().next().getName(), "sample-server");
   }

   public void testListServersWhenReponseIs404IsEmpty() throws Exception
   {
      HttpRequest listServers = HttpRequest.builder().method("GET").endpoint(
         URI.create("https://compute.north.host/v1.1/3456/servers/detail")).headers(
         ImmutableMultimap.<String, String>builder().put("Accept", "application/json").put("X-Auth-Token",
            authToken).build()).build();

      HttpResponse listServersResponse = HttpResponse.builder().statusCode(404).build();

      ComputeService clientWhenNoServersExist = requestsSendResponses(keystoneAuthWithAccessKeyAndSecretKey,
         responseWithKeystoneAccess, listServers, listServersResponse);

      assertTrue(clientWhenNoServersExist.listNodes().isEmpty());
   }
}
