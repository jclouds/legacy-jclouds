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
package org.jclouds.ec2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

/**
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-CreateSnapshot.html"
 *      />
 * @author Adrian Cole
 */
public class Snapshot implements Comparable<Snapshot> {
   public static enum Status {
      PENDING, COMPLETED, ERROR, UNRECOGNIZED;
      public String value() {
         return name().toLowerCase();
      }

      @Override
      public String toString() {
         return value();
      }

      public static Status fromValue(String status) {
         try {
            return valueOf(checkNotNull(status, "status").toUpperCase());
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   private final String region;
   private final String id;
   private final String volumeId;
   private final int volumeSize;
   private final Status status;
   private final Date startTime;
   private final int progress;
   private final String ownerId;
   private final String description;
   private final String ownerAlias;

   public Snapshot(String region, String id, String volumeId, int volumeSize, Status status, Date startTime,
            int progress, String ownerId, String description, String ownerAlias) {
      this.region = checkNotNull(region, "region");
      this.id = id;
      this.volumeId = volumeId;
      this.volumeSize = volumeSize;
      this.status = status;
      this.startTime = startTime;
      this.progress = progress;
      this.ownerId = ownerId;
      this.description = description;
      this.ownerAlias = ownerAlias;
   }

   /**
    * Snapshots are tied to Regions and can only be used for volumes within the same Region.
    */
   public String getRegion() {
      return region;
   }

   /**
    * The ID of the snapshot.
    */
   public String getId() {
      return id;
   }

   /**
    * The ID of the volume.
    */
   public String getVolumeId() {
      return volumeId;
   }

   /**
    * The size of the volume, in GiB.
    */
   public int getVolumeSize() {
      return volumeSize;
   }

   /**
    * Snapshot state (e.g., pending, completed, or error)
    */
   public Status getStatus() {
      return status;
   }

   /**
    * Time stamp when the snapshot was initiated.
    */
   public Date getStartTime() {
      return startTime;
   }

   /**
    * The progress of the snapshot, in percentage.
    */
   public int getProgress() {
      return progress;
   }

   /**
    * AWS Access Key ID of the user who owns the snapshot.
    */
   public String getOwnerId() {
      return ownerId;
   }

   /**
    * Description of the snapshot.
    */
   public String getDescription() {
      return description;
   }

   /**
    * The AWS identity alias (e.g., "amazon", "redhat", "self", etc.) or AWS identity ID that owns
    * the AMI.
    */
   public String getOwnerAlias() {
      return ownerAlias;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((ownerAlias == null) ? 0 : ownerAlias.hashCode());
      result = prime * result + ((ownerId == null) ? 0 : ownerId.hashCode());
      result = prime * result + progress;
      result = prime * result + ((region == null) ? 0 : region.hashCode());
      result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
      result = prime * result + ((status == null) ? 0 : status.hashCode());
      result = prime * result + ((volumeId == null) ? 0 : volumeId.hashCode());
      result = prime * result + volumeSize;
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
      Snapshot other = (Snapshot) obj;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (ownerAlias == null) {
         if (other.ownerAlias != null)
            return false;
      } else if (!ownerAlias.equals(other.ownerAlias))
         return false;
      if (ownerId == null) {
         if (other.ownerId != null)
            return false;
      } else if (!ownerId.equals(other.ownerId))
         return false;
      if (progress != other.progress)
         return false;
      if (region == null) {
         if (other.region != null)
            return false;
      } else if (!region.equals(other.region))
         return false;
      if (startTime == null) {
         if (other.startTime != null)
            return false;
      } else if (!startTime.equals(other.startTime))
         return false;
      if (status == null) {
         if (other.status != null)
            return false;
      } else if (!status.equals(other.status))
         return false;
      if (volumeId == null) {
         if (other.volumeId != null)
            return false;
      } else if (!volumeId.equals(other.volumeId))
         return false;
      if (volumeSize != other.volumeSize)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "Snapshot [description=" + description + ", id=" + id + ", ownerAlias=" + ownerAlias + ", ownerId="
               + ownerId + ", progress=" + progress + ", startTime=" + startTime + ", status=" + status + ", volumeId="
               + volumeId + ", volumeSize=" + volumeSize + "]";
   }

   @Override
   public int compareTo(Snapshot o) {
      return startTime.compareTo(o.startTime);
   }

}
