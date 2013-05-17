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
package org.jclouds.blobstore.config;

import static org.jclouds.rest.config.BinderUtils.bindSyncToAsyncApi;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.LocalAsyncBlobStore;
import org.jclouds.blobstore.LocalBlobRequestSigner;
import org.jclouds.blobstore.LocalStorageStrategy;
import org.jclouds.blobstore.TransientStorageStrategy;
import org.jclouds.blobstore.attr.ConsistencyModel;

import com.google.inject.AbstractModule;

/**
 * Configures the {@link TransientBlobStoreContext}; requires {@link TransientAsyncBlobStore} bound.
 * 
 * @author Adrian Cole
 */
public class TransientBlobStoreContextModule extends AbstractModule {
   @Override
   protected void configure() {
      bind(AsyncBlobStore.class).to(LocalAsyncBlobStore.class).asEagerSingleton();
      // forward all requests from TransientBlobStore to TransientAsyncBlobStore.  needs above binding as cannot proxy a class
      bindSyncToAsyncApi(binder(), LocalBlobStore.class, AsyncBlobStore.class);
      install(new BlobStoreObjectModule());
      install(new BlobStoreMapModule());
      bind(BlobStore.class).to(LocalBlobStore.class);
      bind(ConsistencyModel.class).toInstance(ConsistencyModel.STRICT);
      bind(LocalStorageStrategy.class).to(TransientStorageStrategy.class);
      bind(BlobRequestSigner.class).to(LocalBlobRequestSigner.class);
   }

}
