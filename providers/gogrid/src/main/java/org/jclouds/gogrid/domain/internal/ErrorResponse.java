/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.gogrid.domain.internal;

import com.google.gson.annotations.SerializedName;

/**
 * @author Oleksiy Yarmula
 */
public class ErrorResponse implements Comparable<ErrorResponse> {

   private String message;
   @SerializedName("errorcode")
   private String errorCode;

   /**
    * A no-args constructor is required for deserialization
    */
   public ErrorResponse() {
   }

   public ErrorResponse(String message, String errorCode) {
      this.message = message;
      this.errorCode = errorCode;
   }

   public String getMessage() {
      return message;
   }

   public String getErrorCode() {
      return errorCode;
   }

   @Override
   public int compareTo(ErrorResponse o) {
      return message.compareTo(o.getMessage());
   }

   @Override
   public String toString() {
      return "[errorCode=" + errorCode + ", message=" + message + "]";
   }
}
