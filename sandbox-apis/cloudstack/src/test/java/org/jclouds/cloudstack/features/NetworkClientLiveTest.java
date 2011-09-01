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
package org.jclouds.cloudstack.features;

import static com.google.common.collect.Iterables.find;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.NoSuchElementException;
import java.util.Set;

import org.jclouds.cloudstack.domain.GuestIPType;
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.domain.NetworkOffering;
import org.jclouds.cloudstack.domain.Zone;
import org.jclouds.cloudstack.options.ListNetworksOptions;
import org.jclouds.cloudstack.predicates.NetworkOfferingPredicates;
import org.jclouds.cloudstack.predicates.ZonePredicates;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code NetworkClientLiveTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "NetworkClientLiveTest")
public class NetworkClientLiveTest extends BaseCloudStackClientLiveTest {

   private boolean networksSupported;

   private Zone zone;
   private NetworkOffering offering;

   private Network network;

   @BeforeGroups(groups = "live")
   public void setupClient() {
      super.setupClient();

      try {
         // you can create guest direct network by Admin user, but since we are
         // not admin, let's try to create a guest virtual one
         zone = find(client.getZoneClient().listZones(), ZonePredicates.supportsGuestVirtualNetworks());
         offering = Iterables.find(client.getOfferingClient().listNetworkOfferings(),
               NetworkOfferingPredicates.supportsGuestVirtualNetworks());
         networksSupported = true;
      } catch (NoSuchElementException e) {
      }
   }

   public void testCreateNetwork() throws Exception {
      if (!networksSupported)
         return;
      network = client.getNetworkClient().createNetworkInZone(zone.getId(), offering.getId(), prefix, prefix);
      checkNetwork(network);
   }

   @AfterGroups(groups = "live")
   protected void tearDown() {
      if (network != null) {
         jobComplete.apply(client.getNetworkClient().deleteNetwork(network.getId()));
      }
      super.tearDown();
   }

   public void testListNetworks() throws Exception {
      if (!networksSupported)
         return;
      Set<Network> response = client.getNetworkClient().listNetworks();
      assert null != response;
      long networkCount = response.size();
      assertTrue(networkCount >= 0);
      for (Network network : response) {
         Network newDetails = Iterables.getOnlyElement(client.getNetworkClient().listNetworks(
               ListNetworksOptions.Builder.id(network.getId())));
         assertEquals(network, newDetails);
         assertEquals(network, client.getNetworkClient().getNetwork(network.getId()));
         checkNetwork(network);

      }
   }

   private void checkNetwork(Network network) {
      assert network.getId() > 0 : network;
      assert network.getName() != null : network;
      assert network.getDNS().size() != 0 : network;
      assert network.getGuestIPType() != null && network.getGuestIPType() != GuestIPType.UNRECOGNIZED : network;
      assert network.getAccount() != null : network;
      assert network.getBroadcastDomainType() != null : network;
      assert network.getDisplayText() != null : network;
      assert network.getNetworkDomain() != null : network;
      assert network.getNetworkOfferingAvailability() != null : network;
      assert network.getNetworkOfferingDisplayText() != null : network;
      assert network.getNetworkOfferingId() > 0 : network;
      assert network.getNetworkOfferingName() != null : network;
      assert network.getRelated() > 0 : network;
      assert network.getServices().size() != 0 : network;
      assert network.getState() != null : network;
      assert network.getTrafficType() != null : network;
      assert network.getZoneId() > 0 : network;
      assert network.getDomain() != null : network;
      assert network.getDomainId() > 0 : network;
      switch (network.getGuestIPType()) {
      case VIRTUAL:
         assert network.getNetmask() == null : network;
         assert network.getGateway() == null : network;
         assert network.getVLAN() == null : network;
         assert network.getStartIP() == null : network;
         assert network.getEndIP() == null : network;
         break;
      case DIRECT:
         assert network.getNetmask() != null : network;
         assert network.getGateway() != null : network;
         assert network.getVLAN() != null : network;
         assertEquals(network.getBroadcastURI(), "vlan://" + network.getVLAN());
         assert network.getStartIP() != null : network;
         assert network.getEndIP() != null : network;
         break;
      }
   }
}
