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

/**
 * 
 * @author Adrian Cole
 */
public abstract class Device {
   public static abstract class Builder {
      protected String uuid;
      protected MediaType mediaType = MediaType.DISK;

      public Builder mediaType(MediaType mediaType) {
         this.mediaType = mediaType;
         return this;
      }

      public Builder uuid(String uuid) {
         this.uuid = uuid;
         return this;
      }

      public abstract Device build();
   }

   protected final String driveUuid;
   protected final MediaType mediaType;

   public Device(String driveUuid, MediaType mediaType) {
      this.driveUuid = checkNotNull(driveUuid, "driveUuid");
      this.mediaType = checkNotNull(mediaType, "mediaType");
   }

   /**
    * id generated based on the device bus, unit, and/or index numbers;
    */
   public abstract String getId();

   /**
    * 
    * @return Drive UUID to connect as specified device.
    */
   public String getDriveUuid() {
      return driveUuid;
   }

   /**
    * 
    * @return set to 'cdrom' to simulate a cdrom, set to 'disk' or leave unset to simulate a hard
    *         disk.
    */
   public MediaType getMediaType() {
      return mediaType;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((driveUuid == null) ? 0 : driveUuid.hashCode());
      result = prime * result + ((mediaType == null) ? 0 : mediaType.hashCode());
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
      Device other = (Device) obj;
      if (driveUuid == null) {
         if (other.driveUuid != null)
            return false;
      } else if (!driveUuid.equals(other.driveUuid))
         return false;
      if (mediaType != other.mediaType)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[driveUuid=" + driveUuid + ", mediaType=" + mediaType + "]";
   }
}