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

import static org.jclouds.cloudstack.options.ListIPForwardingRulesOptions.Builder.IPAddressId;
import static org.jclouds.cloudstack.options.ListIPForwardingRulesOptions.Builder.accountInDomain;
import static org.jclouds.cloudstack.options.ListIPForwardingRulesOptions.Builder.domainId;
import static org.jclouds.cloudstack.options.ListIPForwardingRulesOptions.Builder.id;
import static org.jclouds.cloudstack.options.ListIPForwardingRulesOptions.Builder.virtualMachineId;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code ListIPForwardingRulesOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListIPForwardingRulesOptionsTest {

   public void testId() {
      ListIPForwardingRulesOptions options = new ListIPForwardingRulesOptions().id("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("id"));
   }

   public void testIdStatic() {
      ListIPForwardingRulesOptions options = id("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("id"));
   }

   public void testAccount() {
      ListIPForwardingRulesOptions options = new ListIPForwardingRulesOptions().accountInDomain("account", "6");
      assertEquals(ImmutableList.of("account"), options.buildQueryParameters().get("account"));
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testAccountStatic() {
      ListIPForwardingRulesOptions options = accountInDomain("account", "6");
      assertEquals(ImmutableList.of("account"), options.buildQueryParameters().get("account"));
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testName() {
      ListIPForwardingRulesOptions options = new ListIPForwardingRulesOptions().IPAddressId("9");
      assertEquals(ImmutableList.of("9"), options.buildQueryParameters().get("ipaddressid"));
   }

   public void testNameStatic() {
      ListIPForwardingRulesOptions options = IPAddressId("9");
      assertEquals(ImmutableList.of("9"), options.buildQueryParameters().get("ipaddressid"));
   }

   public void testDomainId() {
      ListIPForwardingRulesOptions options = new ListIPForwardingRulesOptions().domainId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testDomainIdStatic() {
      ListIPForwardingRulesOptions options = domainId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testVirtualMachineId() {
      ListIPForwardingRulesOptions options = new ListIPForwardingRulesOptions().virtualMachineId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("virtualmachineid"));
   }

   public void testVirtualMachineIdStatic() {
      ListIPForwardingRulesOptions options = virtualMachineId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("virtualmachineid"));
   }
}
