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

package org.jclouds.rackspace.cloudfiles.blobstore.config;

import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.config.BlobStoreMapModule;
import org.jclouds.blobstore.internal.BlobStoreContextImpl;
import org.jclouds.domain.Location;
import org.jclouds.rackspace.cloudfiles.CloudFilesAsyncClient;
import org.jclouds.rackspace.cloudfiles.CloudFilesClient;
import org.jclouds.rackspace.cloudfiles.blobstore.CloudFilesAsyncBlobStore;
import org.jclouds.rackspace.cloudfiles.blobstore.CloudFilesBlobRequestSigner;
import org.jclouds.rackspace.cloudfiles.blobstore.CloudFilesBlobStore;
import org.jclouds.rackspace.config.RackspaceLocationsModule;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Iterables;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * Configures the {@link CloudFilesBlobStoreContext}; requires {@link CloudFilesAsyncBlobStore}
 * bound.
 * 
 * @author Adrian Cole
 */
public class CloudFilesBlobStoreContextModule extends AbstractModule {

   @Override
   protected void configure() {
      install(new RackspaceLocationsModule());
      install(new BlobStoreMapModule());
      bind(ConsistencyModel.class).toInstance(ConsistencyModel.STRICT);
      bind(AsyncBlobStore.class).to(CloudFilesAsyncBlobStore.class).in(Scopes.SINGLETON);
      bind(BlobStore.class).to(CloudFilesBlobStore.class).in(Scopes.SINGLETON);
      bind(BlobStoreContext.class).to(new TypeLiteral<BlobStoreContextImpl<CloudFilesClient, CloudFilesAsyncClient>>() {
      }).in(Scopes.SINGLETON);
      bind(BlobRequestSigner.class).to(CloudFilesBlobRequestSigner.class);
   }

   @Provides
   @Singleton
   protected Supplier<Set<? extends Location>> getSourceLocationSupplier(Set<? extends Location> locations) {
      return Suppliers.<Set<? extends Location>> ofInstance(locations);
   }

   @Provides
   @Singleton
   protected Supplier<Location> getLocation(Set<? extends Location> locations) {
      return Suppliers.<Location> ofInstance(Iterables.get(locations, 0));
   }

}
