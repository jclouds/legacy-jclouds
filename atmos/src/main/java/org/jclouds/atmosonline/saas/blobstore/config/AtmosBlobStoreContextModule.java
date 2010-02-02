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
package org.jclouds.atmosonline.saas.blobstore.config;

import javax.inject.Singleton;

import org.jclouds.atmosonline.saas.AtmosStorageAsyncClient;
import org.jclouds.atmosonline.saas.AtmosStorageClient;
import org.jclouds.atmosonline.saas.blobstore.AtmosAsyncBlobStore;
import org.jclouds.atmosonline.saas.blobstore.AtmosBlobStore;
import org.jclouds.atmosonline.saas.blobstore.strategy.FindMD5InUserMetadata;
import org.jclouds.atmosonline.saas.config.AtmosStorageContextModule;
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
 * Configures the {@link AtmosBlobStoreContext}; requires {@link AtmosAsyncBlobStore} bound.
 * 
 * @author Adrian Cole
 */
public class AtmosBlobStoreContextModule extends AtmosStorageContextModule {

   @Override
   protected void configure() {
      super.configure();
      install(new BlobStoreMapModule());
      bind(AsyncBlobStore.class).to(AtmosAsyncBlobStore.class).asEagerSingleton();
      bind(BlobStore.class).to(AtmosBlobStore.class).asEagerSingleton();
      bind(ContainsValueInListStrategy.class).to(FindMD5InUserMetadata.class);
   }

   @Provides
   @Singleton
   BlobStoreContext provideContext(BlobMap.Factory blobMapFactory,
            InputStreamMap.Factory inputStreamMapFactory, Closer closer,
            AsyncBlobStore asynchBlobStore, BlobStore blobStore,
            RestContext<AtmosStorageAsyncClient, AtmosStorageClient> context) {
      return new BlobStoreContextImpl<AtmosStorageAsyncClient, AtmosStorageClient>(blobMapFactory,
               inputStreamMapFactory, asynchBlobStore, blobStore, context);
   }

}
