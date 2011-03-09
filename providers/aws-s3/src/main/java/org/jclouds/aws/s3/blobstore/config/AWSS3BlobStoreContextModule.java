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

package org.jclouds.aws.s3.blobstore.config;

import org.jclouds.aws.s3.AWSS3AsyncClient;
import org.jclouds.aws.s3.AWSS3Client;
import org.jclouds.aws.s3.blobstore.AWSS3AsyncBlobStore;
import org.jclouds.aws.s3.blobstore.AWSS3BlobStore;
import org.jclouds.aws.s3.blobstore.strategy.MultipartUploadStrategy;
import org.jclouds.aws.s3.blobstore.strategy.internal.SequentialMultipartUploadStrategy;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.internal.BlobStoreContextImpl;
import org.jclouds.s3.blobstore.S3AsyncBlobStore;
import org.jclouds.s3.blobstore.S3BlobStore;
import org.jclouds.s3.blobstore.config.S3BlobStoreContextModule;

import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * 
 * 
 * @author Tibor Kiss
 */
public class AWSS3BlobStoreContextModule extends S3BlobStoreContextModule {

   @Override
   protected void configure() {
      super.configure();
      bind(S3AsyncBlobStore.class).to(AWSS3AsyncBlobStore.class).in(Scopes.SINGLETON);
      bind(S3BlobStore.class).to(AWSS3BlobStore.class).in(Scopes.SINGLETON);
      bind(MultipartUploadStrategy.class).to(SequentialMultipartUploadStrategy.class);
   }

   @Override
   protected void bindContext() {
      bind(BlobStoreContext.class).to(new TypeLiteral<BlobStoreContextImpl<AWSS3Client, AWSS3AsyncClient>>() {
      }).in(Scopes.SINGLETON);
   }

}
