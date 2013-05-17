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

import static org.jclouds.cloudstack.options.ListLoadBalancerRulesOptions.Builder.accountInDomain;
import static org.jclouds.cloudstack.options.ListLoadBalancerRulesOptions.Builder.domainId;
import static org.jclouds.cloudstack.options.ListLoadBalancerRulesOptions.Builder.id;
import static org.jclouds.cloudstack.options.ListLoadBalancerRulesOptions.Builder.name;
import static org.jclouds.cloudstack.options.ListLoadBalancerRulesOptions.Builder.page;
import static org.jclouds.cloudstack.options.ListLoadBalancerRulesOptions.Builder.pageSize;
import static org.jclouds.cloudstack.options.ListLoadBalancerRulesOptions.Builder.publicIPId;
import static org.jclouds.cloudstack.options.ListLoadBalancerRulesOptions.Builder.virtualMachineId;
import static org.jclouds.cloudstack.options.ListLoadBalancerRulesOptions.Builder.zoneId;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code ListLoadBalancerRulesOptions}
 *
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListLoadBalancerRulesOptionsTest {

   public void testId() {
      ListLoadBalancerRulesOptions options = new ListLoadBalancerRulesOptions().id("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("id"));
   }

   public void testIdStatic() {
      ListLoadBalancerRulesOptions options = id("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("id"));
   }

   public void testAccount() {
      ListLoadBalancerRulesOptions options = new ListLoadBalancerRulesOptions().accountInDomain("account", "6");
      assertEquals(ImmutableList.of("account"), options.buildQueryParameters().get("account"));
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testAccountStatic() {
      ListLoadBalancerRulesOptions options = accountInDomain("account", "6");
      assertEquals(ImmutableList.of("account"), options.buildQueryParameters().get("account"));
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testName() {
      ListLoadBalancerRulesOptions options = new ListLoadBalancerRulesOptions().name("name");
      assertEquals(ImmutableList.of("name"), options.buildQueryParameters().get("name"));
   }

   public void testNameStatic() {
      ListLoadBalancerRulesOptions options = name("name");
      assertEquals(ImmutableList.of("name"), options.buildQueryParameters().get("name"));
   }

   public void testPublicIPId() {
      ListLoadBalancerRulesOptions options = new ListLoadBalancerRulesOptions().publicIPId("9");
      assertEquals(ImmutableList.of("9"), options.buildQueryParameters().get("publicipid"));
   }

   public void testPublicIPIdStatic() {
      ListLoadBalancerRulesOptions options = publicIPId("9");
      assertEquals(ImmutableList.of("9"), options.buildQueryParameters().get("publicipid"));
   }

   public void testDomainId() {
      ListLoadBalancerRulesOptions options = new ListLoadBalancerRulesOptions().domainId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testDomainIdStatic() {
      ListLoadBalancerRulesOptions options = domainId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testVirtualMachineId() {
      ListLoadBalancerRulesOptions options = new ListLoadBalancerRulesOptions().virtualMachineId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("virtualmachineid"));
   }

   public void testVirtualMachineIdStatic() {
      ListLoadBalancerRulesOptions options = virtualMachineId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("virtualmachineid"));
   }

   public void testZoneId() {
      ListLoadBalancerRulesOptions options = new ListLoadBalancerRulesOptions().zoneId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("zoneid"));
   }

   public void testZoneIdStatic() {
      ListLoadBalancerRulesOptions options = zoneId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("zoneid"));
   }

   public void testPage() {
      ListLoadBalancerRulesOptions options = new ListLoadBalancerRulesOptions().page(6);
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("page"));
   }

   public void testPageStatic() {
      ListLoadBalancerRulesOptions options = page(6);
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("page"));
   }

   public void testPageSize() {
      ListLoadBalancerRulesOptions options = new ListLoadBalancerRulesOptions().pageSize(6);
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("pagesize"));
   }

   public void testPageSizeStatic() {
      ListLoadBalancerRulesOptions options = pageSize(6);
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("pagesize"));
   }
}
