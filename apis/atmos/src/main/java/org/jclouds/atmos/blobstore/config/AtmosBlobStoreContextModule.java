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
package org.jclouds.atmos.blobstore.config;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import org.jclouds.atmos.AtmosAsyncClient;
import org.jclouds.atmos.AtmosClient;
import org.jclouds.atmos.blobstore.AtmosAsyncBlobStore;
import org.jclouds.atmos.blobstore.AtmosBlobRequestSigner;
import org.jclouds.atmos.blobstore.AtmosBlobStore;
import org.jclouds.atmos.blobstore.strategy.FindMD5InUserMetadata;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.config.BlobStoreMapModule;
import org.jclouds.blobstore.internal.BlobStoreContextImpl;
import org.jclouds.blobstore.strategy.ContainsValueInListStrategy;
import org.jclouds.location.config.JustProviderLocationModule;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * Configures the {@link AtmosBlobStoreContext}; requires {@link AtmosAsyncBlobStore} bound.
 * 
 * @author Adrian Cole
 */
public class AtmosBlobStoreContextModule extends AbstractModule {

   @Override
   protected void configure() {
      install(new BlobStoreMapModule());
      bind(ConsistencyModel.class).toInstance(ConsistencyModel.EVENTUAL);
      bind(AsyncBlobStore.class).to(AtmosAsyncBlobStore.class).in(Scopes.SINGLETON);
      bind(BlobStore.class).to(AtmosBlobStore.class).in(Scopes.SINGLETON);
      bind(BlobStoreContext.class).to(new TypeLiteral<BlobStoreContextImpl<AtmosClient, AtmosAsyncClient>>() {
      }).in(Scopes.SINGLETON);
      bind(ContainsValueInListStrategy.class).to(FindMD5InUserMetadata.class);
      bind(BlobRequestSigner.class).to(AtmosBlobRequestSigner.class);
      install(new JustProviderLocationModule());
   }

   @Provides
   @Singleton
   protected Map<String, Boolean> isPublic(final AtmosClient client) {
      return new MapMaker().expireAfterWrite(30, TimeUnit.SECONDS).makeComputingMap(new Function<String, Boolean>() {
         public Boolean apply(String directory) {
            return client.isPublic(directory);
         }

         @Override
         public String toString() {
            return "isPublic()";
         }
      });
   }
}
