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
package org.jclouds.ec2.compute.loaders;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.domain.RegionNameAndIngressRules;
import org.jclouds.ec2.domain.IpProtocol;
import org.jclouds.ec2.domain.SecurityGroup;
import org.jclouds.ec2.domain.UserIdGroupPair;
import org.jclouds.ec2.services.SecurityGroupClient;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", singleThreaded = true, testName = "CreateSecurityGroupIfNeeded")
public class CreateSecurityGroupIfNeededTest {

   @SuppressWarnings("unchecked")
   @Test
   public void testWhenPort22AndToItselfAuthorizesIngressTwice() throws ExecutionException {

      SecurityGroupClient client = createMock(SecurityGroupClient.class);
      Predicate<RegionAndName> tester = Predicates.alwaysTrue();

      SecurityGroup group = createNiceMock(SecurityGroup.class);
      Set<SecurityGroup> groups = ImmutableSet.<SecurityGroup> of(group);

      client.createSecurityGroupInRegion("region", "group", "group");
      client.authorizeSecurityGroupIngressInRegion("region", "group", IpProtocol.TCP, 22, 22, "0.0.0.0/0");
      expect(client.describeSecurityGroupsInRegion("region", "group")).andReturn(Set.class.cast(groups));
      expect(group.getOwnerId()).andReturn("ownerId");
      client.authorizeSecurityGroupIngressInRegion("region", "group", new UserIdGroupPair("ownerId", "group"));

      replay(client);
      replay(group);

      CreateSecurityGroupIfNeeded function = new CreateSecurityGroupIfNeeded(client, tester);

      assertEquals("group", function.load(new RegionNameAndIngressRules("region", "group", new int[] { 22 }, true)));

      verify(client);
      verify(group);

   }

   @Test
   public void testIllegalStateExceptionCreatingGroupJustReturns() throws ExecutionException {

      SecurityGroupClient client = createMock(SecurityGroupClient.class);
      Predicate<RegionAndName> tester = Predicates.alwaysTrue();

      client.createSecurityGroupInRegion("region", "group", "group");
      expectLastCall().andThrow(new IllegalStateException());

      replay(client);

      CreateSecurityGroupIfNeeded function = new CreateSecurityGroupIfNeeded(client, tester);

      assertEquals("group", function.load(new RegionNameAndIngressRules("region", "group", new int[] { 22 }, true)));

      verify(client);

   }

   @Test(expectedExceptions = RuntimeException.class)
   public void testWhenEventualConsistencyExpiresIllegalStateException() throws ExecutionException {

      SecurityGroupClient client = createMock(SecurityGroupClient.class);
      Predicate<RegionAndName> tester = Predicates.alwaysFalse();

      client.createSecurityGroupInRegion("region", "group", "group");

      replay(client);

      CreateSecurityGroupIfNeeded function = new CreateSecurityGroupIfNeeded(client, tester);
      function.load(new RegionNameAndIngressRules("region", "group", new int[] { 22 }, true));
   }
}
