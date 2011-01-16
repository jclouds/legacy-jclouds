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

package org.jclouds.atmos;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Properties;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.atmos.blobstore.functions.BlobToObject;
import org.jclouds.atmos.config.AtmosRestClientModule;
import org.jclouds.atmos.domain.AtmosObject;
import org.jclouds.atmos.filters.SignRequest;
import org.jclouds.atmos.functions.ParseDirectoryListFromContentAndHeaders;
import org.jclouds.atmos.functions.ParseObjectFromHeadersAndHttpContent;
import org.jclouds.atmos.functions.ParseSystemMetadataFromHeaders;
import org.jclouds.atmos.functions.ReturnEndpointIfAlreadyExists;
import org.jclouds.atmos.options.ListOptions;
import org.jclouds.blobstore.binders.BindBlobToMultipartFormTest;
import org.jclouds.blobstore.functions.ThrowContainerNotFoundOn404;
import org.jclouds.blobstore.functions.ThrowKeyNotFoundOn404;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.functions.ParseURIFromListOrLocationHeaderIf20x;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.http.options.GetOptions;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code AtmosAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "AtmosAsyncClientTest")
public class AtmosAsyncClientTest extends RestClientTest<AtmosAsyncClient> {

   private BlobToObject blobToObject;

   public void testListDirectories() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AtmosAsyncClient.class.getMethod("listDirectories", Array
               .newInstance(ListOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method);

      assertRequestLineEquals(request, "GET https://accesspoint.atmosonline.com/rest/namespace HTTP/1.1");
      assertNonPayloadHeadersEqual(request, HttpHeaders.ACCEPT + ": text/xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseDirectoryListFromContentAndHeaders.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testListDirectory() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AtmosAsyncClient.class.getMethod("listDirectory", String.class, Array.newInstance(
               ListOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, "directory");

      assertRequestLineEquals(request, "GET https://accesspoint.atmosonline.com/rest/namespace/directory/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, HttpHeaders.ACCEPT + ": text/xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseDirectoryListFromContentAndHeaders.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ThrowContainerNotFoundOn404.class);

      checkFilters(request);
   }

   public void testListDirectoriesOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AtmosAsyncClient.class.getMethod("listDirectories", Array
               .newInstance(ListOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, new ListOptions().limit(1).token("asda"));

      assertRequestLineEquals(request, "GET https://accesspoint.atmosonline.com/rest/namespace HTTP/1.1");
      assertNonPayloadHeadersEqual(request, HttpHeaders.ACCEPT + ": text/xml\nx-emc-limit: 1\nx-emc-token: asda\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseDirectoryListFromContentAndHeaders.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testListDirectoryOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AtmosAsyncClient.class.getMethod("listDirectory", String.class, Array.newInstance(
               ListOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, "directory", new ListOptions().limit(1).token("asda"));

      assertRequestLineEquals(request, "GET https://accesspoint.atmosonline.com/rest/namespace/directory/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, HttpHeaders.ACCEPT + ": text/xml\nx-emc-limit: 1\nx-emc-token: asda\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseDirectoryListFromContentAndHeaders.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ThrowContainerNotFoundOn404.class);

      checkFilters(request);
   }

   public void testCreateDirectory() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AtmosAsyncClient.class.getMethod("createDirectory", String.class);
      HttpRequest request = processor.createRequest(method, "dir");

      assertRequestLineEquals(request, "POST https://accesspoint.atmosonline.com/rest/namespace/dir/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, HttpHeaders.ACCEPT + ": */*\n");
      assertPayloadEquals(request, "", "application/octet-stream", false);

      assertResponseParserClassEquals(method, request, ParseURIFromListOrLocationHeaderIf20x.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEndpointIfAlreadyExists.class);

      checkFilters(request);
   }

   public void testCreateFile() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AtmosAsyncClient.class.getMethod("createFile", String.class, AtmosObject.class);
      HttpRequest request = processor.createRequest(method, "dir", blobToObject
               .apply(BindBlobToMultipartFormTest.TEST_BLOB));

      assertRequestLineEquals(request, "POST https://accesspoint.atmosonline.com/rest/namespace/dir/hello HTTP/1.1");
      assertNonPayloadHeadersEqual(request, HttpHeaders.ACCEPT + ": */*\n");
      assertPayloadEquals(request, "hello", "text/plain", false);

      assertResponseParserClassEquals(method, request, ParseURIFromListOrLocationHeaderIf20x.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testUpdateFile() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AtmosAsyncClient.class.getMethod("updateFile", String.class, AtmosObject.class);
      HttpRequest request = processor.createRequest(method, "dir", blobToObject
               .apply(BindBlobToMultipartFormTest.TEST_BLOB));

      assertRequestLineEquals(request, "PUT https://accesspoint.atmosonline.com/rest/namespace/dir/hello HTTP/1.1");
      assertNonPayloadHeadersEqual(request, HttpHeaders.ACCEPT + ": */*\n");
      assertPayloadEquals(request, "hello", "text/plain", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ThrowKeyNotFoundOn404.class);

      checkFilters(request);
   }

   public void testReadFile() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AtmosAsyncClient.class.getMethod("readFile", String.class, GetOptions[].class);
      HttpRequest request = processor.createRequest(method, "dir/file");

      assertRequestLineEquals(request, "GET https://accesspoint.atmosonline.com/rest/namespace/dir/file HTTP/1.1");
      assertNonPayloadHeadersEqual(request, HttpHeaders.ACCEPT + ": */*\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseObjectFromHeadersAndHttpContent.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetSystemMetadata() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AtmosAsyncClient.class.getMethod("getSystemMetadata", String.class);
      HttpRequest request = processor.createRequest(method, "dir/file");

      assertRequestLineEquals(request, "HEAD https://accesspoint.atmosonline.com/rest/namespace/dir/file HTTP/1.1");
      assertNonPayloadHeadersEqual(request, HttpHeaders.ACCEPT + ": */*\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSystemMetadataFromHeaders.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDeletePath() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AtmosAsyncClient.class.getMethod("deletePath", String.class);
      HttpRequest request = processor.createRequest(method, "dir/file");

      assertRequestLineEquals(request, "DELETE https://accesspoint.atmosonline.com/rest/namespace/dir/file HTTP/1.1");
      assertNonPayloadHeadersEqual(request, HttpHeaders.ACCEPT + ": */*\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testNewObject() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AtmosAsyncClient.class.getMethod("newObject");
      assertEquals(method.getReturnType(), AtmosObject.class);
   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), SignRequest.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<AtmosAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<AtmosAsyncClient>>() {
      };
   }

   @BeforeClass
   @Override
   protected void setupFactory() throws IOException {
      super.setupFactory();
      blobToObject = injector.getInstance(BlobToObject.class);
   }

   @Override
   protected Module createModule() {
      return new TestAtmosRestClientModule();
   }

   @RequiresHttp
   @ConfiguresRestClient
   private static final class TestAtmosRestClientModule extends AtmosRestClientModule {
      @Override
      protected void configure() {
         super.configure();
      }

      @Override
      protected String provideTimeStamp(@TimeStamp Supplier<String> cache) {
         return "Thu, 05 Jun 2008 16:38:19 GMT";
      }
   }

   @Override
   public RestContextSpec<?, ?> createContextSpec() {
      return new RestContextFactory().createContextSpec("atmos", "identity", "credential", new Properties());
   }

}
