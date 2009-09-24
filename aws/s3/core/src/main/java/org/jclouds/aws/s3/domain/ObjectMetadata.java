/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.aws.s3.domain;

import java.io.Serializable;
import java.util.Arrays;

import org.jclouds.blobstore.domain.BlobMetadata;

/**
 * /** Amazon S3 is designed to store objects. Objects are stored in {@link S3BucketListing buckets}
 * and consist of a {@link org.jclouds.aws.s3.domain.S3Object#getData() value}, a
 * {@link S3Object#getKey key}, {@link ObjectMetadata#getUserMetadata() metadata}, and an access
 * control policy.
 * 
 * @author Adrian Cole
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?UsingObjects.html"
 * 
 * @see <a href= "http://docs.amazonwebservices.com/AmazonS3/2006-03-01/UsingMetadata.html" />
 */
public class ObjectMetadata extends BlobMetadata implements Serializable {

   /** The serialVersionUID */
   private static final long serialVersionUID = -4415449798024051115L;

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("Metadata [accessControlList=").append(accessControlList).append(
               ", cacheControl=").append(cacheControl).append(", dataDisposition=").append(
               dataDisposition).append(", owner=").append(owner).append(", storageClass=").append(
               storageClass).append(", allHeaders=").append(allHeaders).append(", dataEncoding=")
               .append(dataEncoding).append(", dataType=").append(dataType).append(", eTag=")
               .append(Arrays.toString(eTag)).append(", key=").append(key)
               .append(", lastModified=").append(lastModified).append(", size=").append(size)
               .append(", userMetadata=").append(userMetadata).append("]");
      return builder.toString();
   }

   private String cacheControl;
   private String dataDisposition;
   private AccessControlList accessControlList;

   // only parsed on list
   private CanonicalUser owner = null;
   private String storageClass = null;
   protected String dataEncoding;

   public ObjectMetadata() {
      super();
   }

   /**
    * @param key
    * @see #getKey()
    */
   public ObjectMetadata(String key) {
      super(key);
   }

   public void setOwner(CanonicalUser owner) {
      this.owner = owner;
   }

   /**
    * Every bucket and object in Amazon S3 has an owner, the user that created the bucket or object.
    * The owner of a bucket or object cannot be changed. However, if the object is overwritten by
    * another user (deleted and rewritten), the new object will have a new owner.
    */
   public CanonicalUser getOwner() {
      return owner;
   }

   public void setStorageClass(String storageClass) {
      this.storageClass = storageClass;
   }

   /**
    * Currently defaults to 'STANDARD' and not used.
    */
   public String getStorageClass() {
      return storageClass;
   }

   public void setCacheControl(String cacheControl) {
      this.cacheControl = cacheControl;
   }

   /**
    * Can be used to specify caching behavior along the request/reply chain.
    * 
    * @link http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html?sec14.9.
    */
   public String getCacheControl() {
      return cacheControl;
   }

   public void setContentDisposition(String dataDisposition) {
      this.dataDisposition = dataDisposition;
   }

   /**
    * Specifies presentational information for the object.
    * 
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec19.html?sec19.5.1."/>
    */
   public String getContentDisposition() {
      return dataDisposition;
   }

   public void setAccessControlList(AccessControlList acl) {
      this.accessControlList = acl;
   }

   public AccessControlList getAccessControlList() {
      return this.accessControlList;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((accessControlList == null) ? 0 : accessControlList.hashCode());
      result = prime * result + ((cacheControl == null) ? 0 : cacheControl.hashCode());
      result = prime * result + ((dataDisposition == null) ? 0 : dataDisposition.hashCode());
      result = prime * result + ((owner == null) ? 0 : owner.hashCode());
      result = prime * result + ((storageClass == null) ? 0 : storageClass.hashCode());
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
      ObjectMetadata other = (ObjectMetadata) obj;
      if (accessControlList == null) {
         if (other.accessControlList != null)
            return false;
      } else if (!accessControlList.equals(other.accessControlList))
         return false;
      if (cacheControl == null) {
         if (other.cacheControl != null)
            return false;
      } else if (!cacheControl.equals(other.cacheControl))
         return false;
      if (dataDisposition == null) {
         if (other.dataDisposition != null)
            return false;
      } else if (!dataDisposition.equals(other.dataDisposition))
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
      return true;
   }

   public int compareTo(ObjectMetadata o) {
      return (this == o) ? 0 : getKey().compareTo(o.getKey());
   }

   public void setContentEncoding(String dataEncoding) {
      this.dataEncoding = dataEncoding;
   }

   /**
    * Specifies what content encodings have been applied to the object and thus what decoding
    * mechanisms must be applied in order to obtain the media-type referenced by the Content-Type
    * header field.
    * 
    * @see <a href= "http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html?sec14.11" />
    */
   public String getContentEncoding() {
      return dataEncoding;
   }

}