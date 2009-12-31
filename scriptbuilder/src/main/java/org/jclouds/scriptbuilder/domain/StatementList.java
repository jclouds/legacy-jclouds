/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import java.util.List;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Statements used in a shell script
 * 
 * @author Adrian Cole
 */
public class StatementList implements Statement {

   public final List<Statement> statements;

   public StatementList(Statement... statements) {
      this.statements = Lists.newArrayList(checkNotNull(statements, "statements"));
   }

   public StatementList(Iterable<Statement> statements) {
      this.statements = Lists.newArrayList(checkNotNull(statements, "statements"));
   }

   public String render(OsFamily family) {
      StringBuilder statementsBuilder = new StringBuilder();
      for (Statement statement : statements) {
         statementsBuilder.append(statement.render(family));
      }
      return statementsBuilder.toString();
   }

   @Override
   public Iterable<String> functionDependecies(OsFamily family) {
      List<String> functions = Lists.newArrayList();
      for (Statement statement : statements) {
         Iterables.addAll(functions, statement.functionDependecies(family));
      }
      return functions;
   }
}