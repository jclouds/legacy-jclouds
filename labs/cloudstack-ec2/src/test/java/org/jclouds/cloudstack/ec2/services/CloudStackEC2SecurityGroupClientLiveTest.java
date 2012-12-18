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
package org.jclouds.cloudstack.ec2.services;

import com.google.common.collect.Iterables;
import org.jclouds.ec2.domain.SecurityGroup;
import org.jclouds.ec2.services.SecurityGroupClientLiveTest;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "CloudStackEC2SecurityGroupClientLiveTest")
public class CloudStackEC2SecurityGroupClientLiveTest extends SecurityGroupClientLiveTest {
   private String regionId = "AmazonEC2";

   public CloudStackEC2SecurityGroupClientLiveTest() {
      provider = "cloudstack-ec2";
   }

   public static final String PREFIX = System.getProperty("user.name") + "-cloudstack-ec2";

   @Test
   void testDescribe() {
      Set<SecurityGroup> allResults = client.describeSecurityGroupsInRegion(regionId);
      assertNotNull(allResults);
      if (allResults.size() >= 1) {
         SecurityGroup group = Iterables.getLast(allResults);
         Set<SecurityGroup> result = client.describeSecurityGroupsInRegion(regionId, group.getName());
         assertNotNull(result);
         SecurityGroup compare = Iterables.getLast(result);
         assertEquals(compare, group);
      }
   }

   @Test
   void testCreateSecurityGroup() {
      String groupName = PREFIX + "1";
      cleanupAndSleep(groupName);
      try {
         String groupDescription = PREFIX + "1";
         client.createSecurityGroupInRegion(null, groupName, groupDescription);
         verifySecurityGroup(groupName, groupDescription);
      } finally {
         client.deleteSecurityGroupInRegion(null, groupName);
      }
   }

   private void verifySecurityGroup(String groupName, String description) {
      Set<SecurityGroup> oneResult = client.describeSecurityGroupsInRegion(null);
      assertNotNull(oneResult);
      assertEquals(oneResult.size(), 2);
      Iterator<SecurityGroup> sresult = oneResult.iterator();
      sresult.next();
      SecurityGroup listPair = sresult.next();
      assertEquals(listPair.getName(), groupName);
      assertEquals(listPair.getDescription(), description);
   }
}