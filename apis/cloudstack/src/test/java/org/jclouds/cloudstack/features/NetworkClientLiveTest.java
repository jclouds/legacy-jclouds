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
package org.jclouds.cloudstack.features;

import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.cloudstack.options.CreateNetworkOptions.Builder.vlan;
import static org.jclouds.cloudstack.options.ListNetworkOfferingsOptions.Builder.specifyVLAN;
import static org.jclouds.cloudstack.options.ListNetworksOptions.Builder.accountInDomain;
import static org.jclouds.cloudstack.options.ListNetworksOptions.Builder.id;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jclouds.cloudstack.domain.GuestIPType;
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.domain.NetworkOffering;
import org.jclouds.cloudstack.domain.Zone;
import org.jclouds.cloudstack.internal.BaseCloudStackClientLiveTest;
import org.jclouds.cloudstack.predicates.NetworkOfferingPredicates;
import org.jclouds.cloudstack.predicates.ZonePredicates;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code NetworkClientLiveTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "NetworkClientLiveTest")
public class NetworkClientLiveTest extends BaseCloudStackClientLiveTest {

   private boolean networksSupported;
   private Zone zone;

   @BeforeGroups(groups = "live")
   public void setupContext() {
      super.setupContext();

      try {
         zone = find(client.getZoneClient().listZones(), ZonePredicates.supportsAdvancedNetworks());
         networksSupported = true;
      } catch (NoSuchElementException e) {
      }
   }

   @Test
   public void testCreateGuestVirtualNetwork() {
      if (!networksSupported)
         return;
      final NetworkOffering offering;
      try {
         offering = find(client.getOfferingClient().listNetworkOfferings(),
               NetworkOfferingPredicates.supportsGuestVirtualNetworks());

      } catch (NoSuchElementException e) {
         Logger.getAnonymousLogger().log(Level.SEVERE, "guest networks not supported, skipping test");
         return;
      }
      String name = prefix + "-virtual";

      Network network = null;
      try {
         network = client.getNetworkClient().createNetworkInZone(zone.getId(), offering.getId(), name, name);
         checkNetwork(network);
      } catch (IllegalStateException e) {
         Logger.getAnonymousLogger().log(Level.SEVERE, "couldn't create a network, skipping test", e);
      } finally {
         if (network != null) {
            String jobId = client.getNetworkClient().deleteNetwork(network.getId());
            if (jobId != null)
               jobComplete.apply(jobId);
         }
      }
   }

   @Test
   public void testCreateVLANNetwork() {
      skipIfNotDomainAdmin();
      if (!networksSupported)
         return;

      final NetworkOffering offering;
      try {
         offering = get(
               cloudStackContext.getApi().getOfferingClient().listNetworkOfferings(specifyVLAN(true).zoneId(zone.getId())), 0);
      } catch (NoSuchElementException e) {
         Logger.getAnonymousLogger().log(Level.SEVERE, "VLAN networks not supported, skipping test");
         return;
      }
      String name = prefix + "-vlan";

      Network network = null;
      try {
         network = domainAdminClient
               .getNetworkClient()
               // startIP/endIP/netmask/gateway must be specified together
               .createNetworkInZone(zone.getId(), offering.getId(), name, name,
                     vlan("65").startIP("192.168.1.2").netmask("255.255.255.0").gateway("192.168.1.1"));
         checkNetwork(network);
      } catch (IllegalStateException e) {
         Logger.getAnonymousLogger().log(Level.SEVERE, "couldn't create a network, skipping test", e);
      } finally {
         if (network != null) {
            String jobId = adminClient.getNetworkClient().deleteNetwork(network.getId());
            if (jobId != null)
               adminJobComplete.apply(jobId);
         }
      }
   }

   @Test
   public void testListNetworks() throws Exception {
      if (!networksSupported)
         return;
      Set<Network> response = client.getNetworkClient().listNetworks(
            accountInDomain(user.getAccount(), user.getDomainId()));
      assert null != response;
      long networkCount = response.size();
      assertTrue(networkCount >= 0);
      for (Network network : response) {
         Network newDetails = getOnlyElement(client.getNetworkClient().listNetworks(id(network.getId())));
         assertEquals(network, newDetails);
         assertEquals(network, client.getNetworkClient().getNetwork(network.getId()));
         checkNetwork(network);
      }
   }

   private void checkNetwork(Network network) {
      assert network.getId() != null : network;
      assert network.getName() != null : network;
      assert network.getDNS().size() != 0 : network;
      assert network.getGuestIPType() != null && network.getGuestIPType() != GuestIPType.UNRECOGNIZED : network;
      assert network.getBroadcastDomainType() != null : network;
      assert network.getDisplayText() != null : network;
      // Network domain can be null sometimes
      // assert network.getNetworkDomain() != null : network;
      assert network.getNetworkOfferingAvailability() != null : network;
      assert network.getNetworkOfferingDisplayText() != null : network;
      assert network.getNetworkOfferingId() != null : network;
      assert network.getNetworkOfferingName() != null : network;
      assert network.getRelated() != null : network;
      assert network.getServices().size() != 0 : network;
      assert network.getState() != null : network;
      assert network.getTrafficType() != null : network;
      assert network.getZoneId() != null : network;
      assert network.getDomain() != null : network;
      switch (network.getGuestIPType()) {
      case VIRTUAL:
         assert network.getNetmask() == null : network;
         assert network.getGateway() == null : network;
         assert network.getVLAN() == null : network;
         assert network.getStartIP() == null : network;
         assert network.getEndIP() == null : network;
         break;
      case DIRECT:
         // TODO: I've found a network that doesn't have a netmask associated
         assert network.getNetmask() != null : network;
         assert network.getGateway() != null : network;
         assert network.getVLAN() != null : network;
         assertEquals(network.getBroadcastURI(), URI.create("vlan://" + network.getVLAN()));
         assert network.getStartIP() != null : network;
         assert network.getEndIP() != null : network;
         break;
      }
   }

}
