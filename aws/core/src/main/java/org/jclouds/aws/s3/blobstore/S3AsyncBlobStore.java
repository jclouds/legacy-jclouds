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
package org.jclouds.aws.s3.blobstore;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.Futures.compose;

import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.aws.domain.Region;
import org.jclouds.aws.s3.S3AsyncClient;
import org.jclouds.aws.s3.S3Client;
import org.jclouds.aws.s3.blobstore.functions.BlobToObject;
import org.jclouds.aws.s3.blobstore.functions.BucketToResourceList;
import org.jclouds.aws.s3.blobstore.functions.BucketToResourceMetadata;
import org.jclouds.aws.s3.blobstore.functions.ContainerToBucketListOptions;
import org.jclouds.aws.s3.blobstore.functions.ObjectToBlob;
import org.jclouds.aws.s3.blobstore.functions.ObjectToBlobMetadata;
import org.jclouds.aws.s3.domain.BucketMetadata;
import org.jclouds.aws.s3.domain.ListBucketResponse;
import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.aws.s3.options.ListBucketOptions;
import org.jclouds.aws.s3.util.S3Utils;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.internal.PageSetImpl;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.blobstore.internal.BaseAsyncBlobStore;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.internal.FetchBlobMetadata;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.domain.Location;
import org.jclouds.http.options.GetOptions;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class S3AsyncBlobStore extends BaseAsyncBlobStore {

   private final S3AsyncClient async;
   private final S3Client sync;
   private final BucketToResourceMetadata bucket2ResourceMd;
   private final ContainerToBucketListOptions container2BucketListOptions;
   private final BlobToHttpGetOptions blob2ObjectGetOptions;
   private final BucketToResourceList bucket2ResourceList;
   private final ObjectToBlob object2Blob;
   private final BlobToObject blob2Object;
   private final ObjectToBlobMetadata object2BlobMd;
   private final Provider<FetchBlobMetadata> fetchBlobMetadataProvider;

   @Inject
   S3AsyncBlobStore(BlobStoreContext context, BlobUtils blobUtils,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService service,
            Location defaultLocation, Set<? extends Location> locations, S3AsyncClient async,
            S3Client sync, BucketToResourceMetadata bucket2ResourceMd,
            ContainerToBucketListOptions container2BucketListOptions,
            BucketToResourceList bucket2ResourceList, ObjectToBlob object2Blob,
            BlobToHttpGetOptions blob2ObjectGetOptions, BlobToObject blob2Object,
            ObjectToBlobMetadata object2BlobMd,
            Provider<FetchBlobMetadata> fetchBlobMetadataProvider) {
      super(context, blobUtils, service, defaultLocation, locations);
      this.blob2ObjectGetOptions = checkNotNull(blob2ObjectGetOptions, "blob2ObjectGetOptions");
      this.async = checkNotNull(async, "async");
      this.sync = checkNotNull(sync, "sync");
      this.bucket2ResourceMd = checkNotNull(bucket2ResourceMd, "bucket2ResourceMd");
      this.container2BucketListOptions = checkNotNull(container2BucketListOptions,
               "container2BucketListOptions");
      this.bucket2ResourceList = checkNotNull(bucket2ResourceList, "bucket2ResourceList");
      this.object2Blob = checkNotNull(object2Blob, "object2Blob");
      this.blob2Object = checkNotNull(blob2Object, "blob2Object");
      this.object2BlobMd = checkNotNull(object2BlobMd, "object2BlobMd");
      this.fetchBlobMetadataProvider = checkNotNull(fetchBlobMetadataProvider,
               "fetchBlobMetadataProvider");
   }

   /**
    * This implementation invokes {@link S3AsyncClient#listOwnedBuckets}
    */
   @Override
   public ListenableFuture<? extends PageSet<? extends StorageMetadata>> list() {
      return compose(
               async.listOwnedBuckets(),
               new Function<SortedSet<BucketMetadata>, org.jclouds.blobstore.domain.PageSet<? extends StorageMetadata>>() {
                  public org.jclouds.blobstore.domain.PageSet<? extends StorageMetadata> apply(
                           SortedSet<BucketMetadata> from) {
                     return new PageSetImpl<StorageMetadata>(Iterables.transform(from,
                              bucket2ResourceMd), null);
                  }
               }, service);
   }

   /**
    * This implementation invokes {@link S3AsyncClient#bucketExists}
    * 
    * @param container
    *           bucket name
    */
   @Override
   public ListenableFuture<Boolean> containerExists(String container) {
      return async.bucketExists(container);
   }

   /**
    * This implementation invokes {@link S3AsyncClient#putBucketInRegion}
    * 
    * @param location
    *           corresponds to {@link Region}
    * @param container
    *           bucket name
    */
   @Override
   public ListenableFuture<Boolean> createContainerInLocation(Location location, String container) {
      location = location != null ? location : defaultLocation;
      return async.putBucketInRegion(location.getId(), container);
   }

   /**
    * This implementation invokes {@link S3AsyncClient#listBucket}
    * 
    * @param container
    *           bucket name
    */
   @Override
   public ListenableFuture<? extends PageSet<? extends StorageMetadata>> list(String container,
            ListContainerOptions options) {
      ListBucketOptions httpOptions = container2BucketListOptions.apply(options);
      ListenableFuture<ListBucketResponse> returnVal = async.listBucket(container, httpOptions);
      ListenableFuture<PageSet<? extends StorageMetadata>> list = compose(returnVal,
               bucket2ResourceList, service);
      return options.isDetailed() ? compose(list, fetchBlobMetadataProvider.get().setContainerName(
               container), service) : list;
   }

   /**
    * This implementation invokes {@link S3Utils#deleteAndVerifyContainerGone}
    */
   protected boolean deleteAndVerifyContainerGone(final String container) {
      return S3Utils.deleteAndVerifyContainerGone(sync, container);
   }

   /**
    * This implementation invokes {@link S3AsyncClient#objectExists}
    * 
    * @param container
    *           bucket name
    * @param key
    *           object key
    */
   @Override
   public ListenableFuture<Boolean> blobExists(String container, String key) {
      return async.objectExists(container, key);
   }

   /**
    * This implementation invokes {@link S3AsyncClient#headObject}
    * 
    * @param container
    *           bucket name
    * @param key
    *           object key
    */
   @Override
   public ListenableFuture<BlobMetadata> blobMetadata(String container, String key) {
      return compose(async.headObject(container, key),
               new Function<ObjectMetadata, BlobMetadata>() {

                  @Override
                  public BlobMetadata apply(ObjectMetadata from) {
                     return object2BlobMd.apply(from);
                  }

               }, service);
   }

   /**
    * This implementation invokes {@link S3AsyncClient#getObject}
    * 
    * @param container
    *           bucket name
    * @param key
    *           object key
    */
   @Override
   public ListenableFuture<Blob> getBlob(String container, String key,
            org.jclouds.blobstore.options.GetOptions options) {
      GetOptions httpOptions = blob2ObjectGetOptions.apply(options);
      return compose(async.getObject(container, key, httpOptions), object2Blob, service);
   }

   /**
    * This implementation invokes {@link S3AsyncClient#putObject}
    * 
    * @param container
    *           bucket name
    * @param blob
    *           object
    */
   @Override
   public ListenableFuture<String> putBlob(String container, Blob blob) {
      return async.putObject(container, blob2Object.apply(blob));
   }

   /**
    * This implementation invokes {@link S3AsyncClient#deleteObject}
    * 
    * @param container
    *           bucket name
    * @param key
    *           object key
    */
   @Override
   public ListenableFuture<Void> removeBlob(String container, String key) {
      return async.deleteObject(container, key);
   }

}
