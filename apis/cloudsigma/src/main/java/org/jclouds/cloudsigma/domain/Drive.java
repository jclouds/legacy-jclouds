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
package org.jclouds.cloudsigma.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
public class Drive extends Item {
   public static class Builder extends Item.Builder {
      protected long size;
      protected ClaimType claimType = ClaimType.EXCLUSIVE;
      protected Set<String> tags = ImmutableSet.of();
      protected Set<String> readers = ImmutableSet.of();

      public Builder claimType(ClaimType claimType) {
         this.claimType = claimType;
         return this;
      }

      public Builder tags(Iterable<String> tags) {
          this.tags = ImmutableSet.copyOf(checkNotNull(tags, "tags"));
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

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder uuid(String uuid) {
         return Builder.class.cast(super.uuid(uuid));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder use(Iterable<String> use) {
         return Builder.class.cast(super.use(use));
      }

      public Drive build() {
         return new Drive(uuid, name, size, claimType, tags, readers, use);
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = super.hashCode();
         result = prime * result + ((claimType == null) ? 0 : claimType.hashCode());
         result = prime * result + ((tags == null) ? 0 : tags.hashCode());
         result = prime * result + ((readers == null) ? 0 : readers.hashCode());
         result = prime * result + (int) (size ^ (size >>> 32));
         return result;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (!super.equals(obj))
            return false;
         if (getClass() != obj.getClass())
            return false;
         Builder other = (Builder) obj;
         if (claimType != other.claimType)
            return false;
         if (!Objects.equal(tags, other.tags))
             return false;
         if (!Objects.equal(readers, other.readers))
             return false;
         if (size != other.size)
            return false;
         return true;
      }
   }

   protected final long size;
   protected final ClaimType claimType;
   protected final Set<String> tags;
   protected final Set<String> readers;

   public Drive(@Nullable String uuid, String name, long size, @Nullable ClaimType claimType,
         Iterable<String> tags, Iterable<String> readers, Iterable<String> use) {
      super(uuid, name, use);
      this.size = size;
      this.claimType = checkNotNull(claimType, "set claimType to exclusive, not null");
      this.tags = ImmutableSet.copyOf(checkNotNull(tags, "tags"));
      this.readers = ImmutableSet.copyOf(checkNotNull(readers, "readers"));
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
    * @return all tags associated with this drive, both user-specified and "system" tags (e.g. "affinity:ssd")
    */
   public Set<String> getTags() {
       return tags;
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

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((claimType == null) ? 0 : claimType.hashCode());
      result = prime * result + ((tags == null) ? 0 : tags.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((readers == null) ? 0 : readers.hashCode());
      result = prime * result + (int) (size ^ (size >>> 32));
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
      Drive other = (Drive) obj;
      if (claimType != other.claimType)
         return false;
      if (!Objects.equal(tags, other.tags))
          return false;
      if (!Objects.equal(name, other.name))
          return false;
      if (!Objects.equal(readers, other.readers))
          return false;
      if (size != other.size)
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
      return "[uuid=" + uuid + ", name=" + name + ", use=" + use + ", size=" + size + ", claimType=" + claimType
            + ", tags=" + tags + ", readers=" + readers + "]";
   }

}
