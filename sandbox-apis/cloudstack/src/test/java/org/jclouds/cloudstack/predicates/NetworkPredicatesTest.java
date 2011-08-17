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
package org.jclouds.cloudstack.predicates;

import static org.jclouds.cloudstack.predicates.NetworkPredicates.hasLoadBalancerService;
import static org.jclouds.cloudstack.predicates.NetworkPredicates.supportsPortForwarding;
import static org.jclouds.cloudstack.predicates.NetworkPredicates.supportsStaticNAT;

import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.domain.NetworkService;
import org.testng.annotations.Test;

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
      Network network = Network.builder().id(204).services(ImmutableSet.of(new NetworkService("Lb"))).build();

      assert hasLoadBalancerService().apply(network);
      assert !supportsStaticNAT().apply(network);
      assert !supportsPortForwarding().apply(network);

   }

   public void testSupportsStaticNATFindsWhenFirewallHasStaticNatFeature() {
      Network network = Network.builder().id(204).services(
               ImmutableSet.of(new NetworkService("Firewall", ImmutableMap.<String, String> of("StaticNat", "true"))))
               .build();

      assert !hasLoadBalancerService().apply(network);
      assert supportsStaticNAT().apply(network);
      assert !supportsPortForwarding().apply(network);
   }

   public void testNoSupport() {
      Network network = Network.builder().id(204).services(
               ImmutableSet.of(new NetworkService("Firewall", ImmutableMap.<String, String> of()))).build();

      assert !hasLoadBalancerService().apply(network);
      assert !supportsStaticNAT().apply(network);
      assert !supportsPortForwarding().apply(network);
   }

   public void testSupportsPortForwardingFindsWhenFirewallHasPortForwardingFeature() {
      Network network = Network.builder().id(204).services(
               ImmutableSet.of(new NetworkService("Firewall", ImmutableMap
                        .<String, String> of("PortForwarding", "true")))).build();

      assert !hasLoadBalancerService().apply(network);
      assert !supportsStaticNAT().apply(network);
      assert supportsPortForwarding().apply(network);
   }

   public void testSupportsPortForwardingAndStaticNATWhenFirewallHasFeatures() {
      Network network = Network.builder().id(204).services(
               ImmutableSet.of(new NetworkService("Firewall", ImmutableMap.<String, String> of("StaticNat", "true",
                        "PortForwarding", "true")))).build();

      assert Predicates.and(supportsPortForwarding(), supportsStaticNAT()).apply(network);
      assert !hasLoadBalancerService().apply(network);

   }
}
