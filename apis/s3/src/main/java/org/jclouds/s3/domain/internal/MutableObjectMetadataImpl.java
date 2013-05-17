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

import org.jclouds.http.HttpUtils;
import org.jclouds.io.MutableContentMetadata;
import org.jclouds.io.payloads.BaseMutableContentMetadata;
import org.jclouds.s3.domain.CanonicalUser;
import org.jclouds.s3.domain.MutableObjectMetadata;
import org.jclouds.s3.domain.ObjectMetadata;

import com.google.common.collect.Maps;

/**
 * Allows you to manipulate metadata.
 * 
 * @author Adrian Cole
 */
public class MutableObjectMetadataImpl implements MutableObjectMetadata {

   private String key;
   private String bucket;
   private URI uri;
   private Date lastModified;
   private String eTag;
   private CanonicalUser owner;
   private StorageClass storageClass;
   private String cacheControl;
   private Map<String, String> userMetadata = Maps.newHashMap();
   private MutableContentMetadata contentMetadata;

   public MutableObjectMetadataImpl() {
      this.storageClass = StorageClass.STANDARD;
      this.contentMetadata = new BaseMutableContentMetadata();
   }

   public MutableObjectMetadataImpl(ObjectMetadata from) {
      this.storageClass = StorageClass.STANDARD;
      this.contentMetadata = new BaseMutableContentMetadata();
      HttpUtils.copy(from.getContentMetadata(), this.contentMetadata);
      this.key = from.getKey();
      this.uri = from.getUri();
      this.bucket = from.getBucket();
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
   public URI getUri() {
      return uri;
   }

   /**
    *{@inheritDoc}
    */
   @Override
   public void setUri(URI uri) {
      this.uri = uri;
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
      return cacheControl;
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
      return (this == o) ? 0 : getKey().compareTo(o.getKey());
   }

   /**
    *{@inheritDoc}
    */
   @Override
   public Map<String, String> getUserMetadata() {
      return userMetadata;
   }

   /**
    *{@inheritDoc}
    */
   @Override
   public void setCacheControl(String cacheControl) {
      this.cacheControl = cacheControl;
   }

   /**
    *{@inheritDoc}
    */
   @Override
   public void setETag(String eTag) {
      this.eTag = eTag;
   }

   /**
    *{@inheritDoc}
    */
   @Override
   public void setKey(String key) {
      this.key = key;
   }

   /**
    *{@inheritDoc}
    */
   @Override
   public void setBucket(String bucket) {
      this.bucket = bucket;
   }

   /**
    *{@inheritDoc}
    */
   @Override
   public void setLastModified(Date lastModified) {
      this.lastModified = lastModified;
   }

   /**
    *{@inheritDoc}
    */
   @Override
   public void setOwner(CanonicalUser owner) {
      this.owner = owner;
   }

   /**
    *{@inheritDoc}
    */
   @Override
   public void setStorageClass(StorageClass storageClass) {
      this.storageClass = storageClass;
   }

   /**
    *{@inheritDoc}
    */
   @Override
   public void setUserMetadata(Map<String, String> userMetadata) {
      this.userMetadata = userMetadata;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public MutableContentMetadata getContentMetadata() {
      return contentMetadata;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setContentMetadata(MutableContentMetadata contentMetadata) {
      this.contentMetadata = contentMetadata;
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
      MutableObjectMetadataImpl other = (MutableObjectMetadataImpl) obj;
      if (uri == null) {
         if (other.uri != null)
            return false;
      } else if (!uri.equals(other.uri))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return String
               .format(
                        "[key=%s, bucket=%s, uri=%s, eTag=%s, cacheControl=%s, contentMetadata=%s, lastModified=%s, owner=%s, storageClass=%s, userMetadata=%s]",
                        key, bucket, uri, eTag, cacheControl, contentMetadata, lastModified, owner, storageClass,
                        userMetadata);
   }

}
