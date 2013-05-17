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
package org.jclouds.gogrid.services;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.net.URI;

import org.jclouds.gogrid.GoGridClient;
import org.jclouds.gogrid.options.AddServerOptions;
import org.jclouds.gogrid.options.GetServerListOptions;
import org.jclouds.gogrid.parse.ParseServerListTest;
import org.jclouds.gogrid.parse.ParseServerTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "GridServerClientExpectTest")
public class GridServerClientExpectTest extends BaseGoGridRestClientExpectTest {

   HttpRequest addServer = HttpRequest.builder().method("GET")
                                      .endpoint("https://api.gogrid.com/api/grid/server/add")
                                      .addQueryParam("v", "1.6")
                                      .addQueryParam("name", "serverName")
                                      .addQueryParam("image", "img55")
                                      .addQueryParam("server.ram", "memory")
                                      .addQueryParam("ip", "127.0.0.1")
                                      .addQueryParam("sig", "e9aafd0a5d4c69bb24536be4bce8a528")
                                      .addQueryParam("api_key", "identity").build();

   public void testAddServerWhenResponseIs2xx() throws Exception {
      HttpResponse listGridServersResponse = HttpResponse.builder().statusCode(200).payload(
               payloadFromResourceWithContentType("/test_get_server_list.json", "application/json")).build();

      GoGridClient addServerWorked = requestSendsResponse(addServer, listGridServersResponse);

      assertEquals(addServerWorked.getServerServices().addServer("serverName", "img55", "memory", "127.0.0.1")
               .toString(), new ParseServerTest().expected().toString());
   }

   HttpRequest addServerOptions = HttpRequest.builder().method("GET")
                                             .endpoint("https://api.gogrid.com/api/grid/server/add")
                                             .addQueryParam("v", "1.6")
                                             .addQueryParam("name", "serverName")
                                             .addQueryParam("image", "img55")
                                             .addQueryParam("server.ram", "memory")
                                             .addQueryParam("ip", "127.0.0.1")
                                             .addQueryParam("isSandbox", "true")
                                             .addQueryParam("description", "fooy")
                                             .addQueryParam("sig", "e9aafd0a5d4c69bb24536be4bce8a528")
                                             .addQueryParam("api_key", "identity").build();

   public void testAddServerWithOptionsWhenResponseIs2xx() throws Exception {
      HttpResponse listGridServersResponse = HttpResponse.builder().statusCode(200).payload(
               payloadFromResourceWithContentType("/test_get_server_list.json", "application/json")).build();

      GoGridClient addServerWithOptionsWorked = requestSendsResponse(addServerOptions, listGridServersResponse);

      assertEquals(addServerWithOptionsWorked.getServerServices().addServer("serverName", "img55", "memory",
               "127.0.0.1", new AddServerOptions().asSandboxType().withDescription("fooy")).toString(),
               new ParseServerTest().expected().toString());
   }

   public void testGetServerListWhenResponseIs2xx() throws Exception {
      HttpRequest listGridServers = HttpRequest.builder().method("GET").endpoint(
               URI.create("https://api.gogrid.com/api/grid/server/list?" + "v=1.6&"
                        + "sig=e9aafd0a5d4c69bb24536be4bce8a528&api_key=identity")).build();

      HttpResponse listGridServersResponse = HttpResponse.builder().statusCode(200).payload(
               payloadFromResourceWithContentType("/test_get_server_list.json", "application/json")).build();

      GoGridClient clientWhenGridServersExist = requestSendsResponse(listGridServers, listGridServersResponse);

      assertEquals(clientWhenGridServersExist.getServerServices().getServerList().toString(), new ParseServerListTest()
               .expected().toString());
   }

   public void testGetServerListWhenReponseIs404IsEmpty() throws Exception {
      HttpRequest listGridServers = HttpRequest.builder().method("GET").endpoint(
               URI.create("https://api.gogrid.com/api/grid/server/list?" + "v=1.6&"
                        + "sig=e9aafd0a5d4c69bb24536be4bce8a528&api_key=identity")).build();

      HttpResponse listGridServersResponse = HttpResponse.builder().statusCode(404).payload(
               payloadFromResourceWithContentType("/test_error_handler.json", "application/json")).build();

      GoGridClient clientWhenNoGridServersExist = requestSendsResponse(listGridServers, listGridServersResponse);

      assertTrue(clientWhenNoGridServersExist.getServerServices().getServerList().isEmpty());
   }

   public void testGetServerListWithOptionsWhenResponseIs2xx() throws Exception {
      HttpRequest listGridServers = HttpRequest.builder().method("GET").endpoint(
               URI.create("https://api.gogrid.com/api/grid/server/list?" + "v=1.6&isSandbox=true&"
                        + "sig=e9aafd0a5d4c69bb24536be4bce8a528&api_key=identity")).build();

      HttpResponse listGridServersResponse = HttpResponse.builder().statusCode(200).payload(
               payloadFromResourceWithContentType("/test_get_server_list.json", "application/json")).build();

      GoGridClient clientWhenGridServersExist = requestSendsResponse(listGridServers, listGridServersResponse);

      assertEquals(clientWhenGridServersExist.getServerServices().getServerList(
               new GetServerListOptions.Builder().onlySandboxServers()).toString(), new ParseServerListTest()
               .expected().toString());
   }

   public void testGetServerCredentialsWhenNotFoundThrowsResourceNotFoundExceptionWithNiceMessage() throws Exception {
      HttpRequest listGridServers = HttpRequest.builder().method("GET").endpoint(
               URI.create("https://api.gogrid.com/api/support/grid/password/get?" + "v=1.6&id=11&"
                        + "sig=e9aafd0a5d4c69bb24536be4bce8a528&api_key=identity")).build();

      HttpResponse listGridServersResponse = HttpResponse.builder().statusCode(400).payload(
               payloadFromResourceWithContentType("/test_error_handler.json", "application/json")).build();

      GoGridClient clientWhenGridServersNotFound = requestSendsResponse(listGridServers, listGridServersResponse);
      try {
         clientWhenGridServersNotFound.getServerServices().getServerCredentials(11);
         fail("should have failed");
      } catch (ResourceNotFoundException e) {
         assertEquals(e.getMessage(), "No object found that matches your input criteria.");
      }
   }
}
