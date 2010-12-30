/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.gogrid.services;

import java.io.IOException;
import java.lang.reflect.Method;

import org.jclouds.gogrid.domain.PowerCommand;
import org.jclouds.gogrid.functions.ParseCredentialsFromJsonResponse;
import org.jclouds.gogrid.functions.ParseOptionsFromJsonResponse;
import org.jclouds.gogrid.functions.ParseServerFromJsonResponse;
import org.jclouds.gogrid.functions.ParseServerListFromJsonResponse;
import org.jclouds.gogrid.options.AddServerOptions;
import org.jclouds.gogrid.options.GetServerListOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code GridServerAsyncClient}
 * 
 * @author Oleksiy Yarmula
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "GridServerAsyncClientTest")
public class GridServerAsyncClientTest extends BaseGoGridAsyncClientTest<GridServerAsyncClient> {

   @Test
   public void testGetServerListNoOptions() throws NoSuchMethodException, IOException {
      Method method = GridServerAsyncClient.class.getMethod("getServerList", GetServerListOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/server/list?v=1.5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseServerListFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
      httpRequest = Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/server/list?"
               + "v=1.5&sig=3f446f171455fbb5574aecff4997b273&api_key=foo " + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }

   @Test
   public void testGetServerListWithOptions() throws NoSuchMethodException, IOException {
      Method method = GridServerAsyncClient.class.getMethod("getServerList", GetServerListOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method,
               new GetServerListOptions.Builder().onlySandboxServers());

      assertRequestLineEquals(httpRequest,
               "GET https://api.gogrid.com/api/grid/server/list?v=1.5&isSandbox=true HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseServerListFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
      httpRequest = Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/server/list?"
               + "v=1.5&isSandbox=true&sig=3f446f171455fbb5574aecff4997b273&api_key=foo " + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }

   @Test
   public void testGetServersByName() throws NoSuchMethodException, IOException {
      Method method = GridServerAsyncClient.class.getMethod("getServersByName", String[].class);
      HttpRequest httpRequest = processor.createRequest(method, "server1");

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/server/get?v=1.5&name=server1 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseServerListFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);
      httpRequest = Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/server/get?" + "v=1.5&name=server1&"
               + "sig=3f446f171455fbb5574aecff4997b273&api_key=foo " + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }

   @Test
   public void testGetServersById() throws NoSuchMethodException, IOException {
      Method method = GridServerAsyncClient.class.getMethod("getServersById", long[].class);
      HttpRequest httpRequest = processor.createRequest(method, 123L);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/server/get?v=1.5&id=123 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseServerListFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);
      httpRequest = Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/server/get?" + "v=1.5&id=123&"
               + "sig=3f446f171455fbb5574aecff4997b273&api_key=foo " + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }

   @Test
   public void testAddServerNoOptions() throws NoSuchMethodException, IOException {
      Method method = GridServerAsyncClient.class.getMethod("addServer", String.class, String.class, String.class,
               String.class, AddServerOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, "serverName", "img55",
               "memory", "127.0.0.1");

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/server/add?v=1.5&"
               + "name=serverName&server.ram=memory&image=img55&ip=127.0.0.1 " + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseServerFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
      httpRequest = Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/server/add?"
               + "v=1.5&name=serverName&server.ram=memory&" + "image=img55&ip=127.0.0.1&"
               + "sig=3f446f171455fbb5574aecff4997b273&api_key=foo " + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }

   @Test
   public void testAddServerOptions() throws NoSuchMethodException, IOException {
      Method method = GridServerAsyncClient.class.getMethod("addServer", String.class, String.class, String.class,
               String.class, AddServerOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, "serverName", "img55",
               "memory", "127.0.0.1", new AddServerOptions().asSandboxType().withDescription("fooy"));

      assertRequestLineEquals(
               httpRequest,
               "GET https://api.gogrid.com/api/grid/server/add?v=1.5&name=serverName&server.ram=memory&image=img55&ip=127.0.0.1&isSandbox=true&description=fooy HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseServerFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
      httpRequest = Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(
               httpRequest,
               "GET https://api.gogrid.com/api/grid/server/add?v=1.5&name=serverName&server.ram=memory&image=img55&ip=127.0.0.1&isSandbox=true&description=fooy&sig=3f446f171455fbb5574aecff4997b273&api_key=foo HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }

   @Test
   public void testPowerServer() throws NoSuchMethodException, IOException {
      Method method = GridServerAsyncClient.class.getMethod("power", String.class, PowerCommand.class);
      HttpRequest httpRequest = processor.createRequest(method, "PowerServer",
               PowerCommand.RESTART);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/server/power?v=1.5&"
               + "server=PowerServer&power=restart " + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseServerFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
      httpRequest = Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/server/power?v=1.5&"
               + "server=PowerServer&power=restart&" + "sig=3f446f171455fbb5574aecff4997b273&api_key=foo " + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }

   @Test
   public void testDeleteByName() throws NoSuchMethodException, IOException {
      Method method = GridServerAsyncClient.class.getMethod("deleteByName", String.class);
      HttpRequest httpRequest = processor.createRequest(method, "PowerServer");

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/server/delete?v=1.5&"
               + "name=PowerServer " + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseServerFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);
      httpRequest = Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/server/delete?v=1.5&"
               + "name=PowerServer&" + "sig=3f446f171455fbb5574aecff4997b273&api_key=foo " + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }

   @Test
   public void testGetRamSizes() throws NoSuchMethodException, IOException {
      Method method = GridServerAsyncClient.class.getMethod("getRamSizes");
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/common/lookup/list?v=1.5&lookup=server.ram "
               + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseOptionsFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
      httpRequest = Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/common/lookup/list?v=1.5&lookup=server.ram&"
               + "sig=3f446f171455fbb5574aecff4997b273&api_key=foo " + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }

   @Test
   public void testServerCredentials() throws NoSuchMethodException, IOException {
      Method method = GridServerAsyncClient.class.getMethod("getServerCredentials", long.class);
      HttpRequest httpRequest = processor.createRequest(method, 1);

      assertRequestLineEquals(httpRequest,
               "GET https://api.gogrid.com/api/support/grid/password/get?v=1.5&id=1 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseCredentialsFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<GridServerAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<GridServerAsyncClient>>() {
      };
   }

}
