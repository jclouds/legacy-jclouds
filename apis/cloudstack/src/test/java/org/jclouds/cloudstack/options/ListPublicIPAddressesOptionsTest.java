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

import static org.jclouds.cloudstack.options.ListPublicIPAddressesOptions.Builder.IPAddress;
import static org.jclouds.cloudstack.options.ListPublicIPAddressesOptions.Builder.VLANId;
import static org.jclouds.cloudstack.options.ListPublicIPAddressesOptions.Builder.accountInDomain;
import static org.jclouds.cloudstack.options.ListPublicIPAddressesOptions.Builder.allocatedOnly;
import static org.jclouds.cloudstack.options.ListPublicIPAddressesOptions.Builder.domainId;
import static org.jclouds.cloudstack.options.ListPublicIPAddressesOptions.Builder.id;
import static org.jclouds.cloudstack.options.ListPublicIPAddressesOptions.Builder.networkId;
import static org.jclouds.cloudstack.options.ListPublicIPAddressesOptions.Builder.usesVirtualNetwork;
import static org.jclouds.cloudstack.options.ListPublicIPAddressesOptions.Builder.zoneId;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code ListIPAddressesOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListPublicIPAddressesOptionsTest {

   public void testAllocatedOnly() {
      ListPublicIPAddressesOptions options = new ListPublicIPAddressesOptions().allocatedOnly(true);
      assertEquals(ImmutableList.of("true"), options.buildQueryParameters().get("allocatedonly"));
   }

   public void testAllocatedOnlyStatic() {
      ListPublicIPAddressesOptions options = allocatedOnly(true);
      assertEquals(ImmutableList.of("true"), options.buildQueryParameters().get("allocatedonly"));
   }

   public void testVLANId() {
      ListPublicIPAddressesOptions options = new ListPublicIPAddressesOptions().VLANId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("vlanid"));
   }

   public void testVLANIdStatic() {
      ListPublicIPAddressesOptions options = VLANId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("vlanid"));
   }

   public void testNetworkId() {
      ListPublicIPAddressesOptions options = new ListPublicIPAddressesOptions().networkId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("networkid"));
   }

   public void testNetworkIdStatic() {
      ListPublicIPAddressesOptions options = networkId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("networkid"));
   }

   public void testIPAddress() {
      ListPublicIPAddressesOptions options = new ListPublicIPAddressesOptions().IPAddress("10.1.1.1");
      assertEquals(ImmutableList.of("10.1.1.1"), options.buildQueryParameters().get("ipaddress"));
   }

   public void testIPAddressStatic() {
      ListPublicIPAddressesOptions options = IPAddress("10.1.1.1");
      assertEquals(ImmutableList.of("10.1.1.1"), options.buildQueryParameters().get("ipaddress"));
   }

   public void testAccountInDomainId() {
      ListPublicIPAddressesOptions options = new ListPublicIPAddressesOptions().accountInDomain("adrian", "6");
      assertEquals(ImmutableList.of("adrian"), options.buildQueryParameters().get("account"));
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testAccountInDomainIdStatic() {
      ListPublicIPAddressesOptions options = accountInDomain("adrian", "6");
      assertEquals(ImmutableList.of("adrian"), options.buildQueryParameters().get("account"));
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testZoneId() {
      ListPublicIPAddressesOptions options = new ListPublicIPAddressesOptions().zoneId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("zoneid"));
   }

   public void testZoneIdStatic() {
      ListPublicIPAddressesOptions options = zoneId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("zoneid"));
   }

   public void testUsingVirtualNetwork() {
      ListPublicIPAddressesOptions options = new ListPublicIPAddressesOptions().usesVirtualNetwork(true);
      assertEquals(ImmutableList.of("true"), options.buildQueryParameters().get("forvirtualnetwork"));
   }

   public void testUsingVirtualNetworkStatic() {
      ListPublicIPAddressesOptions options = usesVirtualNetwork(true);
      assertEquals(ImmutableList.of("true"), options.buildQueryParameters().get("forvirtualnetwork"));
   }

   public void testId() {
      ListPublicIPAddressesOptions options = new ListPublicIPAddressesOptions().id("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("id"));
   }

   public void testDomainId() {
      ListPublicIPAddressesOptions options = new ListPublicIPAddressesOptions().domainId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testIdStatic() {
      ListPublicIPAddressesOptions options = id("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("id"));
   }

   public void testDomainIdStatic() {
      ListPublicIPAddressesOptions options = domainId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }
}
