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
package org.jclouds.openstack.nova.v2_0.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiExpectTest;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;
import org.jclouds.openstack.nova.v2_0.parse.ParseCreatedServerTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseServerListTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * Tests annotation parsing of {@code ServerAsyncApi}
 *
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ServerAsyncApiTest")
public class ServerApiExpectTest extends BaseNovaApiExpectTest {

   public void testListServersWhenResponseIs2xx() throws Exception {
      HttpRequest listServers = HttpRequest
            .builder()
            .method("GET")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse listServersResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/server_list.json")).build();

      NovaApi apiWhenServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, listServers, listServersResponse);

      assertEquals(apiWhenServersExist.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1"));

      assertEquals(apiWhenServersExist.getServerApiForZone("az-1.region-a.geo-1").list().concat().toString(),
            new ParseServerListTest().expected().toString());
   }

   public void testListServersWhenReponseIs404IsEmpty() throws Exception {
      HttpRequest listServers = HttpRequest
            .builder()
            .method("GET")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse listServersResponse = HttpResponse.builder().statusCode(404).build();

      NovaApi apiWhenNoServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, listServers, listServersResponse);

      assertTrue(apiWhenNoServersExist.getServerApiForZone("az-1.region-a.geo-1").list().concat().isEmpty());
   }

   public void testCreateServerWhenResponseIs202() throws Exception {
      HttpRequest createServer = HttpRequest
            .builder()
            .method("POST")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken)
            .payload(payloadFromStringWithContentType(
                     "{\"server\":{\"name\":\"test-e92\",\"imageRef\":\"1241\",\"flavorRef\":\"100\"}}","application/json"))
            .build();


      HttpResponse createServerResponse = HttpResponse.builder().statusCode(202).message("HTTP/1.1 202 Accepted")
            .payload(payloadFromResourceWithContentType("/new_server.json","application/json; charset=UTF-8")).build();

      NovaApi apiWithNewServer = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, createServer, createServerResponse);

      assertEquals(apiWithNewServer.getServerApiForZone("az-1.region-a.geo-1").create("test-e92", "1241", "100").toString(),
              new ParseCreatedServerTest().expected().toString());
   }

   public void testCreateServerWithSecurityGroupsWhenResponseIs202() throws Exception {

      HttpRequest createServer = HttpRequest
         .builder()
         .method("POST")
         .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers")
         .addHeader("Accept", "application/json")
         .addHeader("X-Auth-Token", authToken)
         .payload(payloadFromStringWithContentType(
                  "{\"server\":{\"name\":\"test-e92\",\"imageRef\":\"1241\",\"flavorRef\":\"100\",\"security_groups\":[{\"name\":\"group1\"},{\"name\":\"group2\"}]}}","application/json"))
         .build();


      HttpResponse createServerResponse = HttpResponse.builder().statusCode(202).message("HTTP/1.1 202 Accepted")
         .payload(payloadFromResourceWithContentType("/new_server.json","application/json; charset=UTF-8")).build();


      NovaApi apiWithNewServer = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, createServer, createServerResponse);

      assertEquals(apiWithNewServer.getServerApiForZone("az-1.region-a.geo-1").create("test-e92", "1241",
               "100", new CreateServerOptions().securityGroupNames("group1", "group2")).toString(),
              new ParseCreatedServerTest().expected().toString());
   }

   public void testCreateImageWhenResponseIs2xx() throws Exception {
      String serverId = "123";
      String imageId = "456";
      String imageName = "foo";

      HttpRequest createImage = HttpRequest
            .builder()
            .method("POST")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/" + serverId + "/action")
                     .addHeader("Accept", "application/json")
                     .addHeader("X-Auth-Token", authToken)
            .payload(payloadFromStringWithContentType(
                  "{\"createImage\":{\"name\":\"" + imageName + "\", \"metadata\": {}}}", "application/json"))
               .build();

      HttpResponse createImageResponse = HttpResponse.builder()
            .statusCode(200)
            .headers(
                  ImmutableMultimap.<String, String> builder()
                  .put("Location", "https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/images/" + imageId).build()).build();

      NovaApi apiWhenServerExists = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, createImage, createImageResponse);

      assertEquals(apiWhenServerExists.getServerApiForZone("az-1.region-a.geo-1").createImageFromServer(imageName, serverId),
            imageId);
   }

   public void testCreateImageWhenResponseIs404IsEmpty() throws Exception {
      String serverId = "123";
      String imageName = "foo";

      HttpRequest createImage = HttpRequest
            .builder()
            .method("POST")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/" + serverId + "/action")
                     .addHeader("Accept", "application/json")
                     .addHeader("X-Auth-Token", authToken)
            .payload(payloadFromStringWithContentType(
                  "{\"createImage\":{\"name\":\"" + imageName + "\", \"metadata\": {}}}", "application/json"))
               .build();

      HttpResponse createImageResponse = HttpResponse.builder().statusCode(404).build();
      NovaApi apiWhenServerDoesNotExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, createImage, createImageResponse);

      try {
         apiWhenServerDoesNotExist.getServerApiForZone("az-1.region-a.geo-1").createImageFromServer(imageName, serverId);
         fail("Expected an exception.");
      } catch (Exception e) {
         ;
      }
   }
   
   public void testStopServerWhenResponseIs2xx() throws Exception {
      String serverId = "123";
      HttpRequest stopServer = HttpRequest
            .builder()
            .method("POST")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/" + serverId + "/action")
            .addHeader("Accept", "*/*")
            .addHeader("X-Auth-Token", authToken)
            .payload(payloadFromStringWithContentType(
                  "{\"os-stop\":null}", "application/json"))
               .build();

      HttpResponse stopServerResponse = HttpResponse.builder().statusCode(202).build();

      NovaApi apiWhenServerExists = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, stopServer, stopServerResponse);

      apiWhenServerExists.getServerApiForZone("az-1.region-a.geo-1").stop(serverId);
   }
   
   public void testStopServerWhenResponseIs404() throws Exception {
      String serverId = "123";
      HttpRequest stopServer = HttpRequest
            .builder()
            .method("POST")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/" + serverId + "/action")
            .addHeader("Accept", "*/*")
            .addHeader("X-Auth-Token", authToken)

            .payload(payloadFromStringWithContentType(
                  "{\"os-stop\":null}", "application/json"))
               .build();

      HttpResponse stopServerResponse = HttpResponse.builder().statusCode(404).build();

      NovaApi apiWhenServerExists = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, stopServer, stopServerResponse);

      try {
         apiWhenServerExists.getServerApiForZone("az-1.region-a.geo-1").stop(serverId);
         fail("Expected an exception.");
      } catch (Exception e) {
         ;
      }
   }
   
   public void testStartServerWhenResponseIs2xx() throws Exception {
      String serverId = "123";
      HttpRequest startServer = HttpRequest
            .builder()
            .method("POST")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/" + serverId + "/action")
            .addHeader("Accept", "*/*")
            .addHeader("X-Auth-Token", authToken)

            .payload(payloadFromStringWithContentType(
                  "{\"os-start\":null}", "application/json"))
               .build();

      HttpResponse startServerResponse = HttpResponse.builder().statusCode(202).build();

      NovaApi apiWhenServerExists = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, startServer, startServerResponse);

      apiWhenServerExists.getServerApiForZone("az-1.region-a.geo-1").start(serverId);
   }
   
   public void testStartServerWhenResponseIs404() throws Exception {
      String serverId = "123";
      HttpRequest startServer = HttpRequest
            .builder()
            .method("POST")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/" + serverId + "/action")
            .addHeader("Accept", "*/*")
            .addHeader("X-Auth-Token", authToken)
            .payload(payloadFromStringWithContentType(
                  "{\"os-startp\":null}", "application/json"))
               .build();

      HttpResponse startServerResponse = HttpResponse.builder().statusCode(404).build();

      NovaApi apiWhenServerExists = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess, startServer, startServerResponse);

      try {
         apiWhenServerExists.getServerApiForZone("az-1.region-a.geo-1").start(serverId);
         fail("Expected an exception.");
      } catch (Exception e) {
         ;
      }
   }

}
