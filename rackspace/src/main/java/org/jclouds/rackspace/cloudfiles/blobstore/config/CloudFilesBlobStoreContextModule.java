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
package org.jclouds.rackspace.cloudfiles.blobstore.config;

import javax.inject.Singleton;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.InputStreamMap;
import org.jclouds.blobstore.config.BlobStoreMapModule;
import org.jclouds.blobstore.internal.BlobStoreContextImpl;
import org.jclouds.lifecycle.Closer;
import org.jclouds.rackspace.cloudfiles.CloudFilesAsyncClient;
import org.jclouds.rackspace.cloudfiles.CloudFilesClient;
import org.jclouds.rackspace.cloudfiles.blobstore.CloudFilesAsyncBlobStore;
import org.jclouds.rackspace.cloudfiles.blobstore.CloudFilesBlobStore;
import org.jclouds.rackspace.cloudfiles.config.CloudFilesContextModule;
import org.jclouds.rest.RestContext;

import com.google.inject.Provides;

/**
 * Configures the {@link CloudFilesBlobStoreContext}; requires {@link CloudFilesAsyncBlobStore}
 * bound.
 * 
 * @author Adrian Cole
 */
public class CloudFilesBlobStoreContextModule extends CloudFilesContextModule {

   @Override
   protected void configure() {
      super.configure();
      install(new BlobStoreMapModule());
      bind(AsyncBlobStore.class).to(CloudFilesAsyncBlobStore.class).asEagerSingleton();
      bind(BlobStore.class).to(CloudFilesBlobStore.class).asEagerSingleton();
   }

   @Provides
   @Singleton
   BlobStoreContext provideContext(BlobMap.Factory blobMapFactory,
            InputStreamMap.Factory inputStreamMapFactory, Closer closer,
            AsyncBlobStore asynchBlobStore, BlobStore blobStore,
            RestContext<CloudFilesAsyncClient, CloudFilesClient> context) {
      return new BlobStoreContextImpl<CloudFilesAsyncClient, CloudFilesClient>(blobMapFactory,
               inputStreamMapFactory, asynchBlobStore, blobStore, context);
   }
}
