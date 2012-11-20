/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.blobstore.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Date;
import java.util.Map;

import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.domain.Location;
import org.jclouds.domain.internal.ResourceMetadataImpl;
import org.jclouds.javax.annotation.Nullable;

/**
 * Idpayload of the object
 * 
 * @author Adrian Cole
 */
public class StorageMetadataImpl extends ResourceMetadataImpl<StorageType> implements StorageMetadata {

   @Nullable
   private final String eTag;
   @Nullable
   private final Date lastModified;
   private final StorageType type;

   public StorageMetadataImpl(StorageType type, @Nullable String id, @Nullable String name,
         @Nullable Location location, @Nullable URI uri, @Nullable String eTag, @Nullable Date lastModified,
         Map<String, String> userMetadata) {
      super(id, name, location, uri, userMetadata);
      this.eTag = eTag;
      this.lastModified = lastModified;
      this.type = checkNotNull(type, "type");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public StorageType getType() {
      return type;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((eTag == null) ? 0 : eTag.hashCode());
      result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      StorageMetadataImpl other = (StorageMetadataImpl) obj;
      if (eTag == null) {
         if (other.eTag != null)
            return false;
      } else if (!eTag.equals(other.eTag))
         return false;
      if (lastModified == null) {
         if (other.lastModified != null)
            return false;
      } else if (!lastModified.equals(other.lastModified))
         return false;
      if (type != other.type)
         return false;
      return true;
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
   public Date getLastModified() {
      return lastModified;
   }

}
