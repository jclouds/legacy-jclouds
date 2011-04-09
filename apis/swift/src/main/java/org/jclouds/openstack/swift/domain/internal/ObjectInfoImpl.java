/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import java.net.URI;
import java.util.Arrays;
import java.util.Date;

import org.jclouds.openstack.swift.domain.ObjectInfo;

import com.google.gson.annotations.SerializedName;

public class ObjectInfoImpl implements ObjectInfo {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String name;
      private String container;
      private URI uri;
      private byte[] hash;
      private Long bytes;
      private String contentType;
      private Date lastModified;

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder container(String container) {
         this.container = container;
         return this;
      }

      public Builder uri(URI uri) {
         this.uri = uri;
         return this;
      }

      public Builder hash(byte[] hash) {
         this.hash = hash;
         return this;
      }

      public Builder bytes(Long bytes) {
         this.bytes = bytes;
         return this;
      }

      public Builder contentType(String contentType) {
         this.contentType = contentType;
         return this;
      }

      public Builder lastModified(Date lastModified) {
         this.lastModified = lastModified;
         return this;
      }

      public ObjectInfoImpl build() {
         return new ObjectInfoImpl(name, uri, container, hash, bytes, contentType, lastModified);
      }

      public Builder fromObjectInfo(ObjectInfo in) {
         return name(in.getName()).container(in.getContainer()).uri(uri).hash(in.getHash()).bytes(in.getBytes())
                  .contentType(in.getContentType()).lastModified(in.getLastModified());
      }
   }

   private String name;
   private String container;
   private URI uri;
   private byte[] hash;
   private Long bytes;
   @SerializedName("content_type")
   private String contentType;
   @SerializedName("last_modified")
   private Date lastModified;

   public ObjectInfoImpl(String name, URI uri, String container, byte[] hash, Long bytes, String contentType,
            Date lastModified) {
      this.name = name;
      this.container = container;
      this.uri = uri;
      this.hash = hash;
      this.bytes = bytes;
      this.contentType = contentType;
      this.lastModified = lastModified;
   }

   ObjectInfoImpl() {

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getName() {
      return name;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getContainer() {
      return container;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URI getUri() {
      return uri;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public byte[] getHash() {
      return hash;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Long getBytes() {
      return bytes;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getContentType() {
      return contentType;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Date getLastModified() {
      return lastModified;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((container == null) ? 0 : container.hashCode());
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
      ObjectInfoImpl other = (ObjectInfoImpl) obj;
      if (container == null) {
         if (other.container != null)
            return false;
      } else if (!container.equals(other.container))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return String.format("[name=%s, container=%s, uri=%s, bytes=%s, contentType=%s, lastModified=%s, hash=%s]", name,
               container, uri, bytes, contentType, lastModified, Arrays.toString(hash));
   }

   public Builder toBuilder() {
      return builder().fromObjectInfo(this);
   }

   @Override
   public int compareTo(ObjectInfo o) {
      return name.compareTo(o.getName());
   }

}
