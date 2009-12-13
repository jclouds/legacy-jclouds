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
package org.jclouds.atmosonline.saas;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.URI;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.atmosonline.saas.blobstore.functions.BlobToObject;
import org.jclouds.atmosonline.saas.config.AtmosObjectModule;
import org.jclouds.atmosonline.saas.domain.AtmosObject;
import org.jclouds.atmosonline.saas.filters.SignRequest;
import org.jclouds.atmosonline.saas.functions.ParseDirectoryListFromContentAndHeaders;
import org.jclouds.atmosonline.saas.functions.ParseObjectFromHeadersAndHttpContent;
import org.jclouds.atmosonline.saas.functions.ParseSystemMetadataFromHeaders;
import org.jclouds.atmosonline.saas.functions.ReturnEndpointIfAlreadyExists;
import org.jclouds.atmosonline.saas.options.ListOptions;
import org.jclouds.atmosonline.saas.reference.AtmosStorageConstants;
import org.jclouds.blobstore.binders.BindBlobToMultipartFormTest;
import org.jclouds.blobstore.config.BlobStoreObjectModule;
import org.jclouds.blobstore.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.blobstore.functions.ThrowKeyNotFoundOn404;
import org.jclouds.date.TimeStamp;
import org.jclouds.encryption.internal.Base64;
import org.jclouds.http.functions.ParseURIFromListOrLocationHeaderIf20x;
import org.jclouds.http.functions.ReturnVoidIf2xx;
import org.jclouds.http.options.GetOptions;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.Jsr330;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code AtmosStorageClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "emcsaas.AtmosStorageClientTest")
public class AtmosStorageClientTest extends RestClientTest<AtmosStorageAsyncClient> {

   private BlobToObject blobToObject;

   public void testListDirectories() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AtmosStorageAsyncClient.class.getMethod("listDirectories", Array.newInstance(
               ListOptions.class, 0).getClass());
      GeneratedHttpRequest<AtmosStorageAsyncClient> httpMethod = processor.createRequest(method);

      assertRequestLineEquals(httpMethod,
               "GET http://accesspoint.emccis.com/rest/namespace HTTP/1.1");
      assertHeadersEqual(httpMethod, HttpHeaders.ACCEPT + ": text/xml\n");
      assertEntityEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod,
               ParseDirectoryListFromContentAndHeaders.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testListDirectory() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AtmosStorageAsyncClient.class.getMethod("listDirectory", String.class, Array
               .newInstance(ListOptions.class, 0).getClass());
      GeneratedHttpRequest<AtmosStorageAsyncClient> httpMethod = processor.createRequest(method,
               "directory");

      assertRequestLineEquals(httpMethod,
               "GET http://accesspoint.emccis.com/rest/namespace/directory/ HTTP/1.1");
      assertHeadersEqual(httpMethod, HttpHeaders.ACCEPT + ": text/xml\n");
      assertEntityEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod,
               ParseDirectoryListFromContentAndHeaders.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testListDirectoriesOptions() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = AtmosStorageAsyncClient.class.getMethod("listDirectories", Array.newInstance(
               ListOptions.class, 0).getClass());
      GeneratedHttpRequest<AtmosStorageAsyncClient> httpMethod = processor.createRequest(method,
               new ListOptions().limit(1).token("asda"));

      assertRequestLineEquals(httpMethod,
               "GET http://accesspoint.emccis.com/rest/namespace HTTP/1.1");
      assertHeadersEqual(httpMethod, HttpHeaders.ACCEPT
               + ": text/xml\nx-emc-limit: 1\nx-emc-token: asda\n");
      assertEntityEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod,
               ParseDirectoryListFromContentAndHeaders.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testListDirectoryOptions() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = AtmosStorageAsyncClient.class.getMethod("listDirectory", String.class, Array
               .newInstance(ListOptions.class, 0).getClass());
      GeneratedHttpRequest<AtmosStorageAsyncClient> httpMethod = processor.createRequest(method,
               "directory", new ListOptions().limit(1).token("asda"));

      assertRequestLineEquals(httpMethod,
               "GET http://accesspoint.emccis.com/rest/namespace/directory/ HTTP/1.1");
      assertHeadersEqual(httpMethod, HttpHeaders.ACCEPT
               + ": text/xml\nx-emc-limit: 1\nx-emc-token: asda\n");
      assertEntityEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod,
               ParseDirectoryListFromContentAndHeaders.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testCreateDirectory() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AtmosStorageAsyncClient.class.getMethod("createDirectory", String.class);
      GeneratedHttpRequest<AtmosStorageAsyncClient> httpMethod = processor.createRequest(method,
               "dir");

      assertRequestLineEquals(httpMethod,
               "POST http://accesspoint.emccis.com/rest/namespace/dir/ HTTP/1.1");
      assertHeadersEqual(httpMethod, HttpHeaders.ACCEPT + ": */*\n");
      assertEntityEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod,
               ParseURIFromListOrLocationHeaderIf20x.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEndpointIfAlreadyExists.class);

      checkFilters(httpMethod);
   }

   public void testCreateFile() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AtmosStorageAsyncClient.class.getMethod("createFile", String.class,
               AtmosObject.class);
      GeneratedHttpRequest<AtmosStorageAsyncClient> httpMethod = processor.createRequest(method,
               "dir", blobToObject.apply(BindBlobToMultipartFormTest.TEST_BLOB));

      assertRequestLineEquals(httpMethod,
               "POST http://accesspoint.emccis.com/rest/namespace/dir/hello HTTP/1.1");
      assertHeadersEqual(httpMethod, HttpHeaders.ACCEPT
               + ": */*\nContent-Length: 5\nContent-Type: text/plain\n");
      assertEntityEquals(httpMethod, "hello");

      assertResponseParserClassEquals(method, httpMethod,
               ParseURIFromListOrLocationHeaderIf20x.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testUpdateFile() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AtmosStorageAsyncClient.class.getMethod("updateFile", String.class,
               AtmosObject.class);
      GeneratedHttpRequest<AtmosStorageAsyncClient> httpMethod = processor.createRequest(method,
               "dir", blobToObject.apply(BindBlobToMultipartFormTest.TEST_BLOB));

      assertRequestLineEquals(httpMethod,
               "PUT http://accesspoint.emccis.com/rest/namespace/dir/hello HTTP/1.1");
      assertHeadersEqual(httpMethod, HttpHeaders.ACCEPT
               + ": */*\nContent-Length: 5\nContent-Type: text/plain\n");
      assertEntityEquals(httpMethod, "hello");

      assertResponseParserClassEquals(method, httpMethod, ReturnVoidIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ThrowKeyNotFoundOn404.class);

      checkFilters(httpMethod);
   }

   public void testReadFile() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AtmosStorageAsyncClient.class.getMethod("readFile", String.class, Array
               .newInstance(GetOptions.class, 0).getClass());
      GeneratedHttpRequest<AtmosStorageAsyncClient> httpMethod = processor.createRequest(method,
               "dir/file");

      assertRequestLineEquals(httpMethod,
               "GET http://accesspoint.emccis.com/rest/namespace/dir/file HTTP/1.1");
      assertHeadersEqual(httpMethod, HttpHeaders.ACCEPT + ": */*\n");
      assertEntityEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod,
               ParseObjectFromHeadersAndHttpContent.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ThrowKeyNotFoundOn404.class);

      checkFilters(httpMethod);
   }

   public void testGetSystemMetadata() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AtmosStorageAsyncClient.class.getMethod("getSystemMetadata", String.class);
      GeneratedHttpRequest<AtmosStorageAsyncClient> httpMethod = processor.createRequest(method,
               "dir/file");

      assertRequestLineEquals(httpMethod,
               "HEAD http://accesspoint.emccis.com/rest/namespace/dir/file HTTP/1.1");
      assertHeadersEqual(httpMethod, HttpHeaders.ACCEPT + ": */*\n");
      assertEntityEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSystemMetadataFromHeaders.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ThrowKeyNotFoundOn404.class);

      checkFilters(httpMethod);
   }

   public void testDeletePath() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AtmosStorageAsyncClient.class.getMethod("deletePath", String.class);
      GeneratedHttpRequest<AtmosStorageAsyncClient> httpMethod = processor.createRequest(method,
               "dir/file");

      assertRequestLineEquals(httpMethod,
               "DELETE http://accesspoint.emccis.com/rest/namespace/dir/file HTTP/1.1");
      assertHeadersEqual(httpMethod, HttpHeaders.ACCEPT + ": */*\n");
      assertEntityEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ReturnVoidIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpMethod);
   }

   public void testNewObject() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AtmosStorageAsyncClient.class.getMethod("newObject");
      assertEquals(method.getReturnType(), AtmosObject.class);
   }

   @Override
   protected void checkFilters(GeneratedHttpRequest<AtmosStorageAsyncClient> httpMethod) {
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), SignRequest.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<AtmosStorageAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<AtmosStorageAsyncClient>>() {
      };
   }

   @BeforeClass
   @Override
   protected void setupFactory() {
      super.setupFactory();
      blobToObject = injector.getInstance(BlobToObject.class);
   }

   @Override
   protected Module createModule() {
      return new AbstractModule() {
         @Override
         protected void configure() {
            install(new BlobStoreObjectModule());
            install(new AtmosObjectModule());
            bind(URI.class).annotatedWith(AtmosStorage.class).toInstance(
                     URI.create("http://accesspoint.emccis.com"));
            bind(String.class).annotatedWith(TimeStamp.class).toInstance("timestamp");
            bindConstant().annotatedWith(
                     Jsr330.named(AtmosStorageConstants.PROPERTY_EMCSAAS_ENDPOINT)).to(
                     "http://accesspoint.emccis.com");
            bindConstant().annotatedWith(Jsr330.named(AtmosStorageConstants.PROPERTY_EMCSAAS_UID))
                     .to("uid");
            bindConstant().annotatedWith(Jsr330.named(AtmosStorageConstants.PROPERTY_EMCSAAS_KEY))
                     .to(Base64.encodeBytes("key".getBytes()));
            bind(Logger.LoggerFactory.class).toInstance(new LoggerFactory() {
               public Logger getLogger(String category) {
                  return Logger.NULL;
               }
            });
            bindConstant().annotatedWith(
                     Jsr330.named(AtmosStorageConstants.PROPERTY_EMCSAAS_SESSIONINTERVAL)).to(1l);

         }

      };
   }
}
