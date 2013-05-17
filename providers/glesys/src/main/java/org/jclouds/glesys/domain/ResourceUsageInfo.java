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
public class ResourceUsageInfo {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromResourceUsageInfo(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected String resource;
      protected String resolution;
      protected String unit;

      /**
       * @see ResourceUsageInfo#getResource()
       */
      public T resource(String resource) {
         this.resource = checkNotNull(resource, "resource");
         return self();
      }

      /**
       * @see ResourceUsageInfo#getResolution()
       */
      public T resolution(String resolution) {
         this.resolution = checkNotNull(resolution, "resolution");
         return self();
      }

      /**
       * @see ResourceUsageInfo#getUnit()
       */
      public T unit(String unit) {
         this.unit = checkNotNull(unit, "unit");
         return self();
      }

      public ResourceUsageInfo build() {
         return new ResourceUsageInfo(resource, resolution, unit);
      }

      public T fromResourceUsageInfo(ResourceUsageInfo in) {
         return this
               .resource(in.getResource())
               .resolution(in.getResolution())
               .unit(in.getUnit());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String resource;
   private final String resolution;
   private final String unit;

   @ConstructorProperties({
         "type", "resolution", "unit"
   })
   protected ResourceUsageInfo(String resource, String resolution, String unit) {
      this.resource = checkNotNull(resource, "resource");
      this.resolution = checkNotNull(resolution, "resolution");
      this.unit = checkNotNull(unit, "unit");
   }

   public String getResource() {
      return this.resource;
   }

   public String getResolution() {
      return this.resolution;
   }

   public String getUnit() {
      return this.unit;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(resource, resolution, unit);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ResourceUsageInfo that = ResourceUsageInfo.class.cast(obj);
      return Objects.equal(this.resource, that.resource)
            && Objects.equal(this.resolution, that.resolution)
            && Objects.equal(this.unit, that.unit);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("")
            .add("resource", resource).add("resolution", resolution).add("unit", unit);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
