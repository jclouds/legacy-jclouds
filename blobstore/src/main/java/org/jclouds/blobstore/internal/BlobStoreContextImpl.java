/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.blobstore.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.Context;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.InputStreamMap;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.internal.BaseView;
import org.jclouds.location.Provider;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.Utils;

import com.google.common.io.Closeables;
import com.google.common.reflect.TypeToken;

/**
 * @author Adrian Cole
 */
@Singleton
public class BlobStoreContextImpl extends BaseView implements BlobStoreContext {
   private final BlobMap.Factory blobMapFactory;
   private final InputStreamMap.Factory inputStreamMapFactory;
   private final AsyncBlobStore ablobStore;
   private final BlobStore blobStore;
   private final ConsistencyModel consistencyModel;
   private final Utils utils;
   private final BlobRequestSigner blobRequestSigner;

   @Inject
   public BlobStoreContextImpl(@Provider Context backend, @Provider TypeToken<? extends Context> backendType,
            BlobMap.Factory blobMapFactory, Utils utils, ConsistencyModel consistencyModel,
            InputStreamMap.Factory inputStreamMapFactory, AsyncBlobStore ablobStore, BlobStore blobStore,
            BlobRequestSigner blobRequestSigner) {
      super(backend, backendType);
      this.consistencyModel = checkNotNull(consistencyModel, "consistencyModel");
      this.blobMapFactory = checkNotNull(blobMapFactory, "blobMapFactory");
      this.inputStreamMapFactory = checkNotNull(inputStreamMapFactory, "inputStreamMapFactory");
      this.ablobStore = checkNotNull(ablobStore, "ablobStore");
      this.blobStore = checkNotNull(blobStore, "blobStore");
      this.utils = checkNotNull(utils, "utils");
      this.blobRequestSigner = checkNotNull(blobRequestSigner, "blobRequestSigner");
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

   @Override
   public Utils getUtils() {
      return utils();
   }

   @Override
   public Utils utils() {
      return utils;
   }

   @Override
   public BlobRequestSigner getSigner() {
      return blobRequestSigner;
   }

   @Override
   public void close() {
      Closeables.closeQuietly(delegate());
   }

   public int hashCode() {
      return delegate().hashCode();
   }

   @Override
   public String toString() {
      return delegate().toString();
   }

   @Override
   public boolean equals(Object obj) {
      return delegate().equals(obj);
   }

}
