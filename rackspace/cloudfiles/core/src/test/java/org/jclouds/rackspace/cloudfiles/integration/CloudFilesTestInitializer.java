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
package org.jclouds.rackspace.cloudfiles.integration;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest.BlobStoreObjectFactory;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rackspace.cloudfiles.CloudFilesBlobStore;
import org.jclouds.rackspace.cloudfiles.CloudFilesContext;
import org.jclouds.rackspace.cloudfiles.CloudFilesContextBuilder;
import org.jclouds.rackspace.cloudfiles.domain.ContainerMetadata;
import org.jclouds.rackspace.config.StubCloudFilesBlobStoreModule;
import org.testng.ITestContext;

import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
public class CloudFilesTestInitializer
         implements
         BaseBlobStoreIntegrationTest.TestInitializer<CloudFilesBlobStore, ContainerMetadata, BlobMetadata, Blob<BlobMetadata>> {

   public BaseBlobStoreIntegrationTest.TestInitializer.Result<CloudFilesBlobStore, ContainerMetadata, BlobMetadata, Blob<BlobMetadata>> init(
            Module configurationModule, ITestContext testContext) throws Exception {
      String account = System.getProperty("jclouds.test.user");
      String key = System.getProperty("jclouds.test.key");
      if (account != null)
         testContext.setAttribute("jclouds.test.user", account);
      if (key != null)
         testContext.setAttribute("jclouds.test.key", key);

      final CloudFilesContext context;
      if (account != null) {
         context = createLiveCloudFilesContext(configurationModule, account, key);
      } else {
         context = createStubCloudFilesContext();
      }
      assert context != null;

      final CloudFilesBlobStore client = context.getApi();
      assert client != null;

      final BlobStoreObjectFactory<ContainerMetadata, Blob<BlobMetadata>> objectFactory = new BaseBlobStoreIntegrationTest.BlobStoreObjectFactory<ContainerMetadata, Blob<BlobMetadata>>() {

         public Blob<BlobMetadata> createBlob(String key) {
            return new Blob<BlobMetadata>(key);

         }

         public ContainerMetadata createContainerMetadata(String key) {
            return new ContainerMetadata(key);
         }

      };
      assert objectFactory != null;

      return new BaseBlobStoreIntegrationTest.TestInitializer.Result<CloudFilesBlobStore, ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>() {

         public CloudFilesBlobStore getClient() {
            return client;
         }

         public BlobStoreContext<CloudFilesBlobStore, BlobMetadata, Blob<BlobMetadata>> getContext() {
            return (BlobStoreContext<CloudFilesBlobStore, BlobMetadata, Blob<BlobMetadata>>) context;
         }

         public BlobStoreObjectFactory<ContainerMetadata, Blob<BlobMetadata>> getObjectFactory() {
            return objectFactory;
         }

      };
   }

   protected CloudFilesContext createStubCloudFilesContext() {
      return CloudFilesContextBuilder.newBuilder("stub", "stub").withModules(
               new StubCloudFilesBlobStoreModule()).buildContext();
   }

   protected CloudFilesContext createLiveCloudFilesContext(Module configurationModule,
            String account, String key) {
      return buildCloudFilesContextFactory(configurationModule, account, key).buildContext();
   }

   protected CloudFilesContextBuilder buildCloudFilesContextFactory(Module configurationModule,
            String account, String key) {
      return (CloudFilesContextBuilder) CloudFilesContextBuilder.newBuilder(account, key)
               .relaxSSLHostname().withModules(configurationModule, new Log4JLoggingModule());
   }

}