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

/**
 * Object that the payload on requests is wrapped in.
 *
 * @author Ivan Meredith
 */
public class RimuHostingResponse {
   private String status_message;
   private Integer status_code;
   private RimuHostingError error_info;
   public String getStatusMessage() {
      return status_message;
   }

   public void setStatusMessage(String status_message) {
      this.status_message = status_message;
   }

   public Integer getStatusCode() {
      return status_code;
   }

   public void setStatusCode(Integer status_code) {
      this.status_code = status_code;
   }


   public RimuHostingError getErrorInfo() {
      return error_info;
   }

   public void setErrorInfo(RimuHostingError error_info) {
      this.error_info = error_info;
   }
}
