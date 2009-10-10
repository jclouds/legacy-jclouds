/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.azure.storage.blob;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collections;

import javax.inject.Singleton;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.azure.storage.AzureBlob;
import org.jclouds.azure.storage.blob.domain.Blob;
import org.jclouds.azure.storage.blob.functions.ReturnTrueIfContainerAlreadyExists;
import org.jclouds.azure.storage.reference.AzureStorageConstants;
import org.jclouds.blobstore.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.http.functions.ParseETagHeader;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.http.functions.ReturnVoidIf2xx;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rest.config.RestModule;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.Jsr330;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code AzureBlobStore}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "azureblob.AzureBlobStoreTest")
public class AzureBlobStoreTest {

   public void testListContainers() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobStore.class.getMethod("listContainers");

      HttpRequest httpMethod = processor.createRequest(method, new Object[] {});
      assertEquals(httpMethod.getEndpoint().getHost(), "myaccount.blob.core.windows.net");
      assertEquals(httpMethod.getEndpoint().getPath(), "/");
      assertEquals(httpMethod.getEndpoint().getQuery(), "comp=list");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-07-17"));
      assertEquals(processor.createResponseParser(method, httpMethod, null).getClass(),
               ParseSax.class);
      // TODO check generic type of response parser
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   public void testCreateContainer() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobStore.class.getMethod("createContainer", String.class);

      HttpRequest httpMethod = processor.createRequest(method, new Object[] { "container" });
      assertEquals(httpMethod.getEndpoint().getHost(), "myaccount.blob.core.windows.net");
      assertEquals(httpMethod.getEndpoint().getPath(), "/container");
      assertEquals(httpMethod.getEndpoint().getQuery(), "restype=container");
      assertEquals(httpMethod.getMethod(), HttpMethod.PUT);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-07-17"));
      assertEquals(httpMethod.getHeaders().get("Content-Length"), Collections.singletonList("0"));
      assertEquals(processor.createResponseParser(method, httpMethod, null).getClass(),
               ReturnTrueIf2xx.class);
      // TODO check generic type of response parser
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnTrueIfContainerAlreadyExists.class);
   }

   public void testDeleteContainer() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobStore.class.getMethod("deleteContainer", String.class);

      HttpRequest httpMethod = processor.createRequest(method, new Object[] { "container" });
      assertEquals(httpMethod.getEndpoint().getHost(), "myaccount.blob.core.windows.net");
      assertEquals(httpMethod.getEndpoint().getPath(), "/container");
      assertEquals(httpMethod.getEndpoint().getQuery(), "restype=container");
      assertEquals(httpMethod.getMethod(), HttpMethod.DELETE);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-07-17"));
      assertEquals(processor.createResponseParser(method, httpMethod, null).getClass(),
               ReturnVoidIf2xx.class);
      // TODO check generic type of response parser
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnVoidOnNotFoundOr404.class);
   }

   public void testListBlobs() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobStore.class.getMethod("listBlobs", String.class);

      HttpRequest httpMethod = processor.createRequest(method, new Object[] { "container" });
      assertEquals(httpMethod.getEndpoint().getHost(), "myaccount.blob.core.windows.net");
      assertEquals(httpMethod.getEndpoint().getPath(), "/container");
      assertEquals(httpMethod.getEndpoint().getQuery(), "restype=container&comp=list");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-07-17"));
      assertEquals(processor.createResponseParser(method, httpMethod, null).getClass(),
               ParseSax.class);
      // TODO check generic type of response parser
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   public void testPutBlob() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AzureBlobStore.class.getMethod("putBlob", String.class, Blob.class);

      Blob blob = new Blob("test");
      blob.setData("test");
      HttpRequest httpMethod = processor
               .createRequest(method, new Object[] { "mycontainer", blob });
      assertEquals(httpMethod.getEndpoint().getHost(), "myaccount.blob.core.windows.net");
      assertEquals(httpMethod.getEndpoint().getPath(), "/mycontainer/test");
      assertEquals(httpMethod.getEndpoint().getQuery(), null);
      assertEquals(httpMethod.getMethod(), HttpMethod.PUT);
      assertEquals(httpMethod.getHeaders().size(), 4);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-07-17"));
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList("test".length() + ""));
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList("application/octet-stream"));
      assertEquals(httpMethod.getHeaders().get("Content-MD5"), Collections.singletonList(HttpUtils
               .toBase64String(HttpUtils.md5("test"))));
      assertEquals(httpMethod.getEntity(), "test");
      assertEquals(processor.createResponseParser(method, httpMethod, null).getClass(),
               ParseETagHeader.class);
   }

   @BeforeClass
   void setupFactory() {
      Injector injector = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bind(URI.class).annotatedWith(AzureBlob.class).toInstance(
                     URI.create("http://myaccount.blob.core.windows.net"));
            bindConstant().annotatedWith(
                     Jsr330.named(AzureStorageConstants.PROPERTY_AZURESTORAGE_ACCOUNT)).to(
                     "myaccount");
            bindConstant().annotatedWith(
                     Jsr330.named(AzureStorageConstants.PROPERTY_AZURESTORAGE_KEY)).to(
                     HttpUtils.toBase64String("key".getBytes()));
            bindConstant().annotatedWith(
                     Jsr330.named(BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX)).to("prefix");
            bind(Logger.LoggerFactory.class).toInstance(new LoggerFactory() {
               public Logger getLogger(String category) {
                  return Logger.NULL;
               }
            });
         }

         @SuppressWarnings("unused")
         @Provides
         @Singleton
         AzureBlobUtil getAzureBlobUtil() {
            return new AzureBlobUtil() {

               public byte[] getMD5(URI container, String key) {
                  return HttpUtils.fromHexString("01");
               }
            };
         }

      }, new RestModule(), new ExecutorServiceModule(new WithinThreadExecutorService()),
               new JavaUrlHttpCommandExecutorServiceModule());
      processor = injector.getInstance(Key
               .get(new TypeLiteral<RestAnnotationProcessor<AzureBlobStore>>() {
               }));
   }

   RestAnnotationProcessor<AzureBlobStore> processor;
}
