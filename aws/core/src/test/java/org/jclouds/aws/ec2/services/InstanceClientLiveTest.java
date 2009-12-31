/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.aws.ec2.services;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Iterator;
import java.util.SortedSet;

import org.jclouds.aws.ec2.EC2ContextFactory;
import org.jclouds.aws.ec2.domain.Region;
import org.jclouds.aws.ec2.domain.Reservation;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Tests behavior of {@code EC2Client}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "ec2.InstanceClientLiveTest")
public class InstanceClientLiveTest {
   public static final String PREFIX = System.getProperty("user.name") + "-ec2";

   private InstanceClient client;
   private String user;

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");

      client = EC2ContextFactory.createContext(user, password, new Log4JLoggingModule()).getApi()
               .getInstanceServices();
   }

   @Test
   void testDescribeInstances() {
      for (Region region : ImmutableSet.of(Region.DEFAULT, Region.EU_WEST_1, Region.US_EAST_1,
               Region.US_WEST_1)) {
         SortedSet<Reservation> allResults = Sets.newTreeSet(client
                  .describeInstancesInRegion(region));
         assertNotNull(allResults);
         assert allResults.size() >= 0 : allResults.size();
         if (allResults.size() >= 2) {
            Iterator<Reservation> iterator = allResults.iterator();
            String id1 = Sets.newTreeSet(iterator.next().getRunningInstances()).first()
                     .getId();
            String id2 = Sets.newTreeSet(iterator.next().getRunningInstances()).first()
                     .getId();
            SortedSet<Reservation> twoResults = Sets.newTreeSet(client.describeInstancesInRegion(
                     region, id1, id2));
            assertNotNull(twoResults);
            assertEquals(twoResults.size(), 2);
            iterator = allResults.iterator();
            assertEquals(Sets.newTreeSet(iterator.next().getRunningInstances()).first()
                     .getId(), id1);
            assertEquals(Sets.newTreeSet(iterator.next().getRunningInstances()).first()
                     .getId(), id2);
         }
      }
   }

}
