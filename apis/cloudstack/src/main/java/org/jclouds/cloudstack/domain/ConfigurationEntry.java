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

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Representation of the API configuration entry response
 *
 * @author Andrei Savu
 */
public class ConfigurationEntry implements Comparable<ConfigurationEntry> {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromConfigurationEntry(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String category;
      protected String description;
      protected String name;
      protected String value;

      /**
       * @see ConfigurationEntry#getCategory()
       */
      public T category(String category) {
         this.category = category;
         return self();
      }

      /**
       * @see ConfigurationEntry#getDescription()
       */
      public T description(String description) {
         this.description = description;
         return self();
      }

      /**
       * @see ConfigurationEntry#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see ConfigurationEntry#getValue()
       */
      public T value(String value) {
         this.value = value;
         return self();
      }

      public ConfigurationEntry build() {
         return new ConfigurationEntry(category, description, name, value);
      }

      public T fromConfigurationEntry(ConfigurationEntry in) {
         return this
               .category(in.getCategory())
               .description(in.getDescription())
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

   private final String category;
   private final String description;
   private final String name;
   private final String value;

   @ConstructorProperties({
         "category", "description", "name", "value"
   })
   protected ConfigurationEntry(@Nullable String category, @Nullable String description, String name, @Nullable String value) {
      this.category = category;
      this.description = description;
      this.name = checkNotNull(name, "name");
      this.value = value;
   }

   @Nullable
   public String getCategory() {
      return this.category;
   }

   @Nullable
   public String getDescription() {
      return this.description;
   }

   public String getName() {
      return this.name;
   }

   @Nullable
   public String getValue() {
      return this.value;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(category, description, name, value);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ConfigurationEntry that = ConfigurationEntry.class.cast(obj);
      return Objects.equal(this.category, that.category)
            && Objects.equal(this.description, that.description)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.value, that.value);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("category", category).add("description", description).add("name", name).add("value", value);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(ConfigurationEntry other) {
      return name.compareTo(other.getName());
   }
}
