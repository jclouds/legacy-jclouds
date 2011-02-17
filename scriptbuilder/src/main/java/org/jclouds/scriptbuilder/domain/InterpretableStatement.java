/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.scriptbuilder.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;

import org.jclouds.scriptbuilder.util.Utils;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

/**
 * Statement used in a shell script
 * 
 * @author Adrian Cole
 */
public class InterpretableStatement implements Statement {

   private String[] statements;

   public InterpretableStatement(String... statements) {
      this.statements = checkNotNull(statements, "statements");
   }

   public String render(OsFamily family) {
      return Utils
               .replaceTokens(Joiner.on(ShellToken.LF.to(family)).join(statements), ShellToken.tokenValueMap(family));
   }

   @Override
   public Iterable<String> functionDependecies(OsFamily family) {
      return ImmutableList.of();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + Arrays.hashCode(statements);
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
      InterpretableStatement other = (InterpretableStatement) obj;
      if (!Arrays.equals(statements, other.statements))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[statements=" + Arrays.toString(statements) + "]";
   }
}