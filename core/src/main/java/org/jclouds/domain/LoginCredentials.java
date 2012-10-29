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

import com.google.common.base.Optional;

/**
 * @author Adrian Cole
 */
public class LoginCredentials extends Credentials {
   
   public static LoginCredentials fromCredentials(Credentials creds) {
      if (creds == null)
         return null;
      if (creds instanceof LoginCredentials)
         return LoginCredentials.class.cast(creds);
      return builder(creds).build();
   }

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
      private Optional<String> password;
      private Optional<String> privateKey;

      public Builder identity(String identity) {
         return Builder.class.cast(super.identity(identity));
      }

      public Builder user(String user) {
         return identity(user);
      }

      public Builder password(String password) {
         this.password = Optional.fromNullable(password);
         if (privateKey == null)
            noPrivateKey();
         return this;
      }

      public Builder noPassword() {
         this.password = Optional.absent();
         return this;
      }

      public Builder privateKey(String privateKey) {
         this.privateKey = Optional.fromNullable(privateKey);
         if (password == null)
            noPassword();
         return this;
      }

      public Builder noPrivateKey() {
         this.privateKey = Optional.absent();
         return this;
      }

      public Builder credential(String credential) {
         if (CredentialUtils.isPrivateKeyCredential(credential))
            return noPassword().privateKey(credential);
         else if (credential != null)
            return password(credential).noPrivateKey();
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
   private final Optional<String> password;
   private final Optional<String> privateKey;

   public LoginCredentials(String username, boolean authenticateSudo) {
      this(username, Optional.<String>absent(), Optional.<String>absent(), authenticateSudo);
   }

   public LoginCredentials(String username, @Nullable String password, @Nullable String privateKey, boolean authenticateSudo) {
      this(username, Optional.fromNullable(password), Optional.fromNullable(privateKey), authenticateSudo);
   }

   public LoginCredentials(String username, @Nullable Optional<String> password, @Nullable Optional<String> privateKey, boolean authenticateSudo) {
      super(username, privateKey != null && privateKey.isPresent() && CredentialUtils.isPrivateKeyCredential(privateKey.get())
                    ? privateKey.get()
                    : (password != null && password.isPresent() ? password.get() : null));
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
    * @return true if a password is available
    */
   public boolean hasPassword() {
      return password != null && password.isPresent();
   }

   /**
    * @return true if a password was set
    */
   public boolean hasPasswordOption() {
      return password != null;
   }

   /**
    * @return the password of the login user or null
    */
   @Nullable
   public String getPassword() {
      return hasPassword() ? password.get() : null;
   }

   /**
    * @return the optional password of the user or null
    */
   @Nullable
   public Optional<String> getOptionalPassword() {
      return password;
   }

   /**
    * @return true if a private key is available
    */
   public boolean hasPrivateKey() {
      return privateKey != null && privateKey.isPresent();
   }

   /**
    * @return true if a password was set
    */
   public boolean hasPrivateKeyOption() {
      return privateKey != null;
   }

   /**
    * @return the private ssh key of the user or null
    */
   @Nullable
   public String getPrivateKey() {
      return  hasPrivateKey() ? privateKey.get() : null;
   }

   /**
    * @return the optional private ssh key of the user or null
    */
   @Nullable
   public Optional<String> getOptionalPrivateKey() {
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
      Builder builder = new Builder().user(identity).authenticateSudo(authenticateSudo);
      if (password != null) {
         if (password.isPresent()) {
            builder = builder.password(password.get());
         } else {
            builder = builder.noPassword();
         }
      }
      if (privateKey != null) {
         if (privateKey.isPresent()) {
            builder = builder.privateKey(privateKey.get());
         } else {
            builder = builder.noPrivateKey();
         }
      }
      return builder;
   }

   @Override
   public String toString() {
      return "[user=" + getUser() + ", passwordPresent=" + hasPassword() + ", privateKeyPresent="
            + hasPrivateKey() + ", shouldAuthenticateSudo=" + authenticateSudo + "]";
   }
}
