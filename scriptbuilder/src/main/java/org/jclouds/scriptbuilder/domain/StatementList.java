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
package org.jclouds.scriptbuilder.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * Statements used in a shell script
 * 
 * @author Adrian Cole
 */
public class StatementList implements Statement, AcceptsStatementVisitor {

   public final List<Statement> statements;

   public StatementList(Statement... statements) {
      this.statements = ImmutableList.copyOf(checkNotNull(statements, "statements"));
   }

   public StatementList(Iterable<Statement> statements) {
      this.statements = ImmutableList.copyOf(checkNotNull(statements, "statements"));
   }

   public String render(OsFamily family) {
      StringBuilder statementsBuilder = new StringBuilder();
      for (Statement statement : statements) {
         statementsBuilder.append(statement.render(family));
      }
      return statementsBuilder.toString();
   }

   @Override
   public Iterable<String> functionDependencies(OsFamily family) {
      Builder<String> functions = ImmutableList.<String> builder();
      for (Statement statement : statements) {
         functions.addAll(statement.functionDependencies(family));
      }
      return functions.build();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((statements == null) ? 0 : statements.hashCode());
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
      StatementList other = (StatementList) obj;
      if (statements == null) {
         if (other.statements != null)
            return false;
      } else if (!statements.equals(other.statements))
         return false;
      return true;
   }

   @Override
   public void accept(StatementVisitor visitor) {
      for (Statement statement : statements) {
         visitor.visit(statement);
      }
   }
}