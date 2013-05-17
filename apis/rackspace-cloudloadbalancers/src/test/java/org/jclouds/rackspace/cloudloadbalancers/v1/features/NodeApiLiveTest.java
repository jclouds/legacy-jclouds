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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import org.jclouds.rackspace.cloudloadbalancers.v1.domain.LoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.CreateLoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.Metadata;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.Node;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.UpdateNode;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.AddNode;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.LoadBalancer.Status;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.VirtualIP.Type;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.LoadBalancerApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.internal.BaseCloudLoadBalancersApiLiveTest;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.Test;

/**
 * @author Dan Lo Bianco
 */
@Test(groups = "live", singleThreaded = true, testName = "NodeClientLiveTest")
public class NodeApiLiveTest extends BaseCloudLoadBalancersApiLiveTest {
   private Map<LoadBalancer, Set<Node>> nodes = Maps.newHashMap();

   public void testCreateLoadBalancers() {
      assertTrue(api.getConfiguredZones().size() > 0, "Need to have some zones!");
      Logger.getAnonymousLogger().info("running against zones " + api.getConfiguredZones());
      for (String zone : api.getConfiguredZones()) {
         Logger.getAnonymousLogger().info("starting lb in zone " + zone);
         LoadBalancer lb = api.getLoadBalancerApiForZone(zone).create(
                  CreateLoadBalancer.builder().name(prefix + "-" + zone).protocol("HTTP").port(80).virtualIPType(
                           Type.PUBLIC).node(AddNode.builder().address("192.168.1.1").port(8080).build()).build());
         nodes.put(lb, new HashSet<Node>());

         assertTrue(awaitAvailable(api.getLoadBalancerApiForZone(lb.getRegion())).apply(lb));
      }
   }

   @Test(dependsOnMethods = "testCreateLoadBalancers")
   public void testAddNodes() throws Exception {
      for (Map.Entry<LoadBalancer, Set<Node>> entry : nodes.entrySet()) {
         LoadBalancer lb = entry.getKey();
         Set<Node> nodeSet = entry.getValue();
         String region = lb.getRegion();
         Logger.getAnonymousLogger().info("starting node on loadbalancer " + lb.getId() + " in region " + region);
         Set<Node> newNodes = api.getNodeApiForZoneAndLoadBalancer(region, lb.getId()).add(
                  ImmutableSet.<AddNode> of(AddNode.builder().address("192.168.1.2").port(8080).build()));

         for (Node n : newNodes) {
            assertEquals(n.getStatus(), Node.Status.ONLINE);
            nodeSet.add(n);
            assertEquals(api.getNodeApiForZoneAndLoadBalancer(region, lb.getId()).get(n.getId()).getStatus(),
                     Node.Status.ONLINE);
         }

         assertTrue(awaitAvailable(api.getLoadBalancerApiForZone(lb.getRegion())).apply(lb));
      }
   }

   @Test(dependsOnMethods = "testAddNodes")
   public void testModifyNode() throws Exception {
      for (Entry<LoadBalancer, Set<Node>> entry : nodes.entrySet()) {
         for (Node n : entry.getValue()) {
            String region = entry.getKey().getRegion();
            api.getNodeApiForZoneAndLoadBalancer(region, entry.getKey().getId()).update(n.getId(),
                     UpdateNode.builder().weight(23).build());
            assertEquals(api.getNodeApiForZoneAndLoadBalancer(region, entry.getKey().getId()).get(n.getId())
                     .getStatus(), Node.Status.ONLINE);

            Node newNode = api.getNodeApiForZoneAndLoadBalancer(region, entry.getKey().getId()).get(n.getId());
            assertEquals(newNode.getStatus(), Node.Status.ONLINE);
            assertEquals(newNode.getWeight(), (Integer) 23);
         }
      }
   }

   @Test(dependsOnMethods = "testModifyNode")
   public void testListNodes() throws Exception {
      for (LoadBalancer lb : nodes.keySet()) {
         Set<Node> response = api.getNodeApiForZoneAndLoadBalancer(lb.getRegion(), lb.getId()).list().concat().toSet();
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

            Node getDetails = api.getNodeApiForZoneAndLoadBalancer(lb.getRegion(), lb.getId()).get(n.getId());

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
   
   @Test(dependsOnMethods = "testListNodes")
   public void testNodeMetadata() throws Exception {
      for (Entry<LoadBalancer, Set<Node>> entry : nodes.entrySet()) {
         LoadBalancer lb = entry.getKey();
         Node node = entry.getValue().iterator().next();
         Map<String, String> metadataMap = ImmutableMap.<String, String> of(
               "key1", "value1",
               "key2", "value2",
               "key3", "value3");
         
         Metadata metadata = api.getNodeApiForZoneAndLoadBalancer(lb.getRegion(), lb.getId()).createMetadata(node.getId(), metadataMap);
         assertEquals(metadata, getExpectedMetadata());
         assertTrue(awaitAvailable(api.getLoadBalancerApiForZone(lb.getRegion())).apply(lb));

         metadata = api.getNodeApiForZoneAndLoadBalancer(lb.getRegion(), lb.getId()).getMetadata(node.getId());
         assertEquals(metadata, getExpectedMetadata());

         assertTrue(api.getNodeApiForZoneAndLoadBalancer(lb.getRegion(), lb.getId()).updateMetadatum(node.getId(), metadata.getId("key1"), "key1-updated"));
         assertTrue(awaitAvailable(api.getLoadBalancerApiForZone(lb.getRegion())).apply(lb));
         metadata = api.getNodeApiForZoneAndLoadBalancer(lb.getRegion(), lb.getId()).getMetadata(node.getId());
         assertEquals(metadata.get("key1"), "key1-updated");

         assertTrue(api.getNodeApiForZoneAndLoadBalancer(lb.getRegion(), lb.getId()).deleteMetadatum(node.getId(), metadata.getId("key1")));
         assertTrue(awaitAvailable(api.getLoadBalancerApiForZone(lb.getRegion())).apply(lb));
         metadata = api.getNodeApiForZoneAndLoadBalancer(lb.getRegion(), lb.getId()).getMetadata(node.getId());
         assertNull(metadata.get("key1"));

         assertTrue(api.getNodeApiForZoneAndLoadBalancer(lb.getRegion(), lb.getId()).deleteMetadata(node.getId(), 
               ImmutableList.<Integer> of(metadata.getId("key2"), metadata.getId("key3"))));
         assertTrue(awaitAvailable(api.getLoadBalancerApiForZone(lb.getRegion())).apply(lb));
         metadata = api.getNodeApiForZoneAndLoadBalancer(lb.getRegion(), lb.getId()).getMetadata(node.getId());
         assertEquals(metadata.size(), 0);
      }
   }

   @Override
   @AfterGroups(groups = "live")
   protected void tearDown() {
      for (Entry<LoadBalancer, Set<Node>> entry : nodes.entrySet()) {
         LoadBalancer lb = entry.getKey();
         LoadBalancerApi lbClient = api.getLoadBalancerApiForZone(lb.getRegion());

         if (lbClient.get(lb.getId()).getStatus() != Status.DELETED) {
            assertTrue(awaitAvailable(api.getLoadBalancerApiForZone(lb.getRegion())).apply(lb));
            lbClient.delete(lb.getId());
         }
         assertTrue(awaitDeleted(api.getLoadBalancerApiForZone(lb.getRegion())).apply(lb));
      }
      super.tearDown();
   }

   private Metadata getExpectedMetadata() {
      Metadata metadata = new Metadata();
      metadata.put("key1", "value1");
      metadata.put("key2", "value2");
      metadata.put("key3", "value3");

      return metadata;
   }
}
