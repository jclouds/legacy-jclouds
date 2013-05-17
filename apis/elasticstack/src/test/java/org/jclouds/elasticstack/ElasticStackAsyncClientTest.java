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
package org.jclouds.elasticstack;

import static org.jclouds.reflect.Reflection2.method;
import static org.testng.Assert.assertEquals;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.elasticstack.binders.BindServerToPlainTextStringTest;
import org.jclouds.elasticstack.domain.CreateDriveRequest;
import org.jclouds.elasticstack.domain.Drive;
import org.jclouds.elasticstack.domain.DriveData;
import org.jclouds.elasticstack.domain.ImageConversionType;
import org.jclouds.elasticstack.domain.Server;
import org.jclouds.elasticstack.functions.KeyValuesDelimitedByBlankLinesToDriveInfo;
import org.jclouds.elasticstack.functions.KeyValuesDelimitedByBlankLinesToServerInfo;
import org.jclouds.elasticstack.functions.ListOfKeyValuesDelimitedByBlankLinesToDriveInfoSet;
import org.jclouds.elasticstack.functions.ListOfKeyValuesDelimitedByBlankLinesToServerInfoSet;
import org.jclouds.elasticstack.functions.ReturnPayload;
import org.jclouds.elasticstack.functions.SplitNewlines;
import org.jclouds.fallbacks.MapHttp4xxCodesToExceptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.rest.internal.BaseAsyncClientTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.reflect.Invokable;
/**
 * Tests behavior of {@code ElasticStackAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "ElasticStackAsyncClientTest")
public class ElasticStackAsyncClientTest extends BaseAsyncClientTest<ElasticStackAsyncClient> {
   public void testListServers() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ElasticStackAsyncClient.class, "listServers");
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(httpRequest, "GET https://api-lon-p.elastichosts.com/servers/list HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      // now make sure request filters apply by replaying
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api-lon-p.elastichosts.com/servers/list HTTP/1.1");
      // for example, using basic authentication, we should get "only one"
      // header
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\nAuthorization: Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==\n");
      assertPayloadEquals(httpRequest, null, null, false);

      // TODO: insert expected response class, which probably extends ParseJson
      assertResponseParserClassEquals(method, httpRequest, SplitNewlines.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testListServerInfo() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ElasticStackAsyncClient.class, "listServerInfo");
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(httpRequest, "GET https://api-lon-p.elastichosts.com/servers/info HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ListOfKeyValuesDelimitedByBlankLinesToServerInfoSet.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testGetServerInfo() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ElasticStackAsyncClient.class, "getServerInfo", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("uuid"));

      assertRequestLineEquals(httpRequest, "GET https://api-lon-p.elastichosts.com/servers/uuid/info HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToServerInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testCreateAndStartServer() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ElasticStackAsyncClient.class, "createAndStartServer", Server.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(
            BindServerToPlainTextStringTest.SERVER));

      assertRequestLineEquals(httpRequest, "POST https://api-lon-p.elastichosts.com/servers/create HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, BindServerToPlainTextStringTest.CREATED_SERVER, "text/plain", false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToServerInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testCreateServer() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ElasticStackAsyncClient.class, "createServer", Server.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(
            BindServerToPlainTextStringTest.SERVER));

      assertRequestLineEquals(httpRequest, "POST https://api-lon-p.elastichosts.com/servers/create/stopped HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, BindServerToPlainTextStringTest.CREATED_SERVER, "text/plain", false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToServerInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testSetServerConfiguration() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ElasticStackAsyncClient.class, "setServerConfiguration", String.class, Server.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("100",
            BindServerToPlainTextStringTest.SERVER));

      assertRequestLineEquals(httpRequest, "POST https://api-lon-p.elastichosts.com/servers/100/set HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, BindServerToPlainTextStringTest.CREATED_SERVER, "text/plain", false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToServerInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testDestroyServer() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ElasticStackAsyncClient.class, "destroyServer", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("uuid"));

      assertRequestLineEquals(httpRequest, "POST https://api-lon-p.elastichosts.com/servers/uuid/destroy HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testStartServer() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ElasticStackAsyncClient.class, "startServer", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("uuid"));

      assertRequestLineEquals(httpRequest, "POST https://api-lon-p.elastichosts.com/servers/uuid/start HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testStopServer() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ElasticStackAsyncClient.class, "stopServer", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("uuid"));

      assertRequestLineEquals(httpRequest, "POST https://api-lon-p.elastichosts.com/servers/uuid/stop HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testShutdownServer() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ElasticStackAsyncClient.class, "shutdownServer", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("uuid"));

      assertRequestLineEquals(httpRequest, "POST https://api-lon-p.elastichosts.com/servers/uuid/shutdown HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testResetServer() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ElasticStackAsyncClient.class, "resetServer", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("uuid"));

      assertRequestLineEquals(httpRequest, "POST https://api-lon-p.elastichosts.com/servers/uuid/reset HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testListDrives() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ElasticStackAsyncClient.class, "listDrives");
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(httpRequest, "GET https://api-lon-p.elastichosts.com/drives/list HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      // now make sure request filters apply by replaying
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api-lon-p.elastichosts.com/drives/list HTTP/1.1");
      // for example, using basic authentication, we should get "only one"
      // header
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\nAuthorization: Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==\n");
      assertPayloadEquals(httpRequest, null, null, false);

      // TODO: insert expected response class, which probably extends ParseJson
      assertResponseParserClassEquals(method, httpRequest, SplitNewlines.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testListDriveInfo() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ElasticStackAsyncClient.class, "listDriveInfo");
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(httpRequest, "GET https://api-lon-p.elastichosts.com/drives/info HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ListOfKeyValuesDelimitedByBlankLinesToDriveInfoSet.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testGetDriveInfo() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ElasticStackAsyncClient.class, "getDriveInfo", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("uuid"));

      assertRequestLineEquals(httpRequest, "GET https://api-lon-p.elastichosts.com/drives/uuid/info HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToDriveInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testCreateDrive() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ElasticStackAsyncClient.class, "createDrive", Drive.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(
            new CreateDriveRequest.Builder().name("foo").size(10000l).build()));

      assertRequestLineEquals(httpRequest, "POST https://api-lon-p.elastichosts.com/drives/create HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, "name foo\nsize 10000", "text/plain", false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToDriveInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testSetDriveData() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ElasticStackAsyncClient.class, "setDriveData", String.class, DriveData.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("100",
            new DriveData.Builder().name("foo").size(10000l).tags(ImmutableList.of("production", "candy")).build()));

      assertRequestLineEquals(httpRequest, "POST https://api-lon-p.elastichosts.com/drives/100/set HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, "name foo\nsize 10000\ntags production candy", "text/plain", false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToDriveInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testDestroyDrive() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ElasticStackAsyncClient.class, "destroyDrive", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("uuid"));

      assertRequestLineEquals(httpRequest, "POST https://api-lon-p.elastichosts.com/drives/uuid/destroy HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testImageDrive() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ElasticStackAsyncClient.class, "imageDrive", String.class, String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("100", "200"));

      assertRequestLineEquals(httpRequest, "POST https://api-lon-p.elastichosts.com/drives/200/image/100 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testImageDriveWithConversion() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ElasticStackAsyncClient.class, "imageDrive", String.class, String.class,
            ImageConversionType.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("100", "200",
            ImageConversionType.GUNZIP));

      assertRequestLineEquals(httpRequest, "POST https://api-lon-p.elastichosts.com/drives/200/image/100/gunzip HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testReadDrive() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ElasticStackAsyncClient.class, "readDrive", String.class, long.class, long.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("100", 1024, 2048));

      assertRequestLineEquals(httpRequest, "POST https://api-lon-p.elastichosts.com/drives/100/read/1024/2048 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/octet-stream\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReturnPayload.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testWriteDrive() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ElasticStackAsyncClient.class, "writeDrive", String.class, Payload.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("100",
            Payloads.newStringPayload("foo")));

      assertRequestLineEquals(httpRequest, "POST https://api-lon-p.elastichosts.com/drives/100/write HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, "foo", MediaType.APPLICATION_OCTET_STREAM, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testWriteDriveOffset() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ElasticStackAsyncClient.class, "writeDrive", String.class, Payload.class, long.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("100",
            Payloads.newStringPayload("foo"), 2048));

      assertRequestLineEquals(httpRequest, "POST https://api-lon-p.elastichosts.com/drives/100/write/2048 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, "foo", MediaType.APPLICATION_OCTET_STREAM, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), BasicAuthentication.class);
   }

   @Override
   protected ApiMetadata createApiMetadata() {
      return new ElasticStackApiMetadata();
   }

}
