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
package org.jclouds.rds.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.CaseFormat;
import com.google.common.base.Objects;

/**
 * 
 * @author Adrian Cole
 */
public class Authorization {
   /**
    * Status of a source of traffic to the security group
    */
   public static enum Status {

      AUTHORIZING, AUTHORIZED, REVOKING, UNRECOGNIZED;

      public String value() {
         return name().toLowerCase();
      }

      @Override
      public String toString() {
         return value();
      }

      public static Status fromValue(String status) {
         try {
            return valueOf(CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(status, "status")));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromAuthorization(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String rawStatus;
      protected Status status;

      /**
       * @see Authorization#getRawStatus()
       */
      public T rawStatus(String rawStatus) {
         this.rawStatus = rawStatus;
         return self();
      }

      /**
       * @see Authorization#getStatus()
       */
      public T status(Status status) {
         this.status = status;
         return self();
      }

      public T fromAuthorization(Authorization in) {
         return this.rawStatus(in.getRawStatus()).status(in.getStatus());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   protected final String rawStatus;
   protected final Status status;

   protected Authorization(String rawStatus, Status status) {
      this.rawStatus = checkNotNull(rawStatus, "rawStatus");
      this.status = checkNotNull(status, "status");
   }

   /**
    * Specifies the status of the authorization.
    */
   public Status getStatus() {
      return status;
   }

   /**
    * Specifies the status of the authorization.
    */
   public String getRawStatus() {
      return rawStatus;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(rawStatus);
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
      Authorization other = (Authorization) obj;
      return Objects.equal(this.rawStatus, other.rawStatus);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("status", rawStatus).toString();
   }

}
