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

import java.util.List;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * Statements used in a shell script
 * 
 * @author Adrian Cole
 */
public class StatementList extends ForwardingList<Statement> implements Statement, AcceptsStatementVisitor {

   public final List<Statement> statements;

   public StatementList(Statement... statements) {
      this.statements = ImmutableList.copyOf(checkNotNull(statements, "statements"));
   }

   public StatementList(Iterable<Statement> statements) {
      this.statements = ImmutableList.copyOf(checkNotNull(statements, "statements"));
   }

   public String render(OsFamily family) {
      StringBuilder statementsBuilder = new StringBuilder();
      for (Statement statement : delegate()) {
         statementsBuilder.append(statement.render(family));
      }
      return statementsBuilder.toString();
   }

   @Override
   public Iterable<String> functionDependencies(OsFamily family) {
      Builder<String> functions = ImmutableList.builder();
      for (Statement statement : delegate()) {
         functions.addAll(statement.functionDependencies(family));
      }
      return functions.build();
   }

   @Override
   public void accept(StatementVisitor visitor) {
      for (Statement statement : delegate()) {
         visitor.visit(statement);
      }
   }

   @Override
   public List<Statement> delegate() {
      return statements;
   }
}
