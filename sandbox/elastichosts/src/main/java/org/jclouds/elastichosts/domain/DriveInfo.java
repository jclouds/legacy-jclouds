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

package org.jclouds.elastichosts.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.jclouds.elastichosts.domain.internal.BaseDrive;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
public class DriveInfo extends BaseDrive {
   public static class Builder extends BaseDrive.Builder {
      private DriveStatus status;
      private String user;
      private Boolean autoexpanding;
      private Integer bits;
      private Set<String> claimed = ImmutableSet.of();
      private String description;
      private String uuid;
      private Set<String> driveType = ImmutableSet.of();
      private String encryptionKey;
      private Boolean free;
      private String imaging;
      private String installNotes;
      private String os;
      private Long readBytes;
      private Long readRequests;
      private DriveType type;
      private URI url;
      private Set<String> use = ImmutableSet.of();
      private Long writeBytes;
      private Long writeRequests;

      public Builder status(DriveStatus status) {
         this.status = status;
         return this;
      }

      public Builder user(String user) {
         this.user = user;
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

      public Builder claimed(Iterable<String> claimed) {
         this.claimed = ImmutableSet.copyOf(checkNotNull(claimed, "claimed"));
         return this;
      }

      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public Builder uuid(String uuid) {
         this.uuid = uuid;
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

      public Builder imaging(String imaging) {
         this.imaging = imaging;
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

      public Builder readBytes(Long readBytes) {
         this.readBytes = readBytes;
         return this;
      }

      public Builder readRequests(Long readRequests) {
         this.readRequests = readRequests;
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

      public Builder use(Iterable<String> use) {
         this.use = ImmutableSet.copyOf(checkNotNull(use, "use"));
         return this;
      }

      public Builder writeBytes(Long writeBytes) {
         this.writeBytes = writeBytes;
         return this;
      }

      public Builder writeRequests(Long writeRequests) {
         this.writeRequests = writeRequests;
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
      public Builder encryptionCipher(String encryptionCipher) {
         return Builder.class.cast(super.encryptionCipher(encryptionCipher));
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

      /**
       * {@inheritDoc}
       */
      @Override
      public DriveInfo build() {
         return new DriveInfo(status, user, autoexpanding, bits, claimed, claimType, description, uuid, driveType,
               encryptionCipher, encryptionKey, free, imaging, installNotes, name, os, readers, readBytes,
               readRequests, size, tags, type, url, use, userMetadata, writeBytes, writeRequests);
      }
   }

   private final DriveStatus status;
   private final String user;
   @Nullable
   private final Boolean autoexpanding;
   @Nullable
   private final Integer bits;
   private final Set<String> claimed;
   @Nullable
   private final String description;
   @Nullable
   private final String uuid;
   @Nullable
   private final Set<String> driveType;
   @Nullable
   private final String encryptionKey;
   @Nullable
   private final Boolean free;
   @Nullable
   private final String imaging;
   @Nullable
   private final String installNotes;
   @Nullable
   private final String os;
   @Nullable
   private final Long readBytes;
   @Nullable
   private final Long readRequests;
   @Nullable
   private final DriveType type;
   @Nullable
   private final URI url;
   @Nullable
   private final Set<String> use;
   @Nullable
   private final Long writeBytes;
   @Nullable
   private final Long writeRequests;

   public DriveInfo(DriveStatus status, String user, Boolean autoexpanding, Integer bits, Iterable<String> claimed,
         ClaimType claimType, String description, String drive, Iterable<String> driveType, String encryptionCipher,
         String encryptionKey, Boolean free, String imaging, String installNotes, String name, String os,
         Iterable<String> readers, Long readBytes, Long readRequests, Long size, Iterable<String> tags, DriveType type,
         URI url, Iterable<String> use, Map<String, String> userMetadata, Long writeBytes, Long writeRequests) {
      super(name, size, claimType, readers, tags, userMetadata, encryptionCipher);
      this.status = status;
      this.user = user;
      this.autoexpanding = autoexpanding;
      this.bits = bits;
      this.claimed = ImmutableSet.copyOf(claimed);
      this.description = description;
      this.uuid = drive;
      this.driveType = ImmutableSet.copyOf(driveType);
      this.encryptionKey = encryptionKey;
      this.free = free;
      this.imaging = imaging;
      this.installNotes = installNotes;
      this.os = os;
      this.readBytes = readBytes;
      this.readRequests = readRequests;
      this.type = type;
      this.url = url;
      this.use = ImmutableSet.copyOf(use);
      this.writeBytes = writeBytes;
      this.writeRequests = writeRequests;
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

   // TODO
   public Boolean getAutoexpanding() {
      return autoexpanding;
   }

   // TODO
   public Integer getBits() {
      return bits;
   }

   /**
    * 
    * @return if drive is in use by a server, values are the server uuids
    */
   public Set<String> getClaimed() {
      return claimed;
   }

   // TODO
   public String getDescription() {
      return description;
   }

   /**
    * 
    * @return uuid of the drive.
    */
   public String getUuid() {
      return uuid;
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

   /**
    * 
    * @return percentage completed of drive imaging if this is underway, or 'queued' if waiting for
    *         another imaging operation to complete first
    */
   public String getImaging() {
      return imaging;
   }

   // TODO
   public String getInstallNotes() {
      return installNotes;
   }

   // TODO
   public String getOs() {
      return os;
   }

   /**
    * 
    * @return Cumulative i/o byte/request count for each drive
    */
   public Long getReadBytes() {
      return readBytes;
   }

   /**
    * 
    * @return Cumulative i/o byte/request count for each drive
    */
   public Long getReadRequests() {
      return readRequests;
   }

   // TODO
   public DriveType getType() {
      return type;
   }

   // TODO

   public URI getUrl() {
      return url;
   }

   // TODO is this the same as tags?
   public Set<String> getUse() {
      return use;
   }

   /**
    * 
    * @return Cumulative i/o byte/request count for each drive
    */
   public Long getWriteBytes() {
      return writeBytes;
   }

   /**
    * 
    * @return Cumulative i/o byte/request count for each drive
    */
   public Long getWriteRequests() {
      return writeRequests;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((autoexpanding == null) ? 0 : autoexpanding.hashCode());
      result = prime * result + ((bits == null) ? 0 : bits.hashCode());
      result = prime * result + ((claimed == null) ? 0 : claimed.hashCode());
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
      result = prime * result + ((driveType == null) ? 0 : driveType.hashCode());
      result = prime * result + ((encryptionKey == null) ? 0 : encryptionKey.hashCode());
      result = prime * result + ((free == null) ? 0 : free.hashCode());
      result = prime * result + ((imaging == null) ? 0 : imaging.hashCode());
      result = prime * result + ((installNotes == null) ? 0 : installNotes.hashCode());
      result = prime * result + ((os == null) ? 0 : os.hashCode());
      result = prime * result + ((readBytes == null) ? 0 : readBytes.hashCode());
      result = prime * result + ((readRequests == null) ? 0 : readRequests.hashCode());
      result = prime * result + ((status == null) ? 0 : status.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      result = prime * result + ((url == null) ? 0 : url.hashCode());
      result = prime * result + ((use == null) ? 0 : use.hashCode());
      result = prime * result + ((user == null) ? 0 : user.hashCode());
      result = prime * result + ((writeBytes == null) ? 0 : writeBytes.hashCode());
      result = prime * result + ((writeRequests == null) ? 0 : writeRequests.hashCode());
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
      if (uuid == null) {
         if (other.uuid != null)
            return false;
      } else if (!uuid.equals(other.uuid))
         return false;
      if (driveType == null) {
         if (other.driveType != null)
            return false;
      } else if (!driveType.equals(other.driveType))
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
      if (os == null) {
         if (other.os != null)
            return false;
      } else if (!os.equals(other.os))
         return false;
      if (readBytes == null) {
         if (other.readBytes != null)
            return false;
      } else if (!readBytes.equals(other.readBytes))
         return false;
      if (readRequests == null) {
         if (other.readRequests != null)
            return false;
      } else if (!readRequests.equals(other.readRequests))
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
      if (use == null) {
         if (other.use != null)
            return false;
      } else if (!use.equals(other.use))
         return false;
      if (user == null) {
         if (other.user != null)
            return false;
      } else if (!user.equals(other.user))
         return false;
      if (writeBytes == null) {
         if (other.writeBytes != null)
            return false;
      } else if (!writeBytes.equals(other.writeBytes))
         return false;
      if (writeRequests == null) {
         if (other.writeRequests != null)
            return false;
      } else if (!writeRequests.equals(other.writeRequests))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[name=" + name + ", size=" + size + ", claimType=" + claimType + ", readers=" + readers + ", tags="
            + tags + ", userMetadata=" + userMetadata + ", encryptionCipher=" + encryptionCipher + ", status=" + status
            + ", user=" + user + ", autoexpanding=" + autoexpanding + ", bits=" + bits + ", claimed=" + claimed
            + ", description=" + description + ", drive=" + uuid + ", driveType=" + driveType + ", encryptionKey="
            + encryptionKey + ", free=" + free + ", imaging=" + imaging + ", installNotes=" + installNotes + ", os="
            + os + ", readBytes=" + readBytes + ", readRequests=" + readRequests + ", type=" + type + ", url=" + url
            + ", use=" + use + ", writeBytes=" + writeBytes + ", writeRequests=" + writeRequests + "]";
   }

}