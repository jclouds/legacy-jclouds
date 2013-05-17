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
package org.jclouds.gogrid.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ComparisonChain;

/**
 * Class ErrorResponse
 *
 * @author Oleksiy Yarmula
 */
public class ErrorResponse implements Comparable<ErrorResponse> {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromErrorResponse(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected String message;
      protected String errorCode;

      /**
       * @see ErrorResponse#getMessage()
       */
      public T message(String message) {
         this.message = message;
         return self();
      }

      /**
       * @see ErrorResponse#getErrorCode()
       */
      public T errorCode(String errorCode) {
         this.errorCode = errorCode;
         return self();
      }

      public ErrorResponse build() {
         return new ErrorResponse(message, errorCode);
      }

      public T fromErrorResponse(ErrorResponse in) {
         return this
               .message(in.getMessage())
               .errorCode(in.getErrorCode());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String message;
   private final String errorCode;

   @ConstructorProperties({
         "message", "errorcode"
   })
   protected ErrorResponse(String message, String errorCode) {
      this.message = checkNotNull(message, "message");
      this.errorCode = checkNotNull(errorCode, "errorCode");
   }

   public String getMessage() {
      return this.message;
   }

   public String getErrorCode() {
      return this.errorCode;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(message, errorCode);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ErrorResponse that = ErrorResponse.class.cast(obj);
      return Objects.equal(this.message, that.message)
            && Objects.equal(this.errorCode, that.errorCode);
   }

   @Override
   public int compareTo(ErrorResponse that) {
      return ComparisonChain.start()
            .compare(errorCode, that.errorCode)
            .compare(message, that.message)
            .result();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("message", message).add("errorCode", errorCode);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
