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

import org.jclouds.aws.s3.handlers.AWSClientErrorRetryHandler;
import org.jclouds.aws.s3.handlers.AWSRedirectionRetryHandler;
import org.jclouds.aws.s3.handlers.ParseAWSErrorFromXmlContent;
import org.jclouds.aws.s3.reference.S3Constants;
import org.jclouds.aws.s3.xml.config.S3ParserModule;
import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jclouds.util.Jsr330;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "s3.S3ContextModuleTest")
public class S3ContextModuleTest {

   Injector createInjector() {
      return Guice.createInjector(new RestS3ConnectionModule(), new ExecutorServiceModule(
               new WithinThreadExecutorService()), new S3ParserModule(), new S3ContextModule() {
         @Override
         protected void configure() {
            bindConstant().annotatedWith(Jsr330.named(S3Constants.PROPERTY_AWS_ACCESSKEYID)).to(
                     "localhost");
            bindConstant().annotatedWith(Jsr330.named(S3Constants.PROPERTY_AWS_SECRETACCESSKEY)).to(
                     "localhost");
            bindConstant().annotatedWith(Jsr330.named(S3Constants.PROPERTY_HTTP_ADDRESS)).to(
                     "localhost");
            bindConstant().annotatedWith(Jsr330.named(S3Constants.PROPERTY_HTTP_PORT)).to("1000");
            bindConstant().annotatedWith(Jsr330.named(S3Constants.PROPERTY_HTTP_SECURE)).to("false");
            bindConstant().annotatedWith(Jsr330.named(S3Constants.PROPERTY_HTTP_MAX_RETRIES))
                     .to("5");
            bindConstant().annotatedWith(Jsr330.named(S3Constants.PROPERTY_HTTP_MAX_REDIRECTS)).to(
                     "5");
            super.configure();
         }
      }, new JavaUrlHttpCommandExecutorServiceModule());
   }

   @Test
   void testServerErrorHandler() {
      DelegatingErrorHandler handler = createInjector().getInstance(DelegatingErrorHandler.class);
      assertEquals(handler.getServerErrorHandler().getClass(), ParseAWSErrorFromXmlContent.class);
   }

   @Test
   void testClientErrorHandler() {
      DelegatingErrorHandler handler = createInjector().getInstance(DelegatingErrorHandler.class);
      assertEquals(handler.getClientErrorHandler().getClass(), ParseAWSErrorFromXmlContent.class);
   }

   @Test
   void testClientRetryHandler() {
      DelegatingRetryHandler handler = createInjector().getInstance(DelegatingRetryHandler.class);
      assertEquals(handler.getClientErrorRetryHandler().getClass(),
               AWSClientErrorRetryHandler.class);
   }

   @Test
   void testRedirectionRetryHandler() {
      DelegatingRetryHandler handler = createInjector().getInstance(DelegatingRetryHandler.class);
      assertEquals(handler.getRedirectionRetryHandler().getClass(),
               AWSRedirectionRetryHandler.class);
   }

}