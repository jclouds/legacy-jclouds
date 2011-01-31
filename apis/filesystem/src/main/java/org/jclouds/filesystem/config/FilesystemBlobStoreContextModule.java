/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.filesystem.config;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.config.BlobStoreMapModule;
import org.jclouds.blobstore.config.BlobStoreObjectModule;
import org.jclouds.blobstore.internal.BlobStoreContextImpl;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.filesystem.FilesystemBlobStore;
import org.jclouds.filesystem.predicates.validators.FilesystemBlobKeyValidator;
import org.jclouds.filesystem.predicates.validators.FilesystemContainerNameValidator;
import org.jclouds.filesystem.predicates.validators.internal.FilesystemBlobKeyValidatorImpl;
import org.jclouds.filesystem.predicates.validators.internal.FilesystemContainerNameValidatorImpl;
import org.jclouds.filesystem.strategy.FilesystemStorageStrategy;
import org.jclouds.filesystem.strategy.internal.FilesystemStorageStrategyImpl;
import org.jclouds.filesystem.util.internal.FileSystemBlobUtilsImpl;
import org.jclouds.location.config.JustProviderLocationModule;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class FilesystemBlobStoreContextModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(new TypeLiteral<BlobStoreContext>() {
      }).to(new TypeLiteral<BlobStoreContextImpl<FilesystemBlobStore, AsyncBlobStore>>() {
      }).in(Scopes.SINGLETON);
      install(new BlobStoreObjectModule());
      install(new BlobStoreMapModule());
      bind(ConsistencyModel.class).toInstance(ConsistencyModel.STRICT);
      bind(FilesystemStorageStrategy.class).to(FilesystemStorageStrategyImpl.class);
      bind(BlobUtils.class).to(FileSystemBlobUtilsImpl.class);
      bind(FilesystemBlobKeyValidator.class).to(FilesystemBlobKeyValidatorImpl.class);
      bind(FilesystemContainerNameValidator.class).to(FilesystemContainerNameValidatorImpl.class);
      install(new JustProviderLocationModule());
   }

   @Provides
   @Singleton
   BlobStore provide(FilesystemBlobStore in) {
      return in;
   }

}
