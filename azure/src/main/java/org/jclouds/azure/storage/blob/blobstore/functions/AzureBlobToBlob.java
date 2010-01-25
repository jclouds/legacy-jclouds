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

import org.jclouds.azure.storage.blob.domain.AzureBlob;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.Blob.Factory;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class AzureBlobToBlob implements Function<AzureBlob, Blob> {
   private final Blob.Factory blobFactory;
   private final BlobPropertiesToBlobMetadata blobPr2BlobMd;

   @Inject
   AzureBlobToBlob(Factory blobFactory, BlobPropertiesToBlobMetadata blobPr2BlobMd) {
      this.blobFactory = blobFactory;
      this.blobPr2BlobMd = blobPr2BlobMd;
   }

   public Blob apply(AzureBlob from) {
      if (from == null)
         return null;
      Blob blob = blobFactory.create(blobPr2BlobMd.apply(from.getProperties()));
      if (from.getContentLength() != null)
         blob.setContentLength(from.getContentLength());
      if (from.getPayload() != null)
         blob.setPayload(from.getPayload());
      blob.setAllHeaders(from.getAllHeaders());
      return blob;
   }
}
