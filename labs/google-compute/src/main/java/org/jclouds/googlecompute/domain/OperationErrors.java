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
import com.google.common.collect.ImmutableSet;
import org.jclouds.javax.annotation.Nullable;

import java.beans.ConstructorProperties;
import java.util.Set;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.googlecompute.domain.Resource.nullCollectionOnNullOrEmpty;

/**
 * Represents a set of Operation error
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/operations"/>
 */
public class OperationErrors {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromOperationError(this);
   }

   public static class Builder {

      private ImmutableSet.Builder<OperationError> errors = ImmutableSet.builder();


      /**
       * @see OperationErrors#getErrors()
       */
      public Builder addError(OperationError error) {
         this.errors.add(checkNotNull(error));
         return this;
      }

      /**
       * @see OperationErrors#getErrors()
       */
      public Builder error(Set<OperationError> errors) {
         this.errors.addAll(checkNotNull(errors));
         return this;
      }

      public OperationErrors build() {
         return new OperationErrors(errors.build());
      }

      public Builder fromOperationError(OperationErrors operationError) {
         return new Builder().error(operationError.getErrors());
      }
   }


   private final Set<OperationError> errors;

   @ConstructorProperties({
           "errors"
   })
   private OperationErrors(Set<OperationError> errors) {
      this.errors = nullCollectionOnNullOrEmpty(errors);
   }


   /**
    * @return the set of error encountered while processing this operation.
    */
   @Nullable
   public Set<OperationError> getErrors() {
      return errors;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(errors);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      OperationErrors that = OperationErrors.class.cast(obj);
      return equal(this.errors, that.errors);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return toStringHelper(this).add("error", errors);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }


}
