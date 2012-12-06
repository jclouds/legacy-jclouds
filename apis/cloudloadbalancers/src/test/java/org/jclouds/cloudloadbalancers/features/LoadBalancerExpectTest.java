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

import java.net.URI;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.jclouds.cloudloadbalancers.CloudLoadBalancersClient;
import org.jclouds.cloudloadbalancers.domain.LoadBalancer;
import org.jclouds.cloudloadbalancers.domain.LoadBalancerAttributes;
import org.jclouds.cloudloadbalancers.domain.LoadBalancerRequest;
import org.jclouds.cloudloadbalancers.domain.NodeRequest;
import org.jclouds.cloudloadbalancers.domain.VirtualIP;
import org.jclouds.cloudloadbalancers.functions.UnwrapLoadBalancerTest;
import org.jclouds.cloudloadbalancers.functions.UnwrapLoadBalancersTest;
import org.jclouds.cloudloadbalancers.internal.BaseCloudLoadBalancerExpectTest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;

/**
 * @author Everett Toews
 */
@Test(groups = "unit")
public class LoadBalancerExpectTest extends BaseCloudLoadBalancerExpectTest<CloudLoadBalancersClient> {

   public void testListLoadBalancers() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers");
      LoadBalancerClient api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/loadbalancers-list.json")).build()
      ).getLoadBalancerClient("DFW");

      Set<LoadBalancer> loadBalancers = api.listLoadBalancers();
      assertEquals(loadBalancers, testLoadBalancers());
   }

   public void testGetLoadBalancer() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000");
      LoadBalancerClient api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/loadbalancer-get.json")).build()
      ).getLoadBalancerClient("DFW");

      LoadBalancer loadBalancer = api.getLoadBalancer(2000);
      assertEquals(loadBalancer, testLoadBalancer());
   }

   public void testCreateLoadBalancer() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers");
      LoadBalancerClient api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET()
                  .method("POST")
                  .payload(payloadFromResource("/loadbalancer-create.json"))
                  .endpoint(endpoint)
                  .build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/loadbalancer-get.json")).build()
      ).getLoadBalancerClient("DFW");

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
            .algorithm(LoadBalancer.Algorithm.RANDOM.name())
            .virtualIPType(VirtualIP.Type.PUBLIC)
            .nodes(nodeRequests)
            .build();
      
      LoadBalancer loadBalancer = api.createLoadBalancer(lbRequest);
      
      assertEquals(loadBalancer, testLoadBalancer());
   }

   public void testUpdateLoadBalancerAttributes() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000");
      LoadBalancerClient api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().method("PUT").payload(payloadFromResource("/loadbalancer-update.json")).endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(202).payload("").build()
      ).getLoadBalancerClient("DFW");

      LoadBalancerAttributes lbAttrs = LoadBalancerAttributes.Builder
            .name("foo")
            .protocol("HTTPS")
            .port(443)
            .algorithm(LoadBalancer.Algorithm.RANDOM.name());

      api.updateLoadBalancerAttributes(2000, lbAttrs);
   }

   public void testRemoveLoadBalancer() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000");
      LoadBalancerClient api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET()
                  .method("DELETE")
                  .replaceHeader("Accept", MediaType.WILDCARD)
                  .endpoint(endpoint)
                  .build(),
            HttpResponse.builder().statusCode(202).payload("").build()
      ).getLoadBalancerClient("DFW");
      
      api.removeLoadBalancer(2000);
   }

   private Object testLoadBalancer() {
      return new UnwrapLoadBalancerTest().expected();
   }

   private Set<LoadBalancer> testLoadBalancers() {      
      return new UnwrapLoadBalancersTest().expected();
   }   
}
