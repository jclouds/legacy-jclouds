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

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpResponse;
import org.jclouds.rackspace.cloudloadbalancers.CloudLoadBalancersApi;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancerAttributes;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancerRequest;
import org.jclouds.rackspace.cloudloadbalancers.domain.NodeRequest;
import org.jclouds.rackspace.cloudloadbalancers.domain.VirtualIP;
import org.jclouds.rackspace.cloudloadbalancers.functions.ParseLoadBalancerTest;
import org.jclouds.rackspace.cloudloadbalancers.functions.ParseLoadBalancersTest;
import org.jclouds.rackspace.cloudloadbalancers.internal.BaseCloudLoadBalancerApiExpectTest;
import org.testng.annotations.Test;

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
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/loadbalancers-list.json")).build()
      ).getLoadBalancerApiForZone("DFW");

      Set<LoadBalancer> loadBalancers = api.list().concat().toSet();
      assertEquals(loadBalancers, testLoadBalancers());
   }

   public void testGetLoadBalancer() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000");
      LoadBalancerApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/loadbalancer-get.json")).build()
      ).getLoadBalancerApiForZone("DFW");

      LoadBalancer loadBalancer = api.get(2000);
      assertEquals(loadBalancer, testLoadBalancer());
   }

   public void testCreateLoadBalancer() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers");
      LoadBalancerApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET()
                  .method("POST")
                  .payload(payloadFromResource("/loadbalancer-create.json"))
                  .endpoint(endpoint)
                  .build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/loadbalancer-get.json")).build()
      ).getLoadBalancerApiForZone("DFW");

      NodeRequest nodeRequest1 = NodeRequest.builder()
            .address("10.1.1.1")
            .condition(NodeRequest.Condition.ENABLED)
            .port(80)
            .build();
      
      NodeRequest nodeRequest2 = NodeRequest.builder()
            .address("10.1.1.2")
            .condition(NodeRequest.Condition.ENABLED)
            .port(80)
            .build();
      
      Set<NodeRequest> nodeRequests = Sets.newHashSet(nodeRequest1, nodeRequest2);

      LoadBalancerRequest lbRequest = LoadBalancerRequest.builder()
            .name("sample-loadbalancer")
            .protocol("HTTP")
            .port(80)
            .algorithm(LoadBalancer.Algorithm.RANDOM)
            .virtualIPType(VirtualIP.Type.PUBLIC)
            .nodes(nodeRequests)
            .build();
      
      LoadBalancer loadBalancer = api.create(lbRequest);
      
      assertEquals(loadBalancer, testLoadBalancer());
   }

   public void testUpdateLoadBalancerAttributes() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000");
      LoadBalancerApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().method("PUT").payload(payloadFromResource("/loadbalancer-update.json")).endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(202).payload("").build()
      ).getLoadBalancerApiForZone("DFW");

      LoadBalancerAttributes lbAttrs = LoadBalancerAttributes.Builder
            .name("foo")
            .protocol("HTTPS")
            .port(443)
            .algorithm(LoadBalancer.Algorithm.RANDOM);

      api.update(2000, lbAttrs);
   }

   public void testRemoveLoadBalancer() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000");
      LoadBalancerApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET()
                  .method("DELETE")
                  .replaceHeader("Accept", MediaType.WILDCARD)
                  .endpoint(endpoint)
                  .build(),
            HttpResponse.builder().statusCode(202).payload("").build()
      ).getLoadBalancerApiForZone("DFW");
      
      api.remove(2000);
   }

   private Object testLoadBalancer() {
      return new ParseLoadBalancerTest().expected();
   }

   private Set<LoadBalancer> testLoadBalancers() {      
      return new ParseLoadBalancersTest().data();
   }   
}
