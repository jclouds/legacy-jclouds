/**
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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

/**
 * Password Credentials
 * 
 * @see <a href="http://docs.openstack.org/api/openstack-identity-service/2.0/content/POST_authenticate_v2.0_tokens_Service_API_Client_Operations.html#d662e583"
 *      />
 * @author Adrian Cole
 */
public class PasswordCredentials {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromPasswordCredentials(this);
   }

   public static PasswordCredentials createWithUsernameAndPassword(String username, String password) {
      return builder().password(password).username(username).build();
   }

   public static class Builder {
      protected String username;
      protected String password;

      /**
       * @see PasswordCredentials#getUsername()
       */
      protected Builder password(String password) {
         this.password = password;
         return this;
      }

      /**
       * @see PasswordCredentials#getPassword()
       */
      public Builder username(String username) {
         this.username = username;
         return this;
      }

      public PasswordCredentials build() {
         return new PasswordCredentials(username, password);
      }

      public Builder fromPasswordCredentials(PasswordCredentials from) {
         return username(from.getUsername()).password(from.getPassword());
      }
   }

   protected final String username;
   protected final String password;

   protected PasswordCredentials(String username, String password) {
      this.username = checkNotNull(username, "username");
      this.password = checkNotNull(password, "password");
   }

   /**
    * @return the username
    */
   public String getUsername() {
      return username;
   }

   /**
    * @return the password
    */
   public String getPassword() {
      return password;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof PasswordCredentials) {
         final PasswordCredentials other = PasswordCredentials.class.cast(object);
         return equal(username, other.username) && equal(password, other.password);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(username, password);
   }

   @Override
   public String toString() {
      return toStringHelper("").add("username", username).add("password", password).toString();
   }

}
