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
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementList;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.scriptbuilder.statements.ssh.AuthorizeRSAPublicKeys;
import org.jclouds.scriptbuilder.statements.ssh.InstallRSAPrivateKey;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

/**
 * Creates a statement that will add a given user to a machine ("login"), with optional
 * password, groups, private key, and authorized keys.
 * <p>
 * This is supported on most *nix environments. Not currently supported on Windows.
 * <p>
 * Note that some places where this is used may have stricter requirements on the parameters
 * (for example {@link AdminAccess} requires password and keys).
 *
 * @author Adrian Cole
 */
public class UserAdd implements Statement {
   public static UserAdd.Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private Function<String, String> cryptFunction;
      private String defaultHome = "/home/users";
      private String home;
      private String login;
      private String password;
      private String RSAPrivateKey;
      private List<String> groups = Lists.newArrayList();
      private List<String> authorizeRSAPublicKeys = Lists.newArrayList();
      private String shell = "/bin/bash";
      private String fullName;

      /**
       * @see  org.jclouds.compute.functions.Sha512Crypt
       */
      public Builder cryptFunction(Function<String, String> cryptFunction) {
         this.cryptFunction = cryptFunction;
         return this;
      }
      
      /**
       * See --home in `man useradd`.
       */
      public UserAdd.Builder home(String home) {
         this.home = home;
         return this;
      }

      /**
       * See --base-dir in `man useradd`.
       */
      public UserAdd.Builder defaultHome(String defaultHome) {
         this.defaultHome = defaultHome;
         return this;
      }

      /** the username of the user to add (not the login to use when performing the add) */
      public UserAdd.Builder login(String login) {
         this.login = login;
         return this;
      }

      /** the password to add for the user (not the password to use when logging in to perform the add) */
      public UserAdd.Builder password(String password) {
         this.password = password;
         return this;
      }

      public UserAdd.Builder group(String group) {
         this.groups.add(checkNotNull(group, "group"));
         return this;
      }

      public UserAdd.Builder groups(Iterable<String> groups) {
         this.groups = ImmutableList.<String> copyOf(checkNotNull(groups, "groups"));
         return this;
      }

      public UserAdd.Builder installRSAPrivateKey(String RSAPrivateKey) {
         this.RSAPrivateKey = RSAPrivateKey;
         return this;
      }

      public UserAdd.Builder authorizeRSAPublicKey(String RSAPublicKey) {
         this.authorizeRSAPublicKeys.add(checkNotNull(RSAPublicKey, "RSAPublicKey"));
         return this;
      }

      public UserAdd.Builder authorizeRSAPublicKeys(Iterable<String> RSAPublicKeys) {
         this.authorizeRSAPublicKeys = ImmutableList.<String> copyOf(checkNotNull(RSAPublicKeys, "RSAPublicKeys"));
         return this;
      }

      public UserAdd.Builder shell(String shell) {
         this.shell = shell;
         return this;
      }

      public UserAdd.Builder fullName(String fullName) {
         this.fullName = fullName;
         return this;
      }

      public UserAdd build() {
         return new UserAdd(cryptFunction, login, groups, password, RSAPrivateKey, authorizeRSAPublicKeys, home,
               defaultHome, shell, fullName);
      }
   }

   public UserAdd(Function<String, String> cryptFunction, String login, List<String> groups, @Nullable String password,
         @Nullable String installRSAPrivateKey, List<String> authorizeRSAPublicKeys, String defaultHome, String shell) {
      this(cryptFunction, login, groups, password, installRSAPrivateKey, authorizeRSAPublicKeys, null, defaultHome,
            shell, login);
   }

   public UserAdd(Function<String, String> cryptFunction, String login, List<String> groups, @Nullable String password,
         @Nullable String installRSAPrivateKey, List<String> authorizeRSAPublicKeys, @Nullable String home,
         String defaultHome, String shell, String fullName) {
      this.login = checkNotNull(login, "login");
      this.password = password;
      this.cryptFunction = password == null ? null : checkNotNull(cryptFunction,
            "cryptFunction must be set! ex. org.jclouds.compute.functions.Sha512Crypt.INSTANCE");
      this.groups = ImmutableList.copyOf(checkNotNull(groups, "groups"));
      this.installRSAPrivateKey = installRSAPrivateKey;
      this.authorizeRSAPublicKeys = ImmutableList
            .copyOf(checkNotNull(authorizeRSAPublicKeys, "authorizeRSAPublicKeys"));
      this.home = home;
      this.defaultHome = checkNotNull(defaultHome, "defaultHome");
      this.shell = checkNotNull(shell, "shell");
      this.fullName = fullName;
   }
   
   private final Function<String, String> cryptFunction;
   private final String home;
   private final String defaultHome;
   private final String login;
   private final List<String> groups;
   private final String password;
   private final String installRSAPrivateKey;
   private final List<String> authorizeRSAPublicKeys;
   private final String shell;
   private final String fullName;

   @Override
   public Iterable<String> functionDependencies(OsFamily family) {
      return ImmutableList.of();
   }

   @Override
   public String render(OsFamily family) {
      checkNotNull(family, "family");
      if (family == OsFamily.WINDOWS)
         throw new UnsupportedOperationException("windows not yet implemented");
      String homeDir = (home != null) ? home : (defaultHome + '/' + login);
      ImmutableList.Builder<Statement> statements = ImmutableList.builder();
      // useradd cannot create the default homedir
      statements.add(Statements.exec("{md} " + homeDir.substring(0, homeDir.lastIndexOf('/'))));

      ImmutableMap.Builder<String, String> userAddOptions = ImmutableMap.builder();
      // Include the username as the full name for now.

      if (Strings.isNullOrEmpty(fullName)) {
         userAddOptions.put("-c", login);
      } else {
         userAddOptions.put("-c", "'" + fullName + "'");
      }

      userAddOptions.put("-s", shell);
      if (groups.size() > 0) {
         for (String group : groups)
            statements.add(Statements.exec("groupadd -f " + group));

         List<String> groups = Lists.newArrayList(this.groups);
         String primaryGroup = groups.remove(0);
         userAddOptions.put("-g", primaryGroup);
         if (groups.size() > 0)
            userAddOptions.put("-G", Joiner.on(',').join(groups));

      }
      userAddOptions.put("-m", "");
      userAddOptions.put("-d", homeDir);
      if (password != null) {
         try {
            userAddOptions.put("-p", "'" + cryptFunction.apply(password) + "'");
         } catch (Exception e) {
            Throwables.propagate(e);
         }
      }

      String options = Joiner.on(' ').withKeyValueSeparator(" ").join(userAddOptions.build());

      statements.add(Statements.exec(String.format("useradd %s %s", options, login)));

      if (authorizeRSAPublicKeys.size() > 0 || installRSAPrivateKey != null) {
         String sshDir = homeDir + "{fs}.ssh";
         if (authorizeRSAPublicKeys.size() > 0)
            statements.add(new AuthorizeRSAPublicKeys(sshDir, authorizeRSAPublicKeys));
         if (installRSAPrivateKey != null)
            statements.add(new InstallRSAPrivateKey(sshDir, installRSAPrivateKey));
      }
      statements.add(Statements.exec(String.format("chown -R %s %s", login, homeDir)));
      return new StatementList(statements.build()).render(family);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      UserAdd that = UserAdd.class.cast(o);
      return equal(this.login, that.login);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(login);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("login", login).toString();
   }
}
