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
package org.jclouds.scriptbuilder.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

/**
 * Statement used in a shell script without modifications apart from a trailing newline.
 * 
 * @author Adrian Cole
 */
public class LiteralStatement implements Statement {

   private String statement;

   public LiteralStatement(String statement) {
      this.statement = checkNotNull(statement, "statement");
   }

   public String render(OsFamily family) {
      return statement + ShellToken.LF.to(family);
   }

   @Override
   public Iterable<String> functionDependencies(OsFamily family) {
      return ImmutableList.of();
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(statement);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (!(obj instanceof LiteralStatement))
         return false;
      LiteralStatement other = LiteralStatement.class.cast(obj);
      return Objects.equal(statement, other.statement);
   }

   @Override
   public String toString() {
      return statement + "{lf}";
   }
}
