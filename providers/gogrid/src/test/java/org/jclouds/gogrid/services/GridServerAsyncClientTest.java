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

import static org.jclouds.reflect.Reflection2.method;

import java.io.IOException;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.gogrid.domain.PowerCommand;
import org.jclouds.gogrid.functions.ParseCredentialsFromJsonResponse;
import org.jclouds.gogrid.functions.ParseOptionsFromJsonResponse;
import org.jclouds.gogrid.options.GetServerListOptions;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.reflect.Invokable;
/**
 * Tests behavior of {@code GridServerAsyncClient}
 * 
 * @author Oleksiy Yarmula, Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "GridServerAsyncClientTest")
public class GridServerAsyncClientTest extends BaseGoGridAsyncClientTest<GridServerAsyncClient> {

   @Test
   public void testGetServerListWithOptions() throws NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(GridServerAsyncClient.class, "getServerList", GetServerListOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(
               new GetServerListOptions.Builder().onlySandboxServers()));

      assertRequestLineEquals(httpRequest,
               "GET https://api.gogrid.com/api/grid/server/list?v=1.6&isSandbox=true HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/server/list?"
               + "v=1.6&isSandbox=true&sig=e9aafd0a5d4c69bb24536be4bce8a528&api_key=identity " + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }

   @Test
   public void testGetServersByName() throws NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(GridServerAsyncClient.class, "getServersByName", String[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("server1"));

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/server/get?v=1.6&name=server1 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/server/get?" + "v=1.6&name=server1&"
               + "sig=e9aafd0a5d4c69bb24536be4bce8a528&api_key=identity " + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }

   @Test
   public void testGetServersById() throws NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(GridServerAsyncClient.class, "getServersById", long[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(123L));

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/server/get?v=1.6&id=123 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/server/get?" + "v=1.6&id=123&"
               + "sig=e9aafd0a5d4c69bb24536be4bce8a528&api_key=identity " + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }


   @Test
   public void testPowerServer() throws NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(GridServerAsyncClient.class, "power", String.class, PowerCommand.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("PowerServer",
               PowerCommand.RESTART));

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/server/power?v=1.6&"
               + "server=PowerServer&power=restart " + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/server/power?v=1.6&"
               + "server=PowerServer&power=restart&" + "sig=e9aafd0a5d4c69bb24536be4bce8a528&api_key=identity " + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }

   @Test
   public void testDeleteByName() throws NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(GridServerAsyncClient.class, "deleteByName", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("PowerServer"));

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/server/delete?v=1.6&"
               + "name=PowerServer " + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/server/delete?v=1.6&"
               + "name=PowerServer&" + "sig=e9aafd0a5d4c69bb24536be4bce8a528&api_key=identity " + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }

   @Test
   public void testGetRamSizes() throws NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(GridServerAsyncClient.class, "getRamSizes");
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/common/lookup/list?v=1.6&lookup=server.ram "
               + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseOptionsFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/common/lookup/list?v=1.6&lookup=server.ram&"
               + "sig=e9aafd0a5d4c69bb24536be4bce8a528&api_key=identity " + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }

   @Test
   public void testServerCredentials() throws NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(GridServerAsyncClient.class, "getServerCredentials", long.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(1));

      assertRequestLineEquals(httpRequest,
               "GET https://api.gogrid.com/api/support/grid/password/get?v=1.6&id=1 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseCredentialsFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);
   }

   @Test
   public void testTypes() throws NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(GridServerAsyncClient.class, "getTypes");
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(httpRequest,
               "GET https://api.gogrid.com/api/common/lookup/list?v=1.6&lookup=server.type HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseOptionsFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);
   }

   @Test
   public void testEditServerDescription() throws NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(GridServerAsyncClient.class, "editServerDescription", long.class, String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(2, "newDesc"));

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/server/edit?v=1.6&"
            + "id=2&description=newDesc HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/server/edit?v=1.6&"
            + "id=2&description=newDesc&" + "sig=e9aafd0a5d4c69bb24536be4bce8a528&api_key=identity " + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }
   
   @Test
   public void testEditServerRam() throws NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(GridServerAsyncClient.class, "editServerRam", long.class, String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(2, "1GB"));

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/server/edit?v=1.6&"
            + "id=2&server.ram=1GB HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/server/edit?v=1.6&"
            + "id=2&server.ram=1GB&" + "sig=e9aafd0a5d4c69bb24536be4bce8a528&api_key=identity " + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }
   
   @Test
   public void testEditServerType() throws NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(GridServerAsyncClient.class, "editServerType", long.class, String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(2, "web"));

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/server/edit?v=1.6&"
            + "id=2&server.type=web HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/server/edit?v=1.6&"
            + "id=2&server.type=web&" + "sig=e9aafd0a5d4c69bb24536be4bce8a528&api_key=identity " + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }
}
