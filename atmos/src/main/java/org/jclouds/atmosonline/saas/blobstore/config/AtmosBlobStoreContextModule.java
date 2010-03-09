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

import org.jclouds.atmosonline.saas.AtmosStorageAsyncClient;
import org.jclouds.atmosonline.saas.AtmosStorageClient;
import org.jclouds.atmosonline.saas.blobstore.AtmosAsyncBlobStore;
import org.jclouds.atmosonline.saas.blobstore.AtmosBlobStore;
import org.jclouds.atmosonline.saas.blobstore.strategy.FindMD5InUserMetadata;
import org.jclouds.atmosonline.saas.config.AtmosStorageContextModule;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.config.BlobStoreMapModule;
import org.jclouds.blobstore.internal.BlobStoreContextImpl;
import org.jclouds.blobstore.strategy.ContainsValueInListStrategy;

import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

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
      bind(ConsistencyModel.class).toInstance(ConsistencyModel.EVENTUAL);
      bind(AsyncBlobStore.class).to(AtmosAsyncBlobStore.class).in(Scopes.SINGLETON);
      bind(BlobStore.class).to(AtmosBlobStore.class).in(Scopes.SINGLETON);
      bind(BlobStoreContext.class)
               .to(
                        new TypeLiteral<BlobStoreContextImpl<AtmosStorageAsyncClient, AtmosStorageClient>>() {
                        }).in(Scopes.SINGLETON);
      bind(ContainsValueInListStrategy.class).to(FindMD5InUserMetadata.class);
   }

}
