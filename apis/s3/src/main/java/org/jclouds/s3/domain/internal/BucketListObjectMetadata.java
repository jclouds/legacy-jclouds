/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.s3.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Date;
import java.util.Map;

import org.jclouds.io.ContentMetadata;
import org.jclouds.io.payloads.BaseImmutableContentMetadata;
import org.jclouds.s3.domain.CanonicalUser;
import org.jclouds.s3.domain.ObjectMetadata;

import com.google.common.collect.ImmutableMap;

/**
 * Returns the metadata parsable from a bucket listing
 * 
 * @author Adrian Cole
 */
public class BucketListObjectMetadata implements ObjectMetadata {

   private final String key;
   private final String bucket;
   private final URI uri;
   private final Date lastModified;
   private final String eTag;
   private final CanonicalUser owner;
   private final StorageClass storageClass;
   private final ContentMetadata contentMetadata;

   public BucketListObjectMetadata(String key, String bucket, URI uri, Date lastModified, String eTag, byte[] md5,
            long contentLength, CanonicalUser owner, StorageClass storageClass) {
      this.key = checkNotNull(key, "key");
      this.bucket = checkNotNull(bucket, "bucket");
      this.uri = checkNotNull(uri, "uri");
      this.lastModified = lastModified;
      this.eTag = eTag;
      this.owner = owner;
      this.contentMetadata = new BaseImmutableContentMetadata(null, contentLength, md5, null, null, null, null);
      this.storageClass = storageClass;
   }

   /**
    *{@inheritDoc}
    */
   @Override
   public URI getUri() {
      return uri;
   }

   /**
    *{@inheritDoc}
    */
   @Override
   public String getKey() {
      return key;
   }

   /**
    *{@inheritDoc}
    */
   @Override
   public String getBucket() {
      return bucket;
   }

   /**
    *{@inheritDoc}
    */
   @Override
   public CanonicalUser getOwner() {
      return owner;
   }

   /**
    *{@inheritDoc}
    */
   @Override
   public StorageClass getStorageClass() {
      return storageClass;
   }

   /**
    *{@inheritDoc}
    */
   @Override
   public String getCacheControl() {
      return null;
   }

   /**
    *{@inheritDoc}
    */
   @Override
   public Date getLastModified() {
      return lastModified;
   }

   /**
    *{@inheritDoc}
    */
   @Override
   public String getETag() {
      return eTag;
   }

   /**
    *{@inheritDoc}
    */
   @Override
   public int compareTo(ObjectMetadata o) {
      return (this == o) ? 0 : getUri().compareTo(o.getUri());
   }

   /**
    *{@inheritDoc}
    */
   @Override
   public Map<String, String> getUserMetadata() {
      return ImmutableMap.of();
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
      result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
      if (uri == null) {
         if (other.uri != null)
            return false;
      } else if (!uri.equals(other.uri))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return String.format(
               "[uri=%s, key=%s, bucket=%s, contentMetadata=%s, eTag=%s, lastModified=%s, owner=%s, storageClass=%s]",
               uri, key, bucket, contentMetadata, eTag, lastModified, owner, storageClass);
   }

}
