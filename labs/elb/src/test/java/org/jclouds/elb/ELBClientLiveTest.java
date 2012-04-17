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

import java.util.Set;

import org.jclouds.apis.BaseContextLiveTest;
import org.jclouds.elb.domain.LoadBalancer;
import org.jclouds.rest.RestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

/**
 * Tests behavior of {@code ELBClient}
 * 
 * @author Lili Nader
 */
@Test(groups = "live", singleThreaded = true, testName = "ELBClientLiveTest")
public class ELBClientLiveTest<S extends ELBClient, A extends ELBAsyncClient> extends BaseContextLiveTest<RestContext<S, A>>  {

   public ELBClientLiveTest() {
      provider = "elb";
   }

   protected S client;
   protected String name = "TestLoadBalancer";

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
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

   @AfterClass(groups = { "integration", "live" })
   protected void tearDownContext() {
      try {
         testDeleteLoadBalancer();
      } finally {
         super.tearDownContext();
      }
   }

   @SuppressWarnings({ "serial", "unchecked" })
   @Override
   protected TypeToken<RestContext<S, A>> contextType() {
      return new TypeToken<RestContext<S, A>>() {
      }.where(new TypeParameter<S>() {
      }, (TypeToken) TypeToken.of(ELBClient.class)).where(new TypeParameter<A>() {
      }, (TypeToken) TypeToken.of(ELBAsyncClient.class));
   }
}
