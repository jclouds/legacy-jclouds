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
import static org.testng.Assert.assertTrue;

import java.net.URI;

import org.jclouds.openstack.nova.v2_0.domain.VirtualInterface;
import org.jclouds.openstack.nova.v2_0.extensions.VirtualInterfaceClient;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaClientExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests parsing and guice wiring of VirtualInterfaceClient
 *
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "VirtualInterfaceClientLiveTest")
public class VirtualInterfaceClientExpectTest extends BaseNovaClientExpectTest {

   public void testListVirtualInterfaces() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/1/os-virtual-interfaces");
      VirtualInterfaceClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).build(),
            standardResponseBuilder(200).payload(payloadFromResource("/virtual_interfaces_list.json")).build()
      ).getVirtualInterfaceExtensionForZone("az-1.region-a.geo-1").get();

      VirtualInterface vif = Iterables.getOnlyElement(client.listVirtualInterfacesForServer("1"));
      assertEquals(vif.getId(), "02315827-b05c-4668-9c05-75c68838074a");
      assertEquals(vif.getMacAddress(), "fa:16:3e:09:71:34");
   }

   public void testListVirtualInterfacesFailNotFound() {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/1/os-virtual-interfaces");
      VirtualInterfaceClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            standardRequestBuilder(endpoint).build(),
            standardResponseBuilder(404).build()
      ).getVirtualInterfaceExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(client.listVirtualInterfacesForServer("1").isEmpty());
   }
}