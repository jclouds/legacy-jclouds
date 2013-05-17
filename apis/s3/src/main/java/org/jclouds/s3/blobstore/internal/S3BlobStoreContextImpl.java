/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.s3.blobstore.internal;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.Context;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.InputStreamMap;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.internal.BlobStoreContextImpl;
import org.jclouds.location.Provider;
import org.jclouds.rest.Utils;
import org.jclouds.s3.blobstore.S3AsyncBlobStore;
import org.jclouds.s3.blobstore.S3BlobStore;
import org.jclouds.s3.blobstore.S3BlobStoreContext;

import com.google.common.reflect.TypeToken;

/**
 * @author Adrian Cole
 */
@Singleton
public class S3BlobStoreContextImpl extends BlobStoreContextImpl implements S3BlobStoreContext {

   @Inject
   public S3BlobStoreContextImpl(@Provider Context backend, @Provider TypeToken<? extends Context> backendType,
            BlobMap.Factory blobMapFactory, Utils utils, ConsistencyModel consistencyModel,
            InputStreamMap.Factory inputStreamMapFactory, AsyncBlobStore ablobStore, BlobStore blobStore,
            BlobRequestSigner blobRequestSigner) {
      super(backend, backendType, blobMapFactory, utils, consistencyModel, inputStreamMapFactory, ablobStore,
               blobStore, blobRequestSigner);
   }

   @Override
   public S3BlobStore getBlobStore() {
      return S3BlobStore.class.cast(super.getBlobStore());
   }

   @Override
   public S3AsyncBlobStore getAsyncBlobStore() {
      return S3AsyncBlobStore.class.cast(super.getAsyncBlobStore());
   }

}
