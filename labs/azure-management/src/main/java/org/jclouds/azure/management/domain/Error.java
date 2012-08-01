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
package org.jclouds.azure.management.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.CaseFormat;
import com.google.common.base.Objects;

/**
 * additional error information that is defined by the management service. Th
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/ee460801" >api</a>
 * 
 * @author Adrian Cole
 */
public class Error {

   public static enum Code {

      /**
       * Bad Request (400)
       * 
       * The versioning header is not specified or was specified incorrectly.
       */
      MISSING_OR_INCORRECT_VERSION_HEADER,

      /**
       * Bad Request (400)
       * 
       * The request body’s XML was invalid or not correctly specified.
       */
      INVALID_XML_REQUEST,

      /**
       * Bad Request (400)
       * 
       * A required query parameter was not specified for this request or was specified incorrectly.
       */
      MISSING_OR_INVALID_REQUIRED_QUERY_PARAMETER,

      /**
       * Bad Request (400)
       * 
       * The HTTP verb specified was not recognized by the server or isn’t valid for this resource.
       */
      INVALID_HTTP_VERB,

      /**
       * Forbidden (403)
       * 
       * The server failed to authenticate the request. Verify that the certificate is valid and is
       * associated with this subscription.
       */
      AUTHENTICATION_FAILED,

      /**
       * Not Found (404)
       * 
       * The specified resource does not exist.
       */
      RESOURCE_NOT_FOUND,

      /**
       * Internal Server Error (500)
       * 
       * The server encountered an internal error. Please retry the request.
       */
      INTERNAL_ERROR,

      /**
       * Internal Server Error (500)
       * 
       * The operation could not be completed within the permitted time.
       */
      OPERATION_TIMED_OUT,

      /**
       * Service Unavailable (503)
       * 
       * The server (or an internal component) is currently unavailable to receive requests. Please
       * retry your request
       */
      SERVER_BUSY,

      /**
       * Forbidden (403)
       * 
       * The subscription is in a disabled state.
       */
      SUBSCRIPTION_DISABLED,

      /**
       * Bad Request (400)
       * 
       * A parameter was incorrect.
       */
      BAD_REQUEST,

      /**
       * Conflict (409)
       * 
       * A conflict occurred to prevent the operation from completing.
       */
      CONFLICT_ERROR,

      UNRECOGNIZED;

      public String value() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
      }

      @Override
      public String toString() {
         return value();
      }

      public static Code fromValue(String code) {
         try {
            return valueOf(CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(code, "code")));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromError(this);
   }

   public static class Builder {

      private String rawCode;
      private Code code;
      private String message;

      /**
       * @see Error#getRawCode()
       */
      public Builder rawCode(String rawCode) {
         this.rawCode = rawCode;
         return this;
      }

      /**
       * @see Error#getCode()
       */
      public Builder code(Code code) {
         this.code = code;
         return this;
      }

      /**
       * @see Error#getMessage()
       */
      public Builder message(String message) {
         this.message = message;
         return this;
      }

      public Error build() {
         return new Error(rawCode, code, message);
      }

      public Builder fromError(Error in) {
         return this.rawCode(in.rawCode).code(in.code).message(in.message);
      }
   }

   private final String rawCode;
   private final Code code;
   private final String message;

   protected Error(String rawCode, Code code, String message) {
      this.rawCode = checkNotNull(rawCode, "rawCode for %s", message);
      this.code = checkNotNull(code, "code for %s", message);
      this.message = checkNotNull(message, "message");
   }

   /**
    * Error code
    */
   public Code getCode() {
      return code;
   }

   /**
    * Error code, unparsed
    */
   public String getRawCode() {
      return rawCode;
   }

   /**
    * User message
    */
   public String getMessage() {
      return message;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(rawCode, code, message);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Error other = (Error) obj;
      return Objects.equal(this.rawCode, other.rawCode) && Objects.equal(this.code, other.code)
               && Objects.equal(this.message, other.message);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("code", rawCode).add("message", message).toString();
   }

}
