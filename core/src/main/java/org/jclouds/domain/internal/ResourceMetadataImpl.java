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

package org.jclouds.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.net.URI;
import java.util.Map;

import javax.annotation.Nullable;

import org.jclouds.domain.Location;
import org.jclouds.domain.ResourceMetadata;

import com.google.common.collect.Maps;

/**
 * Idpayload of the object
 * 
 * @author Adrian Cole
 */
public abstract class ResourceMetadataImpl<T extends Enum<T>> implements ResourceMetadata<T>, Serializable {

   /** The serialVersionUID */
   private static final long serialVersionUID = -280558162576368264L;

   @Nullable
   private final String providerId;
   @Nullable
   private final String name;
   @Nullable
   private final Location location;
   @Nullable
   private final URI uri;
   private final Map<String, String> userMetadata = Maps.newLinkedHashMap();

   public ResourceMetadataImpl(@Nullable String providerId, @Nullable String name, @Nullable Location location,
         @Nullable URI uri, Map<String, String> userMetadata) {
      this.providerId = providerId;
      this.name = name;
      this.location = location;
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
   public String getProviderId() {
      return providerId;
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
   public Location getLocation() {
      return location;
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
   public String toString() {
      return "[type=" + getType() + ", providerId=" + providerId + ", name=" + name + ", location=" + location
            + ", uri=" + uri + ", userMetadata=" + userMetadata + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((providerId == null) ? 0 : providerId.hashCode());
      result = prime * result + ((location == null) ? 0 : location.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
      if (providerId == null) {
         if (other.providerId != null)
            return false;
      } else if (!providerId.equals(other.providerId))
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
      if (uri == null) {
         if (other.uri != null)
            return false;
      } else if (!uri.equals(other.uri))
         return false;
      return true;
   }

}