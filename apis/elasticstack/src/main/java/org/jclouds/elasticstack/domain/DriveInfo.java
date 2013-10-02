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

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
public class DriveInfo extends Drive {
   public static class Builder extends Drive.Builder {

      protected DriveStatus status;
      protected String user;
      protected Set<String> claimed = ImmutableSet.of();
      @Nullable
      protected String encryptionCipher;
      @Nullable
      protected String imaging;
      protected DriveMetrics metrics;

      public Builder status(DriveStatus status) {
         this.status = status;
         return this;
      }

      public Builder user(String user) {
         this.user = user;
         return this;
      }

      public Builder claimed(Iterable<String> claimed) {
         this.claimed = ImmutableSet.copyOf(checkNotNull(claimed, "claimed"));
         return this;
      }

      public Builder imaging(String imaging) {
         this.imaging = imaging;
         return this;
      }

      public Builder metrics(DriveMetrics metrics) {
         this.metrics = metrics;
         return this;
      }

      public Builder encryptionCipher(String encryptionCipher) {
         this.encryptionCipher = encryptionCipher;
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder claimType(ClaimType claimType) {
         return Builder.class.cast(super.claimType(claimType));
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
      public Builder tags(Iterable<String> tags) {
         return Builder.class.cast(super.tags(tags));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder userMetadata(Map<String, String> userMetadata) {
         return Builder.class.cast(super.userMetadata(userMetadata));
      }

      public static Builder fromDriveInfo(DriveInfo driveInfo) {
         return new Builder().uuid(driveInfo.getUuid()).name(driveInfo.getName()).size(driveInfo.getSize())
               .claimType(driveInfo.getClaimType()).readers(driveInfo.getReaders()).tags(driveInfo.getTags())
               .userMetadata(driveInfo.getUserMetadata()).status(driveInfo.getStatus()).user(driveInfo.getUser())
               .claimed(driveInfo.getClaimed()).encryptionCipher(driveInfo.getEncryptionCipher())
               .imaging(driveInfo.getImaging()).metrics(driveInfo.getMetrics());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public DriveInfo build() {
         return new DriveInfo(uuid, name, size, claimType, readers, tags, userMetadata, status, user, claimed,
               encryptionCipher, imaging, metrics);
      }

   }

   protected final DriveStatus status;
   protected final String user;
   protected final Set<String> claimed;
   @Nullable
   protected final String encryptionCipher;
   @Nullable
   protected final String imaging;
   protected final DriveMetrics metrics;

   public DriveInfo(String uuid, String name, long size, ClaimType claimType, Iterable<String> readers,
         Iterable<String> tags, Map<String, String> userMetadata, DriveStatus status, String user, Set<String> claimed,
         String encryptionCipher, String imaging, DriveMetrics metrics) {
      super(uuid, name, size, claimType, readers, tags, userMetadata);
      this.status = status;
      this.user = user;
      this.claimed = ImmutableSet.copyOf(checkNotNull(claimed, "claimed"));
      this.encryptionCipher = encryptionCipher;
      this.imaging = imaging;
      this.metrics = checkNotNull(metrics, "metrics");
   }

   /**
    * 
    * @return current status of the drive
    */
   public DriveStatus getStatus() {
      return status;
   }

   /**
    * 
    * @return owner of the drive
    */
   public String getUser() {
      return user;
   }

   /**
    * 
    * @return if drive is in use by a server, values are the server uuids
    */
   public Set<String> getClaimed() {
      return claimed;
   }

   /**
    * 
    * @return either 'none' or 'aes-xts-plain' (the default)
    */
   @Nullable
   public String getEncryptionCipher() {
      return encryptionCipher;
   }

   /**
    * 
    * @return percentage completed of drive imaging if this is underway, or 'queued' if waiting for
    *         another imaging operation to complete first
    */
   public String getImaging() {
      return imaging;
   }

   /**
    * 
    * @return i/o and request metrics for read and write ops
    */
   public DriveMetrics getMetrics() {
      return metrics;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((claimed == null) ? 0 : claimed.hashCode());
      result = prime * result + ((metrics == null) ? 0 : metrics.hashCode());
      result = prime * result + ((encryptionCipher == null) ? 0 : encryptionCipher.hashCode());
      result = prime * result + ((imaging == null) ? 0 : imaging.hashCode());
      result = prime * result + ((status == null) ? 0 : status.hashCode());
      result = prime * result + ((user == null) ? 0 : user.hashCode());
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
      DriveInfo other = (DriveInfo) obj;
      if (claimed == null) {
         if (other.claimed != null)
            return false;
      } else if (!claimed.equals(other.claimed))
         return false;
      if (metrics == null) {
         if (other.metrics != null)
            return false;
      } else if (!metrics.equals(other.metrics))
         return false;
      if (encryptionCipher == null) {
         if (other.encryptionCipher != null)
            return false;
      } else if (!encryptionCipher.equals(other.encryptionCipher))
         return false;
      if (imaging == null) {
         if (other.imaging != null)
            return false;
      } else if (!imaging.equals(other.imaging))
         return false;
      if (status != other.status)
         return false;
      if (user == null) {
         if (other.user != null)
            return false;
      } else if (!user.equals(other.user))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[size=" + size + ", claimType=" + claimType + ", readers=" + readers + ", uuid=" + uuid + ", name="
            + name + ", tags=" + tags + ", userMetadata=" + userMetadata + ", status=" + status + ", user=" + user
            + ", claimed=" + claimed + ", encryptionCipher=" + encryptionCipher + ", imaging=" + imaging
            + ", metrics=" + metrics + "]";
   }

}
