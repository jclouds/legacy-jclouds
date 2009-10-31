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
package org.jclouds.azure.storage.blob.blobstore.config;

import java.net.URI;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.azure.storage.AzureBlob;
import org.jclouds.azure.storage.blob.AzureBlobClient;
import org.jclouds.azure.storage.blob.blobstore.AzureBlobStore;
import org.jclouds.azure.storage.blob.blobstore.strategy.FindMD5InBlobProperties;
import org.jclouds.azure.storage.blob.config.AzureBlobModule;
import org.jclouds.azure.storage.reference.AzureStorageConstants;
import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.InputStreamMap;
import org.jclouds.blobstore.config.BlobStoreMapModule;
import org.jclouds.blobstore.config.BlobStoreObjectModule;
import org.jclouds.blobstore.internal.BlobStoreContextImpl;
import org.jclouds.blobstore.strategy.ContainsValueInListStrategy;
import org.jclouds.lifecycle.Closer;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures the {@link AzureBlobStoreContext}; requires {@link AzureBlobStore} bound.
 * 
 * @author Adrian Cole
 */
public class AzureBlobStoreContextModule extends AbstractModule {

   @Override
   protected void configure() {
      install(new BlobStoreObjectModule());
      install(new BlobStoreMapModule());
      install(new AzureBlobModule());
      bind(BlobStore.class).to(AzureBlobStore.class).asEagerSingleton();
      bind(ContainsValueInListStrategy.class).to(FindMD5InBlobProperties.class);
   }

   @Provides
   @Singleton
   BlobStoreContext<AzureBlobClient> provideContext(BlobMap.Factory blobMapFactory,
            InputStreamMap.Factory inputStreamMapFactory, Closer closer, BlobStore blobStore,
            AzureBlobClient defaultApi, @AzureBlob URI endPoint,
            @Named(AzureStorageConstants.PROPERTY_AZURESTORAGE_ACCOUNT) String account) {
      return new BlobStoreContextImpl<AzureBlobClient>(blobMapFactory, inputStreamMapFactory,
               closer, blobStore, defaultApi, endPoint, account);
   }

}
