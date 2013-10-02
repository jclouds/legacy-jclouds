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
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.jclouds.rackspace.cloudloadbalancers.v1.domain.LoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.UpdateLoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.CreateLoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.Metadata;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.AddNode;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.VirtualIP.Type;
import org.jclouds.rackspace.cloudloadbalancers.v1.internal.BaseCloudLoadBalancersApiLiveTest;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
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
   protected void tearDown() {
      for (LoadBalancer lb: lbs) {
         assertTrue(awaitAvailable(api.getLoadBalancerApiForZone(lb.getRegion())).apply(lb));
         api.getLoadBalancerApiForZone(lb.getRegion()).delete(lb.getId());
         assertTrue(awaitDeleted(api.getLoadBalancerApiForZone(lb.getRegion())).apply(lb));
      }
      super.tearDown();
   }

   public void testCreateLoadBalancer() throws Exception {
      for (String zone: api.getConfiguredZones()) {
         Logger.getAnonymousLogger().info("starting lb in region " + zone);
         
         LoadBalancer lb = api.getLoadBalancerApiForZone(zone).create(
               CreateLoadBalancer.builder()
                     .name(prefix + "-" + zone)
                     .protocol("HTTP")
                     .port(80)
                     .virtualIPType(Type.PUBLIC)
                     .node(AddNode.builder()
                           .address("192.168.1.1")
                           .port(8080)
                           .build())
                     .build());
         checkLBInRegion(zone, lb, prefix + "-" + zone);
         
         assertEquals(lb.getStatus(), LoadBalancer.Status.BUILD);
         
         lbs.add(lb);
         
         assertTrue(awaitAvailable(api.getLoadBalancerApiForZone(lb.getRegion())).apply(lb));

         LoadBalancer newLb = api.getLoadBalancerApiForZone(zone).get(lb.getId());
         checkLBInRegion(zone, newLb, prefix + "-" + zone);
         
         assertEquals(newLb.getStatus(), LoadBalancer.Status.ACTIVE);
      }
   }

   @Test(dependsOnMethods = "testCreateLoadBalancer")
   public void testUpdateLoadBalancer() throws Exception {
      for (LoadBalancer lb: lbs) {
         api.getLoadBalancerApiForZone(lb.getRegion()).update(lb.getId(),
               UpdateLoadBalancer.builder().name("foo" + "-" + lb.getRegion()).build());
         
         assertTrue(awaitAvailable(api.getLoadBalancerApiForZone(lb.getRegion())).apply(lb));

         LoadBalancer newLb = api.getLoadBalancerApiForZone(lb.getRegion()).get(lb.getId());
         checkLBInRegion(newLb.getRegion(), newLb, "foo" + "-" + lb.getRegion());
         
         assertEquals(newLb.getStatus(), LoadBalancer.Status.ACTIVE);
      }
   }

   @Test(dependsOnMethods = "testUpdateLoadBalancer")
   public void testListLoadBalancers() throws Exception {
      for (String zone: api.getConfiguredZones()) {

         Set<LoadBalancer> response = api.getLoadBalancerApiForZone(zone).list().concat().toSet();
         
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

            LoadBalancer getDetails = api.getLoadBalancerApiForZone(zone).get(lb.getId());
            
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
   
   @Test(dependsOnMethods = "testListLoadBalancers")
   public void testLoadBalancerMetadata() throws Exception {
      for (LoadBalancer lb: lbs) {
         Map<String, String> metadataMap = ImmutableMap.<String, String> of(
               "key1", "value1",
               "key2", "value2",
               "key3", "value3");
         
         Metadata metadata = api.getLoadBalancerApiForZone(lb.getRegion()).createMetadata(lb.getId(), metadataMap);
         assertEquals(metadata, getExpectedMetadata());
         assertTrue(awaitAvailable(api.getLoadBalancerApiForZone(lb.getRegion())).apply(lb));

         metadata = api.getLoadBalancerApiForZone(lb.getRegion()).getMetadata(lb.getId());
         assertEquals(metadata, getExpectedMetadata());

         assertTrue(api.getLoadBalancerApiForZone(lb.getRegion()).updateMetadatum(lb.getId(), metadata.getId("key1"), "key1-updated"));
         assertTrue(awaitAvailable(api.getLoadBalancerApiForZone(lb.getRegion())).apply(lb));
         metadata = api.getLoadBalancerApiForZone(lb.getRegion()).getMetadata(lb.getId());
         assertEquals(metadata.get("key1"), "key1-updated");

         assertTrue(api.getLoadBalancerApiForZone(lb.getRegion()).deleteMetadatum(lb.getId(), metadata.getId("key1")));
         assertTrue(awaitAvailable(api.getLoadBalancerApiForZone(lb.getRegion())).apply(lb));
         metadata = api.getLoadBalancerApiForZone(lb.getRegion()).getMetadata(lb.getId());
         assertNull(metadata.get("key1"));

         assertTrue(api.getLoadBalancerApiForZone(lb.getRegion()).deleteMetadata(lb.getId(), 
               ImmutableList.<Integer> of(metadata.getId("key2"), metadata.getId("key3"))));
         assertTrue(awaitAvailable(api.getLoadBalancerApiForZone(lb.getRegion())).apply(lb));
         metadata = api.getLoadBalancerApiForZone(lb.getRegion()).getMetadata(lb.getId());
         assertEquals(metadata.size(), 0);
      }
   }

   private void checkLBInRegion(String region, LoadBalancer lb, String name) {
      assertEquals(lb.getRegion(), region);
      assertEquals(lb.getName(), name);
      assertEquals(lb.getProtocol(), "HTTP");
      assertEquals(lb.getPort(), new Integer(80));
      assertEquals(Iterables.get(lb.getVirtualIPs(), 0).getType(), Type.PUBLIC);
   }

   private Metadata getExpectedMetadata() {
      Metadata metadata = new Metadata();
      metadata.put("key1", "value1");
      metadata.put("key2", "value2");
      metadata.put("key3", "value3");

      return metadata;
   }
}
