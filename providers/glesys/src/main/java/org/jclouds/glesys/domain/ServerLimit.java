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

import java.beans.ConstructorProperties;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Detailed information about an OpenVZ server's limits
 *
 * @author Adam Lowe
 * @see <a href= "https://customer.glesys.com/api.php?a=doc#server_limits" />
 */
public class ServerLimit {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromServerLimit(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected long held;
      protected long maxHeld;
      protected long barrier;
      protected long limit;
      protected long failCount;

      /**
       * @see ServerLimit#getHeld()
       */
      public T held(long held) {
         this.held = held;
         return self();
      }

      /**
       * @see ServerLimit#getMaxHeld()
       */
      public T maxHeld(long maxHeld) {
         this.maxHeld = maxHeld;
         return self();
      }

      /**
       * @see ServerLimit#getBarrier()
       */
      public T barrier(long barrier) {
         this.barrier = barrier;
         return self();
      }

      /**
       * @see ServerLimit#getLimit()
       */
      public T limit(long limit) {
         this.limit = limit;
         return self();
      }

      /**
       * @see ServerLimit#getFailCount()
       */
      public T failCount(long failCount) {
         this.failCount = failCount;
         return self();
      }

      public ServerLimit build() {
         return new ServerLimit(held, maxHeld, barrier, limit, failCount);
      }

      public T fromServerLimit(ServerLimit in) {
         return this.held(in.getHeld())
               .maxHeld(in.getMaxHeld())
               .barrier(in.getBarrier())
               .limit(in.getLimit())
               .failCount(in.getFailCount());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final long held;
   private final long maxHeld;
   private final long barrier;
   private final long limit;
   private final long failCount;

   @ConstructorProperties({
         "held", "maxHeld", "barrier", "limit", "failCount"
   })
   protected ServerLimit(long held, long maxHeld, long barrier, long limit, long failCount) {
      this.held = held;
      this.maxHeld = maxHeld;
      this.barrier = barrier;
      this.limit = limit;
      this.failCount = failCount;
   }

   public long getHeld() {
      return this.held;
   }

   public long getMaxHeld() {
      return this.maxHeld;
   }

   public long getBarrier() {
      return this.barrier;
   }

   public long getLimit() {
      return this.limit;
   }

   public long getFailCount() {
      return this.failCount;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(held, maxHeld, barrier, limit, failCount);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ServerLimit that = ServerLimit.class.cast(obj);
      return Objects.equal(this.held, that.held)
            && Objects.equal(this.maxHeld, that.maxHeld)
            && Objects.equal(this.barrier, that.barrier)
            && Objects.equal(this.limit, that.limit)
            && Objects.equal(this.failCount, that.failCount);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").add("held", held).add("maxHeld", maxHeld).add("barrier", barrier)
            .add("limit", limit).add("failCount", failCount);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
