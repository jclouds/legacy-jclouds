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

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.domain.NetworkOffering;
import org.jclouds.cloudstack.domain.VlanIPRange;
import org.jclouds.cloudstack.domain.Zone;
import org.jclouds.cloudstack.options.CreateVlanIPRangeOptions;
import org.jclouds.cloudstack.options.ListVlanIPRangesOptions;
import org.jclouds.cloudstack.predicates.NetworkOfferingPredicates;
import org.jclouds.cloudstack.predicates.ZonePredicates;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.Set;

import static com.google.common.collect.Iterables.find;
import static org.testng.Assert.*;

/**
 * Tests behavior of {@code GlobalVlanClient}
 *
 * @author Richard Downer
 */
@Test(groups = "live", singleThreaded = true, testName = "GlobalVlanClientLiveTest")
public class GlobalVlanClientLiveTest extends BaseCloudStackClientLiveTest {

   private Network network;
   private VlanIPRange range;

   public void testListVlanIPRanges() throws Exception {
      Set<VlanIPRange> response = globalAdminClient.getVlanClient().listVlanIPRanges();
      assert null != response;
      long rangeCount = response.size();
      assertTrue(rangeCount >= 0);
      for (VlanIPRange range : response) {
         VlanIPRange newDetails = Iterables.getOnlyElement(globalAdminClient.getVlanClient().listVlanIPRanges(
            ListVlanIPRangesOptions.Builder.id(range.getId())));
         assertEquals(range, newDetails);
         assertEquals(range, globalAdminClient.getVlanClient().getVlanIPRange(range.getId()));
         assertFalse(range.getId() <= 0);
         assertFalse(range.getZoneId() <= 0);
         assertFalse(Strings.isNullOrEmpty(range.getVlan()));
         assertFalse(Strings.isNullOrEmpty(range.getAccount()));
         assertFalse(range.getDomainId() <= 0);
         assertFalse(Strings.isNullOrEmpty(range.getDomain()));
         assertFalse(Strings.isNullOrEmpty(range.getGateway()));
         assertFalse(Strings.isNullOrEmpty(range.getNetmask()));
         assertFalse(Strings.isNullOrEmpty(range.getStartIP()));
         assertFalse(Strings.isNullOrEmpty(range.getEndIP()));
         assertFalse(range.getNetworkId() <= 0);
      }
   }

   public void testCreateVlanIPRange() {
      Zone zone = Iterables.find(client.getZoneClient().listZones(), ZonePredicates.supportsAdvancedNetworks());
      NetworkOffering offering = find(client.getOfferingClient().listNetworkOfferings(), NetworkOfferingPredicates.supportsGuestVirtualNetworks());

      network = client.getNetworkClient().createNetworkInZone(zone.getId(), offering.getId(), "net-"+prefix, "jclouds test "+prefix);

      range = globalAdminClient.getVlanClient().createVlanIPRange("172.19.1.1", "172.19.1.199", CreateVlanIPRangeOptions.Builder
         .accountInDomain(user.getAccount(), user.getDomainId())
         .forVirtualNetwork(true)
         .vlan(1001)
         .networkId(network.getId())
      );
   }

   @AfterClass
   public void testFixtureTearDown() {
      if (range != null) {
         globalAdminClient.getVlanClient().deleteVlanIPRange(range.getId());
         range = null;
      }
      if (network != null) {
         client.getNetworkClient().deleteNetwork(network.getId());
         network = null;
      }
   }

}
