/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.ec2.services;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.ec2.options.DescribeAvailabilityZonesOptions.Builder.availabilityZones;
import static org.jclouds.ec2.options.DescribeRegionsOptions.Builder.regions;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;

import org.jclouds.Constants;
import org.jclouds.aws.domain.Region;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.ec2.EC2AsyncClient;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.domain.AvailabilityZoneInfo;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Module;

/**
 * Tests behavior of {@code AvailabilityZoneAndRegionClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true)
public class AvailabilityZoneAndRegionClientLiveTest {

   private AvailabilityZoneAndRegionClient client;
   private RestContext<EC2Client, EC2AsyncClient> context;
   protected String provider = "ec2";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;

   @BeforeClass
   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = System.getProperty("test." + provider + ".credential");
      endpoint = System.getProperty("test." + provider + ".endpoint");
      apiversion = System.getProperty("test." + provider + ".apiversion");
   }

   protected Properties setupProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      overrides.setProperty(provider + ".identity", identity);
      if (credential != null)
         overrides.setProperty(provider + ".credential", credential);
      if (endpoint != null)
         overrides.setProperty(provider + ".endpoint", endpoint);
      if (apiversion != null)
         overrides.setProperty(provider + ".apiversion", apiversion);
      return overrides;
   }

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      setupCredentials();
      Properties overrides = setupProperties();
      context = new ComputeServiceContextFactory().createContext(provider,
            ImmutableSet.<Module> of(new Log4JLoggingModule()), overrides).getProviderSpecificContext();
      client = context.getApi().getAvailabilityZoneAndRegionServices();
   }

   public void testDescribeAvailabilityZones() {
      for (String region : Lists.newArrayList(null, Region.EU_WEST_1, Region.US_EAST_1, Region.US_WEST_1,
            Region.AP_SOUTHEAST_1)) {
         Set<AvailabilityZoneInfo> allResults = client.describeAvailabilityZonesInRegion(region);
         assertNotNull(allResults);
         assert allResults.size() >= 2 : allResults.size();
         Iterator<AvailabilityZoneInfo> iterator = allResults.iterator();
         String id1 = iterator.next().getZone();
         String id2 = iterator.next().getZone();
         Set<AvailabilityZoneInfo> twoResults = client.describeAvailabilityZonesInRegion(region,
               availabilityZones(id1, id2));
         assertNotNull(twoResults);
         assertEquals(twoResults.size(), 2);
         iterator = allResults.iterator();
         assertEquals(iterator.next().getZone(), id1);
         assertEquals(iterator.next().getZone(), id2);
      }
   }

   public void testDescribeRegions() {
      SortedMap<String, URI> allResults = Maps.newTreeMap();
      allResults.putAll(client.describeRegions());
      assertNotNull(allResults);
      assert allResults.size() >= 2 : allResults.size();
      Iterator<Entry<String, URI>> iterator = allResults.entrySet().iterator();
      String r1 = iterator.next().getKey();
      String r2 = iterator.next().getKey();
      SortedMap<String, URI> twoResults = Maps.newTreeMap();
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
