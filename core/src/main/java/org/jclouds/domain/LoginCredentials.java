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
package org.jclouds.domain;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.util.CredentialUtils;

/**
 * @author Adrian Cole
 */
public class LoginCredentials extends Credentials {

   public static Builder builder(Credentials creds) {
      if (creds == null)
         return builder();
      if (creds instanceof LoginCredentials)
         return LoginCredentials.class.cast(creds).toBuilder();
      else
         return builder().identity(creds.identity).credential(creds.credential);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder extends Credentials.Builder<LoginCredentials> {

      private boolean authenticateSudo;
      private String password;
      private String privateKey;

      public Builder identity(String identity) {
         return Builder.class.cast(super.identity(identity));
      }

      public Builder user(String user) {
         return identity(user);
      }

      public Builder password(String password) {
         this.password = password;
         return this;
      }

      public Builder privateKey(String privateKey) {
         this.privateKey = privateKey;
         return this;
      }

      public Builder credential(String credential) {
         if (CredentialUtils.isPrivateKeyCredential(credential))
            return privateKey(credential);
         else if (credential != null)
            return password(credential);
         return this;
      }

      public Builder authenticateSudo(boolean authenticateSudo) {
         this.authenticateSudo = authenticateSudo;
         return this;
      }

      public LoginCredentials build() {
         if (identity == null && password == null && privateKey == null && !authenticateSudo)
            return null;
         return new LoginCredentials(identity, password, privateKey, authenticateSudo);
      }
   }

   private final boolean authenticateSudo;
   private final String password;
   private final String privateKey;

   public LoginCredentials(String username, @Nullable String password, @Nullable String privateKey,
         boolean authenticateSudo) {
      super(username, CredentialUtils.isPrivateKeyCredential(privateKey) ? privateKey : password);
      this.authenticateSudo = authenticateSudo;
      this.password = password;
      this.privateKey = privateKey;
   }

   /**
    * @return the login user
    */
   public String getUser() {
      return identity;
   }

   /**
    * @return the password of the login user or null
    */
   @Nullable
   public String getPassword() {
      return password;
   }

   /**
    * @return the private ssh key of the user or null
    */
   @Nullable
   public String getPrivateKey() {
      return privateKey;
   }

   /**
    * secures access to root requires a password. This password is required to
    * access either the console or run sudo as root.
    * <p/>
    * ex. {@code echo 'password' |sudo -S command}
    * 
    * @return if a password is required to access the root user
    */
   public boolean shouldAuthenticateSudo() {
      return authenticateSudo;
   }

   @Override
   public Builder toBuilder() {
      return new Builder().user(identity).password(password).privateKey(privateKey).authenticateSudo(authenticateSudo);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + (authenticateSudo ? 1231 : 1237);
      result = prime * result + ((password == null) ? 0 : password.hashCode());
      result = prime * result + ((privateKey == null) ? 0 : privateKey.hashCode());
      return result;
   }

   @Override
   public String toString() {
      return "[user=" + getUser() + ", passwordPresent=" + (password != null) + ", privateKeyPresent="
            + (privateKey != null) + ", shouldAuthenticateSudo=" + authenticateSudo + "]";
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      LoginCredentials other = (LoginCredentials) obj;
      if (authenticateSudo != other.authenticateSudo)
         return false;
      if (password == null) {
         if (other.password != null)
            return false;
      } else if (!password.equals(other.password))
         return false;
      if (privateKey == null) {
         if (other.privateKey != null)
            return false;
      } else if (!privateKey.equals(other.privateKey))
         return false;
      return true;
   }

}