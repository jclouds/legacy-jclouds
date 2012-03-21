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
package org.jclouds.openstack.nova.v1_1.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v1_1.NovaClient;
import org.jclouds.openstack.nova.v1_1.internal.BaseNovaClientExpectTest;
import org.jclouds.openstack.nova.v1_1.options.CreateServerOptions;
import org.jclouds.openstack.nova.v1_1.parse.ParseCreatedServerTest;
import org.jclouds.openstack.nova.v1_1.parse.ParseServerListTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * Tests annotation parsing of {@code ServerAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ServerAsyncClientTest")
public class ServerClientExpectTest extends BaseNovaClientExpectTest {

   public void testListServersWhenResponseIs2xx() throws Exception {
      HttpRequest listServers = HttpRequest
            .builder()
            .method("GET")
            .endpoint(URI.create("https://compute.north.host/v1.1/3456/servers"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build()).build();

      HttpResponse listServersResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/server_list.json")).build();

      NovaClient clientWhenServersExist = requestsSendResponses(keystoneAuthWithAccessKeyAndSecretKey,
            responseWithKeystoneAccess, listServers, listServersResponse);

      assertEquals(clientWhenServersExist.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1"));

      assertEquals(clientWhenServersExist.getServerClientForZone("az-1.region-a.geo-1").listServers().toString(),
            new ParseServerListTest().expected().toString());
   }

   public void testListServersWhenReponseIs404IsEmpty() throws Exception {
      HttpRequest listServers = HttpRequest
            .builder()
            .method("GET")
            .endpoint(URI.create("https://compute.north.host/v1.1/3456/servers"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build()).build();

      HttpResponse listServersResponse = HttpResponse.builder().statusCode(404).build();

      NovaClient clientWhenNoServersExist = requestsSendResponses(keystoneAuthWithAccessKeyAndSecretKey,
            responseWithKeystoneAccess, listServers, listServersResponse);

      assertTrue(clientWhenNoServersExist.getServerClientForZone("az-1.region-a.geo-1").listServers().isEmpty());
   }
   
   public void testCreateServerWhenResponseIs202() throws Exception {
      HttpRequest createServer = HttpRequest
            .builder()
            .method("POST")
            .endpoint(URI.create("https://compute.north.host/v1.1/3456/servers"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build())
            .payload(payloadFromStringWithContentType(
                     "{\"server\":{\"name\":\"test-e92\",\"imageRef\":\"1241\",\"flavorRef\":\"100\"}}","application/json"))
            .build();

     
      HttpResponse createServerResponse = HttpResponse.builder().statusCode(202).message("HTTP/1.1 202 Accepted")
            .payload(payloadFromResourceWithContentType("/new_server.json","application/json; charset=UTF-8")).build();

      NovaClient clientWithNewServer = requestsSendResponses(keystoneAuthWithAccessKeyAndSecretKey,
            responseWithKeystoneAccess, createServer, createServerResponse);

      assertEquals(clientWithNewServer.getServerClientForZone("az-1.region-a.geo-1").createServer("test-e92", "1241", "100").toString(),
              new ParseCreatedServerTest().expected().toString());
   }

   public void testCreateServerWithSecurityGroupsWhenResponseIs202() throws Exception {

      HttpRequest createServer = HttpRequest
         .builder()
         .method("POST")
         .endpoint(URI.create("https://compute.north.host/v1.1/3456/servers"))
         .headers(
               ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                     .put("X-Auth-Token", authToken).build())
         .payload(payloadFromStringWithContentType(
                  "{\"server\":{\"name\":\"test-e92\",\"imageRef\":\"1241\",\"flavorRef\":\"100\",\"security_groups\":[{\"name\":\"group2\"},{\"name\":\"group1\"}]}}","application/json"))
         .build();

  
      HttpResponse createServerResponse = HttpResponse.builder().statusCode(202).message("HTTP/1.1 202 Accepted")
         .payload(payloadFromResourceWithContentType("/new_server.json","application/json; charset=UTF-8")).build();


      NovaClient clientWithNewServer = requestsSendResponses(keystoneAuthWithAccessKeyAndSecretKey,
            responseWithKeystoneAccess, createServer, createServerResponse);

      assertEquals(clientWithNewServer.getServerClientForZone("az-1.region-a.geo-1").createServer("test-e92", "1241",
               "100", new CreateServerOptions().securityGroupNames("group1", "group2")).toString(),
              new ParseCreatedServerTest().expected().toString());
   }

}
