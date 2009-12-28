/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.aws.ec2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * 
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-CreateVolume.html"
 *      />
 * @author Adrian Cole
 */
public class Volume implements Comparable<Volume> {

   public static enum Status {
      CREATING, AVAILABLE, IN_USE, DELETING;
      public String value() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, name());
      }

      @Override
      public String toString() {
         return value();
      }

      public static Status fromValue(String status) {
         return valueOf(CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(
                  status, "status")));
      }
   }

   public static class Attachment {
      public static enum Status {
         ATTACHING, ATTACHED, DETACHING, DETACHED;
         public String value() {
            return name().toLowerCase();
         }

         @Override
         public String toString() {
            return value();
         }

         public static Status fromValue(String status) {
            return valueOf(checkNotNull(status, "status").toUpperCase());
         }
      }

      private final String volumeId;
      private final String instanceId;
      private final String device;
      private final Status status;
      private final Date attachTime;

      public Attachment(String volumeId, String instanceId, String device, Status status,
               Date attachTime) {
         this.volumeId = volumeId;
         this.instanceId = instanceId;
         this.device = device;
         this.status = status;
         this.attachTime = attachTime;
      }

      public String getVolumeId() {
         return volumeId;
      }

      public String getInstanceId() {
         return instanceId;
      }

      public String getDevice() {
         return device;
      }

      public Status getStatus() {
         return status;
      }

      public Date getAttachTime() {
         return attachTime;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((attachTime == null) ? 0 : attachTime.hashCode());
         result = prime * result + ((device == null) ? 0 : device.hashCode());
         result = prime * result + ((instanceId == null) ? 0 : instanceId.hashCode());
         result = prime * result + ((status == null) ? 0 : status.hashCode());
         result = prime * result + ((volumeId == null) ? 0 : volumeId.hashCode());
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
         Attachment other = (Attachment) obj;
         if (attachTime == null) {
            if (other.attachTime != null)
               return false;
         } else if (!attachTime.equals(other.attachTime))
            return false;
         if (device == null) {
            if (other.device != null)
               return false;
         } else if (!device.equals(other.device))
            return false;
         if (instanceId == null) {
            if (other.instanceId != null)
               return false;
         } else if (!instanceId.equals(other.instanceId))
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
         return true;
      }

      @Override
      public String toString() {
         return "Attachment [attachTime=" + attachTime + ", device=" + device + ", instanceId="
                  + instanceId + ", status=" + status + ", volumeId=" + volumeId + "]";
      }

   }

   private final String id;
   private final int size;
   @Nullable
   private final String snapshotId;
   private final AvailabilityZone availabilityZone;
   private final Status status;
   private final Date createTime;
   private final Set<Attachment> attachments = Sets.newLinkedHashSet();

   public Volume(String id, int size, String snapshotId, AvailabilityZone availabilityZone,
            org.jclouds.aws.ec2.domain.Volume.Status status, Date createTime,
            Iterable<Attachment> attachments) {
      this.id = id;
      this.size = size;
      this.snapshotId = snapshotId;
      this.availabilityZone = availabilityZone;
      this.status = status;
      this.createTime = createTime;
      Iterables.addAll(this.attachments, attachments);
   }

   public String getId() {
      return id;
   }

   public int getSize() {
      return size;
   }

   public String getSnapshotId() {
      return snapshotId;
   }

   public AvailabilityZone getAvailabilityZone() {
      return availabilityZone;
   }

   public Status getStatus() {
      return status;
   }

   public Date getCreateTime() {
      return createTime;
   }

   public Set<Attachment> getAttachments() {
      return attachments;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((attachments == null) ? 0 : attachments.hashCode());
      result = prime * result + ((availabilityZone == null) ? 0 : availabilityZone.hashCode());
      result = prime * result + ((createTime == null) ? 0 : createTime.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + size;
      result = prime * result + ((snapshotId == null) ? 0 : snapshotId.hashCode());
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
      Volume other = (Volume) obj;
      if (attachments == null) {
         if (other.attachments != null)
            return false;
      } else if (!attachments.equals(other.attachments))
         return false;
      if (availabilityZone == null) {
         if (other.availabilityZone != null)
            return false;
      } else if (!availabilityZone.equals(other.availabilityZone))
         return false;
      if (createTime == null) {
         if (other.createTime != null)
            return false;
      } else if (!createTime.equals(other.createTime))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (size != other.size)
         return false;
      if (snapshotId == null) {
         if (other.snapshotId != null)
            return false;
      } else if (!snapshotId.equals(other.snapshotId))
         return false;
      if (status == null) {
         if (other.status != null)
            return false;
      } else if (!status.equals(other.status))
         return false;
      return true;
   }

   @Override
   public int compareTo(Volume that) {
      return id.compareTo(that.id);
   }

   @Override
   public String toString() {
      return "Volume [attachments=" + attachments + ", availabilityZone=" + availabilityZone
               + ", createTime=" + createTime + ", id=" + id + ", size=" + size + ", snapshotId="
               + snapshotId + ", status=" + status + "]";
   }
}