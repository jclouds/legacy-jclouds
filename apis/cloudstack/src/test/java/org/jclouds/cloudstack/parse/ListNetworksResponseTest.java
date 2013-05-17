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
package org.jclouds.cloudstack.parse;

import java.net.URI;
import java.util.Set;

import org.jclouds.cloudstack.domain.GuestIPType;
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.domain.NetworkService;
import org.jclouds.cloudstack.domain.TrafficType;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListNetworksResponseTest extends BaseSetParserTest<Network> {

   @Override
   public String resource() {
      return "/listnetworksresponse.json";
   }

   @Override
   @SelectJson("network")
   public Set<Network> expected() {
      return ImmutableSet
            .<Network> of(Network
                  .builder()
                  .id("204")
                  .account("adrian")
                  .name("Virtual Network")
                  .displayText(
                        "A dedicated virtualized network for your account.  The broadcast domain is contained within a VLAN and all public network access is routed out by a virtual router.")
                  .broadcastDomainType("Vlan")
                  .trafficType(TrafficType.GUEST)
                  .zoneId("1")
                  .networkOfferingId("6")
                  .networkOfferingName("DefaultVirtualizedNetworkOffering")
                  .networkOfferingDisplayText("Virtual Vlan")
                  .networkOfferingAvailability("Required")
                  .isShared(false)
                  .isSystem(false)
                  .state("Implemented")
                  .related("204")
                  .broadcastURI(URI.create("vlan://240"))
                  .DNS(ImmutableList.of("8.8.8.8"))
                  .guestIPType(GuestIPType.VIRTUAL)
                  .domain("ROOT")
                  .domainId("1")
                  .isDefault(true)
                  .services(
                        ImmutableSortedSet.of(
                              NetworkService.builder().name("Vpn").capabilities(ImmutableMap.of("SupportedVpnTypes", "pptp,l2tp,ipsec")).build(),
                              NetworkService.builder().name("Gateway").build(),
                              NetworkService.builder().name("UserData").build(),
                              NetworkService.builder().name("Dhcp").build(),
                              NetworkService.builder().name("Firewall").capabilities(ImmutableSortedMap.<String, String> naturalOrder()
                                    .put("SupportedSourceNatTypes", "per account").put("StaticNat", "true")
                                    .put("TrafficStatistics", "per public ip").put("PortForwarding", "true")
                                    .put("MultipleIps", "true").put("SupportedProtocols", "tcp,udp").build()).build(),
                              NetworkService.builder().name("Dns").build(),
                              NetworkService.builder().name("Lb").capabilities(ImmutableMap.of("SupportedLbAlgorithms",
                                    "roundrobin,leastconn,source", "SupportedProtocols", "tcp, udp")).build()))
                  .networkDomain("cs3cloud.internal").build());
   }
}
