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
package org.jclouds.domain.internal;

import java.net.URI;
import java.util.Map;

import org.jclouds.domain.Location;
import org.jclouds.domain.MutableResourceMetadata;
import org.jclouds.domain.ResourceMetadata;

import com.google.common.collect.Maps;

/**
 * Used to construct new resources or modify existing ones.
 * 
 * @author Adrian Cole
 */
public class MutableResourceMetadataImpl<T extends Enum<T>> implements MutableResourceMetadata<T> {

   private T type;
   private String id;
   private String name;
   private Location location;
   private URI uri;
   private Map<String, String> userMetadata;

   public MutableResourceMetadataImpl() {
      userMetadata = Maps.newLinkedHashMap();
   }

   public MutableResourceMetadataImpl(ResourceMetadata<T> from) {
      this.type = from.getType();
      this.id = from.getProviderId();
      this.name = from.getName();
      this.location = from.getLocation();
      this.uri = from.getUri();
      this.userMetadata = from.getUserMetadata();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int compareTo(ResourceMetadata<T> o) {
      if (getName() == null)
         return -1;
      return (this == o) ? 0 : getName().compareTo(o.getName());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public T getType() {
      return type;
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
   public String getProviderId() {
      return id;
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
   public Map<String, String> getUserMetadata() {
      return userMetadata;
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
   public void setType(T type) {
      this.type = type;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setUserMetadata(Map<String, String> userMetadata) {
      this.userMetadata = userMetadata;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setId(String id) {
      this.id = id;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setUri(URI uri) {
      this.uri = uri;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setLocation(Location location) {
      this.location = location;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Location getLocation() {
      return location;
   }

   @Override
   public String toString() {
      return "[type=" + type + ", id=" + id + ", name=" + name + ", location=" + location
               + ", uri=" + uri + ", userMetadata=" + userMetadata + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((location == null) ? 0 : location.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      result = prime * result + ((uri == null) ? 0 : uri.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (!(obj instanceof MutableResourceMetadata<?>))
         return false;
      MutableResourceMetadata<?> other = (MutableResourceMetadata<?>) obj;
      if (id == null) {
         if (other.getProviderId() != null)
            return false;
      } else if (!id.equals(other.getProviderId()))
         return false;
      if (location == null) {
         if (other.getLocation() != null)
            return false;
      } else if (!location.equals(other.getLocation()))
         return false;
      if (name == null) {
         if (other.getName() != null)
            return false;
      } else if (!name.equals(other.getName()))
         return false;
      if (type == null) {
         if (other.getType() != null)
            return false;
      } else if (!type.equals(other.getType()))
         return false;
      if (uri == null) {
         if (other.getUri() != null)
            return false;
      } else if (!uri.equals(other.getUri()))
         return false;
      return true;
   }

}
