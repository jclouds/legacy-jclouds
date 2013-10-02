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
package org.jclouds.cloudstack.predicates;

import static org.jclouds.cloudstack.predicates.NetworkPredicates.defaultNetworkInZone;
import static org.jclouds.cloudstack.predicates.NetworkPredicates.hasLoadBalancerService;
import static org.jclouds.cloudstack.predicates.NetworkPredicates.supportsPortForwarding;
import static org.jclouds.cloudstack.predicates.NetworkPredicates.supportsStaticNAT;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.domain.NetworkService;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class NetworkPredicatesTest {

   public void testHasLoadBalancerService() {
      Network network = Network.builder().id("204").services(ImmutableSet.of(NetworkService.builder().name("Lb").build())).build();

      assert hasLoadBalancerService().apply(network);
      assert !supportsStaticNAT().apply(network);
      assert !supportsPortForwarding().apply(network);

   }

   public void testSupportsStaticNATFindsWhenFirewallHasStaticNatFeature() {
      Network network = Network
            .builder()
            .id("204")
            .services(
                  ImmutableSet.of(NetworkService.builder().name("Firewall").capabilities(
                        ImmutableMap.<String, String> of("StaticNat", "true")).build()))
            .build();

      assert !hasLoadBalancerService().apply(network);
      assert supportsStaticNAT().apply(network);
      assert !supportsPortForwarding().apply(network);
   }

   public void testNoSupport() {
      Network network = Network.builder().id("204")
            .services(ImmutableSet.of(NetworkService.builder().name("Firewall").capabilities(
                  ImmutableMap.<String, String> of()).build())).build();

      assert !hasLoadBalancerService().apply(network);
      assert !supportsStaticNAT().apply(network);
      assert !supportsPortForwarding().apply(network);
   }

   public void testSupportsPortForwardingFindsWhenFirewallHasPortForwardingFeature() {
      Network network = Network
            .builder()
            .id("204")
            .services(
                  ImmutableSet.of(NetworkService.builder().name("Firewall").capabilities(
                        ImmutableMap.<String, String> of("PortForwarding", "true")).build())).build();

      assert !hasLoadBalancerService().apply(network);
      assert !supportsStaticNAT().apply(network);
      assert supportsPortForwarding().apply(network);
   }

   public void testSupportsPortForwardingAndStaticNATWhenFirewallHasFeatures() {
      Network network = Network
            .builder()
            .id("204")
            .services(
                  ImmutableSet.of(NetworkService.builder().name("Firewall").capabilities(
                        ImmutableMap.<String, String> of("StaticNat", "true", "PortForwarding", "true")).build())).build();

      assert Predicates.and(supportsPortForwarding(), supportsStaticNAT()).apply(network);
      assert !hasLoadBalancerService().apply(network);

   }

   public void testDefaultNetworkInZone() {
      Network defaultInZone = Network.builder().id("42-1").isDefault(true).zoneId("42").build();
      Network defaultNotInZone = Network.builder().id("200-1").isDefault(true).zoneId("200").build();
      Network notDefaultInZone = Network.builder().id("42-2").isDefault(false).zoneId("42").build();
      Network notDefaultNotInZone = Network.builder().id("200-2").isDefault(false).zoneId("200").build();

      Predicate<Network> predicate = defaultNetworkInZone("42");
      assertTrue(predicate.apply(defaultInZone));
      assertFalse(predicate.apply(defaultNotInZone));
      assertFalse(predicate.apply(notDefaultInZone));
      assertFalse(predicate.apply(notDefaultNotInZone));
   }
}
