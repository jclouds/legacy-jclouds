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

import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.domain.internal.ResourceMetadataImpl;

import com.google.inject.internal.Nullable;

/**
 * Idpayload of the object
 * 
 * @author Adrian Cole
 */
public class StorageMetadataImpl extends ResourceMetadataImpl<StorageType> implements
         StorageMetadata, Serializable {

   /** The serialVersionUID */
   private static final long serialVersionUID = -280558162576368264L;

   @Nullable
   private final String eTag;
   @Nullable
   private final Long size;
   @Nullable
   private final Date lastModified;

   public StorageMetadataImpl(StorageType type, @Nullable String id, @Nullable String name,
            @Nullable String location, @Nullable URI uri, @Nullable String eTag,
            @Nullable Long size, @Nullable Date lastModified, Map<String, String> userMetadata) {
      super(type, id, name, location, uri, userMetadata);
      this.eTag = eTag;
      this.size = size;
      this.lastModified = lastModified;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getETag() {
      return eTag;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Long getSize() {
      return size;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Date getLastModified() {
      return lastModified;
   }

}