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
package org.jclouds.atmos;

import static org.jclouds.reflect.Reflection2.method;
import static org.testng.Assert.assertEquals;

import java.io.IOException;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.atmos.blobstore.functions.BlobToObject;
import org.jclouds.atmos.config.AtmosRestClientModule;
import org.jclouds.atmos.domain.AtmosObject;
import org.jclouds.atmos.fallbacks.EndpointIfAlreadyExists;
import org.jclouds.atmos.filters.SignRequest;
import org.jclouds.atmos.functions.ParseDirectoryListFromContentAndHeaders;
import org.jclouds.atmos.functions.ParseObjectFromHeadersAndHttpContent;
import org.jclouds.atmos.functions.ParseSystemMetadataFromHeaders;
import org.jclouds.atmos.functions.ReturnTrueIfGroupACLIsOtherRead;
import org.jclouds.atmos.options.ListOptions;
import org.jclouds.atmos.options.PutOptions;
import org.jclouds.blobstore.BlobStoreFallbacks.ThrowContainerNotFoundOn404;
import org.jclouds.blobstore.BlobStoreFallbacks.ThrowKeyNotFoundOn404;
import org.jclouds.blobstore.binders.BindBlobToMultipartFormTest;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseURIFromListOrLocationHeaderIf20x;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.http.options.GetOptions;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.internal.BaseAsyncClientTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;
import com.google.inject.Module;
/**
 * Tests behavior of {@code AtmosAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "AtmosAsyncClientTest")
public class AtmosAsyncClientTest extends BaseAsyncClientTest<AtmosAsyncClient> {

   private BlobToObject blobToObject;

   public void testListDirectories() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AtmosAsyncClient.class, "listDirectories", ListOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(request, "GET https://accesspoint.atmosonline.com/rest/namespace HTTP/1.1");
      assertNonPayloadHeadersEqual(request, HttpHeaders.ACCEPT + ": text/xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseDirectoryListFromContentAndHeaders.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testListDirectory() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AtmosAsyncClient.class, "listDirectory", String.class, ListOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("directory"));

      assertRequestLineEquals(request, "GET https://accesspoint.atmosonline.com/rest/namespace/directory/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, HttpHeaders.ACCEPT + ": text/xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseDirectoryListFromContentAndHeaders.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, ThrowContainerNotFoundOn404.class);

      checkFilters(request);
   }

   public void testListDirectoriesOptions() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AtmosAsyncClient.class, "listDirectories", ListOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(new ListOptions().limit(1).token("asda")));

      assertRequestLineEquals(request, "GET https://accesspoint.atmosonline.com/rest/namespace HTTP/1.1");
      assertNonPayloadHeadersEqual(request, HttpHeaders.ACCEPT + ": text/xml\nx-emc-limit: 1\nx-emc-token: asda\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseDirectoryListFromContentAndHeaders.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testListDirectoryOptions() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AtmosAsyncClient.class, "listDirectory", String.class, ListOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("directory", new ListOptions().limit(1).token("asda")));

      assertRequestLineEquals(request, "GET https://accesspoint.atmosonline.com/rest/namespace/directory/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, HttpHeaders.ACCEPT + ": text/xml\nx-emc-limit: 1\nx-emc-token: asda\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseDirectoryListFromContentAndHeaders.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, ThrowContainerNotFoundOn404.class);

      checkFilters(request);
   }

   public void testCreateDirectory() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AtmosAsyncClient.class, "createDirectory", String.class, PutOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("dir"));

      assertRequestLineEquals(request, "POST https://accesspoint.atmosonline.com/rest/namespace/dir/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, HttpHeaders.ACCEPT + ": */*\n");
      assertPayloadEquals(request, "", "application/octet-stream", false);

      assertResponseParserClassEquals(method, request, ParseURIFromListOrLocationHeaderIf20x.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EndpointIfAlreadyExists.class);

      checkFilters(request);
   }

   public void testCreateDirectoryOptions() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AtmosAsyncClient.class, "createDirectory", String.class, PutOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("dir", PutOptions.Builder.publicRead()));

      assertRequestLineEquals(request, "POST https://accesspoint.atmosonline.com/rest/namespace/dir/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, HttpHeaders.ACCEPT
               + ": */*\nx-emc-groupacl: other=READ\nx-emc-useracl: root=FULL_CONTROL\n");
      assertPayloadEquals(request, "", "application/octet-stream", false);

      assertResponseParserClassEquals(method, request, ParseURIFromListOrLocationHeaderIf20x.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EndpointIfAlreadyExists.class);

      checkFilters(request);
   }

   public void testCreateFile() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AtmosAsyncClient.class, "createFile", String.class, AtmosObject.class,
               PutOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("dir", blobToObject
               .apply(BindBlobToMultipartFormTest.TEST_BLOB)));

      assertRequestLineEquals(request, "POST https://accesspoint.atmosonline.com/rest/namespace/dir/hello HTTP/1.1");
      assertNonPayloadHeadersEqual(request, HttpHeaders.ACCEPT + ": */*\nExpect: 100-continue\n");
      assertPayloadEquals(request, "hello", "text/plain", false);

      assertResponseParserClassEquals(method, request, ParseURIFromListOrLocationHeaderIf20x.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testCreateFileOptions() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AtmosAsyncClient.class, "createFile", String.class, AtmosObject.class,
               PutOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("dir", blobToObject
               .apply(BindBlobToMultipartFormTest.TEST_BLOB), PutOptions.Builder.publicRead()));

      assertRequestLineEquals(request, "POST https://accesspoint.atmosonline.com/rest/namespace/dir/hello HTTP/1.1");
      assertNonPayloadHeadersEqual(request, HttpHeaders.ACCEPT
               + ": */*\nExpect: 100-continue\nx-emc-groupacl: other=READ\nx-emc-useracl: root=FULL_CONTROL\n");
      assertPayloadEquals(request, "hello", "text/plain", false);

      assertResponseParserClassEquals(method, request, ParseURIFromListOrLocationHeaderIf20x.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testUpdateFile() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AtmosAsyncClient.class, "updateFile", String.class, AtmosObject.class,
               PutOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("dir", blobToObject
               .apply(BindBlobToMultipartFormTest.TEST_BLOB)));

      assertRequestLineEquals(request, "PUT https://accesspoint.atmosonline.com/rest/namespace/dir/hello HTTP/1.1");
      assertNonPayloadHeadersEqual(request, HttpHeaders.ACCEPT + ": */*\nExpect: 100-continue\n");
      assertPayloadEquals(request, "hello", "text/plain", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, ThrowKeyNotFoundOn404.class);

      checkFilters(request);
   }

   public void testUpdateFileOptions() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AtmosAsyncClient.class, "updateFile", String.class, AtmosObject.class,
               PutOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("dir", blobToObject
               .apply(BindBlobToMultipartFormTest.TEST_BLOB), PutOptions.Builder.publicRead()));

      assertRequestLineEquals(request, "PUT https://accesspoint.atmosonline.com/rest/namespace/dir/hello HTTP/1.1");
      assertNonPayloadHeadersEqual(request, HttpHeaders.ACCEPT
               + ": */*\nExpect: 100-continue\nx-emc-groupacl: other=READ\nx-emc-useracl: root=FULL_CONTROL\n");
      assertPayloadEquals(request, "hello", "text/plain", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, ThrowKeyNotFoundOn404.class);

      checkFilters(request);
   }

   public void testReadFile() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AtmosAsyncClient.class, "readFile", String.class, GetOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("dir/file"));

      assertRequestLineEquals(request, "GET https://accesspoint.atmosonline.com/rest/namespace/dir/file HTTP/1.1");
      assertNonPayloadHeadersEqual(request, HttpHeaders.ACCEPT + ": */*\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseObjectFromHeadersAndHttpContent.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetSystemMetadata() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AtmosAsyncClient.class, "getSystemMetadata", String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("dir/file"));

      assertRequestLineEquals(request, "HEAD https://accesspoint.atmosonline.com/rest/namespace/dir/file HTTP/1.1");
      assertNonPayloadHeadersEqual(request, HttpHeaders.ACCEPT + ": */*\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSystemMetadataFromHeaders.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDeletePath() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AtmosAsyncClient.class, "deletePath", String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("dir/file"));

      assertRequestLineEquals(request, "DELETE https://accesspoint.atmosonline.com/rest/namespace/dir/file HTTP/1.1");
      assertNonPayloadHeadersEqual(request, HttpHeaders.ACCEPT + ": */*\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testIsPublic() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AtmosAsyncClient.class, "isPublic", String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("dir/file"));

      assertRequestLineEquals(request, "HEAD https://accesspoint.atmosonline.com/rest/namespace/dir/file HTTP/1.1");
      assertNonPayloadHeadersEqual(request, HttpHeaders.ACCEPT + ": */*\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnTrueIfGroupACLIsOtherRead.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, FalseOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testNewObject() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AtmosAsyncClient.class, "newObject");
      assertEquals(method.getReturnType().getRawType(), AtmosObject.class);
   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), SignRequest.class);
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

   protected String provider = "atmos";

   @Override
   public ApiMetadata createApiMetadata() {
      return new AtmosApiMetadata();
   }

}
