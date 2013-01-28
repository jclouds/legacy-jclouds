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
package org.jclouds.rackspace.cloudloadbalancers.features;

import static org.jclouds.rackspace.cloudloadbalancers.predicates.LoadBalancerPredicates.awaitAvailable;
import static org.jclouds.rackspace.cloudloadbalancers.predicates.LoadBalancerPredicates.awaitDeleted;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.rackspace.cloudloadbalancers.domain.SSLTermination;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancerRequest;
import org.jclouds.rackspace.cloudloadbalancers.domain.NodeRequest;
import org.jclouds.rackspace.cloudloadbalancers.domain.VirtualIP.Type;
import org.jclouds.rackspace.cloudloadbalancers.internal.BaseCloudLoadBalancersApiLiveTest;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * @author Everett Toews
 */
@Test(groups = "live", singleThreaded = true, testName = "SSLTerminationApiLiveTest")
public class SSLTerminationApiLiveTest extends BaseCloudLoadBalancersApiLiveTest {
   private LoadBalancer lb;
   private String zone;

   public void testCreateLoadBalancer() {
      NodeRequest nodeRequest = NodeRequest.builder().address("192.168.1.1").port(8080).build();
      LoadBalancerRequest lbRequest = LoadBalancerRequest.builder()
            .name(prefix+"-jclouds").protocol("HTTP").port(80).virtualIPType(Type.PUBLIC).node(nodeRequest).build(); 

      zone = Iterables.getFirst(clbApi.getConfiguredZones(), null);
      lb = clbApi.getLoadBalancerApiForZone(zone).create(lbRequest);
      
      assertTrue(awaitAvailable(clbApi.getLoadBalancerApiForZone(zone)).apply(lb));
   }

   @Test(dependsOnMethods = "testCreateLoadBalancer")
   public void testCreateAndGetSSLTermination() throws Exception {
      clbApi.getSSLTerminationApiForZoneAndLoadBalancer(zone, lb.getId()).createOrUpdate(
            SSLTerminationApiExpectTest.getSSLTermination());
      assertTrue(awaitAvailable(clbApi.getLoadBalancerApiForZone(zone)).apply(lb));
      
      SSLTermination sslTermination = 
            clbApi.getSSLTerminationApiForZoneAndLoadBalancer(zone, lb.getId()).get();

      assertEquals(sslTermination, SSLTerminationApiExpectTest.getSSLTermination());
   }
   
   @Test(dependsOnMethods = "testCreateAndGetSSLTermination")
   public void testRemoveAndGetSSLTermination() throws Exception {
      assertTrue(clbApi.getSSLTerminationApiForZoneAndLoadBalancer(zone, lb.getId()).delete());
      assertTrue(awaitAvailable(clbApi.getLoadBalancerApiForZone(zone)).apply(lb));
      
      SSLTermination sslTermination = 
            clbApi.getSSLTerminationApiForZoneAndLoadBalancer(zone, lb.getId()).get();
      
      assertNull(sslTermination);
   }

   @Override
   @AfterGroups(groups = "live")
   protected void tearDownContext() {
      assertTrue(awaitAvailable(clbApi.getLoadBalancerApiForZone(zone)).apply(lb));
      clbApi.getLoadBalancerApiForZone(zone).delete(lb.getId());
      assertTrue(awaitDeleted(clbApi.getLoadBalancerApiForZone(zone)).apply(lb));
      super.tearDownContext();
   }
}
