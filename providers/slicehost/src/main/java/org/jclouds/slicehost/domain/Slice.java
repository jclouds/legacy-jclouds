/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.slicehost.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.CaseFormat;

/**
 * A slice is a virtual machine instance in the Slicehost system. Flavor and image are requisite
 * elements when creating a slice.
 * 
 * @author Adrian Cole
 */
public class Slice {
   /**
    * The current status of the slice
    * 
    */
   public enum Status {

      ACTIVE, BUILD, REBOOT, HARD_REBOOT, TERMINATED, UNRECOGNIZED;

      public String value() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_UNDERSCORE, name());
      }

      @Override
      public String toString() {
         return value();
      }

      public static Status fromValue(String state) {
         try {
            return valueOf(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(state, "state")));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   private final int id;
   private final String name;
   private final int flavorId;
   @Nullable
   private final Integer imageId;
   @Nullable
   private final Integer backupId;
   private final Status status;
   @Nullable
   private final Integer progress;
   private final float bandwidthIn;
   private final float bandwidthOut;
   private final Set<String> addresses;
   @Nullable
   private final String rootPassword;

   public Slice(int id, String name, int flavorId, @Nullable Integer imageId, @Nullable Integer backupId,
            Status status, @Nullable Integer progress, float bandwidthIn, float bandwidthOut, Set<String> addresses,
            @Nullable String rootPassword) {
      this.id = id;
      this.name = name;
      this.flavorId = flavorId;
      this.imageId = imageId;
      this.backupId = backupId;
      this.status = status;
      this.progress = progress;
      this.bandwidthIn = bandwidthIn;
      this.bandwidthOut = bandwidthOut;
      this.addresses = addresses;
      this.rootPassword = rootPassword;
   }

   /**
    * @return unique id within slicehost
    */
   public int getId() {
      return id;
   }

   /**
    * @return A string to identify the slice
    */
   public String getName() {
      return name;
   }

   /**
    * @return the flavor of a slice
    */
   public int getFlavorId() {
      return flavorId;
   }

   /**
    * @return the image used to create the slice or null
    */
   public Integer getImageId() {
      return imageId;
   }

   /**
    * @return The current status of the slice
    */
   public Status getStatus() {
      return status;
   }

   /**
    * @return The percentage of current action in percentage
    */
   public Integer getProgress() {
      return progress;
   }

   /**
    * @return The incoming bandwidth total for this billing cycle, in Gigabytes
    */
   public float getBandwidthIn() {
      return bandwidthIn;
   }

   /**
    * @return The outgoing bandwidth total for this billing cycle, in Gigabytes
    */
   public float getBandwidthOut() {
      return bandwidthOut;
   }

   /**
    * @return an array of strings representing the Slice's IPs, including private IPs
    */
   public Set<String> getAddresses() {
      return addresses;
   }

   /**
    * @return root password, if just created
    */
   public String getRootPassword() {
      return rootPassword;
   }

   /**
    * @return backup used to create this instance or null
    */
   public Integer getBackupId() {
      return backupId;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((addresses == null) ? 0 : addresses.hashCode());
      result = prime * result + ((backupId == null) ? 0 : backupId.hashCode());
      result = prime * result + Float.floatToIntBits(bandwidthIn);
      result = prime * result + Float.floatToIntBits(bandwidthOut);
      result = prime * result + flavorId;
      result = prime * result + id;
      result = prime * result + ((imageId == null) ? 0 : imageId.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((progress == null) ? 0 : progress.hashCode());
      result = prime * result + ((rootPassword == null) ? 0 : rootPassword.hashCode());
      result = prime * result + ((status == null) ? 0 : status.hashCode());
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
      Slice other = (Slice) obj;
      if (addresses == null) {
         if (other.addresses != null)
            return false;
      } else if (!addresses.equals(other.addresses))
         return false;
      if (backupId == null) {
         if (other.backupId != null)
            return false;
      } else if (!backupId.equals(other.backupId))
         return false;
      if (Float.floatToIntBits(bandwidthIn) != Float.floatToIntBits(other.bandwidthIn))
         return false;
      if (Float.floatToIntBits(bandwidthOut) != Float.floatToIntBits(other.bandwidthOut))
         return false;
      if (flavorId != other.flavorId)
         return false;
      if (id != other.id)
         return false;
      if (imageId == null) {
         if (other.imageId != null)
            return false;
      } else if (!imageId.equals(other.imageId))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (progress == null) {
         if (other.progress != null)
            return false;
      } else if (!progress.equals(other.progress))
         return false;
      if (rootPassword == null) {
         if (other.rootPassword != null)
            return false;
      } else if (!rootPassword.equals(other.rootPassword))
         return false;
      if (status == null) {
         if (other.status != null)
            return false;
      } else if (!status.equals(other.status))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + id + ", name=" + name + ", flavorId=" + flavorId + ", imageId=" + imageId + ", backupId="
               + backupId + ", status=" + status + ", progress=" + progress + ", bandwidthIn=" + bandwidthIn
               + ", bandwidthOut=" + bandwidthOut + ", addresses=" + addresses + ", rootPassword="
               + (rootPassword != null) + "]";
   }

}
