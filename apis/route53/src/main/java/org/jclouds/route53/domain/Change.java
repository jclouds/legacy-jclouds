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
package org.jclouds.route53.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

import com.google.common.base.Objects;

/**
 * @author Adrian Cole
 */
public final class Change {

   private final String id;
   private final Status status;
   private final Date submittedAt;

   private Change(String id, Status status, Date submittedAt) {
      this.id = checkNotNull(id, "id");
      this.status = checkNotNull(status, "status for %s", id);
      this.submittedAt = checkNotNull(submittedAt, "submittedAt for %s", id);
   }

   /**
    * The ID of the change batch.
    */
   public String getId() {
      return id;
   }

   /**
    * The current status of the change batch request.
    */
   public Status getStatus() {
      return status;
   }

   /**
    * The date and time that the change batch request was submitted.
    */
   public Date getSubmittedAt() {
      return submittedAt;
   }

   public enum Status {
      /**
       * indicates that the changes in this request have not replicated to all
       * Amazon Route 53 DNS servers.
       */
      PENDING,
      /**
       * indicates that the changes have replicated to all Amazon Route 53 DNS
       * servers.
       */
      INSYNC, UNRECOGNIZED;

      public static Status fromValue(String status) {
         try {
            return valueOf(checkNotNull(status, "status"));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      Change that = Change.class.cast(obj);
      return equal(this.id, that.id);
   }

   @Override
   public String toString() {
      return toStringHelper(this).add("id", id).add("status", status).add("submittedAt", submittedAt).toString();
   }

   public static Change create(String id, Status status, Date submittedAt) {
      return new Change(id, status, submittedAt);
   }
}
