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
package org.jclouds.openstack.keystone.v2_0.domain;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Class MediaType
 */
public class MediaType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromMediaType(this);
   }

   public static abstract class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected String base;
      protected String type;

      /**
       * @see MediaType#getBase()
       */
      public T base(String base) {
         this.base = base;
         return self();
      }

      /**
       * @see MediaType#getType()
       */
      public T type(String type) {
         this.type = type;
         return self();
      }

      public MediaType build() {
         return new MediaType(base, type);
      }

      public T fromMediaType(MediaType in) {
         return this
               .base(in.getBase())
               .type(in.getType());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String base;
   private final String type;

   @ConstructorProperties({
         "base", "type"
   })
   protected MediaType(@Nullable String base, @Nullable String type) {
      this.base = base;
      this.type = type;
   }

   @Nullable
   public String getBase() {
      return this.base;
   }

   @Nullable
   public String getType() {
      return this.type;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(base, type);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      MediaType that = MediaType.class.cast(obj);
      return Objects.equal(this.base, that.base)
            && Objects.equal(this.type, that.type);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("base", base).add("type", type);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
