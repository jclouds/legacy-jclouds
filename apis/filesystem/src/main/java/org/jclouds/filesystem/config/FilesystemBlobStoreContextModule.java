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
package org.jclouds.filesystem.config;

import static org.jclouds.rest.config.BinderUtils.bindSyncToAsyncApi;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.LocalAsyncBlobStore;
import org.jclouds.blobstore.LocalBlobRequestSigner;
import org.jclouds.blobstore.LocalStorageStrategy;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.config.BlobStoreMapModule;
import org.jclouds.blobstore.config.BlobStoreObjectModule;
import org.jclouds.blobstore.config.LocalBlobStore;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.filesystem.predicates.validators.FilesystemBlobKeyValidator;
import org.jclouds.filesystem.predicates.validators.FilesystemContainerNameValidator;
import org.jclouds.filesystem.predicates.validators.internal.FilesystemBlobKeyValidatorImpl;
import org.jclouds.filesystem.predicates.validators.internal.FilesystemContainerNameValidatorImpl;
import org.jclouds.filesystem.strategy.internal.FilesystemStorageStrategyImpl;
import org.jclouds.filesystem.util.internal.FileSystemBlobUtilsImpl;

import com.google.inject.AbstractModule;

/**
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class FilesystemBlobStoreContextModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(AsyncBlobStore.class).to(LocalAsyncBlobStore.class).asEagerSingleton();
      // forward all requests from TransientBlobStore to TransientAsyncBlobStore.  needs above binding as cannot proxy a class
      bindSyncToAsyncApi(binder(), LocalBlobStore.class, AsyncBlobStore.class);
      bind(BlobStore.class).to(LocalBlobStore.class);

      install(new BlobStoreObjectModule());
      install(new BlobStoreMapModule());
      bind(ConsistencyModel.class).toInstance(ConsistencyModel.STRICT);
      bind(LocalStorageStrategy.class).to(FilesystemStorageStrategyImpl.class);
      bind(BlobUtils.class).to(FileSystemBlobUtilsImpl.class);
      bind(FilesystemBlobKeyValidator.class).to(FilesystemBlobKeyValidatorImpl.class);
      bind(FilesystemContainerNameValidator.class).to(FilesystemContainerNameValidatorImpl.class);
      bind(BlobRequestSigner.class).to(LocalBlobRequestSigner.class);
   }

}
