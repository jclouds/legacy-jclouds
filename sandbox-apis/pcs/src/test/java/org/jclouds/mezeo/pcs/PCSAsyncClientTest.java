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
package org.jclouds.mezeo.pcs;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;
import java.util.Properties;

import org.jclouds.blobstore.binders.BindBlobToMultipartFormTest;
import org.jclouds.blobstore.functions.ReturnNullOnKeyNotFound;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseURIFromListOrLocationHeaderIf20x;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.http.functions.ReturnInputStream;
import org.jclouds.mezeo.pcs.PCSAsyncClient;
import org.jclouds.mezeo.pcs.PCSClient;
import org.jclouds.mezeo.pcs.PCSCloudAsyncClient.Response;
import org.jclouds.mezeo.pcs.blobstore.functions.BlobToPCSFile;
import org.jclouds.mezeo.pcs.config.PCSRestClientModule;
import org.jclouds.mezeo.pcs.domain.PCSFile;
import org.jclouds.mezeo.pcs.functions.AddMetadataItemIntoMap;
import org.jclouds.mezeo.pcs.options.PutBlockOptions;
import org.jclouds.mezeo.pcs.xml.ContainerHandler;
import org.jclouds.mezeo.pcs.xml.FileHandler;
import org.jclouds.rest.AsyncClientFactory;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code PCSClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "pcs2.PCSClientTest")
public class PCSAsyncClientTest extends RestClientTest<PCSAsyncClient> {

   public void testList() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PCSAsyncClient.class.getMethod("list");
      GeneratedHttpRequest request = processor.createRequest(method);

      assertRequestLineEquals(request, "GET http://root HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "X-Cloud-Depth: 2\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ContainerHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testCreateContainer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PCSAsyncClient.class.getMethod("createContainer", String.class);
      GeneratedHttpRequest request = processor.createRequest(method, "container");

      assertRequestLineEquals(request, "POST http://root/contents HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "<container><name>container</name></container>",
            "application/vnd.csp.container-info+xml", false);

      assertResponseParserClassEquals(method, request, ParseURIFromListOrLocationHeaderIf20x.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);

   }

   public void testDeleteContainer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PCSAsyncClient.class.getMethod("deleteContainer", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, URI.create("http://localhost/container/1234"));

      assertRequestLineEquals(request, "DELETE http://localhost/container/1234 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListURI() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PCSAsyncClient.class.getMethod("list", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, URI.create("http://localhost/mycontainer"));

      assertRequestLineEquals(request, "GET http://localhost/mycontainer HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "X-Cloud-Depth: 2\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ContainerHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetFileInfo() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PCSAsyncClient.class.getMethod("getFileInfo", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, URI.create("http://localhost/myfile"));

      assertRequestLineEquals(request, "GET http://localhost/myfile HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "X-Cloud-Depth: 2\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, FileHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnKeyNotFound.class);

      checkFilters(request);
   }

   public void testUploadFile() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PCSAsyncClient.class.getMethod("uploadFile", URI.class, PCSFile.class);
      GeneratedHttpRequest request = processor.createRequest(method, URI.create("http://localhost/mycontainer"), blobToPCSFile
            .apply(BindBlobToMultipartFormTest.TEST_BLOB));

      assertRequestLineEquals(request, "POST http://localhost/mycontainer/contents HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, BindBlobToMultipartFormTest.EXPECTS, "multipart/form-data; boundary=--JCLOUDS--",
            false);

      assertResponseParserClassEquals(method, request, ParseURIFromListOrLocationHeaderIf20x.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);

   }

   public void testUploadBlock() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PCSAsyncClient.class.getMethod("uploadBlock", URI.class, PCSFile.class, Array.newInstance(
            PutBlockOptions.class, 0).getClass());
      GeneratedHttpRequest request = processor.createRequest(method, URI.create("http://localhost/mycontainer"), blobToPCSFile
            .apply(BindBlobToMultipartFormTest.TEST_BLOB));

      assertRequestLineEquals(request, "PUT http://localhost/mycontainer/content HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "hello", "text/plain", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testDownloadFile() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PCSAsyncClient.class.getMethod("downloadFile", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, URI.create("http://localhost/container"));

      assertRequestLineEquals(request, "GET http://localhost/container/content HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnInputStream.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnKeyNotFound.class);

      checkFilters(request);

   }

   public void testDeleteFile() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PCSAsyncClient.class.getMethod("deleteFile", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method,
            new Object[] { URI.create("http://localhost/contents/file") });

      assertRequestLineEquals(request, "DELETE http://localhost/contents/file HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testPutMetadata() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PCSAsyncClient.class.getMethod("putMetadataItem", URI.class, String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, URI.create("http://localhost/contents/file"), "pow", "bar");

      assertRequestLineEquals(request, "PUT http://localhost/contents/file/metadata/pow HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "bar", "application/unknown", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);

   }

   public void testAddEntryToMap() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PCSAsyncClient.class.getMethod("addMetadataItemToMap", URI.class, String.class, Map.class);
      GeneratedHttpRequest request = processor.createRequest(method, URI.create("http://localhost/pow"), "newkey", ImmutableMap
            .of("key", "value"));

      assertRequestLineEquals(request, "GET http://localhost/pow/metadata/newkey HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, AddMetadataItemIntoMap.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   private BlobToPCSFile blobToPCSFile;

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), BasicAuthentication.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<PCSAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<PCSAsyncClient>>() {
      };
   }

   @BeforeClass
   @Override
   protected void setupFactory() throws IOException {
      super.setupFactory();
      blobToPCSFile = injector.getInstance(BlobToPCSFile.class);
   }

   @Override
   protected Module createModule() {
      return new TestPCSRestClientModule();
   }

   @RequiresHttp
   @ConfiguresRestClient
   private static final class TestPCSRestClientModule extends PCSRestClientModule {
      @Override
      protected void configure() {
         super.configure();
      }

      @Override
      protected Response provideCloudResponse(AsyncClientFactory factory) {
         return null;
      }

      @Override
      protected URI provideRootContainerUrl(Response response) {
         return URI.create("http://root");
      }
   }

   @Override
   public RestContextSpec<PCSClient, PCSAsyncClient> createContextSpec() {
      Properties properties = new Properties();
      properties.setProperty("pcs.endpoint", "http://goo");
      return new RestContextFactory().createContextSpec("pcs", "identity", "credential", properties);
   }
}
