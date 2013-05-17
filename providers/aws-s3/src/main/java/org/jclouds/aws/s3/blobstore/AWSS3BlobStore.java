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
package org.jclouds.aws.s3.blobstore;

import static org.jclouds.s3.domain.ObjectMetadata.StorageClass.REDUCED_REDUNDANCY;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jclouds.aws.s3.AWSS3ApiMetadata;
import org.jclouds.aws.s3.AWSS3Client;
import org.jclouds.aws.s3.blobstore.options.AWSS3PutObjectOptions;
import org.jclouds.aws.s3.blobstore.options.AWSS3PutOptions;
import org.jclouds.aws.s3.blobstore.strategy.MultipartUploadStrategy;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.blobstore.strategy.internal.FetchBlobMetadata;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.s3.blobstore.S3BlobStore;
import org.jclouds.s3.blobstore.functions.BlobToObject;
import org.jclouds.s3.blobstore.functions.BucketToResourceList;
import org.jclouds.s3.blobstore.functions.ContainerToBucketListOptions;
import org.jclouds.s3.blobstore.functions.ObjectToBlob;
import org.jclouds.s3.blobstore.functions.ObjectToBlobMetadata;
import org.jclouds.s3.domain.AccessControlList;
import org.jclouds.s3.domain.BucketMetadata;
import org.jclouds.s3.domain.CannedAccessPolicy;
import org.jclouds.s3.domain.ObjectMetadata;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Provide AWS S3 specific extensions.
 *
 * @author Tibor Kiss, Andrei Savu
 */
public class AWSS3BlobStore extends S3BlobStore {

   private final Provider<MultipartUploadStrategy> multipartUploadStrategy;
   private final LoadingCache<String, AccessControlList> bucketAcls;
   private final BlobToObject blob2Object;

   @Inject
   AWSS3BlobStore(BlobStoreContext context, BlobUtils blobUtils, Supplier<Location> defaultLocation,
            @Memoized Supplier<Set<? extends Location>> locations, AWSS3Client sync,
            Function<Set<BucketMetadata>, PageSet<? extends StorageMetadata>> convertBucketsToStorageMetadata,
            ContainerToBucketListOptions container2BucketListOptions, BucketToResourceList bucket2ResourceList,
            ObjectToBlob object2Blob, BlobToHttpGetOptions blob2ObjectGetOptions, BlobToObject blob2Object,
            ObjectToBlobMetadata object2BlobMd, Provider<FetchBlobMetadata> fetchBlobMetadataProvider,
            LoadingCache<String, AccessControlList> bucketAcls,
            Provider<MultipartUploadStrategy> multipartUploadStrategy) {
      super(context, blobUtils, defaultLocation, locations, sync, convertBucketsToStorageMetadata,
               container2BucketListOptions, bucket2ResourceList, object2Blob, blob2ObjectGetOptions, blob2Object,
               object2BlobMd, fetchBlobMetadataProvider, bucketAcls);
      this.multipartUploadStrategy = multipartUploadStrategy;
      this.bucketAcls = bucketAcls;
      this.blob2Object = blob2Object;
   }

   @Override
   public String putBlob(String container, Blob blob, PutOptions options) {
      if (options.isMultipart()) {
         // need to use a provider if the strategy object is stateful
         return multipartUploadStrategy.get().execute(container, blob);

      } else if ((options instanceof AWSS3PutOptions) &&
         (((AWSS3PutOptions) options).getStorageClass() == REDUCED_REDUNDANCY)) {
         return putBlobWithReducedRedundancy(container, blob);

      } else {
         return super.putBlob(container, blob, options);
      }
   }

   private String putBlobWithReducedRedundancy(String container, Blob blob) {
      AWSS3PutObjectOptions options = new AWSS3PutObjectOptions();
      try {
         AccessControlList acl = bucketAcls.getUnchecked(container);
         if (acl != null && acl.hasPermission(AccessControlList.GroupGranteeURI.ALL_USERS,
                                              AccessControlList.Permission.READ)) {
            options.withAcl(CannedAccessPolicy.PUBLIC_READ);
         }
         options.storageClass(ObjectMetadata.StorageClass.REDUCED_REDUNDANCY);

      } catch (CacheLoader.InvalidCacheLoadException e) {
         // nulls not permitted from cache loader
      }
      return getContext().unwrap(AWSS3ApiMetadata.CONTEXT_TOKEN).getApi().putObject(container, blob2Object.apply(blob),
               options);
   }
}
