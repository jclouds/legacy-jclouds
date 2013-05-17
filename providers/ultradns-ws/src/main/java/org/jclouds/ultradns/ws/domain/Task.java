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
package org.jclouds.ultradns.ws.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * 
 * @author Adrian Cole
 */
public final class Task {

   private final String guid;
   private final StatusCode statusCode;
   private final Optional<String> message;
   private final Optional<URI> resultUrl;

   private Task(String guid, StatusCode statusCode, Optional<String> message, Optional<URI> resultUrl) {
      this.guid = checkNotNull(guid, "guid");
      this.statusCode = checkNotNull(statusCode, "statusCode for %s", guid);
      this.message = checkNotNull(message, "message for %s", guid);
      this.resultUrl = checkNotNull(resultUrl, "resultUrl for %s", guid);
   }

   /**
    * The guid of the task. ex. {@statusCode 0b40c7dd-748d-4c49-8506-26f0c7d2ea9c}
    */
   public String getGuid() {
      return guid;
   }

   /**
    * The status statusCode of the task
    */
   public StatusCode getStatusCode() {
      return statusCode;
   }

   /**
    * The message pertaining to status
    */
   public Optional<String> getMessage() {
      return message;
   }

   /**
    * present when {@link #getClass()} is {@link StatusCode#COMPLETE}
    */
   public Optional<URI> getResultUrl() {
      return resultUrl;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(guid);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      Task that = Task.class.cast(obj);
      return Objects.equal(this.guid, that.guid);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("guid", guid).add("statusCode", statusCode).add("message", message)
            .add("resultUrl", resultUrl).toString();
   }

   public static enum StatusCode {

      PENDING, IN_PROCESS,
      /**
       * For a task with status statusCode {@statusCode COMPLETE}, copy and paste the resultUrl into a browser to pull down the
       * file, which will be an attachment with the proper MIME type and extension.
       */
      COMPLETE, ERROR;

      public static StatusCode fromValue(String statusCode) {
         return valueOf(checkNotNull(statusCode, "statusCode"));
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }

   public static final class Builder {
      private String guid;
      private StatusCode statusCode;
      private Optional<String> message = Optional.absent();
      private Optional<URI> resultUrl = Optional.absent();

      /**
       * @see Task#getGuid()
       */
      public Builder guid(String guid) {
         this.guid = guid;
         return this;
      }

      /**
       * @see Task#getStatusCode()
       */
      public Builder statusCode(StatusCode statusCode) {
         this.statusCode = statusCode;
         return this;
      }

      /**
       * @see Task#getMessage()
       */
      public Builder message(String message) {
         this.message = Optional.fromNullable(message);
         return this;
      }

      /**
       * @see Task#getResultUrl()
       */
      public Builder resultUrl(URI resultUrl) {
         this.resultUrl = Optional.fromNullable(resultUrl);
         return this;
      }

      public Task build() {
         return new Task(guid, statusCode, message, resultUrl);
      }

      public Builder from(Task in) {
         return this.guid(in.guid).statusCode(in.statusCode).message(in.message.orNull()).resultUrl(in.resultUrl.orNull());
      }
   }
}
