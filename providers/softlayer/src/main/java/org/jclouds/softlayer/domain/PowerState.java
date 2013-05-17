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
package org.jclouds.softlayer.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * The power state class provides a common set of values for which a guest's power state will be presented in the SoftLayer API.
 *
 * @author Jason King
 * @see <a href= "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Virtual_Guest_Power_State"
/>
 */
public class PowerState {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromPowerState(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected VirtualGuest.State keyName;

      /**
       * @see PowerState#getKeyName()
       */
      public T keyName(VirtualGuest.State keyName) {
         this.keyName = keyName;
         return self();
      }

      public PowerState build() {
         return new PowerState(keyName);
      }

      public T fromPowerState(PowerState in) {
         return this
               .keyName(in.getKeyName());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final VirtualGuest.State keyName;

   @ConstructorProperties("keyName")
   public PowerState(VirtualGuest.State keyName) {
      this.keyName = checkNotNull(keyName,"keyName cannot be null or empty:"+keyName);
   }

   /**
    * Maps onto {@code VirtualGuest.State}
    *
    * @return The key name of a power state.
    */
   @Nullable
   public VirtualGuest.State getKeyName() {
      return this.keyName;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(keyName);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      PowerState that = PowerState.class.cast(obj);
      return Objects.equal(this.keyName, that.keyName);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("keyName", keyName);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
