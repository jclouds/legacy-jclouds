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
import static javax.ws.rs.HttpMethod.DELETE;
import static javax.ws.rs.HttpMethod.PUT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.net.URI;

import org.jclouds.http.HttpResponse;
import org.jclouds.rackspace.cloudloadbalancers.v1.CloudLoadBalancersApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.SessionPersistence;
import org.jclouds.rackspace.cloudloadbalancers.v1.internal.BaseCloudLoadBalancerApiExpectTest;
import org.testng.annotations.Test;

/**
 * @author Everett Toews
 */
@Test(groups = "unit")
public class SessionPersistenceApiExpectTest extends BaseCloudLoadBalancerApiExpectTest<CloudLoadBalancersApi> {
   public void testGetSessionPersistence() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/sessionpersistence");
      SessionPersistenceApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/sessionpersistence-get.json")).build()
      ).getSessionPersistenceApiForZoneAndLoadBalancer("DFW", 2000);

      SessionPersistence sessionPersistence = api.get();
      assertEquals(sessionPersistence, SessionPersistence.HTTP_COOKIE);
   }

   public void testGetDeletedSessionPersistence() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/sessionpersistence");
      SessionPersistenceApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/sessionpersistence-get-deleted.json")).build()
      ).getSessionPersistenceApiForZoneAndLoadBalancer("DFW", 2000);

      SessionPersistence sessionPersistence = api.get();
      assertNull(sessionPersistence);
   }
   
   public void testCreateSessionPersistence() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/sessionpersistence");
      SessionPersistenceApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().method(PUT).endpoint(endpoint).payload(payloadFromResource("/sessionpersistence-create.json")).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).build()
      ).getSessionPersistenceApiForZoneAndLoadBalancer("DFW", 2000);
      
      api.create(SessionPersistence.HTTP_COOKIE);
   }
   
   public void testRemoveSessionPersistence() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/sessionpersistence");
      SessionPersistenceApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().method(DELETE).endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).build()
      ).getSessionPersistenceApiForZoneAndLoadBalancer("DFW", 2000);
      
      api.delete();
   }   
}
