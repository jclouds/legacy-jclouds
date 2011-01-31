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

package org.jclouds.openstack.swift.blobstore.config;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.config.BlobStoreMapModule;
import org.jclouds.blobstore.internal.BlobStoreContextImpl;
import org.jclouds.location.config.JustProviderLocationModule;
import org.jclouds.openstack.swift.CommonSwiftAsyncClient;
import org.jclouds.openstack.swift.CommonSwiftClient;
import org.jclouds.openstack.swift.blobstore.SwiftAsyncBlobStore;
import org.jclouds.openstack.swift.blobstore.SwiftBlobRequestSigner;
import org.jclouds.openstack.swift.blobstore.SwiftBlobStore;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * Configures the {@link CloudFilesBlobStoreContext}; requires {@link SwiftAsyncBlobStore}
 * bound.
 * 
 * @author Adrian Cole
 */
public class SwiftBlobStoreContextModule extends AbstractModule {

   @Override
   protected void configure() {
      install(new BlobStoreMapModule());
      install(new JustProviderLocationModule());
      bind(ConsistencyModel.class).toInstance(ConsistencyModel.STRICT);
      bind(AsyncBlobStore.class).to(SwiftAsyncBlobStore.class).in(Scopes.SINGLETON);
      bind(BlobStore.class).to(SwiftBlobStore.class).in(Scopes.SINGLETON);
      bind(BlobStoreContext.class).to(new TypeLiteral<BlobStoreContextImpl<CommonSwiftClient, CommonSwiftAsyncClient>>() {
      }).in(Scopes.SINGLETON);
      bind(BlobRequestSigner.class).to(SwiftBlobRequestSigner.class);
   }


}
