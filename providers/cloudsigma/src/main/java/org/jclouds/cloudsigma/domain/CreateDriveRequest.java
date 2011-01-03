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
public class CreateDriveRequest extends Drive {
   public static class Builder extends Drive.Builder {

      private Set<String> avoid = ImmutableSet.of();

      @Nullable
      private String encryptionCipher;

      public Builder avoid(Iterable<String> avoid) {
         this.avoid = ImmutableSet.copyOf(checkNotNull(avoid, "avoid"));
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder claimType(ClaimType claimType) {
         return Builder.class.cast(super.claimType(claimType));
      }

      public Builder encryptionCipher(String encryptionCipher) {
         this.encryptionCipher = encryptionCipher;
         return this;
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
      public Builder readers(Iterable<String> readers) {
         return Builder.class.cast(super.readers(readers));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder size(long size) {
         return Builder.class.cast(super.size(size));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder use(Iterable<String> use) {
         return Builder.class.cast(super.use(use));
      }

      public CreateDriveRequest build() {
         return new CreateDriveRequest(name, size, claimType, readers, use, encryptionCipher, avoid);
      }
   }

   private final Set<String> avoid;
   @Nullable
   private final String encryptionCipher;

   public CreateDriveRequest(String name, long size, @Nullable ClaimType claimType, Iterable<String> readers,
         Iterable<String> use, @Nullable String encryptionCipher, Iterable<String> avoid) {
      super(null, name, size, claimType, readers, use);
      this.encryptionCipher = encryptionCipher;
      this.avoid = ImmutableSet.copyOf(checkNotNull(avoid, "avoid"));
   }

   /**
    * 
    * @return list of existing drives to ensure this new drive is created on physical different
    *         hardware than those existing drives
    */
   public Set<String> getAvoid() {
      return avoid;
   }

   /**
    * 
    * @return either 'none' or 'aes-xts-plain' (the default)
    */
   @Nullable
   public String getEncryptionCipher() {
      return encryptionCipher;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((avoid == null) ? 0 : avoid.hashCode());
      result = prime * result + ((encryptionCipher == null) ? 0 : encryptionCipher.hashCode());
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
      CreateDriveRequest other = (CreateDriveRequest) obj;
      if (avoid == null) {
         if (other.avoid != null)
            return false;
      } else if (!avoid.equals(other.avoid))
         return false;
      if (encryptionCipher == null) {
         if (other.encryptionCipher != null)
            return false;
      } else if (!encryptionCipher.equals(other.encryptionCipher))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[name=" + name + ", size=" + size + ", claimType=" + claimType + ", readers=" + readers + ", use=" + use
            + ", avoid=" + avoid + ", encryptionCipher=" + encryptionCipher + "]";
   }
}