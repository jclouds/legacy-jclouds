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
import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jclouds.rackspace.cloudloadbalancers.v1.domain.AccessRule;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.AccessRuleWithId;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.LoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.CreateLoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.AddNode;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.VirtualIP.Type;
import org.jclouds.rackspace.cloudloadbalancers.v1.internal.BaseCloudLoadBalancersApiLiveTest;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * @author Everett Toews
 */
@Test(groups = "live", singleThreaded = true, testName = "AccessListApiLiveTest")
public class AccessRuleApiLiveTest extends BaseCloudLoadBalancersApiLiveTest {
   private LoadBalancer lb;
   private String zone;
   private AccessRule accessRule1;
   private AccessRule accessRule2;
   private AccessRule accessRule3;
   private AccessRule accessRule4;
   private Map<String, AccessRule> accessRules;

   @Override
   @BeforeGroups(groups = { "live" })
   public void setup() {
      super.setup();

      accessRule1 = AccessRule.deny("206.160.163.21");
      accessRule2 = AccessRule.deny("206.160.165.11");
      accessRule3 = AccessRule.deny("206.160.163.22");
      accessRule4 = AccessRule.deny("206.160.168.22");
      
      accessRules = new HashMap<String, AccessRule>();
      accessRules.put(accessRule1.getAddress(), accessRule1);
      accessRules.put(accessRule2.getAddress(), accessRule2);
      accessRules.put(accessRule3.getAddress(), accessRule3);
      accessRules.put(accessRule4.getAddress(), accessRule4); 
   }

   public void testCreateLoadBalancer() {
      AddNode addNode = AddNode.builder().address("192.168.1.1").port(8080).build();
      CreateLoadBalancer createLB = CreateLoadBalancer.builder()
            .name(prefix+"-jclouds").protocol("HTTP").port(80).virtualIPType(Type.PUBLIC).node(addNode).build(); 

      zone = "ORD";//Iterables.getFirst(api.getConfiguredZones(), null);
      lb = api.getLoadBalancerApiForZone(zone).create(createLB);
      
      assertTrue(awaitAvailable(api.getLoadBalancerApiForZone(zone)).apply(lb));
   }

   @Test(dependsOnMethods = "testCreateLoadBalancer")
   public void testCreateAccessList() throws Exception {
      api.getAccessRuleApiForZoneAndLoadBalancer(zone, lb.getId()).create(accessRules.values());
      assertTrue(awaitAvailable(api.getLoadBalancerApiForZone(zone)).apply(lb));
      
      assertExpectedAccessRules(accessRules);
   }
   
   @Test(dependsOnMethods = "testCreateAccessList")
   public void testRemoveSingleAccessRule() throws Exception {
      Iterable<AccessRuleWithId> actualAccessList = api.getAccessRuleApiForZoneAndLoadBalancer(zone, lb.getId()).list();
      AccessRuleWithId removedAccessRule = Iterables.getFirst(actualAccessList, null);
      accessRules.remove(removedAccessRule.getAddress());
      
      assertTrue(api.getAccessRuleApiForZoneAndLoadBalancer(zone, lb.getId()).delete(removedAccessRule.getId()));
      assertTrue(awaitAvailable(api.getLoadBalancerApiForZone(zone)).apply(lb));
      
      assertExpectedAccessRules(accessRules);
   }
   
   @Test(dependsOnMethods = "testRemoveSingleAccessRule")
   public void testRemoveManyAccessRules() throws Exception {
      Iterable<AccessRuleWithId> actualAccessList = api.getAccessRuleApiForZoneAndLoadBalancer(zone, lb.getId()).list();
      AccessRuleWithId removedAccessRule1 = Iterables.getFirst(actualAccessList, null);
      AccessRuleWithId removedAccessRule2 = Iterables.getLast(actualAccessList);
      List<Integer> removedAccessRuleIds = ImmutableList.<Integer> of(removedAccessRule1.getId(), removedAccessRule2.getId());
      accessRules.remove(removedAccessRule1.getAddress());
      accessRules.remove(removedAccessRule2.getAddress());
      
      assertTrue(api.getAccessRuleApiForZoneAndLoadBalancer(zone, lb.getId()).delete(removedAccessRuleIds));
      assertTrue(awaitAvailable(api.getLoadBalancerApiForZone(zone)).apply(lb));
      
      assertExpectedAccessRules(accessRules);
   }
   
   @Test(dependsOnMethods = "testRemoveManyAccessRules")
   public void testRemoveAllAccessRules() throws Exception {
      assertTrue(api.getAccessRuleApiForZoneAndLoadBalancer(zone, lb.getId()).deleteAll());
      assertTrue(awaitAvailable(api.getLoadBalancerApiForZone(zone)).apply(lb));
      
      assertExpectedAccessRules(new HashMap<String, AccessRule>());
   }
   
   private void assertExpectedAccessRules(Map<String, AccessRule> expectedAccessList) {
      Iterable<AccessRuleWithId> actualAccessList = api.getAccessRuleApiForZoneAndLoadBalancer(zone, lb.getId()).list();
      
      for (AccessRule actualAccessRule: actualAccessList) {
         assertEquals(expectedAccessList.containsKey(actualAccessRule.getAddress()), true, 
               "The AccessRule " + actualAccessRule + " was not found in " + expectedAccessList);
      }
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
