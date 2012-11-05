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
package org.jclouds.cloudloadbalancers.features;

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

import org.jclouds.cloudloadbalancers.domain.LoadBalancer;
import org.jclouds.cloudloadbalancers.domain.LoadBalancerRequest;
import org.jclouds.cloudloadbalancers.domain.Node;
import org.jclouds.cloudloadbalancers.domain.NodeAttributes;
import org.jclouds.cloudloadbalancers.domain.NodeRequest;
import org.jclouds.cloudloadbalancers.domain.LoadBalancer.Status;
import org.jclouds.cloudloadbalancers.domain.VirtualIP.Type;
import org.jclouds.cloudloadbalancers.internal.BaseCloudLoadBalancersClientLiveTest;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code NodeClient}
 * 
 * @author Dan Lo Bianco
 */
@Test(groups = "live", singleThreaded = true, testName = "NodeClientLiveTest")
public class NodeClientLiveTest extends BaseCloudLoadBalancersClientLiveTest {
   private Map<LoadBalancer, Set<Node>> nodes = Maps.newHashMap();

   @Test(groups = "live")
   public void testCreateLoadBalancers() {
      assertTrue(client.getConfiguredRegions().size() > 0, "Need to have some regions!");
      Logger.getAnonymousLogger().info("running against regions " + client.getConfiguredRegions());
      for (String region : client.getConfiguredRegions()) {
         Logger.getAnonymousLogger().info("starting lb in region " + region);
         LoadBalancer lb = client.getLoadBalancerClient(region).createLoadBalancer(
                  LoadBalancerRequest.builder().name(prefix + "-" + region).protocol("HTTP").port(80).virtualIPType(
                           Type.PUBLIC).node(NodeRequest.builder().address("192.168.1.1").port(8080).build()).build());
         nodes.put(lb, new HashSet<Node>());

         assert loadBalancerActive.apply(lb) : lb;
      }
   }

   @Test(groups = "live", dependsOnMethods = "testCreateLoadBalancers")
   public void testAddNodes() throws Exception {
      for (LoadBalancer lb : nodes.keySet()) {
         String region = lb.getRegion();
         Logger.getAnonymousLogger().info("starting node on loadbalancer " + lb.getId() + " in region " + region);
         Set<Node> newNodes = client.getNodeClient(region).createNodesInLoadBalancer(
                  ImmutableSet.<NodeRequest> of(NodeRequest.builder().address("192.168.1.2").port(8080).build()),
                  lb.getId());

         for (Node n : newNodes) {
            assertEquals(n.getStatus(), Node.Status.ONLINE);
            nodes.get(lb).add(n);
            assertEquals(client.getNodeClient(region).getNodeInLoadBalancer(n.getId(), lb.getId()).getStatus(),
                     Node.Status.ONLINE);
         }

         assert loadBalancerActive.apply(lb) : lb;
      }
   }

   @Test(groups = "live", dependsOnMethods = "testAddNodes")
   public void testModifyNode() throws Exception {
      for (Entry<LoadBalancer, Set<Node>> entry : nodes.entrySet()) {
         for (Node n : entry.getValue()) {
            String region = entry.getKey().getRegion();
            client.getNodeClient(region).updateAttributesForNodeInLoadBalancer(NodeAttributes.Builder.weight(23),
                     n.getId(), entry.getKey().getId());
            assertEquals(client.getNodeClient(region).getNodeInLoadBalancer(n.getId(), entry.getKey().getId())
                     .getStatus(), Node.Status.ONLINE);

            Node newNode = client.getNodeClient(region).getNodeInLoadBalancer(n.getId(), entry.getKey().getId());
            assertEquals(newNode.getStatus(), Node.Status.ONLINE);
            assertEquals(newNode.getWeight(), (Integer) 23);
         }
      }
   }

   @Test(groups = "live", dependsOnMethods = "testModifyNode")
   public void testListNodes() throws Exception {
      for (LoadBalancer lb : nodes.keySet()) {
         Set<Node> response = client.getNodeClient(lb.getRegion()).listNodes(lb.getId());
         assert null != response;
         assertTrue(response.size() >= 0);
         for (Node n : response) {
            assert n.getId() != -1 : n;
            assert n.getCondition() != null : n;
            assert n.getAddress() != null : n;
            assert n.getPort() != -1 : n;
            assert n.getStatus() != null : n;
            assert !Arrays.asList(LoadBalancer.WEIGHTED_ALGORITHMS).contains(lb.getTypedAlgorithm())
                     || n.getWeight() != null : n;

            Node getDetails = client.getNodeClient(lb.getRegion()).getNodeInLoadBalancer(n.getId(), lb.getId());
            System.out.println(n.toString());
            try {
               assertEquals(getDetails.getId(), n.getId());
               assertEquals(getDetails.getCondition(), n.getCondition());
               assertEquals(getDetails.getAddress(), n.getAddress());
               assertEquals(getDetails.getPort(), n.getPort());
               assertEquals(getDetails.getStatus(), n.getStatus());
               if (Arrays.asList(LoadBalancer.WEIGHTED_ALGORITHMS).contains(lb.getTypedAlgorithm())) {
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
         LoadBalancerClient lbClient = client.getLoadBalancerClient(lb.getRegion());

         if (lbClient.getLoadBalancer(lb.getId()).getStatus() != Status.DELETED) {
            lbClient.removeLoadBalancer(lb.getId());
         }
         assert loadBalancerDeleted.apply(lb) : lb;
      }
      super.tearDownContext();
   }
}
