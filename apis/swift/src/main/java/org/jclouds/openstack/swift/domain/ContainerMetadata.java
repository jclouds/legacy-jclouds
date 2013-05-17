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
package org.jclouds.openstack.swift.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableMap;

/**
 * Class ContainerMetadata
 *
 * @author Adrian Cole
 */
public class ContainerMetadata {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromContainerMetadata(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String name;
      protected long count;
      protected long bytes;
      protected String readACL;
      protected Map<String, String> metadata = ImmutableMap.of();

      /**
       * @see ContainerMetadata#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see ContainerMetadata#getCount()
       */
      public T count(long count) {
         this.count = count;
         return self();
      }

      /**
       * @see ContainerMetadata#getBytes()
       */
      public T bytes(long bytes) {
         this.bytes = bytes;
         return self();
      }

      /**
       * @see ContainerMetadata#getReadACL()
       */
      public T readACL(String readACL) {
         this.readACL = readACL;
         return self();
      }

      /**
       * @see ContainerMetadata#getMetadata()
       */
      public T metadata(Map<String, String> metadata) {
         this.metadata = ImmutableMap.copyOf(checkNotNull(metadata, "metadata"));
         return self();
      }

      public ContainerMetadata build() {
         return new ContainerMetadata(name, count, bytes, readACL, metadata);
      }

      public T fromContainerMetadata(ContainerMetadata in) {
         return this
               .name(in.getName())
               .count(in.getCount())
               .bytes(in.getBytes())
               .readACL(in.getReadACL())
               .metadata(in.getMetadata());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String name;
   private final long count;
   private final long bytes;
   private final String readACL;
   private final Map<String, String> metadata;

   @ConstructorProperties({
         "name", "count", "bytes", "X-Container-Read", "metadata"
   })
   protected ContainerMetadata(String name, long count, long bytes, @Nullable String readACL, @Nullable Map<String, String> metadata) {
      this.name = checkNotNull(name, "name");
      this.count = count;
      this.bytes = bytes;
      this.readACL = readACL;
      this.metadata = metadata == null ? ImmutableMap.<String, String>of() : ImmutableMap.copyOf(metadata);
   }

   public String getName() {
      return this.name;
   }

   public long getCount() {
      return this.count;
   }

   public long getBytes() {
      return this.bytes;
   }

   @Nullable
   public String getReadACL() {
      return this.readACL;
   }

   public Map<String, String> getMetadata() {
      return this.metadata;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, count, bytes);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ContainerMetadata that = ContainerMetadata.class.cast(obj);
      return Objects.equal(this.name, that.name)
            && Objects.equal(this.count, that.count)
            && Objects.equal(this.bytes, that.bytes);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("name", name).add("count", count).add("bytes", bytes).add("readACL", readACL).add("metadata", metadata);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
