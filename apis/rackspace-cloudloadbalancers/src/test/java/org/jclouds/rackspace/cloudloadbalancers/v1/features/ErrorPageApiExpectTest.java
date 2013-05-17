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
import static javax.ws.rs.HttpMethod.PUT;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.WILDCARD;
import static javax.ws.rs.core.Response.Status.OK;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;

import org.jclouds.http.HttpResponse;
import org.jclouds.rackspace.cloudloadbalancers.v1.CloudLoadBalancersApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.internal.BaseCloudLoadBalancerApiExpectTest;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

/**
 * @author Everett Toews
 */
@Test(groups = "unit")
public class ErrorPageApiExpectTest extends BaseCloudLoadBalancerApiExpectTest<CloudLoadBalancersApi> {
   public String contentExpected;
   public String contentEscaped;
   
   public ErrorPageApiExpectTest() {
      super();
      
      contentExpected = getContentExpected();
      contentEscaped = getContentEscaped();
   }
   
   public void testGetErrorPage() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/errorpage");
      ErrorPageApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder()
               .statusCode(OK.getStatusCode())
               .payload(payloadFromStringWithContentType("{\"errorpage\":{\"content\":\"" + contentEscaped + "\"}}", APPLICATION_JSON))
               .build()
      ).getErrorPageApiForZoneAndLoadBalancer("DFW", 2000);

      String content = api.get();
      assertEquals(content, contentExpected);
   }

   public void testCreateErrorPage() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/errorpage");
      ErrorPageApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET()
               .method(PUT)
               .endpoint(endpoint)
               .replaceHeader(ACCEPT, WILDCARD)
               .payload(payloadFromStringWithContentType("{\"errorpage\":{\"content\":\"" + contentEscaped + "\"}}", APPLICATION_JSON))
               .build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).build()
      ).getErrorPageApiForZoneAndLoadBalancer("DFW", 2000);
      
      api.create(contentEscaped);
   }

   public void testRemoveErrorPage() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/errorpage");
      ErrorPageApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().method(DELETE).endpoint(endpoint).replaceHeader(ACCEPT, WILDCARD).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).build()
      ).getErrorPageApiForZoneAndLoadBalancer("DFW", 2000);

      assertTrue(api.delete());
   }
   
   public static String getContentExpected() {
      String contentExpected;
      
      try {
         contentExpected = Strings2.toStringAndClose(ErrorPageApiExpectTest.class.getResourceAsStream("/errorpage.html"));
      }
      catch (IOException e) {
         throw new RuntimeException("Could not read in /errorpage.html", e);
      }
      
      return contentExpected;
   }
   
   public static String getContentEscaped() {
      String contentEscaped = getContentExpected().replaceAll("\"", "\\\\\"");      
      contentEscaped = contentEscaped.replaceAll("\n", "\\\\n");
      
      return contentEscaped;
   }
}
