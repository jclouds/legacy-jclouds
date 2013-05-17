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
package org.jclouds.cloudstack.domain;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * @author Adrian Cole
 */
public class AsyncJobError {

   /**
    * Error codes for job errors
    */
   public static enum ErrorCode {
      INTERNAL_ERROR(530),
      ACCOUNT_ERROR(531),
      ACCOUNT_RESOURCE_LIMIT_ERROR(532),
      INSUFFICIENT_CAPACITY_ERROR(533),
      RESOURCE_UNAVAILABLE_ERROR(534),
      RESOURCE_ALLOCATION_ERROR(535),
      RESOURCE_IN_USE_ERROR(536),
      NETWORK_RULE_CONFLICT_ERROR(537),
      UNKNOWN(-1);

      private final int code;

      private ErrorCode(int code) {
         this.code = code;
      }

      public int code() {
         return this.code;
      }

      public static ErrorCode fromValue(String value) {
         try {
            int errorCode = Integer.parseInt(value);
            for (ErrorCode candidate : values()) {
               if (candidate.code() == errorCode) {
                  return candidate;
               }
            }
            return UNKNOWN;

         } catch (NumberFormatException e) {
            return UNKNOWN;
         }
      }
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromAsyncJobError(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected AsyncJobError.ErrorCode errorCode;
      protected String errorText;

      /**
       * @see AsyncJobError#getErrorCode()
       */
      public T errorCode(ErrorCode errorCode) {
         this.errorCode = errorCode;
         return self();
      }

      /**
       * @see AsyncJobError#getErrorText()
       */
      public T errorText(String errorText) {
         this.errorText = errorText;
         return self();
      }

      public AsyncJobError build() {
         return new AsyncJobError(errorCode, errorText);
      }

      public T fromAsyncJobError(AsyncJobError in) {
         return this
               .errorCode(in.getErrorCode())
               .errorText(in.getErrorText());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final ErrorCode errorCode;
   private final String errorText;

   @ConstructorProperties({
         "errorcode", "errortext"
   })
   protected AsyncJobError(@Nullable ErrorCode errorCode, @Nullable String errorText) {
      this.errorCode = errorCode;
      this.errorText = errorText;
   }

   @Nullable
   public ErrorCode getErrorCode() {
      return this.errorCode;
   }

   @Nullable
   public String getErrorText() {
      return this.errorText;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(errorCode, errorText);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      AsyncJobError that = AsyncJobError.class.cast(obj);
      return Objects.equal(this.errorCode, that.errorCode)
            && Objects.equal(this.errorText, that.errorText);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("errorCode", errorCode).add("errorText", errorText);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
