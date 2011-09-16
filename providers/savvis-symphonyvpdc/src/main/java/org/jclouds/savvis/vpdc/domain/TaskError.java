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
package org.jclouds.savvis.vpdc.domain;

import org.jclouds.javax.annotation.Nullable;

/**
 * 
 * 
 * @author Adrian Cole
 */
public class TaskError {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private String message;
      private int majorErrorCode = -1;
      private int minorErrorCode = -1;
      private String vendorSpecificErrorCode;

      public Builder message(String message) {
         this.message = message;
         return this;
      }

      public Builder majorErrorCode(int majorErrorCode) {
         this.majorErrorCode = majorErrorCode;
         return this;
      }

      public Builder minorErrorCode(int minorErrorCode) {
         this.minorErrorCode = minorErrorCode;
         return this;
      }

      public Builder vendorSpecificErrorCode(String vendorSpecificErrorCode) {
         this.vendorSpecificErrorCode = vendorSpecificErrorCode;
         return this;
      }

      public TaskError build() {
         return new TaskError(message, majorErrorCode, minorErrorCode, vendorSpecificErrorCode);
      }
   }

   private final String message;
   private final int majorErrorCode;
   private final int minorErrorCode;
   private final String vendorSpecificErrorCode;

   public TaskError(String message, int majorErrorCode, int minorErrorCode, @Nullable String vendorSpecificErrorCode) {
      this.message = message;
      this.majorErrorCode = majorErrorCode;
      this.minorErrorCode = minorErrorCode;
      this.vendorSpecificErrorCode = vendorSpecificErrorCode;
   }

   /**
    * 
    * @return message describing the error
    */
   public String getMessage() {
      return message;
   }

   /**
    * 
    * @return matches the HTTP status code
    */
   public int getMajorErrorCode() {
      return majorErrorCode;
   }

   /**
    * 
    * @return matches the minor code, typically -1
    */
   public int getMinorErrorCode() {
      return minorErrorCode;
   }

   /**
    * 
    * @return optional additional information about the source of the error
    */
   @Nullable
   public String getVendorSpecificErrorCode() {
      return vendorSpecificErrorCode;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + majorErrorCode;
      result = prime * result + ((message == null) ? 0 : message.hashCode());
      result = prime * result + minorErrorCode;
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
      TaskError other = (TaskError) obj;
      if (majorErrorCode != other.majorErrorCode)
         return false;
      if (message == null) {
         if (other.message != null)
            return false;
      } else if (!message.equals(other.message))
         return false;
      if (minorErrorCode != other.minorErrorCode)
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
      return "[message=" + message + ", majorErrorCode=" + majorErrorCode + ", minorErrorCode=" + minorErrorCode
            + ", vendorSpecificErrorCode=" + vendorSpecificErrorCode + "]";
   }

}