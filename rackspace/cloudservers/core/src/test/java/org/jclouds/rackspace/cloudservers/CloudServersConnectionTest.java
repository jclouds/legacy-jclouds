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
package org.jclouds.rackspace.cloudservers;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Method;
import java.net.URI;

import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.HttpMethod;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.rackspace.Authentication;
import org.jclouds.rackspace.cloudservers.functions.ParseFlavorFromGsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseFlavorListFromGsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseImageFromGsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseImageListFromGsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseServerListFromGsonResponse;
import org.jclouds.rest.JaxrsAnnotationProcessor;
import org.jclouds.rest.config.JaxrsModule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;

/**
 * Tests behavior of {@code CloudServersConnection}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudservers.CloudServersConnectionTest")
public class CloudServersConnectionTest {

   JaxrsAnnotationProcessor.Factory factory;

   public void testListServers() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("listServers");
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = factory.create(CloudServersConnection.class).createRequest(endpoint,
               method, new Object[] {});
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/servers");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      factory.create(CloudServersConnection.class);
      assertEquals(JaxrsAnnotationProcessor.getParserOrThrowException(method),
               ParseServerListFromGsonResponse.class);

   }

   public void testListServersDetail() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("listServerDetails");
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = factory.create(CloudServersConnection.class).createRequest(endpoint,
               method, new Object[] {});
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/servers/detail");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      factory.create(CloudServersConnection.class);
      assertEquals(JaxrsAnnotationProcessor.getParserOrThrowException(method),
               ParseServerListFromGsonResponse.class);

   }

   public void testListFlavors() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("listFlavors");
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = factory.create(CloudServersConnection.class).createRequest(endpoint,
               method, new Object[] {});
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/flavors");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      factory.create(CloudServersConnection.class);
      assertEquals(JaxrsAnnotationProcessor.getParserOrThrowException(method),
               ParseFlavorListFromGsonResponse.class);

   }

   public void testListFlavorsDetail() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("listFlavorDetails");
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = factory.create(CloudServersConnection.class).createRequest(endpoint,
               method, new Object[] {});
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/flavors/detail");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      factory.create(CloudServersConnection.class);
      assertEquals(JaxrsAnnotationProcessor.getParserOrThrowException(method),
               ParseFlavorListFromGsonResponse.class);

   }

   public void testListImages() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("listImages");
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = factory.create(CloudServersConnection.class).createRequest(endpoint,
               method, new Object[] {});
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/images");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      factory.create(CloudServersConnection.class);
      assertEquals(JaxrsAnnotationProcessor.getParserOrThrowException(method),
               ParseImageListFromGsonResponse.class);

   }

   public void testListImagesDetail() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("listImageDetails");
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = factory.create(CloudServersConnection.class).createRequest(endpoint,
               method, new Object[] {});
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/images/detail");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      factory.create(CloudServersConnection.class);
      assertEquals(JaxrsAnnotationProcessor.getParserOrThrowException(method),
               ParseImageListFromGsonResponse.class);

   }

   public void testGetImageDetails() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("getImageDetails", int.class);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = factory.create(CloudServersConnection.class).createRequest(endpoint,
               method, new Object[] { 2 });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/images/2");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      factory.create(CloudServersConnection.class);
      assertEquals(JaxrsAnnotationProcessor.getParserOrThrowException(method),
               ParseImageFromGsonResponse.class);

   }

   public void testGetFlavorDetails() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("getFlavorDetails", int.class);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = factory.create(CloudServersConnection.class).createRequest(endpoint,
               method, new Object[] { 2 });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/flavors/2");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      factory.create(CloudServersConnection.class);
      assertEquals(JaxrsAnnotationProcessor.getParserOrThrowException(method),
               ParseFlavorFromGsonResponse.class);

   }

   @BeforeClass
   void setupFactory() {
      factory = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bind(URI.class).toInstance(URI.create("http://localhost:8080"));
         }

         @SuppressWarnings("unused")
         @Provides
         @Authentication
         public String getAuthToken() {
            return "testtoken";
         }
      }, new JaxrsModule(), new ExecutorServiceModule(new WithinThreadExecutorService()),
               new JavaUrlHttpCommandExecutorServiceModule()).getInstance(
               JaxrsAnnotationProcessor.Factory.class);
   }

}
