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
package org.jclouds.cloudstack.compute.strategy;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Map;

import org.jclouds.cloudstack.compute.options.CloudStackTemplateOptions;
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.domain.NetworkService;
import org.jclouds.cloudstack.options.DeployVirtualMachineOptions;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

@Test(singleThreaded = true, testName="OptionsConverterTest")
public class OptionsConverterTest {

   private static final Map<String,Network> EMPTY_NETWORKS_MAP = ImmutableMap.<String, Network>of();
   private static final String ZONE_ID = "2";
   private final NetworkService firewallServiceWithStaticNat
      = NetworkService.builder().name("Firewall").capabilities(ImmutableMap.of("StaticNat", "true")).build();

   @Test
   public void testBasicNetworkOptionsConverter() {
      BasicNetworkOptionsConverter converter = new BasicNetworkOptionsConverter();

      CloudStackTemplateOptions optionsIn = CloudStackTemplateOptions.Builder.securityGroupId("42").networkId("46");
      DeployVirtualMachineOptions optionsOut = new DeployVirtualMachineOptions();

      DeployVirtualMachineOptions optionsOut2 = converter.apply(optionsIn, EMPTY_NETWORKS_MAP, ZONE_ID, optionsOut);
      assertEquals(optionsOut, optionsOut2);

      DeployVirtualMachineOptions optionsExpected = DeployVirtualMachineOptions.Builder.securityGroupId("42").networkId("46");
      assertEquals(optionsOut, optionsExpected);
   }

   @Test
   public void testAdvancedSecurityGroupsNotAllowed() {
      boolean exceptionThrown = false;
      AdvancedNetworkOptionsConverter converter = new AdvancedNetworkOptionsConverter();
      CloudStackTemplateOptions optionsIn = CloudStackTemplateOptions.Builder.securityGroupId("42");

      try {
         converter.apply(optionsIn, EMPTY_NETWORKS_MAP, ZONE_ID, DeployVirtualMachineOptions.NONE);
      } catch(IllegalArgumentException e) {
         exceptionThrown = true;
      }

      assertTrue(exceptionThrown, "IllegalArgumentException should have been thrown");
   }

   @Test
   public void testAdvancedExplicitNetworkSelection() {
      AdvancedNetworkOptionsConverter converter = new AdvancedNetworkOptionsConverter();
      DeployVirtualMachineOptions optionsActual = converter.apply(CloudStackTemplateOptions.Builder.networkId("42"),
         EMPTY_NETWORKS_MAP, ZONE_ID, DeployVirtualMachineOptions.NONE);
      DeployVirtualMachineOptions optionsExpected = DeployVirtualMachineOptions.Builder.networkId("42");
      assertEquals(optionsActual, optionsExpected);
   }

   @Test
   public void testAdvancedAutoDetectNetwork() {
      AdvancedNetworkOptionsConverter converter = new AdvancedNetworkOptionsConverter();

      Network eligibleNetwork = Network.builder()
         .id("25").zoneId(ZONE_ID).isDefault(true).services(ImmutableSet.of(firewallServiceWithStaticNat))
         .build();
      DeployVirtualMachineOptions optionsActual = converter.apply(CloudStackTemplateOptions.NONE,
         ImmutableMap.of(eligibleNetwork.getId(), eligibleNetwork), ZONE_ID, DeployVirtualMachineOptions.NONE);
      DeployVirtualMachineOptions optionsExpected = DeployVirtualMachineOptions.Builder.networkId("25");
      assertEquals(optionsActual, optionsExpected);
   }

   @Test
   public void testAdvancedWhenNoNetworkGiven() {
      AdvancedNetworkOptionsConverter converter = new AdvancedNetworkOptionsConverter();
      boolean exceptionThrown = false;
      try {
         converter.apply(CloudStackTemplateOptions.NONE, EMPTY_NETWORKS_MAP, ZONE_ID, DeployVirtualMachineOptions.NONE);
      } catch(IllegalArgumentException e) {
         exceptionThrown = true;
      }
      assertTrue(exceptionThrown);
   }

   @Test
   public void testAdvancedWhenNoNetworkEligible() {
      AdvancedNetworkOptionsConverter converter = new AdvancedNetworkOptionsConverter();
      Network unsuitableNetwork = Network.builder()
         .id("25").zoneId(ZONE_ID)
         .build();

      boolean exceptionThrown = false;
      try {
         converter.apply(CloudStackTemplateOptions.NONE, ImmutableMap.of(unsuitableNetwork.getId(), unsuitableNetwork), ZONE_ID, DeployVirtualMachineOptions.NONE);
      } catch(IllegalArgumentException e) {
         exceptionThrown = true;
      }
      assertTrue(exceptionThrown);
   }
}
