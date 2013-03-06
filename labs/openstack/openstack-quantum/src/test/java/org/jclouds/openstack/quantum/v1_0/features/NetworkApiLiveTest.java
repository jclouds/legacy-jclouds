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
import org.jclouds.openstack.quantum.v1_0.internal.BaseQuantumApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests NetworkApi
 *
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "NetworkApiLiveTest", singleThreaded = true)
public class NetworkApiLiveTest extends BaseQuantumApiLiveTest {

   public void testListNetworks() {
      for (String zoneId : quantumContext.getApi().getConfiguredZones()) {
         Set<? extends Reference> ids = quantumContext.getApi().getNetworkApiForZone(zoneId).listReferences().toSet();
         Set<? extends Network> networks = quantumContext.getApi().getNetworkApiForZone(zoneId).list().toSet();
         assertNotNull(ids);
         assertEquals(ids.size(), networks.size());
         for (Network network : networks) {
            assertNotNull(network.getName());
            assertTrue(ids.contains(Reference.builder().id(network.getId()).build()));
         }
      }
   }

   public void testCreateUpdateAndDeleteNetwork() {
      for (String zoneId : quantumContext.getApi().getConfiguredZones()) {
         NetworkApi api = quantumContext.getApi().getNetworkApiForZone(zoneId);
         Reference net = api.create("jclouds-test");
         assertNotNull(net);

         Network network = api.get(net.getId());
         NetworkDetails details = api.getDetails(net.getId());
         
         for(Network checkme : ImmutableList.of(network, details)) {
            assertEquals(checkme.getId(), net.getId());
            assertEquals(checkme.getName(), "jclouds-test");
         }
         
         assertTrue(details.getPorts().isEmpty());

         assertTrue(api.rename(net.getId(), "jclouds-live-test"));
         
         // Grab the updated metadata
         network = api.get(net.getId());
         details = api.getDetails(net.getId());

         for(Network checkme : ImmutableList.of(network, details)) {
            assertEquals(checkme.getId(), net.getId());
            assertEquals(checkme.getName(), "jclouds-live-test");
         }

         assertTrue(details.getPorts().isEmpty());

         Reference net2 = api.create("jclouds-test2");
         assertNotNull(net2);
        
         assertTrue(api.delete(net.getId()));
         assertTrue(api.delete(net2.getId()));
      }
   }   
}
