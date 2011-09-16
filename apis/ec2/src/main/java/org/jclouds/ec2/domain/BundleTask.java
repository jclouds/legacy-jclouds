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

import org.jclouds.javax.annotation.Nullable;

/**
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-ItemType-BundleInstanceTaskType.html"
 *      />
 * @author Adrian Cole
 */
public class BundleTask implements Comparable<BundleTask> {
   /**
    * {@inheritDoc}
    */
   public int compareTo(BundleTask o) {
      return (this == o) ? 0 : getBundleId().compareTo(o.getBundleId());
   }

   /**
    * If the task fails, a description of the error.
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-ItemType-BundleInstanceTaskErrorType.html"
    *      />
    * @author Adrian Cole
    */
   public static class Error {
      private final String code;
      private final String message;

      public Error(String code, String message) {
         this.code = checkNotNull(code, "code");
         this.message = checkNotNull(message, "message");
      }

      public String getCode() {
         return code;
      }

      public String getMessage() {
         return message;
      }

      @Override
      public String toString() {
         return "[code=" + code + ", message=" + message + "]";
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((code == null) ? 0 : code.hashCode());
         result = prime * result + ((message == null) ? 0 : message.hashCode());
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
         Error other = (Error) obj;
         if (code == null) {
            if (other.code != null)
               return false;
         } else if (!code.equals(other.code))
            return false;
         if (message == null) {
            if (other.message != null)
               return false;
         } else if (!message.equals(other.message))
            return false;
         return true;
      }

   }

   private final String region;
   private final String bundleId;
   private final Error error;
   private final String instanceId;
   private final int progress;
   private final Date startTime;
   private final String state;
   private final String bucket;
   private final String prefix;
   private final Date updateTime;

   public BundleTask(String region, String bundleId, @Nullable Error error, String instanceId, int progress,
         Date startTime, String state, String bucket, String prefix, Date updateTime) {
      this.region = checkNotNull(region, "region");
      this.bundleId = checkNotNull(bundleId, "bundleId");
      this.error = error;
      this.instanceId = checkNotNull(instanceId, "instanceId");
      this.progress = checkNotNull(progress, "progress");
      this.startTime = checkNotNull(startTime, "startTime");
      this.state = checkNotNull(state, "state");
      this.bucket = checkNotNull(bucket, "bucket");
      this.prefix = checkNotNull(prefix, "prefix");
      this.updateTime = checkNotNull(updateTime, "updateTime");
   }

   @Override
   public String toString() {
      return "[bucket=" + bucket + ", bundleId=" + bundleId + ", error=" + error + ", instanceId=" + instanceId
            + ", prefix=" + prefix + ", progress=" + progress + ", region=" + region + ", startTime=" + startTime
            + ", state=" + state + ", updateTime=" + updateTime + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((bucket == null) ? 0 : bucket.hashCode());
      result = prime * result + ((bundleId == null) ? 0 : bundleId.hashCode());
      result = prime * result + ((error == null) ? 0 : error.hashCode());
      result = prime * result + ((instanceId == null) ? 0 : instanceId.hashCode());
      result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
      result = prime * result + progress;
      result = prime * result + ((region == null) ? 0 : region.hashCode());
      result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
      result = prime * result + ((state == null) ? 0 : state.hashCode());
      result = prime * result + ((updateTime == null) ? 0 : updateTime.hashCode());
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
      BundleTask other = (BundleTask) obj;
      if (bucket == null) {
         if (other.bucket != null)
            return false;
      } else if (!bucket.equals(other.bucket))
         return false;
      if (bundleId == null) {
         if (other.bundleId != null)
            return false;
      } else if (!bundleId.equals(other.bundleId))
         return false;
      if (error == null) {
         if (other.error != null)
            return false;
      } else if (!error.equals(other.error))
         return false;
      if (instanceId == null) {
         if (other.instanceId != null)
            return false;
      } else if (!instanceId.equals(other.instanceId))
         return false;
      if (prefix == null) {
         if (other.prefix != null)
            return false;
      } else if (!prefix.equals(other.prefix))
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
      if (state == null) {
         if (other.state != null)
            return false;
      } else if (!state.equals(other.state))
         return false;
      if (updateTime == null) {
         if (other.updateTime != null)
            return false;
      } else if (!updateTime.equals(other.updateTime))
         return false;
      return true;
   }

   /**
    * 
    * @return region of the bundle task
    */
   public String getRegion() {
      return region;
   }

   /**
    * 
    * @return The bucket in which to store the AMI. You can specify a bucket
    *         that you already own or a new bucket that Amazon EC2 creates on
    *         your behalf. If you specify a bucket that belongs to someone e
    *         lse, Amazon EC2 returns an error.
    */
   public String getBucket() {
      return bucket;
   }

   /**
    * 
    * @return Specifies the beginning of the file name of the AMI.
    */
   public String getPrefix() {
      return prefix;
   }

   /**
    * 
    * @return Identifier for this task.
    */
   public String getBundleId() {
      return bundleId;
   }

   /**
    * 
    * @return If the task fails, a description of the error.
    */
   public Error getError() {
      return error;
   }

   /**
    * 
    * @return Instance associated with this bundle task
    */
   public String getInstanceId() {
      return instanceId;
   }

   /**
    * 
    * @return A percentage description of the progress of the task, such as 20.
    */
   public int getProgress() {
      return progress;
   }

   /**
    * 
    * @return The time this task started.
    */
   public Date getStartTime() {
      return startTime;
   }

   /**
    * 
    * @return The state of the task.
    */
   public String getState() {
      return state;
   }

   /**
    * 
    * @return The time of the most recent update for the task.
    */
   public Date getUpdateTime() {
      return updateTime;
   }

}
