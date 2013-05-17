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
import java.util.Map;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Class Limits
*/
public class Limits {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromLimits(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected Set<RateLimit> rate = ImmutableSet.of();
      protected Map<String, Integer> absolute = ImmutableMap.of();
   
      /** 
       * @see Limits#getRate()
       */
      public T rate(Set<RateLimit> rate) {
         this.rate = ImmutableSet.copyOf(checkNotNull(rate, "rate"));      
         return self();
      }

      public T rate(RateLimit... in) {
         return rate(ImmutableSet.copyOf(in));
      }

      /** 
       * @see Limits#getAbsolute()
       */
      public T absolute(Map<String, Integer> absolute) {
         this.absolute = ImmutableMap.copyOf(checkNotNull(absolute, "absolute"));     
         return self();
      }

      public Limits build() {
         return new Limits(rate, absolute);
      }
      
      public T fromLimits(Limits in) {
         return this
                  .rate(in.getRate())
                  .absolute(in.getAbsolute());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final Set<RateLimit> rate;
   private final Map<String, Integer> absolute;

   @ConstructorProperties({
      "rate", "absolute"
   })
   protected Limits(Set<RateLimit> rate, Map<String, Integer> absolute) {
      this.rate = ImmutableSet.copyOf(checkNotNull(rate, "rate"));      
      this.absolute = ImmutableMap.copyOf(checkNotNull(absolute, "absolute"));     
   }

   public Set<RateLimit> getRate() {
      return this.rate;
   }

   public Map<String, Integer> getAbsolute() {
      return this.absolute;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(rate, absolute);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Limits that = Limits.class.cast(obj);
      return Objects.equal(this.rate, that.rate)
               && Objects.equal(this.absolute, that.absolute);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("rate", rate).add("absolute", absolute);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

}
