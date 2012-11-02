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
import com.google.common.base.Optional;

/**
 * 
 * Determines whether the operation has succeeded, failed, or is still in progress.
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/ee460783" >api</a>
 * 
 * @author Adrian Cole
 */
public class Operation {

   public static enum Status {

      IN_PROGRESS,

      SUCCEEDED,

      FAILED,

      UNRECOGNIZED;

      public String value() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
      }

      @Override
      public String toString() {
         return value();
      }

      public static Status fromValue(String status) {
         try {
            return valueOf(CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(status, "status")));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromOperation(this);
   }

   public static class Builder {

      private String id;
      private String rawStatus;
      private Status status;
      // When the operation is in progress, no status code is returned
      private Optional<Integer> httpStatusCode = Optional.absent();
      private Optional<Error> error = Optional.absent();

      /**
       * @see Operation#getId()
       */
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see Operation#getRawStatus()
       */
      public Builder rawStatus(String rawStatus) {
         this.rawStatus = rawStatus;
         return this;
      }

      /**
       * @see Operation#getStatus()
       */
      public Builder status(Status status) {
         this.status = status;
         return this;
      }

      /**
       * @see Operation#getHttpStatusCode()
       */
      public Builder httpStatusCode(Integer httpStatusCode) {
         this.httpStatusCode = Optional.fromNullable(httpStatusCode);
         return this;
      }

      /**
       * @see Operation#getError()
       */
      public Builder error(Error error) {
         this.error = Optional.fromNullable(error);
         return this;
      }

      public Operation build() {
         return new Operation(id, rawStatus, status, httpStatusCode, error);
      }

      public Builder fromOperation(Operation in) {
         return this.id(in.id).rawStatus(in.rawStatus).status(in.status).httpStatusCode(in.httpStatusCode.orNull())
                  .error(in.error.orNull());
      }
   }

   private final String id;
   private final String rawStatus;
   private final Status status;
   private final Optional<Integer> httpStatusCode;
   private final Optional<Error> error;

   protected Operation(String id, String rawStatus, Status status, Optional<Integer> httpStatusCode, Optional<Error> error) {
      this.id = checkNotNull(id, "id");
      this.rawStatus = checkNotNull(rawStatus, "rawStatus for %s", id);
      this.status = checkNotNull(status, "status for %s", id);
      this.httpStatusCode = checkNotNull(httpStatusCode, "httpStatusCode for %s", id);
      this.error = checkNotNull(error, "error for %s", id);
   }

   /**
    * The request ID of the asynchronous request.
    */
   public String getId() {
      return id;
   }

   /**
    * The status of the asynchronous request.
    */
   public Status getStatus() {
      return status;
   }

   /**
    * The status of the asynchronous request, unparsed
    */
   public String getRawStatus() {
      return rawStatus;
   }

   /**
    * The HTTP status code for the asynchronous request.
    */
   public Optional<Integer> getHttpStatusCode() {
      return httpStatusCode;
   }

   /**
    * The management service error returned if the asynchronous request failed.
    */
   public Optional<Error> getError() {
      return error;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(id);
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
      Operation other = (Operation) obj;
      return Objects.equal(this.id, other.id);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("id", id).add("status", rawStatus)
               .add("httpStatusCode", httpStatusCode).add("error", error.orNull()).toString();
   }

}
