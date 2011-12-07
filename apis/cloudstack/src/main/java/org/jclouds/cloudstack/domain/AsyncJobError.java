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
package org.jclouds.cloudstack.domain;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Adrian Cole
 */
public class AsyncJobError {

   /**
    * Error codes for job errors
    */
   public static enum ErrorCode {
      INTERNAL_ERROR (530),
      ACCOUNT_ERROR (531),
      ACCOUNT_RESOURCE_LIMIT_ERROR(532),
      INSUFFICIENT_CAPACITY_ERROR (533),
      RESOURCE_UNAVAILABLE_ERROR (534),
      RESOURCE_ALLOCATION_ERROR (535),
      RESOURCE_IN_USE_ERROR (536),
      NETWORK_RULE_CONFLICT_ERROR (537),
      UNKNOWN (-1);

      private final int code;

      private ErrorCode(int code) {
         this.code = code;
      }

      public int code() { return this.code; }

      public static ErrorCode fromValue(String value) {
         try {
            int errorCode = Integer.parseInt(value);
            for(ErrorCode candidate : values()) {
               if (candidate.code() == errorCode) {
                  return candidate;
               }
            }
            return UNKNOWN;

         } catch(NumberFormatException e) {
            return UNKNOWN;
         }
      }
   }

   @SerializedName("errorcode")
   private ErrorCode errorCode;
   @SerializedName("errortext")
   private String errorText;

   /**
    * present only for serializer
    * 
    */
   AsyncJobError() {

   }

   public AsyncJobError(ErrorCode errorCode, String errorText) {
      this.errorCode = errorCode;
      this.errorText = errorText;
   }

   public ErrorCode getErrorCode() {
      return errorCode;
   }

   public String getErrorText() {
      return errorText;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + errorCode.code();
      result = prime * result + ((errorText == null) ? 0 : errorText.hashCode());
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
      AsyncJobError other = (AsyncJobError) obj;
      if (errorCode != other.errorCode)
         return false;
      if (errorText == null) {
         if (other.errorText != null)
            return false;
      } else if (!errorText.equals(other.errorText))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "AsyncJobError{" +
            "errorCode=" + errorCode +
            ", errorText='" + errorText + '\'' +
            '}';
   }

}
