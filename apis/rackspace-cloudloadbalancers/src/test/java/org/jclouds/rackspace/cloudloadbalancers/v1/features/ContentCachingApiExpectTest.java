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
import static javax.ws.rs.HttpMethod.PUT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import org.jclouds.http.HttpResponse;
import org.jclouds.rackspace.cloudloadbalancers.v1.CloudLoadBalancersApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.internal.BaseCloudLoadBalancerApiExpectTest;
import org.testng.annotations.Test;

/**
 * @author Everett Toews 
 */
@Test(groups = "unit")
public class ContentCachingApiExpectTest extends BaseCloudLoadBalancerApiExpectTest<CloudLoadBalancersApi> {
   public void testIsContentCaching() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/contentcaching");
      ContentCachingApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/contentcaching-enable.json")).build()
      ).getContentCachingApiForZoneAndLoadBalancer("DFW", 2000);
      
      assertTrue(api.isContentCaching());
   }
   
   public void testEnableContentCaching() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/contentcaching");
      ContentCachingApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().method(PUT).endpoint(endpoint).payload(payloadFromResource("/contentcaching-enable.json")).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).build()
      ).getContentCachingApiForZoneAndLoadBalancer("DFW", 2000);
      
      api.enable();
   }
   
   public void testDisableContentCaching() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/contentcaching");
      ContentCachingApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().method(PUT).endpoint(endpoint).payload(payloadFromResource("/contentcaching-disable.json")).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).build()
      ).getContentCachingApiForZoneAndLoadBalancer("DFW", 2000);
      
      api.disable();
   }   
}
