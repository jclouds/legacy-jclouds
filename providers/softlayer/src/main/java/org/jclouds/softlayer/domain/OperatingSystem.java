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
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;

/**
 * Extends the SoftLayer_Software_Component data type to include operating system specific properties.
 *
 * @author Jason King
 * @see <a href=
"http://sldn.softlayer.com/reference/datatypes/SoftLayer_Software_Component_OperatingSystem"
/>
 */
public class OperatingSystem {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromOperatingSystem(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected int id;
      protected Set<Password> passwords = ImmutableSet.of();

      /**
       * @see OperatingSystem#getId()
       */
      public T id(int id) {
         this.id = id;
         return self();
      }

      /**
       * @see OperatingSystem#getPasswords()
       */
      public T passwords(Set<Password> passwords) {
         this.passwords = ImmutableSet.copyOf(checkNotNull(passwords, "passwords"));
         return self();
      }

      public T passwords(Password... in) {
         return passwords(ImmutableSet.copyOf(in));
      }

      public OperatingSystem build() {
         return new OperatingSystem(id, passwords);
      }

      public T fromOperatingSystem(OperatingSystem in) {
         return this
               .id(in.getId())
               .passwords(in.getPasswords());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final int id;
   private final Set<Password> passwords;

   @ConstructorProperties({
         "id", "passwords"
   })
   protected OperatingSystem(int id, @Nullable Set<Password> passwords) {
      this.id = id;
      this.passwords = passwords == null ? ImmutableSet.<Password>of() : ImmutableSet.copyOf(passwords);
   }

   /**
    * @return An ID number identifying this Software Component (Software Installation)
    */
   public int getId() {
      return this.id;
   }

   /**
    * @return Username/Password pairs used for access to this Software Installation.
    */
   public Set<Password> getPasswords() {
      return this.passwords;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      OperatingSystem that = OperatingSystem.class.cast(obj);
      return Objects.equal(this.id, that.id);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("passwords", passwords);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
