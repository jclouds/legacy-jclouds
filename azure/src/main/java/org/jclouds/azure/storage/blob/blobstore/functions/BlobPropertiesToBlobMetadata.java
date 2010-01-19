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
package org.jclouds.azure.storage.blob.blobstore.functions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.azure.storage.blob.domain.BlobProperties;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.domain.internal.MutableBlobMetadataImpl;
import org.jclouds.blobstore.strategy.IsDirectoryStrategy;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class BlobPropertiesToBlobMetadata implements Function<BlobProperties, MutableBlobMetadata> {
   private final IsDirectoryStrategy isDirectoryStrategy;

   @Inject
   public BlobPropertiesToBlobMetadata(IsDirectoryStrategy isDirectoryStrategy) {
      this.isDirectoryStrategy = isDirectoryStrategy;
   }

   public MutableBlobMetadata apply(BlobProperties from) {
      MutableBlobMetadata to = new MutableBlobMetadataImpl();
      if (from.getContentMD5() != null)
         to.setContentMD5(from.getContentMD5());
      if (from.getContentType() != null)
         to.setContentType(from.getContentType());
      to.setUserMetadata(from.getMetadata());
      to.setETag(from.getETag());
      to.setLastModified(from.getLastModified());
      to.setName(from.getName());
      to.setSize(from.getContentLength());
      to.setType(StorageType.BLOB);
      if (isDirectoryStrategy.execute(to)) {
         to.setType(StorageType.RELATIVE_PATH);
      }
      return to;
   }
}