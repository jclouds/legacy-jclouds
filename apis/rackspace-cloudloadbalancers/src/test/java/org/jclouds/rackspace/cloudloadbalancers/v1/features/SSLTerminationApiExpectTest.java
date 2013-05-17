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
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;

import org.jclouds.http.HttpResponse;
import org.jclouds.rackspace.cloudloadbalancers.v1.CloudLoadBalancersApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.SSLTermination;
import org.jclouds.rackspace.cloudloadbalancers.v1.internal.BaseCloudLoadBalancerApiExpectTest;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

/**
 * @author Everett Toews 
 */
@Test(groups = "unit")
public class SSLTerminationApiExpectTest extends BaseCloudLoadBalancerApiExpectTest<CloudLoadBalancersApi> {

   public void testGetSSLTermination() throws IOException {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/ssltermination");
      SSLTerminationApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/ssltermination-get.json")).build()
      ).getSSLTerminationApiForZoneAndLoadBalancer("DFW", 2000);

      SSLTermination sslTermination = api.get();
      assertEquals(sslTermination, getSSLTermination());
   }

   public void testGetDeletedSSLTermination() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/ssltermination");
      SSLTerminationApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(NOT_FOUND.getStatusCode()).build()
      ).getSSLTerminationApiForZoneAndLoadBalancer("DFW", 2000);

      SSLTermination sslTermination = api.get();
      assertNull(sslTermination);
   }

   public void testCreateSSLTermination() throws IOException {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/ssltermination");
      SSLTerminationApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET()
               .method(PUT)
               .endpoint(endpoint)
               .payload(payloadFromResourceWithContentType("/ssltermination-create.json", APPLICATION_JSON))
               .build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).build()
      ).getSSLTerminationApiForZoneAndLoadBalancer("DFW", 2000);
      
      api.createOrUpdate(getSSLTermination());
   }

   public void testRemoveSSLTermination() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/ssltermination");
      SSLTerminationApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().method(DELETE).endpoint(endpoint).replaceHeader(ACCEPT, WILDCARD).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).build()
      ).getSSLTerminationApiForZoneAndLoadBalancer("DFW", 2000);

      assertTrue(api.delete());
   }

   public static SSLTermination getSSLTermination() throws IOException {
      String certificate = Strings2.toStringAndClose(
            SSLTerminationApiExpectTest.class.getResourceAsStream("/ssltermination-certificate.txt"));
      String privateKey = Strings2.toStringAndClose(
            SSLTerminationApiExpectTest.class.getResourceAsStream("/ssltermination-privatekey.txt"));
      String intermediateCertificate = Strings2.toStringAndClose(
            SSLTerminationApiExpectTest.class.getResourceAsStream("/ssltermination-intermediatecertificate.txt"));
      
      SSLTermination sslTermination = SSLTermination.builder()
            .enabled(true)
            .secureTrafficOnly(false)
            .securePort(443)
            .certificate(certificate)
            .privatekey(privateKey)
            .intermediateCertificate(intermediateCertificate)
            .build();

      return sslTermination;
   }
}
