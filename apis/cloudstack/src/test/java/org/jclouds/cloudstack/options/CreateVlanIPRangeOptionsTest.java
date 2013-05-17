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
package org.jclouds.cloudstack.options;

import static org.jclouds.cloudstack.options.CreateVlanIPRangeOptions.Builder.accountInDomain;
import static org.jclouds.cloudstack.options.CreateVlanIPRangeOptions.Builder.domainId;
import static org.jclouds.cloudstack.options.CreateVlanIPRangeOptions.Builder.forVirtualNetwork;
import static org.jclouds.cloudstack.options.CreateVlanIPRangeOptions.Builder.gateway;
import static org.jclouds.cloudstack.options.CreateVlanIPRangeOptions.Builder.netmask;
import static org.jclouds.cloudstack.options.CreateVlanIPRangeOptions.Builder.networkId;
import static org.jclouds.cloudstack.options.CreateVlanIPRangeOptions.Builder.podId;
import static org.jclouds.cloudstack.options.CreateVlanIPRangeOptions.Builder.vlan;
import static org.jclouds.cloudstack.options.CreateVlanIPRangeOptions.Builder.zoneId;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code CreateVlanIPRangeOptions}
 *
 * @author Richard Downer
 */
@Test(groups = "unit")
public class CreateVlanIPRangeOptionsTest {

   public void testAccountInDomain() {
      CreateVlanIPRangeOptions options = new CreateVlanIPRangeOptions().accountInDomain("fred", "6");
      assertEquals(ImmutableSet.of("fred"), options.buildQueryParameters().get("account"));
      assertEquals(ImmutableSet.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testAccountInDomainStatic() {
      CreateVlanIPRangeOptions options = accountInDomain("fred", "6");
      assertEquals(ImmutableSet.of("fred"), options.buildQueryParameters().get("account"));
      assertEquals(ImmutableSet.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testDomainId() {
      CreateVlanIPRangeOptions options = new CreateVlanIPRangeOptions().domainId("6");
      assertEquals(ImmutableSet.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testDomainIdStatic() {
      CreateVlanIPRangeOptions options = domainId("6");
      assertEquals(ImmutableSet.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testForVirtualNetwork() {
      CreateVlanIPRangeOptions options = new CreateVlanIPRangeOptions().forVirtualNetwork(true);
      assertEquals(ImmutableSet.of("true"), options.buildQueryParameters().get("forvirtualnetwork"));
   }

   public void testForVirtualNetworkStatic() {
      CreateVlanIPRangeOptions options = forVirtualNetwork(true);
      assertEquals(ImmutableSet.of("true"), options.buildQueryParameters().get("forvirtualnetwork"));
   }

   public void testGateway() {
      CreateVlanIPRangeOptions options = new CreateVlanIPRangeOptions().gateway("192.168.42.24");
      assertEquals(ImmutableSet.of("192.168.42.24"), options.buildQueryParameters().get("gateway"));
   }

   public void testGatewayStatic() {
      CreateVlanIPRangeOptions options = gateway("192.168.42.24");
      assertEquals(ImmutableSet.of("192.168.42.24"), options.buildQueryParameters().get("gateway"));
   }

   public void testNetmask() {
      CreateVlanIPRangeOptions options = new CreateVlanIPRangeOptions().netmask("255.255.255.240");
      assertEquals(ImmutableSet.of("255.255.255.240"), options.buildQueryParameters().get("netmask"));
   }

   public void testNetmaskStatic() {
      CreateVlanIPRangeOptions options = netmask("255.255.255.240");
      assertEquals(ImmutableSet.of("255.255.255.240"), options.buildQueryParameters().get("netmask"));
   }

   public void testNetworkId() {
      CreateVlanIPRangeOptions options = new CreateVlanIPRangeOptions().networkId("9");
      assertEquals(ImmutableSet.of("9"), options.buildQueryParameters().get("networkid"));
   }

   public void testNetworkIdStatic() {
      CreateVlanIPRangeOptions options = networkId("9");
      assertEquals(ImmutableSet.of("9"), options.buildQueryParameters().get("networkid"));
   }

   public void testPodId() {
      CreateVlanIPRangeOptions options = new CreateVlanIPRangeOptions().podId("8");
      assertEquals(ImmutableSet.of("8"), options.buildQueryParameters().get("podid"));
   }

   public void testPodIdStatic() {
      CreateVlanIPRangeOptions options = podId("8");
      assertEquals(ImmutableSet.of("8"), options.buildQueryParameters().get("podid"));
   }

   public void testVlan() {
      CreateVlanIPRangeOptions options = new CreateVlanIPRangeOptions().vlan(1001);
      assertEquals(ImmutableSet.of("1001"), options.buildQueryParameters().get("vlan"));
   }

   public void testVlanStatic() {
      CreateVlanIPRangeOptions options = vlan(1001);
      assertEquals(ImmutableSet.of("1001"), options.buildQueryParameters().get("vlan"));
   }

   public void testZoneId() {
      CreateVlanIPRangeOptions options = new CreateVlanIPRangeOptions().zoneId("7");
      assertEquals(ImmutableSet.of("7"), options.buildQueryParameters().get("zoneid"));
   }

   public void testZoneIdStatic() {
      CreateVlanIPRangeOptions options = zoneId("7");
      assertEquals(ImmutableSet.of("7"), options.buildQueryParameters().get("zoneid"));
   }

}
