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
package org.jclouds.net.util;

import static org.testng.Assert.assertEquals;

import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests possible uses of IpPermissions
 * 
 * @author Adrian Cole
 */
@Test(testName = "IpPermissionsTest")
public class IpPermissionsTest {
   public void testAllProtocol() {
      IpPermissions authorization = IpPermissions.permitAnyProtocol();
      assertEquals(authorization, IpPermission.builder().ipProtocol(IpProtocol.ALL).fromPort(1).toPort(65535)
               .cidrBlock("0.0.0.0/0").build());
   }

   public void testAllProtocolCidrBound() {
      IpPermissions authorization = IpPermissions.permit(IpProtocol.ALL).originatingFromCidrBlock("1.1.1.1/32");
      assertEquals(authorization, IpPermission.builder().ipProtocol(IpProtocol.ALL).fromPort(1).toPort(65535)
               .cidrBlock("1.1.1.1/32").build());
   }

   public void testJustProtocolAndCidr() {
      IpPermissions authorization = IpPermissions.permit(IpProtocol.TCP).originatingFromCidrBlock("1.1.1.1/32");
      assertEquals(authorization, IpPermission.builder().ipProtocol(IpProtocol.TCP).fromPort(1).toPort(65535)
               .cidrBlock("1.1.1.1/32").build());
   }

   public void testAnyProtocol() {
      IpPermissions authorization = IpPermissions.permitAnyProtocol().originatingFromCidrBlock("1.1.1.1/32");
      assertEquals(authorization, IpPermission.builder().ipProtocol(IpProtocol.ALL).fromPort(1).toPort(65535)
               .cidrBlock("1.1.1.1/32").build());
   }

   public void testMultipleCidrs() {
      IpPermissions authorization = IpPermissions.permit(IpProtocol.TCP).originatingFromCidrBlocks(
               ImmutableSet.of("1.1.1.1/32", "1.1.1.2/32"));
      assertEquals(authorization, IpPermission.builder().ipProtocol(IpProtocol.TCP).fromPort(1).toPort(65535)
               .cidrBlocks(ImmutableSet.of("1.1.1.1/32", "1.1.1.2/32")).build());
   }

   public void testProtocolFromAndToPortAndGroupIds() {
      IpPermissions authorization = IpPermissions.permit(IpProtocol.UDP).fromPort(11).to(53)
               .originatingFromSecurityGroupId("groupId");
      assertEquals(authorization, IpPermission.builder().ipProtocol(IpProtocol.UDP).fromPort(11).toPort(53)
               .groupId("groupId").build());
   }

   public void testProtocolICMPAny() {
      IpPermissions authorization = IpPermissions.permitICMP().originatingFromSecurityGroupId("groupId");
      assertEquals(authorization, IpPermission.builder().ipProtocol(IpProtocol.ICMP).fromPort(-1).toPort(-1)
               .groupId("groupId").build());
   }

   public void testProtocolICMPTypeAnyCode() {
      IpPermissions authorization = IpPermissions.permitICMP().type(8).originatingFromSecurityGroupId("groupId");
      assertEquals(authorization, IpPermission.builder().ipProtocol(IpProtocol.ICMP).fromPort(8).toPort(-1)
               .groupId("groupId").build());
   }

   public void testProtocolICMPTypeCode() {
      IpPermissions authorization = IpPermissions.permitICMP().type(8).andCode(0).originatingFromSecurityGroupId(
               "groupId");
      assertEquals(authorization, IpPermission.builder().ipProtocol(IpProtocol.ICMP).fromPort(8).toPort(0).groupId(
               "groupId").build());
   }

   public void testProtocolFromAndToPortAndUserGroups() {
      IpPermissions authorization = IpPermissions.permit(IpProtocol.ICMP).fromPort(8).to(0)
               .originatingFromTenantAndSecurityGroup("tenantId", "groupName");

      assertEquals(authorization, IpPermission.builder().ipProtocol(IpProtocol.ICMP).fromPort(8).toPort(0)
               .tenantIdGroupNamePair("tenantId", "groupName").build());
   }
}
