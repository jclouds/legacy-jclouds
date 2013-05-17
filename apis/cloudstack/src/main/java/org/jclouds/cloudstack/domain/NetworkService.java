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
import java.util.Map;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

/**
 * Class NetworkService
 *
 * @author Adrian Cole
 */
public class NetworkService implements Comparable<NetworkService> {

   public static class Capability implements Comparable<Capability> {

      public static Builder<?> builder() {
         return new ConcreteBuilder();
      }

      public Builder<?> toBuilder() {
         return new ConcreteBuilder().fromCapability(this);
      }

      public abstract static class Builder<T extends Builder<T>> {
         protected abstract T self();

         protected String name;
         protected String value;

         /**
          * @see Capability#getName()
          */
         public T name(String name) {
            this.name = name;
            return self();
         }

         /**
          * @see Capability#getValue()
          */
         public T value(String value) {
            this.value = value;
            return self();
         }

         public Capability build() {
            return new Capability(name, value);
         }

         public T fromCapability(Capability in) {
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
      private final String value;

      @ConstructorProperties({
            "name", "value"
      })
      protected Capability(String name, @Nullable String value) {
         this.name = checkNotNull(name, "name");
         this.value = value;
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
         return Objects.hashCode(name, value);
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) return true;
         if (obj == null || getClass() != obj.getClass()) return false;
         Capability that = Capability.class.cast(obj);
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

      @Override
      public int compareTo(Capability o) {
         return name.compareTo(o.getName());
      }
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromNetworkService(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String name;
      protected Set<Capability> capabilities = Sets.newHashSet();

      /**
       * @see NetworkService#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see NetworkService#getCapabilities()
       */
      public T capabilities(Map<String, String> capabilities) {
         for (Map.Entry<String, String> entry : capabilities.entrySet()) {
            this.capabilities.add(Capability.builder().name(entry.getKey()).value(entry.getValue()).build());
         }
         return self();
      }

      public NetworkService build() {
         return new NetworkService(name, capabilities);
      }

      public T fromNetworkService(NetworkService in) {
         return this
               .name(in.getName())
               .capabilities(in.getCapabilities());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String name;
   private final Set<Capability> capabilities;

   @ConstructorProperties({
         "name", "capability"
   })
   protected NetworkService(String name, @Nullable Set<Capability> capabilities) {
      this.name = checkNotNull(name, "name");
      this.capabilities = capabilities == null ? ImmutableSet.<Capability>of() : ImmutableSortedSet.copyOf(capabilities);
   }

   public String getName() {
      return this.name;
   }

   public Map<String, String> getCapabilities() {
      // so tests and serialization comes out expected
      ImmutableSortedMap.Builder<String, String> returnVal = ImmutableSortedMap.naturalOrder();
      for (Capability capability : capabilities) {
         returnVal.put(capability.name, capability.value);
      }
      return returnVal.build();
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, capabilities);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      NetworkService that = NetworkService.class.cast(obj);
      return Objects.equal(this.name, that.name)
            && Objects.equal(this.capabilities, that.capabilities);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).add("name", name).add("capabilities", capabilities);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(NetworkService o) {
      return name.compareTo(o.getName());
   }
}
