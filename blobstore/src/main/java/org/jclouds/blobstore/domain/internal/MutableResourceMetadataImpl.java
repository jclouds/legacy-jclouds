/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.blobstore.domain.internal;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;
import java.util.Map;

import org.jclouds.blobstore.domain.MutableResourceMetadata;
import org.jclouds.blobstore.domain.ResourceMetadata;
import org.jclouds.blobstore.domain.ResourceType;

import com.google.common.collect.Maps;

/**
 * Identity of the object
 * 
 * @author Adrian Cole
 */
public class MutableResourceMetadataImpl implements MutableResourceMetadata, Serializable {

   /** The serialVersionUID */
   private static final long serialVersionUID = -280558162576368264L;

   private ResourceType type;
   private String id;
   private String name;
   private URI location;
   private String eTag;
   private Long size;
   private Date lastModified;
   private Map<String, String> userMetadata;

   public MutableResourceMetadataImpl() {
      userMetadata = Maps.newHashMap();
   }

   public MutableResourceMetadataImpl(ResourceMetadata from) {
      this.type = from.getType();
      this.id = from.getId();
      this.name = from.getName();
      this.location = from.getLocation();
      this.eTag = from.getETag();
      this.size = from.getSize();
      this.lastModified = from.getLastModified();
      this.userMetadata = from.getUserMetadata();
   }

   public int compareTo(ResourceMetadata o) {
      if (getName() == null)
         return -1;
      return (this == o) ? 0 : getName().compareTo(o.getName());
   }

   public ResourceType getType() {
      return type;
   }

   public String getName() {
      return name;
   }

   public String getId() {
      return id;
   }

   public URI getLocation() {
      return location;
   }

   public String getETag() {
      return eTag;
   }

   public Long getSize() {
      return size;
   }

   public Date getLastModified() {
      return lastModified;
   }

   public Map<String, String> getUserMetadata() {
      return userMetadata;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((eTag == null) ? 0 : eTag.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
      result = prime * result + ((location == null) ? 0 : location.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((size == null) ? 0 : size.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      result = prime * result + ((userMetadata == null) ? 0 : userMetadata.hashCode());
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
      MutableResourceMetadataImpl other = (MutableResourceMetadataImpl) obj;
      if (eTag == null) {
         if (other.eTag != null)
            return false;
      } else if (!eTag.equals(other.eTag))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (lastModified == null) {
         if (other.lastModified != null)
            return false;
      } else if (!lastModified.equals(other.lastModified))
         return false;
      if (location == null) {
         if (other.location != null)
            return false;
      } else if (!location.equals(other.location))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (size == null) {
         if (other.size != null)
            return false;
      } else if (!size.equals(other.size))
         return false;
      if (type == null) {
         if (other.type != null)
            return false;
      } else if (!type.equals(other.type))
         return false;
      if (userMetadata == null) {
         if (other.userMetadata != null)
            return false;
      } else if (!userMetadata.equals(other.userMetadata))
         return false;
      return true;
   }

   public void setLastModified(Date lastModified) {
      this.lastModified = lastModified;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setSize(long size) {
      this.size = size;
   }

   public void setType(ResourceType type) {
      this.type = type;
   }

   public void setUserMetadata(Map<String, String> userMetadata) {
      this.userMetadata = userMetadata;
   }

   public void setETag(String eTag) {
      this.eTag = eTag;
   }

   public void setId(String id) {
      this.id = id;
   }

   public void setLocation(URI location) {
      this.location = location;
   }

}