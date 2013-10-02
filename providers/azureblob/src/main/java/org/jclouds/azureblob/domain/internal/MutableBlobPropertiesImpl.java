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
package org.jclouds.azureblob.domain.internal;

import java.net.URI;
import java.util.Date;
import java.util.Map;

import org.jclouds.azureblob.domain.BlobProperties;
import org.jclouds.azureblob.domain.BlobType;
import org.jclouds.azureblob.domain.LeaseStatus;
import org.jclouds.azureblob.domain.MutableBlobProperties;
import org.jclouds.http.HttpUtils;
import org.jclouds.io.MutableContentMetadata;
import org.jclouds.io.payloads.BaseMutableContentMetadata;

import com.google.common.collect.Maps;

/**
 * Allows you to manipulate metadata.
 * 
 * @author Adrian Cole
 */
public class MutableBlobPropertiesImpl implements MutableBlobProperties {

   private BlobType type = BlobType.BLOCK_BLOB;
   private LeaseStatus leaseStatus = LeaseStatus.UNLOCKED;

   private String name;
   private String container;
   private URI url;
   private Date lastModified;
   private String eTag;
   private MutableContentMetadata contentMetadata;
   private Map<String, String> metadata = Maps.newHashMap();

   public MutableBlobPropertiesImpl() {
      super();
      this.contentMetadata = new BaseMutableContentMetadata();
   }

   public MutableBlobPropertiesImpl(BlobProperties from) {
      this.contentMetadata = new BaseMutableContentMetadata();
      this.name = from.getName();
      this.container = from.getContainer();
      this.url = from.getUrl();
      this.lastModified = from.getLastModified();
      this.eTag = from.getETag();
      this.metadata.putAll(from.getMetadata());
      HttpUtils.copy(from.getContentMetadata(), this.contentMetadata);
   }

   /**
    *{@inheritDoc}
    */
   @Override
   public BlobType getType() {
      return type;
   }

   /**
    *{@inheritDoc}
    */
   public void setType(BlobType type) {
      this.type = type;
   }

   /**
    *{@inheritDoc}
    */
   public String getName() {
      return name;
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
   public int compareTo(BlobProperties o) {
      return (this == o) ? 0 : getName().compareTo(o.getName());
   }

   /**
    *{@inheritDoc}
    */
   public Map<String, String> getMetadata() {
      return metadata;
   }

   /**
    *{@inheritDoc}
    */
   @Override
   public LeaseStatus getLeaseStatus() {
      return leaseStatus;
   }

   /**
    *{@inheritDoc}
    */
   public void setETag(String eTag) {
      this.eTag = eTag;
   }

   /**
    *{@inheritDoc}
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    *{@inheritDoc}
    */
   public void setLastModified(Date lastModified) {
      this.lastModified = lastModified;
   }

   /**
    *{@inheritDoc}
    */
   public void setMetadata(Map<String, String> metadata) {
      this.metadata = metadata;
   }

   public void setUrl(URI url) {
      this.url = url;
   }

   public URI getUrl() {
      return url;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((url == null) ? 0 : url.hashCode());
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
      MutableBlobPropertiesImpl other = (MutableBlobPropertiesImpl) obj;
      if (url == null) {
         if (other.url != null)
            return false;
      } else if (!url.equals(other.url))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return String
               .format(
                        "[name=%s, container=%s, url=%s, contentMetadata=%s, eTag=%s, lastModified=%s, leaseStatus=%s, metadata=%s, type=%s]",
                        name, container, url, contentMetadata, eTag, lastModified, leaseStatus, metadata, type);
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

   /**
    *{@inheritDoc}
    */
   @Override
   public String getContainer() {
      return container;
   }

   /**
    *{@inheritDoc}
    */
   @Override
   public void setContainer(String container) {
      this.container = container;
   }

}
