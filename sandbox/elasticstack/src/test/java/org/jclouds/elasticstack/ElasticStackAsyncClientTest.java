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

package org.jclouds.elasticstack;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

import javax.ws.rs.core.MediaType;

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
import org.jclouds.elasticstack.options.ReadDriveOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.inject.TypeLiteral;

/**
 * Tests annotation parsing of {@code ElasticStackAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "elasticstack.ElasticStackAsyncClientTest")
public class ElasticStackAsyncClientTest extends RestClientTest<ElasticStackAsyncClient> {
   public void testListServers() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ElasticStackAsyncClient.class.getMethod("listServers");
      GeneratedHttpRequest<ElasticStackAsyncClient> httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET https://api.elasticstack.com/servers/list HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      // now make sure request filters apply by replaying
      Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);
      Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.elasticstack.com/servers/list HTTP/1.1");
      // for example, using basic authentication, we should get "only one"
      // header
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\nAuthorization: Basic Zm9vOmJhcg==\n");
      assertPayloadEquals(httpRequest, null, null, false);

      // TODO: insert expected response class, which probably extends ParseJson
      assertResponseParserClassEquals(method, httpRequest, SplitNewlines.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testListServerInfo() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ElasticStackAsyncClient.class.getMethod("listServerInfo");
      GeneratedHttpRequest<ElasticStackAsyncClient> httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET https://api.elasticstack.com/servers/info HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ListOfKeyValuesDelimitedByBlankLinesToServerInfoSet.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testGetServerInfo() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ElasticStackAsyncClient.class.getMethod("getServerInfo", String.class);
      GeneratedHttpRequest<ElasticStackAsyncClient> httpRequest = processor.createRequest(method, "uuid");

      assertRequestLineEquals(httpRequest, "GET https://api.elasticstack.com/servers/uuid/info HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToServerInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testCreateAndStartServer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ElasticStackAsyncClient.class.getMethod("createAndStartServer", Server.class);
      GeneratedHttpRequest<ElasticStackAsyncClient> httpRequest = processor.createRequest(method,
            BindServerToPlainTextStringTest.SERVER);

      assertRequestLineEquals(httpRequest, "POST https://api.elasticstack.com/servers/create HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, BindServerToPlainTextStringTest.CREATED_SERVER, "text/plain", false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToServerInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testCreateServer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ElasticStackAsyncClient.class.getMethod("createServer", Server.class);
      GeneratedHttpRequest<ElasticStackAsyncClient> httpRequest = processor.createRequest(method,
            BindServerToPlainTextStringTest.SERVER);

      assertRequestLineEquals(httpRequest, "POST https://api.elasticstack.com/servers/create/stopped HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, BindServerToPlainTextStringTest.CREATED_SERVER, "text/plain", false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToServerInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testSetServerConfiguration() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ElasticStackAsyncClient.class.getMethod("setServerConfiguration", String.class, Server.class);
      GeneratedHttpRequest<ElasticStackAsyncClient> httpRequest = processor.createRequest(method, "100",
            BindServerToPlainTextStringTest.SERVER);

      assertRequestLineEquals(httpRequest, "POST https://api.elasticstack.com/servers/100/set HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, BindServerToPlainTextStringTest.CREATED_SERVER, "text/plain", false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToServerInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testDestroyServer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ElasticStackAsyncClient.class.getMethod("destroyServer", String.class);
      GeneratedHttpRequest<ElasticStackAsyncClient> httpRequest = processor.createRequest(method, "uuid");

      assertRequestLineEquals(httpRequest, "POST https://api.elasticstack.com/servers/uuid/destroy HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testStartServer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ElasticStackAsyncClient.class.getMethod("startServer", String.class);
      GeneratedHttpRequest<ElasticStackAsyncClient> httpRequest = processor.createRequest(method, "uuid");

      assertRequestLineEquals(httpRequest, "POST https://api.elasticstack.com/servers/uuid/start HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testStopServer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ElasticStackAsyncClient.class.getMethod("stopServer", String.class);
      GeneratedHttpRequest<ElasticStackAsyncClient> httpRequest = processor.createRequest(method, "uuid");

      assertRequestLineEquals(httpRequest, "POST https://api.elasticstack.com/servers/uuid/stop HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testShutdownServer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ElasticStackAsyncClient.class.getMethod("shutdownServer", String.class);
      GeneratedHttpRequest<ElasticStackAsyncClient> httpRequest = processor.createRequest(method, "uuid");

      assertRequestLineEquals(httpRequest, "POST https://api.elasticstack.com/servers/uuid/shutdown HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testResetServer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ElasticStackAsyncClient.class.getMethod("resetServer", String.class);
      GeneratedHttpRequest<ElasticStackAsyncClient> httpRequest = processor.createRequest(method, "uuid");

      assertRequestLineEquals(httpRequest, "POST https://api.elasticstack.com/servers/uuid/reset HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testListDrives() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ElasticStackAsyncClient.class.getMethod("listDrives");
      GeneratedHttpRequest<ElasticStackAsyncClient> httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET https://api.elasticstack.com/drives/list HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      // now make sure request filters apply by replaying
      Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);
      Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.elasticstack.com/drives/list HTTP/1.1");
      // for example, using basic authentication, we should get "only one"
      // header
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\nAuthorization: Basic Zm9vOmJhcg==\n");
      assertPayloadEquals(httpRequest, null, null, false);

      // TODO: insert expected response class, which probably extends ParseJson
      assertResponseParserClassEquals(method, httpRequest, SplitNewlines.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testListDriveInfo() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ElasticStackAsyncClient.class.getMethod("listDriveInfo");
      GeneratedHttpRequest<ElasticStackAsyncClient> httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET https://api.elasticstack.com/drives/info HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ListOfKeyValuesDelimitedByBlankLinesToDriveInfoSet.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testGetDriveInfo() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ElasticStackAsyncClient.class.getMethod("getDriveInfo", String.class);
      GeneratedHttpRequest<ElasticStackAsyncClient> httpRequest = processor.createRequest(method, "uuid");

      assertRequestLineEquals(httpRequest, "GET https://api.elasticstack.com/drives/uuid/info HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToDriveInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testCreateDrive() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ElasticStackAsyncClient.class.getMethod("createDrive", Drive.class);
      GeneratedHttpRequest<ElasticStackAsyncClient> httpRequest = processor.createRequest(method,
            new CreateDriveRequest.Builder().name("foo").size(10000l).build());

      assertRequestLineEquals(httpRequest, "POST https://api.elasticstack.com/drives/create HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, "name foo\nsize 10000", "text/plain", false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToDriveInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testSetDriveData() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ElasticStackAsyncClient.class.getMethod("setDriveData", String.class, DriveData.class);
      GeneratedHttpRequest<ElasticStackAsyncClient> httpRequest = processor.createRequest(method, "100",
            new DriveData.Builder().name("foo").size(10000l).tags(ImmutableList.of("production", "candy")).build());

      assertRequestLineEquals(httpRequest, "POST https://api.elasticstack.com/drives/100/set HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, "name foo\nsize 10000\ntags production candy", "text/plain", false);

      assertResponseParserClassEquals(method, httpRequest, KeyValuesDelimitedByBlankLinesToDriveInfo.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testDestroyDrive() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ElasticStackAsyncClient.class.getMethod("destroyDrive", String.class);
      GeneratedHttpRequest<ElasticStackAsyncClient> httpRequest = processor.createRequest(method, "uuid");

      assertRequestLineEquals(httpRequest, "POST https://api.elasticstack.com/drives/uuid/destroy HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testImageDrive() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ElasticStackAsyncClient.class.getMethod("imageDrive", String.class, String.class);
      GeneratedHttpRequest<ElasticStackAsyncClient> httpRequest = processor.createRequest(method, "100", "200");

      assertRequestLineEquals(httpRequest, "POST https://api.elasticstack.com/drives/200/image/100 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testImageDriveWithConversion() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ElasticStackAsyncClient.class.getMethod("imageDrive", String.class, String.class,
            ImageConversionType.class);
      GeneratedHttpRequest<ElasticStackAsyncClient> httpRequest = processor.createRequest(method, "100", "200",
            ImageConversionType.GUNZIP);

      assertRequestLineEquals(httpRequest, "POST https://api.elasticstack.com/drives/200/image/100/gunzip HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testReadDrive() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ElasticStackAsyncClient.class.getMethod("readDrive", String.class);
      GeneratedHttpRequest<ElasticStackAsyncClient> httpRequest = processor.createRequest(method, "100");

      assertRequestLineEquals(httpRequest, "GET https://api.elasticstack.com/drives/100/read HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/octet-stream\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReturnPayload.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testReadDriveOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ElasticStackAsyncClient.class.getMethod("readDrive", String.class, ReadDriveOptions.class);
      GeneratedHttpRequest<ElasticStackAsyncClient> httpRequest = processor.createRequest(method, "100",
            new ReadDriveOptions().offset(1024).size(2048));

      assertRequestLineEquals(httpRequest, "POST https://api.elasticstack.com/drives/100/read/1024/2048 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/octet-stream\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReturnPayload.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testWriteDrive() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ElasticStackAsyncClient.class.getMethod("writeDrive", String.class, Payload.class);
      GeneratedHttpRequest<ElasticStackAsyncClient> httpRequest = processor.createRequest(method, "100",
            Payloads.newStringPayload("foo"));

      assertRequestLineEquals(httpRequest, "POST https://api.elasticstack.com/drives/100/write HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, "foo", MediaType.APPLICATION_OCTET_STREAM, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testWriteDriveOffset() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ElasticStackAsyncClient.class.getMethod("writeDrive", String.class, Payload.class, long.class);
      GeneratedHttpRequest<ElasticStackAsyncClient> httpRequest = processor.createRequest(method, "100",
            Payloads.newStringPayload("foo"), 2048);

      assertRequestLineEquals(httpRequest, "POST https://api.elasticstack.com/drives/100/write/2048 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/plain\n");
      assertPayloadEquals(httpRequest, "foo", MediaType.APPLICATION_OCTET_STREAM, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), BasicAuthentication.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<ElasticStackAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<ElasticStackAsyncClient>>() {
      };
   }

   @Override
   public RestContextSpec<ElasticStackClient, ElasticStackAsyncClient> createContextSpec() {
      Properties props = new Properties();
      props.setProperty("elasticstack.endpoint", "https://api.elasticstack.com");
      return new RestContextFactory().createContextSpec("elasticstack", "foo", "bar", props);
   }
}
