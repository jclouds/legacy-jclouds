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
package org.jclouds.aws.s3.config;

import static org.testng.Assert.assertEquals;

import org.jclouds.aws.s3.handlers.ParseAWSErrorFromXmlContent;
import org.jclouds.aws.s3.reference.S3Constants;
import org.jclouds.http.HttpResponseHandler;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.annotation.ClientErrorHandler;
import org.jclouds.http.annotation.RedirectHandler;
import org.jclouds.http.annotation.RetryHandler;
import org.jclouds.http.annotation.ServerErrorHandler;
import org.jclouds.http.config.JavaUrlHttpFutureCommandClientModule;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.http.handlers.CloseContentAndSetExceptionHandler;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Names;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "s3.S3ContextModuleTest")
public class S3ContextModuleTest {

   Injector createInjector() {
      return Guice.createInjector(new LiveS3ConnectionModule(), new S3ContextModule() {
         @Override
         protected void configure() {
            bindConstant().annotatedWith(Names.named(S3Constants.PROPERTY_AWS_ACCESSKEYID)).to(
                     "localhost");
            bindConstant().annotatedWith(Names.named(S3Constants.PROPERTY_AWS_SECRETACCESSKEY)).to(
                     "localhost");
            bindConstant().annotatedWith(Names.named(S3Constants.PROPERTY_HTTP_ADDRESS)).to(
                     "localhost");
            bindConstant().annotatedWith(Names.named(S3Constants.PROPERTY_HTTP_PORT)).to("1000");
            bindConstant().annotatedWith(Names.named(S3Constants.PROPERTY_HTTP_SECURE)).to("false");
            bindConstant().annotatedWith(Names.named(S3Constants.PROPERTY_HTTP_MAX_RETRIES)).to("5");
            super.configure();
         }
      }, new JavaUrlHttpFutureCommandClientModule());
   }

   private static class ClientErrorHandlerTest {
      @Inject
      @ClientErrorHandler
      HttpResponseHandler errorHandler;
   }

   @Test
   void testClientErrorHandler() {
      ClientErrorHandlerTest error = createInjector().getInstance(ClientErrorHandlerTest.class);
      assertEquals(error.errorHandler.getClass(), ParseAWSErrorFromXmlContent.class);
   }

   private static class ServerErrorHandlerTest {
      @Inject
      @ServerErrorHandler
      HttpResponseHandler errorHandler;
   }

   @Test
   void testServerErrorHandler() {
      ServerErrorHandlerTest error = createInjector().getInstance(ServerErrorHandlerTest.class);
      assertEquals(error.errorHandler.getClass(), ParseAWSErrorFromXmlContent.class);
   }

   private static class RedirectHandlerTest {
      @Inject
      @RedirectHandler
      HttpResponseHandler errorHandler;
   }

   @Test
   void testRedirectHandler() {
      RedirectHandlerTest error = createInjector().getInstance(RedirectHandlerTest.class);
      assertEquals(error.errorHandler.getClass(), CloseContentAndSetExceptionHandler.class);
   }

   private static class RetryHandlerTest {
      @Inject
      @RetryHandler
      HttpRetryHandler retryHandler;
   }

   @Test
   void testRetryHandler() {
      RetryHandlerTest handler = createInjector().getInstance(RetryHandlerTest.class);
      assertEquals(handler.retryHandler.getClass(), BackoffLimitedRetryHandler.class);
   }

}