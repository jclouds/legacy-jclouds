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
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import org.jclouds.aws.s3.domain.CanonicalUser;
import org.jclouds.aws.s3.domain.ObjectMetadata;

import com.google.common.collect.Maps;

/**
 * Returns the metadata parsable from a bucket listing
 * 
 * @author Adrian Cole
 */
public class BucketListObjectMetadata implements Serializable, ObjectMetadata {

   /** The serialVersionUID */
   private static final long serialVersionUID = -4415449798024051115L;

   private final String key;
   private final Date lastModified;
   private final String eTag;
   private final long size;
   private final CanonicalUser owner;
   private final StorageClass storageClass;
   private final String contentType;
   private final byte[] contentMD5;
   private final String cacheControl;
   private final String contentDisposition;
   private final String contentEncoding;
   private final Map<String, String> userMetadata;

   public BucketListObjectMetadata(String key, Date lastModified, String eTag, byte[] md5,
            long size, CanonicalUser owner, StorageClass storageClass) {
      this.key = key;
      this.lastModified = lastModified;
      this.eTag = eTag;
      this.size = size;
      this.owner = owner;
      this.contentMD5 = md5;
      this.storageClass = storageClass;
      this.contentType = null;
      this.cacheControl = null;
      this.contentDisposition = null;
      this.contentEncoding = null;
      this.userMetadata = Maps.newHashMap();
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
   public String getContentDisposition() {
      return contentDisposition;
   }

   /**
    *{@inheritDoc}
    */
   public String getContentEncoding() {
      return contentEncoding;
   }

   /**
    *{@inheritDoc}
    */
   public String getContentType() {
      return contentType;
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
   public Long getSize() {
      return size;
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
   public byte[] getContentMD5() {
      return contentMD5;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((cacheControl == null) ? 0 : cacheControl.hashCode());
      result = prime * result + ((contentDisposition == null) ? 0 : contentDisposition.hashCode());
      result = prime * result + ((contentEncoding == null) ? 0 : contentEncoding.hashCode());
      result = prime * result + Arrays.hashCode(contentMD5);
      result = prime * result + ((contentType == null) ? 0 : contentType.hashCode());
      result = prime * result + ((eTag == null) ? 0 : eTag.hashCode());
      result = prime * result + ((key == null) ? 0 : key.hashCode());
      result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
      result = prime * result + ((owner == null) ? 0 : owner.hashCode());
      result = prime * result + (int) (size ^ (size >>> 32));
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
      BucketListObjectMetadata other = (BucketListObjectMetadata) obj;
      if (cacheControl == null) {
         if (other.cacheControl != null)
            return false;
      } else if (!cacheControl.equals(other.cacheControl))
         return false;
      if (contentDisposition == null) {
         if (other.contentDisposition != null)
            return false;
      } else if (!contentDisposition.equals(other.contentDisposition))
         return false;
      if (contentEncoding == null) {
         if (other.contentEncoding != null)
            return false;
      } else if (!contentEncoding.equals(other.contentEncoding))
         return false;
      if (!Arrays.equals(contentMD5, other.contentMD5))
         return false;
      if (contentType == null) {
         if (other.contentType != null)
            return false;
      } else if (!contentType.equals(other.contentType))
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
      if (size != other.size)
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