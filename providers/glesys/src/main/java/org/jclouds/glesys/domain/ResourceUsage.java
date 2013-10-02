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
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;

/**
 * Detailed information on usage
 *
 * @author Adam Lowe
 * @see ResourceUsageInfo
 * @see ResourceUsageValue
 */
public class ResourceUsage {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromResourceUsages(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected ResourceUsageInfo info;
      protected Set<ResourceUsageValue> values = ImmutableSet.of();

      /**
       * @see ResourceUsage#getInfo()
       */
      public T info(ResourceUsageInfo info) {
         this.info = checkNotNull(info, "info");
         return self();
      }

      /**
       * @see ResourceUsage#getValues()
       */
      public T values(Set<ResourceUsageValue> values) {
         this.values = ImmutableSet.copyOf(checkNotNull(values, "values"));
         return self();
      }

      /**
       * @see ResourceUsage#getValues()
       */
      public T values(ResourceUsageValue... in) {
         return values(ImmutableSet.copyOf(in));
      }

      public ResourceUsage build() {
         return new ResourceUsage(info, values);
      }

      public T fromResourceUsages(ResourceUsage in) {
         return this
               .info(in.getInfo())
               .values(in.getValues());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final ResourceUsageInfo info;
   private final Set<ResourceUsageValue> values;

   @ConstructorProperties({
         "info", "values"
   })
   protected ResourceUsage(ResourceUsageInfo info, Set<ResourceUsageValue> values) {
      this.info = checkNotNull(info, "info");
      this.values = ImmutableSet.copyOf(checkNotNull(values, "values"));
   }

   public ResourceUsageInfo getInfo() {
      return this.info;
   }

   public Set<ResourceUsageValue> getValues() {
      return this.values;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(info, values);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ResourceUsage that = ResourceUsage.class.cast(obj);
      return Objects.equal(this.info, that.info)
            && Objects.equal(this.values, that.values);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("")
            .add("info", info).add("values", values);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
