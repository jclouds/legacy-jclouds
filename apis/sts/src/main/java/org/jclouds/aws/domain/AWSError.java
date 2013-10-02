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
package org.jclouds.aws.domain;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * When an Amazon S3 request is in error, the client receives an error response.
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?ErrorResponse.html"
 *      />
 * @author Adrian Cole
 * 
 */
public class AWSError {
   private String code;
   private String message;
   private String requestId;
   private String requestToken;
   private Map<String, String> details = Maps.newHashMap();
   private String stringSigned;
   private String signature;

   @Override
   public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append("AWSError");
      sb.append("{requestId='").append(requestId).append('\'');
      sb.append(", requestToken='").append(requestToken).append('\'');
      if (code != null)
         sb.append(", code='").append(code).append('\'');
      if (message != null)
         sb.append(", message='").append(message).append('\'');
      if (stringSigned != null)
         sb.append(", stringSigned='").append(stringSigned).append('\'');
      if (getSignature() != null)
         sb.append(", signature='").append(getSignature()).append('\'');
      if (details.size() != 0)
         sb.append(", context='").append(details.toString()).append('\'');
      sb.append('}');
      return sb.toString();
   }

   public void setCode(String code) {
      this.code = code;
   }

   /**
    * The error code is a string that uniquely identifies an error condition. It is meant to be read
    * and understood by programs that detect and handle errors by type
    * 
    * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/ErrorCode.html" />
    */
   public String getCode() {
      return code;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   /**
    * The error message contains a generic description of the error condition in English.
    * 
    * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/ErrorMessage.html" />
    */
   public String getMessage() {
      return message;
   }

   public void setRequestId(String requestId) {
      this.requestId = requestId;
   }

   /**
    * * A unique ID assigned to each request by the system. In the unlikely event that you have
    * problems with Amazon S3, Amazon can use this to help troubleshoot the problem.
    * 
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

   public void setRequestToken(String requestToken) {
      this.requestToken = requestToken;
   }

   public String getRequestToken() {
      return requestToken;
   }

   public void setSignature(String signature) {
      this.signature = signature;
   }

   public String getSignature() {
      return signature;
   }
}
