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
import static com.google.common.base.Strings.emptyToNull;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Contains a password for a specific software component instance
 *
 * @author Jason King
 * @see <a href= "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Software_Component_Password"
/>
 */
public class Password {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromPassword(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected int id;
      protected String username;
      protected String password;

      /**
       * @see Password#getId()
       */
      public T id(int id) {
         this.id = id;
         return self();
      }

      /**
       * @see Password#getUsername()
       */
      public T username(String username) {
         this.username = username;
         return self();
      }

      /**
       * @see Password#getPassword()
       */
      public T password(String password) {
         this.password = password;
         return self();
      }

      public Password build() {
         return new Password(id, username, password);
      }

      public T fromPassword(Password in) {
         return this
               .id(in.getId())
               .username(in.getUsername())
               .password(in.getPassword());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final int id;
   private final String username;
   private final String password;

   @ConstructorProperties({"id", "username", "password"})
   public Password(int id, String username, @Nullable String password) {
      this.id = id;
      this.username = checkNotNull(emptyToNull(username),"username cannot be null or empty:"+username);
      this.password = password;
   }

   /**
    * @return An id number for this specific username/password pair.
    */
   public int getId() {
      return this.id;
   }

   /**
    * @return The username part of the username/password pair.
    */
   @Nullable
   public String getUsername() {
      return this.username;
   }

   /**
    * @return The password part of the username/password pair.
    */
   @Nullable
   public String getPassword() {
      return this.password;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Password that = Password.class.cast(obj);
      return Objects.equal(this.id, that.id);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("username", username).add("password", password);
   }

   @Override
   public String toString() {
      return string().toString();
   }
}
