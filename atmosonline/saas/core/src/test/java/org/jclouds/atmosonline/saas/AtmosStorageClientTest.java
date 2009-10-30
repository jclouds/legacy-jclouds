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

import java.lang.reflect.Method;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jclouds.atmosonline.saas.config.AtmosStorageRestClientModule;
import org.jclouds.atmosonline.saas.filters.SignRequest;
import org.jclouds.atmosonline.saas.reference.AtmosStorageConstants;
import org.jclouds.atmosonline.saas.xml.ListDirectoryResponseHandler;
import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseURIFromListOrLocationHeaderIf20x;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rest.config.RestModule;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.Jsr330;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code AtmosStorageClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "azureblob.AtmosStorageClientTest")
public class AtmosStorageClientTest {

   public void testListDirectories() throws SecurityException, NoSuchMethodException {
      Method method = AtmosStorageClient.class.getMethod("listDirectories");

      GeneratedHttpRequest<AtmosStorageClient> httpMethod = processor.createRequest(method,
               new Object[] {});
      assertEquals(httpMethod.getRequestLine(),
               "GET http://accesspoint.emccis.com/rest/namespace HTTP/1.1");
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getFirstHeaderOrNull(HttpHeaders.ACCEPT), "text/xml");
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               ParseSax.class);
      assertEquals(RestAnnotationProcessor.getSaxResponseParserClassOrNull(method), ListDirectoryResponseHandler.class);
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), SignRequest.class);
   }

   public void testCreateDirectory() throws SecurityException, NoSuchMethodException {
      Method method = AtmosStorageClient.class.getMethod("createDirectory", String.class);

      GeneratedHttpRequest<AtmosStorageClient> httpMethod = processor.createRequest(method,
               "dir");
      assertEquals(httpMethod.getRequestLine(),
               "POST http://accesspoint.emccis.com/rest/namespace/dir/ HTTP/1.1");
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getFirstHeaderOrNull(HttpHeaders.ACCEPT), MediaType.WILDCARD);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               ParseURIFromListOrLocationHeaderIf20x.class);
      assertEquals(RestAnnotationProcessor.getSaxResponseParserClassOrNull(method), null);
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), SignRequest.class);
   }

   @BeforeClass
   void setupFactory() {
      Injector injector = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bindConstant().annotatedWith(
                     Jsr330.named(AtmosStorageConstants.PROPERTY_EMCSAAS_ENDPOINT)).to(
                     "http://accesspoint.emccis.com");
            bindConstant().annotatedWith(Jsr330.named(AtmosStorageConstants.PROPERTY_EMCSAAS_UID))
                     .to("uid");
            bindConstant().annotatedWith(Jsr330.named(AtmosStorageConstants.PROPERTY_EMCSAAS_KEY))
                     .to(HttpUtils.toBase64String("key".getBytes()));
            bind(Logger.LoggerFactory.class).toInstance(new LoggerFactory() {
               public Logger getLogger(String category) {
                  return Logger.NULL;
               }
            });
            bindConstant().annotatedWith(
                     Jsr330.named(AtmosStorageConstants.PROPERTY_EMCSAAS_SESSIONINTERVAL)).to(1l);
         }
      }, new AtmosStorageRestClientModule(), new RestModule(), new ExecutorServiceModule(
               new WithinThreadExecutorService()), new JavaUrlHttpCommandExecutorServiceModule());
      processor = injector.getInstance(Key
               .get(new TypeLiteral<RestAnnotationProcessor<AtmosStorageClient>>() {
               }));
   }

   RestAnnotationProcessor<AtmosStorageClient> processor;
}
