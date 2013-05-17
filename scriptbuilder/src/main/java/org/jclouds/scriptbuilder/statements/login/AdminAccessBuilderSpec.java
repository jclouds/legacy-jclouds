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
package org.jclouds.scriptbuilder.statements.login;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * A specification of a {@link AdminAccess.Builder} configuration.
 * 
 * <p>
 * {@code AdminAccess.Builder} supports parsing configuration off of a string, which makes it
 * especially useful for command-line configuration of a {@code AdminAccess.Builder}.
 * 
 * <p>
 * The string syntax is a series of comma-separated keys or key-value pairs, each corresponding to a
 * {@code AdminAccess.Builder} method.
 * <ul>
 * <li>{@code adminUsername=[String]}: sets {@link AdminAccess.Builder#adminUsername}.
 * <li>{@code adminHome=[String]}: sets {@link AdminAccess.Builder#adminHome}.
 * <li>{@code adminPublicKeyFile=[String]}: sets {@link AdminAccess.Builder#adminPublicKeyFile}.
 * <li>{@code adminPrivateKeyFile=[String]}: sets {@link AdminAccess.Builder#adminPrivateKeyFile}. *
 * <li>{@code adminPassword=[String]}: sets {@link AdminAccess.Builder#adminPassword}.
 * <li>{@code loginPassword=[String]}: sets {@link AdminAccess.Builder#loginPassword}.
 * <li>{@code lockSsh=[Boolean]}: sets {@link TemplateBuilder#lockSsh}.
 * <li>{@code grantSudoToAdminUser=[Boolean]}: sets {@link TemplateBuilder#grantSudoToAdminUser}.
 * <li>{@code authorizeAdminPublicKey=[Boolean]}: sets
 * {@link TemplateBuilder#authorizeAdminPublicKey}.
 * <li>{@code installAdminPrivateKey=[Boolean]}: sets {@link TemplateBuilder#installAdminPrivateKey}.
 * <li>{@code resetLoginPassword=[Boolean]}: sets {@link TemplateBuilder#resetLoginPassword}.
 * </ul>
 * 
 * <p>
 * Whitespace before and after commas and equal signs is ignored. Keys may not be repeated and both
 * private key and public key must be passed through files, as they might include weird characters.
 * 
 * <p>
 * {@code AdminAccessBuilderSpec} does not support configuring {@code AdminAccess.Builder} methods
 * with non-value parameters. These must be configured in code.
 * 
 * <p>
 * A new {@code AdminAccess.Builder} can be instantiated from a {@code AdminAccessBuilderSpec} using
 * {@link AdminAccess.Builder#from(AdminAccessBuilderSpec)} or
 * {@link AdminAccess.Builder#from(String)}.
 * 
 * <p>
 * Design inspired by {@link CacheBuilderSpec}
 * 
 * @author David Alves
 * @since 1.5
 */

public class AdminAccessBuilderSpec {

   /** Parses a single value. */
   protected static interface ValueParser {
      void parse(AdminAccessBuilderSpec spec, String key, @Nullable String value);
   }

   /** Splits each key-value pair. */
   protected static final Splitter KEYS_SPLITTER = Splitter.on(',').trimResults();

   /** Splits the key from the value. */
   protected static final Splitter KEY_VALUE_SPLITTER = Splitter.on('=').trimResults();

   /** Map of names to ValueParser. */
   protected static final ImmutableMap<String, ValueParser> VALUE_PARSERS = ImmutableMap
            .<String, ValueParser> builder().put("adminUsername", new AdminUserNameParser())
            .put("adminHome", new AdminHomeParser()).put("adminPublicKeyFile", new AdminPublicKeyFileParser())
            .put("adminPrivateKeyFile", new AdminPrivateKeyFileParser())
            .put("adminPassword", new AdminPasswordParser()).put("loginPassword", new LoginPasswordParser())
            .put("lockSsh", new LockSshParser()).put("grantSudoToAdminUser", new GrantSudoToAdminUserParser())
            .put("authorizeAdminPublicKey", new AuthorizeAdminPublicKeyParser())
            .put("installAdminPrivateKey", new InstallAdminPrivateKeyParser())
            .put("resetLoginPassword", new ResetLoginPasswordParser()).build();

   @VisibleForTesting
   String adminUsername;
   @VisibleForTesting
   String adminHome;
   @VisibleForTesting
   File adminPublicKeyFile;
   @VisibleForTesting
   File adminPrivateKeyFile;
   @VisibleForTesting
   String adminPassword;
   @VisibleForTesting
   String loginPassword;
   @VisibleForTesting
   Boolean lockSsh;
   @VisibleForTesting
   Boolean grantSudoToAdminUser;
   @VisibleForTesting
   Boolean authorizeAdminPublicKey;
   @VisibleForTesting
   Boolean installAdminPrivateKey;
   @VisibleForTesting
   Boolean resetLoginPassword;

   /** Base class for parsing strings. */
   abstract static class StringParser implements ValueParser {
      protected abstract void set(AdminAccessBuilderSpec spec, String value);

      @Override
      public void parse(AdminAccessBuilderSpec spec, String key, String value) {
         checkArgument(value != null && !value.isEmpty(), "value of key %s omitted", key);
         set(spec, value);
      }
   }

   /** Base class for parsing booleans. */
   abstract static class BooleanParser implements ValueParser {
      protected abstract void parseBoolean(AdminAccessBuilderSpec spec, boolean value);

      @Override
      public void parse(AdminAccessBuilderSpec spec, String key, String value) {
         checkArgument(value != null && !value.isEmpty(), "value of key %s omitted", key);
         try {
            parseBoolean(spec, Boolean.parseBoolean(value));
         } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("key %s value set to %s, must be booleans", key, value), e);
         }
      }
   }

   /** Base class for parsing files. */
   abstract static class FileParser implements ValueParser {
      protected abstract void set(AdminAccessBuilderSpec spec, File value);

      @Override
      public void parse(AdminAccessBuilderSpec spec, String key, String value) {
         checkArgument(value != null && !value.isEmpty(), "value of key %s omitted", key);
         File file = new File(value);
         if (!file.exists()) {
            throw new IllegalArgumentException(String.format("key %s value set to %s, must be an existing file", key,
                     value));
         }
         set(spec, file);
      }
   }

   /** Parse adminUsername */
   static class AdminUserNameParser extends StringParser {
      @Override
      protected void set(AdminAccessBuilderSpec spec, String value) {
         checkArgument(spec.adminUsername == null, "admin username was already set to ", spec.adminUsername);
         spec.adminUsername = value;
      }
   }

   /** Parse adminHome */
   static class AdminHomeParser extends StringParser {
      @Override
      protected void set(AdminAccessBuilderSpec spec, String value) {
         checkArgument(spec.adminHome == null, "admin home was already set to ", spec.adminHome);
         spec.adminHome = value;
      }
   }

   /** Parse adminPublicKeyFile */
   static class AdminPublicKeyFileParser extends FileParser {
      @Override
      protected void set(AdminAccessBuilderSpec spec, File value) {
         checkArgument(spec.adminPublicKeyFile == null, "admin public key file was already set to ",
                  spec.adminPublicKeyFile);
         spec.adminPublicKeyFile = value;
      }
   }

   /** Parse adminPrivateKeyFile */
   static class AdminPrivateKeyFileParser extends FileParser {
      @Override
      protected void set(AdminAccessBuilderSpec spec, File value) {
         checkArgument(spec.adminPrivateKeyFile == null, "admin private key file was already set to ",
                  spec.adminPrivateKeyFile);
         spec.adminPrivateKeyFile = value;
      }
   }

   /** Parse adminPassword */
   static class AdminPasswordParser extends StringParser {
      @Override
      protected void set(AdminAccessBuilderSpec spec, String value) {
         checkArgument(spec.adminPassword == null, "admin password was already set to ", spec.adminPassword);
         spec.adminPassword = value;
      }
   }

   /** Parse loginPassword */
   static class LoginPasswordParser extends StringParser {
      @Override
      protected void set(AdminAccessBuilderSpec spec, String value) {
         checkArgument(spec.loginPassword == null, "login password was already set to ", spec.loginPassword);
         spec.loginPassword = value;
      }
   }

   /** Parse lockSsh */
   static class LockSshParser extends BooleanParser {
      @Override
      protected void parseBoolean(AdminAccessBuilderSpec spec, boolean value) {
         checkArgument(spec.loginPassword == null, "lockSsh was already set to ", spec.lockSsh);
         spec.lockSsh = value;
      }
   }

   /** Parse grantSudoToAdminUser */
   static class GrantSudoToAdminUserParser extends BooleanParser {
      @Override
      protected void parseBoolean(AdminAccessBuilderSpec spec, boolean value) {
         checkArgument(spec.grantSudoToAdminUser == null, "grant sudo to admin user was already set to ",
                  spec.grantSudoToAdminUser);
         spec.grantSudoToAdminUser = value;
      }
   }

   /** Parse authorizeAdminPublicKey */
   static class AuthorizeAdminPublicKeyParser extends BooleanParser {
      @Override
      protected void parseBoolean(AdminAccessBuilderSpec spec, boolean value) {
         checkArgument(spec.authorizeAdminPublicKey == null, "authorize admin public key was already set to ",
                  spec.authorizeAdminPublicKey);
         spec.authorizeAdminPublicKey = value;
      }
   }

   /** Parse installPrivateKey */
   static class InstallAdminPrivateKeyParser extends BooleanParser {
      @Override
      protected void parseBoolean(AdminAccessBuilderSpec spec, boolean value) {
         checkArgument(spec.installAdminPrivateKey == null, "install admin private key was already set to ",
                  spec.installAdminPrivateKey);
         spec.installAdminPrivateKey = value;
      }
   }

   /** Parse resetLoginPassword */
   static class ResetLoginPasswordParser extends BooleanParser {
      @Override
      protected void parseBoolean(AdminAccessBuilderSpec spec, boolean value) {
         checkArgument(spec.resetLoginPassword == null, "reset login password was already set to ",
                  spec.resetLoginPassword);
         spec.resetLoginPassword = value;
      }
   }

   /** Specification; used for toParseableString(). */
   // transient in case people using serializers don't want this to show up
   protected transient String[] specifications;

   protected AdminAccessBuilderSpec() {
      // we want serializers like Gson to work w/o using sun.misc.Unsafe,
      // prohibited in GAE. This also implies fields are not final.
      // see http://code.google.com/p/jclouds/issues/detail?spec=925
   }

   protected AdminAccessBuilderSpec(String... specifications) {
      this.specifications = specifications;
   }

   /**
    * Creates a AdminAccessBuilderSpec from multiple specifications,
    * 
    * @param adminAccessSpecification
    *           the string form
    */
   public static AdminAccessBuilderSpec parse(String adminAccessSpecification) {
      AdminAccessBuilderSpec spec = new AdminAccessBuilderSpec(adminAccessSpecification);
      if (!adminAccessSpecification.isEmpty()) {
         for (String keyValuePair : KEYS_SPLITTER.split(adminAccessSpecification)) {
            List<String> keyAndValue = ImmutableList.copyOf(KEY_VALUE_SPLITTER.split(keyValuePair));
            checkArgument(!keyAndValue.isEmpty(), "blank key-value pair");
            checkArgument(keyAndValue.size() <= 2, "key-value pair %s with more than one equals sign", keyValuePair);

            // Find the ValueParser for the current key.
            String key = keyAndValue.get(0);
            ValueParser valueParser = VALUE_PARSERS.get(key);
            checkArgument(valueParser != null, "unknown key %s", key);

            String value = keyAndValue.size() == 1 ? null : keyAndValue.get(1);
            valueParser.parse(spec, key, value);
         }
      }
      return spec;
   }

   /**
    * Returns a AdminAccess.Builder configured according to this instance's specification.
    */
   public AdminAccess.Builder copyTo(AdminAccess.Builder builder) {
      if (adminUsername != null) {
         builder.adminUsername(adminUsername);
      }
      if (adminHome != null) {
         builder.adminHome(adminHome);
      }
      if (adminPublicKeyFile != null) {
         builder.adminPublicKey(adminPublicKeyFile);
      }
      if (adminPrivateKeyFile != null) {
         builder.adminPrivateKey(adminPrivateKeyFile);
      }
      if (adminPassword != null) {
         builder.adminPassword(adminPassword);
      }
      if (loginPassword != null) {
         builder.loginPassword(loginPassword);
      }
      if (lockSsh != null) {
         builder.lockSsh(lockSsh);
      }
      if (grantSudoToAdminUser != null) {
         builder.grantSudoToAdminUser(grantSudoToAdminUser);
      }
      if (authorizeAdminPublicKey != null) {
         builder.authorizeAdminPublicKey(authorizeAdminPublicKey);
      }
      if (installAdminPrivateKey != null) {
         builder.installAdminPrivateKey(installAdminPrivateKey);
      }
      if (resetLoginPassword != null) {
         builder.resetLoginPassword(resetLoginPassword);
      }
      return builder;
   }

   /**
    * Returns a string that can be used to parse an equivalent {@code AdminAccessSpec}. The order
    * and form of this representation is not guaranteed, except that reparsing its output will
    * produce a {@code AdminAccessSpec} equal to this instance.
    */
   public String[] toParsableStrings() {
      return specifications;
   }

   /**
    * Returns a string representation for this AdminAccessSpec instance. The form of this
    * representation is not guaranteed.
    */
   @Override
   public String toString() {
      return toStringHelper(this).addValue(Arrays.toString(toParsableStrings())).toString();
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(adminUsername, adminHome, adminPublicKeyFile, adminPrivateKeyFile, adminPassword,
               loginPassword, lockSsh, grantSudoToAdminUser, authorizeAdminPublicKey, installAdminPrivateKey,
               resetLoginPassword);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (!(obj instanceof AdminAccessBuilderSpec)) {
         return false;
      }
      AdminAccessBuilderSpec that = (AdminAccessBuilderSpec) obj;
      return equal(adminUsername, that.adminUsername) && equal(adminHome, that.adminHome)
               && equal(adminPublicKeyFile, that.adminPublicKeyFile)
               && equal(adminPrivateKeyFile, that.adminPrivateKeyFile) && equal(adminPassword, that.adminPassword)
               && equal(loginPassword, that.loginPassword) && equal(lockSsh, that.lockSsh)
               && equal(grantSudoToAdminUser, that.grantSudoToAdminUser)
               && equal(installAdminPrivateKey, that.installAdminPrivateKey)
               && equal(resetLoginPassword, that.resetLoginPassword);
   }

   public String getAdminUsername() {
      return adminUsername;
   }

   public String getAdminHome() {
      return adminHome;
   }

   public File getAdminPublicKeyFile() {
      return adminPublicKeyFile;
   }

   public File getAdminPrivateKeyFile() {
      return adminPrivateKeyFile;
   }

   public String getAdminPassword() {
      return adminPassword;
   }

   public String getLoginPassword() {
      return loginPassword;
   }

   public Boolean getLockSsh() {
      return lockSsh;
   }

   public Boolean getGrantSudoToAdminUser() {
      return grantSudoToAdminUser;
   }

   public Boolean getInstallAdminPrivateKey() {
      return installAdminPrivateKey;
   }

   public Boolean getResetLoginPassword() {
      return resetLoginPassword;
   }
}
