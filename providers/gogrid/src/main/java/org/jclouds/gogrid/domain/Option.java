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
package org.jclouds.gogrid.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.primitives.Longs;

/**
 * Class Option
 * 
 * @author Oleksiy Yarmula
*/
public class Option implements Comparable<Option> {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromOption(this);
   }
   
   public static Option createWithIdNameAndDescription(Long id, String name, String description) {
      return new Option(id, name, description);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected Long id;
      protected String name;
      protected String description;
   
      /** 
       * @see Option#getId()
       */
      public T id(Long id) {
         this.id = id;
         return self();
      }

      /** 
       * @see Option#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /** 
       * @see Option#getDescription()
       */
      public T description(String description) {
         this.description = description;
         return self();
      }

      public Option build() {
         return new Option(id, name, description);
      }
      
      public T fromOption(Option in) {
         return this
                  .id(in.getId())
                  .name(in.getName())
                  .description(in.getDescription());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final Long id;
   private final String name;
   private final String description;

   @ConstructorProperties({
      "id", "name", "description"
   })
   protected Option(Long id, String name, @Nullable String description) {
      this.id = checkNotNull(id, "id");
      this.name = checkNotNull(name, "name");
      this.description = description;
   }

   public Long getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   @Nullable
   public String getDescription() {
      return this.description;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name, description);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Option that = Option.class.cast(obj);
      return Objects.equal(this.id, that.id)
               && Objects.equal(this.name, that.name)
               && Objects.equal(this.description, that.description);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("name", name).add("description", description);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(Option o) {
      return Longs.compare(id, o.id);
   }
}
