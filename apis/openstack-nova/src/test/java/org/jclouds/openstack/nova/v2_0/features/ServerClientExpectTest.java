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

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.NovaClient;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaClientExpectTest;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;
import org.jclouds.openstack.nova.v2_0.parse.ParseCreatedServerTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseServerListTest;
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
            .endpoint(URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build()).build();

      HttpResponse listServersResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/server_list.json")).build();

      NovaClient clientWhenServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, listServers, listServersResponse);

      assertEquals(clientWhenServersExist.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1"));

      assertEquals(clientWhenServersExist.getServerClientForZone("az-1.region-a.geo-1").listServers().toString(),
            new ParseServerListTest().expected().toString());
   }

   public void testListServersWhenReponseIs404IsEmpty() throws Exception {
      HttpRequest listServers = HttpRequest
            .builder()
            .method("GET")
            .endpoint(URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build()).build();

      HttpResponse listServersResponse = HttpResponse.builder().statusCode(404).build();

      NovaClient clientWhenNoServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, listServers, listServersResponse);

      assertTrue(clientWhenNoServersExist.getServerClientForZone("az-1.region-a.geo-1").listServers().isEmpty());
   }

   public void testCreateServerWhenResponseIs202() throws Exception {
      HttpRequest createServer = HttpRequest
            .builder()
            .method("POST")
            .endpoint(URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build())
            .payload(payloadFromStringWithContentType(
                     "{\"server\":{\"name\":\"test-e92\",\"imageRef\":\"1241\",\"flavorRef\":\"100\"}}","application/json"))
            .build();


      HttpResponse createServerResponse = HttpResponse.builder().statusCode(202).message("HTTP/1.1 202 Accepted")
            .payload(payloadFromResourceWithContentType("/new_server.json","application/json; charset=UTF-8")).build();

      NovaClient clientWithNewServer = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, createServer, createServerResponse);

      assertEquals(clientWithNewServer.getServerClientForZone("az-1.region-a.geo-1").createServer("test-e92", "1241", "100").toString(),
              new ParseCreatedServerTest().expected().toString());
   }

   public void testCreateServerWithSecurityGroupsWhenResponseIs202() throws Exception {

      HttpRequest createServer = HttpRequest
         .builder()
         .method("POST")
         .endpoint(URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers"))
         .headers(
               ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                     .put("X-Auth-Token", authToken).build())
         .payload(payloadFromStringWithContentType(
                  "{\"server\":{\"name\":\"test-e92\",\"imageRef\":\"1241\",\"flavorRef\":\"100\",\"security_groups\":[{\"name\":\"group1\"},{\"name\":\"group2\"}]}}","application/json"))
         .build();


      HttpResponse createServerResponse = HttpResponse.builder().statusCode(202).message("HTTP/1.1 202 Accepted")
         .payload(payloadFromResourceWithContentType("/new_server.json","application/json; charset=UTF-8")).build();


      NovaClient clientWithNewServer = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, createServer, createServerResponse);

      assertEquals(clientWithNewServer.getServerClientForZone("az-1.region-a.geo-1").createServer("test-e92", "1241",
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
			   .endpoint(URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/" + serverId + "/action"))
			   .headers(
					   ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
					   .put("X-Auth-Token", authToken).build())
			   .payload(payloadFromStringWithContentType(
					   "{\"createImage\":{\"name\":\"" + imageName + "\", \"metadata\": {}}}", "application/json"))
               .build();

	   HttpResponse createImageResponse = HttpResponse.builder()
			   .statusCode(200)
			   .headers(
					   ImmutableMultimap.<String, String> builder()
					   .put("Location", "https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/images/" + imageId).build()).build();

	   NovaClient clientWhenServerExists = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
	            responseWithKeystoneAccess, createImage, createImageResponse);

	   assertEquals(clientWhenServerExists.getServerClientForZone("az-1.region-a.geo-1").createImageFromServer(imageName, serverId),
			   imageId);
   }

   public void testCreateImageWhenResponseIs404IsEmpty() throws Exception {
	   String serverId = "123";
	   String imageName = "foo";

	   HttpRequest createImage = HttpRequest
			   .builder()
			   .method("POST")
			   .endpoint(URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/" + serverId + "/action"))
			   .headers(
					   ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
					   .put("X-Auth-Token", authToken)
					   .put("Content-Type", "application/json").build())
			   .payload(payloadFromStringWithContentType(
					   "{\"createImage\":{\"name\":\"" + imageName + "\", \"metadata\": {}}}", "application/json"))
               .build();

	   HttpResponse createImageResponse = HttpResponse.builder().statusCode(404).build();
	   NovaClient clientWhenServerDoesNotExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
	            responseWithKeystoneAccess, createImage, createImageResponse);

	   try {
		   clientWhenServerDoesNotExist.getServerClientForZone("az-1.region-a.geo-1").createImageFromServer(imageName, serverId);
		   fail("Expected an exception.");
	   } catch (Exception e) {
		   ;
	   }
   }

}
