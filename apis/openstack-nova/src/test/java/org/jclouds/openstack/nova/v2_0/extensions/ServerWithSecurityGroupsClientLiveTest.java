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
import static org.testng.Assert.assertNotNull;

import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerWithSecurityGroups;
import org.jclouds.openstack.nova.v2_0.extensions.ServerWithSecurityGroupsClient;
import org.jclouds.openstack.nova.v2_0.features.ServerClient;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaClientLiveTest;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of ServerWithSecurityGroupsClient
 *
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "ServerWithSecurityGroupsClientLiveTest", singleThreaded = true)
public class ServerWithSecurityGroupsClientLiveTest extends BaseNovaClientLiveTest {
   private ServerClient serverClient;
   private Optional<ServerWithSecurityGroupsClient> clientOption;
   private String zone;

   @BeforeGroups(groups = {"integration", "live"})
   @Override
   public void setupContext() {
      super.setupContext();
      zone = Iterables.getLast(novaContext.getApi().getConfiguredZones(), "nova");
      serverClient = novaContext.getApi().getServerClientForZone(zone);
      clientOption = novaContext.getApi().getServerWithSecurityGroupsExtensionForZone(zone);
   }

   public void testGetServer() {
      if (clientOption.isPresent()) {

         for (Resource server : serverClient.listServers()) {
            ServerWithSecurityGroups serverWithGroups = clientOption.get().getServer(server.getId());
            assertEquals(serverWithGroups.getId(), server.getId());
            assertEquals(serverWithGroups.getName(), server.getName());
            assertNotNull(serverWithGroups.getSecurityGroupNames());
         }

         // Create a new server to verify the groups work as expected
         Server testServer = null;
         try {
            testServer = createServerInZone(zone);
            
            ServerWithSecurityGroups results = clientOption.get().getServer(testServer.getId());
            assertEquals(results.getId(), testServer.getId());
            assertEquals(results.getSecurityGroupNames(), ImmutableSet.of("default"));
         } finally {
            if (testServer != null) {
               serverClient.deleteServer(testServer.getId());
            }
         }
      }
   }

}