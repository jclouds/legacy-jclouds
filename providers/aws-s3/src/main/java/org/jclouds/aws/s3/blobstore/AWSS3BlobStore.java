/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jclouds.aws.s3.AWSS3Client;
import org.jclouds.aws.s3.blobstore.strategy.MultipartUploadStrategy;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.blobstore.strategy.internal.FetchBlobMetadata;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.s3.blobstore.S3BlobStore;
import org.jclouds.s3.blobstore.functions.BlobToObject;
import org.jclouds.s3.blobstore.functions.BucketToResourceList;
import org.jclouds.s3.blobstore.functions.BucketToResourceMetadata;
import org.jclouds.s3.blobstore.functions.ContainerToBucketListOptions;
import org.jclouds.s3.blobstore.functions.ObjectToBlob;
import org.jclouds.s3.blobstore.functions.ObjectToBlobMetadata;
import org.jclouds.s3.domain.AccessControlList;

import com.google.common.base.Supplier;

/**
 * Proived AWS S3 specific extensions.
 * 
 * @author Tibor Kiss
 */
public class AWSS3BlobStore extends S3BlobStore {

   private final Provider<MultipartUploadStrategy> multipartUploadStrategy;

   @Inject
   AWSS3BlobStore(BlobStoreContext context, BlobUtils blobUtils, Supplier<Location> defaultLocation,
            @Memoized Supplier<Set<? extends Location>> locations, AWSS3Client sync,
            BucketToResourceMetadata bucket2ResourceMd, ContainerToBucketListOptions container2BucketListOptions,
            BucketToResourceList bucket2ResourceList, ObjectToBlob object2Blob,
            BlobToHttpGetOptions blob2ObjectGetOptions, BlobToObject blob2Object, ObjectToBlobMetadata object2BlobMd,
            Provider<FetchBlobMetadata> fetchBlobMetadataProvider, Map<String, AccessControlList> bucketAcls,
            Provider<MultipartUploadStrategy> multipartUploadStrategy) {
      super(context, blobUtils, defaultLocation, locations, sync, bucket2ResourceMd, container2BucketListOptions,
               bucket2ResourceList, object2Blob, blob2ObjectGetOptions, blob2Object, object2BlobMd,
               fetchBlobMetadataProvider, bucketAcls);
      this.multipartUploadStrategy = multipartUploadStrategy;
   }

   @Override
   public String putBlob(String container, Blob blob, PutOptions options) {
    // need to use a provider if the strategy object is stateful
      return multipartUploadStrategy.get().execute(container, blob);
   }
}
