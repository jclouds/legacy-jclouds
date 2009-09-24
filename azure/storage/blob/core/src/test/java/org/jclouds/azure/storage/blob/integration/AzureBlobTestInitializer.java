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
package org.jclouds.azure.storage.blob.integration;

import org.jclouds.azure.storage.blob.AzureBlobContext;
import org.jclouds.azure.storage.blob.AzureBlobContextBuilder;
import org.jclouds.azure.storage.blob.AzureBlobStore;
import org.jclouds.azure.storage.blob.config.StubAzureBlobStoreModule;
import org.jclouds.azure.storage.blob.domain.Blob;
import org.jclouds.azure.storage.blob.domain.BlobMetadata;
import org.jclouds.azure.storage.blob.domain.ContainerMetadata;
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
public class AzureBlobTestInitializer
         implements
         BaseBlobStoreIntegrationTest.TestInitializer<AzureBlobStore, ContainerMetadata, BlobMetadata, Blob> {

   public BaseBlobStoreIntegrationTest.TestInitializer.Result<AzureBlobStore, ContainerMetadata, BlobMetadata, Blob> init(
            Module configurationModule, ITestContext testContext) throws Exception {
      String account = System.getProperty("jclouds.test.user");
      String key = System.getProperty("jclouds.test.key");
      if (account != null)
         testContext.setAttribute("jclouds.test.user", account);
      if (key != null)
         testContext.setAttribute("jclouds.test.key", key);

      final AzureBlobContext context;
      if (account != null) {
         context = createLiveAzureBlobContext(configurationModule, account, key);
      } else {
         context = createStubAzureBlobContext();
      }
      assert context != null;

      final AzureBlobStore client = context.getApi();
      assert client != null;

      final BlobStoreObjectFactory<ContainerMetadata, Blob> objectFactory = new BaseBlobStoreIntegrationTest.BlobStoreObjectFactory<ContainerMetadata, Blob>() {

         public Blob createBlob(String key) {
            return new Blob(key);

         }

         public ContainerMetadata createContainerMetadata(String key) {
            return new ContainerMetadata(key);
         }

      };
      assert objectFactory != null;

      return new BaseBlobStoreIntegrationTest.TestInitializer.Result<AzureBlobStore, ContainerMetadata, BlobMetadata, Blob>() {

         public AzureBlobStore getClient() {
            return client;
         }

         public BlobStoreContext<AzureBlobStore, BlobMetadata, Blob> getContext() {
            return (BlobStoreContext<AzureBlobStore, BlobMetadata, Blob>) context;
         }

         public BlobStoreObjectFactory<ContainerMetadata, Blob> getObjectFactory() {
            return objectFactory;
         }

      };
   }

   protected AzureBlobContext createStubAzureBlobContext() {
      return AzureBlobContextBuilder.newBuilder("stub", "stub").withModules(
               new StubAzureBlobStoreModule()).buildContext();
   }

   protected AzureBlobContext createLiveAzureBlobContext(Module configurationModule,
            String account, String key) {
      return buildAzureBlobContextFactory(configurationModule, account, key).buildContext();
   }

   protected AzureBlobContextBuilder buildAzureBlobContextFactory(Module configurationModule,
            String account, String key) {
      return (AzureBlobContextBuilder) AzureBlobContextBuilder.newBuilder(account, key)
               .relaxSSLHostname().withModules(configurationModule, new Log4JLoggingModule());
   }

}