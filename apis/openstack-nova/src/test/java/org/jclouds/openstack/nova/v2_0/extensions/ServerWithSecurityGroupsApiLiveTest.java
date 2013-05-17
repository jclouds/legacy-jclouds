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
import static org.testng.Assert.assertNotNull;

import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerWithSecurityGroups;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiLiveTest;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of ServerWithSecurityGroupsApi
 *
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "ServerWithSecurityGroupsApiLiveTest", singleThreaded = true)
public class ServerWithSecurityGroupsApiLiveTest extends BaseNovaApiLiveTest {
   private ServerApi serverApi;
   private Optional<? extends ServerWithSecurityGroupsApi> apiOption;
   private String zone;

   @BeforeGroups(groups = {"integration", "live"})
   @Override
   public void setup() {
      super.setup();
      zone = Iterables.getLast(api.getConfiguredZones(), "nova");
      serverApi = api.getServerApiForZone(zone);
      apiOption = api.getServerWithSecurityGroupsExtensionForZone(zone);
   }

   public void testGetServer() {
      if (apiOption.isPresent()) {

         for (Resource server : serverApi.list().concat()) {
            ServerWithSecurityGroups serverWithGroups = apiOption.get().get(server.getId());
            assertEquals(serverWithGroups.getId(), server.getId());
            assertEquals(serverWithGroups.getName(), server.getName());
            assertNotNull(serverWithGroups.getSecurityGroupNames());
         }

         // Create a new server to verify the groups work as expected
         Server testServer = null;
         try {
            testServer = createServerInZone(zone);
            
            ServerWithSecurityGroups results = apiOption.get().get(testServer.getId());
            assertEquals(results.getId(), testServer.getId());
            assertEquals(results.getSecurityGroupNames(), ImmutableSet.of("default"));
         } finally {
            if (testServer != null) {
               serverApi.delete(testServer.getId());
            }
         }
      }
   }

}
