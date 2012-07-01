/*
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.openstack.keystone.v2_0.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import org.jclouds.openstack.keystone.v2_0.config.CredentialType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Password Credentials
 *
 * @see <a href="http://docs.openstack.org/api/openstack-identity-service/2.0/content/POST_authenticate_v2.0_tokens_Service_API_Client_Operations.html#d662e583"
/>
 * @author Adrian Cole
 */
@CredentialType("passwordCredentials")
public class PasswordCredentials {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromPasswordCredentials(this);
   }

   public static PasswordCredentials createWithUsernameAndPassword(String username, String password) {
      return new PasswordCredentials(username, password);
   }

   public static abstract class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected String username;
      protected String password;

      /**
       * @see PasswordCredentials#getUsername()
       */
      public T username(String username) {
         this.username = username;
         return self();
      }

      /**
       * @see PasswordCredentials#getPassword()
       */
      public T password(String password) {
         this.password = password;
         return self();
      }

      public PasswordCredentials build() {
         return new PasswordCredentials(username, password);
      }

      public T fromPasswordCredentials(PasswordCredentials in) {
         return this
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

   private final String username;
   private final String password;

   @ConstructorProperties({
         "username", "password"
   })
   protected PasswordCredentials(String username, String password) {
      this.username = checkNotNull(username, "username");
      this.password = checkNotNull(password, "password");
   }

   /**
    * @return the username
    */
   public String getUsername() {
      return this.username;
   }

   /**
    * @return the password
    */
   public String getPassword() {
      return this.password;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(username, password);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      PasswordCredentials that = PasswordCredentials.class.cast(obj);
      return Objects.equal(this.username, that.username)
            && Objects.equal(this.password, that.password);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("username", username).add("password", password);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
