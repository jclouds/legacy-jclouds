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
import static com.google.common.base.Preconditions.checkState;

import org.jclouds.collect.PaginatedIterable;
import org.jclouds.rds.domain.Subnet;
import org.jclouds.rds.domain.SubnetGroup;
import org.jclouds.rds.internal.BaseRDSClientLiveTest;
import org.jclouds.rds.options.ListSubnetGroupsOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "SubnetGroupClientLiveTest")
public class SubnetGroupClientLiveTest extends BaseRDSClientLiveTest {

   static void checkSubnetGroup(SubnetGroup subnetGroup) {
      checkNotNull(subnetGroup.getName(), "Name cannot be null for a SubnetGroup: %s", subnetGroup);
      checkNotNull(subnetGroup.getDescription(),  "Description cannot be null for a SubnetGroup: %s", subnetGroup);
      checkNotNull(subnetGroup.getStatus(),  "Status cannot be null for a SubnetGroup: %s", subnetGroup);
      checkState(subnetGroup.getSubnets().size() > 0, "Subnets must have at least one one: %s", subnetGroup);
      for (Subnet subnet : subnetGroup.getSubnets()) {
         checkSubnet(subnet);
      }
   }

   static void checkSubnet(Subnet subnet) {
      checkNotNull(subnet.getId(), "Id cannot be null for a SubnetGroup: %s", subnet);
      checkNotNull(subnet.getStatus(), "Status cannot be null for a SubnetGroup: %s", subnet);
      checkNotNull(subnet.getAvailabilityZone(), "AvailabilityZone cannot be null for a SubnetGroup: %s", subnet);
   }

   @Test
   protected void testDescribeSubnetGroups() {
      PaginatedIterable<SubnetGroup> response = client().list();

      for (SubnetGroup subnetGroup : response) {
         checkSubnetGroup(subnetGroup);
      }

      if (Iterables.size(response) > 0) {
         SubnetGroup subnetGroup = response.iterator().next();
         Assert.assertEquals(client().get(subnetGroup.getName()), subnetGroup);
      }

      // Test with a Marker, even if it's null
      response = client().list(ListSubnetGroupsOptions.Builder.afterMarker(response.getNextMarker()));
      for (SubnetGroup subnetGroup : response) {
         checkSubnetGroup(subnetGroup);
      }
   }

   protected SubnetGroupClient client() {
      return context.getApi().getSubnetGroupClient();
   }
}
