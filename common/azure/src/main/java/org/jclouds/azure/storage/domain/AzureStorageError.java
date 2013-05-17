/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.azure.storage.domain;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * When an Azure Storage request is in error, the client receives an error response.
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/dd573365.aspx" />
 * @author Adrian Cole
 * 
 */
public class AzureStorageError {
   private String code;
   private String message;
   private String requestId;
   private Map<String, String> details = Maps.newHashMap();
   private String stringSigned;
   private String signature;

   @Override
   public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append("AzureError");
      sb.append("{requestId='").append(requestId).append('\'');
      if (code != null)
         sb.append(", code='").append(code).append('\'');
      if (message != null)
         sb.append(", message='").append(message).append('\'');
      if (stringSigned != null)
         sb.append(", stringSigned='").append(stringSigned).append('\'');
      if (getSignature() != null)
         sb.append(", signature='").append(getSignature()).append('\'');
      if (details.size() != 0)
         sb.append(", context='").append(details.toString()).append('\'').append('}');
      return sb.toString();
   }

   public void setCode(String code) {
      this.code = code;
   }

   public String getCode() {
      return code;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public String getMessage() {
      return message;
   }

   public void setRequestId(String requestId) {
      this.requestId = requestId;
   }

   /**
    * If a request is consistently failing and you have verified that the request is properly
    * formulated, you may use this value to report the error to Microsoft. In your report, include
    * the value of x-ms-request-id, the approximate time that the request was made, the storage
    * service against which the request was made, and the type of operation that the request
    * attempted
    */
   public String getRequestId() {
      return requestId;
   }

   public void setStringSigned(String stringSigned) {
      this.stringSigned = stringSigned;
   }

   /**
    * @return what jclouds signed before sending the request.
    */
   public String getStringSigned() {
      return stringSigned;
   }

   public void setDetails(Map<String, String> context) {
      this.details = context;
   }

   /**
    * @return additional details surrounding the error.
    */
   public Map<String, String> getDetails() {
      return details;
   }

   public void setSignature(String signature) {
      this.signature = signature;
   }

   public String getSignature() {
      return signature;
   }
}
