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
package org.jclouds.rackspace;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collections;

import javax.ws.rs.HttpMethod;

import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.rackspace.functions.ParseAuthenticationResponseFromHeaders;
import org.jclouds.rackspace.reference.RackspaceHeaders;
import org.jclouds.rest.JaxrsAnnotationProcessor;
import org.jclouds.rest.config.JaxrsModule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;

/**
 * Tests behavior of {@code JaxrsAnnotationProcessor}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "rackspace.RackspaceAuthentication")
public class RackspaceAuthenticationTest {

   JaxrsAnnotationProcessor.Factory factory;

   public void testAuthenticate() throws SecurityException, NoSuchMethodException {
      Method method = RackspaceAuthentication.class.getMethod("authenticate", String.class,
               String.class);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = factory.create(RackspaceAuthentication.class).createRequest(
               endpoint, method, new Object[] { "foo", "bar" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/auth");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get(RackspaceHeaders.AUTH_USER), Collections
               .singletonList("foo"));
      assertEquals(httpMethod.getHeaders().get(RackspaceHeaders.AUTH_KEY), Collections
               .singletonList("bar"));
      factory.create(RackspaceAuthentication.class);
      assertEquals(JaxrsAnnotationProcessor.getParserOrThrowException(method),
               ParseAuthenticationResponseFromHeaders.class);

   }

   @BeforeClass
   void setupFactory() {
      factory = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bind(URI.class).toInstance(URI.create("http://localhost:8080"));
         }
      }, new JaxrsModule(), new ExecutorServiceModule(new WithinThreadExecutorService()),
               new JavaUrlHttpCommandExecutorServiceModule()).getInstance(
               JaxrsAnnotationProcessor.Factory.class);
   }

}
