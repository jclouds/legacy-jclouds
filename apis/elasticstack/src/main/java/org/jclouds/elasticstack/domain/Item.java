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
package org.jclouds.elasticstack.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
public class Item {
   public static class Builder {
      protected String uuid;
      protected String name;
      protected Set<String> tags = ImmutableSet.of();
      protected Map<String, String> userMetadata = ImmutableMap.of();

      public Builder uuid(String uuid) {
         this.uuid = uuid;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder tags(Iterable<String> tags) {
         this.tags = ImmutableSet.copyOf(checkNotNull(tags, "tags"));
         return this;
      }

      public Builder userMetadata(Map<String, String> userMetadata) {
         this.userMetadata = ImmutableMap.copyOf(checkNotNull(userMetadata, "userMetadata"));
         return this;
      }

      public Item build() {
         return new Item(uuid, name, tags, userMetadata);
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((name == null) ? 0 : name.hashCode());
         result = prime * result + ((tags == null) ? 0 : tags.hashCode());
         result = prime * result + ((userMetadata == null) ? 0 : userMetadata.hashCode());
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
         if (tags == null) {
            if (other.tags != null)
               return false;
         } else if (!tags.equals(other.tags))
            return false;
         if (userMetadata == null) {
            if (other.userMetadata != null)
               return false;
         } else if (!userMetadata.equals(other.userMetadata))
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
   protected final Set<String> tags;
   protected final Map<String, String> userMetadata;

   public Item(@Nullable String uuid, String name, Iterable<String> tags, Map<String, String> userMetadata) {
      this.uuid = uuid;
      this.name = checkNotNull(name, "name");
      this.tags = ImmutableSet.copyOf(checkNotNull(tags, "tags"));
      this.userMetadata = ImmutableMap.copyOf(checkNotNull(userMetadata, "userMetadata"));
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
    * @return list of tags
    */
   public Set<String> getTags() {
      return tags;
   }

   /**
    * 
    * @return user-defined KEY VALUE pairs
    */
   public Map<String, String> getUserMetadata() {
      return userMetadata;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((tags == null) ? 0 : tags.hashCode());
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
      Item other = (Item) obj;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (tags == null) {
         if (other.tags != null)
            return false;
      } else if (!tags.equals(other.tags))
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
      return "[uuid=" + uuid + ", name=" + name + ", tags=" + tags + ", userMetadata=" + userMetadata + "]";
   }

}
