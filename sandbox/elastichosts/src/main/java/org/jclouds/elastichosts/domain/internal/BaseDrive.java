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

package org.jclouds.elastichosts.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.jclouds.elastichosts.domain.ClaimType;

import com.google.common.collect.ImmutableSet;
import com.google.inject.internal.util.ImmutableMap;

/**
 * 
 * @author Adrian Cole
 */
public class BaseDrive {
   public static class Builder {
      protected String name;
      protected long size;
      protected ClaimType claimType = ClaimType.EXCLUSIVE;
      protected Set<String> readers = ImmutableSet.of();
      protected Set<String> tags = ImmutableSet.of();
      protected Map<String, String> userMetadata = ImmutableMap.of();

      public Builder claimType(ClaimType claimType) {
         this.claimType = claimType;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder readers(Iterable<String> readers) {
         this.readers = ImmutableSet.copyOf(checkNotNull(readers, "readers"));
         return this;
      }

      public Builder size(long size) {
         this.size = size;
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

      public BaseDrive build() {
         return new BaseDrive(name, size, claimType, readers, tags, userMetadata);
      }
   }

   protected final String name;
   protected final long size;
   protected final ClaimType claimType;
   protected final Set<String> readers;
   protected final Set<String> tags;
   protected final Map<String, String> userMetadata;

   public BaseDrive(String name, long size, @Nullable ClaimType claimType, Iterable<String> readers,
         Iterable<String> tags, Map<String, String> userMetadata) {
      this.name = checkNotNull(name, "name");
      this.size = size;
      this.claimType = checkNotNull(claimType, "set claimType to exclusive, not null");
      this.readers = ImmutableSet.copyOf(checkNotNull(readers, "readers"));
      this.tags = ImmutableSet.copyOf(checkNotNull(tags, "tags"));
      this.userMetadata = ImmutableMap.copyOf(checkNotNull(userMetadata, "userMetadata"));
   }

   /**
    * 
    * @return either 'exclusive' (the default) or 'shared' to allow multiple servers to access a
    *         drive simultaneously
    */
   @Nullable
   public ClaimType getClaimType() {
      return claimType;
   }

   /**
    * 
    * @return Drive name
    */
   public String getName() {
      return name;
   }

   /**
    * 
    * @return list of users allowed to read from a drive or 'ffffffff-ffff-ffff-ffff-ffffffffffff'
    *         for all users
    */
   public Set<String> getReaders() {
      return readers;
   }

   /**
    * 
    * @return size of drive in bytes
    */
   public long getSize() {
      return size;
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
      result = prime * result + ((claimType == null) ? 0 : claimType.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((readers == null) ? 0 : readers.hashCode());
      result = prime * result + (int) (size ^ (size >>> 32));
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
      BaseDrive other = (BaseDrive) obj;
      if (claimType != other.claimType)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (readers == null) {
         if (other.readers != null)
            return false;
      } else if (!readers.equals(other.readers))
         return false;
      if (size != other.size)
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
      return "[name=" + name + ", size=" + size + ", claimType=" + claimType + ", readers=" + readers + ", tags="
            + tags + ", userMetadata=" + userMetadata + "]";
   }

}