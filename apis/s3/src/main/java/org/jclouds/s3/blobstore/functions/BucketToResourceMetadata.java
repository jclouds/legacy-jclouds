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
package org.jclouds.s3.blobstore.functions;


import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.s3.domain.BucketMetadata;
import org.jclouds.blobstore.domain.MutableStorageMetadata;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.domain.internal.MutableStorageMetadataImpl;
import org.jclouds.domain.Location;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class BucketToResourceMetadata implements Function<BucketMetadata, StorageMetadata> {
   private final Function<BucketMetadata, Location> locationOfBucket;

   @Inject
   BucketToResourceMetadata(Function<BucketMetadata, Location> locationOfBucket) {
      this.locationOfBucket = locationOfBucket;
   }

   public StorageMetadata apply(BucketMetadata from) {
      MutableStorageMetadata to = new MutableStorageMetadataImpl();
      to.setName(from.getName());
      to.setType(StorageType.CONTAINER);
      to.setLocation(locationOfBucket.apply(from));
      return to;
   }
}
