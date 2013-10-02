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

import org.jclouds.azureblob.domain.ContainerProperties;
import org.jclouds.azureblob.domain.MutableContainerPropertiesWithMetadata;

import com.google.common.collect.Maps;

/**
 * Allows you to manipulate metadata.
 * 
 * @author Adrian Cole
 */
public class MutableContainerPropertiesWithMetadataImpl implements
         MutableContainerPropertiesWithMetadata {

   private String name;
   private URI url;
   private Date lastModified;
   private String eTag;

   private Map<String, String> metadata = Maps.newHashMap();

   public MutableContainerPropertiesWithMetadataImpl() {
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
   public int compareTo(ContainerProperties o) {
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
      result = prime * result + ((eTag == null) ? 0 : eTag.hashCode());
      result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
      result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
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
      MutableContainerPropertiesWithMetadataImpl other = (MutableContainerPropertiesWithMetadataImpl) obj;
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
      if (url == null) {
         if (other.url != null)
            return false;
      } else if (!url.equals(other.url))
         return false;
      return true;
   }
}
