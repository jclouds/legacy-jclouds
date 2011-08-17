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

import java.util.Date;

import org.jclouds.ec2.domain.Attachment.Status;

/**
 * 
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-ItemType-RunningInstancesItemType.html"
 *      />
 * @author Adrian Cole
 */
public class BlockDevice {
   private final String volumeId;
   private final Attachment.Status attachmentStatus;
   private final Date attachTime;
   private final boolean deleteOnTermination;

   public BlockDevice(String volumeId, Status attachmentStatus, Date attachTime, boolean deleteOnTermination) {
      this.volumeId = volumeId;
      this.attachmentStatus = attachmentStatus;
      this.attachTime = attachTime;
      this.deleteOnTermination = deleteOnTermination;
   }

   public BlockDevice(String volumeId, boolean deleteOnTermination) {
      this(volumeId, null, null, deleteOnTermination);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((attachTime == null) ? 0 : attachTime.hashCode());
      result = prime * result + ((attachmentStatus == null) ? 0 : attachmentStatus.hashCode());
      result = prime * result + (deleteOnTermination ? 1231 : 1237);
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
      BlockDevice other = (BlockDevice) obj;
      if (attachTime == null) {
         if (other.attachTime != null)
            return false;
      } else if (!attachTime.equals(other.attachTime))
         return false;
      if (attachmentStatus == null) {
         if (other.attachmentStatus != null)
            return false;
      } else if (!attachmentStatus.equals(other.attachmentStatus))
         return false;
      if (deleteOnTermination != other.deleteOnTermination)
         return false;
      if (volumeId == null) {
         if (other.volumeId != null)
            return false;
      } else if (!volumeId.equals(other.volumeId))
         return false;
      return true;
   }

   public String getVolumeId() {
      return volumeId;
   }

   public Attachment.Status getAttachmentStatus() {
      return attachmentStatus;
   }

   public Date getAttachTime() {
      return attachTime;
   }

   public boolean isDeleteOnTermination() {
      return deleteOnTermination;
   }

   @Override
   public String toString() {
      return "[volumeId=" + volumeId + ", attachmentStatus=" + attachmentStatus + ", attachTime="
            + attachTime + ", deleteOnTermination=" + deleteOnTermination + "]";
   }

}