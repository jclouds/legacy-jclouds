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
package org.jclouds.vcloud.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.domain.internal.ErrorImpl;

import com.google.inject.ImplementedBy;

/**
 * 
 * 
 * @author Adrian Cole
 */
@ImplementedBy(ErrorImpl.class)
public interface VCloudError {
   public static enum MinorCode {
      /**
       * The request was made by a user who had insufficient rights to the object or operation.
       */
      ACCESS_TO_RESOURCE_IS_FORBIDDEN,
      /**
       * The request could not be validated or contained invalid XML.
       */
      BAD_REQUEST,
      /**
       * A conflict was detected between sections of an OVF descriptor.
       */
      CONFLICT,
      /**
       * The entity is busy
       */
      BUSY_ENTITY,
      /**
       * An attempt to instantiate a vAppTemplate or use a vAppTemplate or a Vm in a composition did
       * not include an AllEULAsAccepted element with a value of true.
       */
      EULA_NOT_ACCEPTED,
      /**
       * Returned for any failure that cannot be matched to another minor error code.
       */
      INTERNAL_SERVER_ERROR,
      /**
       * One or more references (href attribute values) supplied in the request could not be
       * resolved to an object.
       */
      INVALID_REFERENCE,
      /**
       * The HTTP method (GET, PUT, POST, DELETE) is not allowed for the request.
       */
      METHOD_NOT_ALLOWED,
      /**
       * One or more references (href attribute values) supplied in the request could not be
       * resolved to an object, or the Content‐type of the request was incorrect.
       */
      RESOURCE_NOT_FOUND,
      /**
       * The request raised an exception that did not match any HTTP status code.
       */
      UNKNOWN,
      /**
       * The wrong content type was specified for the request.
       */
      UNSUPPORTED_MEDIA_TYPE, UNRECOGNIZED;

      public static MinorCode fromValue(String minorCode) {
         try {
            return valueOf(checkNotNull(minorCode, "minorCode"));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   /**
    * 
    * @return message describing the error
    */
   String getMessage();

   /**
    * 
    * @return matches the HTTP status code
    */
   int getMajorErrorCode();

   /**
    * 
    * @return error code specific to the failed operation or null if vcloud <0.9
    */
   @Nullable
   MinorCode getMinorErrorCode();

   /**
    * 
    * @return optional additional information about the source of the error
    */
   @Nullable
   String getVendorSpecificErrorCode();

   /**
    * 
    * @return stack trace of the error, if available. This attribute is returned only when a request
    *         is made by the system administrator.
    */
   String getStackTrace();
}