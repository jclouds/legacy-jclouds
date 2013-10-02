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
package org.jclouds.atmos.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * User metadata
 * 
 * @author Adrian Cole
 */
public class UserMetadata {
   private final Map<String, String> metadata;
   private final Map<String, String> listableMetadata;
   private final Set<String> tags;
   private final Set<String> listableTags;

   public UserMetadata(Map<String, String> metadata, Map<String, String> listableMetadata, Iterable<String> tags,
         Iterable<String> listableTags) {
      this.metadata = Maps.newLinkedHashMap(checkNotNull(metadata, "metadata"));
      this.listableMetadata = Maps.newLinkedHashMap(checkNotNull(listableMetadata, "listableMetadata"));
      this.tags = Sets.newLinkedHashSet(checkNotNull(tags, "tags"));
      this.listableTags = Sets.newLinkedHashSet(checkNotNull(listableTags, "listableTags"));
   }

   public UserMetadata() {
      this.metadata = Maps.newLinkedHashMap();
      this.listableMetadata = Maps.newLinkedHashMap();
      this.tags = Sets.newLinkedHashSet();
      this.listableTags = Sets.newLinkedHashSet();
   }

   public Map<String, String> getMetadata() {
      return metadata;
   }

   public Map<String, String> getListableMetadata() {
      return listableMetadata;
   }

   public Set<String> getTags() {
      return tags;
   }

   public Set<String> getListableTags() {
      return listableTags;
   }

   @Override
   public String toString() {
      return "[metadata=" + metadata + ", listableMetadata=" + listableMetadata + ", tags=" + tags + ", listableTags="
            + listableTags + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((listableMetadata == null) ? 0 : listableMetadata.hashCode());
      result = prime * result + ((listableTags == null) ? 0 : listableTags.hashCode());
      result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
      result = prime * result + ((tags == null) ? 0 : tags.hashCode());
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
      UserMetadata other = (UserMetadata) obj;
      if (listableMetadata == null) {
         if (other.listableMetadata != null)
            return false;
      } else if (!listableMetadata.equals(other.listableMetadata))
         return false;
      if (listableTags == null) {
         if (other.listableTags != null)
            return false;
      } else if (!listableTags.equals(other.listableTags))
         return false;
      if (metadata == null) {
         if (other.metadata != null)
            return false;
      } else if (!metadata.equals(other.metadata))
         return false;
      if (tags == null) {
         if (other.tags != null)
            return false;
      } else if (!tags.equals(other.tags))
         return false;
      return true;
   }

}
