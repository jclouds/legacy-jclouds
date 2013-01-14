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
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.logging.Logger;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.rds.domain.Authorization;
import org.jclouds.rds.domain.Authorization.Status;
import org.jclouds.rds.domain.EC2SecurityGroup;
import org.jclouds.rds.domain.SecurityGroup;
import org.jclouds.rds.internal.BaseRDSApiLiveTest;
import org.jclouds.rds.options.ListSecurityGroupsOptions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "SecurityGroupApiLiveTest")
public class SecurityGroupApiLiveTest extends BaseRDSApiLiveTest {
   public static final String SECURITYGROUP = (System.getProperty("user.name") + "-jclouds-securityGroup")
            .toLowerCase();

   private Predicate<SecurityGroup> ipRangesAuthorized;
   private Predicate<SecurityGroup> ipRangesRevoked;

   @BeforeClass(groups = "live")
   @Override
   public void setupContext() {
      super.setupContext();
      ipRangesAuthorized = retry(new Predicate<SecurityGroup>() {
         public boolean apply(SecurityGroup input) {
            return Iterables.all(api().get(input.getName()).getIPRanges(), new Predicate<Authorization>() {
               public boolean apply(Authorization i2) {
                  return i2.getStatus() == Status.AUTHORIZED;
               }
            });
         }
      }, 30000, 100, 500, MILLISECONDS);
      ipRangesRevoked = retry(new Predicate<SecurityGroup>() {
         public boolean apply(SecurityGroup input) {
            return api().get(input.getName()).getIPRanges().size() == 0;
         }
      }, 30000, 100, 500, MILLISECONDS);
   }

   private SecurityGroup securityGroup;

   public void testCreateSecurityGroup() {

      SecurityGroup newSecurityGroup = api().createWithNameAndDescription(SECURITYGROUP, "jclouds");

      securityGroup = newSecurityGroup;
      Logger.getAnonymousLogger().info("created securityGroup: " + securityGroup);

      assertEquals(securityGroup.getName(), SECURITYGROUP);
      assertEquals(securityGroup.getDescription(), "jclouds");

      checkSecurityGroup(newSecurityGroup);

   }

   @Test(dependsOnMethods = "testCreateSecurityGroup")
   protected void testAuthorizeIPRange() {
      securityGroup = api().authorizeIngressToIPRange(SECURITYGROUP, "0.0.0.0/0");

      assertTrue(ipRangesAuthorized.apply(securityGroup), securityGroup.toString());
      securityGroup = api().get(securityGroup.getName());
      Logger.getAnonymousLogger().info("ip range authorized: " + securityGroup);
   }

   @Test(dependsOnMethods = "testAuthorizeIPRange")
   protected void testRevokeIPRange() {
      securityGroup = api().revokeIngressFromIPRange(SECURITYGROUP, "0.0.0.0/0");

      assertTrue(ipRangesRevoked.apply(securityGroup), securityGroup.toString());
      securityGroup = api().get(securityGroup.getName());
      Logger.getAnonymousLogger().info("ip range revoked: " + securityGroup);
   }

   @Test(dependsOnMethods = "testRevokeIPRange")
   public void testDeleteSecurityGroup() {
      api().delete(securityGroup.getName());
      // TODO block and determine the state of a deleted securityGroup
      Logger.getAnonymousLogger().info("securityGroup deleted: " + securityGroup);
   }

   @Override
   @AfterClass(groups = "live")
   protected void tearDownContext() {
      api().delete(SECURITYGROUP);
      super.tearDownContext();
   }

   static void checkSecurityGroup(SecurityGroup securityGroup) {
      checkNotNull(securityGroup.getName(), "Name cannot be null for a SecurityGroup: %s", securityGroup);
      checkNotNull(securityGroup.getDescription(), "Description cannot be null for a SecurityGroup: %s", securityGroup);
      checkNotNull(securityGroup.getOwnerId(), "OwnerId cannot be null for a SecurityGroup: %s", securityGroup);
      checkNotNull(securityGroup.getVpcId(), "VpcId cannot be null for a SecurityGroup: %s", securityGroup);
      for (EC2SecurityGroup security : securityGroup.getEC2SecurityGroups()) {
         checkEC2SecurityGroup(security);
      }
   }

   static void checkEC2SecurityGroup(EC2SecurityGroup security) {
      checkNotNull(security.getId(), "Id can be null for a SecurityGroup, but its Optional Wrapper cannot: %s",
               security);
      checkNotNull(security.getStatus(), "Status cannot be null for a SecurityGroup: %s", security);
      checkNotNull(security.getName(), "Name cannot be null for a SecurityGroup: %s", security);
      checkNotNull(security.getOwnerId(), "Name cannot be null for a SecurityGroup: %s", security);
   }

   @Test
   protected void testDescribeSecurityGroups() {
      IterableWithMarker<SecurityGroup> response = api().list().get(0);

      for (SecurityGroup securityGroup : response) {
         checkSecurityGroup(securityGroup);
      }

      if (Iterables.size(response) > 0) {
         SecurityGroup securityGroup = response.iterator().next();
         Assert.assertEquals(api().get(securityGroup.getName()), securityGroup);
      }

      // Test with a Marker, even if it's null
      response = api().list(ListSecurityGroupsOptions.Builder.afterMarker(response.nextMarker().orNull()));
      for (SecurityGroup securityGroup : response) {
         checkSecurityGroup(securityGroup);
      }
   }

   protected SecurityGroupApi api() {
      return context.getApi().getSecurityGroupApi();
   }
}
