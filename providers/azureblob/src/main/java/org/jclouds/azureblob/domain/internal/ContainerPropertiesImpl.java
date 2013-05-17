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

import org.jclouds.azureblob.domain.ContainerProperties;

import com.google.common.collect.Maps;

/**
 * Allows you to manipulate metadata.
 * 
 * @author Adrian Cole
 */
public class ContainerPropertiesImpl implements ContainerProperties {

   private final String name;
   private final URI url;
   private final Date lastModified;
   private final String eTag;
   private final Map<String, String> metadata = Maps.newLinkedHashMap();

   public ContainerPropertiesImpl(URI url, Date lastModified, String eTag,Map<String, String> metadata) {
      this.url = checkNotNull(url, "url");
      this.name = checkNotNull(url.getPath(), "url.getPath()").replaceFirst("/", "");
      this.lastModified = checkNotNull(lastModified, "lastModified");
      this.eTag = checkNotNull(eTag, "eTag");
      this.metadata.putAll(checkNotNull(metadata, "metadata"));
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
   public int compareTo(ContainerProperties o) {
      return (this == o) ? 0 : getName().compareTo(o.getName());
   }
   /**
    *{@inheritDoc}
    */
   @Override
   public Map<String, String> getMetadata() {
      return metadata;
   }   /**
    *{@inheritDoc}
    */
   @Override
   public URI getUrl() {
      return url;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((eTag == null) ? 0 : eTag.hashCode());
      result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
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
      ContainerPropertiesImpl other = (ContainerPropertiesImpl) obj;
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
