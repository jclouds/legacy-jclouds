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

package org.jclouds.googlecompute.domain;

import com.google.common.base.Objects;
import org.jclouds.javax.annotation.Nullable;

import java.beans.ConstructorProperties;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A particular error for an operation including the details.
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/operations"/>
 */
public class OperationError {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromOperationErrorDetail(this);
   }

   public static class Builder {

      private String code;
      private String location;
      private String message;

      /**
       * @see OperationError#getCode()
       */
      public Builder code(String code) {
         this.code = checkNotNull(code);
         return this;
      }

      /**
       * @see OperationError#getLocation()
       */
      public Builder location(String location) {
         this.location = checkNotNull(location);
         return this;
      }

      /**
       * @see OperationError#getMessage()
       */
      public Builder message(String message) {
         this.message = message;
         return this;
      }

      public OperationError build() {
         return new OperationError(code, location, message);
      }

      public Builder fromOperationErrorDetail(OperationError in) {
         return new Builder().code(in.getCode()).location(in.getLocation()).message
                 (in.getMessage());
      }
   }

   private final String code;
   private final String location;
   private final String message;

   @ConstructorProperties({
           "code", "location", "message"
   })
   private OperationError(String code, String location, String message) {
      this.code = checkNotNull(code);
      this.location = location;
      this.message = message;
   }

   /**
    * @return the error type identifier for this error.
    */
   public String getCode() {
      return code;
   }

   /**
    * @return indicates the field in the request which caused the error. This property is optional.
    */
   public String getLocation() {
      return location;
   }

   /**
    * @return an optional, human-readable error message.
    */
   @Nullable
   public String getMessage() {
      return message;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(code, location, message);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      OperationError that = OperationError.class.cast(obj);
      return equal(this.code, that.code)
              && equal(this.location, that.location)
              && equal(this.message, that.message);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return toStringHelper(this)
              .add("code", code).add("location", location).add("message", message);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }
}
