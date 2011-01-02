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

import java.net.URI;
import java.util.Set;

import javax.annotation.Nullable;

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
      private Boolean autoexpanding;
      private Integer bits;
      private String description;
      private Set<String> driveType = ImmutableSet.of();
      private String encryptionKey;
      private Boolean free;
      private String installNotes;
      private String os;
      private DriveType type;
      private URI url;

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

      public Builder autoexpanding(Boolean autoexpanding) {
         this.autoexpanding = autoexpanding;
         return this;
      }

      public Builder bits(Integer bits) {
         this.bits = bits;
         return this;
      }

      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public Builder driveType(Iterable<String> driveType) {
         this.driveType = ImmutableSet.copyOf(checkNotNull(driveType, "driveType"));
         return this;
      }

      public Builder encryptionKey(String encryptionKey) {
         this.encryptionKey = encryptionKey;
         return this;
      }

      public Builder free(Boolean free) {
         this.free = free;
         return this;
      }

      public Builder installNotes(String installNotes) {
         this.installNotes = installNotes;
         return this;
      }

      public Builder os(String os) {
         this.os = os;
         return this;
      }

      public Builder type(DriveType type) {
         this.type = type;
         return this;
      }

      public Builder url(URI url) {
         this.url = url;
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
      public Builder use(Iterable<String> use) {
         return Builder.class.cast(super.use(use));
      }

      public static Builder fromDriveInfo(DriveInfo in) {
         return new Builder().uuid(in.getUuid()).name(in.getName()).size(in.getSize()).claimType(in.getClaimType())
               .readers(in.getReaders()).use(in.getUse()).status(in.getStatus()).user(in.getUser())
               .claimed(in.getClaimed()).encryptionCipher(in.getEncryptionCipher()).imaging(in.getImaging())
               .metrics(in.getMetrics()).autoexpanding(in.getAutoexpanding()).bits(in.getBits())
               .description(in.getDescription()).encryptionKey(in.getEncryptionKey()).free(in.getFree())
               .installNotes(in.getInstallNotes()).type(in.getType()).url(in.getUrl());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public DriveInfo build() {
         return new DriveInfo(uuid, name, size, claimType, readers, use, status, user, claimed, encryptionCipher,
               imaging, metrics, autoexpanding, bits, description, driveType, encryptionKey, free, installNotes, os,
               type, url);
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
   private final Boolean autoexpanding;
   private final Integer bits;
   private final String description;
   private final ImmutableSet<String> driveType;
   private final String encryptionKey;
   private final Boolean free;
   private final String installNotes;
   private final String os;
   private final DriveType type;
   private final URI url;

   public DriveInfo(String uuid, String name, long size, ClaimType claimType, Iterable<String> readers,
         Iterable<String> use, DriveStatus status, String user, Set<String> claimed, String encryptionCipher,
         String imaging, DriveMetrics metrics, Boolean autoexpanding, Integer bits, String description,
         Iterable<String> driveType, String encryptionKey, Boolean free, String installNotes, String os,
         DriveType type, URI url) {
      super(uuid, name, size, claimType, readers, use);
      this.status = status;
      this.user = user;
      this.claimed = ImmutableSet.copyOf(checkNotNull(claimed, "claimed"));
      this.encryptionCipher = encryptionCipher;
      this.imaging = imaging;
      this.metrics = checkNotNull(metrics, "metrics");
      this.autoexpanding = autoexpanding;
      this.bits = bits;
      this.description = description;
      this.driveType = ImmutableSet.copyOf(driveType);
      this.encryptionKey = encryptionKey;
      this.free = free;
      this.installNotes = installNotes;
      this.os = os;
      this.type = type;
      this.url = url;
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

   // TODO
   public Boolean getAutoexpanding() {
      return autoexpanding;
   }

   // TODO
   public Integer getBits() {
      return bits;
   }

   // TODO undocumented
   public String getDescription() {
      return description;
   }

   // TODO
   public Set<String> getDriveType() {
      return driveType;
   }

   // TODO
   public String getEncryptionKey() {
      return encryptionKey;
   }

   // TODO
   public Boolean getFree() {
      return free;
   }

   // TODO
   public String getInstallNotes() {
      return installNotes;
   }

   // TODO
   public String getOs() {
      return os;
   }

   // TODO
   public DriveType getType() {
      return type;
   }

   public URI getUrl() {
      return url;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((autoexpanding == null) ? 0 : autoexpanding.hashCode());
      result = prime * result + ((bits == null) ? 0 : bits.hashCode());
      result = prime * result + ((claimed == null) ? 0 : claimed.hashCode());
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((driveType == null) ? 0 : driveType.hashCode());
      result = prime * result + ((encryptionCipher == null) ? 0 : encryptionCipher.hashCode());
      result = prime * result + ((encryptionKey == null) ? 0 : encryptionKey.hashCode());
      result = prime * result + ((free == null) ? 0 : free.hashCode());
      result = prime * result + ((imaging == null) ? 0 : imaging.hashCode());
      result = prime * result + ((installNotes == null) ? 0 : installNotes.hashCode());
      result = prime * result + ((metrics == null) ? 0 : metrics.hashCode());
      result = prime * result + ((os == null) ? 0 : os.hashCode());
      result = prime * result + ((status == null) ? 0 : status.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      result = prime * result + ((url == null) ? 0 : url.hashCode());
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
      if (autoexpanding == null) {
         if (other.autoexpanding != null)
            return false;
      } else if (!autoexpanding.equals(other.autoexpanding))
         return false;
      if (bits == null) {
         if (other.bits != null)
            return false;
      } else if (!bits.equals(other.bits))
         return false;
      if (claimed == null) {
         if (other.claimed != null)
            return false;
      } else if (!claimed.equals(other.claimed))
         return false;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (driveType == null) {
         if (other.driveType != null)
            return false;
      } else if (!driveType.equals(other.driveType))
         return false;
      if (encryptionCipher == null) {
         if (other.encryptionCipher != null)
            return false;
      } else if (!encryptionCipher.equals(other.encryptionCipher))
         return false;
      if (encryptionKey == null) {
         if (other.encryptionKey != null)
            return false;
      } else if (!encryptionKey.equals(other.encryptionKey))
         return false;
      if (free == null) {
         if (other.free != null)
            return false;
      } else if (!free.equals(other.free))
         return false;
      if (imaging == null) {
         if (other.imaging != null)
            return false;
      } else if (!imaging.equals(other.imaging))
         return false;
      if (installNotes == null) {
         if (other.installNotes != null)
            return false;
      } else if (!installNotes.equals(other.installNotes))
         return false;
      if (metrics == null) {
         if (other.metrics != null)
            return false;
      } else if (!metrics.equals(other.metrics))
         return false;
      if (os == null) {
         if (other.os != null)
            return false;
      } else if (!os.equals(other.os))
         return false;
      if (status != other.status)
         return false;
      if (type != other.type)
         return false;
      if (url == null) {
         if (other.url != null)
            return false;
      } else if (!url.equals(other.url))
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
            + name + ", use=" + use + ", status=" + status + ", user=" + user + ", claimed=" + claimed
            + ", encryptionCipher=" + encryptionCipher + ", imaging=" + imaging + ", metrics=" + metrics + "]";
   }

}