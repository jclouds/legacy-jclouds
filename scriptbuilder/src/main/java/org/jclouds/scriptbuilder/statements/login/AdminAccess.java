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
package org.jclouds.scriptbuilder.statements.login;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;

import org.jclouds.crypto.Sha512Crypt;
import org.jclouds.domain.Credentials;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementList;
import org.jclouds.scriptbuilder.statements.ssh.SshStatements;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import com.google.inject.ImplementedBy;

/**
 * Controls the administrative access to a node. By default, it will perform the following:
 * 
 * <ul>
 *   <li>setup a new admin user which folks should use as opposed to any built-in account</li>
 *   <ul>
 *     <li>associate a random (or given) password to that account
 *     <ul>
 *       <li>securely (using sha 512 on client side and literally rewriting the shadow entry, 
 *           rather than sending password plaintext to OS in a script)</li>
 *       <li>but note password access is often blocked in any case, see below</li>
 *     </ul>
 *     <li>associate the users' ssh public key with the account for login</li> 
 *     <li>associate it with the os group wheel</li> 
 *   </ul> 
 *   <li>set up sudoers for password-less access to root for this user (shouldGrantSudo)</li>
 *   <ul>
 *     <li>creating os group wheel and assigning the new admin user to it</li> 
 *     <li>create (overwriting) sudoers file to grant root access for wheel members</li>
 *   </ul>
 *   <li>reset password for the user logging in (e.g. root, because root password is 
 *   sometimes known to the provider), securely and randomly as described above (resetLoginPassword)</li>
 *   <li>lockdown sshd_config for no root login, nor passwords allowed (lockSsh)</li> 
 * </ul>
 * 
 * @author Adrian Cole
 */
public class AdminAccess implements Statement {
   public static AdminAccess.Builder builder() {
      return new Builder();
   }

   public static AdminAccess.Builder builder(Function<String, String> cryptFunction) {
      return new Builder(cryptFunction);
   }

   public static AdminAccess standard() {
      return new Builder().build();
   }

   @ImplementedBy(DefaultConfiguration.class)
   public static interface Configuration {
      Supplier<String> defaultAdminUsername();

      Supplier<Map<String, String>> defaultAdminSshKeys();

      Supplier<String> passwordGenerator();

      Function<String, String> cryptFunction();
   }

   public static class Builder {
      private final Function<String, String> cryptFunction;

      public Builder() {
         this(Sha512Crypt.function());
      }

      public Builder(Function<String, String> cryptFunction) {
         this.cryptFunction = cryptFunction;
      }

      private String adminUsername;
      private String adminPublicKey;
      private File adminPublicKeyFile;
      private String adminPrivateKey;
      private File adminPrivateKeyFile;
      private String adminPassword;
      private String loginPassword;
      private boolean lockSsh = true;
      private boolean grantSudoToAdminUser = true;
      private boolean authorizeAdminPublicKey = true;
      private boolean installAdminPrivateKey = false;
      private boolean resetLoginPassword = true;

      public AdminAccess.Builder adminUsername(String adminUsername) {
         this.adminUsername = adminUsername;
         return this;
      }

      public AdminAccess.Builder adminPassword(String adminPassword) {
         this.adminPassword = adminPassword;
         return this;
      }

      public AdminAccess.Builder loginPassword(String loginPassword) {
         this.loginPassword = loginPassword;
         return this;
      }

      public AdminAccess.Builder lockSsh(boolean lockSsh) {
         this.lockSsh = lockSsh;
         return this;
      }

      public AdminAccess.Builder resetLoginPassword(boolean resetLoginPassword) {
         this.resetLoginPassword = resetLoginPassword;
         return this;
      }

      public AdminAccess.Builder authorizeAdminPublicKey(boolean authorizeAdminPublicKey) {
         this.authorizeAdminPublicKey = authorizeAdminPublicKey;
         return this;
      }

      public AdminAccess.Builder installAdminPrivateKey(boolean installAdminPrivateKey) {
         this.installAdminPrivateKey = installAdminPrivateKey;
         return this;
      }

      public AdminAccess.Builder grantSudoToAdminUser(boolean grantSudoToAdminUser) {
         this.grantSudoToAdminUser = grantSudoToAdminUser;
         return this;
      }

      public AdminAccess.Builder adminPublicKey(File adminPublicKey) {
         this.adminPublicKeyFile = adminPublicKey;
         this.adminPublicKey = null;
         return this;
      }

      public AdminAccess.Builder adminPublicKey(String adminPublicKey) {
         this.adminPublicKey = adminPublicKey;
         this.adminPublicKeyFile = null;
         return this;
      }

      public AdminAccess.Builder adminPrivateKey(File adminPrivateKey) {
         this.adminPrivateKeyFile = adminPrivateKey;
         this.adminPrivateKey = null;
         return this;
      }

      public AdminAccess.Builder adminPrivateKey(String adminPrivateKey) {
         this.adminPrivateKey = adminPrivateKey;
         this.adminPrivateKeyFile = null;
         return this;
      }

      public AdminAccess build() {
         return new AdminAccess(buildConfig());
      }

      protected Config buildConfig() {
         try {
            String adminPublicKey = this.adminPublicKey;
            if (adminPublicKey == null && adminPublicKeyFile != null)
               adminPublicKey = Files.toString(adminPublicKeyFile, UTF_8);
            String adminPrivateKey = this.adminPrivateKey;
            if (adminPrivateKey == null && adminPrivateKeyFile != null)
               adminPrivateKey = Files.toString(adminPrivateKeyFile, UTF_8);
            return new Config(adminUsername, adminPublicKey, adminPrivateKey, adminPassword, loginPassword, lockSsh,
                     grantSudoToAdminUser, authorizeAdminPublicKey, installAdminPrivateKey, resetLoginPassword,
                     cryptFunction);
         } catch (IOException e) {
            Throwables.propagate(e);
            return null;
         }
      }
   }

   protected static class Config {
      private final String adminUsername;
      private final String adminPublicKey;
      private final String adminPrivateKey;
      private final String adminPassword;
      private final String loginPassword;
      private final boolean lockSsh;
      private final boolean grantSudoToAdminUser;
      private final boolean authorizeAdminPublicKey;
      private final boolean installAdminPrivateKey;
      private final boolean resetLoginPassword;
      private final Function<String, String> cryptFunction;
      private final Credentials adminCredentials;

      protected Config(@Nullable String adminUsername, @Nullable String adminPublicKey,
               @Nullable String adminPrivateKey, @Nullable String adminPassword, @Nullable String loginPassword,
               boolean lockSsh, boolean grantSudoToAdminUser, boolean authorizeAdminPublicKey,
               boolean installAdminPrivateKey, boolean resetLoginPassword, Function<String, String> cryptFunction) {
         this.adminUsername = adminUsername;
         this.adminPublicKey = adminPublicKey;
         this.adminPrivateKey = adminPrivateKey;
         this.adminPassword = adminPassword;
         this.loginPassword = loginPassword;
         this.lockSsh = lockSsh;
         this.grantSudoToAdminUser = grantSudoToAdminUser;
         this.authorizeAdminPublicKey = authorizeAdminPublicKey;
         this.installAdminPrivateKey = installAdminPrivateKey;
         this.resetLoginPassword = resetLoginPassword;
         this.cryptFunction = cryptFunction;
         if (adminUsername != null && authorizeAdminPublicKey && adminPrivateKey != null)
            this.adminCredentials = new Credentials(adminUsername, adminPrivateKey);
         else
            this.adminCredentials = null;
      }

      public String getAdminUsername() {
         return adminUsername;
      }

      public String getAdminPublicKey() {
         return adminPublicKey;
      }

      public String getAdminPrivateKey() {
         return adminPrivateKey;
      }

      public String getAdminPassword() {
         return adminPassword;
      }

      public String getLoginPassword() {
         return loginPassword;
      }

      public boolean shouldLockSsh() {
         return lockSsh;
      }

      public boolean shouldGrantSudoToAdminUser() {
         return grantSudoToAdminUser;
      }

      public boolean shouldAuthorizeAdminPublicKey() {
         return authorizeAdminPublicKey;
      }

      public boolean shouldInstallAdminPrivateKey() {
         return installAdminPrivateKey;
      }

      public boolean shouldResetLoginPassword() {
         return resetLoginPassword;
      }

      public Function<String, String> getCryptFunction() {
         return cryptFunction;
      }

      public Credentials getAdminCredentials() {
         return adminCredentials;
      }
   }

   private Config config;

   protected AdminAccess(Config in) {
      this.config = checkNotNull(in, "in");
   }

   /**
    * 
    * @return new credentials or null if unchanged or unavailable
    */
   @Nullable
   public Credentials getAdminCredentials() {
      return config.getAdminCredentials();
   }

   @Nullable
   public String getAdminPassword() {
      return config.getAdminPassword();
   }

   public boolean shouldGrantSudoToAdminUser() {
      return config.shouldGrantSudoToAdminUser();
   }

   @Override
   public Iterable<String> functionDependencies(OsFamily family) {
      return ImmutableList.of();
   }

   public AdminAccess init(Configuration configuration) {
      Builder builder = AdminAccess.builder(configuration.cryptFunction());
      builder.adminUsername(config.getAdminUsername() != null ? config.getAdminUsername() : configuration
               .defaultAdminUsername().get());
      builder.adminPassword(config.getAdminPassword() != null ? config.getAdminPassword() : configuration
               .passwordGenerator().get());
      Map<String, String> adminSshKeys = (config.getAdminPublicKey() != null && config.getAdminPrivateKey() != null) ? ImmutableMap
               .of("public", config.getAdminPublicKey(), "private", config.getAdminPrivateKey())
               : configuration.defaultAdminSshKeys().get();
      builder.adminPublicKey(adminSshKeys.get("public"));
      builder.adminPrivateKey(adminSshKeys.get("private"));
      builder.loginPassword(config.getLoginPassword() != null ? config.getLoginPassword() : configuration
               .passwordGenerator().get());
      builder.grantSudoToAdminUser(config.shouldGrantSudoToAdminUser());
      builder.authorizeAdminPublicKey(config.shouldAuthorizeAdminPublicKey());
      builder.installAdminPrivateKey(config.shouldInstallAdminPrivateKey());
      builder.lockSsh(config.shouldLockSsh());
      builder.resetLoginPassword(config.shouldResetLoginPassword());
      this.config = builder.buildConfig();
      return this;
   }

   @Override
   public String render(OsFamily family) {
      checkNotNull(family, "family");
      if (family == OsFamily.WINDOWS)
         throw new UnsupportedOperationException("windows not yet implemented");
      checkNotNull(config.getAdminUsername(), "adminUsername");
      checkNotNull(config.getAdminPassword(), "adminPassword");
      checkNotNull(config.getAdminPublicKey(), "adminPublicKey");
      checkNotNull(config.getAdminPrivateKey(), "adminPrivateKey");
      checkNotNull(config.getLoginPassword(), "loginPassword");

      ImmutableList.Builder<Statement> statements = ImmutableList.<Statement> builder();
      UserAdd.Builder userBuilder = UserAdd.builder();
      userBuilder.login(config.getAdminUsername());
      if (config.shouldAuthorizeAdminPublicKey())
         userBuilder.authorizeRSAPublicKey(config.getAdminPublicKey());
      userBuilder.password(config.getAdminPassword());
      if (config.shouldInstallAdminPrivateKey())
         userBuilder.installRSAPrivateKey(config.getAdminPrivateKey());
      if (config.shouldGrantSudoToAdminUser()) {
         statements.add(SudoStatements.createWheel());
         userBuilder.group("wheel");
      }
      statements.add(userBuilder.build().cryptFunction(config.getCryptFunction()));
      if (config.shouldLockSsh())
         statements.add(SshStatements.lockSshd());
      if (config.shouldResetLoginPassword()) {
         statements.add(ShadowStatements.resetLoginUserPasswordTo(config.getLoginPassword()).cryptFunction(
                  config.getCryptFunction()));
      }
      return new StatementList(statements.build()).render(family);
   }
}