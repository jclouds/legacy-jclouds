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
package org.jclouds.blobstore.config;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.blobstore.TransientAsyncBlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.domain.Location;
import org.jclouds.util.Jsr330;

import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

public class TransientBlobStoreModule extends AbstractModule {

   // must be singleton for all threads and all objects or tests may fail;
   static final ConcurrentHashMap<String, ConcurrentMap<String, Blob>> map = new ConcurrentHashMap<String, ConcurrentMap<String, Blob>>();
   static final ConcurrentHashMap<String, Location> containerToLocation = new ConcurrentHashMap<String, Location>();

   @Override
   protected void configure() {
      bind(new TypeLiteral<ConcurrentMap<String, ConcurrentMap<String, Blob>>>() {
      }).toInstance(map);
      bind(new TypeLiteral<ConcurrentMap<String, Location>>() {
      }).toInstance(containerToLocation);
      bind(TransientAsyncBlobStore.class).in(Scopes.SINGLETON);
      bindConstant().annotatedWith(Jsr330.named(Constants.PROPERTY_USER_THREADS)).to(0);
      bindConstant().annotatedWith(Jsr330.named(Constants.PROPERTY_IO_WORKER_THREADS)).to(0);
   }

   @Provides
   @Singleton
   Set<Location> provideLocations(Location defaultLocation) {
      return ImmutableSet.of( defaultLocation);
   }
}