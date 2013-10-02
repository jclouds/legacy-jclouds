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
import static org.jclouds.cloudstack.options.ListNetworksOptions.Builder.zoneId;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.domain.NetworkOffering;
import org.jclouds.cloudstack.domain.TrafficType;
import org.jclouds.cloudstack.domain.VlanIPRange;
import org.jclouds.cloudstack.domain.Zone;
import org.jclouds.cloudstack.internal.BaseCloudStackApiLiveTest;
import org.jclouds.cloudstack.options.CreateVlanIPRangeOptions;
import org.jclouds.cloudstack.options.ListVlanIPRangesOptions;
import org.jclouds.cloudstack.predicates.NetworkOfferingPredicates;
import org.jclouds.cloudstack.predicates.ZonePredicates;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Tests behavior of {@code GlobalVlanApi}
 *
 * @author Richard Downer
 */
@Test(groups = "live", singleThreaded = true, testName = "GlobalVlanApiLiveTest")
public class GlobalVlanApiLiveTest extends BaseCloudStackApiLiveTest {

   private Network network;
   private boolean usingExistingNetwork;

   private VlanIPRange range;

   @Test
   public void testListVlanIPRanges() throws Exception {
      skipIfNotGlobalAdmin();

      Set<VlanIPRange> response = globalAdminClient.getVlanClient().listVlanIPRanges();
      assert null != response;
      long rangeCount = response.size();
      assertTrue(rangeCount >= 0);

      for (VlanIPRange range : response) {
         VlanIPRange newDetails = Iterables.getOnlyElement(globalAdminClient.getVlanClient().listVlanIPRanges(
            ListVlanIPRangesOptions.Builder.id(range.getId())));
         assertEquals(range, newDetails);
         assertEquals(range, globalAdminClient.getVlanClient().getVlanIPRange(range.getId()));
         assertNull(range.getId());
         assertNull(range.getZoneId());
         assertFalse(Strings.isNullOrEmpty(range.getVlan()));
         assertFalse(Strings.isNullOrEmpty(range.getAccount()));
         assertNull(range.getDomainId());
         assertFalse(Strings.isNullOrEmpty(range.getDomain()));
         assertFalse(Strings.isNullOrEmpty(range.getGateway()));
         assertFalse(Strings.isNullOrEmpty(range.getNetmask()));
         assertFalse(Strings.isNullOrEmpty(range.getStartIP()));
         assertFalse(Strings.isNullOrEmpty(range.getEndIP()));
         assertNull(range.getNetworkId());
      }
   }

   @Test
   public void testCreateVlanIPRange() {
      skipIfNotGlobalAdmin();

      final Zone zone = Iterables.find(client.getZoneApi().listZones(), ZonePredicates.supportsAdvancedNetworks());
      final NetworkOffering offering = Iterables.tryFind(client.getOfferingApi().listNetworkOfferings(),
                                                         NetworkOfferingPredicates.supportsGuestVirtualNetworks()).orNull();

      if (offering != null) {
         Set<Network> suitableNetworks = Sets.filter(client.getNetworkApi().listNetworks(
                                                                                         zoneId(zone.getId()).isSystem(false).trafficType(TrafficType.GUEST)),
                                                     new Predicate<Network>() {
                                                        @Override
                                                        public boolean apply(Network network) {
                                                           return network.getNetworkOfferingId().equals(offering.getId());
                                                        }
                                                     });
         
         if (suitableNetworks.size() > 0) {
            network = Iterables.get(suitableNetworks, 0);
            usingExistingNetwork = true;
         
         } else if (network == null) {
            network = client.getNetworkApi().createNetworkInZone(zone.getId(),
                                                                 offering.getId(), "net-" + prefix, "jclouds test " + prefix);
            usingExistingNetwork = false;
         }
         
         range = globalAdminClient.getVlanClient().createVlanIPRange("172.19.1.1", "172.19.1.199", CreateVlanIPRangeOptions.Builder
                                                                     .accountInDomain(user.getAccount(), user.getDomainId())
                                                                     .forVirtualNetwork(true)
                                                                     .vlan(1001)
                                                                     .networkId(network.getId())
                                                                     );
      }
   }

   @AfterGroups(groups = "live")
   @Override
   protected void tearDownContext() {
      if (range != null) {
         globalAdminClient.getVlanClient().deleteVlanIPRange(range.getId());
         range = null;
      }
      if (network != null && !usingExistingNetwork) {
         client.getNetworkApi().deleteNetwork(network.getId());
         network = null;
      }
      super.tearDownContext();
   }

}
