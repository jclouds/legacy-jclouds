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
package org.jclouds.sqs.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/MessageLifecycle.html"
 *      >doc</a>
 * 
 * @author Adrian Cole
 */
public class BatchResult<V> extends ForwardingMap<String, V> {

   public static <V> Builder<V> builder() {
      return new Builder<V>();
   }

   public Builder<V> toBuilder() {
      return BatchResult.<V> builder().fromBatchResult(this);
   }

   public static class Builder<V> {

      private ImmutableMap.Builder<String, V> results = ImmutableMap.<String, V> builder();
      private ImmutableSet.Builder<BatchError> errors = ImmutableSet.<BatchError> builder();

      /**
       * @see BatchResult#getErrors()
       */
      public Builder<V> addError(BatchError error) {
         this.errors.add(checkNotNull(error, "error"));
         return this;
      }

      /**
       * @see BatchResult#getErrors()
       */
      public Builder<V> errors(Iterable<BatchError> errors) {
         this.errors = ImmutableSet.<BatchError> builder().addAll(checkNotNull(errors, "errors"));
         return this;
      }

      /**
       * @see BatchResult#get
       */
      public Builder<V> putAll(Map<String, V> results) {
         this.results.putAll(checkNotNull(results, "results"));
         return this;
      }

      /**
       * @see BatchResult#get
       */
      public Builder<V> put(String name, V value) {
         this.results.put(checkNotNull(name, "name"), checkNotNull(value, "value"));
         return this;
      }

      public BatchResult<V> build() {
         return new BatchResult<V>(results.build(), errors.build());
      }

      public Builder<V> fromBatchResult(BatchResult<V> in) {
         return putAll(in).errors(in.getErrors().values());
      }
   }

   private final Map<String, V> results;
   private final Map<String, BatchError> errors;

   private BatchResult(Map<String, V> results, Iterable<BatchError> errors) {
      this.results = ImmutableMap.copyOf(checkNotNull(results, "results"));
      this.errors = Maps.uniqueIndex(checkNotNull(errors, "errors"), new Function<BatchError, String>() {
         @Override
         public String apply(BatchError in) {
            return in.getId();
         }

      });
   }

   @Override
   protected Map<String, V> delegate() {
      return results;
   }

   /**
    * Errors indexed by requestor supplied id
    */
   public Map<String, BatchError> getErrors() {
      return errors;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(results, errors);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      @SuppressWarnings("unchecked")
      BatchResult<V> that = BatchResult.class.cast(obj);
      return Objects.equal(this.results, that.results) && Objects.equal(this.errors, that.errors);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("results", results).add("errors", errors).toString();
   }

}
