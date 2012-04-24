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
package org.jclouds.ec2.services;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

import org.jclouds.aws.domain.Region;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.ec2.EC2ApiMetadata;
import org.jclouds.ec2.domain.IpPermission;
import org.jclouds.ec2.domain.IpProtocol;
import org.jclouds.ec2.domain.SecurityGroup;
import org.jclouds.ec2.domain.UserIdGroupPair;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Tests behavior of {@code SecurityGroupClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "SecurityGroupClientLiveTest")
public class SecurityGroupClientLiveTest extends BaseComputeServiceContextLiveTest {
   public SecurityGroupClientLiveTest() {
      provider = "ec2";
   }

   protected SecurityGroupClient client;

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      client = view.unwrap(EC2ApiMetadata.CONTEXT_TOKEN).getApi().getSecurityGroupServices();
   }

   @Test
   void testDescribe() {
      for (String region : Lists.newArrayList(null, Region.EU_WEST_1, Region.US_EAST_1, Region.US_WEST_1,
            Region.AP_SOUTHEAST_1, Region.AP_NORTHEAST_1)) {
         SortedSet<SecurityGroup> allResults = ImmutableSortedSet.<SecurityGroup> copyOf(client
               .describeSecurityGroupsInRegion(region));
         assertNotNull(allResults);
         if (allResults.size() >= 1) {
            SecurityGroup group = allResults.last();
            SortedSet<SecurityGroup> result = ImmutableSortedSet.<SecurityGroup> copyOf(client
                  .describeSecurityGroupsInRegion(region, group.getName()));
            assertNotNull(result);
            SecurityGroup compare = result.last();
            assertEquals(compare, group);
         }
      }
   }

   @Test
   void testCreateSecurityGroup() {
      String groupName = PREFIX + "1";
      cleanupAndSleep(groupName);
      try {
         String groupDescription = PREFIX + "1 description";
         client.deleteSecurityGroupInRegion(null, groupName);
         client.createSecurityGroupInRegion(null, groupName, groupDescription);
         verifySecurityGroup(groupName, groupDescription);
      } finally {
         client.deleteSecurityGroupInRegion(null, groupName);
      }
   }

   protected void cleanupAndSleep(String groupName) {
      try {
         client.deleteSecurityGroupInRegion(null, groupName);
         Thread.sleep(2000);
      } catch (Exception e) {

      }
   }

   @Test
   void testAuthorizeSecurityGroupIngressCidr() {
      String groupName = PREFIX + "ingress";
      cleanupAndSleep(groupName);
      try {
         client.createSecurityGroupInRegion(null, groupName, groupName);
         client.authorizeSecurityGroupIngressInRegion(null, groupName, IpProtocol.TCP, 80, 80, "0.0.0.0/0");
         assertEventually(new GroupHasPermission(client, groupName, new TCPPort80AllIPs()));

         client.revokeSecurityGroupIngressInRegion(null, groupName, IpProtocol.TCP, 80, 80, "0.0.0.0/0");
         assertEventually(new GroupHasNoPermissions(client, groupName));
      } finally {
         client.deleteSecurityGroupInRegion(null, groupName);
      }
   }

   @Test
   void testAuthorizeSecurityGroupIngressSourcePort() {
      String groupName = PREFIX + "ingress";
      cleanupAndSleep(groupName);
      try {
         client.createSecurityGroupInRegion(null, groupName, groupName);
         client.authorizeSecurityGroupIngressInRegion(null, groupName, IpProtocol.TCP, 80, 80, "0.0.0.0/0");
         assertEventually(new GroupHasPermission(client, groupName, new TCPPort80AllIPs()));

         client.revokeSecurityGroupIngressInRegion(null, groupName, IpProtocol.TCP, 80, 80, "0.0.0.0/0");
         assertEventually(new GroupHasNoPermissions(client, groupName));
      } finally {
         client.deleteSecurityGroupInRegion(null, groupName);
      }
   }

   private void verifySecurityGroup(String groupName, String description) {
      Set<SecurityGroup> oneResult = client.describeSecurityGroupsInRegion(null, groupName);
      assertNotNull(oneResult);
      assertEquals(oneResult.size(), 1);
      SecurityGroup listPair = oneResult.iterator().next();
      assertEquals(listPair.getName(), groupName);
      assertEquals(listPair.getDescription(), description);
   }

   @Test
   void testAuthorizeSecurityGroupIngressSourceGroup() {
      final String group1Name = PREFIX + "ingress1";
      String group2Name = PREFIX + "ingress2";
      cleanupAndSleep(group2Name);
      cleanupAndSleep(group1Name);
      try {
         client.createSecurityGroupInRegion(null, group1Name, group1Name);
         client.createSecurityGroupInRegion(null, group2Name, group2Name);
         ensureGroupsExist(group1Name, group2Name);
         client.authorizeSecurityGroupIngressInRegion(null, group1Name, IpProtocol.TCP, 80, 80, "0.0.0.0/0");
         assertEventually(new GroupHasPermission(client, group1Name, new TCPPort80AllIPs()));
         Set<SecurityGroup> oneResult = client.describeSecurityGroupsInRegion(null, group1Name);
         assertNotNull(oneResult);
         assertEquals(oneResult.size(), 1);
         final SecurityGroup group = oneResult.iterator().next();
         assertEquals(group.getName(), group1Name);
         final UserIdGroupPair to = new UserIdGroupPair(group.getOwnerId(), group1Name);
         client.authorizeSecurityGroupIngressInRegion(null, group2Name, to);
         assertEventually(new GroupHasPermission(client, group2Name, new Predicate<IpPermission>() {
            @Override
            public boolean apply(IpPermission arg0) {
               return arg0.getUserIdGroupPairs().equals(ImmutableMultimap.of(group.getOwnerId(), group1Name));
            }
         }));

         client.revokeSecurityGroupIngressInRegion(null, group2Name,
               new UserIdGroupPair(group.getOwnerId(), group1Name));
         assertEventually(new GroupHasNoPermissions(client, group2Name));
      } finally {
         client.deleteSecurityGroupInRegion(null, group2Name);
         client.deleteSecurityGroupInRegion(null, group1Name);
      }
   }

   public final class TCPPort80AllIPs implements Predicate<IpPermission> {
      @Override
      public boolean apply(IpPermission arg0) {
         return arg0.getIpProtocol() == IpProtocol.TCP && arg0.getFromPort() == 80 && arg0.getToPort() == 80
               && arg0.getIpRanges().equals(ImmutableSet.of("0.0.0.0/0"));
      }
   }

   public static final class GroupHasPermission implements Runnable {
      private final SecurityGroupClient client;
      private final String group;
      private final Predicate<IpPermission> permission;

      public GroupHasPermission(SecurityGroupClient client, String group, Predicate<IpPermission> permission) {
         this.client = client;
         this.group = group;
         this.permission = permission;
      }

      public void run() {
         try {
            Set<SecurityGroup> oneResult = client.describeSecurityGroupsInRegion(null, group);
            assert Iterables.all(Iterables.getOnlyElement(oneResult).getIpPermissions(), permission) : permission
                  + ": " + oneResult;
         } catch (Exception e) {
            throw new AssertionError(e);
         }
      }
   }

   public static final class GroupHasNoPermissions implements Runnable {
      private final SecurityGroupClient client;
      private final String group;

      public GroupHasNoPermissions(SecurityGroupClient client, String group) {
         this.client = client;
         this.group = group;
      }

      public void run() {
         try {
            Set<SecurityGroup> oneResult = client.describeSecurityGroupsInRegion(null, group);
            assertNotNull(oneResult);
            assertEquals(oneResult.size(), 1);
            SecurityGroup listPair = oneResult.iterator().next();
            assertEquals(listPair.getIpPermissions().size(), 0);
         } catch (Exception e) {
            throw new AssertionError(e);
         }
      }
   }

   protected void ensureGroupsExist(String group1Name, String group2Name) {
      SortedSet<SecurityGroup> twoResults = ImmutableSortedSet.copyOf(client.describeSecurityGroupsInRegion(null,
            group1Name, group2Name));
      assertNotNull(twoResults);
      assertEquals(twoResults.size(), 2);
      Iterator<SecurityGroup> iterator = twoResults.iterator();
      SecurityGroup listPair1 = iterator.next();
      assertEquals(listPair1.getName(), group1Name);
      assertEquals(listPair1.getDescription(), group1Name);

      SecurityGroup listPair2 = iterator.next();
      assertEquals(listPair2.getName(), group2Name);
      assertEquals(listPair2.getDescription(), group2Name);
   }

   private static final int INCONSISTENCY_WINDOW = 5000;

   /**
    * Due to eventual consistency, container commands may not return correctly
    * immediately. Hence, we will try up to the inconsistency window to see if
    * the assertion completes.
    */
   protected static void assertEventually(Runnable assertion) {
      long start = System.currentTimeMillis();
      AssertionError error = null;
      for (int i = 0; i < 30; i++) {
         try {
            assertion.run();
            if (i > 0)
               System.err.printf("%d attempts and %dms asserting %s%n", i + 1, System.currentTimeMillis() - start,
                     assertion.getClass().getSimpleName());
            return;
         } catch (AssertionError e) {
            error = e;
         }
         try {
            Thread.sleep(INCONSISTENCY_WINDOW / 30);
         } catch (InterruptedException e) {
         }
      }
      if (error != null)
         throw error;

   }

   public static final String PREFIX = System.getProperty("user.name") + "-ec2";

   @AfterTest
   public void shutdown() {
      view.close();
   }
}
