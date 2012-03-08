/*
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
package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.*;
import static com.google.common.base.Preconditions.*;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorRuntimeException;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * The standard error message type used in the vCloud REST API.
 *
 * <pre>
 * &lt;xs:complexType name="ErrorType" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlRootElement(name = "Error")
public class Error {

   public static enum Code {

      OK(200),
      CREATED(201),
      ACCEPTED(202),
      NO_CONTENT(204),
      SEE_OTHER(303),
      BAD_REQUEST(400),
      UNAUTHORIZED(401),
      FORBIDDEN(403), // NOTE also means 'not found' for entities
      NOT_FOUND(404),
      NOT_ALLOWED(405),
      INTERNAL_ERROR(500),
      NOT_IMPLEMENTED(501),
      UNAVAILABLE(503);

      private Integer majorErrorCode;

      private Code(Integer majorErrorCode) {
         this.majorErrorCode = majorErrorCode;
      }

      public Integer getCode() {
         return majorErrorCode;
      }

      public static Code fromCode(final int majorErrorCode) {
         Optional<Code> found = Iterables.tryFind(Arrays.asList(values()), new Predicate<Code>() {
            @Override
            public boolean apply(Code code) {
               return code.getCode().equals(majorErrorCode);
            }
         });
         if (found.isPresent()) {
            return found.get();
         } else {
            throw new VCloudDirectorRuntimeException(String.format("Illegal major error code '%d'", majorErrorCode));
         }
      }
   }

   public static final String MEDIA_TYPE = VCloudDirectorMediaType.ERROR;

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromError(this);
   }

   public static class Builder {

      protected String message;
      protected int majorErrorCode;
      protected String minorErrorCode;
      protected String vendorSpecificErrorCode;
      protected String stackTrace;

      /**
       * @see Error#getMessage()
       */
      public Builder message(String message) {
         this.message = message;
         return this;
      }

      /**
       * @see Error#getMajorErrorCode()
       */
      public Builder majorErrorCode(int majorErrorCode) {
         this.majorErrorCode = majorErrorCode;
         return this;
      }

      /**
       * @see Error#getMinorErrorCode()
       */
      public Builder minorErrorCode(String minorErrorCode) {
         this.minorErrorCode = minorErrorCode;
         return this;
      }

      /**
       * @see Error#getVendorSpecificErrorCode()
       */
      public Builder vendorSpecificErrorCode(String vendorSpecificErrorCode) {
         this.vendorSpecificErrorCode = vendorSpecificErrorCode;
         return this;
      }

      /**
       * @see Error#getStackTrace()
       */
      public Builder stackTrace(String stackTrace) {
         this.stackTrace = stackTrace;
         return this;
      }

      public Error build() {
         return new Error(message, majorErrorCode, minorErrorCode, vendorSpecificErrorCode, stackTrace);
      }

      public Builder fromError(Error in) {
         return message(in.getMessage())
               .majorErrorCode(in.getMajorErrorCode())
               .minorErrorCode(in.getMinorErrorCode())
               .vendorSpecificErrorCode(in.getVendorSpecificErrorCode())
               .stackTrace(in.getStackTrace());
      }
   }

   @XmlAttribute
   private String message;
   @XmlAttribute
   private Integer majorErrorCode;
   @XmlAttribute
   private String minorErrorCode;
   @XmlAttribute
   private String vendorSpecificErrorCode;
   @XmlAttribute
   private String stackTrace;

   private Error(String message, Integer majorErrorCode, String minorErrorCode, String vendorSpecificErrorCode, String stackTrace) {
      this.message = checkNotNull(message, "message");
      this.majorErrorCode = checkNotNull(majorErrorCode, "majorErrorCode");
      this.minorErrorCode = checkNotNull(minorErrorCode, "minorErrorCode");
      this.vendorSpecificErrorCode = vendorSpecificErrorCode;
      this.stackTrace = stackTrace;
   }

   private Error() {
      // For JAXB
   }

   /**
    * An one line, human-readable message describing the error that occurred.
    */
   public String getMessage() {
      return message;
   }

   /**
    * The class of the error. Matches the HTTP status code.
    */
   public Integer getMajorErrorCode() {
      return majorErrorCode;
   }

   /**
    * Specific API error code.
    * <p/>
    * For example - can indicate that vApp power on failed by some reason.
    */
   public String getMinorErrorCode() {
      return minorErrorCode;
   }

   /**
    * A vendor/implementation specific error code that point to specific
    * modules/parts of the code and can make problem diagnostics easier.
    */
   public String getVendorSpecificErrorCode() {
      return vendorSpecificErrorCode;
   }

   /**
    * The stack trace of the exception which when examined might make problem
    * diagnostics easier.
    */
   public String getStackTrace() {
      return stackTrace;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      Error that = (Error) o;
      return equal(this.message, that.message) &&
            equal(this.majorErrorCode, that.majorErrorCode) &&
            equal(this.minorErrorCode, that.minorErrorCode) &&
            equal(this.vendorSpecificErrorCode, that.vendorSpecificErrorCode) &&
            equal(this.stackTrace, that.stackTrace);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(message, majorErrorCode, minorErrorCode, vendorSpecificErrorCode, stackTrace);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("message", message).add("majorErrorCode", majorErrorCode).add("minorErrorCode", minorErrorCode)
            .add("vendorSpecificErrorCode", vendorSpecificErrorCode).add("stackTrace", stackTrace)
            .toString();
   }
}