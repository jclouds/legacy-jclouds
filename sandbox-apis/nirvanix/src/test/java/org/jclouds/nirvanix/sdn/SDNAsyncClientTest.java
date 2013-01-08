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
package org.jclouds.nirvanix.sdn;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;
import java.util.Properties;

import org.jclouds.blobstore.binders.BindBlobToMultipartFormTest;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.http.functions.ReturnStringIf2xx;
import org.jclouds.nirvanix.sdn.config.SDNRestClientModule;
import org.jclouds.nirvanix.sdn.filters.AddSessionTokenToRequest;
import org.jclouds.nirvanix.sdn.filters.InsertUserContextIntoPath;
import org.jclouds.nirvanix.sdn.functions.ParseMetadataFromJsonResponse;
import org.jclouds.nirvanix.sdn.functions.ParseUploadInfoFromJsonResponse;
import org.jclouds.nirvanix.sdn.reference.SDNConstants;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * Tests behavior of {@code JaxrsAnnotationProcessor}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "sdn.SDNClient")
public class SDNAsyncClientTest extends RestClientTest<SDNAsyncClient> {

   public void testGetStorageNode() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SDNAsyncClient.class.getMethod("getStorageNode", String.class, long.class);
      GeneratedHttpRequest request = processor.createRequest(method, "adriansmovies", 734859264);

      assertRequestLineEquals(
            request,
            "GET http://services.nirvanix.com/ws/IMFS/GetStorageNode.ashx?output=json&destFolderPath=adriansmovies&sizeBytes=734859264 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseUploadInfoFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);

   }

   public void testUpload() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SDNAsyncClient.class.getMethod("upload", URI.class, String.class, String.class, Blob.class);
      Blob blob = BindBlobToMultipartFormTest.TEST_BLOB;
      GeneratedHttpRequest request = processor.createRequest(method, URI.create("http://uploader"), "token", "adriansmovies",
            blob);

      assertRequestLineEquals(request,
            "POST http://uploader/Upload.ashx?output=json&destFolderPath=adriansmovies&uploadToken=token HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      StringBuilder expects = new StringBuilder();
      expects.append("----JCLOUDS--\r\n");
      expects.append("Content-Disposition: form-data; name=\"hello\"\r\n");
      expects.append("Content-Type: text/plain\r\n\r\n");
      expects.append("hello\r\n");
      expects.append("----JCLOUDS----\r\n");

      assertPayloadEquals(request, expects.toString(), "multipart/form-data; boundary=--JCLOUDS--", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testSetMetadata() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SDNAsyncClient.class.getMethod("setMetadata", String.class, Map.class);
      GeneratedHttpRequest request = processor.createRequest(method, "adriansmovies/sushi.avi", ImmutableMap.of("Chef",
            "Kawasaki"));

      assertRequestLineEquals(
            request,
            "GET http://services.nirvanix.com/ws/Metadata/SetMetadata.ashx?output=json&path=adriansmovies/sushi.avi&metadata=chef:Kawasaki HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetMetadata() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SDNAsyncClient.class.getMethod("getMetadata", String.class);
      GeneratedHttpRequest request = processor.createRequest(method, "adriansmovies/sushi.avi");

      assertRequestLineEquals(request,
            "GET http://services.nirvanix.com/ws/Metadata/GetMetadata.ashx?output=json&path=adriansmovies/sushi.avi HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseMetadataFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetFile() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SDNAsyncClient.class.getMethod("getFile", String.class);
      GeneratedHttpRequest request = processor.createRequest(method, "adriansmovies/sushi.avi");

      assertRequestLineEquals(request, "GET http://services.nirvanix.com/adriansmovies/sushi.avi?output=json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnStringIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), InsertUserContextIntoPath.class);

   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), AddSessionTokenToRequest.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<SDNAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<SDNAsyncClient>>() {
      };
   }

   protected Module createModule() {
      return new TestSDNRestClientModule();
   }

   @RequiresHttp
   @ConfiguresRestClient
   static class TestSDNRestClientModule extends SDNRestClientModule {
      @Override
      public void configure() {
         bind(String.class).annotatedWith(SessionToken.class).toInstance("sessiontoken");
         bind(String.class).annotatedWith(Names.named(SDNConstants.PROPERTY_SDN_APPKEY)).toInstance("appKey");
         bind(String.class).annotatedWith(Names.named(SDNConstants.PROPERTY_SDN_APPNAME)).toInstance("appname");
         bind(String.class).annotatedWith(Names.named(SDNConstants.PROPERTY_SDN_USERNAME)).toInstance("username");
      }

   }

   @Override
   public RestContextSpec<SDNClient, SDNAsyncClient> createContextSpec() {
      return new RestContextFactory().createContextSpec("sdn", "user", "password", new Properties());
   }
}
