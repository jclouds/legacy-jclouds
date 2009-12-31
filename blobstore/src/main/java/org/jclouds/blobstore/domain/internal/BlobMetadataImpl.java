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
package org.jclouds.blobstore.domain.internal;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;
import java.util.Map;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ResourceType;

/**
 * System and user Metadata for the {@link Blob}.
 * 
 * @author Adrian Cole
 */
public class BlobMetadataImpl extends ResourceMetadataImpl implements Serializable, BlobMetadata {
   /** The serialVersionUID */
   private static final long serialVersionUID = -5932618957134612231L;

   private final String contentType;
   private final byte[] contentMD5;

   public BlobMetadataImpl(String id, String name, URI location, String eTag, Long size,
            Date lastModified, Map<String, String> userMetadata, String contentType,
            byte[] contentMD5) {
      super(ResourceType.BLOB, id, name, location, eTag, size, lastModified, userMetadata);
      this.contentType = contentType;
      this.contentMD5 = contentMD5;
   }

   public String getContentType() {
      return contentType;
   }

   public byte[] getContentMD5() {
      if (contentMD5 != null) {
         byte[] retval = new byte[contentMD5.length];
         System.arraycopy(this.contentMD5, 0, retval, 0, contentMD5.length);
         return retval;
      } else {
         return null;
      }
   }

   public int compareTo(BlobMetadata o) {
      if (getName() == null)
         return -1;
      return (this == o) ? 0 : getName().compareTo(o.getName());
   }
}