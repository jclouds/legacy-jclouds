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

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.config.BlobStoreMapModule;
import org.jclouds.blobstore.internal.BlobStoreContextImpl;
import org.jclouds.domain.Location;
import org.jclouds.location.config.RegionsLocationModule;
import org.jclouds.s3.S3AsyncClient;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.blobstore.S3AsyncBlobStore;
import org.jclouds.s3.blobstore.S3BlobRequestSigner;
import org.jclouds.s3.blobstore.S3BlobStore;
import org.jclouds.s3.blobstore.functions.LocationFromBucketLocation;
import org.jclouds.s3.domain.BucketMetadata;

import com.google.common.base.Function;
import com.google.inject.AbstractModule;
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
      install(new RegionsLocationModule());
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

   
}
