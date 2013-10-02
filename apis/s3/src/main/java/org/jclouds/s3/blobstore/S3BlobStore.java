/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.s3.blobstore;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.util.Predicates2.retry;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.blobstore.internal.BaseBlobStore;
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.blobstore.strategy.internal.FetchBlobMetadata;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.http.options.GetOptions;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.blobstore.functions.BlobToObject;
import org.jclouds.s3.blobstore.functions.BucketToResourceList;
import org.jclouds.s3.blobstore.functions.ContainerToBucketListOptions;
import org.jclouds.s3.blobstore.functions.ObjectToBlob;
import org.jclouds.s3.blobstore.functions.ObjectToBlobMetadata;
import org.jclouds.s3.domain.AccessControlList;
import org.jclouds.s3.domain.AccessControlList.GroupGranteeURI;
import org.jclouds.s3.domain.AccessControlList.Permission;
import org.jclouds.s3.domain.BucketMetadata;
import org.jclouds.s3.domain.CannedAccessPolicy;
import org.jclouds.s3.options.ListBucketOptions;
import org.jclouds.s3.options.PutBucketOptions;
import org.jclouds.s3.options.PutObjectOptions;
import org.jclouds.s3.util.S3Utils;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class S3BlobStore extends BaseBlobStore {
   private final S3Client sync;
   private final Function<Set<BucketMetadata>, PageSet<? extends StorageMetadata>> convertBucketsToStorageMetadata;
   private final ContainerToBucketListOptions container2BucketListOptions;
   private final BucketToResourceList bucket2ResourceList;
   private final ObjectToBlob object2Blob;
   private final BlobToObject blob2Object;
   private final ObjectToBlobMetadata object2BlobMd;
   private final BlobToHttpGetOptions blob2ObjectGetOptions;
   private final Provider<FetchBlobMetadata> fetchBlobMetadataProvider;
   private final LoadingCache<String, AccessControlList> bucketAcls;

   @Inject
   protected S3BlobStore(BlobStoreContext context, BlobUtils blobUtils, Supplier<Location> defaultLocation,
            @Memoized Supplier<Set<? extends Location>> locations, S3Client sync,
            Function<Set<BucketMetadata>, PageSet<? extends StorageMetadata>> convertBucketsToStorageMetadata,
            ContainerToBucketListOptions container2BucketListOptions, BucketToResourceList bucket2ResourceList,
            ObjectToBlob object2Blob, BlobToHttpGetOptions blob2ObjectGetOptions, BlobToObject blob2Object,
            ObjectToBlobMetadata object2BlobMd, Provider<FetchBlobMetadata> fetchBlobMetadataProvider,
            LoadingCache<String, AccessControlList> bucketAcls) {
      super(context, blobUtils, defaultLocation, locations);
      this.blob2ObjectGetOptions = checkNotNull(blob2ObjectGetOptions, "blob2ObjectGetOptions");
      this.sync = checkNotNull(sync, "sync");
      this.convertBucketsToStorageMetadata = checkNotNull(convertBucketsToStorageMetadata, "convertBucketsToStorageMetadata");
      this.container2BucketListOptions = checkNotNull(container2BucketListOptions, "container2BucketListOptions");
      this.bucket2ResourceList = checkNotNull(bucket2ResourceList, "bucket2ResourceList");
      this.object2Blob = checkNotNull(object2Blob, "object2Blob");
      this.blob2Object = checkNotNull(blob2Object, "blob2Object");
      this.object2BlobMd = checkNotNull(object2BlobMd, "object2BlobMd");
      this.fetchBlobMetadataProvider = checkNotNull(fetchBlobMetadataProvider, "fetchBlobMetadataProvider");
      this.bucketAcls = checkNotNull(bucketAcls, "bucketAcls");
   }

   /**
    * This implementation invokes {@link S3Client#listOwnedBuckets}
    */
   @Override
   public PageSet<? extends StorageMetadata> list() {
      return convertBucketsToStorageMetadata.apply(sync.listOwnedBuckets());
   }

   /**
    * This implementation invokes {@link S3Client#bucketExists}
    * 
    * @param container
    *           bucket name
    */
   @Override
   public boolean containerExists(String container) {
      return sync.bucketExists(container);
   }

   /**
    * This implementation invokes {@link S3Client#putBucketInRegion}
    * 
    * @param location
    *           corresponds to a Region
    * @param container
    *           bucket name
    */
   @Override
   public boolean createContainerInLocation(Location location, String container) {
      return createContainerInLocation(location, container, CreateContainerOptions.NONE);
   }

   /**
    * This implementation invokes {@link S3Client#listBucket}
    * 
    * @param container
    *           bucket name
    */
   @Override
   public PageSet<? extends StorageMetadata> list(String container, ListContainerOptions options) {
      ListBucketOptions httpOptions = container2BucketListOptions.apply(options);
      PageSet<? extends StorageMetadata> list = bucket2ResourceList.apply(sync.listBucket(container, httpOptions));
      return options.isDetailed() ? fetchBlobMetadataProvider.get().setContainerName(container).apply(list) : list;
   }

   /**
    * This implementation invokes {@link #clearContainer} then {@link S3Client#deleteBucketIfEmpty} until it is true.
    */
   @Override
   protected void deletePathAndEnsureGone(String path) {
      checkState(retry(new Predicate<String>() {
         public boolean apply(String in) {
            try {
               clearContainer(in);
               return sync.deleteBucketIfEmpty(in);
            } catch (ContainerNotFoundException e) {
               return true;
            }
         }
      }, 30000).apply(path), "%s still exists after deleting!", path);
   }

   /**
    * This implementation invokes {@link S3Client#objectExists}
    * 
    * @param container
    *           bucket name
    * @param key
    *           object key
    */
   @Override
   public boolean blobExists(String container, String key) {
      return sync.objectExists(container, key);
   }

   /**
    * This implementation invokes {@link S3Client#headObject}
    * 
    * @param container
    *           bucket name
    * @param key
    *           object key
    */
   @Override
   public BlobMetadata blobMetadata(String container, String key) {
      return object2BlobMd.apply(sync.headObject(container, key));
   }

   /**
    * This implementation invokes {@link S3Client#getObject}
    * 
    * @param container
    *           bucket name
    * @param key
    *           object key
    */
   @Override
   public Blob getBlob(String container, String key, org.jclouds.blobstore.options.GetOptions optionsList) {
      GetOptions httpOptions = blob2ObjectGetOptions.apply(optionsList);
      return object2Blob.apply(sync.getObject(container, key, httpOptions));
   }

   /**
    * This implementation invokes {@link S3Client#putObject}
    * 
    * @param container
    *           bucket name
    * @param blob
    *           object
    */
   @Override
   public String putBlob(String container, Blob blob) {
      return putBlob(container, blob, PutOptions.NONE);
   }

   /**
    * This implementation invokes {@link S3Client#putObject}
    * 
    * @param container
    *           bucket name
    * @param blob
    *           object
    */
   @Override
   public String putBlob(String container, Blob blob, PutOptions overrides) {
      // TODO: Make use of options overrides
      PutObjectOptions options = new PutObjectOptions();
      try {
         AccessControlList acl = bucketAcls.getUnchecked(container);
         if (acl != null && acl.hasPermission(GroupGranteeURI.ALL_USERS, Permission.READ))
            options.withAcl(CannedAccessPolicy.PUBLIC_READ);
      } catch (CacheLoader.InvalidCacheLoadException e) {
         // nulls not permitted from cache loader
      }
      return sync.putObject(container, blob2Object.apply(blob), options);
   }

   /**
    * This implementation invokes {@link S3Client#deleteObject}
    * 
    * @param container
    *           bucket name
    * @param key
    *           object key
    */
   @Override
   public void removeBlob(String container, String key) {
      sync.deleteObject(container, key);
   }

   /**
    * This implementation invokes {@link S3Utils#deleteAndVerifyContainerGone}
    */
   protected boolean deleteAndVerifyContainerGone(final String container) {
      return S3Utils.deleteAndVerifyContainerGone(sync, container);
   }

   @Override
   public boolean createContainerInLocation(Location location, String container, CreateContainerOptions options) {
      PutBucketOptions putBucketOptions = new PutBucketOptions();
      if (options.isPublicRead())
         putBucketOptions.withBucketAcl(CannedAccessPolicy.PUBLIC_READ);
      location = location != null ? location : defaultLocation.get();
      return sync.putBucketInRegion(location.getId(), container, putBucketOptions);
   }
}
