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
package org.jclouds.rackspace.clouddns.v1.domain;

import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import org.jclouds.rackspace.clouddns.v1.CloudDNSApi;

import com.google.common.base.Optional;

/**
 * @see CloudDNSApi#getJob(String)
 * @author Everett Toews
 */
public class Job<T> {
   private final String id;
   private final Status status;
   private final Optional<Error> error;
   private final Optional<T> resource;
   
   private Job(String id, Status status, Optional<Error> error, Optional<T> resource) {
      this.id = id;
      this.status = status;
      this.error = error;
      this.resource = resource;
   }

   public String getId() {
      return id;
   }

   public Status getStatus() {
      return status;
   }

   public Optional<Error> getError() {
      return error;
   }

   public Optional<T> getResource() {
      return resource;
   }

   public enum Status {
      /**
       * INITIALIZED is the status that immediately precedes RUNNING and is the first possible state of a job. 
       * It indicates acceptance of the job.
       */
      INITIALIZED,

      RUNNING, COMPLETED, ERROR, UNRECOGNIZED;

      public static Status fromValue(String status) {
         try {
            return valueOf(checkNotNull(status, "status").toUpperCase());
         }
         catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }
   
   public static <T> Builder<T> builder() {
      return new Builder<T>();
   }

   public static class Builder<T> {
      private String id;
      private Status status;
      private Optional<Error> error = Optional.absent();
      private Optional<T> resource = Optional.absent();

      public Builder<T> id(String id) {
         this.id = id;
         return this;
      }

      public Builder<T> status(Status status) {
         this.status = status;
         return this;
      }

      public Builder<T> error(Error error) {
         this.error = Optional.fromNullable(error);
         return this;
      }

      public Builder<T> resource(T resource) {
         this.resource = Optional.fromNullable(resource);
         return this;
      }
      
      public Job<T> build() {
         return new Job<T>(id, status, error, resource);
      }
   }

   public static final class Error {

      private final int code;
      private final String message;
      private final String details;

      @ConstructorProperties({ "code", "message", "details" })
      protected Error(int code, String message, String details) {
         this.code = code;
         this.message = message;
         this.details = details;
      }

      public int getCode() {
         return code;
      }

      public String getMessage() {
         return message;
      }

      public String getDetails() {
         return details;
      }

      @Override
      public String toString() {
         return toStringHelper(this).add("code", code).add("message", message).add("details", details).toString();
      }
   }
}
