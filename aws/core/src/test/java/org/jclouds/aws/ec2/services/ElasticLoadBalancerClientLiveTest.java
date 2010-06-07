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
import static org.testng.Assert.assertNotNull;

import java.util.Set;
import java.util.Map.Entry;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.EC2AsyncClient;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.EC2ContextFactory;
import org.jclouds.aws.ec2.domain.AvailabilityZone;
import org.jclouds.aws.ec2.domain.ElasticLoadBalancer;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.internal.Lists;

/**
 * Tests behavior of {@code ElasticIPAddressClient}
 * 
 * @author Lili Nader
 */
@Test(groups = "live", sequential = true, testName = "ec2.ElasticLoadBalancerClientLiveTest")
public class ElasticLoadBalancerClientLiveTest {

   private ElasticLoadBalancerClient client;
   private RestContext<EC2Client, EC2AsyncClient> context;

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      String user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");

      context = EC2ContextFactory.createContext(user, password, new Log4JLoggingModule())
               .getProviderSpecificContext();
      client = context.getApi().getElasticLoadBalancerServices();
   }

   @Test
   void testCreateLoadBalancer() {
      String name = "TestLoadBalancer";
      for (Entry<String, String> regionZone : ImmutableMap.<String, String> of(Region.US_EAST_1,
               AvailabilityZone.US_EAST_1A, Region.US_WEST_1, AvailabilityZone.US_WEST_1A,
               Region.EU_WEST_1, AvailabilityZone.EU_WEST_1A, Region.AP_SOUTHEAST_1,
               AvailabilityZone.AP_SOUTHEAST_1A).entrySet()) {
         String dnsName = client.createLoadBalancerInRegion(regionZone.getKey(), name, "http", 80, 80,
                  regionZone.getValue());
         assertNotNull(dnsName);
         assert (dnsName.startsWith(name));
      }
   }

   @Test(dependsOnMethods = "testCreateLoadBalancer")
   void testDescribeLoadBalancers() {
      String name = "TestDescribeLoadBalancer";
      for (String region : Lists.newArrayList(null, Region.EU_WEST_1, Region.US_EAST_1,
               Region.US_WEST_1, Region.AP_SOUTHEAST_1)) {
         Set<ElasticLoadBalancer> allResults = client.describeLoadBalancersInRegion(region, name);
         assertNotNull(allResults);
         assert (allResults.size() >= 1);
      }
   }

   @Test
   void testDeleteLoadBalancer() {
      for (String region : Lists.newArrayList(null, Region.EU_WEST_1, Region.US_EAST_1,
               Region.US_WEST_1, Region.AP_SOUTHEAST_1)) {
         client.deleteLoadBalancerInRegion(region, "TestLoadBalancer");
      }
   }

   @AfterTest
   public void shutdown() {
      context.close();
   }
}
