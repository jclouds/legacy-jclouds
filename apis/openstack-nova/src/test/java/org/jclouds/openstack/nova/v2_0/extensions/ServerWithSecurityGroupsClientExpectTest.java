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
import static org.testng.Assert.assertNull;

import java.net.URI;

import org.jclouds.openstack.nova.v2_0.domain.ServerWithSecurityGroups;
import org.jclouds.openstack.nova.v2_0.extensions.ServerWithSecurityGroupsClient;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaClientExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests parsing and guice wiring of ServerWithSecurityGroupsClient
 *
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "ServerWithSecurityGroupsClientExpectTest")
public class ServerWithSecurityGroupsClientExpectTest extends BaseNovaClientExpectTest {

   public void testGetServerWithSecurityGroups() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-create-server-ext/8d0a6ca5-8849-4b3d-b86e-f24c92490ebb");
      ServerWithSecurityGroupsClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).build(),
            standardResponseBuilder(200).payload(payloadFromResource("/server_with_security_groups.json")).build()
      ).getServerWithSecurityGroupsExtensionForZone("az-1.region-a.geo-1").get();

      ServerWithSecurityGroups server = client.getServer("8d0a6ca5-8849-4b3d-b86e-f24c92490ebb");
      assertEquals(server.getId(), "8d0a6ca5-8849-4b3d-b86e-f24c92490ebb");
      assertEquals(server.getSecurityGroupNames(), ImmutableSet.of("default", "group1"));
   }

   public void testGetServerWithSecurityGroupsFailNotFound() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-create-server-ext/8d0a6ca5-8849-4b3d-b86e-f24c92490ebb");
      ServerWithSecurityGroupsClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).build(),
            standardResponseBuilder(404).build()
      ).getServerWithSecurityGroupsExtensionForZone("az-1.region-a.geo-1").get();
      assertNull(client.getServer("8d0a6ca5-8849-4b3d-b86e-f24c92490ebb"));
   }
}