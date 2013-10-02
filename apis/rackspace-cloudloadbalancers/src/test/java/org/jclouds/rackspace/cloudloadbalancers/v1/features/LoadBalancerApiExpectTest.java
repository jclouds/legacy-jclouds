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
import static javax.ws.rs.core.Response.Status.ACCEPTED;
import static javax.ws.rs.core.Response.Status.OK;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Set;

import org.jclouds.http.HttpResponse;
import org.jclouds.rackspace.cloudloadbalancers.v1.CloudLoadBalancersApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.AddNode;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.CreateLoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.LoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.Metadata;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.Node;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.UpdateLoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.VirtualIP;
import org.jclouds.rackspace.cloudloadbalancers.v1.functions.ParseLoadBalancerTest;
import org.jclouds.rackspace.cloudloadbalancers.v1.functions.ParseLoadBalancersTest;
import org.jclouds.rackspace.cloudloadbalancers.v1.internal.BaseCloudLoadBalancerApiExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

/**
 * @author Everett Toews
 */
@Test(groups = "unit")
public class LoadBalancerApiExpectTest extends BaseCloudLoadBalancerApiExpectTest<CloudLoadBalancersApi> {

   public void testListLoadBalancers() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers");
      LoadBalancerApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/loadbalancers-list.json")).build()
      ).getLoadBalancerApiForZone("DFW");

      Set<LoadBalancer> loadBalancers = api.list().concat().toSet();
      assertEquals(loadBalancers, getExpectedLoadBalancers());
   }

   public void testGetLoadBalancer() throws Exception {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000");
      LoadBalancerApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/loadbalancer-get.json")).build()
      ).getLoadBalancerApiForZone("DFW");

      LoadBalancer loadBalancer = api.get(2000);
      assertEquals(loadBalancer, getExpectedLoadBalancer());
   }

   public void testCreateLoadBalancer() throws Exception {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers");
      LoadBalancerApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET()
                  .method(POST)
                  .payload(payloadFromResource("/loadbalancer-create.json"))
                  .endpoint(endpoint)
                  .build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/loadbalancer-get.json")).build()
      ).getLoadBalancerApiForZone("DFW");

      AddNode addNode1 = AddNode.builder()
            .address("10.1.1.1")
            .condition(Node.Condition.ENABLED)
            .port(80)
            .build();
      
      AddNode addNode2 = AddNode.builder()
            .address("10.1.1.2")
            .condition(Node.Condition.ENABLED)
            .port(80)
            .build();
      
      Set<AddNode> addNodes = Sets.newHashSet(addNode1, addNode2);

      CreateLoadBalancer createLB = CreateLoadBalancer.builder()
            .name("sample-loadbalancer")
            .protocol("HTTP")
            .port(80)
            .algorithm(LoadBalancer.Algorithm.RANDOM)
            .virtualIPType(VirtualIP.Type.PUBLIC)
            .nodes(addNodes)
            .build();
      
      LoadBalancer loadBalancer = api.create(createLB);
      
      assertEquals(loadBalancer, getExpectedLoadBalancer());
   }

   public void testUpdateLoadBalancerAttributes() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000");
      LoadBalancerApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().method(PUT).payload(payloadFromResource("/loadbalancer-update.json")).endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(ACCEPTED.getStatusCode()).payload("").build()
      ).getLoadBalancerApiForZone("DFW");

      UpdateLoadBalancer updateLB = UpdateLoadBalancer.builder()
            .name("foo")
            .protocol("HTTPS")
            .port(443)
            .algorithm(LoadBalancer.Algorithm.RANDOM)
            .build();

      api.update(2000, updateLB);
   }

   public void testRemoveLoadBalancer() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000");
      LoadBalancerApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET()
                  .method(DELETE)
                  .replaceHeader(ACCEPT, WILDCARD)
                  .endpoint(endpoint)
                  .build(),
            HttpResponse.builder().statusCode(ACCEPTED.getStatusCode()).payload("").build()
      ).getLoadBalancerApiForZone("DFW");
      
      api.delete(2000);
   }

   public void testListMetadata() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/metadata");
      LoadBalancerApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/metadata-list.json")).build()
      ).getLoadBalancerApiForZone("DFW");

      Metadata metadata = api.getMetadata(2000);
      assertEquals(metadata, getExpectedMetadataWithIds());
   }

   public void testCreateMetadata() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/metadata");
      LoadBalancerApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET()
               .method(POST)
               .endpoint(endpoint)
               .payload(payloadFromResourceWithContentType("/metadata-create.json", APPLICATION_JSON)).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/metadata-list.json")).build()
      ).getLoadBalancerApiForZone("DFW");
         
      Metadata metadata = api.createMetadata(2000, getExpectedMetadata());
      assertEquals(metadata, getExpectedMetadataWithIds());
   }

   public void testRemoveSingleMetadata() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/metadata/23");
      LoadBalancerApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().method(DELETE).endpoint(endpoint).replaceHeader(ACCEPT, WILDCARD).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).build()
      ).getLoadBalancerApiForZone("DFW");

      assertTrue(api.deleteMetadatum(2000, 23));
   }

   public void testRemoveManyMetadata() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/metadata?id=23&id=24");
      LoadBalancerApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().method(DELETE).endpoint(endpoint).replaceHeader(ACCEPT, WILDCARD).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).build()
      ).getLoadBalancerApiForZone("DFW");
      
      
      
      assertTrue(api.deleteMetadata(2000, ImmutableList.<Integer> of(23, 24)));
   }

   private Object getExpectedLoadBalancer() {
      return new ParseLoadBalancerTest().expected();
   }

   private Set<LoadBalancer> getExpectedLoadBalancers() {      
      return new ParseLoadBalancersTest().data();
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
}
