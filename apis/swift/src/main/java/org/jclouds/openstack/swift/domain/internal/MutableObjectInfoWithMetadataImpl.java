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
package org.jclouds.openstack.swift.domain.internal;

import java.net.URI;
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
   private String container;
   private URI uri;
   private Long bytes;
   private byte[] hash;
   private String contentType = MediaType.APPLICATION_OCTET_STREAM;
   private Date lastModified;
   private String objectManifest;
   private final Map<String, String> metadata = Maps.newLinkedHashMap();

   /**
    * {@inheritDoc}
    */
   @Override
   public Map<String, String> getMetadata() {
      return metadata;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setBytes(Long bytes) {
      this.bytes = bytes;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setContentType(String contentType) {
      this.contentType = contentType;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setHash(byte[] hash) {
      this.hash = hash;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setName(String name) {
      this.name = name;
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
   public byte[] getHash() {
      return hash;
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
      result = prime * result + ((objectManifest == null) ? 0 : objectManifest.hashCode());
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
      if (objectManifest == null) {
         if (other.objectManifest != null)
            return false;
      } else if (!objectManifest.equals(other.objectManifest))
         return false;
      return true;
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
   public int compareTo(ObjectInfo o) {
      return (this == o) ? 0 : getName().compareTo(o.getName());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setLastModified(Date lastModified) {
      this.lastModified = lastModified;
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
   public void setContainer(String container) {
      this.container = container;
   }

   @Override
   public void setUri(URI uri) {
      this.uri = uri;
   }

   @Override
   public URI getUri() {
      return uri;
   }

   @Override
   public String getObjectManifest() {
      return objectManifest;
   }

   @Override
   public void setObjectManifest(String objectManifest) {
      this.objectManifest = objectManifest;
   }

   @Override
   public String toString() {
      return String.format("[name=%s, container=%s, uri=%s, bytes=%s, contentType=%s, lastModified=%s, hash=%s, objectManifest=%s]",
               name, container, uri, bytes, contentType, lastModified, Arrays.toString(hash), objectManifest);
   }

}
