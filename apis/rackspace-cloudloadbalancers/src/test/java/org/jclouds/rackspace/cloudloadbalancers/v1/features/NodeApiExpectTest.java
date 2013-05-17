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
import static com.google.common.net.HttpHeaders.ACCEPT;
import static javax.ws.rs.HttpMethod.DELETE;
import static javax.ws.rs.HttpMethod.POST;
import static javax.ws.rs.HttpMethod.PUT;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.WILDCARD;
import static javax.ws.rs.core.Response.Status.OK;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Set;

import org.jclouds.http.HttpResponse;
import org.jclouds.rackspace.cloudloadbalancers.v1.CloudLoadBalancersApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.AddNode;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.Metadata;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.Node;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.UpdateNode;
import org.jclouds.rackspace.cloudloadbalancers.v1.internal.BaseCloudLoadBalancerApiExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

/**
 * @author Everett Toews
 */
@Test(groups = "unit")
public class NodeApiExpectTest extends BaseCloudLoadBalancerApiExpectTest<CloudLoadBalancersApi> {
   public void testListNodes() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/nodes");
      NodeApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/nodes-list.json")).build()
      ).getNodeApiForZoneAndLoadBalancer("DFW", 2000);

      Set<Node> nodes = api.list().concat().toSet();
      assertEquals(nodes, getExpectedNodes());
   }

   public void testGetNodeInLoadBalancer() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/nodes/410");
      NodeApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/node-get.json")).build()
      ).getNodeApiForZoneAndLoadBalancer("DFW", 2000);

      Node node = api.get(410);
      assertEquals(node, testNode());
   }

   public void testAddNodesInLoadBalancer() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/nodes");
      NodeApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET()
                  .method(POST)
                  .payload(payloadFromResourceWithContentType("/nodes-add.json", APPLICATION_JSON))
                  .endpoint(endpoint)
                  .build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/nodes-list.json")).build()
      ).getNodeApiForZoneAndLoadBalancer("DFW", 2000);

      AddNode addNode1 = AddNode.builder()
            .address("10.1.1.1")
            .condition(Node.Condition.ENABLED)
            .port(80)
            .weight(3)
            .build();
      
      AddNode addNode2 = AddNode.builder()
            .address("10.1.1.2")
            .condition(Node.Condition.ENABLED)
            .type(Node.Type.SECONDARY)
            .port(80)
            .weight(8)
            .build();

      AddNode addNode3 = AddNode.builder()
            .address("10.1.1.3")
            .condition(Node.Condition.DISABLED)
            .port(80)
            .weight(12)
            .build();

      Set<AddNode> addNodes = ImmutableSortedSet.<AddNode> of(addNode1, addNode2, addNode3);
      
      Set<Node> nodes = api.add(addNodes);
      assertEquals(nodes, getExpectedNodes());
   }

   public void testUpdateAttributesForNodeInLoadBalancer() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/nodes/410");
      NodeApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().method(PUT).payload(payloadFromResource("/node-update.json")).endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).build()
      ).getNodeApiForZoneAndLoadBalancer("DFW", 2000);

      UpdateNode updateNode = UpdateNode.builder()
            .condition(Node.Condition.DISABLED)
            .type(Node.Type.SECONDARY)
            .weight(20)
            .build();

      api.update(410, updateNode);
   }

   public void testRemoveNodeFromLoadBalancer() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/nodes/410");
      NodeApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().method(DELETE).replaceHeader(ACCEPT, WILDCARD).endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).build()
      ).getNodeApiForZoneAndLoadBalancer("DFW", 2000);

      api.remove(410);
   }

   public void testRemoveNodesFromLoadBalancer() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/nodes?id=410&id=411");
      NodeApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().method(DELETE).replaceHeader(ACCEPT, WILDCARD).endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).build()
      ).getNodeApiForZoneAndLoadBalancer("DFW", 2000);
      
      Set<Integer> nodeIds = ImmutableSortedSet.<Integer> of(410, 411);

      api.remove(nodeIds);
   }

   public void testListMetadata() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/nodes/410/metadata");
      NodeApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/metadata-list.json")).build()
      ).getNodeApiForZoneAndLoadBalancer("DFW", 2000);

      Metadata metadata = api.getMetadata(410);
      assertEquals(metadata, getExpectedMetadataWithIds());
   }

   public void testCreateMetadata() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/nodes/410/metadata");
      NodeApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET()
               .method(POST)
               .endpoint(endpoint)
               .payload(payloadFromResourceWithContentType("/metadata-create.json", APPLICATION_JSON)).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/metadata-list.json")).build()
      ).getNodeApiForZoneAndLoadBalancer("DFW", 2000);
         
      Metadata metadata = api.createMetadata(410, getExpectedMetadata());
      assertEquals(metadata, getExpectedMetadataWithIds());
   }

   public void testRemoveSingleMetadata() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/nodes/410/metadata/23");
      NodeApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().method(DELETE).endpoint(endpoint).replaceHeader(ACCEPT, WILDCARD).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).build()
      ).getNodeApiForZoneAndLoadBalancer("DFW", 2000);

      assertTrue(api.deleteMetadatum(410, 23));
   }

   public void testRemoveManyMetadata() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/nodes/410/metadata?id=23&id=24");
      NodeApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().method(DELETE).endpoint(endpoint).replaceHeader(ACCEPT, WILDCARD).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).build()
      ).getNodeApiForZoneAndLoadBalancer("DFW", 2000);
      
      
      
      assertTrue(api.deleteMetadata(410, ImmutableList.<Integer> of(23, 24)));
   }

   private Metadata getExpectedMetadata() {
      Metadata metadata = new Metadata();
      metadata.put("color", "red");
      metadata.put("label", "web-load-balancer");
      metadata.put("os", "ubuntu");

      return metadata;
   }

   private Metadata getExpectedMetadataWithIds() {
      Metadata metadata = getExpectedMetadata();
      metadata.putId("color", 1);
      metadata.putId("label", 2);
      metadata.putId("os", 3);

      return metadata;
   }

   private Set<Node> getExpectedNodes() {
      Node node1 = Node.builder()
            .id(410)
            .address("10.1.1.1")
            .port(80)
            .condition(Node.Condition.ENABLED)
            .type(Node.Type.PRIMARY)
            .status(Node.Status.ONLINE)
            .weight(3)
            .build();

      Node node2 = Node.builder()
            .id(411)
            .address("10.1.1.2")
            .port(80)
            .condition(Node.Condition.ENABLED)
            .type(Node.Type.SECONDARY)
            .status(Node.Status.ONLINE)
            .weight(8)
            .build();

      Node node3 = Node.builder()
            .id(412)
            .address("10.1.1.3")
            .port(80)
            .condition(Node.Condition.DISABLED)
            .type(Node.Type.PRIMARY)
            .status(Node.Status.ONLINE)
            .weight(12)
            .build();

      return ImmutableSet.<Node> of(node1, node2, node3);
   }
   
   private Node testNode() {
      return Node.builder()
            .id(410)
            .address("10.1.1.1")
            .port(80)
            .condition(Node.Condition.ENABLED)
            .type(Node.Type.PRIMARY)
            .status(Node.Status.ONLINE)
            .weight(12)
            .build();
   }
}
