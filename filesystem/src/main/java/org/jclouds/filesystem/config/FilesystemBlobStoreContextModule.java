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

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.config.BlobStoreMapModule;
import org.jclouds.blobstore.config.BlobStoreObjectModule;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.internal.BlobStoreContextImpl;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.filesystem.utils.FileSystemBlobUtilsImpl;
import org.jclouds.filesystem.utils.FilesystemStorageStrategy;
import org.jclouds.filesystem.utils.FilesystemStorageStrategyImpl;

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
   }

 /*  @Provides
   @Singleton
   Set<Location> provideLocations(Location defaultLocation) {
      return ImmutableSet.of(defaultLocation);
   }
  *
  */

   @Provides
   @Singleton
   BlobStore provide(FilesystemBlobStore in) {
      return in;
   }

   /*@Provides
   @Singleton
   Location provideDefaultLocation() {
      return new LocationImpl(LocationScope.PROVIDER, "filesystem", "filesystem", null);
   }
    * 
    */


   @Provides
   @Singleton
   Supplier<Set<? extends Location>> provideLocations(Supplier<Location> defaultLocation) {
      return Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet.of(defaultLocation.get()));
   }

   @Provides
   @Singleton
   Supplier<Location> provideDefaultLocation() {
      return Suppliers
               .<Location> ofInstance(new LocationImpl(LocationScope.PROVIDER, "filesystem", "filesystem", null));
   }

}
