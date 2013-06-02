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
package org.jclouds.cloudstack.compute.options;

import static org.jclouds.cloudstack.compute.options.CloudStackTemplateOptions.Builder.account;
import static org.jclouds.cloudstack.compute.options.CloudStackTemplateOptions.Builder.domainId;
import static org.jclouds.cloudstack.compute.options.CloudStackTemplateOptions.Builder.generateKeyPair;
import static org.jclouds.cloudstack.compute.options.CloudStackTemplateOptions.Builder.generateSecurityGroup;
import static org.jclouds.cloudstack.compute.options.CloudStackTemplateOptions.Builder.ipOnDefaultNetwork;
import static org.jclouds.cloudstack.compute.options.CloudStackTemplateOptions.Builder.ipsToNetworks;
import static org.jclouds.cloudstack.compute.options.CloudStackTemplateOptions.Builder.keyPair;
import static org.jclouds.cloudstack.compute.options.CloudStackTemplateOptions.Builder.networkId;
import static org.jclouds.cloudstack.compute.options.CloudStackTemplateOptions.Builder.networkIds;
import static org.jclouds.cloudstack.compute.options.CloudStackTemplateOptions.Builder.securityGroupId;
import static org.jclouds.cloudstack.compute.options.CloudStackTemplateOptions.Builder.securityGroupIds;
import static org.jclouds.cloudstack.compute.options.CloudStackTemplateOptions.Builder.setupStaticNat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.Map;

import org.jclouds.compute.options.TemplateOptions;
import org.testng.annotations.Test;
import org.testng.collections.Maps;

import com.google.common.collect.ImmutableSet;

/**
 * Tests possible uses of {@code CloudStackTemplateOptions} and
 * {@code CloudStackTemplateOptions.Builder.*}.
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "CloudStackTemplateOptionsTest")
public class CloudStackTemplateOptionsTest {
   //TODO: test clone();
   
   @Test
   public void testAs() {
      TemplateOptions options = new CloudStackTemplateOptions();
      assertEquals(options.as(CloudStackTemplateOptions.class), options);
   }

   @Test
   public void testDefaultSecurityGroupIds() {
      TemplateOptions options = new CloudStackTemplateOptions();
      assertEquals(options.as(CloudStackTemplateOptions.class).getSecurityGroupIds(), ImmutableSet.of());
   }

   @Test
   public void testSecurityGroupId() {
      TemplateOptions options = new CloudStackTemplateOptions().securityGroupId("3");
      assertEquals(options.as(CloudStackTemplateOptions.class).getSecurityGroupIds(), ImmutableSet.of("3"));
   }

   @Test
   public void testSecurityGroupIdStatic() {
      TemplateOptions options = securityGroupId("3");
      assertEquals(options.as(CloudStackTemplateOptions.class).getSecurityGroupIds(), ImmutableSet.of("3"));
   }

   @Test
   public void testSecurityGroupIds() {
      TemplateOptions options = new CloudStackTemplateOptions().securityGroupIds(ImmutableSet.of("3"));
      assertEquals(options.as(CloudStackTemplateOptions.class).getSecurityGroupIds(), ImmutableSet.of("3"));
   }

   @Test
   public void testSecurityGroupIdsStatic() {
      TemplateOptions options = securityGroupIds(ImmutableSet.of("3"));
      assertEquals(options.as(CloudStackTemplateOptions.class).getSecurityGroupIds(), ImmutableSet.of("3"));
   }

   @Test
   public void testGenerateSecurityGroupDefaultsFalse() {
      TemplateOptions options = new CloudStackTemplateOptions();
      assertFalse(options.as(CloudStackTemplateOptions.class)
         .shouldGenerateSecurityGroup());
   }

   @Test
   public void testGenerateSecurityGroup() {
      TemplateOptions options = new CloudStackTemplateOptions().generateSecurityGroup(true);
      assertTrue(options.as(CloudStackTemplateOptions.class)
         .shouldGenerateSecurityGroup());
   }

   @Test
   public void testGenerateSecurityGroupStatic() {
      TemplateOptions options = generateSecurityGroup(true);
      assertTrue(options.as(CloudStackTemplateOptions.class)
         .shouldGenerateSecurityGroup());
   }

   @Test
   public void testDefaultNetworkIds() {
      TemplateOptions options = new CloudStackTemplateOptions();
      assertEquals(options.as(CloudStackTemplateOptions.class).getNetworkIds(), ImmutableSet.of());
   }

   @Test
   public void testNetworkId() {
      TemplateOptions options = new CloudStackTemplateOptions().networkId("3");
      assertEquals(options.as(CloudStackTemplateOptions.class).getNetworkIds(), ImmutableSet.of("3"));
   }

   @Test
   public void testNetworkIdStatic() {
      TemplateOptions options = networkId("3");
      assertEquals(options.as(CloudStackTemplateOptions.class).getNetworkIds(), ImmutableSet.of("3"));
   }

   @Test
   public void testNetworkIds() {
      TemplateOptions options = new CloudStackTemplateOptions().networkIds(ImmutableSet.of("3"));
      assertEquals(options.as(CloudStackTemplateOptions.class).getNetworkIds(), ImmutableSet.of("3"));
   }

   @Test
   public void testNetworkIdsStatic() {
      TemplateOptions options = networkIds(ImmutableSet.of("3"));
      assertEquals(options.as(CloudStackTemplateOptions.class).getNetworkIds(), ImmutableSet.of("3"));
   }

   @Test
   public void testIpOnDefaultNetwork() {
      TemplateOptions options = new CloudStackTemplateOptions().ipOnDefaultNetwork("10.0.0.1");
      assertEquals(options.as(CloudStackTemplateOptions.class).getIpOnDefaultNetwork(), "10.0.0.1");
   }

   @Test
   public void testIpOnDefaultNetworkStatic() {
      TemplateOptions options = ipOnDefaultNetwork("10.0.0.1");
      assertEquals(options.as(CloudStackTemplateOptions.class).getIpOnDefaultNetwork(), "10.0.0.1");
   }

   @Test
   public void testIpsToNetwork() {
      Map<String, String> ipsToNetworks = Maps.newHashMap();
      ipsToNetworks.put("10.0.0.1", "5");

      TemplateOptions options = new CloudStackTemplateOptions().ipsToNetworks(ipsToNetworks);
      assertEquals(options.as(CloudStackTemplateOptions.class)
         .getIpsToNetworks().get("10.0.0.1"), "5");
   }

   @Test
   public void testIpsToNetworkStatic() {
      Map<String, String> ipsToNetworks = Maps.newHashMap();
      ipsToNetworks.put("10.0.0.1", "5");

      TemplateOptions options = ipsToNetworks(ipsToNetworks);
      assertEquals(options.as(CloudStackTemplateOptions.class)
         .getIpsToNetworks().get("10.0.0.1"), "5");
   }

   @Test
   public void testSetupStaticNatDefaultsTrue() {
      TemplateOptions options = new CloudStackTemplateOptions();
      assertTrue(options.as(CloudStackTemplateOptions.class)
         .shouldSetupStaticNat());
   }

   @Test
   public void testSetupStaticNat() {
      TemplateOptions options = new CloudStackTemplateOptions().setupStaticNat(false);
      assertFalse(options.as(CloudStackTemplateOptions.class)
         .shouldSetupStaticNat());
   }

   @Test
   public void testSetupStaticNatStatic() {
      TemplateOptions options = setupStaticNat(false);
      assertFalse(options.as(CloudStackTemplateOptions.class)
         .shouldSetupStaticNat());
   }

   @Test
   public void testGenerateKeyPairDefaultsFalse() {
      TemplateOptions options = new CloudStackTemplateOptions();
      assertFalse(options.as(CloudStackTemplateOptions.class)
         .shouldGenerateKeyPair());
   }

   @Test
   public void testGenerateKeyPair() {
      TemplateOptions options = new CloudStackTemplateOptions().generateKeyPair(true);
      assertTrue(options.as(CloudStackTemplateOptions.class)
         .shouldGenerateKeyPair());
   }

   @Test
   public void testGenerateKeyPairStatic() {
      TemplateOptions options = generateKeyPair(true);
      assertTrue(options.as(CloudStackTemplateOptions.class)
         .shouldGenerateKeyPair());
   }

   @Test
   public void testKeyPair() {
      TemplateOptions options = keyPair("test");
      assertEquals(options.as(CloudStackTemplateOptions.class).getKeyPair(), "test");
   }

   @Test
   public void testAccount() {
      TemplateOptions options = account("test");
      assertEquals(options.as(CloudStackTemplateOptions.class).getAccount(), "test");
   }

   @Test
   public void testDomainId() {
      TemplateOptions options = domainId("test");
      assertEquals(options.as(CloudStackTemplateOptions.class).getDomainId(), "test");
   }

   @Test
   public void testSecurityGroupIdsNullHasDecentMessage() {
      try {
         new CloudStackTemplateOptions().securityGroupIds(null);
         fail("should NPE");
      } catch (NullPointerException e) {
         assertEquals(e.getMessage(), "securityGroupIds was null");
      }
   }

}
