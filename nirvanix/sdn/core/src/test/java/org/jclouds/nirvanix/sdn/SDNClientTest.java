/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.nirvanix.sdn;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;

import org.jclouds.blobstore.binders.BindBlobToMultipartFormTest;
import org.jclouds.blobstore.config.BlobStoreObjectModule;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.http.functions.ReturnStringIf200;
import org.jclouds.http.functions.ReturnVoidIf2xx;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.nirvanix.sdn.filters.AddSessionTokenToRequest;
import org.jclouds.nirvanix.sdn.filters.InsertUserContextIntoPath;
import org.jclouds.nirvanix.sdn.functions.ParseMetadataFromJsonResponse;
import org.jclouds.nirvanix.sdn.functions.ParseUploadInfoFromJsonResponse;
import org.jclouds.nirvanix.sdn.reference.SDNConstants;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.Jsr330;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code JaxrsAnnotationProcessor}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "sdn.SDNClient")
public class SDNClientTest extends RestClientTest<SDNAsyncClient> {

   public void testGetStorageNode() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SDNAsyncClient.class.getMethod("getStorageNode", String.class, long.class);
      GeneratedHttpRequest<SDNAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] { "adriansmovies", 734859264 });

      assertRequestLineEquals(
               httpMethod,
               "GET http://stub:8080/ws/IMFS/GetStorageNode.ashx?output=json&destFolderPath=adriansmovies&sizeBytes=734859264 HTTP/1.1");
      assertHeadersEqual(httpMethod, "");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseUploadInfoFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);

   }

   public void testUpload() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SDNAsyncClient.class.getMethod("upload", URI.class, String.class,
               String.class, Blob.class);
      Blob blob = BindBlobToMultipartFormTest.TEST_BLOB;
      GeneratedHttpRequest<SDNAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] { URI.create("http://uploader"), "token", "adriansmovies", blob });

      assertRequestLineEquals(
               httpMethod,
               "POST http://uploader/Upload.ashx?output=json&destFolderPath=adriansmovies&uploadToken=token HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 131\nContent-Type: multipart/form-data; boundary=--JCLOUDS--\n");
      StringBuffer expects = new StringBuffer();
      expects.append("----JCLOUDS--\r\n");
      expects.append("Content-Disposition: form-data; name=\"hello\"; filename=\"hello\"\r\n");
      expects.append("Content-Type: text/plain\r\n\r\n");
      expects.append("hello\r\n");
      expects.append("----JCLOUDS----\r\n");

      assertPayloadEquals(httpMethod, expects.toString());

      assertResponseParserClassEquals(method, httpMethod, ReturnVoidIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testSetMetadata() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SDNAsyncClient.class.getMethod("setMetadata", String.class, Map.class);
      GeneratedHttpRequest<SDNAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] { "adriansmovies/sushi.avi", ImmutableMap.of("Chef", "Kawasaki") });

      assertRequestLineEquals(
               httpMethod,
               "GET http://stub:8080/ws/Metadata/SetMetadata.ashx?output=json&path=adriansmovies/sushi.avi&metadata=chef:Kawasaki HTTP/1.1");
      assertHeadersEqual(httpMethod, "");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ReturnVoidIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetMetadata() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SDNAsyncClient.class.getMethod("getMetadata", String.class);
      GeneratedHttpRequest<SDNAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] { "adriansmovies/sushi.avi" });

      assertRequestLineEquals(
               httpMethod,
               "GET http://stub:8080/ws/Metadata/GetMetadata.ashx?output=json&path=adriansmovies/sushi.avi HTTP/1.1");
      assertHeadersEqual(httpMethod, "");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseMetadataFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetFile() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SDNAsyncClient.class.getMethod("getFile", String.class);
      GeneratedHttpRequest<SDNAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] { "adriansmovies/sushi.avi" });

      assertRequestLineEquals(httpMethod,
               "GET http://stub:8080/adriansmovies/sushi.avi?output=json HTTP/1.1");
      assertHeadersEqual(httpMethod, "");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ReturnStringIf200.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), InsertUserContextIntoPath.class);

   }

   @Override
   protected void checkFilters(GeneratedHttpRequest<SDNAsyncClient> httpMethod) {
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), AddSessionTokenToRequest.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<SDNAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<SDNAsyncClient>>() {
      };
   }

   @BeforeClass
   @Override
   protected void setupFactory() {
      super.setupFactory();
   }

   @Override
   protected Module createModule() {
      return new AbstractModule() {
         @Override
         protected void configure() {
            install(new BlobStoreObjectModule());
            bind(URI.class).annotatedWith(SDN.class).toInstance(URI.create("http://stub:8080"));
            bind(String.class).annotatedWith(SessionToken.class).toInstance("sessiontoken");
            bind(Logger.LoggerFactory.class).toInstance(new LoggerFactory() {
               public Logger getLogger(String category) {
                  return Logger.NULL;
               }
            });
            bindConstant().annotatedWith(Jsr330.named(SDNConstants.PROPERTY_SDN_APPKEY)).to(
                     "appKey");
            bindConstant().annotatedWith(Jsr330.named(SDNConstants.PROPERTY_SDN_APPNAME)).to(
                     "appname");
            bindConstant().annotatedWith(Jsr330.named(SDNConstants.PROPERTY_SDN_USERNAME)).to(
                     "username");
         }

      };
   }

}
