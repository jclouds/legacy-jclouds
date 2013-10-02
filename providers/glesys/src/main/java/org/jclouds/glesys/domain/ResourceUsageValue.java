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
package org.jclouds.glesys.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Date;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Detailed information on usage
 *
 * @author Adam Lowe
 * @see org.jclouds.glesys.domain.ServerStatus
 */
public class ResourceUsageValue {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromResourceUsage(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected double value;
      protected Date timestamp;


      /**
       * @see ResourceUsageValue#getValue()
       */
      public T value(double value) {
         this.value = value;
         return self();
      }

      /**
       * @see ResourceUsageValue#getTimestamp()
       */
      public T timestamp(Date timestamp) {
         this.timestamp = checkNotNull(timestamp, "timestamp");
         return self();
      }

      public ResourceUsageValue build() {
         return new ResourceUsageValue(value, timestamp);
      }

      public T fromResourceUsage(ResourceUsageValue in) {
         return this
               .value(in.getValue())
               .timestamp(in.getTimestamp());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final double value;
   private final Date timestamp;

   @ConstructorProperties({
         "value", "timestamp"
   })
   protected ResourceUsageValue(double value, Date timestamp) {
      this.value = value;
      this.timestamp = checkNotNull(timestamp, "timestamp");
   }

   public double getValue() {
      return this.value;
   }

   public Date getTimestamp() {
      return this.timestamp;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(value, timestamp);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ResourceUsageValue that = ResourceUsageValue.class.cast(obj);
      return Objects.equal(this.value, that.value)
            && Objects.equal(this.timestamp, that.timestamp);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").add("value", value).add("timestamp", timestamp);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
