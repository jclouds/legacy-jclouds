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
package org.jclouds.blobstore.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;
import java.util.Map;

import javax.annotation.Nullable;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.domain.Location;
import org.jclouds.io.ContentMetadata;

/**
 * System and user Metadata for the {@link Blob}.
 * 
 * @author Adrian Cole
 */
public class BlobMetadataImpl extends StorageMetadataImpl implements Serializable, BlobMetadata {
   /** The serialVersionUID */
   private static final long serialVersionUID = -5932618957134612231L;
   private final ContentMetadata contentMetadata;

   public BlobMetadataImpl(String id, String name, @Nullable Location location, URI uri, String eTag,
            Date lastModified, Map<String, String> userMetadata, ContentMetadata contentMetadata) {
      super(StorageType.BLOB, id, name, location, uri, eTag, lastModified, userMetadata);
      this.contentMetadata = checkNotNull(contentMetadata, "contentMetadata");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ContentMetadata getContentMetadata() {
      return contentMetadata;
   }

}