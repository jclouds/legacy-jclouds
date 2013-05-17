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

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents an 'uptime' duration of server in a Glesys cloud
 *
 * @author Adam Lowe
 * @see ServerStatus
 */
public class ServerUptime {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromServerUptime(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected long current;
      protected String unit;

      /**
       * @see ServerUptime#getCurrent()
       */
      public T current(long current) {
         this.current = current;
         return self();
      }

      /**
       * @see ServerUptime#getUnit()
       */
      public T unit(String unit) {
         this.unit = checkNotNull(unit, "unit");
         return self();
      }

      public ServerUptime build() {
         return new ServerUptime(current, unit);
      }

      public T fromServerUptime(ServerUptime in) {
         return this.current(in.getCurrent()).unit(in.getUnit());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final long current;
   private final String unit;

   @ConstructorProperties({
         "current", "unit"
   })
   protected ServerUptime(long current, String unit) {
      this.current = current;
      this.unit = checkNotNull(unit, "unit");
   }

   /**
    * @return the time the server has been up in #getUnit()
    */
   public long getCurrent() {
      return this.current;
   }

   /**
    * @return the unit used for #getCurrent()
    */
   public String getUnit() {
      return this.unit;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(current, unit);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ServerUptime that = ServerUptime.class.cast(obj);
      return Objects.equal(this.current, that.current) && Objects.equal(this.unit, that.unit);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").add("current", current).add("unit", unit);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
