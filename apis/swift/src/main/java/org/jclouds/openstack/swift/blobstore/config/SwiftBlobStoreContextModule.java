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
package org.jclouds.openstack.swift.blobstore.config;

import com.google.common.base.Supplier;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.config.BlobStoreMapModule;
import org.jclouds.date.TimeStamp;
import org.jclouds.openstack.swift.TemporaryUrlKey;
import org.jclouds.openstack.swift.blobstore.SwiftAsyncBlobStore;
import org.jclouds.openstack.swift.blobstore.SwiftBlobRequestSigner;
import org.jclouds.openstack.swift.blobstore.SwiftBlobStore;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import org.jclouds.openstack.swift.extensions.TemporaryUrlKeyApi;
import org.jclouds.openstack.swift.extensions.TemporaryUrlKeyAsyncApi;
import org.jclouds.openstack.swift.suppliers.ReturnOrFetchTemporaryUrlKey;

import java.util.UUID;

import static org.jclouds.rest.config.BinderUtils.bindClientAndAsyncClient;

/**
 * Configures the {@link CloudFilesBlobStoreContext}; requires {@link SwiftAsyncBlobStore}
 * bound.
 *
 * @author Adrian Cole
 */
public class SwiftBlobStoreContextModule extends AbstractModule {

   @Provides
   @TimeStamp
   protected Long unixEpochTimestampProvider() {
      return System.currentTimeMillis() / 1000; /* convert to seconds */
   }

   @Override
   protected void configure() {
      install(new BlobStoreMapModule());
      bind(ConsistencyModel.class).toInstance(ConsistencyModel.STRICT);
      bind(AsyncBlobStore.class).to(SwiftAsyncBlobStore.class).in(Scopes.SINGLETON);
      bind(BlobStore.class).to(SwiftBlobStore.class).in(Scopes.SINGLETON);
      bind(BlobRequestSigner.class).to(SwiftBlobRequestSigner.class);
      configureTemporaryUrlExtension();
   }

   protected void configureTemporaryUrlExtension() {
      bindClientAndAsyncClient(binder(), TemporaryUrlKeyApi.class, TemporaryUrlKeyAsyncApi.class);
      bind(new TypeLiteral<Supplier<String>>() {
      }).annotatedWith(TemporaryUrlKey.class).to(ReturnOrFetchTemporaryUrlKey.class);
   }
}
