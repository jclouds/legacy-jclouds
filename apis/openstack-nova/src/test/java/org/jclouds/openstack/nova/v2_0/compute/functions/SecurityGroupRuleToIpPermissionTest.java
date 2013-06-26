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
package org.jclouds.openstack.nova.v2_0.compute.functions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroupRule;
import org.jclouds.openstack.nova.v2_0.domain.TenantIdAndName;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;


/**
 * Tests for the function for transforming a nova specific SecurityGroupRule into a generic
 * IpPermission object.
 * 
 * @author Andrew Bayer
 */
public class SecurityGroupRuleToIpPermissionTest {

   @Test
   public void testApplyWithGroup() {

      TenantIdAndName group = TenantIdAndName.builder().tenantId("tenant").name("name").build();
      
      SecurityGroupRule ruleToConvert = SecurityGroupRule.builder()
         .id("some-id")
         .ipProtocol(IpProtocol.TCP)
         .fromPort(10)
         .toPort(20)
         .group(group)
         .parentGroupId("some-other-id")
         .build();

      SecurityGroupRuleToIpPermission converter = new SecurityGroupRuleToIpPermission();

      IpPermission convertedPerm = converter.apply(ruleToConvert);

      assertEquals(convertedPerm.getIpProtocol(), ruleToConvert.getIpProtocol());
      assertEquals(convertedPerm.getFromPort(), ruleToConvert.getFromPort());
      assertEquals(convertedPerm.getToPort(), ruleToConvert.getToPort());
      assertTrue(convertedPerm.getTenantIdGroupNamePairs().containsKey(group.getTenantId()));
      assertTrue(convertedPerm.getTenantIdGroupNamePairs().containsValue(group.getName()));
      assertTrue(convertedPerm.getCidrBlocks().size() == 0);
   }

   @Test
   public void testApplyWithCidr() {
      SecurityGroupRule ruleToConvert = SecurityGroupRule.builder()
         .id("some-id")
         .ipProtocol(IpProtocol.TCP)
         .fromPort(10)
         .toPort(20)
         .ipRange("0.0.0.0/0")
         .parentGroupId("some-other-id")
         .build();

      SecurityGroupRuleToIpPermission converter = new SecurityGroupRuleToIpPermission();

      IpPermission convertedPerm = converter.apply(ruleToConvert);

      assertEquals(convertedPerm.getIpProtocol(), ruleToConvert.getIpProtocol());
      assertEquals(convertedPerm.getFromPort(), ruleToConvert.getFromPort());
      assertEquals(convertedPerm.getToPort(), ruleToConvert.getToPort());
      assertEquals(convertedPerm.getCidrBlocks(), ImmutableSet.of("0.0.0.0/0"));
      assertTrue(convertedPerm.getTenantIdGroupNamePairs().size() == 0);
   }
}
