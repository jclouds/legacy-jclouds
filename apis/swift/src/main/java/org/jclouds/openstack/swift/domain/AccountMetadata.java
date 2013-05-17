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

import java.beans.ConstructorProperties;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * 
 * @author James Murty
 * 
 */
public class AccountMetadata {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromAccountMetadata(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected long containerCount;
      protected long bytes;

      /**
       * @see AccountMetadata#getContainerCount()
       */
      public T containerCount(long containerCount) {
         this.containerCount = containerCount;
         return self();
      }

      /**
       * @see AccountMetadata#getBytes()
       */
      public T bytes(long bytes) {
         this.bytes = bytes;
         return self();
      }

      public AccountMetadata build() {
         return new AccountMetadata(containerCount, bytes);
      }

      public T fromAccountMetadata(AccountMetadata in) {
         return this
               .containerCount(in.getContainerCount())
               .bytes(in.getBytes());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final long containerCount;
   private final long bytes;

   @ConstructorProperties({
         "containerCount", "bytes"
   })
   protected AccountMetadata(long containerCount, long bytes) {
      this.containerCount = containerCount;
      this.bytes = bytes;
   }

   public long getContainerCount() {
      return this.containerCount;
   }

   public long getBytes() {
      return this.bytes;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(containerCount, bytes);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      AccountMetadata that = AccountMetadata.class.cast(obj);
      return Objects.equal(this.containerCount, that.containerCount)
            && Objects.equal(this.bytes, that.bytes);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("containerCount", containerCount).add("bytes", bytes);
   }

   @Override
   public String toString() {
      return string().toString();
   }
}
