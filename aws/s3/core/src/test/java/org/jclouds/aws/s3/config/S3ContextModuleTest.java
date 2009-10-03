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

import org.jclouds.aws.s3.S3BlobStore;
import org.jclouds.aws.s3.domain.BucketMetadata;
import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.handlers.AWSClientErrorRetryHandler;
import org.jclouds.aws.s3.handlers.AWSRedirectionRetryHandler;
import org.jclouds.aws.s3.handlers.ParseAWSErrorFromXmlContent;
import org.jclouds.aws.s3.reference.S3Constants;
import org.jclouds.blobstore.BlobStoreMapsModule;
import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.util.Jsr330;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "s3.S3ContextModuleTest")
public class S3ContextModuleTest {

   Injector createInjector() {
      return Guice.createInjector(new RestS3ConnectionModule(), BlobStoreMapsModule.Builder
               .newBuilder(new TypeLiteral<S3BlobStore>() {
               }, new TypeLiteral<BucketMetadata>() {
               }, new TypeLiteral<ObjectMetadata>() {
               }, new TypeLiteral<S3Object>() {
               }).build(), new S3ContextModule() {
         @Override
         protected void configure() {
            bindConstant().annotatedWith(Jsr330.named(S3Constants.PROPERTY_AWS_ACCESSKEYID)).to(
                     "user");
            bindConstant().annotatedWith(Jsr330.named(S3Constants.PROPERTY_AWS_SECRETACCESSKEY))
                     .to("key");
            bindConstant().annotatedWith(Jsr330.named(S3Constants.PROPERTY_S3_ENDPOINT)).to(
                     "http://localhost");
            super.configure();
         }
      }, new ParserModule(), new JavaUrlHttpCommandExecutorServiceModule(),
               new ExecutorServiceModule(new WithinThreadExecutorService()));
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