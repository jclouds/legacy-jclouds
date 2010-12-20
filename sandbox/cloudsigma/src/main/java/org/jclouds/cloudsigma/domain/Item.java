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

package org.jclouds.cloudsigma.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
public class Item {
   public static class Builder {
      protected String uuid;
      protected String name;
      protected Set<String> use = ImmutableSet.of();

      public Builder uuid(String uuid) {
         this.uuid = uuid;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder use(Iterable<String> use) {
         this.use = ImmutableSet.copyOf(checkNotNull(use, "use"));
         return this;
      }

      public Item build() {
         return new Item(uuid, name, use);
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((name == null) ? 0 : name.hashCode());
         result = prime * result + ((use == null) ? 0 : use.hashCode());
         result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
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
         Builder other = (Builder) obj;
         if (name == null) {
            if (other.name != null)
               return false;
         } else if (!name.equals(other.name))
            return false;
         if (use == null) {
            if (other.use != null)
               return false;
         } else if (!use.equals(other.use))
            return false;
         if (uuid == null) {
            if (other.uuid != null)
               return false;
         } else if (!uuid.equals(other.uuid))
            return false;
         return true;
      }
   }

   @Nullable
   protected final String uuid;
   protected final String name;
   protected final Set<String> use;

   public Item(@Nullable String uuid, String name, Iterable<String> use) {
      this.uuid = uuid;
      this.name = checkNotNull(name, "name");
      this.use = ImmutableSet.copyOf(checkNotNull(use, "use"));
   }

   /**
    * 
    * @return uuid of the item.
    */
   @Nullable
   public String getUuid() {
      return uuid;
   }

   /**
    * 
    * @return name of the item
    */
   public String getName() {
      return name;
   }

   /**
    * 
    * @return list of use
    */
   public Set<String> getUse() {
      return use;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((use == null) ? 0 : use.hashCode());
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
      Item other = (Item) obj;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (use == null) {
         if (other.use != null)
            return false;
      } else if (!use.equals(other.use))
         return false;

      return true;
   }

   @Override
   public String toString() {
      return "[uuid=" + uuid + ", name=" + name + ", use=" + use + "]";
   }

}