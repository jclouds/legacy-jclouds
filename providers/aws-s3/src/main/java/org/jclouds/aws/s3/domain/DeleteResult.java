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
package org.jclouds.aws.s3.domain;

import com.google.common.base.Objects;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Multi-object delete API response
 * <p/>
 * Contains a list of the keys that were deleted
 *
 * @author Andrei Savu
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/latest/API/multiobjectdeleteapi.html" />
 */
public class DeleteResult extends ForwardingSet<String> {

   public static class Error {

      private final String code;
      private final String message;

      public Error(String code, String message) {
         this.code = checkNotNull(code, "code is null");
         this.message = checkNotNull(message, "message is null");
      }

      public String getCode() {
         return code;
      }

      public String getMessage() {
         return message;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Error)) return false;

         Error that = (Error) o;

         return Objects.equal(code, that.code)
            && Objects.equal(message, that.message);
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(code, message);
      }

      @Override
      public String toString() {
         return Objects.toStringHelper(this).omitNullValues()
            .add("code", code).add("message", message).toString();
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromDeleteResult(this);
   }

   public static class Builder {

      private ImmutableSet.Builder<String> deleted = ImmutableSet.builder();
      private ImmutableMap.Builder<String, Error> errors = ImmutableMap.builder();

      /**
       * @see DeleteResult#getErrors
       */
      public Builder putError(String key, Error error) {
         this.errors.put(key, error);
         return this;
      }

      /**
       * @see DeleteResult#getErrors
       */
      public Builder errors(Map<String, Error> errors) {
         this.errors = ImmutableMap.<String, Error>builder().putAll(errors);
         return this;
      }

      /**
       * @see DeleteResult#getDeleted
       */
      public Builder deleted(Iterable<String> deleted) {
         this.deleted = ImmutableSet.<String>builder().addAll(deleted);
         return this;
      }

      /**
       * @see DeleteResult#getDeleted
       */
      public Builder add(String key) {
         this.deleted.add(key);
         return this;
      }

      /**
       * @see DeleteResult#getDeleted
       */
      public Builder addAll(Iterable<String> key) {
         this.deleted.addAll(key);
         return this;
      }

      public DeleteResult build() {
         return new DeleteResult(deleted.build(), errors.build());
      }

      public Builder fromDeleteResult(DeleteResult result) {
         return addAll(result.getDeleted()).errors(result.getErrors());
      }
   }

   private final Set<String> deleted;
   private final Map<String, Error> errors;

   public DeleteResult(Set<String> deleted, Map<String, Error> errors) {
      this.deleted = ImmutableSet.copyOf(deleted);
      this.errors = ImmutableMap.copyOf(errors);
   }

   /**
    * Get the set of successfully deleted keys
    */
   public Set<String> getDeleted() {
      return deleted;
   }

   /**
    * Get a map with details about failed delete operations indexed by object name
    */
   public Map<String, Error> getErrors() {
      return errors;
   }

   @Override
   protected Set<String> delegate() {
      return deleted;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof DeleteResult)) return false;

      DeleteResult that = (DeleteResult) o;

      return Objects.equal(errors, that.errors)
         && Objects.equal(deleted, that.deleted);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(deleted, errors);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues()
         .add("deleted", deleted).add("errors", errors).toString();
   }
}
