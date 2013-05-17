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
 * The result of an operation.
 * <p/>
 * A handful of Cloudstack API calls return this structure when there is no domain model data to return - for example,
 * when deleting an object.
 *
 * @author Richard Downer
 */
public class JobResult {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromJobResult(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected boolean success;
      protected String displayText;

      /**
       * @see JobResult#isSuccess()
       */
      public T success(boolean success) {
         this.success = success;
         return self();
      }

      /**
       * @see JobResult#getDisplayText()
       */
      public T displayText(String displayText) {
         this.displayText = displayText;
         return self();
      }

      public JobResult build() {
         return new JobResult(success, displayText);
      }

      public T fromJobResult(JobResult in) {
         return this
               .success(in.isSuccess())
               .displayText(in.getDisplayText());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final boolean success;
   private final String displayText;

   @ConstructorProperties({
         "success", "displaytext"
   })
   protected JobResult(boolean success, @Nullable String displayText) {
      this.success = success;
      this.displayText = displayText;
   }

   public boolean isSuccess() {
      return this.success;
   }

   @Nullable
   public String getDisplayText() {
      return this.displayText;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(success, displayText);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      JobResult that = JobResult.class.cast(obj);
      return Objects.equal(this.success, that.success)
            && Objects.equal(this.displayText, that.displayText);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).add("success", success).add("displayText", displayText);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
