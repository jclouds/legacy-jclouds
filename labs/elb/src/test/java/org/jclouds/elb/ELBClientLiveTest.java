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
package org.jclouds.elb;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertNotNull;

import java.util.Properties;
import java.util.Set;

import org.jclouds.elb.domain.LoadBalancer;
import org.jclouds.loadbalancer.LoadBalancerServiceContextFactory;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.BaseRestClientLiveTest;
import org.jclouds.rest.RestContext;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Tests behavior of {@code ELBClient}
 * 
 * @author Lili Nader
 */
@Test(groups = "live", singleThreaded = true, testName = "ELBClientLiveTest")
public class ELBClientLiveTest extends BaseRestClientLiveTest {

   public ELBClientLiveTest() {
      provider = "elb";
   }

   private ELBClient client;
   private RestContext<ELBClient, ELBAsyncClient> context;

   protected String name = "TestLoadBalancer";

   @BeforeGroups(groups = "live")
   public void setupClient() {
      setupCredentials();
      Properties overrides = setupProperties();
      context = new LoadBalancerServiceContextFactory().createContext(provider,
               ImmutableSet.<Module> of(new Log4JLoggingModule()), overrides).getProviderSpecificContext();
      client = context.getApi();
   }

   @Test
   public void testCreateLoadBalancer() {
      createLoadBalancerInRegionZone(null, checkNotNull(System.getProperty("test." + provider + ".zone"), "test."
               + provider + ".zone"), name);
   }

   protected void createLoadBalancerInRegionZone(String region, String zone, String name) {
      String dnsName = client.createLoadBalancerInRegion(region, name, "http", 80, 80, zone);
      assertNotNull(dnsName);
      assert (dnsName.startsWith(name));
   }

   @Test(dependsOnMethods = "testCreateLoadBalancer")
   public void testDescribeLoadBalancers() {
      describeLoadBalancerInRegion(null);
   }

   protected void describeLoadBalancerInRegion(String region) {
      Set<? extends LoadBalancer> allResults = client.describeLoadBalancersInRegion(region);
      assertNotNull(allResults);
      assert (allResults.size() >= 1) : region;
   }

   @Test(dependsOnMethods = "testDescribeLoadBalancers")
   public void testDeleteLoadBalancer() {
      deleteLoadBalancerInRegion(null);
   }

   protected void deleteLoadBalancerInRegion(String region) {
      client.deleteLoadBalancerInRegion(region, name);
   }

   @AfterGroups(groups = "live")
   public void shutdown() {
      try {
         testDeleteLoadBalancer();
      } finally {
         context.close();
      }
   }
}
