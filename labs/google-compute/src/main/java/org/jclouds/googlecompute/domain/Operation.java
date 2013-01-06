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

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.jclouds.http.HttpResponse;

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Describes an operation being executed on some Resource
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/operations"/>
 */
@Beta
public class Operation extends Resource {

   public static enum Status {
      PENDING,
      RUNNING,
      DONE
   }

   private final URI targetLink;
   private final Optional<String> targetId;
   private final Optional<String> clientOperationId;
   private final Status status;
   private final Optional<String> statusMessage;
   private final String user;
   private final Optional<Integer> progress;
   private final Date insertTime;
   private final Optional<Date> startTime;
   private final Optional<Date> endTime;
   private final Optional<HttpResponse> httpError;
   private final String operationType;
   private final List<Error> errors;

   protected Operation(String id, Date creationTimestamp, URI selfLink, String name, String description,
                       URI targetLink, String targetId, String clientOperationId, Status status,
                       String statusMessage, String user, Integer progress, Date insertTime, Date startTime,
                       Date endTime, Integer httpErrorStatusCode, String httpErrorMessage, String operationType,
                       List<Error> errors) {
      super(Kind.OPERATION, checkNotNull(id, "id of %s", name), fromNullable(creationTimestamp),
              checkNotNull(selfLink, "selfLink of %s", name), checkNotNull(name, "name"), fromNullable(description));
      this.targetLink = checkNotNull(targetLink, "targetLink of %s", name);
      this.targetId = fromNullable(targetId);
      this.clientOperationId = fromNullable(clientOperationId);
      this.status = checkNotNull(status, "status of %s", name);
      this.statusMessage = fromNullable(statusMessage);
      this.user = checkNotNull(user, "user of %s", name);
      this.progress = fromNullable(progress);
      this.insertTime = checkNotNull(insertTime, "insertTime of %s", name);
      this.startTime = fromNullable(startTime);
      this.endTime = fromNullable(endTime);
      this.httpError = httpErrorStatusCode != null ?
              Optional.of(HttpResponse.builder()
                      .statusCode(httpErrorStatusCode)
                      .message(httpErrorMessage)
                      .build())
              : Optional.<HttpResponse>absent();
      this.operationType = checkNotNull(operationType, "insertTime of %s", name);
      this.errors = errors == null ? ImmutableList.<Error>of() : ImmutableList.copyOf(errors);
   }

   /**
    * @return URL of the resource the operation is mutating.
    */
   public URI getTargetLink() {
      return targetLink;
   }

   /**
    * @return unique target id which identifies a particular incarnation of the target.
    */
   public Optional<String> getTargetId() {
      return targetId;
   }

   /**
    * @return An optional identifier specified by the client when the mutation was initiated. Must be unique for all
    *         operation resources in the project.
    */
   public Optional<String> getClientOperationId() {
      return clientOperationId;
   }

   /**
    * @return Status of the operation. Can be one of the following: PENDING, RUNNING, or DONE.
    */
   public Status getStatus() {
      return status;
   }

   /**
    * @return An optional textual description of the current status of the operation.
    */
   public Optional<String> getStatusMessage() {
      return statusMessage;
   }

   /**
    * @return User who requested the operation, for example "user@example.com".
    */
   public String getUser() {
      return user;
   }

   /**
    * @return an optional progress indicator that ranges from 0 to 100. This should not be used to guess at when the
    *         operation will be complete. This number should be monotonically increasing as the operation progresses
    *         (output only).
    */
   public Optional<Integer> getProgress() {
      return progress;
   }

   /**
    * @return the time that this operation was requested.
    */
   public Date getInsertTime() {
      return insertTime;
   }

   /**
    * @return the time that this operation was started by the server.
    */
   public Optional<Date> getStartTime() {
      return startTime;
   }

   /**
    * @return the time that this operation was completed.
    */
   public Optional<Date> getEndTime() {
      return endTime;
   }

   /**
    * @return if operation fails, the HttpResponse with error status code returned and the message, e.g. NOT_FOUND.
    */
   public Optional<HttpResponse> getHttpError() {
      return httpError;
   }

   /**
    * @return type of the operation. Examples include insert, update, and delete.
    */
   public String getOperationType() {
      return operationType;
   }

   /**
    * @return if error occurred during processing of this operation, this field will be populated.
    */
   public List<Error> getErrors() {
      return errors;
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return super.string()
              .omitNullValues()
              .add("targetLink", targetLink)
              .add("targetId", targetId.orNull())
              .add("clientOperationId", clientOperationId.orNull())
              .add("status", status)
              .add("statusMessage", statusMessage.orNull())
              .add("user", user)
              .add("progress", progress.orNull())
              .add("insertTime", insertTime)
              .add("startTime", startTime.orNull())
              .add("endTime", endTime.orNull())
              .add("httpError", httpError.orNull())
              .add("operationType", operationType)
              .add("errors", errors);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromOperation(this);
   }

   public static final class Builder extends Resource.Builder<Builder> {

      private URI targetLink;
      private String targetId;
      private String clientOperationId;
      private Status status;
      private String statusMessage;
      private String user;
      private Integer progress;
      private Date insertTime;
      private Date startTime;
      private Date endTime;
      private Integer httpErrorStatusCode;
      private String httpErrorMessage;
      private String operationType;
      private ImmutableList.Builder<Error> errors = ImmutableList.builder();

      /**
       * @see Operation#getTargetLink()
       */
      public Builder targetLink(URI targetLink) {
         this.targetLink = targetLink;
         return self();
      }

      /**
       * @see Operation#getTargetId()
       */
      public Builder targetId(String targetId) {
         this.targetId = targetId;
         return self();
      }

      /**
       * @see Operation#getClientOperationId()
       */
      public Builder clientOperationId(String clientOperationId) {
         this.clientOperationId = clientOperationId;
         return self();
      }

      /**
       * @see Operation#getStatus()
       */
      public Builder status(Status status) {
         this.status = status;
         return self();
      }

      /**
       * @see Operation#getStatusMessage()
       */
      public Builder statusMessage(String statusMessage) {
         this.statusMessage = statusMessage;
         return self();
      }

      /**
       * @see Operation#getUser()
       */
      public Builder user(String user) {
         this.user = user;
         return self();
      }

      /**
       * @see Operation#getProgress()
       */
      public Builder progress(Integer progress) {
         this.progress = progress;
         return self();
      }

      /**
       * @see Operation#getInsertTime()
       */
      public Builder insertTime(Date insertTime) {
         this.insertTime = insertTime;
         return self();
      }

      /**
       * @see Operation#getStartTime()
       */
      public Builder startTime(Date startTime) {
         this.startTime = startTime;
         return self();
      }

      /**
       * @see Operation#getEndTime()
       */
      public Builder endTime(Date endTime) {
         this.endTime = endTime;
         return self();
      }

      /**
       * @see Operation#getHttpError()
       */
      public Builder httpErrorStatusCode(Integer httpErrorStatusCode) {
         this.httpErrorStatusCode = httpErrorStatusCode;
         return self();
      }

      /**
       * @see Operation#getHttpError()
       */
      public Builder httpErrorMessage(String httpErrorMessage) {
         this.httpErrorMessage = httpErrorMessage;
         return self();
      }

      /**
       * @see Operation#getOperationType()
       */
      public Builder operationType(String operationType) {
         this.operationType = operationType;
         return self();
      }

      /**
       * @see Operation#getErrors()
       */
      public Builder errors(Iterable<Error> errors) {
         if (errors != null)
            this.errors.addAll(errors);
         return self();
      }

      /**
       * @see Operation#getErrors()
       */
      public Builder addError(Error error) {
         this.errors.add(error);
         return self();
      }

      @Override
      protected Builder self() {
         return this;
      }

      public Operation build() {
         return new Operation(super.id, super.creationTimestamp, super.selfLink, super.name,
                 super.description, targetLink, targetId, clientOperationId, status, statusMessage, user, progress,
                 insertTime, startTime, endTime, httpErrorStatusCode, httpErrorMessage, operationType,
                 errors.build());
      }

      public Builder fromOperation(Operation in) {
         return super.fromResource(in)
                 .targetLink(in.getTargetLink())
                 .targetId(in.getTargetId().orNull())
                 .clientOperationId(in.getClientOperationId().orNull())
                 .status(in.getStatus())
                 .statusMessage(in.getStatusMessage().orNull())
                 .user(in.getUser())
                 .progress(in.getProgress().get())
                 .insertTime(in.getInsertTime())
                 .startTime(in.getStartTime().orNull())
                 .endTime(in.getEndTime().orNull())
                 .httpErrorStatusCode(in.getHttpError().isPresent() ? in.getHttpError().get().getStatusCode() : null)
                 .httpErrorMessage(in.getHttpError().isPresent() ? in.getHttpError().get().getMessage() : null)
                 .operationType(in.getOperationType()).errors(in.getErrors());
      }
   }

   /**
    * A particular error for an operation including the details.
    */
   public static final class Error {

      private final String code;
      private final Optional<String> location;
      private final Optional<String> message;

      @ConstructorProperties({
              "code", "location", "message"
      })
      private Error(String code, String location, String message) {
         this.code = checkNotNull(code, "code");
         this.location = fromNullable(location);
         this.message = fromNullable(message);
      }

      /**
       * @return the error type identifier for this error.
       */
      public String getCode() {
         return code;
      }

      /**
       * @return indicates the field in the request which caused the error..
       */
      public Optional<String> getLocation() {
         return location;
      }

      /**
       * @return an optional, human-readable error message.
       */
      public Optional<String> getMessage() {
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
         Error that = Error.class.cast(obj);
         return equal(this.code, that.code)
                 && equal(this.location, that.location)
                 && equal(this.message, that.message);
      }

      /**
       * {@inheritDoc}
       */
      protected Objects.ToStringHelper string() {
         return toStringHelper(this)
                 .omitNullValues()
                 .add("code", code)
                 .add("location", location.orNull())
                 .add("message", message.orNull());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String toString() {
         return string().toString();
      }

      public static Builder builder() {
         return new Builder();
      }

      public Builder toBuilder() {
         return builder().fromOperationErrorDetail(this);
      }

      public static final class Builder {

         private String code;
         private String location;
         private String message;

         /**
          * @see org.jclouds.googlecompute.domain.Operation.Error#getCode()
          */
         public Builder code(String code) {
            this.code = code;
            return this;
         }

         /**
          * @see org.jclouds.googlecompute.domain.Operation.Error#getLocation()
          */
         public Builder location(String location) {
            this.location = location;
            return this;
         }

         /**
          * @see org.jclouds.googlecompute.domain.Operation.Error#getMessage()
          */
         public Builder message(String message) {
            this.message = message;
            return this;
         }

         public Error build() {
            return new Error(code, location, message);
         }

         public Builder fromOperationErrorDetail(Error in) {
            return new Builder().code(in.getCode()).location(in.getLocation().orNull()).message
                    (in.getMessage().orNull());
         }
      }
   }
}
