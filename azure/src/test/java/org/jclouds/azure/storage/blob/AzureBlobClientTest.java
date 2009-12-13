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
package org.jclouds.azure.storage.blob;

import static org.jclouds.azure.storage.blob.options.CreateContainerOptions.Builder.withPublicAcl;
import static org.jclouds.azure.storage.options.ListOptions.Builder.maxResults;
import static org.testng.Assert.assertEquals;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.azure.storage.AzureBlob;
import org.jclouds.azure.storage.blob.functions.ParseContainerPropertiesFromHeaders;
import org.jclouds.azure.storage.blob.functions.ReturnTrueIfContainerAlreadyExists;
import org.jclouds.azure.storage.blob.options.CreateContainerOptions;
import org.jclouds.azure.storage.blob.options.ListBlobsOptions;
import org.jclouds.azure.storage.blob.reference.AzureBlobConstants;
import org.jclouds.azure.storage.config.AzureStorageRestClientModule;
import org.jclouds.azure.storage.options.ListOptions;
import org.jclouds.azure.storage.reference.AzureStorageConstants;
import org.jclouds.blobstore.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.encryption.internal.Base64;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.http.functions.ReturnTrueOn404;
import org.jclouds.http.functions.ReturnVoidIf2xx;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rest.config.RestModule;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.Jsr330;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code AzureBlobClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "azureblob.AzureBlobClientTest")
public class AzureBlobClientTest {

   public void testListContainers() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("listContainers", Array.newInstance(
               ListOptions.class, 0).getClass());

      GeneratedHttpRequest<AzureBlobAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] {});
      assertEquals(httpMethod.getEndpoint().getHost(), "myaccount.blob.core.windows.net");
      assertEquals(httpMethod.getEndpoint().getPath(), "/");
      assertEquals(httpMethod.getEndpoint().getQuery(), "comp=list");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-07-17"));
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(), ParseSax.class);
      // TODO check generic type of response parser
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   public void testListContainersOptions() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("listContainers", Array.newInstance(
               ListOptions.class, 0).getClass());

      GeneratedHttpRequest<AzureBlobAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] { maxResults(1).marker("marker").prefix("prefix") });
      assertEquals(httpMethod.getEndpoint().getHost(), "myaccount.blob.core.windows.net");
      assertEquals(httpMethod.getEndpoint().getPath(), "/");
      assert httpMethod.getEndpoint().getQuery().contains("comp=list");
      assert httpMethod.getEndpoint().getQuery().contains("marker=marker");
      assert httpMethod.getEndpoint().getQuery().contains("maxresults=1");
      assert httpMethod.getEndpoint().getQuery().contains("prefix=prefix");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-07-17"));
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(), ParseSax.class);
      // TODO check generic type of response parser
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   public void testCreateContainer() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("createContainer", String.class, Array
               .newInstance(CreateContainerOptions.class, 0).getClass());

      GeneratedHttpRequest<AzureBlobAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] { "container" });
      assertEquals(httpMethod.getEndpoint().getHost(), "myaccount.blob.core.windows.net");
      assertEquals(httpMethod.getEndpoint().getPath(), "/container");
      assertEquals(httpMethod.getEndpoint().getQuery(), "restype=container");
      assertEquals(httpMethod.getMethod(), HttpMethod.PUT);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-07-17"));
      assertEquals(httpMethod.getHeaders().get("Content-Length"), Collections.singletonList("0"));
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               ReturnTrueIf2xx.class);
      // TODO check generic type of response parser
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnTrueIfContainerAlreadyExists.class);
   }

   public void testDeleteContainer() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("deleteContainer", String.class);

      GeneratedHttpRequest<AzureBlobAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] { "container" });
      assertEquals(httpMethod.getEndpoint().getHost(), "myaccount.blob.core.windows.net");
      assertEquals(httpMethod.getEndpoint().getPath(), "/container");
      assertEquals(httpMethod.getEndpoint().getQuery(), "restype=container");
      assertEquals(httpMethod.getMethod(), HttpMethod.DELETE);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-07-17"));
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               ReturnVoidIf2xx.class);
      // TODO check generic type of response parser
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnVoidOnNotFoundOr404.class);
   }

   public void testCreateContainerOptions() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("createContainer", String.class, Array
               .newInstance(CreateContainerOptions.class, 0).getClass());

      GeneratedHttpRequest<AzureBlobAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] { "container",
                        withPublicAcl().withMetadata(ImmutableMultimap.of("foo", "bar")) });
      assertEquals(httpMethod.getEndpoint().getHost(), "myaccount.blob.core.windows.net");
      assertEquals(httpMethod.getEndpoint().getPath(), "/container");
      assertEquals(httpMethod.getEndpoint().getQuery(), "restype=container");
      assertEquals(httpMethod.getMethod(), HttpMethod.PUT);
      assertEquals(httpMethod.getHeaders().size(), 4);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-07-17"));
      assertEquals(httpMethod.getHeaders().get("x-ms-meta-foo"), Collections.singletonList("bar"));
      assertEquals(httpMethod.getHeaders().get("x-ms-prop-publicaccess"), Collections
               .singletonList("true"));
      assertEquals(httpMethod.getHeaders().get("Content-Length"), Collections.singletonList("0"));
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               ReturnTrueIf2xx.class);
      // TODO check generic type of response parser
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnTrueIfContainerAlreadyExists.class);
   }

   public void testCreateRootContainer() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("createRootContainer", Array
               .newInstance(CreateContainerOptions.class, 0).getClass());

      GeneratedHttpRequest<AzureBlobAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] {});
      assertEquals(httpMethod.getEndpoint().getHost(), "myaccount.blob.core.windows.net");
      assertEquals(httpMethod.getEndpoint().getPath(), "/$root");
      assertEquals(httpMethod.getEndpoint().getQuery(), "restype=container");
      assertEquals(httpMethod.getMethod(), HttpMethod.PUT);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-07-17"));
      assertEquals(httpMethod.getHeaders().get("Content-Length"), Collections.singletonList("0"));
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               ReturnTrueIf2xx.class);
      // TODO check generic type of response parser
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnTrueIfContainerAlreadyExists.class);
   }

   public void testDeleteRootContainer() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("deleteRootContainer");

      GeneratedHttpRequest<AzureBlobAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] {});
      assertEquals(httpMethod.getEndpoint().getHost(), "myaccount.blob.core.windows.net");
      assertEquals(httpMethod.getEndpoint().getPath(), "/$root");
      assertEquals(httpMethod.getEndpoint().getQuery(), "restype=container");
      assertEquals(httpMethod.getMethod(), HttpMethod.DELETE);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-07-17"));
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               ReturnTrueIf2xx.class);
      // TODO check generic type of response parser
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnTrueOn404.class);
   }

   public void testCreateRootContainerOptions() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("createRootContainer", Array
               .newInstance(CreateContainerOptions.class, 0).getClass());

      GeneratedHttpRequest<AzureBlobAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] { withPublicAcl().withMetadata(ImmutableMultimap.of("foo", "bar")) });
      assertEquals(httpMethod.getEndpoint().getHost(), "myaccount.blob.core.windows.net");
      assertEquals(httpMethod.getEndpoint().getPath(), "/$root");
      assertEquals(httpMethod.getEndpoint().getQuery(), "restype=container");
      assertEquals(httpMethod.getMethod(), HttpMethod.PUT);
      assertEquals(httpMethod.getHeaders().size(), 4);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-07-17"));
      assertEquals(httpMethod.getHeaders().get("x-ms-meta-foo"), Collections.singletonList("bar"));
      assertEquals(httpMethod.getHeaders().get("x-ms-prop-publicaccess"), Collections
               .singletonList("true"));
      assertEquals(httpMethod.getHeaders().get("Content-Length"), Collections.singletonList("0"));
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               ReturnTrueIf2xx.class);
      // TODO check generic type of response parser
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnTrueIfContainerAlreadyExists.class);
   }

   public void testListBlobs() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("listBlobs", String.class, Array
               .newInstance(ListBlobsOptions.class, 0).getClass());

      GeneratedHttpRequest<AzureBlobAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] { "container" });
      assertEquals(httpMethod.getEndpoint().getHost(), "myaccount.blob.core.windows.net");
      assertEquals(httpMethod.getEndpoint().getPath(), "/container");
      assertEquals(httpMethod.getEndpoint().getQuery(), "restype=container&comp=list");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-07-17"));
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(), ParseSax.class);
      // TODO check generic type of response parser
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   public void testListRootBlobs() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("listBlobs", Array.newInstance(
               ListBlobsOptions.class, 0).getClass());

      GeneratedHttpRequest<AzureBlobAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] {});
      assertEquals(httpMethod.getEndpoint().getHost(), "myaccount.blob.core.windows.net");
      assertEquals(httpMethod.getEndpoint().getPath(), "/$root");
      assertEquals(httpMethod.getEndpoint().getQuery(), "restype=container&comp=list");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-07-17"));
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(), ParseSax.class);
      // TODO check generic type of response parser
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   public void testContainerProperties() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("getContainerProperties", String.class);

      GeneratedHttpRequest<AzureBlobAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] { "container" });
      assertEquals(httpMethod.getEndpoint().getHost(), "myaccount.blob.core.windows.net");
      assertEquals(httpMethod.getEndpoint().getPath(), "/container");
      assertEquals(httpMethod.getEndpoint().getQuery(), "restype=container");
      assertEquals(httpMethod.getMethod(), HttpMethod.HEAD);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-07-17"));
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               ParseContainerPropertiesFromHeaders.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   public void testSetResourceMetadata() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("setResourceMetadata", String.class,
               Map.class);

      GeneratedHttpRequest<AzureBlobAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] { "container", ImmutableMap.of("key", "value") });
      assertEquals(httpMethod.getEndpoint().getHost(), "myaccount.blob.core.windows.net");
      assertEquals(httpMethod.getEndpoint().getPath(), "/container");
      assertEquals(httpMethod.getEndpoint().getQuery(), "restype=container&comp=metadata");
      assertEquals(httpMethod.getMethod(), HttpMethod.PUT);
      assertEquals(httpMethod.getHeaders().size(), 3);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList("0"));
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-07-17"));
      assertEquals(httpMethod.getHeaders().get("x-ms-meta-key"), Collections.singletonList("value"));

      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               ReturnVoidIf2xx.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   public void testSetBlobMetadata() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("setBlobMetadata", String.class,
               String.class, Map.class);
      GeneratedHttpRequest<AzureBlobAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] { "container", "blob", ImmutableMap.of("key", "value") });
      assertEquals(httpMethod.getEndpoint().getHost(), "myaccount.blob.core.windows.net");
      assertEquals(httpMethod.getEndpoint().getPath(), "/container/blob");
      assertEquals(httpMethod.getEndpoint().getQuery(), "comp=metadata");
      assertEquals(httpMethod.getMethod(), HttpMethod.PUT);
      assertEquals(httpMethod.getHeaders().size(), 3);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-07-17"));
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList("0"));
      assertEquals(httpMethod.getHeaders().get("x-ms-meta-key"), Collections.singletonList("value"));

      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               ReturnVoidIf2xx.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
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
                     Base64.encodeBytes("key".getBytes()));
            bindConstant().annotatedWith(
                     Jsr330.named(BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX)).to(
                     "x-ms-meta-");
            bindConstant().annotatedWith(
                     Jsr330.named(AzureBlobConstants.PROPERTY_AZUREBLOB_METADATA_PREFIX)).to(
                     "x-ms-meta-");
            bind(Logger.LoggerFactory.class).toInstance(new LoggerFactory() {
               public Logger getLogger(String category) {
                  return Logger.NULL;
               }
            });
            bindConstant().annotatedWith(
                     Jsr330.named(AzureStorageConstants.PROPERTY_AZURESTORAGE_SESSIONINTERVAL)).to(
                     1l);
         }
      }, new AzureStorageRestClientModule(), new RestModule(), new ExecutorServiceModule(
               new WithinThreadExecutorService()), new JavaUrlHttpCommandExecutorServiceModule());
      processor = injector.getInstance(Key
               .get(new TypeLiteral<RestAnnotationProcessor<AzureBlobAsyncClient>>() {
               }));
   }

   RestAnnotationProcessor<AzureBlobAsyncClient> processor;
}
