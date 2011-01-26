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

package org.jclouds.openstack.swift.domain.internal;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.jclouds.openstack.swift.domain.MutableObjectInfoWithMetadata;
import org.jclouds.openstack.swift.domain.ObjectInfo;

import com.google.common.collect.Maps;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class MutableObjectInfoWithMetadataImpl implements MutableObjectInfoWithMetadata {
   private String name;
   private Long bytes;
   private byte[] hash;
   private String contentType = MediaType.APPLICATION_OCTET_STREAM;
   private Date lastModified;
   private final Map<String, String> metadata = Maps.newHashMap();

   public Map<String, String> getMetadata() {
      return metadata;
   }

   public void setBytes(Long bytes) {
      this.bytes = bytes;
   }

   public void setContentType(String contentType) {
      this.contentType = contentType;
   }

   public void setHash(byte[] hash) {
      this.hash = hash;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Long getBytes() {
      return bytes;
   }

   public String getContentType() {
      return contentType;
   }

   public byte[] getHash() {
      return hash;
   }

   public Date getLastModified() {
      return lastModified;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((bytes == null) ? 0 : bytes.hashCode());
      result = prime * result + ((contentType == null) ? 0 : contentType.hashCode());
      result = prime * result + Arrays.hashCode(hash);
      result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
      result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
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
      MutableObjectInfoWithMetadataImpl other = (MutableObjectInfoWithMetadataImpl) obj;
      if (bytes == null) {
         if (other.bytes != null)
            return false;
      } else if (!bytes.equals(other.bytes))
         return false;
      if (contentType == null) {
         if (other.contentType != null)
            return false;
      } else if (!contentType.equals(other.contentType))
         return false;
      if (!Arrays.equals(hash, other.hash))
         return false;
      if (lastModified == null) {
         if (other.lastModified != null)
            return false;
      } else if (!lastModified.equals(other.lastModified))
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
      return true;
   }

   public String getName() {
      return name;
   }

   public int compareTo(ObjectInfo o) {
      return (this == o) ? 0 : getName().compareTo(o.getName());
   }

   public void setLastModified(Date lastModified) {
      this.lastModified = lastModified;
   }

}
