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
package org.jclouds.glesys.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Information about an archive
 *
 * @author Adam Lowe
 * @see <a href= "https://customer.glesys.com/api.php?a=doc#archive_list" />
 */
public class Archive {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromArchive(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String username;
      protected String totalSize;
      protected String freeSize;
      protected boolean locked;

      /**
       * @see Archive#getUsername()
       */
      public T username(String username) {
         this.username = checkNotNull(username, "username");
         return self();
      }

      /**
       * @see Archive#getTotalSize()
       */
      public T totalSize(String totalSize) {
         this.totalSize = checkNotNull(totalSize, "totalSize");
         return self();
      }

      /**
       * @see Archive#getFreeSize()
       */
      public T freeSize(String freeSize) {
         this.freeSize = checkNotNull(freeSize, "freeSize");
         return self();
      }

      /**
       * @see Archive#isLocked()
       */
      public T locked(boolean locked) {
         this.locked = locked;
         return self();
      }

      public Archive build() {
         return new Archive(username, totalSize, freeSize, new GleSYSBoolean(locked));
      }

      public T fromArchive(Archive in) {
         return this
               .username(in.getUsername())
               .totalSize(in.getTotalSize())
               .freeSize(in.getFreeSize())
               .locked(in.isLocked());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String username;
   private final String totalSize;
   private final String freeSize;
   private final boolean locked;

   @ConstructorProperties({
         "username", "sizetotal", "sizefree", "locked"
   })
   protected Archive(String username, String totalSize, String freeSize, GleSYSBoolean locked) {
      this.username = checkNotNull(username, "username");
      this.totalSize = checkNotNull(totalSize, "totalSize");
      this.freeSize = checkNotNull(freeSize, "freeSize");
      this.locked = checkNotNull(locked, "locked").getValue();
   }

   /**
    * @return the name (username) of the archive
    */
   public String getUsername() {
      return this.username;
   }

   /**
    * @return the total size of the archive, ex. "10 GB"
    */
   public String getTotalSize() {
      return this.totalSize;
   }

   /**
    * @return the free space left of the archive
    */
   public String getFreeSize() {
      return this.freeSize;
   }

   /**
    * @return true if the archive is locked
    */
   public boolean isLocked() {
      return this.locked;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(username, totalSize, freeSize, locked);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Archive that = Archive.class.cast(obj);
      return Objects.equal(this.username, that.username)
            && Objects.equal(this.totalSize, that.totalSize)
            && Objects.equal(this.freeSize, that.freeSize)
            && Objects.equal(this.locked, that.locked);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("")
            .add("username", username).add("totalSize", totalSize).add("freeSize", freeSize).add("locked", locked);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
