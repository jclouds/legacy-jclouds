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
import static org.jclouds.aws.ec2.options.DescribeAvailabilityZonesOptions.Builder.availabilityZones;
import static org.jclouds.aws.ec2.options.DescribeRegionsOptions.Builder.regions;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Map.Entry;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.EC2AsyncClient;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.EC2ContextFactory;
import org.jclouds.aws.ec2.domain.AvailabilityZone;
import org.jclouds.aws.ec2.domain.AvailabilityZoneInfo;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Tests behavior of {@code AvailabilityZoneAndRegionClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "ec2.AvailabilityZoneAndRegionClientLiveTest")
public class AvailabilityZoneAndRegionClientLiveTest {

   private AvailabilityZoneAndRegionClient client;
   private RestContext<EC2AsyncClient, EC2Client> context;

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      String user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");

      context = EC2ContextFactory.createContext(user, password, new Log4JLoggingModule());
      client = context.getApi().getAvailabilityZoneAndRegionServices();
   }

   public void testDescribeAvailabilityZones() {
      for (Region region : ImmutableSet.of(Region.DEFAULT, Region.EU_WEST_1, Region.US_EAST_1,
               Region.US_WEST_1)) {
         SortedSet<AvailabilityZoneInfo> allResults = Sets.newTreeSet(client
                  .describeAvailabilityZonesInRegion(region));
         assertNotNull(allResults);
         assert allResults.size() >= 2 : allResults.size();
         Iterator<AvailabilityZoneInfo> iterator = allResults.iterator();
         AvailabilityZone id1 = iterator.next().getZone();
         AvailabilityZone id2 = iterator.next().getZone();
         SortedSet<AvailabilityZoneInfo> twoResults = Sets.newTreeSet(client
                  .describeAvailabilityZonesInRegion(region, availabilityZones(id1, id2)));
         assertNotNull(twoResults);
         assertEquals(twoResults.size(), 2);
         iterator = twoResults.iterator();
         assertEquals(iterator.next().getZone(), id1);
         assertEquals(iterator.next().getZone(), id2);
      }
   }

   public void testDescribeRegions() {
      SortedMap<Region, URI> allResults = Maps.newTreeMap();
      allResults.putAll(client.describeRegions());
      assertNotNull(allResults);
      assert allResults.size() >= 2 : allResults.size();
      Iterator<Entry<Region, URI>> iterator = allResults.entrySet().iterator();
      Region r1 = iterator.next().getKey();
      Region r2 = iterator.next().getKey();
      SortedMap<Region, URI> twoResults = Maps.newTreeMap();
      twoResults.putAll(client.describeRegions(regions(r1, r2)));
      assertNotNull(twoResults);
      assertEquals(twoResults.size(), 2);
      iterator = twoResults.entrySet().iterator();
      assertEquals(iterator.next().getKey(), r1);
      assertEquals(iterator.next().getKey(), r2);
   }

   @AfterTest
   public void shutdown() {
      context.close();
   }
}
