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
package org.jclouds.aws.s3.integration;

import org.jclouds.aws.s3.S3Connection;
import org.jclouds.aws.s3.S3ContextBuilder;
import org.jclouds.aws.s3.S3ContextFactory;
import org.jclouds.aws.s3.config.StubS3BlobStoreModule;
import org.jclouds.aws.s3.domain.BucketMetadata;
import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.jclouds.blobstore.integration.internal.BaseTestInitializer;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;

import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
public class S3TestInitializer extends
         BaseTestInitializer<S3Connection, BucketMetadata, ObjectMetadata, S3Object> {

   @Override
   protected BlobStoreContext<S3Connection, BucketMetadata, ObjectMetadata, S3Object> createLiveContext(
            Module configurationModule, String url, String app, String account, String key) {
      BaseBlobStoreIntegrationTest.SANITY_CHECK_RETURNED_BUCKET_NAME = true;
      return new S3ContextBuilder(account, key).withSaxDebug().relaxSSLHostname().withModules(
               configurationModule, new Log4JLoggingModule()).buildContext();
   }

   @Override
   protected BlobStoreContext<S3Connection, BucketMetadata, ObjectMetadata, S3Object> createStubContext() {
      return S3ContextFactory.createContext("user", "pass", new StubS3BlobStoreModule());
   }

}