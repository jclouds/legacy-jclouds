/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.cloudstack.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.cloudstack.domain.GuestIPType;
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.options.ListNetworksOptions;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code NetworkClientLiveTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "NetworkClientLiveTest")
public class NetworkClientLiveTest extends BaseCloudStackClientLiveTest {

   public void testListNetworks() throws Exception {
      Set<Network> response = client.getNetworkClient().listNetworks();
      assert null != response;
      long networkCount = response.size();
      assertTrue(networkCount >= 0);
      for (Network network : response) {
         Network newDetails = Iterables.getOnlyElement(client.getNetworkClient().listNetworks(
                  ListNetworksOptions.Builder.id(network.getId())));
         assertEquals(network, newDetails);
         assertEquals(network, client.getNetworkClient().getNetwork(network.getId()));
         assert network.getId() != null : network;
         assert network.getName() != null : network;
         assert network.getDNS().size() != 0 : network;
         assert network.getGuestIPType() != null && network.getGuestIPType() != GuestIPType.UNRECOGNIZED : network;
         assert network.getAccount() != null : network;
         assert network.getBroadcastDomainType() != null : network;
//         TODO understand when this is null and for what reason
//         assert network.getBroadcastURI() != null : network;
         assert network.getDisplayText() != null : network;
         assert network.getNetworkDomain() != null : network;
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
         assert network.getDomainId() != null : network;
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
               assert network.getStartIP() != null : network;
               assert network.getEndIP() != null : network;
               break;
         }

      }
   }

}
