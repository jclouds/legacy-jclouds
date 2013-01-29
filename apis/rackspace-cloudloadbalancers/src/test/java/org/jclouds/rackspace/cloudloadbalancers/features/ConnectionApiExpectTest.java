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
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNull;

import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpResponse;
import org.jclouds.rackspace.cloudloadbalancers.CloudLoadBalancersApi;
import org.jclouds.rackspace.cloudloadbalancers.domain.ConnectionThrottle;
import org.jclouds.rackspace.cloudloadbalancers.internal.BaseCloudLoadBalancerApiExpectTest;
import org.testng.annotations.Test;

/**
 * @author Everett Toews
 */
@Test(groups = "unit")
public class ConnectionApiExpectTest extends BaseCloudLoadBalancerApiExpectTest<CloudLoadBalancersApi> {
   public void testGetConnectionThrottle() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/connectionthrottle");
      ConnectionApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/connectionthrottle-get.json")).build()
      ).getConnectionApiForZoneAndLoadBalancer("DFW", 2000);

      ConnectionThrottle connectionThrottle = api.getConnectionThrottle();
      assertEquals(connectionThrottle, getConnectionThrottle());
   }

   public void testGetDeletedConnectionThrottle() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/connectionthrottle");
      ConnectionApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/connectionthrottle-get-deleted.json")).build()
      ).getConnectionApiForZoneAndLoadBalancer("DFW", 2000);

      ConnectionThrottle connectionThrottle = api.getConnectionThrottle();
      assertNull(connectionThrottle);
   }

   public void testCreateConnectionThrottle() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/connectionthrottle");
      ConnectionApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().method("PUT").endpoint(endpoint).payload(payloadFromResource("/connectionthrottle-create.json")).build(),
            HttpResponse.builder().statusCode(200).build()
      ).getConnectionApiForZoneAndLoadBalancer("DFW", 2000);
      
      api.createOrUpdateConnectionThrottle(getConnectionThrottle());
   }
   
   public void testValidConnectionThrottle() {
      assertTrue(getConnectionThrottle().isValid());
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testInvalidConnectionThrottle() {
      ConnectionThrottle.builder().build();      
   }

   public void testRemoveConnectionThrottle() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/connectionthrottle");
      ConnectionApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().method("DELETE").endpoint(endpoint).replaceHeader("Accept", MediaType.WILDCARD).build(),
            HttpResponse.builder().statusCode(200).build()
      ).getConnectionApiForZoneAndLoadBalancer("DFW", 2000);

      assertTrue(api.deleteConnectionThrottle());
   }
   
   public void testIsConnectionLogging() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/connectionlogging");
      ConnectionApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/connectionlogging-enable.json")).build()
      ).getConnectionApiForZoneAndLoadBalancer("DFW", 2000);
      
      assertTrue(api.isConnectionLogging());
   }
   
   public void testEnableConnectionLogging() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/connectionlogging");
      ConnectionApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().method("PUT").endpoint(endpoint).payload(payloadFromResource("/connectionlogging-enable.json")).build(),
            HttpResponse.builder().statusCode(200).build()
      ).getConnectionApiForZoneAndLoadBalancer("DFW", 2000);
      
      api.enableConnectionLogging();
   }
   
   public void testDisableConnectionLogging() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/connectionlogging");
      ConnectionApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().method("PUT").endpoint(endpoint).payload(payloadFromResource("/connectionlogging-disable.json")).build(),
            HttpResponse.builder().statusCode(200).build()
      ).getConnectionApiForZoneAndLoadBalancer("DFW", 2000);
      
      api.disableConnectionLogging();
   }   

   public static ConnectionThrottle getConnectionThrottle() {
      ConnectionThrottle connectionThrottle = ConnectionThrottle.builder()
            .maxConnections(100)
            .maxConnectionRate(100)
            .minConnections(10)
            .rateInterval(100)
            .build();

      return connectionThrottle;
   }
}
