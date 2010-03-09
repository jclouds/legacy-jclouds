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
package org.jclouds.blobstore.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.InputStreamMap;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.rest.RestContext;

/**
 * @author Adrian Cole
 */
@Singleton
public class BlobStoreContextImpl<X, Y> implements BlobStoreContext {
   private final BlobMap.Factory blobMapFactory;
   private final InputStreamMap.Factory inputStreamMapFactory;
   private final AsyncBlobStore ablobStore;
   private final BlobStore blobStore;
   private final RestContext<X, Y> providerSpecificContext;
   private final ConsistencyModel consistencyModel;

   @Inject
   public BlobStoreContextImpl(BlobMap.Factory blobMapFactory, ConsistencyModel consistencyModel,
            InputStreamMap.Factory inputStreamMapFactory, AsyncBlobStore ablobStore,
            BlobStore blobStore, RestContext<X, Y> providerSpecificContext) {
      this.providerSpecificContext = providerSpecificContext;
      this.consistencyModel = checkNotNull(consistencyModel, "consistencyModel");
      this.blobMapFactory = checkNotNull(blobMapFactory, "blobMapFactory");
      this.inputStreamMapFactory = checkNotNull(inputStreamMapFactory, "inputStreamMapFactory");
      this.ablobStore = checkNotNull(ablobStore, "ablobStore");
      this.blobStore = checkNotNull(blobStore, "blobStore");
   }

   @Override
   public ConsistencyModel getConsistencyModel() {
      return consistencyModel;
   }

   @Override
   public BlobMap createBlobMap(String container, ListContainerOptions options) {
      return blobMapFactory.create(container, options);
   }

   @Override
   public BlobMap createBlobMap(String container) {
      return blobMapFactory.create(container, ListContainerOptions.NONE);
   }

   @Override
   public InputStreamMap createInputStreamMap(String container, ListContainerOptions options) {
      return inputStreamMapFactory.create(container, options);
   }

   @Override
   public InputStreamMap createInputStreamMap(String container) {
      return inputStreamMapFactory.create(container, ListContainerOptions.NONE);
   }

   @Override
   public BlobStore getBlobStore() {
      return blobStore;
   }

   @Override
   public AsyncBlobStore getAsyncBlobStore() {
      return ablobStore;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <A, S> RestContext<A, S> getProviderSpecificContext() {
      return (RestContext<A, S>) providerSpecificContext;
   }

   @Override
   public void close() {
      providerSpecificContext.close();
   }
}
