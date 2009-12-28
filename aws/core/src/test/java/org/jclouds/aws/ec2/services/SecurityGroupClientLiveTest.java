/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.aws.ec2.services;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.aws.ec2.EC2AsyncClient;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.EC2ContextFactory;
import org.jclouds.aws.ec2.domain.IpPermission;
import org.jclouds.aws.ec2.domain.IpProtocol;
import org.jclouds.aws.ec2.domain.Region;
import org.jclouds.aws.ec2.domain.SecurityGroup;
import org.jclouds.aws.ec2.domain.UserIdGroupPair;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

/**
 * Tests behavior of {@code SecurityGroupClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "ec2.SecurityGroupClientLiveTest")
public class SecurityGroupClientLiveTest {

   private SecurityGroupClient client;
   private RestContext<EC2AsyncClient, EC2Client> context;

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      String user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");

      context = EC2ContextFactory.createContext(user, password, new Log4JLoggingModule());
      client = context.getApi().getSecurityGroupServices();
   }

   @Test
   void testDescribe() {
      for (Region region : ImmutableSet.of(Region.DEFAULT, Region.EU_WEST_1, Region.US_EAST_1,
               Region.US_WEST_1)) {
         SortedSet<SecurityGroup> allResults = Sets.newTreeSet(client
                  .describeSecurityGroupsInRegion(region));
         assertNotNull(allResults);
         if (allResults.size() >= 1) {
            SecurityGroup group = allResults.last();
            SortedSet<SecurityGroup> result = Sets.newTreeSet(client
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
      String groupDescription = PREFIX + "1 description";
      try {
         client.deleteSecurityGroupInRegion(Region.DEFAULT, groupName);
      } catch (Exception e) {

      }
      client.deleteSecurityGroupInRegion(Region.DEFAULT, groupName);

      client.createSecurityGroupInRegion(Region.DEFAULT, groupName, groupDescription);

      verifySecurityGroup(groupName, groupDescription);
   }

   @Test
   void testAuthorizeSecurityGroupIngressCidr() throws InterruptedException, ExecutionException,
            TimeoutException {
      String groupName = PREFIX + "ingress";

      try {
         client.deleteSecurityGroupInRegion(Region.DEFAULT, groupName);
      } catch (Exception e) {
      }

      client.createSecurityGroupInRegion(Region.DEFAULT, groupName, groupName);
      client.authorizeSecurityGroupIngressInRegion(Region.DEFAULT, groupName, IpProtocol.TCP, 80,
               80, "0.0.0.0/0");
      assertEventually(new GroupHasPermission(client, groupName, new IpPermission(80, 80, Sets
               .<UserIdGroupPair> newTreeSet(), IpProtocol.TCP, ImmutableSortedSet.of("0.0.0.0/0"))));

      client.revokeSecurityGroupIngressInRegion(Region.DEFAULT, groupName, IpProtocol.TCP, 80, 80,
               "0.0.0.0/0");
      assertEventually(new GroupHasNoPermissions(client, groupName));

   }

   private void verifySecurityGroup(String groupName, String description) {
      SortedSet<SecurityGroup> oneResult = client.describeSecurityGroupsInRegion(Region.DEFAULT,
               groupName);
      assertNotNull(oneResult);
      assertEquals(oneResult.size(), 1);
      SecurityGroup listPair = oneResult.iterator().next();
      assertEquals(listPair.getName(), groupName);
      assertEquals(listPair.getDescription(), description);
   }

   @Test(enabled = false)
   // TODO
   void testAuthorizeSecurityGroupIngressSourceGroup() throws InterruptedException {
      String group1Name = PREFIX + "ingress1";
      String group2Name = PREFIX + "ingress2";

      try {
         client.deleteSecurityGroupInRegion(Region.DEFAULT, group1Name);
      } catch (Exception e) {

      }
      try {
         client.deleteSecurityGroupInRegion(Region.DEFAULT, group2Name);
      } catch (Exception e) {

      }

      client.createSecurityGroupInRegion(Region.DEFAULT, group1Name, group1Name);
      client.createSecurityGroupInRegion(Region.DEFAULT, group2Name, group2Name);
      ensureGroupsExist(group1Name, group2Name);
      client.authorizeSecurityGroupIngressInRegion(Region.DEFAULT, group1Name, IpProtocol.TCP, 80,
               80, "0.0.0.0/0");
      assertEventually(new GroupHasPermission(client, group2Name, new IpPermission(80, 80, Sets
               .<UserIdGroupPair> newTreeSet(), IpProtocol.TCP, ImmutableSortedSet.of("0.0.0.0/0"))));

      SortedSet<SecurityGroup> oneResult = client.describeSecurityGroupsInRegion(Region.DEFAULT,
               group1Name);
      assertNotNull(oneResult);
      assertEquals(oneResult.size(), 1);
      SecurityGroup group = oneResult.iterator().next();
      assertEquals(group.getName(), group1Name);

      client.authorizeSecurityGroupIngressInRegion(Region.DEFAULT, group2Name, new UserIdGroupPair(
               group.getOwnerId(), group1Name));
      assertEventually(new GroupHasPermission(client, group2Name, new IpPermission(80, 80, Sets
               .<UserIdGroupPair> newTreeSet(), IpProtocol.TCP, ImmutableSortedSet.of("0.0.0.0/0"))));

      client.revokeSecurityGroupIngressInRegion(Region.DEFAULT, group2Name, new UserIdGroupPair(
               group.getOwnerId(), group1Name));
      assertEventually(new GroupHasNoPermissions(client, group2Name));
   }

   private static final class GroupHasPermission implements Runnable {
      private final SecurityGroupClient client;
      private final String group;
      private final IpPermission permission;

      private GroupHasPermission(SecurityGroupClient client, String group, IpPermission permission) {
         this.client = client;
         this.group = group;
         this.permission = permission;
      }

      public void run() {
         try {
            SortedSet<SecurityGroup> oneResult = client.describeSecurityGroupsInRegion(
                     Region.DEFAULT, group);
            assertNotNull(oneResult);
            assertEquals(oneResult.size(), 1);
            SecurityGroup listPair = oneResult.iterator().next();
            assert listPair.getIpPermissions().contains(permission);
         } catch (Exception e) {
            throw new AssertionError(e);
         }
      }
   }

   private static final class GroupHasNoPermissions implements Runnable {
      private final SecurityGroupClient client;
      private final String group;

      private GroupHasNoPermissions(SecurityGroupClient client, String group) {
         this.client = client;
         this.group = group;
      }

      public void run() {
         try {
            Set<SecurityGroup> oneResult = client.describeSecurityGroupsInRegion(Region.DEFAULT,
                     group);
            assertNotNull(oneResult);
            assertEquals(oneResult.size(), 1);
            SecurityGroup listPair = oneResult.iterator().next();
            assertEquals(listPair.getIpPermissions().size(), 0);
         } catch (Exception e) {
            throw new AssertionError(e);
         }
      }
   }

   private void ensureGroupsExist(String group1Name, String group2Name) {
      SortedSet<SecurityGroup> twoResults = client.describeSecurityGroupsInRegion(Region.DEFAULT,
               group1Name, group2Name);
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
    * Due to eventual consistency, container commands may not return correctly immediately. Hence,
    * we will try up to the inconsistency window to see if the assertion completes.
    */
   protected static void assertEventually(Runnable assertion) throws InterruptedException {
      long start = System.currentTimeMillis();
      AssertionError error = null;
      for (int i = 0; i < 30; i++) {
         try {
            assertion.run();
            if (i > 0)
               System.err.printf("%d attempts and %dms asserting %s%n", i + 1, System
                        .currentTimeMillis()
                        - start, assertion.getClass().getSimpleName());
            return;
         } catch (AssertionError e) {
            error = e;
         }
         Thread.sleep(INCONSISTENCY_WINDOW / 30);
      }
      if (error != null)
         throw error;

   }

   public static final String PREFIX = System.getProperty("user.name") + "-ec2";

   @AfterTest
   public void shutdown() {
      context.close();
   }
}
