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

import static org.jclouds.cloudstack.options.ListVirtualMachinesOptions.Builder.accountInDomain;
import static org.jclouds.cloudstack.options.ListVirtualMachinesOptions.Builder.domainId;
import static org.jclouds.cloudstack.options.ListVirtualMachinesOptions.Builder.groupId;
import static org.jclouds.cloudstack.options.ListVirtualMachinesOptions.Builder.hostId;
import static org.jclouds.cloudstack.options.ListVirtualMachinesOptions.Builder.id;
import static org.jclouds.cloudstack.options.ListVirtualMachinesOptions.Builder.networkId;
import static org.jclouds.cloudstack.options.ListVirtualMachinesOptions.Builder.podId;
import static org.jclouds.cloudstack.options.ListVirtualMachinesOptions.Builder.state;
import static org.jclouds.cloudstack.options.ListVirtualMachinesOptions.Builder.usesVirtualNetwork;
import static org.jclouds.cloudstack.options.ListVirtualMachinesOptions.Builder.zoneId;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code ListVirtualMachinesOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListVirtualMachesOptionsTest {

   public void testHostId() {
      ListVirtualMachinesOptions options = new ListVirtualMachinesOptions().hostId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("hostid"));
   }

   public void testHostIdStatic() {
      ListVirtualMachinesOptions options = hostId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("hostid"));
   }

   public void testPodId() {
      ListVirtualMachinesOptions options = new ListVirtualMachinesOptions().podId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("podid"));
   }

   public void testPodIdStatic() {
      ListVirtualMachinesOptions options = podId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("podid"));
   }

   public void testNetworkId() {
      ListVirtualMachinesOptions options = new ListVirtualMachinesOptions().networkId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("networkid"));
   }

   public void testNetworkIdStatic() {
      ListVirtualMachinesOptions options = networkId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("networkid"));
   }

   public void testGroupId() {
      ListVirtualMachinesOptions options = new ListVirtualMachinesOptions().groupId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("groupid"));
   }

   public void testGroupIdStatic() {
      ListVirtualMachinesOptions options = groupId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("groupid"));
   }

   public void testAccountInDomainId() {
      ListVirtualMachinesOptions options = new ListVirtualMachinesOptions().accountInDomain("adrian", "6");
      assertEquals(ImmutableList.of("adrian"), options.buildQueryParameters().get("account"));
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testAccountInDomainIdStatic() {
      ListVirtualMachinesOptions options = accountInDomain("adrian", "6");
      assertEquals(ImmutableList.of("adrian"), options.buildQueryParameters().get("account"));
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testName() {
      ListVirtualMachinesOptions options = new ListVirtualMachinesOptions().id("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("id"));
   }

   public void testNameStatic() {
      ListVirtualMachinesOptions options = id("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("id"));
   }

   public void testZoneId() {
      ListVirtualMachinesOptions options = new ListVirtualMachinesOptions().zoneId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("zoneid"));
   }

   public void testZoneIdStatic() {
      ListVirtualMachinesOptions options = zoneId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("zoneid"));
   }

   public void testState() {
      ListVirtualMachinesOptions options = new ListVirtualMachinesOptions().state("state");
      assertEquals(ImmutableList.of("state"), options.buildQueryParameters().get("state"));
   }

   public void testStateStatic() {
      ListVirtualMachinesOptions options = state("state");
      assertEquals(ImmutableList.of("state"), options.buildQueryParameters().get("state"));
   }

   public void testUsingVirtualNetwork() {
      ListVirtualMachinesOptions options = new ListVirtualMachinesOptions().usesVirtualNetwork(true);
      assertEquals(ImmutableList.of("true"), options.buildQueryParameters().get("forvirtualnetwork"));
   }

   public void testUsingVirtualNetworkStatic() {
      ListVirtualMachinesOptions options = usesVirtualNetwork(true);
      assertEquals(ImmutableList.of("true"), options.buildQueryParameters().get("forvirtualnetwork"));
   }

   public void testId() {
      ListVirtualMachinesOptions options = new ListVirtualMachinesOptions().id("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("id"));
   }

   public void testDomainId() {
      ListVirtualMachinesOptions options = new ListVirtualMachinesOptions().domainId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testIdStatic() {
      ListVirtualMachinesOptions options = id("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("id"));
   }

   public void testDomainIdStatic() {
      ListVirtualMachinesOptions options = domainId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }
}
