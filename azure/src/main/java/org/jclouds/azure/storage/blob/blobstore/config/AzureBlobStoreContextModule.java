/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.azure.storage.blob.blobstore.config;

import javax.inject.Singleton;

import org.jclouds.azure.storage.blob.AzureBlobAsyncClient;
import org.jclouds.azure.storage.blob.AzureBlobClient;
import org.jclouds.azure.storage.blob.blobstore.AzureAsyncBlobStore;
import org.jclouds.azure.storage.blob.blobstore.AzureBlobStore;
import org.jclouds.azure.storage.blob.blobstore.strategy.FindMD5InBlobProperties;
import org.jclouds.azure.storage.blob.config.AzureBlobContextModule;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.InputStreamMap;
import org.jclouds.blobstore.config.BlobStoreMapModule;
import org.jclouds.blobstore.internal.BlobStoreContextImpl;
import org.jclouds.blobstore.strategy.ContainsValueInListStrategy;
import org.jclouds.lifecycle.Closer;
import org.jclouds.rest.RestContext;

import com.google.inject.Provides;

/**
 * Configures the {@link AzureBlobStoreContext}; requires {@link AzureAsyncBlobStore} bound.
 * 
 * @author Adrian Cole
 */
public class AzureBlobStoreContextModule extends AzureBlobContextModule {

   @Override
   protected void configure() {
      super.configure();
      install(new BlobStoreMapModule());
      bind(AsyncBlobStore.class).to(AzureAsyncBlobStore.class).asEagerSingleton();
      bind(BlobStore.class).to(AzureBlobStore.class).asEagerSingleton();
      bind(ContainsValueInListStrategy.class).to(FindMD5InBlobProperties.class);
   }

   @Provides
   @Singleton
   BlobStoreContext provideContext(BlobMap.Factory blobMapFactory,
            InputStreamMap.Factory inputStreamMapFactory, Closer closer,
            AzureAsyncBlobStore asynchBlobStore, AzureBlobStore blobStore,
            RestContext<AzureBlobAsyncClient, AzureBlobClient> context) {
      return new BlobStoreContextImpl<AzureBlobAsyncClient, AzureBlobClient>(blobMapFactory,
               inputStreamMapFactory, asynchBlobStore, blobStore, context);
   }

}
