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
public class CopyObjectResult implements ObjectMetadata {

   private final Date lastModified;
   private final String eTag;
   private final BaseImmutableContentMetadata contentMetadata;

   public CopyObjectResult(Date lastModified, String eTag) {
      this.lastModified = lastModified;
      this.eTag = eTag;
      this.contentMetadata = new BaseImmutableContentMetadata(null, null, null, null, null, null, null);
   }

   /**
    *{@inheritDoc}
    */
   @Override
   public String getKey() {
      return null;
   }

   /**
    *{@inheritDoc}
    */
   @Override
   public String getBucket() {
      return null;
   }

   /**
    *{@inheritDoc}
    */
   @Override
   public URI getUri() {
      return null;
   }

   /**
    *{@inheritDoc}
    */
   @Override
   public CanonicalUser getOwner() {
      return null;
   }

   /**
    *{@inheritDoc}
    */
   @Override
   public StorageClass getStorageClass() {
      return null;
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
      return (this == o) ? 0 : getETag().compareTo(o.getETag());
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
      result = prime * result + ((eTag == null) ? 0 : eTag.hashCode());
      result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
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
      return true;
   }

   @Override
   public String toString() {
      return String.format("[eTag=%s, lastModified=%s]", eTag, lastModified);
   }

}
