/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.scriptbuilder.statements.login;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.annotation.Nullable;

import org.jclouds.encryption.internal.JCECrypto;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementList;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.scriptbuilder.statements.ssh.AuthorizeRSAPublicKeys;
import org.jclouds.scriptbuilder.statements.ssh.InstallRSAPrivateKey;
import org.jclouds.scriptbuilder.util.Sha512Crypt;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

/**
 * 
 * @author Adrian Cole
 */
public class UserAdd implements Statement {
   public static UserAdd.Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String defaultHome = "/home/users";
      private String login;
      private String password;
      private String RSAPrivateKey;
      private List<String> groups = Lists.newArrayList();
      private List<String> authorizeRSAPublicKeys = Lists.newArrayList();
      private String shell = "/bin/bash";

      public UserAdd.Builder defaultHome(String defaultHome) {
         this.defaultHome = defaultHome;
         return this;
      }

      public UserAdd.Builder login(String login) {
         this.login = login;
         return this;
      }

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

      public UserAdd build() {
         return new UserAdd(login, groups, password, RSAPrivateKey, authorizeRSAPublicKeys, defaultHome, shell);
      }
   }

   public UserAdd(String login, List<String> groups, @Nullable String password, @Nullable String installRSAPrivateKey,
            List<String> authorizeRSAPublicKeys, String defaultHome, String shell) {
      this.login = checkNotNull(login, "login");
      this.password = password;
      this.groups = ImmutableList.copyOf(checkNotNull(groups, "groups"));
      this.installRSAPrivateKey = installRSAPrivateKey;
      this.authorizeRSAPublicKeys = ImmutableList
               .copyOf(checkNotNull(authorizeRSAPublicKeys, "authorizeRSAPublicKeys"));
      this.defaultHome = checkNotNull(defaultHome, "defaultHome");
      this.shell = checkNotNull(shell, "shell");
   }

   private final String defaultHome;
   private final String login;
   private final List<String> groups;
   private final String password;
   private final String installRSAPrivateKey;
   private final List<String> authorizeRSAPublicKeys;
   private final String shell;

   @Override
   public Iterable<String> functionDependencies(OsFamily family) {
      return ImmutableList.of();
   }

   @Override
   public String render(OsFamily family) {
      checkNotNull(family, "family");
      if (family == OsFamily.WINDOWS)
         throw new UnsupportedOperationException("windows not yet implemented");
      String homeDir = defaultHome + "{fs}" + login;
      ImmutableList.Builder<Statement> statements = ImmutableList.<Statement> builder();
      statements.add(Statements.exec("{md} " + homeDir));

      ImmutableMap.Builder<String, String> userAddOptions = ImmutableMap.<String, String> builder();
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
      userAddOptions.put("-d", homeDir);
      if (password != null) {
         try {
            userAddOptions.put("-p", "'" + Sha512Crypt.makeShadowLine(password, null, new JCECrypto()) + "'");
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
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((login == null) ? 0 : login.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      UserAdd other = (UserAdd) obj;
      if (login == null) {
         if (other.login != null)
            return false;
      } else if (!login.equals(other.login))
         return false;
      return true;
   }
}