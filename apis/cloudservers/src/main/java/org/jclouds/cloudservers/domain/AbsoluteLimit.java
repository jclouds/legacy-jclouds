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
package org.jclouds.cloudservers.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Class AbsoluteLimit
 * 
 * @author Adrian Cole
*/
public class AbsoluteLimit {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromAbsoluteLimit(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected String name;
      protected int value;
   
      /** 
       * @see AbsoluteLimit#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /** 
       * @see AbsoluteLimit#getValue()
       */
      public T value(int value) {
         this.value = value;
         return self();
      }

      public AbsoluteLimit build() {
         return new AbsoluteLimit(name, value);
      }
      
      public T fromAbsoluteLimit(AbsoluteLimit in) {
         return this
                  .name(in.getName())
                  .value(in.getValue());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String name;
   private final int value;

   @ConstructorProperties({
      "name", "value"
   })
   protected AbsoluteLimit(String name, int value) {
      this.name = checkNotNull(name, "name");
      this.value = value;
   }

   public String getName() {
      return this.name;
   }

   public int getValue() {
      return this.value;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, value);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      AbsoluteLimit that = AbsoluteLimit.class.cast(obj);
      return Objects.equal(this.name, that.name)
               && Objects.equal(this.value, that.value);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("name", name).add("value", value);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

}
