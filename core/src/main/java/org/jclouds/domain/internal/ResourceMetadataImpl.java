/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.net.URI;
import java.util.Map;

import org.jclouds.domain.ResourceMetadata;

import com.google.common.collect.Maps;
import com.google.inject.internal.Nullable;

/**
 * Idpayload of the object
 * 
 * @author Adrian Cole
 */
public class ResourceMetadataImpl<T extends Enum<T>> implements ResourceMetadata<T>, Serializable {

   /** The serialVersionUID */
   private static final long serialVersionUID = -280558162576368264L;

   private final T type;
   @Nullable
   private final String id;
   @Nullable
   private final String name;
   @Nullable
   private final String locationId;
   @Nullable
   private final URI uri;
   private final Map<String, String> userMetadata = Maps.newLinkedHashMap();

   public ResourceMetadataImpl(T type, @Nullable String id, @Nullable String name,
            @Nullable String locationId, @Nullable URI uri, Map<String, String> userMetadata) {
      this.type = checkNotNull(type, "type");
      this.id = id;
      this.name = name;
      this.locationId = locationId;
      this.uri = uri;
      this.userMetadata.putAll(checkNotNull(userMetadata, "userMetadata"));
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
   public String getId() {
      return id;
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
   public String getLocationId() {
      return locationId;
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

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((locationId == null) ? 0 : locationId.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
      ResourceMetadataImpl<?> other = (ResourceMetadataImpl<?>) obj;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (locationId == null) {
         if (other.locationId != null)
            return false;
      } else if (!locationId.equals(other.locationId))
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
      if (uri == null) {
         if (other.uri != null)
            return false;
      } else if (!uri.equals(other.uri))
         return false;
      if (userMetadata == null) {
         if (other.userMetadata != null)
            return false;
      } else if (!userMetadata.equals(other.userMetadata))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[type=" + type + ", id=" + id + ", name=" + name + ", locationId=" + locationId
               + ", uri=" + uri + ", userMetadata=" + userMetadata + "]";
   }

}