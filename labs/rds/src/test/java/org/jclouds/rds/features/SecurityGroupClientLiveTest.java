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

import org.jclouds.collect.PaginatedIterable;
import org.jclouds.rds.domain.EC2SecurityGroup;
import org.jclouds.rds.domain.SecurityGroup;
import org.jclouds.rds.internal.BaseRDSClientLiveTest;
import org.jclouds.rds.options.ListSecurityGroupsOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "SecurityGroupClientLiveTest")
public class SecurityGroupClientLiveTest extends BaseRDSClientLiveTest {

   static void checkSecurityGroup(SecurityGroup securityGroup) {
      checkNotNull(securityGroup.getName(), "Name cannot be null for a SecurityGroup: %s", securityGroup);
      checkNotNull(securityGroup.getDescription(),  "Description cannot be null for a SecurityGroup: %s", securityGroup);
      checkNotNull(securityGroup.getOwnerId(),  "OwnerId cannot be null for a SecurityGroup: %s", securityGroup);
      checkNotNull(securityGroup.getVpcId(),  "VpcId cannot be null for a SecurityGroup: %s", securityGroup);
      for (EC2SecurityGroup security : securityGroup.getEC2SecurityGroups()) {
         checkEC2SecurityGroup(security);
      }
   }

   static void checkEC2SecurityGroup(EC2SecurityGroup security) {
      checkNotNull(security.getId(), "Id can be null for a SecurityGroup, but its Optional Wrapper cannot: %s", security);
      checkNotNull(security.getStatus(), "Status cannot be null for a SecurityGroup: %s", security);
      checkNotNull(security.getName(), "Name cannot be null for a SecurityGroup: %s", security);
      checkNotNull(security.getOwnerId(), "Name cannot be null for a SecurityGroup: %s", security);
   }

   @Test
   protected void testDescribeSecurityGroups() {
      PaginatedIterable<SecurityGroup> response = client().list();

      for (SecurityGroup securityGroup : response) {
         checkSecurityGroup(securityGroup);
      }

      if (Iterables.size(response) > 0) {
         SecurityGroup securityGroup = response.iterator().next();
         Assert.assertEquals(client().get(securityGroup.getName()), securityGroup);
      }

      // Test with a Marker, even if it's null
      response = client().list(ListSecurityGroupsOptions.Builder.afterMarker(response.getNextMarker()));
      for (SecurityGroup securityGroup : response) {
         checkSecurityGroup(securityGroup);
      }
   }

   protected SecurityGroupClient client() {
      return context.getApi().getSecurityGroupClient();
   }
}
