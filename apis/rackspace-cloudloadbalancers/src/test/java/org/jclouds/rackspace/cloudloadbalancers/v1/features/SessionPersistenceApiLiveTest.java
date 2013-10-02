/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.rackspace.cloudloadbalancers.v1.features;

import static org.jclouds.rackspace.cloudloadbalancers.v1.predicates.LoadBalancerPredicates.awaitAvailable;
import static org.jclouds.rackspace.cloudloadbalancers.v1.predicates.LoadBalancerPredicates.awaitDeleted;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.rackspace.cloudloadbalancers.v1.domain.LoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.CreateLoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.AddNode;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.SessionPersistence;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.VirtualIP.Type;
import org.jclouds.rackspace.cloudloadbalancers.v1.internal.BaseCloudLoadBalancersApiLiveTest;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * @author Everett Toews
 */
@Test(groups = "live", singleThreaded = true, testName = "SessionPersistenceApiLiveTest")
public class SessionPersistenceApiLiveTest extends BaseCloudLoadBalancersApiLiveTest {
   private LoadBalancer lb;
   private String zone;

   public void testCreateLoadBalancer() {
      AddNode addNode = AddNode.builder().address("192.168.1.1").port(8080).build();
      CreateLoadBalancer createLB = CreateLoadBalancer.builder()
            .name(prefix+"-jclouds").protocol("HTTP").port(80).virtualIPType(Type.PUBLIC).node(addNode).build(); 

      zone = Iterables.getFirst(api.getConfiguredZones(), null);
      lb = api.getLoadBalancerApiForZone(zone).create(createLB);
      
      assertTrue(awaitAvailable(api.getLoadBalancerApiForZone(zone)).apply(lb));
   }

   @Test(dependsOnMethods = "testCreateLoadBalancer")
   public void testCreateAndGetSessionPersistence() throws Exception {
      api.getSessionPersistenceApiForZoneAndLoadBalancer(zone, lb.getId()).create(SessionPersistence.HTTP_COOKIE);
      assertTrue(awaitAvailable(api.getLoadBalancerApiForZone(zone)).apply(lb));
      
      SessionPersistence sessionPersistence = 
            api.getSessionPersistenceApiForZoneAndLoadBalancer(zone, lb.getId()).get();

      assertEquals(sessionPersistence, SessionPersistence.HTTP_COOKIE);
   }
   
   @Test(dependsOnMethods = "testCreateAndGetSessionPersistence")
   public void testRemoveAndGetSessionPersistence() throws Exception {
      api.getSessionPersistenceApiForZoneAndLoadBalancer(zone, lb.getId()).delete();
      assertTrue(awaitAvailable(api.getLoadBalancerApiForZone(zone)).apply(lb));
      
      SessionPersistence sessionPersistence = 
            api.getSessionPersistenceApiForZoneAndLoadBalancer(zone, lb.getId()).get();
      
      assertNull(sessionPersistence);
   }

   @Override
   @AfterGroups(groups = "live")
   protected void tearDown() {
      assertTrue(awaitAvailable(api.getLoadBalancerApiForZone(zone)).apply(lb));
      api.getLoadBalancerApiForZone(zone).delete(lb.getId());
      assertTrue(awaitDeleted(api.getLoadBalancerApiForZone(zone)).apply(lb));
      super.tearDown();
   }
}
