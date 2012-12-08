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

package org.jclouds.googlecompute.domain;

import com.google.common.base.Objects;

import java.beans.ConstructorProperties;
import java.util.Date;

/**
 * Represents a kernel.
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/kernels"/>
 */
public class Kernel extends Resource {


   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromKernel(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends Resource.Builder<T> {

      public Kernel build() {
         return new Kernel(super.id, super.creationTimestamp, super.selfLink, super.name,
                 super.description);
      }

      public T fromKernel(Kernel in) {
         return super.fromResource(in);
      }

   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }


   @ConstructorProperties({
           "id", "creationTimestamp", "selfLink", "name", "description"
   })
   protected Kernel(String id, Date creationTimestamp, String selfLink, String name, String description) {
      super(Kind.KERNEL, id, creationTimestamp, selfLink, name, description);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return super.hashCode();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Kernel that = Kernel.class.cast(obj);
      return super.equals(that);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return super.string();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

}
