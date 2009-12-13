/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.mezeo.pcs2;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.blobstore.binders.BindBlobToMultipartFormTest;
import org.jclouds.blobstore.config.BlobStoreObjectModule;
import org.jclouds.blobstore.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.blobstore.functions.ThrowKeyNotFoundOn404;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseURIFromListOrLocationHeaderIf20x;
import org.jclouds.http.functions.ReturnInputStream;
import org.jclouds.http.functions.ReturnVoidIf2xx;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.mezeo.pcs2.blobstore.functions.BlobToPCSFile;
import org.jclouds.mezeo.pcs2.config.PCSObjectModule;
import org.jclouds.mezeo.pcs2.domain.PCSFile;
import org.jclouds.mezeo.pcs2.endpoints.RootContainer;
import org.jclouds.mezeo.pcs2.functions.AddMetadataItemIntoMap;
import org.jclouds.mezeo.pcs2.options.PutBlockOptions;
import org.jclouds.mezeo.pcs2.xml.ContainerHandler;
import org.jclouds.mezeo.pcs2.xml.FileHandler;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.EncryptionService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code PCSClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "pcs2.PCSClientTest")
public class PCSClientTest extends RestClientTest<PCSAsyncClient> {

   public void testList() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PCSAsyncClient.class.getMethod("list");
      GeneratedHttpRequest<PCSAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] {});

      assertRequestLineEquals(httpMethod, "GET http://localhost:8080/root HTTP/1.1");
      assertHeadersEqual(httpMethod, "X-Cloud-Depth: 2\n");
      assertEntityEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ContainerHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testCreateContainer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PCSAsyncClient.class.getMethod("createContainer", String.class);
      GeneratedHttpRequest<PCSAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] { "container" });

      assertRequestLineEquals(httpMethod, "POST http://localhost:8080/root/contents HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 45\nContent-Type: application/vnd.csp.container-info+xml\n");
      assertEntityEquals(httpMethod, "<container><name>container</name></container>");

      assertResponseParserClassEquals(method, httpMethod,
               ParseURIFromListOrLocationHeaderIf20x.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);

   }

   public void testDeleteContainer() throws SecurityException, NoSuchMethodException {
      Method method = PCSAsyncClient.class.getMethod("deleteContainer", URI.class);
      GeneratedHttpRequest<PCSAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] { URI.create("http://localhost/container/1234") });
      assertEquals(httpMethod.getRequestLine(), "DELETE http://localhost/container/1234 HTTP/1.1");
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               ReturnVoidIf2xx.class);
      assertEquals(RestAnnotationProcessor.getSaxResponseParserClassOrNull(method), null);
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), BasicAuthentication.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnVoidOnNotFoundOr404.class);
   }

   public void testListURI() throws SecurityException, NoSuchMethodException {
      Method method = PCSAsyncClient.class.getMethod("list", URI.class);
      GeneratedHttpRequest<PCSAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] { URI.create("http://localhost/mycontainer") });
      assertEquals(httpMethod.getRequestLine(), "GET http://localhost/mycontainer HTTP/1.1");
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get("X-Cloud-Depth"), Collections.singletonList("2"));
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(), ParseSax.class);
      assertEquals(RestAnnotationProcessor.getSaxResponseParserClassOrNull(method),
               ContainerHandler.class);
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), BasicAuthentication.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   public void testGetFileInfo() throws SecurityException, NoSuchMethodException {
      Method method = PCSAsyncClient.class.getMethod("getFileInfo", URI.class);
      GeneratedHttpRequest<PCSAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] { URI.create("http://localhost/myfile") });
      assertEquals(httpMethod.getRequestLine(), "GET http://localhost/myfile HTTP/1.1");
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get("X-Cloud-Depth"), Collections.singletonList("2"));
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(), ParseSax.class);
      assertEquals(RestAnnotationProcessor.getSaxResponseParserClassOrNull(method),
               FileHandler.class);
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), BasicAuthentication.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ThrowKeyNotFoundOn404.class);
   }

   public void testUploadFile() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PCSAsyncClient.class.getMethod("uploadFile", URI.class, PCSFile.class);
      GeneratedHttpRequest<PCSAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] { URI.create("http://localhost/mycontainer"),
                        blobToPCSFile.apply(BindBlobToMultipartFormTest.TEST_BLOB) });

      assertRequestLineEquals(httpMethod, "POST http://localhost/mycontainer/contents HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 131\nContent-Type: multipart/form-data; boundary=--JCLOUDS--\n");
      assertEntityEquals(httpMethod, BindBlobToMultipartFormTest.EXPECTS);

      assertResponseParserClassEquals(method, httpMethod,
               ParseURIFromListOrLocationHeaderIf20x.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);

   }

   public void testUploadBlock() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PCSAsyncClient.class.getMethod("uploadBlock", URI.class, PCSFile.class, Array
               .newInstance(PutBlockOptions.class, 0).getClass());
      GeneratedHttpRequest<PCSAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] { URI.create("http://localhost/mycontainer"),
                        blobToPCSFile.apply(BindBlobToMultipartFormTest.TEST_BLOB) });

      assertRequestLineEquals(httpMethod, "PUT http://localhost/mycontainer/content HTTP/1.1");
      assertHeadersEqual(httpMethod, "Content-Length: 5\n");
      assertEntityEquals(httpMethod, "hello");

      assertResponseParserClassEquals(method, httpMethod, ReturnVoidIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDownloadFile() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PCSAsyncClient.class.getMethod("downloadFile", URI.class);
      GeneratedHttpRequest<PCSAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] { URI.create("http://localhost/container") });

      assertRequestLineEquals(httpMethod, "GET http://localhost/container/content HTTP/1.1");
      assertHeadersEqual(httpMethod, "");
      assertEntityEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ReturnInputStream.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ThrowKeyNotFoundOn404.class);

      checkFilters(httpMethod);

   }

   public void testDeleteFile() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PCSAsyncClient.class.getMethod("deleteFile", URI.class);
      GeneratedHttpRequest<PCSAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] { URI.create("http://localhost/contents/file") });
      assertEquals(httpMethod.getRequestLine(), "DELETE http://localhost/contents/file HTTP/1.1");
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               ReturnVoidIf2xx.class);
      assertEquals(RestAnnotationProcessor.getSaxResponseParserClassOrNull(method), null);
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), BasicAuthentication.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnVoidOnNotFoundOr404.class);
   }

   public void testPutMetadata() throws SecurityException, NoSuchMethodException {
      Method method = PCSAsyncClient.class.getMethod("putMetadataItem", URI.class, String.class,
               String.class);
      GeneratedHttpRequest<PCSAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] { URI.create("http://localhost/contents/file"), "pow", "bar" });
      assertEquals(httpMethod.getRequestLine(),
               "PUT http://localhost/contents/file/metadata/pow HTTP/1.1");
      assertEquals(httpMethod.getMethod(), HttpMethod.PUT);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(httpMethod.getEntity().toString().getBytes().length + ""));
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList("application/unknown"));
      assertEquals("bar", httpMethod.getEntity());
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               ReturnVoidIf2xx.class);
   }

   public void testAddEntryToMap() throws SecurityException, NoSuchMethodException {
      Method method = PCSAsyncClient.class.getMethod("addMetadataItemToMap", URI.class,
               String.class, Map.class);

      GeneratedHttpRequest<PCSAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] { URI.create("http://localhost/pow"), "newkey",
                        ImmutableMap.of("key", "value") });
      assertEquals(httpMethod.getRequestLine(), "GET http://localhost/pow/metadata/newkey HTTP/1.1");

      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               AddMetadataItemIntoMap.class);
   }

   private BlobToPCSFile blobToPCSFile;

   @Override
   protected void checkFilters(GeneratedHttpRequest<PCSAsyncClient> httpMethod) {
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), BasicAuthentication.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<PCSAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<PCSAsyncClient>>() {
      };
   }

   @BeforeClass
   @Override
   protected void setupFactory() {
      super.setupFactory();
      blobToPCSFile = injector.getInstance(BlobToPCSFile.class);
   }

   @Override
   protected Module createModule() {
      return new AbstractModule() {
         @Override
         protected void configure() {
            install(new PCSObjectModule());
            install(new BlobStoreObjectModule());
            bind(URI.class).annotatedWith(PCS.class)
                     .toInstance(URI.create("http://localhost:8080"));
            bind(URI.class).annotatedWith(RootContainer.class).toInstance(
                     URI.create("http://localhost:8080/root"));
            bind(Logger.LoggerFactory.class).toInstance(new LoggerFactory() {
               public Logger getLogger(String category) {
                  return Logger.NULL;
               }
            });
         }

         @SuppressWarnings("unused")
         @Provides
         @Singleton
         public BasicAuthentication provideBasicAuthentication(EncryptionService encryptionService)
                  throws UnsupportedEncodingException {
            return new BasicAuthentication("foo", "bar", encryptionService);
         }

      };
   }
}
