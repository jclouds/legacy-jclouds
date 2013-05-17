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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static java.lang.String.format;
import static org.jclouds.scriptbuilder.domain.Statements.exec;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementList;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

/**
 * Replaces the password entry for a user in the shadow file, using SHA-512
 * crypt syntax.
 * 
 * @author Adrian Cole
 */
public class ReplaceShadowPasswordEntry implements Statement {

   private final Function<String, String> cryptFunction;
   private final String login;
   private final String password;

   public ReplaceShadowPasswordEntry(Function<String, String> cryptFunction, String login, String password) {
      this.cryptFunction = checkNotNull(cryptFunction, "cryptFunction");
      this.login = checkNotNull(login, "login");
      this.password = checkNotNull(password, "password");
   }

   @Override
   public Iterable<String> functionDependencies(OsFamily family) {
      return ImmutableList.of();
   }

   @Override
   public String render(OsFamily family) {
      checkNotNull(family, "family");
      if (family == OsFamily.WINDOWS)
         throw new UnsupportedOperationException("windows not yet implemented");
      try {
         String shadowPasswordEntry = cryptFunction.apply(password);
         String shadowFile = "/etc/shadow";
         // note we are using awk variables so that the user can be defined as a
         // shell variable (ex. $USER) As the block is in single quotes,
         // shell interpolation wouldn't work otherwise
         Statement replaceEntryInTempFile = exec(format(
               "awk -v user=^%1$s: -v password='%2$s' 'BEGIN { FS=OFS=\":\" } $0 ~ user { $2 = password } 1' %3$s >%3$s.%1$s",
               login, shadowPasswordEntry, shadowFile));
         // would have preferred to use exec <>3 && style, but for some reason
         // the sha512 line breaks in both awk and sed during an inline
         // expansion. unfortunately, we have to save a temp file. In this case,
         // somewhat avoiding collisions by naming the file .user, conceding it
         // isn't using any locks to prevent overlapping changes
         Statement replaceShadowFile = exec(format("test -f %2$s.%1$s && mv %2$s.%1$s %2$s", login, shadowFile));
         return new StatementList(ImmutableList.of(replaceEntryInTempFile, replaceShadowFile)).render(family);
      } catch (Exception e) {
         propagate(e);
         return null;
      }
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
      ReplaceShadowPasswordEntry other = (ReplaceShadowPasswordEntry) obj;
      if (login == null) {
         if (other.login != null)
            return false;
      } else if (!login.equals(other.login))
         return false;
      return true;
   }
}
