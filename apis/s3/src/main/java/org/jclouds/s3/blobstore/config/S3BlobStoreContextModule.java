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

package org.jclouds.s3.blobstore.config;

import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.s3.S3AsyncClient;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.blobstore.S3AsyncBlobStore;
import org.jclouds.s3.blobstore.S3BlobRequestSigner;
import org.jclouds.s3.blobstore.S3BlobStore;
import org.jclouds.s3.blobstore.functions.LocationFromBucketLocation;
import org.jclouds.s3.domain.BucketMetadata;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.config.BlobStoreMapModule;
import org.jclouds.blobstore.internal.BlobStoreContextImpl;
import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.location.Provider;
import org.jclouds.location.Region;
import org.jclouds.location.suppliers.OnlyLocationOrFirstZoneOrRegionMatchingRegionId;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * Configures the {@link S3BlobStoreContext}; requires {@link S3AsyncBlobStore} bound.
 * 
 * @author Adrian Cole
 */
public class S3BlobStoreContextModule extends AbstractModule {

   @Override
   protected void configure() {
      install(new BlobStoreMapModule());
      bind(new TypeLiteral<Supplier<Location>>() {
      }).to(new TypeLiteral<OnlyLocationOrFirstZoneOrRegionMatchingRegionId>() {
      });
      bind(ConsistencyModel.class).toInstance(ConsistencyModel.EVENTUAL);
      bind(AsyncBlobStore.class).to(S3AsyncBlobStore.class).in(Scopes.SINGLETON);
      bind(BlobStore.class).to(S3BlobStore.class).in(Scopes.SINGLETON);
      bind(BlobStoreContext.class).to(new TypeLiteral<BlobStoreContextImpl<S3Client, S3AsyncClient>>() {
      }).in(Scopes.SINGLETON);
      bind(BlobRequestSigner.class).to(S3BlobRequestSigner.class);
      bindBucketLocationStrategy();
   }

   protected void bindBucketLocationStrategy() {
      bind(new TypeLiteral<Function<BucketMetadata, Location>>() {
      }).to(LocationFromBucketLocation.class);
   }

   @Provides
   @Singleton
   @Memoized
   Supplier<Set<? extends Location>> provideLocations(@Region Set<String> regions, @Provider String providerName) {
      Set<Location> locations = Sets.newHashSet();
      Location s3 = new LocationImpl(LocationScope.PROVIDER, providerName, providerName, null);
      for (String zone : regions) {
         locations.add(new LocationImpl(LocationScope.REGION, zone.toString(), zone.toString(), s3));
      }
      return Suppliers.<Set<? extends Location>> ofInstance(locations);
   }
}
