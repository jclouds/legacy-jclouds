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
package org.jclouds.aws.ec2;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.aws.ec2.options.DescribeImagesOptions.Builder.imageIds;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.aws.ec2.domain.Image;
import org.jclouds.aws.ec2.domain.IpPermission;
import org.jclouds.aws.ec2.domain.IpProtocol;
import org.jclouds.aws.ec2.domain.KeyPair;
import org.jclouds.aws.ec2.domain.Reservation;
import org.jclouds.aws.ec2.domain.SecurityGroup;
import org.jclouds.aws.ec2.domain.UserIdGroupPair;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

/**
 * Tests behavior of {@code EC2Client}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "ec2.EC2ClientLiveTest")
public class EC2ClientLiveTest {

   private EC2Client client;
   private String user;

   @BeforeGroups(groups = { "live" })
   public void setupClient() throws InterruptedException, ExecutionException, TimeoutException {
      user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");

      client = EC2ContextFactory.createContext(user, password, new Log4JLoggingModule()).getApi();
   }

   @Test
   void testDescribeImages() throws InterruptedException, ExecutionException, TimeoutException {
      SortedSet<Image> allResults = client.describeImages().get(30, TimeUnit.SECONDS);
      assertNotNull(allResults);
      assert allResults.size() >= 2 : allResults.size();
      Iterator<Image> iterator = allResults.iterator();
      String id1 = iterator.next().getImageId();
      String id2 = iterator.next().getImageId();
      SortedSet<Image> twoResults = client.describeImages(imageIds(id1, id2)).get(30,
               TimeUnit.SECONDS);
      assertNotNull(twoResults);
      assertEquals(twoResults.size(), 2);
      iterator = twoResults.iterator();
      assertEquals(iterator.next().getImageId(), id1);
      assertEquals(iterator.next().getImageId(), id2);
   }

   @Test
   void testDescribeInstances() throws InterruptedException, ExecutionException, TimeoutException {
      SortedSet<Reservation> allResults = client.describeInstances().get(30, TimeUnit.SECONDS);
      assertNotNull(allResults);
      assert allResults.size() >= 0 : allResults.size();
      if (allResults.size() >= 2) {
         Iterator<Reservation> iterator = allResults.iterator();
         String id1 = iterator.next().getRunningInstances().first().getInstanceId();
         String id2 = iterator.next().getRunningInstances().first().getInstanceId();
         SortedSet<Reservation> twoResults = client.describeInstances(id1, id2).get(30,
                  TimeUnit.SECONDS);
         assertNotNull(twoResults);
         assertEquals(twoResults.size(), 2);
         iterator = allResults.iterator();
         assertEquals(iterator.next().getRunningInstances().first().getInstanceId(), id1);
         assertEquals(iterator.next().getRunningInstances().first().getInstanceId(), id2);
      }
   }

   @Test
   void testDescribeKeyPairs() throws InterruptedException, ExecutionException, TimeoutException {
      SortedSet<KeyPair> allResults = client.describeKeyPairs().get(30, TimeUnit.SECONDS);
      assertNotNull(allResults);
      assert allResults.size() >= 0 : allResults.size();
      if (allResults.size() >= 2) {
         Iterator<KeyPair> iterator = allResults.iterator();
         String id1 = iterator.next().getKeyName();
         String id2 = iterator.next().getKeyName();
         SortedSet<KeyPair> twoResults = client.describeKeyPairs(id1, id2)
                  .get(30, TimeUnit.SECONDS);
         assertNotNull(twoResults);
         assertEquals(twoResults.size(), 2);
         iterator = twoResults.iterator();
         assertEquals(iterator.next().getKeyName(), id1);
         assertEquals(iterator.next().getKeyName(), id2);
      }
   }

   @Test
   void testDescribeSecurityGroups() throws InterruptedException, ExecutionException,
            TimeoutException {
      SortedSet<SecurityGroup> allResults = client.describeSecurityGroups().get(30,
               TimeUnit.SECONDS);
      assertNotNull(allResults);
      assert allResults.size() >= 0 : allResults.size();
      if (allResults.size() >= 2) {
         Iterator<SecurityGroup> iterator = allResults.iterator();
         String id1 = iterator.next().getName();
         String id2 = iterator.next().getName();
         SortedSet<SecurityGroup> twoResults = client.describeSecurityGroups(id1, id2).get(30,
                  TimeUnit.SECONDS);
         assertNotNull(twoResults);
         assertEquals(twoResults.size(), 2);
         iterator = twoResults.iterator();
         assertEquals(iterator.next().getName(), id1);
         assertEquals(iterator.next().getName(), id2);
      }
   }

   public static final String PREFIX = System.getProperty("user.name") + "-ec2";

   @Test
   void testCreateKeyPair() throws InterruptedException, ExecutionException, TimeoutException {
      String keyName = PREFIX + "1";
      try {
         client.deleteKeyPair(keyName).get(30, TimeUnit.SECONDS);
      } catch (Exception e) {

      }
      client.deleteKeyPair(keyName).get(30, TimeUnit.SECONDS);

      KeyPair result = client.createKeyPair(keyName).get(30, TimeUnit.SECONDS);
      assertNotNull(result);
      assertNotNull(result.getKeyMaterial());
      assertNotNull(result.getKeyFingerprint());
      assertEquals(result.getKeyName(), keyName);

      SortedSet<KeyPair> twoResults = client.describeKeyPairs(keyName).get(30, TimeUnit.SECONDS);
      assertNotNull(twoResults);
      assertEquals(twoResults.size(), 1);
      KeyPair listPair = twoResults.iterator().next();
      assertEquals(listPair.getKeyName(), result.getKeyName());
      assertEquals(listPair.getKeyFingerprint(), result.getKeyFingerprint());
   }

   @Test
   void testCreateSecurityGroup() throws InterruptedException, ExecutionException, TimeoutException {
      String groupName = PREFIX + "1";
      String groupDescription = PREFIX + "1 description";
      try {
         client.deleteSecurityGroup(groupName).get(30, TimeUnit.SECONDS);
      } catch (Exception e) {

      }
      client.deleteSecurityGroup(groupName).get(30, TimeUnit.SECONDS);

      client.createSecurityGroup(groupName, groupDescription).get(30, TimeUnit.SECONDS);

      verifySecurityGroup(groupName, groupDescription);
   }

   @Test
   void testAuthorizeSecurityGroupIngressCidr() throws InterruptedException, ExecutionException,
            TimeoutException {
      String groupName = PREFIX + "ingress";

      try {
         client.deleteSecurityGroup(groupName).get(30, TimeUnit.SECONDS);
      } catch (Exception e) {
      }

      client.createSecurityGroup(groupName, groupName).get(30, TimeUnit.SECONDS);
      client.authorizeSecurityGroupIngress(groupName, IpProtocol.TCP, 80, 80, "0.0.0.0/0").get(30,
               TimeUnit.SECONDS);
      assertEventually(new GroupHasPermission(client, groupName, new IpPermission(80, 80, Sets
               .<UserIdGroupPair> newTreeSet(), IpProtocol.TCP, ImmutableSortedSet.of("0.0.0.0/0"))));

      client.revokeSecurityGroupIngress(groupName, IpProtocol.TCP, 80, 80, "0.0.0.0/0").get(30,
               TimeUnit.SECONDS);
      assertEventually(new GroupHasNoPermissions(client, groupName));

   }

   private void verifySecurityGroup(String groupName, String description)
            throws InterruptedException, ExecutionException, TimeoutException {
      SortedSet<SecurityGroup> oneResult = client.describeSecurityGroups(groupName).get(30,
               TimeUnit.SECONDS);
      assertNotNull(oneResult);
      assertEquals(oneResult.size(), 1);
      SecurityGroup listPair = oneResult.iterator().next();
      assertEquals(listPair.getName(), groupName);
      assertEquals(listPair.getDescription(), description);
   }

   @Test(enabled = false)
   // TODO
   void testAuthorizeSecurityGroupIngressSourceGroup() throws InterruptedException,
            ExecutionException, TimeoutException {
      String group1Name = PREFIX + "ingress1";
      String group2Name = PREFIX + "ingress2";

      try {
         client.deleteSecurityGroup(group1Name).get(30, TimeUnit.SECONDS);
      } catch (Exception e) {

      }
      try {
         client.deleteSecurityGroup(group2Name).get(30, TimeUnit.SECONDS);
      } catch (Exception e) {

      }

      client.createSecurityGroup(group1Name, group1Name).get(30, TimeUnit.SECONDS);
      client.createSecurityGroup(group2Name, group2Name).get(30, TimeUnit.SECONDS);
      ensureGroupsExist(group1Name, group2Name);
      client.authorizeSecurityGroupIngress(group1Name, IpProtocol.TCP, 80, 80, "0.0.0.0/0").get(30,
               TimeUnit.SECONDS);
      assertEventually(new GroupHasPermission(client, group2Name, new IpPermission(80, 80, Sets
               .<UserIdGroupPair> newTreeSet(), IpProtocol.TCP, ImmutableSortedSet.of("0.0.0.0/0"))));

      SortedSet<SecurityGroup> oneResult = client.describeSecurityGroups(group1Name).get(30,
               TimeUnit.SECONDS);
      assertNotNull(oneResult);
      assertEquals(oneResult.size(), 1);
      SecurityGroup group = oneResult.iterator().next();
      assertEquals(group.getName(), group1Name);

      client.authorizeSecurityGroupIngress(group2Name,
               new UserIdGroupPair(group.getOwnerId(), group1Name)).get(30, TimeUnit.SECONDS);
      assertEventually(new GroupHasPermission(client, group2Name, new IpPermission(80, 80, Sets
               .<UserIdGroupPair> newTreeSet(), IpProtocol.TCP, ImmutableSortedSet.of("0.0.0.0/0"))));

      client.revokeSecurityGroupIngress(group2Name,
               new UserIdGroupPair(group.getOwnerId(), group1Name)).get(30, TimeUnit.SECONDS);
      assertEventually(new GroupHasNoPermissions(client, group2Name));
   }

   private static final class GroupHasPermission implements Runnable {
      private final EC2Client client;
      private final String group;
      private final IpPermission permission;

      private GroupHasPermission(EC2Client client, String group, IpPermission permission) {
         this.client = client;
         this.group = group;
         this.permission = permission;
      }

      public void run() {
         try {
            SortedSet<SecurityGroup> oneResult = client.describeSecurityGroups(group).get(30,
                     TimeUnit.SECONDS);
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
      private final EC2Client client;
      private final String group;

      private GroupHasNoPermissions(EC2Client client, String group) {
         this.client = client;
         this.group = group;
      }

      public void run() {
         try {
            SortedSet<SecurityGroup> oneResult = client.describeSecurityGroups(group).get(30,
                     TimeUnit.SECONDS);
            assertNotNull(oneResult);
            assertEquals(oneResult.size(), 1);
            SecurityGroup listPair = oneResult.iterator().next();
            assertEquals(listPair.getIpPermissions().size(), 0);
         } catch (Exception e) {
            throw new AssertionError(e);
         }
      }
   }

   private void ensureGroupsExist(String group1Name, String group2Name)
            throws InterruptedException, ExecutionException, TimeoutException {
      SortedSet<SecurityGroup> twoResults = client.describeSecurityGroups(group1Name, group2Name)
               .get(30, TimeUnit.SECONDS);
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

}
