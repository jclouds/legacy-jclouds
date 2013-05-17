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

import static org.jclouds.cloudstack.options.ListVlanIPRangesOptions.Builder.accountInDomain;
import static org.jclouds.cloudstack.options.ListVlanIPRangesOptions.Builder.domainId;
import static org.jclouds.cloudstack.options.ListVlanIPRangesOptions.Builder.forVirtualNetwork;
import static org.jclouds.cloudstack.options.ListVlanIPRangesOptions.Builder.id;
import static org.jclouds.cloudstack.options.ListVlanIPRangesOptions.Builder.keyword;
import static org.jclouds.cloudstack.options.ListVlanIPRangesOptions.Builder.networkId;
import static org.jclouds.cloudstack.options.ListVlanIPRangesOptions.Builder.podId;
import static org.jclouds.cloudstack.options.ListVlanIPRangesOptions.Builder.vlan;
import static org.jclouds.cloudstack.options.ListVlanIPRangesOptions.Builder.zoneId;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code ListVlanIPRangesOptions}
 * 
 * @author Richard Downer
 */
@Test(groups = "unit")
public class ListVlanIPRangesOptionsTest {

   public void testAccountInDomain() {
      ListVlanIPRangesOptions options = new ListVlanIPRangesOptions().accountInDomain("fred", "19");
      assertEquals(ImmutableList.of("fred"), options.buildQueryParameters().get("account"));
      assertEquals(ImmutableList.of("19"), options.buildQueryParameters().get("domainid"));
   }

   public void testAccountInDomainStatic() {
      ListVlanIPRangesOptions options = accountInDomain("fred", "19");
      assertEquals(ImmutableList.of("fred"), options.buildQueryParameters().get("account"));
      assertEquals(ImmutableList.of("19"), options.buildQueryParameters().get("domainid"));
   }

   public void testDomainId() {
      ListVlanIPRangesOptions options = new ListVlanIPRangesOptions().domainId("19");
      assertEquals(ImmutableList.of("19"), options.buildQueryParameters().get("domainid"));
   }

   public void testDomainIdStatic() {
      ListVlanIPRangesOptions options = domainId("19");
      assertEquals(ImmutableList.of("19"), options.buildQueryParameters().get("domainid"));
   }

   public void testForVirtualNetwork() {
      ListVlanIPRangesOptions options = new ListVlanIPRangesOptions().forVirtualNetwork(true);
      assertEquals(ImmutableList.of("true"), options.buildQueryParameters().get("forvirtualnetwork"));
   }

   public void testForVirtualNetworkStatic() {
      ListVlanIPRangesOptions options = forVirtualNetwork(true);
      assertEquals(ImmutableList.of("true"), options.buildQueryParameters().get("forvirtualnetwork"));
   }

   public void testId() {
      ListVlanIPRangesOptions options = new ListVlanIPRangesOptions().id("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("id"));
   }

   public void testIdStatic() {
      ListVlanIPRangesOptions options = id("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("id"));
   }

   public void testKeyword() {
      ListVlanIPRangesOptions options = new ListVlanIPRangesOptions().keyword("fred");
      assertEquals(ImmutableList.of("fred"), options.buildQueryParameters().get("keyword"));
   }

   public void testKeywordStatic() {
      ListVlanIPRangesOptions options = keyword("fred");
      assertEquals(ImmutableList.of("fred"), options.buildQueryParameters().get("keyword"));
   }

   public void testNetworkId() {
      ListVlanIPRangesOptions options = new ListVlanIPRangesOptions().networkId("209");
      assertEquals(ImmutableList.of("209"), options.buildQueryParameters().get("networkid"));
   }

   public void testNetworkIdStatic() {
      ListVlanIPRangesOptions options = networkId("209");
      assertEquals(ImmutableList.of("209"), options.buildQueryParameters().get("networkid"));
   }

   public void testPodId() {
      ListVlanIPRangesOptions options = new ListVlanIPRangesOptions().podId("13");
      assertEquals(ImmutableList.of("13"), options.buildQueryParameters().get("podid"));
   }

   public void testPodIdStatic() {
      ListVlanIPRangesOptions options = podId("13");
      assertEquals(ImmutableList.of("13"), options.buildQueryParameters().get("podid"));
   }

   public void testVlan() {
      ListVlanIPRangesOptions options = new ListVlanIPRangesOptions().vlan(1001);
      assertEquals(ImmutableList.of("1001"), options.buildQueryParameters().get("vlan"));
   }

   public void testVlanStatic() {
      ListVlanIPRangesOptions options = vlan(1001);
      assertEquals(ImmutableList.of("1001"), options.buildQueryParameters().get("vlan"));
   }

   public void testZoneId() {
      ListVlanIPRangesOptions options = new ListVlanIPRangesOptions().zoneId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("zoneid"));
   }

   public void testZoneIdStatic() {
      ListVlanIPRangesOptions options = zoneId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("zoneid"));
   }

}
