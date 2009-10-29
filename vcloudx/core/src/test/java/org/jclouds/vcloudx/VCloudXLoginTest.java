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
package org.jclouds.vcloudx;

import static org.testng.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URI;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rest.config.RestModule;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.vcloudx.endpoints.VCloudX;
import org.jclouds.vcloudx.functions.ParseLoginResponseFromHeaders;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code VCloudXLogin}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloudx.VCloudXLogin")
public class VCloudXLoginTest {

   private RestAnnotationProcessor<VCloudXLogin> processor;

   public void testAuthenticate() throws SecurityException, NoSuchMethodException {
      Method method = VCloudXLogin.class.getMethod("login");
      HttpRequest httpMethod = processor.createRequest(method);
      assertEquals(httpMethod.getRequestLine(), "POST http://localhost:8080/login HTTP/1.1");
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getFirstHeaderOrNull(HttpHeaders.ACCEPT), MediaType.APPLICATION_XML);
      assertEquals(RestAnnotationProcessor.getParserOrThrowException(method),
               ParseLoginResponseFromHeaders.class);
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), BasicAuthentication.class);
   }

   @BeforeClass
   void setupFactory() {
      Injector injector = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bind(URI.class).annotatedWith(VCloudX.class).toInstance(
                     URI.create("http://localhost:8080"));
            try {
               bind(BasicAuthentication.class).toInstance(new BasicAuthentication("user", "pass"));
            } catch (UnsupportedEncodingException e) {
               throw new RuntimeException(e);
            }
            bind(Logger.LoggerFactory.class).toInstance(new LoggerFactory() {
               public Logger getLogger(String category) {
                  return Logger.NULL;
               }
            });
         }
      }, new RestModule(), new ExecutorServiceModule(new WithinThreadExecutorService()),
               new JavaUrlHttpCommandExecutorServiceModule());
      processor = injector.getInstance(Key
               .get(new TypeLiteral<RestAnnotationProcessor<VCloudXLogin>>() {
               }));
   }

}
