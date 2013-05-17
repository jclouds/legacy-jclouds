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

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Date;
import java.util.Map;

import org.jclouds.azureblob.domain.BlobProperties;
import org.jclouds.azureblob.domain.BlobType;
import org.jclouds.azureblob.domain.LeaseStatus;
import org.jclouds.io.ContentMetadata;
import org.jclouds.io.payloads.BaseImmutableContentMetadata;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.Maps;

/**
 * Allows you to manipulate metadata.
 * 
 * @author Adrian Cole
 */
public class BlobPropertiesImpl implements BlobProperties {

   private final BlobType type;
   private final String name;
   private final String container;
   private final URI url;
   private final Date lastModified;
   private final String eTag;
   private final Map<String, String> metadata = Maps.newLinkedHashMap();
   private final LeaseStatus leaseStatus;
   private final BaseImmutableContentMetadata contentMetadata;

   public BlobPropertiesImpl(BlobType type, String name, String container, URI url, Date lastModified, String eTag,
            long size, String contentType, @Nullable byte[] contentMD5, @Nullable String contentMetadata,
            @Nullable String contentLanguage, @Nullable Date currentExpires, LeaseStatus leaseStatus, 
            Map<String, String> metadata) {
      this.type = checkNotNull(type, "type");
      this.leaseStatus = checkNotNull(leaseStatus, "leaseStatus");
      this.name = checkNotNull(name, "name");
      this.container = checkNotNull(container, "container");
      this.url = checkNotNull(url, "url");
      this.lastModified = checkNotNull(lastModified, "lastModified");
      this.eTag = checkNotNull(eTag, "eTag");
      this.contentMetadata = new BaseImmutableContentMetadata(contentType, size, contentMD5, null, contentLanguage,
               contentMetadata, currentExpires);
      this.metadata.putAll(checkNotNull(metadata, "metadata"));
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
   @Override
   public String getName() {
      return name;
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
   public int compareTo(BlobProperties o) {
      return (this == o) ? 0 : getName().compareTo(o.getName());
   }

   /**
    *{@inheritDoc}
    */
   @Override
   public Map<String, String> getMetadata() {
      return metadata;
   }

   /**
    *{@inheritDoc}
    */
   @Override
   public URI getUrl() {
      return url;
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
   @Override
   public ContentMetadata getContentMetadata() {
      return contentMetadata;
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
      BlobPropertiesImpl other = (BlobPropertiesImpl) obj;
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
}
