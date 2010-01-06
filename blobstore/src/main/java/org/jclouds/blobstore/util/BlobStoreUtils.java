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
package org.jclouds.blobstore.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ResourceMetadata;
import org.jclouds.util.Utils;

/**
 * Encryption, Hashing, and IO Utilities needed to sign and verify S3 requests and responses.
 * 
 * @author Adrian Cole
 */
public class BlobStoreUtils {

   public static Blob newBlob(BlobStore blobStore, ResourceMetadata blobMeta) {
      Blob blob = blobStore.newBlob(blobMeta.getName());
      if (blobMeta instanceof BlobMetadata) {
         blob.getMetadata().setContentMD5(((BlobMetadata) blobMeta).getContentMD5());
         blob.getMetadata().setContentType(((BlobMetadata) blobMeta).getContentType());
      }
      blob.getMetadata().setETag(blobMeta.getETag());
      blob.getMetadata().setId(blobMeta.getId());
      blob.getMetadata().setLastModified(blobMeta.getLastModified());
      blob.getMetadata().setLocation(blobMeta.getLocation());
      blob.getMetadata().setUserMetadata(blobMeta.getUserMetadata());
      return blob;
   }

   public static String parseContainerFromPath(String path) {
      String container = path;
      if (path.indexOf('/') != -1)
         container = path.substring(0, path.indexOf('/'));
      return container;
   }

   public static String parsePrefixFromPath(String path) {
      String prefix = null;
      if (path.indexOf('/') != -1)
         prefix = path.substring(path.indexOf('/') + 1);
      return "".equals(prefix) ? null : prefix;
   }

   public static String getContentAsStringAndClose(Blob blob) throws IOException {
      checkNotNull(blob, "blob");
      checkNotNull(blob.getContent(), "blob.data");
      Object o = blob.getContent();
      if (o instanceof InputStream) {
         return Utils.toStringAndClose((InputStream) o);
      } else {
         throw new IllegalArgumentException("Object type not supported: " + o.getClass().getName());
      }
   }
}