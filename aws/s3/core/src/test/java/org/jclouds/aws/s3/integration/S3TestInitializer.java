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

import org.jclouds.aws.s3.S3BlobStore;
import org.jclouds.aws.s3.S3Context;
import org.jclouds.aws.s3.S3ContextBuilder;
import org.jclouds.aws.s3.S3ContextFactory;
import org.jclouds.aws.s3.config.StubS3BlobStoreModule;
import org.jclouds.aws.s3.domain.BucketMetadata;
import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest.BlobStoreObjectFactory;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.testng.ITestContext;

import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
public class S3TestInitializer
         implements
         BaseBlobStoreIntegrationTest.TestInitializer<S3BlobStore, BucketMetadata, ObjectMetadata, S3Object> {

   public BaseBlobStoreIntegrationTest.TestInitializer.Result<S3BlobStore, BucketMetadata, ObjectMetadata, S3Object> init(
            Module configurationModule, ITestContext testContext) throws Exception {
      String account = System.getProperty("jclouds.test.user");
      String key = System.getProperty("jclouds.test.key");
      if (account != null)
         testContext.setAttribute("jclouds.test.user", account);
      if (key != null)
         testContext.setAttribute("jclouds.test.key", key);

      final S3Context context;
      if (account != null) {
         context = createLiveS3Context(configurationModule, account, key);
      } else {
         context = createStubS3Context();
      }
      assert context != null;

      final S3BlobStore client = context.getApi();
      assert client != null;

      final BlobStoreObjectFactory<BucketMetadata, S3Object> objectFactory = new BaseBlobStoreIntegrationTest.BlobStoreObjectFactory<BucketMetadata, S3Object>() {

         public S3Object createBlob(String key) {
            return new S3Object(key);

         }

         public BucketMetadata createContainerMetadata(String key) {
            return new BucketMetadata(key);
         }

      };
      assert objectFactory != null;

      return new BaseBlobStoreIntegrationTest.TestInitializer.Result<S3BlobStore, BucketMetadata, ObjectMetadata, S3Object>() {

         public S3BlobStore getClient() {
            return client;
         }

         public BlobStoreContext<S3BlobStore, ObjectMetadata, S3Object> getContext() {
            return context;
         }

         public BlobStoreObjectFactory<BucketMetadata, S3Object> getObjectFactory() {
            return objectFactory;
         }

      };
   }

   protected S3Context createStubS3Context() {
      BaseBlobStoreIntegrationTest.SANITY_CHECK_RETURNED_BUCKET_NAME = true;
      return S3ContextFactory.createS3Context("stub", "stub", new StubS3BlobStoreModule());
   }

   protected S3Context createLiveS3Context(Module configurationModule, String AWSAccessKeyId,
            String AWSSecretAccessKey) {
      return buildS3ContextFactory(configurationModule, AWSAccessKeyId, AWSSecretAccessKey)
               .buildContext();
   }

   // protected String createScratchContainerInEU() throws InterruptedException, ExecutionException,
   // TimeoutException {
   // String containerName = getScratchContainerName();
   // deleteContainer(containerName);
   // client.createContainer(containerName, PutBucketOptions.Builder
   // .createIn(LocationConstraint.EU));
   // return containerName;
   // }

   protected S3ContextBuilder buildS3ContextFactory(Module configurationModule,
            String AWSAccessKeyId, String AWSSecretAccessKey) {
      return (S3ContextBuilder) S3ContextBuilder.newBuilder(AWSAccessKeyId, AWSSecretAccessKey)
               .withSaxDebug().relaxSSLHostname().withModules(configurationModule,
                        new Log4JLoggingModule());
   }

}