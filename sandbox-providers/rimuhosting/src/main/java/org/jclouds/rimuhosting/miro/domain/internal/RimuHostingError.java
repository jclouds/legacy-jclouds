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
package org.jclouds.rimuhosting.miro.domain.internal;

import com.google.gson.annotations.SerializedName;

/**
 * Error Object from a response
 *
 * @author Ivan Meredith
 */
public class RimuHostingError {
   @SerializedName("error_class")
   private String errorClass;

   @SerializedName("full_error_message")
   private String errorMessage;

   @SerializedName("error_title")
   private String error;

   public String getErrorClass() {
      return errorClass;
   }

   public void setErrorClass(String errorClass) {
      this.errorClass = errorClass;
   }

   public String getErrorMessage() {
      return errorMessage;
   }

   public void setErrorMessage(String errorMessage) {
      this.errorMessage = errorMessage;
   }

   public String getError() {
      return error;
   }

   public void setError(String error) {
      this.error = error;
   }
}
