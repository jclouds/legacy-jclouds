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
package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.VCLOUD_1_5_NS;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;

import com.google.common.base.Objects;

/**
 * The standard error message type used in the vCloud REST API.
 *
 * <pre>
 * &lt;xs:complexType name="ErrorType"&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlRootElement(namespace = VCLOUD_1_5_NS, name = "Error")
@XmlAccessorType(XmlAccessType.FIELD)
public class Error {
   
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
         Error error = new Error(message, majorErrorCode, minorErrorCode);
         error.setVendorSpecificErrorCode(vendorSpecificErrorCode);
         error.setStackTrace(stackTrace);
         return error;
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

   private Error(String message, Integer majorErrorCode, String minorErrorCode) {
      this.message = checkNotNull(message, "message");
      this.majorErrorCode = checkNotNull(majorErrorCode, "majorErrorCode");
      this.minorErrorCode = checkNotNull(minorErrorCode, "minorErrorCode");
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
    *
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

   public void setVendorSpecificErrorCode(String vendorSpecificErrorCode) {
      this.vendorSpecificErrorCode = vendorSpecificErrorCode;
   }

   /**
    * The stack trace of the exception which when examined might make problem
    * diagnostics easier.
    */
   public String getStackTrace() {
      return stackTrace;
   }

   public void setStackTrace(String stackTrace) {
      this.stackTrace = stackTrace;
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