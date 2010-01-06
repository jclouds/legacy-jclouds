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
package org.jclouds.aws.s3.blobstore.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;

import org.jclouds.aws.s3.S3AsyncClient;
import org.jclouds.aws.s3.S3Client;
import org.jclouds.aws.s3.blobstore.functions.BlobToObject;
import org.jclouds.aws.s3.blobstore.functions.BlobToObjectGetOptions;
import org.jclouds.aws.s3.blobstore.functions.BucketToResourceList;
import org.jclouds.aws.s3.blobstore.functions.BucketToResourceMetadata;
import org.jclouds.aws.s3.blobstore.functions.ContainerToBucketListOptions;
import org.jclouds.aws.s3.blobstore.functions.ObjectToBlob;
import org.jclouds.aws.s3.blobstore.functions.ObjectToBlobMetadata;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.strategy.ClearListStrategy;
import org.jclouds.blobstore.strategy.GetDirectoryStrategy;
import org.jclouds.blobstore.strategy.MkdirStrategy;
import org.jclouds.concurrent.FutureFunctionWrapper;
import org.jclouds.logging.Logger.LoggerFactory;

import com.google.common.base.Function;

public class BaseS3BlobStore {
   protected final S3AsyncClient async;
   protected final S3Client sync;
   protected final Blob.Factory blobFactory;
   protected final LoggerFactory logFactory;
   protected final ClearListStrategy clearContainerStrategy;
   protected final ObjectToBlobMetadata object2BlobMd;
   protected final ObjectToBlob object2Blob;
   protected final BlobToObject blob2Object;
   protected final ContainerToBucketListOptions container2BucketListOptions;
   protected final BlobToObjectGetOptions blob2ObjectGetOptions;
   protected final BucketToResourceMetadata bucket2ResourceMd;
   protected final BucketToResourceList bucket2ResourceList;
   protected final ExecutorService service;
   protected final GetDirectoryStrategy getDirectoryStrategy;
   protected final MkdirStrategy mkdirStrategy;

   @Inject
   protected BaseS3BlobStore(S3AsyncClient async, S3Client sync, Blob.Factory blobFactory,
            LoggerFactory logFactory, ClearListStrategy clearContainerStrategy,
            ObjectToBlobMetadata object2BlobMd, ObjectToBlob object2Blob, BlobToObject blob2Object,
            ContainerToBucketListOptions container2BucketListOptions,
            BlobToObjectGetOptions blob2ObjectGetOptions,
            GetDirectoryStrategy getDirectoryStrategy, MkdirStrategy mkdirStrategy,
            BucketToResourceMetadata bucket2ResourceMd, BucketToResourceList bucket2ResourceList,
            ExecutorService service) {
      this.async = checkNotNull(async, "async");
      this.sync = checkNotNull(sync, "sync");
      this.blobFactory = checkNotNull(blobFactory, "blobFactory");
      this.logFactory = checkNotNull(logFactory, "logFactory");
      this.clearContainerStrategy = checkNotNull(clearContainerStrategy, "clearContainerStrategy");
      this.object2BlobMd = checkNotNull(object2BlobMd, "object2BlobMd");
      this.object2Blob = checkNotNull(object2Blob, "object2Blob");
      this.blob2Object = checkNotNull(blob2Object, "blob2Object");
      this.container2BucketListOptions = checkNotNull(container2BucketListOptions,
               "container2BucketListOptions");
      this.blob2ObjectGetOptions = checkNotNull(blob2ObjectGetOptions, "blob2ObjectGetOptions");
      this.getDirectoryStrategy = checkNotNull(getDirectoryStrategy,
               "getDirectoryStrategy");
      this.mkdirStrategy = checkNotNull(mkdirStrategy, "mkdirStrategy");
      this.bucket2ResourceMd = checkNotNull(bucket2ResourceMd, "bucket2ResourceMd");
      this.bucket2ResourceList = checkNotNull(bucket2ResourceList, "bucket2ResourceList");
      this.service = checkNotNull(service, "service");
   }

   protected <F, T> Future<T> wrapFuture(Future<? extends F> future, Function<F, T> function) {
      return new FutureFunctionWrapper<F, T>(future, function, logFactory.getLogger(function
               .getClass().getName()));
   }

   public Blob newBlob(String name) {
      Blob blob = blobFactory.create(null);
      blob.getMetadata().setName(name);
      return blob;
   }

}
