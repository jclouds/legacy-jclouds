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

import javax.annotation.Nullable;

import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;

/**
 * Idpayload of the object
 * 
 * @author Adrian Cole
 */
public class LocationImpl implements Location, Serializable {

   /** The serialVersionUID */
   private static final long serialVersionUID = -280558162576368264L;

   private final LocationScope scope;
   private final String id;
   private final String description;
   private final Location parent;

   public LocationImpl(LocationScope scope, String id, String description, @Nullable Location parent) {
      this.scope = checkNotNull(scope, "scope");
      this.id = checkNotNull(id, "id");
      this.description = checkNotNull(description, "description");
      this.parent = parent;
   }

   public LocationScope getScope() {
      return scope;
   }

   public String getId() {
      return id;
   }

   public String getDescription() {
      return description;
   }

   public Location getParent() {
      return parent;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((parent == null) ? 0 : parent.hashCode());
      result = prime * result + ((scope == null) ? 0 : scope.hashCode());
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
      LocationImpl other = (LocationImpl) obj;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (parent == null) {
         if (other.parent != null)
            return false;
      } else if (!parent.equals(other.parent))
         return false;
      if (scope == null) {
         if (other.scope != null)
            return false;
      } else if (!scope.equals(other.scope))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + id + ", scope=" + scope + ", description=" + description + ", parent="
               + ((parent == null) ? null : parent.getId()) + "]";
   }

}