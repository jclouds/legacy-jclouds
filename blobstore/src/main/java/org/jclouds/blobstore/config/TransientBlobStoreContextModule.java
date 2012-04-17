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
package org.jclouds.blobstore.config;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.TransientAsyncBlobStore;
import org.jclouds.blobstore.TransientBlobRequestSigner;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.domain.Location;
import org.jclouds.rest.config.BinderUtils;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

/**
 * Configures the {@link TransientBlobStoreContext}; requires {@link TransientAsyncBlobStore} bound.
 * 
 * @author Adrian Cole
 */
public class TransientBlobStoreContextModule extends AbstractModule {

   // must be singleton for all threads and all objects or tests may fail;
   static final ConcurrentHashMap<String, ConcurrentMap<String, Blob>> map = new ConcurrentHashMap<String, ConcurrentMap<String, Blob>>();
   static final ConcurrentHashMap<String, Location> containerToLocation = new ConcurrentHashMap<String, Location>();

   @Override
   protected void configure() {
      bind(AsyncBlobStore.class).to(TransientAsyncBlobStore.class).asEagerSingleton();
      // forward all requests from TransientBlobStore to TransientAsyncBlobStore.  needs above binding as cannot proxy a class
      BinderUtils.bindClient(binder(), TransientBlobStore.class, AsyncBlobStore.class, ImmutableMap.<Class<?>, Class<?>>of());
      bind(new TypeLiteral<ConcurrentMap<String, ConcurrentMap<String, Blob>>>() {
      }).toInstance(map);
      bind(new TypeLiteral<ConcurrentMap<String, Location>>() {
      }).toInstance(containerToLocation);
      install(new BlobStoreObjectModule());
      install(new BlobStoreMapModule());
      bind(BlobStore.class).to(TransientBlobStore.class);
      bind(ConsistencyModel.class).toInstance(ConsistencyModel.STRICT);
      bind(BlobRequestSigner.class).to(TransientBlobRequestSigner.class);
   }

}
