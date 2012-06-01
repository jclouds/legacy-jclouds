/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 1.1 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-1.1
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.openstack.quantum.v1_0.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.openstack.quantum.v1_0.domain.Network;
import org.jclouds.openstack.quantum.v1_0.domain.NetworkDetails;
import org.jclouds.openstack.quantum.v1_0.domain.Reference;
import org.jclouds.openstack.quantum.v1_0.internal.BaseQuantumClientLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests NetworkClient
 *
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "NetworkClientLiveTest", singleThreaded = true)
public class NetworkClientLiveTest extends BaseQuantumClientLiveTest {

   public void testListNetworks() {
      for (String regionId : quantumContext.getApi().getConfiguredRegions()) {
         Set<Reference> ids = quantumContext.getApi().getNetworkClientForRegion(regionId).listReferences();
         Set<Network> networks = quantumContext.getApi().getNetworkClientForRegion(regionId).list();
         assertNotNull(ids);
         assertEquals(ids.size(), networks.size());
         for (Network network : networks) {
            assertNotNull(network.getName());
            assertTrue(ids.contains(Reference.builder().id(network.getId()).build()));
         }
      }
   }

   public void testCreateUpdateAndDeleteNetwork() {
      for (String regionId : quantumContext.getApi().getConfiguredRegions()) {
         NetworkClient client = quantumContext.getApi().getNetworkClientForRegion(regionId);
         Reference net = client.create("jclouds-test");
         assertNotNull(net);

         Network network = client.show(net.getId());
         NetworkDetails details = client.showDetails(net.getId());
         
         for(Network checkme : ImmutableList.of(network, details)) {
            assertEquals(checkme.getId(), net.getId());
            assertEquals(checkme.getName(), "jclouds-test");
         }
         
         assertTrue(details.getPorts().isEmpty());

         assertTrue(client.update(net.getId(), "jclouds-live-test"));
         
         // Grab the updated metadata
         network = client.show(net.getId());
         details = client.showDetails(net.getId());

         for(Network checkme : ImmutableList.of(network, details)) {
            assertEquals(checkme.getId(), net.getId());
            assertEquals(checkme.getName(), "jclouds-live-test");
         }

         assertTrue(details.getPorts().isEmpty());

         Reference net2 = client.create("jclouds-test2");
         assertNotNull(net2);
        
         assertTrue(client.delete(net.getId()));
         assertTrue(client.delete(net2.getId()));
      }
   }   
}