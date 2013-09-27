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
package org.jclouds.aws.ec2.compute.loaders;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.jclouds.aws.ec2.features.AWSSecurityGroupApi;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.domain.RegionNameAndIngressRules;
import org.jclouds.ec2.compute.functions.EC2SecurityGroupIdFromName;
import org.jclouds.ec2.domain.SecurityGroup;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 * @author Andrew Bayer
 */
@Test(groups = "unit", singleThreaded = true, testName = "AWSEC2CreateSecurityGroupIfNeeded")
public class AWSEC2CreateSecurityGroupIfNeededTest {

   @SuppressWarnings("unchecked")
   @Test
   public void testWhenPort22AndToItselfAuthorizesIngressOnce() throws ExecutionException {

      AWSSecurityGroupApi client = createMock(AWSSecurityGroupApi.class);
      Predicate<RegionAndName> tester = Predicates.alwaysTrue();

      SecurityGroup group = createNiceMock(SecurityGroup.class);
      Set<SecurityGroup> groups = ImmutableSet.<SecurityGroup> of(group);

      EC2SecurityGroupIdFromName groupIdFromName = createMock(EC2SecurityGroupIdFromName.class);

      ImmutableSet.Builder<IpPermission> permissions = ImmutableSet.builder();

      permissions.add(IpPermission.builder()
                      .fromPort(22)
                      .toPort(22)
                      .ipProtocol(IpProtocol.TCP)
                      .cidrBlock("0.0.0.0/0")
                      .build());

      permissions.add(IpPermission.builder()
                      .fromPort(0)
                      .toPort(65535)
                      .ipProtocol(IpProtocol.TCP)
                      .tenantIdGroupNamePair("ownerId", "sg-123456")
                      .build());
      permissions.add(IpPermission.builder()
                      .fromPort(0)
                      .toPort(65535)
                      .ipProtocol(IpProtocol.UDP)
                      .tenantIdGroupNamePair("ownerId", "sg-123456")
                      .build());
      
      client.createSecurityGroupInRegion("region", "group", "group");
      expect(group.getOwnerId()).andReturn("ownerId");
      expect(groupIdFromName.apply("region/group")).andReturn("sg-123456");
      client.authorizeSecurityGroupIngressInRegion("region", "sg-123456", permissions.build());
      expect(client.describeSecurityGroupsInRegion("region", "group")).andReturn(Set.class.cast(groups));


      replay(client);
      replay(group);
      replay(groupIdFromName);

      AWSEC2CreateSecurityGroupIfNeeded function = new AWSEC2CreateSecurityGroupIfNeeded(client, groupIdFromName, tester);

      assertEquals("group", function.load(new RegionNameAndIngressRules("region", "group", new int[] { 22 }, true)));

      verify(client);
      verify(group);
      verify(groupIdFromName);

   }
}
