/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.aws.s3.domain.internal;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.jclouds.aws.s3.domain.CanonicalUser;
import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.io.ContentMetadata;
import org.jclouds.io.payloads.BaseImmutableContentMetadata;

/**
 * Returns the metadata parsable from a bucket listing
 * 
 * @author Adrian Cole
 */
public class CopyObjectResult implements Serializable, ObjectMetadata {

   /** The serialVersionUID */
   private static final long serialVersionUID = -4415449798024051115L;

   private final String key;
   private final Date lastModified;
   private final String eTag;
   private final CanonicalUser owner;
   private final StorageClass storageClass;
   private final String cacheControl;
   private final Map<String, String> userMetadata;
   private final BaseImmutableContentMetadata contentMetadata;

   public CopyObjectResult(Date lastModified, String eTag) {
      this.key = null;
      this.lastModified = lastModified;
      this.eTag = eTag;
      this.owner = null;
      this.storageClass = StorageClass.STANDARD;
      this.contentMetadata = new BaseImmutableContentMetadata(null, null, null, null, null, null);
      this.cacheControl = null;
      this.userMetadata = null;
   }

   /**
    *{@inheritDoc}
    */
   public String getKey() {
      return key;
   }

   /**
    *{@inheritDoc}
    */
   public CanonicalUser getOwner() {
      return owner;
   }

   /**
    *{@inheritDoc}
    */
   public StorageClass getStorageClass() {
      return storageClass;
   }

   /**
    *{@inheritDoc}
    */
   public String getCacheControl() {
      return cacheControl;
   }

   /**
    *{@inheritDoc}
    */
   public Date getLastModified() {
      return lastModified;
   }

   /**
    *{@inheritDoc}
    */
   public String getETag() {
      return eTag;
   }

   /**
    *{@inheritDoc}
    */
   public int compareTo(ObjectMetadata o) {
      return (this == o) ? 0 : getKey().compareTo(o.getKey());
   }

   /**
    *{@inheritDoc}
    */
   public Map<String, String> getUserMetadata() {
      return userMetadata;
   }

   /**
    *{@inheritDoc}
    */
   @Override
   public ContentMetadata getContentMetadata() {
      return contentMetadata;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((cacheControl == null) ? 0 : cacheControl.hashCode());
      result = prime * result + ((contentMetadata == null) ? 0 : contentMetadata.hashCode());
      result = prime * result + ((eTag == null) ? 0 : eTag.hashCode());
      result = prime * result + ((key == null) ? 0 : key.hashCode());
      result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
      result = prime * result + ((owner == null) ? 0 : owner.hashCode());
      result = prime * result + ((storageClass == null) ? 0 : storageClass.hashCode());
      result = prime * result + ((userMetadata == null) ? 0 : userMetadata.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      CopyObjectResult other = (CopyObjectResult) obj;
      if (cacheControl == null) {
         if (other.cacheControl != null)
            return false;
      } else if (!cacheControl.equals(other.cacheControl))
         return false;
      if (contentMetadata == null) {
         if (other.contentMetadata != null)
            return false;
      } else if (!contentMetadata.equals(other.contentMetadata))
         return false;
      if (eTag == null) {
         if (other.eTag != null)
            return false;
      } else if (!eTag.equals(other.eTag))
         return false;
      if (key == null) {
         if (other.key != null)
            return false;
      } else if (!key.equals(other.key))
         return false;
      if (lastModified == null) {
         if (other.lastModified != null)
            return false;
      } else if (!lastModified.equals(other.lastModified))
         return false;
      if (owner == null) {
         if (other.owner != null)
            return false;
      } else if (!owner.equals(other.owner))
         return false;
      if (storageClass == null) {
         if (other.storageClass != null)
            return false;
      } else if (!storageClass.equals(other.storageClass))
         return false;
      if (userMetadata == null) {
         if (other.userMetadata != null)
            return false;
      } else if (!userMetadata.equals(other.userMetadata))
         return false;
      return true;
   }

}