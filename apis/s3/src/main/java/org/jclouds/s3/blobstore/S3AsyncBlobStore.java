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
package org.jclouds.s3.blobstore;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.internal.PageSetImpl;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.blobstore.internal.BaseAsyncBlobStore;
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.blobstore.strategy.internal.FetchBlobMetadata;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.collect.Memoized;
import org.jclouds.concurrent.Futures;
import org.jclouds.domain.Location;
import org.jclouds.http.options.GetOptions;
import org.jclouds.s3.S3AsyncClient;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.blobstore.functions.BlobToObject;
import org.jclouds.s3.blobstore.functions.BucketToResourceList;
import org.jclouds.s3.blobstore.functions.BucketToResourceMetadata;
import org.jclouds.s3.blobstore.functions.ContainerToBucketListOptions;
import org.jclouds.s3.blobstore.functions.ObjectToBlob;
import org.jclouds.s3.blobstore.functions.ObjectToBlobMetadata;
import org.jclouds.s3.domain.AccessControlList;
import org.jclouds.s3.domain.AccessControlList.GroupGranteeURI;
import org.jclouds.s3.domain.AccessControlList.Permission;
import org.jclouds.s3.domain.BucketMetadata;
import org.jclouds.s3.domain.CannedAccessPolicy;
import org.jclouds.s3.domain.ListBucketResponse;
import org.jclouds.s3.domain.ObjectMetadata;
import org.jclouds.s3.options.ListBucketOptions;
import org.jclouds.s3.options.PutBucketOptions;
import org.jclouds.s3.options.PutObjectOptions;
import org.jclouds.s3.util.S3Utils;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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
   private final LoadingCache<String, AccessControlList> bucketAcls;

   @Inject
   protected S3AsyncBlobStore(BlobStoreContext context, BlobUtils blobUtils,
         @Named(Constants.PROPERTY_USER_THREADS) ExecutorService service, Supplier<Location> defaultLocation,
         @Memoized Supplier<Set<? extends Location>> locations, S3AsyncClient async, S3Client sync,
         BucketToResourceMetadata bucket2ResourceMd, ContainerToBucketListOptions container2BucketListOptions,
         BucketToResourceList bucket2ResourceList, ObjectToBlob object2Blob,
         BlobToHttpGetOptions blob2ObjectGetOptions, BlobToObject blob2Object, ObjectToBlobMetadata object2BlobMd,
         Provider<FetchBlobMetadata> fetchBlobMetadataProvider, LoadingCache<String, AccessControlList> bucketAcls) {
      super(context, blobUtils, service, defaultLocation, locations);
      this.blob2ObjectGetOptions = checkNotNull(blob2ObjectGetOptions, "blob2ObjectGetOptions");
      this.async = checkNotNull(async, "async");
      this.sync = checkNotNull(sync, "sync");
      this.bucket2ResourceMd = checkNotNull(bucket2ResourceMd, "bucket2ResourceMd");
      this.container2BucketListOptions = checkNotNull(container2BucketListOptions, "container2BucketListOptions");
      this.bucket2ResourceList = checkNotNull(bucket2ResourceList, "bucket2ResourceList");
      this.object2Blob = checkNotNull(object2Blob, "object2Blob");
      this.blob2Object = checkNotNull(blob2Object, "blob2Object");
      this.object2BlobMd = checkNotNull(object2BlobMd, "object2BlobMd");
      this.fetchBlobMetadataProvider = checkNotNull(fetchBlobMetadataProvider, "fetchBlobMetadataProvider");
      this.bucketAcls = checkNotNull(bucketAcls, "bucketAcls");
   }

   /**
    * This implementation invokes {@link S3AsyncClient#listOwnedBuckets}
    */
   @Override
   public ListenableFuture<PageSet<? extends StorageMetadata>> list() {
      return Futures.compose(async.listOwnedBuckets(),
            new Function<Set<BucketMetadata>, org.jclouds.blobstore.domain.PageSet<? extends StorageMetadata>>() {
               public org.jclouds.blobstore.domain.PageSet<? extends StorageMetadata> apply(Set<BucketMetadata> from) {
                  return new PageSetImpl<StorageMetadata>(Iterables.transform(from, bucket2ResourceMd), null);
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
    *           corresponds to Region
    * @param container
    *           bucket name
    */
   @Override
   public ListenableFuture<Boolean> createContainerInLocation(Location location, String container) {
      return createContainerInLocation(location, container, CreateContainerOptions.NONE);
   }

   /**
    * This implementation invokes {@link S3AsyncClient#listBucket}
    * 
    * @param container
    *           bucket name
    */
   @Override
   // TODO get rid of compose, as it serializes async results when the executor is single-threaded.
   public ListenableFuture<PageSet<? extends StorageMetadata>> list(String container, ListContainerOptions options) {
      ListBucketOptions httpOptions = container2BucketListOptions.apply(options);
      ListenableFuture<ListBucketResponse> returnVal = async.listBucket(container, httpOptions);
      ListenableFuture<PageSet<? extends StorageMetadata>> list = Futures.compose(returnVal, bucket2ResourceList,
            service);
      return (options.isDetailed()) ? Futures.compose(list,
            fetchBlobMetadataProvider.get().setContainerName(container), service) : list;
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
      return Futures.compose(async.headObject(container, key), new Function<ObjectMetadata, BlobMetadata>() {

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
   public ListenableFuture<Blob> getBlob(String container, String key, org.jclouds.blobstore.options.GetOptions options) {
      GetOptions httpOptions = blob2ObjectGetOptions.apply(options);
      return Futures.compose(async.getObject(container, key, httpOptions), object2Blob, service);
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
      return putBlob(container, blob, PutOptions.NONE);
   }

   @Override
   public ListenableFuture<String> putBlob(String container, Blob blob, PutOptions overrides) {
      // TODO: Make use of options overrides
      PutObjectOptions options = new PutObjectOptions();
      try {
         AccessControlList acl = bucketAcls.getUnchecked(container);
         if (acl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.READ))
            options.withAcl(CannedAccessPolicy.PUBLIC_READ);
      } catch (CacheLoader.InvalidCacheLoadException e) {
         // nulls not permitted from cache loader
      }
      return async.putObject(container, blob2Object.apply(blob), options);
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

   @Override
   public ListenableFuture<Boolean> createContainerInLocation(Location location, String container,
         CreateContainerOptions options) {
      PutBucketOptions putBucketOptions = new PutBucketOptions();
      if (options.isPublicRead())
         putBucketOptions.withBucketAcl(CannedAccessPolicy.PUBLIC_READ);
      location = location != null ? location : defaultLocation.get();
      return async.putBucketInRegion(location.getId(), container, putBucketOptions);
   }

}
