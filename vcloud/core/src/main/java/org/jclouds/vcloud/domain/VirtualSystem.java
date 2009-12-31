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
package org.jclouds.vcloud.domain;

import static com.google.common.base.Preconditions.checkNotNull;

public class VirtualSystem {
   protected final int id;
   protected final String name;
   protected final String identifier;
   protected final String type;

   public VirtualSystem(int id, String name, String identifier, String type) {
      this.id = id;
      this.name = checkNotNull(name, "name");
      this.identifier = checkNotNull(identifier, "identifier");
      this.type = checkNotNull(type, "type");
   }

   public String getName() {
      return name;
   }

   public int getId() {
      return id;
   }

   public String getIdentifier() {
      return identifier;
   }

   public String getType() {
      return type;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + id;
      result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
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
      VirtualSystem other = (VirtualSystem) obj;
      if (id != other.id)
         return false;
      if (identifier == null) {
         if (other.identifier != null)
            return false;
      } else if (!identifier.equals(other.identifier))
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
      return true;
   }

   @Override
   public String toString() {
      return "VirtualSystem [id=" + id + ", identifier=" + identifier + ", name=" + name
               + ", type=" + type + "]";
   }

}