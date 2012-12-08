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
import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Describes an operation being executed on some Resource
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/operations"/>
 */
public class Operation extends Resource {

   public enum Status {
      PENDING,
      RUNNING,
      DONE
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromOperation(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends Resource.Builder<T> {

      private String targetLink;
      private String targetId;
      private String clientOperationId;
      private Status status;
      private String statusMessage;
      private String user;
      private int progress;
      private Date insertTime;
      private Date startTime;
      private Date endTime;
      private int httpErrorStatusCode;
      private String httpErrorMessage;
      private String operationType;
      private OperationErrors error;

      /**
       * @see Operation#getTargetLink()
       */
      public T targetLink(String targetLink) {
         this.targetLink = targetLink;
         return self();
      }

      /**
       * @see Operation#getTargetId()
       */
      public T targetId(String targetId) {
         this.targetId = targetId;
         return self();
      }

      /**
       * @see Operation#getClientOperationId()
       */
      public T clientOperationId(String clientOperationId) {
         this.clientOperationId = clientOperationId;
         return self();
      }

      /**
       * @see Operation#getStatus()
       */
      public T status(Status status) {
         this.status = status;
         return self();
      }

      /**
       * @see Operation#getStatusMessage()
       */
      public T statusMessage(String statusMessage) {
         this.statusMessage = statusMessage;
         return self();
      }

      /**
       * @see Operation#getUser()
       */
      public T user(String user) {
         this.user = user;
         return self();
      }

      /**
       * @see Operation#getProgress()
       */
      public T progress(int progress) {
         this.progress = progress;
         return self();
      }

      /**
       * @see Operation#getInsertTime()
       */
      public T insertTime(Date insertTime) {
         this.insertTime = insertTime;
         return self();
      }

      /**
       * @see Operation#getStartTime()
       */
      public T startTime(Date startTime) {
         this.startTime = startTime;
         return self();
      }

      /**
       * @see Operation#getEndTime()
       */
      public T endTime(Date endTime) {
         this.endTime = endTime;
         return self();
      }

      /**
       * @see Operation#getHttpErrorStatusCode()
       */
      public T httpErrorStatusCode(int httpErrorStatusCode) {
         this.httpErrorStatusCode = httpErrorStatusCode;
         return self();
      }

      /**
       * @see Operation#getHttpErrorMessage()
       */
      public T httpErrorMessage(String httpErrorMessage) {
         this.httpErrorMessage = httpErrorMessage;
         return self();
      }

      /**
       * @see Operation#getOperationType()
       */
      public T operationType(String operationType) {
         this.operationType = operationType;
         return self();
      }

      /**
       * @see Operation#getError()
       */
      public T error(OperationErrors error) {
         this.error = error;
         return self();
      }

      public Operation build() {
         return new Operation(super.id, super.creationTimestamp, super.selfLink, super.name,
                 super.description, targetLink, targetId, clientOperationId, status, statusMessage, user, progress,
                 insertTime, startTime, endTime, httpErrorStatusCode, httpErrorMessage, operationType,
                 error);
      }

      public T fromOperation(Operation in) {
         return super.fromResource(in).targetLink(in.getTargetLink()).targetId(in.getTargetId()).clientOperationId(in
                 .getClientOperationId()).status(in.getStatus()).statusMessage(in.getStatusMessage()).user(in.getUser
                 ()).progress(in.getProgress()).insertTime(in.getInsertTime()).startTime(in.getStartTime()).endTime
                 (in.getEndTime()).httpErrorStatusCode(in.getHttpErrorStatusCode()).httpErrorMessage(in
                 .getHttpErrorMessage()).operationType(in.getOperationType()).error(in.getError());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String targetLink;
   private final String targetId;
   private final String clientOperationId;
   private final Status status;
   private final String statusMessage;
   private final String user;
   private final int progress;
   private final Date insertTime;
   private final Date startTime;
   private final Date endTime;
   private final int httpErrorStatusCode;
   private final String httpErrorMessage;
   private final String operationType;
   private final OperationErrors error;

   @ConstructorProperties({
           "id", "creationTimestamp", "selfLink", "name", "description", "targetLink", "targetId",
           "clientOperationId", "status", "statusMessage", "user", "progress", "insertTime", "startTime", "endTime",
           "httpErrorStatusCode", "httpErrorMessage", "operationType", "error"
   })
   protected Operation(String id, Date creationTimestamp, String selfLink, String name, String description,
                       String targetLink, String targetId, String clientOperationId, Status status,
                       String statusMessage, String user, int progress, Date insertTime, Date startTime,
                       Date endTime, int httpErrorStatusCode, String httpErrorMessage, String operationType,
                       OperationErrors error) {
      super(Kind.OPERATION, id, creationTimestamp, selfLink, checkNotNull(name), description);
      this.targetLink = targetLink;
      this.targetId = targetId;
      this.clientOperationId = clientOperationId;
      this.status = status;
      this.statusMessage = statusMessage;
      this.user = user;
      this.progress = progress;
      this.insertTime = insertTime;
      this.startTime = startTime;
      this.endTime = endTime;
      this.httpErrorStatusCode = httpErrorStatusCode;
      this.httpErrorMessage = httpErrorMessage;
      this.operationType = operationType;
      this.error = error;
   }

   /**
    * @return URL of the resource the operation is mutating (output only).
    */
   public String getTargetLink() {
      return targetLink;
   }

   /**
    * @return unique target id which identifies a particular incarnation of the target (output only).
    */
   public String getTargetId() {
      return targetId;
   }

   /**
    * @return An optional identifier specified by the client when the mutation was initiated. Must be unique for all
    *         operation resources in the project (output only).
    */
   @Nullable
   public String getClientOperationId() {
      return clientOperationId;
   }

   /**
    * @return Status of the operation. Can be one of the following: PENDING, RUNNING, or DONE (output only).
    */
   public Status getStatus() {
      return status;
   }

   /**
    * @return An optional textual description of the current status of the operation (output only).
    */
   @Nullable
   public String getStatusMessage() {
      return statusMessage;
   }

   /**
    * @return User who requested the operation, for example "user@example.com" (output only).
    */
   public String getUser() {
      return user;
   }

   /**
    * @return an optional progress indicator that ranges from 0 to 100. There is no requirement that this be linear
    *         or support any granularity of operations. This should not be used to guess at when the operation will
    *         be complete. This number should be monotonically increasing as the operation progresses (output only).
    */
   @Nullable
   public int getProgress() {
      return progress;
   }

   /**
    * @return the time that this operation was requested. This is in RFC3339 format (output only).
    */
   public Date getInsertTime() {
      return insertTime;
   }

   /**
    * @return the time that this operation was started by the server. This is in RFC3339 format (output only).
    */
   public Date getStartTime() {
      return startTime;
   }

   /**
    * @return the time that this operation was completed. This is in RFC3339 format (output only).
    */
   public Date getEndTime() {
      return endTime;
   }

   /**
    * @return if operation fails, the HTTP error status code returned, e.g. 404. (output only).
    */
   public int getHttpErrorStatusCode() {
      return httpErrorStatusCode;
   }

   /**
    * @return if operation fails, the HTTP error message returned, e.g. NOT FOUND. (output only).
    */
   public String getHttpErrorMessage() {
      return httpErrorMessage;
   }

   /**
    * @return type of the operation. Examples include insert, update, and delete (output only).
    */
   public String getOperationType() {
      return operationType;
   }

   /**
    * @return if error occurred during processing of this operation, this field will be populated (output only).
    */
   @Nullable
   public OperationErrors getError() {
      return error;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(kind, id, creationTimestamp, selfLink, name, description, targetLink, targetId,
              clientOperationId, status, statusMessage, user, progress, insertTime, startTime, endTime,
              httpErrorStatusCode, httpErrorMessage, operationType, error);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Operation that = Operation.class.cast(obj);
      return super.equals(that)
              && Objects.equal(this.targetLink, that.targetLink)
              && Objects.equal(this.targetId, that.targetId)
              && Objects.equal(this.clientOperationId, that.clientOperationId)
              && Objects.equal(this.status, that.status)
              && Objects.equal(this.statusMessage, that.statusMessage)
              && Objects.equal(this.user, that.user)
              && Objects.equal(this.progress, that.progress)
              && Objects.equal(this.insertTime, that.insertTime)
              && Objects.equal(this.startTime, that.startTime)
              && Objects.equal(this.endTime, that.endTime)
              && Objects.equal(this.httpErrorStatusCode, that.httpErrorStatusCode)
              && Objects.equal(this.httpErrorMessage, that.httpErrorMessage)
              && Objects.equal(this.operationType, that.operationType)
              && Objects.equal(this.error, that.error);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return super.string()
              .add("targetLink", targetLink)
              .add("targetId", targetId)
              .add("clientOperationId", clientOperationId)
              .add("status", status)
              .add("statusMessage", statusMessage)
              .add("user", user)
              .add("progress", progress)
              .add("insertTime", insertTime)
              .add("startTime", startTime)
              .add("endTime", endTime)
              .add("httpErrorStatusCode", httpErrorStatusCode)
              .add("httpErrorMessage", httpErrorMessage)
              .add("operationType", operationType)
              .add("error", error);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }
}
