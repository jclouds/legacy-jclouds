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
package org.jclouds.openstack.nova.v2_0.predicates;

import static org.jclouds.openstack.nova.v2_0.predicates.SecurityGroupPredicates.nameEquals;
import static org.jclouds.openstack.nova.v2_0.predicates.SecurityGroupPredicates.nameIn;
import static org.jclouds.openstack.nova.v2_0.predicates.SecurityGroupPredicates.ruleCidr;
import static org.jclouds.openstack.nova.v2_0.predicates.SecurityGroupPredicates.ruleEndPort;
import static org.jclouds.openstack.nova.v2_0.predicates.SecurityGroupPredicates.ruleGroup;
import static org.jclouds.openstack.nova.v2_0.predicates.SecurityGroupPredicates.ruleProtocol;
import static org.jclouds.openstack.nova.v2_0.predicates.SecurityGroupPredicates.ruleStartPort;
import static org.testng.Assert.assertTrue;

import org.jclouds.net.domain.IpProtocol;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroup;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroupRule;
import org.jclouds.openstack.nova.v2_0.domain.TenantIdAndName;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "SecurityGroupPredicatesTest")
public class SecurityGroupPredicatesTest {
   SecurityGroup ref = SecurityGroup.builder().id("12345").name("jclouds").description("description").build();
   SecurityGroupRule ruleRef = SecurityGroupRule.builder().id("6789").parentGroupId("12345").ipRange("0.0.0.0/0")
           .fromPort(10).toPort(20).ipProtocol(IpProtocol.TCP)
           .group(TenantIdAndName.builder().tenantId("11111111").name("abcd").build())
           .build();

   public void testnameEqualsWhenEqual() {
      assertTrue(nameEquals("jclouds").apply(ref), "expected 'jclouds' as the name of " + ref);
   }

   @Test
   public void testnameEqualsWhenNotEqual() {
      assertTrue(!nameEquals("foo").apply(ref), "expected 'foo' not to be the name of " + ref);
   }

   @Test
   public void testNameInWhenIn() {
      assertTrue(nameIn(ImmutableSet.of("jclouds", "pants")).apply(ref),
              "expected the name of " + ref + " to be one of 'jclouds' or 'pants'");
   }

   @Test
   public void testNameInWhenNotIn() {
      assertTrue(!nameIn(ImmutableSet.of("foo", "pants")).apply(ref),
              "expected the name of " + ref + " to not be either of 'foo' or 'pants'");

   }

   @Test
   public void testRuleCidrWhenEqual() {
      assertTrue(ruleCidr("0.0.0.0/0").apply(ruleRef),
              "expected the CIDR to be '0.0.0.0/0' for " + ruleRef);
   }

   @Test
   public void testRuleCidrWhenNotEqual() {
      assertTrue(!ruleCidr("1.1.1.1/0").apply(ruleRef),
              "expected the CIDR to not be '1.1.1.1/0' for " + ruleRef);
   }

   @Test
   public void testRuleGroupWhenEqual() {
      assertTrue(ruleGroup("abcd").apply(ruleRef),
              "expected the group to be equal to 'abcd' for " + ruleRef);
   }

   @Test
   public void testRuleGroupWhenNotEqual() {
      assertTrue(!ruleGroup("pants").apply(ruleRef),
              "expected the group to not be equal to 'pants' for " + ruleRef);
   }

   @Test
   public void testRuleProtocolWhenEqual() {
      assertTrue(ruleProtocol(IpProtocol.TCP).apply(ruleRef),
              "expected TCP for " + ruleRef);
   }

   @Test
   public void testRuleProtocolWhenNotEqual() {
      assertTrue(!ruleProtocol(IpProtocol.UDP).apply(ruleRef),
              "expected not UDP for " + ruleRef);
   }

   @Test
   public void testRuleStartPortWhenEqual() {
      assertTrue(ruleStartPort(10).apply(ruleRef),
              "expected start port 10 for " + ruleRef);
   }

   @Test
   public void testRuleStartPortWhenNotEqual() {
      assertTrue(!ruleStartPort(50).apply(ruleRef),
              "expected start port not to be 50 for " + ruleRef);
   }

   @Test
   public void testRuleEndPortWhenEqual() {
      assertTrue(ruleEndPort(20).apply(ruleRef),
              "expected end port 20 for " + ruleRef);
   }

   @Test
   public void testRuleEndPortWhenNotEqual() {
      assertTrue(!ruleEndPort(50).apply(ruleRef),
              "expected end port not to be 50 for " + ruleRef);
   }
}
