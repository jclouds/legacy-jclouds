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

import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.TaskStatus;

import com.google.inject.internal.Nullable;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class TaskImpl extends ReferenceTypeImpl implements Task {

   public static class ErrorImpl implements Error {
      private final String message;
      private final int majorErrorCode;
      private final String minorErrorCode;
      @Nullable
      private final String vendorSpecificErrorCode;
      @Nullable
      private final String stackTrace;

      public ErrorImpl(String message, int majorErrorCode, @Nullable String minorErrorCode,
               @Nullable String vendorSpecificErrorCode, @Nullable String stackTrace) {
         this.message = checkNotNull(message, "message");
         this.majorErrorCode = checkNotNull(majorErrorCode, "majorErrorCode");
         this.minorErrorCode = minorErrorCode; // check null after 0.8 is gone
         this.vendorSpecificErrorCode = vendorSpecificErrorCode;
         this.stackTrace = stackTrace;
      }

      public String getMessage() {
         return message;
      }

      public int getMajorErrorCode() {
         return majorErrorCode;
      }

      public String getMinorErrorCode() {
         return minorErrorCode;
      }

      public String getVendorSpecificErrorCode() {
         return vendorSpecificErrorCode;
      }

      public String getStackTrace() {
         return stackTrace;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + majorErrorCode;
         result = prime * result + ((message == null) ? 0 : message.hashCode());
         result = prime * result + ((minorErrorCode == null) ? 0 : minorErrorCode.hashCode());
         result = prime * result + ((stackTrace == null) ? 0 : stackTrace.hashCode());
         result = prime * result + ((vendorSpecificErrorCode == null) ? 0 : vendorSpecificErrorCode.hashCode());
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
         if (majorErrorCode != other.majorErrorCode)
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
         if (stackTrace == null) {
            if (other.stackTrace != null)
               return false;
         } else if (!stackTrace.equals(other.stackTrace))
            return false;
         if (vendorSpecificErrorCode == null) {
            if (other.vendorSpecificErrorCode != null)
               return false;
         } else if (!vendorSpecificErrorCode.equals(other.vendorSpecificErrorCode))
            return false;
         return true;
      }

      @Override
      public String toString() {
         return "[majorErrorCode=" + majorErrorCode + ", message=" + message + ", minorErrorCode=" + minorErrorCode
                  + ", stackTrace=" + stackTrace + ", vendorSpecificErrorCode=" + vendorSpecificErrorCode + "]";
      }

   }

   private final TaskStatus status;
   private final Date startTime;
   @Nullable
   private final Date endTime;
   @Nullable
   private final Date expiryTime;
   private final ReferenceType owner;
   @Nullable
   private final Error error;

   public TaskImpl(URI id, TaskStatus status, Date startTime, @Nullable Date endTime, @Nullable Date expiryTime,
            ReferenceType owner, Error error) {
      super(null, VCloudMediaType.TASK_XML, id);
      this.status = checkNotNull(status, "status");
      this.startTime = startTime;
      this.endTime = endTime;
      this.expiryTime = expiryTime;
      this.owner = owner;
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
   public ReferenceType getOwner() {
      return owner;
   }

   @Override
   public Date getEndTime() {
      return endTime;
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
      result = prime * result + ((owner == null) ? 0 : owner.hashCode());
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
      if (owner == null) {
         if (other.owner != null)
            return false;
      } else if (!owner.equals(other.owner))
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
      return "TaskImpl [endTime=" + endTime + ", error=" + error + ", id=" + getName() + ", owner=" + owner
               + ", startTime=" + startTime + ", status=" + status + "]";
   }

   public Date getExpiryTime() {
      return expiryTime;
   }

}