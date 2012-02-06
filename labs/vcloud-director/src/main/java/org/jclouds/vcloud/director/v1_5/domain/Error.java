package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.*;
import static com.google.common.base.Preconditions.*;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.*;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;

/**
 * The standard error message type used in the vCloud REST API.
 *
 * <pre>
 * &lt;xs:complexType name="ErrorType"&gt;
 * </pre>
 *
 * @author Adrian Cole
 */
@XmlRootElement(namespace = NS, name = "Error")
public class Error {

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
   protected String message;
   @XmlAttribute
   protected int majorErrorCode;
   @XmlAttribute
   protected String minorErrorCode;
   @XmlAttribute
   protected String vendorSpecificErrorCode;
   @XmlAttribute
   protected String stackTrace;

   private Error(String message, int majorErrorCode, String minorErrorCode) {
      this.message = checkNotNull(message, "message");
      this.majorErrorCode = majorErrorCode;
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

   public void setMessage(String message) {
      this.message = message;
   }

   /**
    * The class of the error. Matches the HTTP status code.
    */
   public int getMajorErrorCode() {
      return majorErrorCode;
   }

   public void setMajorErrorCode(int majorErrorCode) {
      this.majorErrorCode = majorErrorCode;
   }

   /**
    * Specific API error code.
    *
    * For example - can indicate that vApp power on failed by some reason.
    */
   public String getMinorErrorCode() {
      return minorErrorCode;
   }

   public void setMinorErrorCode(String minorErrorCode) {
      this.minorErrorCode = minorErrorCode;
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
      if (!super.equals(o))
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
      return super.hashCode() + Objects.hashCode(message, majorErrorCode, minorErrorCode, vendorSpecificErrorCode, stackTrace);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("message", message)
            .add("majorErrorCode", majorErrorCode)
            .add("minorErrorCode", minorErrorCode)
            .toString();
   }
}