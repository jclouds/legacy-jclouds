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
package org.jclouds.aws.s3.blobstore.config;

import javax.inject.Singleton;

import org.jclouds.aws.s3.S3AsyncClient;
import org.jclouds.aws.s3.S3Client;
import org.jclouds.aws.s3.blobstore.S3AsyncBlobStore;
import org.jclouds.aws.s3.blobstore.S3BlobStore;
import org.jclouds.aws.s3.config.S3ContextModule;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.InputStreamMap;
import org.jclouds.blobstore.config.BlobStoreMapModule;
import org.jclouds.blobstore.internal.BlobStoreContextImpl;
import org.jclouds.lifecycle.Closer;
import org.jclouds.rest.RestContext;

import com.google.inject.Provides;

/**
 * Configures the {@link S3BlobStoreContext}; requires {@link S3AsyncBlobStore} bound.
 * 
 * @author Adrian Cole
 */
public class S3BlobStoreContextModule extends S3ContextModule {

   @Override
   protected void configure() {
      super.configure();
      install(new BlobStoreMapModule());
      bind(AsyncBlobStore.class).to(S3AsyncBlobStore.class).asEagerSingleton();
      bind(BlobStore.class).to(S3BlobStore.class).asEagerSingleton();
   }

   @Provides
   @Singleton
   BlobStoreContext provideContext(BlobMap.Factory blobMapFactory,
            InputStreamMap.Factory inputStreamMapFactory, Closer closer,
            S3AsyncBlobStore asynchBlobStore, S3BlobStore blobStore,
            RestContext<S3AsyncClient, S3Client> context) {
      return new BlobStoreContextImpl<S3AsyncClient, S3Client>(blobMapFactory,
               inputStreamMapFactory, asynchBlobStore, blobStore, context);
   }

}
