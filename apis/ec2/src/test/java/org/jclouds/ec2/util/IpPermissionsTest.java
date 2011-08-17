/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.ec2.util;

import static org.testng.Assert.assertEquals;

import org.jclouds.ec2.domain.IpProtocol;
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
      assertEquals(
            IpPermissions.buildFormParametersForIndex(0, authorization).toString(),
            "{IpPermissions.0.IpProtocol=[-1], IpPermissions.0.FromPort=[1], IpPermissions.0.ToPort=[65535], IpPermissions.0.IpRanges.0.CidrIp=[0.0.0.0/0]}");
   }

   public void testAllProtocolCidrBound() {
      IpPermissions authorization = IpPermissions.permit(IpProtocol.ALL).originatingFromCidrBlock("1.1.1.1/32");
      assertEquals(
            IpPermissions.buildFormParametersForIndex(0, authorization).toString(),
            "{IpPermissions.0.IpProtocol=[-1], IpPermissions.0.FromPort=[1], IpPermissions.0.ToPort=[65535], IpPermissions.0.IpRanges.0.CidrIp=[1.1.1.1/32]}");
   }

   public void testJustProtocolAndCidr() {
      IpPermissions authorization = IpPermissions.permit(IpProtocol.TCP).originatingFromCidrBlock("1.1.1.1/32");
      assertEquals(
            IpPermissions.buildFormParametersForIndex(0, authorization).toString(),
            "{IpPermissions.0.IpProtocol=[tcp], IpPermissions.0.FromPort=[1], IpPermissions.0.ToPort=[65535], IpPermissions.0.IpRanges.0.CidrIp=[1.1.1.1/32]}");
   }

   public void testAnyProtocol() {
      IpPermissions authorization = IpPermissions.permitAnyProtocol().originatingFromCidrBlock("1.1.1.1/32");
      assertEquals(
            IpPermissions.buildFormParametersForIndex(0, authorization).toString(),
            "{IpPermissions.0.IpProtocol=[-1], IpPermissions.0.FromPort=[1], IpPermissions.0.ToPort=[65535], IpPermissions.0.IpRanges.0.CidrIp=[1.1.1.1/32]}");
   }

   public void testMultipleCidrs() {
      IpPermissions authorization = IpPermissions.permit(IpProtocol.TCP).originatingFromCidrBlocks(
            ImmutableSet.of("1.1.1.1/32", "1.1.1.2/32"));
      assertEquals(
            IpPermissions.buildFormParametersForIndex(0, authorization).toString(),
            "{IpPermissions.0.IpProtocol=[tcp], IpPermissions.0.FromPort=[1], IpPermissions.0.ToPort=[65535], IpPermissions.0.IpRanges.0.CidrIp=[1.1.1.1/32], IpPermissions.0.IpRanges.1.CidrIp=[1.1.1.2/32]}");
   }

   public void testProtocolFromAndToPortAndGroupIds() {
      IpPermissions authorization = IpPermissions.permit(IpProtocol.UDP).fromPort(11).to(53)
            .originatingFromSecurityGroupId("groupId");
      assertEquals(
            IpPermissions.buildFormParametersForIndex(0, authorization).toString(),
            "{IpPermissions.0.IpProtocol=[udp], IpPermissions.0.FromPort=[11], IpPermissions.0.ToPort=[53], IpPermissions.0.Groups.0.GroupId=[groupId]}");
   }

   public void testProtocolICMPAny() {
      IpPermissions authorization = IpPermissions.permitICMP().originatingFromSecurityGroupId("groupId");
      assertEquals(
            IpPermissions.buildFormParametersForIndex(0, authorization).toString(),
            "{IpPermissions.0.IpProtocol=[icmp], IpPermissions.0.FromPort=[-1], IpPermissions.0.ToPort=[-1], IpPermissions.0.Groups.0.GroupId=[groupId]}");
   }

   public void testProtocolICMPTypeAnyCode() {
      IpPermissions authorization = IpPermissions.permitICMP().type(8).originatingFromSecurityGroupId("groupId");
      assertEquals(
            IpPermissions.buildFormParametersForIndex(0, authorization).toString(),
            "{IpPermissions.0.IpProtocol=[icmp], IpPermissions.0.FromPort=[8], IpPermissions.0.ToPort=[-1], IpPermissions.0.Groups.0.GroupId=[groupId]}");
   }

   public void testProtocolICMPTypeCode() {
      IpPermissions authorization = IpPermissions.permitICMP().type(8).andCode(0)
            .originatingFromSecurityGroupId("groupId");
      assertEquals(
            IpPermissions.buildFormParametersForIndex(0, authorization).toString(),
            "{IpPermissions.0.IpProtocol=[icmp], IpPermissions.0.FromPort=[8], IpPermissions.0.ToPort=[0], IpPermissions.0.Groups.0.GroupId=[groupId]}");
   }

   public void testProtocolFromAndToPortAndUserGroups() {
      IpPermissions authorization = IpPermissions.permit(IpProtocol.ICMP).originatingFromUserAndSecurityGroup("userId",
            "groupId");
      assertEquals(
            IpPermissions.buildFormParametersForIndex(0, authorization).toString(),
            "{IpPermissions.0.IpProtocol=[icmp], IpPermissions.0.FromPort=[-1], IpPermissions.0.ToPort=[-1], IpPermissions.0.Groups.0.UserId=[userId], IpPermissions.0.Groups.1.GroupName=[groupId]}");
   }
}
