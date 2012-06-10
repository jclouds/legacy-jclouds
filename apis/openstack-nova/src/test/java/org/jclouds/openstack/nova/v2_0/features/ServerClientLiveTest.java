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
package org.jclouds.openstack.nova.v2_0.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.features.ServerClient;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaClientLiveTest;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code ServerClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "ServerClientLiveTest")
public class ServerClientLiveTest extends BaseNovaClientLiveTest {

   @Test
   public void testListServersInDetail() throws Exception {
      for (String zoneId : novaContext.getApi().getConfiguredZones()) {
         ServerClient client = novaContext.getApi().getServerClientForZone(zoneId);
         Set<Resource> response = client.listServers();
         assert null != response;
         assertTrue(response.size() >= 0);
         for (Resource server : response) {
            Server newDetails = client.getServer(server.getId());
            assertEquals(newDetails.getId(), server.getId());
            assertEquals(newDetails.getName(), server.getName());
            assertEquals(newDetails.getLinks(), server.getLinks());
            checkServer(newDetails);
         }
      }
   }

   private void checkServer(Server server) {
      assert server.getAddresses().size() > 0 : server;
   }
}
