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

import static com.google.common.collect.Iterables.transform;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroupRule;
import org.jclouds.openstack.nova.v2_0.domain.TenantIdAndName;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;

/**
 * @author Andrew Bayer
 */
@Test(groups = "unit", testName = "NovaSecurityGroupToSecurityGroupTest")
public class NovaSecurityGroupToSecurityGroupTest {

   private static final SecurityGroupRuleToIpPermission ruleConverter = new SecurityGroupRuleToIpPermission();
   
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

      org.jclouds.openstack.nova.v2_0.domain.SecurityGroup origGroup = org.jclouds.openstack.nova.v2_0.domain.SecurityGroup.builder()
         .tenantId("tenant")
         .id("some-id")
         .name("some-group")
         .description("some-description")
         .rules(ruleToConvert)
         .build();

      NovaSecurityGroupToSecurityGroup parser = createGroupParser();

      SecurityGroup newGroup = parser.apply(origGroup);
      
      assertEquals(newGroup.getId(), origGroup.getId());
      assertEquals(newGroup.getProviderId(), origGroup.getId());
      assertEquals(newGroup.getName(), origGroup.getName());
      assertEquals(newGroup.getOwnerId(), origGroup.getTenantId());
      assertEquals(newGroup.getIpPermissions(), ImmutableSet.copyOf(transform(origGroup.getRules(), ruleConverter)));
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

      org.jclouds.openstack.nova.v2_0.domain.SecurityGroup origGroup = org.jclouds.openstack.nova.v2_0.domain.SecurityGroup.builder()
         .tenantId("tenant")
         .id("some-id")
         .name("some-group")
         .description("some-description")
         .rules(ruleToConvert)
         .build();

      NovaSecurityGroupToSecurityGroup parser = createGroupParser();

      SecurityGroup group = parser.apply(origGroup);
      
      assertEquals(group.getId(), origGroup.getId());
      assertEquals(group.getProviderId(), origGroup.getId());
      assertEquals(group.getName(), origGroup.getName());
      assertEquals(group.getOwnerId(), origGroup.getTenantId());
      assertEquals(group.getIpPermissions(), ImmutableSet.copyOf(transform(origGroup.getRules(), ruleConverter)));
   }

   private NovaSecurityGroupToSecurityGroup createGroupParser() {
      NovaSecurityGroupToSecurityGroup parser = new NovaSecurityGroupToSecurityGroup(ruleConverter);

      return parser;
   }

}
