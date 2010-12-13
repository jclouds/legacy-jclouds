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

package org.jclouds.azure.storage.blob.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;
import java.util.Map;

import javax.annotation.Nullable;

import org.jclouds.azure.storage.blob.domain.BlobProperties;
import org.jclouds.azure.storage.blob.domain.BlobType;
import org.jclouds.azure.storage.blob.domain.LeaseStatus;
import org.jclouds.io.ContentMetadata;
import org.jclouds.io.payloads.BaseImmutableContentMetadata;

import com.google.common.collect.Maps;

/**
 * Allows you to manipulate metadata.
 * 
 * @author Adrian Cole
 */
public class BlobPropertiesImpl implements Serializable, BlobProperties {

   /** The serialVersionUID */
   private static final long serialVersionUID = -4648755473986695062L;
   private final BlobType type;
   private final String name;
   private final URI url;
   private final Date lastModified;
   private final String eTag;
   private final Map<String, String> metadata = Maps.newLinkedHashMap();
   private final LeaseStatus leaseStatus;
   private final BaseImmutableContentMetadata contentMetadata;

   public BlobPropertiesImpl(BlobType type, String name, URI url, Date lastModified, String eTag, long size,
            String contentType, @Nullable byte[] contentMD5, @Nullable String contentMetadata,
            @Nullable String contentLanguage, LeaseStatus leaseStatus, Map<String, String> metadata) {
      this.type = checkNotNull(type, "type");
      this.leaseStatus = checkNotNull(leaseStatus, "leaseStatus");
      this.name = checkNotNull(name, "name");
      this.url = checkNotNull(url, "url");
      this.lastModified = checkNotNull(lastModified, "lastModified");
      this.eTag = checkNotNull(eTag, "eTag");
      this.contentMetadata = new BaseImmutableContentMetadata(contentType, size, contentMD5, null, contentLanguage,
               contentMetadata);
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
      result = prime * result + ((contentMetadata == null) ? 0 : contentMetadata.hashCode());
      result = prime * result + ((eTag == null) ? 0 : eTag.hashCode());
      result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
      result = prime * result + ((leaseStatus == null) ? 0 : leaseStatus.hashCode());
      result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
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
      if (lastModified == null) {
         if (other.lastModified != null)
            return false;
      } else if (!lastModified.equals(other.lastModified))
         return false;
      if (leaseStatus == null) {
         if (other.leaseStatus != null)
            return false;
      } else if (!leaseStatus.equals(other.leaseStatus))
         return false;
      if (metadata == null) {
         if (other.metadata != null)
            return false;
      } else if (!metadata.equals(other.metadata))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (type == null) {
         if (other.type != null)
            return false;
      } else if (!type.equals(other.type))
         return false;
      if (url == null) {
         if (other.url != null)
            return false;
      } else if (!url.equals(other.url))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[name=" + name + ", type=" + type + ", contentMetadata=" + contentMetadata + ", eTag=" + eTag
               + ", lastModified=" + lastModified + "]";
   }

}