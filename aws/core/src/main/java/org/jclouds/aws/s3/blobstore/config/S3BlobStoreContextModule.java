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
package org.jclouds.aws.s3.blobstore.config;

import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.s3.S3;
import org.jclouds.aws.s3.S3AsyncClient;
import org.jclouds.aws.s3.S3Client;
import org.jclouds.aws.s3.blobstore.S3AsyncBlobStore;
import org.jclouds.aws.s3.blobstore.S3BlobStore;
import org.jclouds.aws.s3.config.S3ContextModule;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.config.BlobStoreMapModule;
import org.jclouds.blobstore.internal.BlobStoreContextImpl;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * Configures the {@link S3BlobStoreContext}; requires {@link S3AsyncBlobStore} bound.
 * 
 * @author Adrian Cole
 */
public class S3BlobStoreContextModule extends S3ContextModule {

   @Override
   protected void configure() {
      super.configure();
      install(new BlobStoreMapModule());
      bind(ConsistencyModel.class).toInstance(ConsistencyModel.EVENTUAL);
      bind(AsyncBlobStore.class).to(S3AsyncBlobStore.class).in(Scopes.SINGLETON);
      bind(BlobStore.class).to(S3BlobStore.class).in(Scopes.SINGLETON);
      bind(BlobStoreContext.class).to(
               new TypeLiteral<BlobStoreContextImpl<S3AsyncClient, S3Client>>() {
               }).in(Scopes.SINGLETON);
   }

   @Provides
   @Singleton
   Location getDefaultLocation(@S3 String region, Map<String, ? extends Location> locations) {
      return locations.get(region.toString());
   }

   @Provides
   @Singleton
   Map<String, ? extends Location> provideLocations(@S3 Set<String> regions) {
      Set<Location> locations = Sets.newHashSet();
      for (String zone : regions) {
         locations
                  .add(new LocationImpl(LocationScope.ZONE, zone.toString(), zone.toString(), null));
      }
      return Maps.uniqueIndex(locations, new Function<Location, String>() {
         @Override
         public String apply(Location from) {
            return from.getId();
         }
      });
   }
}
