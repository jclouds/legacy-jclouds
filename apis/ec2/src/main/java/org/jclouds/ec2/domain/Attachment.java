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
 * @author Adrian Cole
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-CreateVolume.html"
 *      />
 */
public class Attachment implements Comparable<Attachment> {
   public static enum Status {
      ATTACHING, ATTACHED, DETACHING, DETACHED, BUSY, UNRECOGNIZED;

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

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String region;
      private String volumeId;
      private String instanceId;
      private String device;
      private Status status;
      private Date attachTime;
      
      public Builder region(String region) {
         this.region = region;
         return this;
      }
      
      public Builder volumeId(String volumeId) {
         this.volumeId = volumeId;
         return this;
      }
      
      public Builder instanceId(String instanceId) {
         this.instanceId = instanceId;
         return this;
      }
      
      public Builder device(String device) {
         this.device = device;
         return this;
      }
      
      public Builder status(Status status) {
         this.status = status;
         return this;
      }
      
      public Builder attachTime(Date attachTime)  {
         this.attachTime = attachTime;
         return this;
      }

      public Attachment build() {
         return new Attachment(region, volumeId, instanceId, device, status, attachTime);
      }
   }

   private final String region;
   private final String volumeId;
   private final String instanceId;
   private final String device;
   private final Status status;
   private final Date attachTime;

   public Attachment(String region, String volumeId, String instanceId, String device, Status status, Date attachTime) {
      this.region = checkNotNull(region, "region");
      this.volumeId = volumeId;
      this.instanceId = instanceId;
      this.device = device;
      this.status = status;
      this.attachTime = attachTime;
   }

   /**
    * Snapshots are tied to Regions and can only be used for volumes within the same Region.
    */
   public String getRegion() {
      return region;
   }

   /**
    * The ID of the volume.
    */
   public String getVolumeId() {
      return volumeId;
   }

   /**
    * The ID of the instance.
    */
   public String getId() {
      return instanceId;
   }

   /**
    * The device as it is exposed to the instance.
    */
   public String getDevice() {
      return device;
   }

   /**
    * Volume state.
    */
   public Status getStatus() {
      return status;
   }

   /**
    * Time stamp when the attachment initiated.
    */
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
      result = prime * result + ((region == null) ? 0 : region.hashCode());
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
      if (region == null) {
         if (other.region != null)
            return false;
      } else if (!region.equals(other.region))
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
      return "Attachment [region=" + region + ", volumeId=" + volumeId + ", instanceId=" + instanceId + ", device="
              + device + ", attachTime=" + attachTime + ", status=" + status + "]";
   }

   @Override
   public int compareTo(Attachment o) {
      return attachTime.compareTo(o.attachTime);
   }

}
