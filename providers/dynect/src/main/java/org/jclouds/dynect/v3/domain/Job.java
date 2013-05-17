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
package org.jclouds.dynect.v3.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import javax.inject.Named;

import com.google.common.base.Objects;

/**
 * @author Adrian Cole
 */
public final class Job {

   @Named("job_id")
   private final long id;
   private final Status status;

   @ConstructorProperties({ "job_id", "status" })
   private Job(long id, Status status) {
      this.id = checkNotNull(id, "id");
      this.status = checkNotNull(status, "status for %s", id);
   }

   /**
    * The ID of the job.
    */
   public long getId() {
      return id;
   }

   /**
    * The current status of the job.
    */
   public Status getStatus() {
      return status;
   }

   public enum Status {
      SUCCESS, FAILURE, UNRECOGNIZED;

      public static Status fromValue(String status) {
         try {
            return valueOf(checkNotNull(status, "status").toUpperCase());
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
      Job that = Job.class.cast(obj);
      return equal(this.id, that.id);
   }

   @Override
   public String toString() {
      return toStringHelper(this).add("id", id).add("status", status).toString();
   }

   public static Job success(long id) {
      return new Job(id, Status.SUCCESS);
   }

   public static Job failure(long id) {
      return new Job(id, Status.FAILURE);
   }
}
