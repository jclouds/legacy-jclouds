/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.elb;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertNotNull;

import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.jclouds.Constants;
import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.domain.AvailabilityZone;
import org.jclouds.elb.domain.LoadBalancer;
import org.jclouds.loadbalancer.LoadBalancerServiceContextFactory;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.Module;

/**
 * Tests behavior of {@code ELBClient}
 * 
 * @author Lili Nader
 */
@Test(groups = "live", sequential = true)
public class ELBClientLiveTest {

   private ELBClient client;
   private RestContext<ELBClient, ELBAsyncClient> context;
   protected String provider = "elb";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;

   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = checkNotNull(System.getProperty("test." + provider + ".credential"), "test." + provider
            + ".credential");
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
      context = new LoadBalancerServiceContextFactory().createContext(provider,
            ImmutableSet.<Module> of(new Log4JLoggingModule()), overrides).getProviderSpecificContext();
      client = context.getApi();
   }

   @Test
   void testCreateLoadBalancer() {
      String name = "TestLoadBalancer";
      for (Entry<String, String> regionZone : ImmutableMap.<String, String> of(Region.US_EAST_1,
            AvailabilityZone.US_EAST_1A, Region.US_WEST_1, AvailabilityZone.US_WEST_1A, Region.EU_WEST_1,
            AvailabilityZone.EU_WEST_1A, Region.AP_SOUTHEAST_1, AvailabilityZone.AP_SOUTHEAST_1A).entrySet()) {
         String dnsName = client.createLoadBalancerInRegion(regionZone.getKey(), name, "http", 80, 80,
               regionZone.getValue());
         assertNotNull(dnsName);
         assert (dnsName.startsWith(name));
      }
   }

   @Test(dependsOnMethods = "testCreateLoadBalancer")
   void testDescribeLoadBalancers() {
      for (String region : Lists.newArrayList(null, Region.EU_WEST_1, Region.US_EAST_1, Region.US_WEST_1,
            Region.AP_SOUTHEAST_1)) {
         Set<? extends LoadBalancer> allResults = client.describeLoadBalancersInRegion(region);
         assertNotNull(allResults);
         assert (allResults.size() >= 1) : region;
      }
   }

   @Test
   void testDeleteLoadBalancer() {
      for (String region : Lists.newArrayList(null, Region.EU_WEST_1, Region.US_EAST_1, Region.US_WEST_1,
            Region.AP_SOUTHEAST_1)) {
         client.deleteLoadBalancerInRegion(region, "TestLoadBalancer");
      }
   }

   @AfterTest
   public void shutdown() {
      context.close();
   }
}
