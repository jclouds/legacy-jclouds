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

import static org.jclouds.cloudstack.options.CreateNetworkOptions.Builder.accountInDomain;
import static org.jclouds.cloudstack.options.CreateNetworkOptions.Builder.endIP;
import static org.jclouds.cloudstack.options.CreateNetworkOptions.Builder.gateway;
import static org.jclouds.cloudstack.options.CreateNetworkOptions.Builder.isDefault;
import static org.jclouds.cloudstack.options.CreateNetworkOptions.Builder.isShared;
import static org.jclouds.cloudstack.options.CreateNetworkOptions.Builder.netmask;
import static org.jclouds.cloudstack.options.CreateNetworkOptions.Builder.networkDomain;
import static org.jclouds.cloudstack.options.CreateNetworkOptions.Builder.startIP;
import static org.jclouds.cloudstack.options.CreateNetworkOptions.Builder.vlan;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code CreateNetworkOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class CreateNetworkOptionsTest {

   public void testAccountInDomainId() {
      CreateNetworkOptions options = new CreateNetworkOptions().accountInDomain("adrian", "6");
      assertEquals(ImmutableList.of("adrian"), options.buildQueryParameters().get("account"));
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testAccountInDomainIdStatic() {
      CreateNetworkOptions options = accountInDomain("adrian", "6");
      assertEquals(ImmutableList.of("adrian"), options.buildQueryParameters().get("account"));
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("domainid"));
   }

   public void testIsDefault() {
      CreateNetworkOptions options = new CreateNetworkOptions().isDefault(true);
      assertEquals(ImmutableList.of("true"), options.buildQueryParameters().get("isdefault"));
   }

   public void testIsDefaultStatic() {
      CreateNetworkOptions options = isDefault(true);
      assertEquals(ImmutableList.of("true"), options.buildQueryParameters().get("isdefault"));
   }

   public void testIsShared() {
      CreateNetworkOptions options = new CreateNetworkOptions().isShared(true);
      assertEquals(ImmutableList.of("true"), options.buildQueryParameters().get("isshared"));
   }

   public void testIsSharedStatic() {
      CreateNetworkOptions options = isShared(true);
      assertEquals(ImmutableList.of("true"), options.buildQueryParameters().get("isshared"));
   }

   public void testStartIP() {
      CreateNetworkOptions options = new CreateNetworkOptions().startIP("1.1.1.1");
      assertEquals(ImmutableList.of("1.1.1.1"), options.buildQueryParameters().get("startip"));
   }

   public void testStartIPStatic() {
      CreateNetworkOptions options = startIP("1.1.1.1");
      assertEquals(ImmutableList.of("1.1.1.1"), options.buildQueryParameters().get("startip"));
   }

   public void testEndIP() {
      CreateNetworkOptions options = new CreateNetworkOptions().endIP("1.1.1.1");
      assertEquals(ImmutableList.of("1.1.1.1"), options.buildQueryParameters().get("endip"));
   }

   public void testEndIPStatic() {
      CreateNetworkOptions options = endIP("1.1.1.1");
      assertEquals(ImmutableList.of("1.1.1.1"), options.buildQueryParameters().get("endip"));
   }

   public void testGateway() {
      CreateNetworkOptions options = new CreateNetworkOptions().gateway("1.1.1.1");
      assertEquals(ImmutableList.of("1.1.1.1"), options.buildQueryParameters().get("gateway"));
   }

   public void testGatewayStatic() {
      CreateNetworkOptions options = gateway("1.1.1.1");
      assertEquals(ImmutableList.of("1.1.1.1"), options.buildQueryParameters().get("gateway"));
   }

   public void testNetmask() {
      CreateNetworkOptions options = new CreateNetworkOptions().netmask("1.1.1.1");
      assertEquals(ImmutableList.of("1.1.1.1"), options.buildQueryParameters().get("netmask"));
   }

   public void testNetmaskStatic() {
      CreateNetworkOptions options = netmask("1.1.1.1");
      assertEquals(ImmutableList.of("1.1.1.1"), options.buildQueryParameters().get("netmask"));
   }

   public void testNetworkDomain() {
      CreateNetworkOptions options = new CreateNetworkOptions().networkDomain("network.com");
      assertEquals(ImmutableList.of("network.com"), options.buildQueryParameters().get("networkdomain"));
   }

   public void testNetworkDomainStatic() {
      CreateNetworkOptions options = networkDomain("network.com");
      assertEquals(ImmutableList.of("network.com"), options.buildQueryParameters().get("networkdomain"));
   }

   public void testVlan() {
      CreateNetworkOptions options = new CreateNetworkOptions().vlan("tag");
      assertEquals(ImmutableList.of("tag"), options.buildQueryParameters().get("vlan"));
   }

   public void testVlanStatic() {
      CreateNetworkOptions options = vlan("tag");
      assertEquals(ImmutableList.of("tag"), options.buildQueryParameters().get("vlan"));
   }

}
