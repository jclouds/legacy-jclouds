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

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Detailed information on usage
 *
 * @author Adam Lowe
 * @see ServerStatus
 */
public class ResourceStatus {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromResourceUsage(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected double usage;
      protected double max;
      protected String unit;

      /**
       * @see ResourceStatus#getUsage()
       */
      public T usage(double usage) {
         this.usage = usage;
         return self();
      }

      /**
       * @see ResourceStatus#getMax()
       */
      public T max(double max) {
         this.max = max;
         return self();
      }

      /**
       * @see ResourceStatus#getUnit()
       */
      public T unit(String unit) {
         this.unit = checkNotNull(unit, "unit");
         return self();
      }

      public ResourceStatus build() {
         return new ResourceStatus(usage, max, unit);
      }

      public T fromResourceUsage(ResourceStatus in) {
         return this.usage(in.getUsage()).max(in.getMax()).unit(in.getUnit());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final double usage;
   private final double max;
   private final String unit;

   @ConstructorProperties({
         "usage", "max", "unit"
   })
   protected ResourceStatus(double usage, double max, String unit) {
      this.usage = usage;
      this.max = max;
      this.unit = checkNotNull(unit, "unit");
   }

   /**
    * @return the usage in #unit
    */
   public double getUsage() {
      return this.usage;
   }

   /**
    * @return the max usage in #unit
    */
   public double getMax() {
      return this.max;
   }

   /**
    * @return the unit used
    */
   public String getUnit() {
      return this.unit;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(usage, max, unit);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ResourceStatus that = ResourceStatus.class.cast(obj);
      return Objects.equal(this.usage, that.usage)
            && Objects.equal(this.max, that.max)
            && Objects.equal(this.unit, that.unit);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").add("usage", usage).add("max", max).add("unit", unit);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
