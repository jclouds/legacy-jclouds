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

package org.jclouds.vcloud.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Date;

import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.TaskStatus;

import com.google.inject.internal.Nullable;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class TaskImpl implements Task {

   public static class ErrorImpl implements Error {
      private final String message;
      private final String majorErrorCode;
      private final String minorErrorCode;

      public ErrorImpl(String message, String majorErrorCode, String minorErrorCode) {
         this.message = message;
         this.majorErrorCode = majorErrorCode;
         this.minorErrorCode = minorErrorCode;
      }

      public String getMessage() {
         return message;
      }

      public String getMajorErrorCode() {
         return majorErrorCode;
      }

      public String getMinorErrorCode() {
         return minorErrorCode;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((majorErrorCode == null) ? 0 : majorErrorCode.hashCode());
         result = prime * result + ((message == null) ? 0 : message.hashCode());
         result = prime * result + ((minorErrorCode == null) ? 0 : minorErrorCode.hashCode());
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
         ErrorImpl other = (ErrorImpl) obj;
         if (majorErrorCode == null) {
            if (other.majorErrorCode != null)
               return false;
         } else if (!majorErrorCode.equals(other.majorErrorCode))
            return false;
         if (message == null) {
            if (other.message != null)
               return false;
         } else if (!message.equals(other.message))
            return false;
         if (minorErrorCode == null) {
            if (other.minorErrorCode != null)
               return false;
         } else if (!minorErrorCode.equals(other.minorErrorCode))
            return false;
         return true;
      }

      @Override
      public String toString() {
         return "ErrorImpl [majorErrorCode=" + majorErrorCode + ", message=" + message + ", minorErrorCode="
               + minorErrorCode + "]";
      }
   }

   private final URI id;
   private final TaskStatus status;
   private final Date startTime;
   @Nullable
   private final Date endTime;
   @Nullable
   private final Date expiryTime;
   private final NamedResource owner;
   @Nullable
   private final NamedResource result;
   @Nullable
   private final Error error;

   public TaskImpl(URI id, TaskStatus status, Date startTime, @Nullable Date endTime, @Nullable Date expiryTime,
         NamedResource owner, @Nullable NamedResource result, Error error) {
      this.id = checkNotNull(id, "id");
      this.status = checkNotNull(status, "status");
      this.startTime = startTime;
      this.endTime = endTime;
      this.expiryTime = expiryTime;
      this.owner = owner;
      this.result = result;
      this.error = error;
   }

   @Override
   public TaskStatus getStatus() {
      return status;
   }

   @Override
   public Date getStartTime() {
      return startTime;
   }

   @Override
   public NamedResource getOwner() {
      return owner;
   }

   @Override
   public NamedResource getResult() {
      return result;
   }

   @Override
   public Date getEndTime() {
      return endTime;
   }

   @Override
   public int compareTo(Task o) {
      return (this == o) ? 0 : getLocation().compareTo(o.getLocation());
   }

   @Override
   public URI getLocation() {
      return id;
   }

   @Override
   public Error getError() {
      return error;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
      result = prime * result + ((error == null) ? 0 : error.hashCode());
      result = prime * result + ((expiryTime == null) ? 0 : expiryTime.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((owner == null) ? 0 : owner.hashCode());
      result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
      result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
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
      TaskImpl other = (TaskImpl) obj;
      if (endTime == null) {
         if (other.endTime != null)
            return false;
      } else if (!endTime.equals(other.endTime))
         return false;
      if (error == null) {
         if (other.error != null)
            return false;
      } else if (!error.equals(other.error))
         return false;
      if (expiryTime == null) {
         if (other.expiryTime != null)
            return false;
      } else if (!expiryTime.equals(other.expiryTime))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (owner == null) {
         if (other.owner != null)
            return false;
      } else if (!owner.equals(other.owner))
         return false;
      if (result == null) {
         if (other.result != null)
            return false;
      } else if (!result.equals(other.result))
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
      return true;
   }

   @Override
   public String toString() {
      return "TaskImpl [endTime=" + endTime + ", error=" + error + ", id=" + id + ", owner=" + owner
            + ", result=" + result + ", startTime=" + startTime + ", status=" + status + "]";
   }

   public Date getExpiryTime() {
      return expiryTime;
   }

}