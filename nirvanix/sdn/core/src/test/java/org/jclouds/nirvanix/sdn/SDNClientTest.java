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
package org.jclouds.nirvanix.sdn;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.blobstore.binders.BindBlobToMultipartFormTest;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.http.functions.ReturnStringIf200;
import org.jclouds.http.functions.ReturnVoidIf2xx;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.nirvanix.sdn.filters.AddSessionTokenToRequest;
import org.jclouds.nirvanix.sdn.filters.InsertUserContextIntoPath;
import org.jclouds.nirvanix.sdn.functions.ParseUploadInfoFromJsonResponse;
import org.jclouds.nirvanix.sdn.reference.SDNConstants;
import org.jclouds.rest.config.RestModule;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.Jsr330;
import org.jclouds.util.Utils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code JaxrsAnnotationProcessor}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "sdn.SDNClient")
public class SDNClientTest {

   private RestAnnotationProcessor<SDNClient> processor;

   public void testGetStorageNode() throws SecurityException, NoSuchMethodException {
      Method method = SDNClient.class.getMethod("getStorageNode", String.class, long.class);
      GeneratedHttpRequest<?> httpMethod = processor.createRequest(method, new Object[] {
               "adriansmovies", 734859264 });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/ws/IMFS/GetStorageNode.ashx");
      assertEquals(httpMethod.getEndpoint().getQuery(),
               "output=json&sizeBytes=734859264&destFolderPath=adriansmovies");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(RestAnnotationProcessor.getParserOrThrowException(method),
               ParseUploadInfoFromJsonResponse.class);
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), AddSessionTokenToRequest.class);
   }

   public void testUpload() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SDNClient.class.getMethod("upload", URI.class, String.class,
               String.class, Blob.class);
      Blob blob = BindBlobToMultipartFormTest.TEST_BLOB;
      GeneratedHttpRequest<SDNClient> httpMethod = processor.createRequest(method,
               new Object[] { URI.create("http://uploader"), "token", "adriansmovies", blob });
      assertEquals(httpMethod.getEndpoint().getHost(), "uploader");
      assertEquals(httpMethod.getEndpoint().getPath(), "/Upload.ashx");
      assertEquals(httpMethod.getEndpoint().getQuery(),
               "output=json&uploadToken=token&destFolderPath=adriansmovies");
      assertEquals(httpMethod.getMethod(), HttpMethod.POST);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(BindBlobToMultipartFormTest.EXPECTS.length() + ""));
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList("multipart/form-data; boundary="
                        + BindBlobToMultipartFormTest.BOUNDRY));
      assertEquals(Utils.toStringAndClose((InputStream) httpMethod.getEntity()),
               BindBlobToMultipartFormTest.EXPECTS);
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               ReturnVoidIf2xx.class);
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), AddSessionTokenToRequest.class);
   }

   public void testSetMetadata() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SDNClient.class.getMethod("setMetadata", String.class, Map.class);
      GeneratedHttpRequest<SDNClient> httpMethod = processor
               .createRequest(method, new Object[] { "adriansmovies/sushi.avi",
                        ImmutableMap.of("Chef", "Kawasaki") });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/ws/Metadata/SetMetadata.ashx");
      assertEquals(httpMethod.getEndpoint().getQuery(),
               "output=json&path=adriansmovies/sushi.avi&metadata=chef:Kawasaki");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(httpMethod.getEntity(), null);
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               ReturnVoidIf2xx.class);
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), AddSessionTokenToRequest.class);
   }

   public void testGetMetadata() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SDNClient.class.getMethod("getMetadata", String.class);
      GeneratedHttpRequest<SDNClient> httpMethod = processor.createRequest(method,
               new Object[] { "adriansmovies/sushi.avi" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/ws/Metadata/GetMetadata.ashx");
      assertEquals(httpMethod.getEndpoint().getQuery(), "output=json&path=adriansmovies/sushi.avi");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(httpMethod.getEntity(), null);
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               ReturnStringIf200.class);
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), AddSessionTokenToRequest.class);
   }

   public void testGetFile() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SDNClient.class.getMethod("getFile", String.class);
      GeneratedHttpRequest<SDNClient> httpMethod = processor.createRequest(method,
               new Object[] { "adriansmovies/sushi.avi" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/adriansmovies/sushi.avi");
      assertEquals(httpMethod.getEndpoint().getQuery(), "output=json");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(httpMethod.getEntity(), null);
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               ReturnStringIf200.class);
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), InsertUserContextIntoPath.class);
   }

   @BeforeClass
   void setupFactory() {
      Injector injector = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bind(URI.class).annotatedWith(SDN.class)
                     .toInstance(URI.create("http://localhost:8080"));
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

         @SuppressWarnings("unused")
         @SessionToken
         @Provides
         String authTokenProvider() {
            return "session-token";
         }
      }, new RestModule(), new ExecutorServiceModule(new WithinThreadExecutorService()),
               new JavaUrlHttpCommandExecutorServiceModule());
      processor = injector.getInstance(Key
               .get(new TypeLiteral<RestAnnotationProcessor<SDNClient>>() {
               }));
   }

}
