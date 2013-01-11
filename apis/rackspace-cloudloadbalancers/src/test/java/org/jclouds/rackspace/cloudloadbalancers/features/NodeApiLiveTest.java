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
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancerRequest;
import org.jclouds.rackspace.cloudloadbalancers.domain.Node;
import org.jclouds.rackspace.cloudloadbalancers.domain.NodeAttributes;
import org.jclouds.rackspace.cloudloadbalancers.domain.NodeRequest;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancer.Status;
import org.jclouds.rackspace.cloudloadbalancers.domain.VirtualIP.Type;
import org.jclouds.rackspace.cloudloadbalancers.features.LoadBalancerApi;
import org.jclouds.rackspace.cloudloadbalancers.internal.BaseCloudLoadBalancersApiLiveTest;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.Test;

/**
 * @author Dan Lo Bianco
 */
@Test(groups = "live", singleThreaded = true, testName = "NodeClientLiveTest")
public class NodeApiLiveTest extends BaseCloudLoadBalancersApiLiveTest {
   private Map<LoadBalancer, Set<Node>> nodes = Maps.newHashMap();

   public void testCreateLoadBalancers() {
      assertTrue(clbApi.getConfiguredZones().size() > 0, "Need to have some zones!");
      Logger.getAnonymousLogger().info("running against zones " + clbApi.getConfiguredZones());
      for (String zone : clbApi.getConfiguredZones()) {
         Logger.getAnonymousLogger().info("starting lb in zone " + zone);
         LoadBalancer lb = clbApi.getLoadBalancerApiForZone(zone).create(
                  LoadBalancerRequest.builder().name(prefix + "-" + zone).protocol("HTTP").port(80).virtualIPType(
                           Type.PUBLIC).node(NodeRequest.builder().address("192.168.1.1").port(8080).build()).build());
         nodes.put(lb, new HashSet<Node>());

         assertTrue(awaitAvailable(clbApi.getLoadBalancerApiForZone(lb.getRegion())).apply(lb));
      }
   }

   @Test(dependsOnMethods = "testCreateLoadBalancers")
   public void testAddNodes() throws Exception {
      for (LoadBalancer lb : nodes.keySet()) {
         String region = lb.getRegion();
         Logger.getAnonymousLogger().info("starting node on loadbalancer " + lb.getId() + " in region " + region);
         Set<Node> newNodes = clbApi.getNodeApiForZoneAndLoadBalancer(region, lb.getId()).add(
                  ImmutableSet.<NodeRequest> of(NodeRequest.builder().address("192.168.1.2").port(8080).build()));

         for (Node n : newNodes) {
            assertEquals(n.getStatus(), Node.Status.ONLINE);
            nodes.get(lb).add(n);
            assertEquals(clbApi.getNodeApiForZoneAndLoadBalancer(region, lb.getId()).get(n.getId()).getStatus(),
                     Node.Status.ONLINE);
         }

         assertTrue(awaitAvailable(clbApi.getLoadBalancerApiForZone(lb.getRegion())).apply(lb));
      }
   }

   @Test(dependsOnMethods = "testAddNodes")
   public void testModifyNode() throws Exception {
      for (Entry<LoadBalancer, Set<Node>> entry : nodes.entrySet()) {
         for (Node n : entry.getValue()) {
            String region = entry.getKey().getRegion();
            clbApi.getNodeApiForZoneAndLoadBalancer(region, entry.getKey().getId()).update(n.getId(),
                     NodeAttributes.Builder.weight(23));
            assertEquals(clbApi.getNodeApiForZoneAndLoadBalancer(region, entry.getKey().getId()).get(n.getId())
                     .getStatus(), Node.Status.ONLINE);

            Node newNode = clbApi.getNodeApiForZoneAndLoadBalancer(region, entry.getKey().getId()).get(n.getId());
            assertEquals(newNode.getStatus(), Node.Status.ONLINE);
            assertEquals(newNode.getWeight(), (Integer) 23);
         }
      }
   }

   @Test(dependsOnMethods = "testModifyNode")
   public void testListNodes() throws Exception {
      for (LoadBalancer lb : nodes.keySet()) {
         Set<Node> response = clbApi.getNodeApiForZoneAndLoadBalancer(lb.getRegion(), lb.getId()).list().concat().toSet();
         assert null != response;
         assertTrue(response.size() >= 0);
         for (Node n : response) {
            assert n.getId() != -1 : n;
            assert n.getCondition() != null : n;
            assert n.getAddress() != null : n;
            assert n.getPort() != -1 : n;
            assert n.getStatus() != null : n;
            assert !Arrays.asList(LoadBalancer.WEIGHTED_ALGORITHMS).contains(lb.getAlgorithm())
                     || n.getWeight() != null : n;

            Node getDetails = clbApi.getNodeApiForZoneAndLoadBalancer(lb.getRegion(), lb.getId()).get(n.getId());

            try {
               assertEquals(getDetails.getId(), n.getId());
               assertEquals(getDetails.getCondition(), n.getCondition());
               assertEquals(getDetails.getAddress(), n.getAddress());
               assertEquals(getDetails.getPort(), n.getPort());
               assertEquals(getDetails.getStatus(), n.getStatus());
               if (Arrays.asList(LoadBalancer.WEIGHTED_ALGORITHMS).contains(lb.getAlgorithm())) {
                  assertEquals(getDetails.getWeight(), n.getWeight());
               }
            } catch (AssertionError e) {
               throw new AssertionError(String.format("%s\n%s - %s", e.getMessage(), getDetails, n));
            }
         }
      }
   }

   @Override
   @AfterGroups(groups = "live")
   protected void tearDownContext() {
      for (Entry<LoadBalancer, Set<Node>> entry : nodes.entrySet()) {
         LoadBalancer lb = entry.getKey();
         LoadBalancerApi lbClient = clbApi.getLoadBalancerApiForZone(lb.getRegion());

         if (lbClient.get(lb.getId()).getStatus() != Status.DELETED) {
            assertTrue(awaitAvailable(clbApi.getLoadBalancerApiForZone(lb.getRegion())).apply(lb));
            lbClient.remove(lb.getId());
         }
         assertTrue(awaitDeleted(clbApi.getLoadBalancerApiForZone(lb.getRegion())).apply(lb));
      }
      super.tearDownContext();
   }
}
