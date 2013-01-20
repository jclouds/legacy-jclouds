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
package org.jclouds.rds.features;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

import java.util.logging.Logger;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rds.domain.Authorization;
import org.jclouds.rds.domain.Authorization.Status;
import org.jclouds.rds.domain.Instance;
import org.jclouds.rds.domain.InstanceRequest;
import org.jclouds.rds.domain.SecurityGroup;
import org.jclouds.rds.internal.BaseRDSApiLiveTest;
import org.jclouds.rds.options.ListInstancesOptions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.net.HostAndPort;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "InstanceApiLiveTest")
public class InstanceApiLiveTest extends BaseRDSApiLiveTest {
   public static final String INSTANCE = (System.getProperty("user.name") + "-jclouds-instance").toLowerCase();

   private Predicate<HostAndPort> socketTester;
   private Predicate<Instance> instanceAvailable;
   private Predicate<Instance> instanceGone;

   private SecurityGroup securityGroup;

   @BeforeClass(groups = "live")
   @Override
   public void setupContext() {
      super.setupContext();
      securityGroup = createSecurityGroupAndAuthorizeIngressToAll(INSTANCE);
      SocketOpen socketOpen = context.utils().injector().getInstance(SocketOpen.class);
      socketTester = retry(socketOpen, 180, 1, 1, SECONDS);
      instanceAvailable = retry(new Predicate<Instance>() {
         public boolean apply(Instance input) {
            return api().get(input.getId()).getStatus() == Instance.Status.AVAILABLE;
         }
      }, 600, 5, 5, SECONDS);
      instanceGone = retry(new Predicate<Instance>() {
         public boolean apply(Instance input) {
            return api().get(input.getId()) == null;
         }
      }, 600, 5, 5, SECONDS);
   }

   private SecurityGroup createSecurityGroupAndAuthorizeIngressToAll(String name) {
      Predicate<SecurityGroup> ipRangesAuthorized = retry(
               new Predicate<SecurityGroup>() {
                  public boolean apply(SecurityGroup input) {
                     return Iterables.all(sgApi().get(input.getName()).getIPRanges(), new Predicate<Authorization>() {
                        public boolean apply(Authorization i2) {
                           return i2.getStatus() == Status.AUTHORIZED;
                        }
                     });
                  }
               }, 30000, 100, 500, MILLISECONDS);

      try {
         SecurityGroup securityGroup = sgApi().createWithNameAndDescription(name, "jclouds");

         Logger.getAnonymousLogger().info("created securityGroup: " + securityGroup);

         // we could look up our IP address alternatively
         securityGroup = sgApi().authorizeIngressToIPRange(name, "0.0.0.0/0");

         assertTrue(ipRangesAuthorized.apply(securityGroup), securityGroup.toString());

         securityGroup = sgApi().get(securityGroup.getName());
         Logger.getAnonymousLogger().info("ip range authorized: " + securityGroup);
         return securityGroup;
      } catch (RuntimeException e) {
         sgApi().delete(name);
         throw e;
      }
   }

   private Instance instance;

   public void testCreateInstance() {

      Instance newInstance = api().create(
               INSTANCE,
               InstanceRequest.builder()
                              .instanceClass("db.t1.micro")
                              .allocatedStorageGB(5)
                              .securityGroup(securityGroup.getName())
                              .name("jclouds")
                              .engine("mysql")
                              .masterUsername("master").masterPassword("Password01")
                              .backupRetentionPeriod(0).build());

      instance = newInstance;
      Logger.getAnonymousLogger().info("created instance: " + instance);

      assertEquals(instance.getId(), INSTANCE);
      assertEquals(instance.getName().get(), "jclouds");

      checkInstance(newInstance);

      assertTrue(instanceAvailable.apply(newInstance), newInstance.toString());
      instance = api().get(newInstance.getId());
      Logger.getAnonymousLogger().info("instance available: " + instance);

   }

   @Test(dependsOnMethods = "testCreateInstance")
   protected void testPortResponds() {
      assertTrue(socketTester.apply(instance.getEndpoint().get()), instance.toString());
      Logger.getAnonymousLogger().info("instance reachable on: " + instance.getEndpoint().get());
   }

   @Test(dependsOnMethods = "testPortResponds")
   public void testDeleteInstance() {
      instance = api().delete(instance.getId());
      assertTrue(instanceGone.apply(instance), instance.toString());
      Logger.getAnonymousLogger().info("instance deleted: " + instance);
   }

   @Override
   @AfterClass(groups = "live")
   protected void tearDownContext() {
      try {
         api().delete(INSTANCE);
      } finally {
         sgApi().delete(INSTANCE);
      }
      super.tearDownContext();
   }

   private void checkInstance(Instance instance) {
      checkNotNull(instance.getId(), "Id cannot be null for a Instance: %s", instance);
      checkNotNull(instance.getStatus(), "Status cannot be null for a Instance: %s", instance);
      assertNotEquals(instance.getStatus(), Instance.Status.UNRECOGNIZED,
               "Status cannot be UNRECOGNIZED for a Instance: " + instance);
      checkNotNull(instance.getCreatedTime(), "CreatedTime cannot be null for a Instance: %s", instance);
      checkNotNull(instance.getName(), "While Name can be null for a Instance, its Optional wrapper cannot: %s",
               instance);

      checkNotNull(instance.getSubnetGroup(),
               "While SubnetGroup can be null for a Instance, its Optional wrapper cannot: %s", instance);
      // TODO: other checks
      if (instance.getSubnetGroup().isPresent())
         SubnetGroupApiLiveTest.checkSubnetGroup(instance.getSubnetGroup().get());
   }

   @Test
   protected void testDescribeInstances() {
      IterableWithMarker<Instance> response = api().list().get(0);

      for (Instance instance : response) {
         checkInstance(instance);
      }

      if (Iterables.size(response) > 0) {
         Instance instance = response.iterator().next();
         Assert.assertEquals(api().get(instance.getId()), instance);
      }

      // Test with a Marker, even if it's null
      response = api().list(ListInstancesOptions.Builder.afterMarker(response.nextMarker().orNull()));
      for (Instance instance : response) {
         checkInstance(instance);
      }
   }

   protected InstanceApi api() {
      return context.getApi().getInstanceApi();
   }

   protected SecurityGroupApi sgApi() {
      return context.getApi().getSecurityGroupApi();
   }
}
