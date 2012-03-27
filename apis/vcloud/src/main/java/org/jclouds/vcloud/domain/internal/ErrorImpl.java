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
package org.jclouds.vcloud.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.domain.VCloudError;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class ErrorImpl implements VCloudError {
   private final String message;
   private final int majorErrorCode;
   private final MinorCode minorErrorCode;
   @Nullable
   private final String vendorSpecificErrorCode;
   @Nullable
   private final String stackTrace;

   public ErrorImpl(String message, int majorErrorCode, @Nullable MinorCode minorErrorCode,
            @Nullable String vendorSpecificErrorCode, @Nullable String stackTrace) {
      this.message = checkNotNull(message, "message");
      this.majorErrorCode = majorErrorCode;
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

   public MinorCode getMinorErrorCode() {
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