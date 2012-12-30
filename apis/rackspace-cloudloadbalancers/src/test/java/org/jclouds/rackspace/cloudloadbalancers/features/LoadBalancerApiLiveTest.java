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
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;
import java.util.logging.Logger;

import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancerAttributes;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancerRequest;
import org.jclouds.rackspace.cloudloadbalancers.domain.NodeRequest;
import org.jclouds.rackspace.cloudloadbalancers.domain.VirtualIP.Type;
import org.jclouds.rackspace.cloudloadbalancers.internal.BaseCloudLoadBalancersApiLiveTest;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "LoadBalancerApiLiveTest")
public class LoadBalancerApiLiveTest extends BaseCloudLoadBalancersApiLiveTest {
   private Set<LoadBalancer> lbs = Sets.newLinkedHashSet();

   @Override
   @AfterGroups(groups = "live")
   protected void tearDownContext() {
      for (LoadBalancer lb: lbs) {
         assertTrue(awaitAvailable(clbApi.getLoadBalancerApiForZone(lb.getRegion())).apply(lb));
         clbApi.getLoadBalancerApiForZone(lb.getRegion()).remove(lb.getId());
         assertTrue(awaitDeleted(clbApi.getLoadBalancerApiForZone(lb.getRegion())).apply(lb));
      }
      super.tearDownContext();
   }

   public void testCreateLoadBalancer() throws Exception {
      for (String zone: clbApi.getConfiguredZones()) {
         Logger.getAnonymousLogger().info("starting lb in region " + zone);
         
         LoadBalancer lb = clbApi.getLoadBalancerApiForZone(zone).create(
               LoadBalancerRequest.builder()
                     .name(prefix + "-" + zone)
                     .protocol("HTTP")
                     .port(80)
                     .virtualIPType(Type.PUBLIC)
                     .node(NodeRequest.builder()
                           .address("192.168.1.1")
                           .port(8080)
                           .build())
                     .build());
         checkLBInRegion(zone, lb, prefix + "-" + zone);
         
         assertEquals(lb.getStatus(), LoadBalancer.Status.BUILD);
         
         lbs.add(lb);
         
         assertTrue(awaitAvailable(clbApi.getLoadBalancerApiForZone(lb.getRegion())).apply(lb));

         LoadBalancer newLb = clbApi.getLoadBalancerApiForZone(zone).get(lb.getId());
         checkLBInRegion(zone, newLb, prefix + "-" + zone);
         
         assertEquals(newLb.getStatus(), LoadBalancer.Status.ACTIVE);
      }
   }

   @Test(dependsOnMethods = "testCreateLoadBalancer")
   public void testUpdateLoadBalancer() throws Exception {
      for (LoadBalancer lb: lbs) {
         clbApi.getLoadBalancerApiForZone(lb.getRegion()).update(lb.getId(),
               LoadBalancerAttributes.Builder.name("foo" + "-" + lb.getRegion()));
         
         assertTrue(awaitAvailable(clbApi.getLoadBalancerApiForZone(lb.getRegion())).apply(lb));

         LoadBalancer newLb = clbApi.getLoadBalancerApiForZone(lb.getRegion()).get(lb.getId());
         checkLBInRegion(newLb.getRegion(), newLb, "foo" + "-" + lb.getRegion());
         
         assertEquals(newLb.getStatus(), LoadBalancer.Status.ACTIVE);
      }
   }

   @Test(dependsOnMethods = "testUpdateLoadBalancer")
   public void testListLoadBalancers() throws Exception {
      for (String zone: clbApi.getConfiguredZones()) {

         Set<LoadBalancer> response = clbApi.getLoadBalancerApiForZone(zone).list().concat().toSet();
         
         assertNotNull(response);
         assertTrue(response.size() >= 0);
         
         for (LoadBalancer lb: response) {
            if (!lbs.contains(lb))
               continue;
            assert lb.getRegion() != null : lb;
            assert lb.getName() != null : lb;
            assert lb.getId() != -1 : lb;
            assert lb.getProtocol() != null : lb;
            assert lb.getPort() != -1 : lb;
            assert lb.getStatus() != null : lb;
            assert lb.getCreated() != null : lb;
            assert lb.getUpdated() != null : lb;
            assert lb.getVirtualIPs().size() > 0 : lb;
            // node info not available during list;
            assert lb.getNodes().size() == 0 : lb;

            LoadBalancer getDetails = clbApi.getLoadBalancerApiForZone(zone).get(lb.getId());
            
            try {
               assertEquals(getDetails.getRegion(), lb.getRegion());
               assertEquals(getDetails.getName(), lb.getName());
               assertEquals(getDetails.getId(), lb.getId());
               assertEquals(getDetails.getProtocol(), lb.getProtocol());
               assertEquals(getDetails.getPort(), lb.getPort());
               assertEquals(getDetails.getStatus(), lb.getStatus());
               assertEquals(getDetails.getCreated(), lb.getCreated());
               assertEquals(getDetails.getUpdated(), lb.getUpdated());
               assertEquals(getDetails.getVirtualIPs(), lb.getVirtualIPs());
               // node info not available during list;
               assert getDetails.getNodes().size() > 0 : lb;
            } catch (AssertionError e) {
               throw new AssertionError(String.format("%s\n%s - %s", e.getMessage(), getDetails, lb));
            }
         }
      }
   }

   private void checkLBInRegion(String region, LoadBalancer lb, String name) {
      assertEquals(lb.getRegion(), region);
      assertEquals(lb.getName(), name);
      assertEquals(lb.getProtocol(), "HTTP");
      assertEquals(lb.getPort(), Integer.valueOf(80));
      assertEquals(Iterables.get(lb.getVirtualIPs(), 0).getType(), Type.PUBLIC);
   }

}
