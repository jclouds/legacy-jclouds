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

import static org.jclouds.azure.storage.blob.options.CreateContainerOptions.Builder.withPublicAcl;
import static org.jclouds.azure.storage.options.ListOptions.Builder.maxResults;
import static org.testng.Assert.assertEquals;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collections;

import javax.ws.rs.HttpMethod;

import org.jclouds.azure.storage.AzureBlob;
import org.jclouds.azure.storage.blob.functions.ReturnTrueIfContainerAlreadyExists;
import org.jclouds.azure.storage.blob.options.CreateContainerOptions;
import org.jclouds.azure.storage.options.CreateOptions;
import org.jclouds.azure.storage.options.ListOptions;
import org.jclouds.azure.storage.reference.AzureStorageConstants;
import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.http.functions.ReturnTrueOn404;
import org.jclouds.rest.JaxrsAnnotationProcessor;
import org.jclouds.rest.config.JaxrsModule;
import org.jclouds.util.Jsr330;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;

/**
 * Tests behavior of {@code AzureBlobStore}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudservers.AzureBlobStoreTest")
public class AzureBlobStoreTest {
   JaxrsAnnotationProcessor.Factory factory;

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

   public void testListContainersOptions() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobStore.class.getMethod("listContainers", ListOptions.class);

      HttpRequest httpMethod = processor.createRequest(method, new Object[] { maxResults(1).marker(
               "marker").prefix("prefix") });
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
               ReturnTrueIf2xx.class);
      // TODO check generic type of response parser
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnTrueOn404.class);
   }

   public void testCreateContainerOptions() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobStore.class.getMethod("createContainer", String.class,
               CreateContainerOptions.class);

      HttpRequest httpMethod = processor.createRequest(method, new Object[] { "container",
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
      assertEquals(processor.createResponseParser(method, httpMethod, null).getClass(),
               ReturnTrueIf2xx.class);
      // TODO check generic type of response parser
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnTrueIfContainerAlreadyExists.class);
   }

   public void testCreateRootContainer() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobStore.class.getMethod("createRootContainer");

      HttpRequest httpMethod = processor.createRequest(method, new Object[] {});
      assertEquals(httpMethod.getEndpoint().getHost(), "myaccount.blob.core.windows.net");
      assertEquals(httpMethod.getEndpoint().getPath(), "/$root");
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

   public void testDeleteRootContainer() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobStore.class.getMethod("deleteRootContainer");

      HttpRequest httpMethod = processor.createRequest(method, new Object[] {});
      assertEquals(httpMethod.getEndpoint().getHost(), "myaccount.blob.core.windows.net");
      assertEquals(httpMethod.getEndpoint().getPath(), "/$root");
      assertEquals(httpMethod.getEndpoint().getQuery(), "restype=container");
      assertEquals(httpMethod.getMethod(), HttpMethod.DELETE);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-07-17"));
      assertEquals(processor.createResponseParser(method, httpMethod, null).getClass(),
               ReturnTrueIf2xx.class);
      // TODO check generic type of response parser
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnTrueOn404.class);
   }

   public void testCreateRootContainerOptions() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobStore.class.getMethod("createRootContainer", CreateOptions.class);

      HttpRequest httpMethod = processor.createRequest(method, new Object[] { withPublicAcl()
               .withMetadata(ImmutableMultimap.of("foo", "bar")) });
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
      assertEquals(processor.createResponseParser(method, httpMethod, null).getClass(),
               ReturnTrueIf2xx.class);
      // TODO check generic type of response parser
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnTrueIfContainerAlreadyExists.class);
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

   public void testListRootBlobs() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobStore.class.getMethod("listBlobs");

      HttpRequest httpMethod = processor.createRequest(method, new Object[] {});
      assertEquals(httpMethod.getEndpoint().getHost(), "myaccount.blob.core.windows.net");
      assertEquals(httpMethod.getEndpoint().getPath(), "/$root");
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

   JaxrsAnnotationProcessor processor;

   @BeforeClass
   void setupFactory() {
      factory = Guice.createInjector(
               new AbstractModule() {
                  @Override
                  protected void configure() {
                     bind(URI.class).annotatedWith(AzureBlob.class).toInstance(
                              URI.create("http://myaccount.blob.core.windows.net"));
                     bindConstant().annotatedWith(
                              Jsr330.named(AzureStorageConstants.PROPERTY_AZURESTORAGE_ACCOUNT))
                              .to("myaccount");
                     bindConstant().annotatedWith(
                              Jsr330.named(AzureStorageConstants.PROPERTY_AZURESTORAGE_KEY)).to(
                              HttpUtils.toBase64String("key".getBytes()));
                  }
               }, new JaxrsModule(), new ExecutorServiceModule(new WithinThreadExecutorService()),
               new JavaUrlHttpCommandExecutorServiceModule()).getInstance(
               JaxrsAnnotationProcessor.Factory.class);
      processor = factory.create(AzureBlobStore.class);
   }
}
