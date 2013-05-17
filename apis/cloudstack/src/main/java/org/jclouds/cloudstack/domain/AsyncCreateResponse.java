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
package org.jclouds.cloudstack.domain;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Class AsyncCreateResponse
 *
 * @author Adrian Cole
 */
public class AsyncCreateResponse {
   public static final AsyncCreateResponse UNINITIALIZED = new AsyncCreateResponse(null, null);

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromAsyncCreateResponse(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected String jobId;

      /**
       * @see AsyncCreateResponse#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see AsyncCreateResponse#getJobId()
       */
      public T jobId(String jobId) {
         this.jobId = jobId;
         return self();
      }

      public AsyncCreateResponse build() {
         return new AsyncCreateResponse(id, jobId);
      }

      public T fromAsyncCreateResponse(AsyncCreateResponse in) {
         return this
               .id(in.getId())
               .jobId(in.getJobId());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final String jobId;

   @ConstructorProperties({
         "id", "jobid"
   })
   protected AsyncCreateResponse(@Nullable String id, @Nullable String jobId) {
      this.id = id;
      this.jobId = jobId;
   }

   /**
    * @return id of the resource being created
    */
   @Nullable
   public String getId() {
      return this.id;
   }

   /**
    * @return id of the job in progress
    */
   @Nullable
   public String getJobId() {
      return this.jobId;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, jobId);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      AsyncCreateResponse that = AsyncCreateResponse.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.jobId, that.jobId);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("jobId", jobId);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
