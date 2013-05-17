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
package org.jclouds.scriptbuilder;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jclouds.scriptbuilder.domain.AcceptsStatementVisitor;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.ShellToken;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementVisitor;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.scriptbuilder.util.Utils;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Creates a shell script.
 * 
 * @author Adrian Cole
 */
public class ScriptBuilder implements Statement, AcceptsStatementVisitor {

   @VisibleForTesting
   List<Statement> statements = Lists.newArrayList();

   @VisibleForTesting
   Map<String, Map<String, String>> variableScopes = Maps.newLinkedHashMap();

   @VisibleForTesting
   List<String> variablesToUnset = Lists.newArrayList("PATH", "JAVA_HOME", "LIBRARY_PATH");

   public ScriptBuilder addStatement(Statement statement) {
      statements.add(checkNotNull(statement, "statement"));
      return this;
   }

   /**
    * Unsets a variable to ensure it is set within the script.
    * @param variable name in UPPER_UNDERSCORE case format
    */
   public ScriptBuilder unsetEnvironmentVariable(String name) {
      variablesToUnset.add(checkNotNull(name, "name"));
      return this;
   }

   /**
    * Exports a variable inside the script
    * @param scopeName
    * @param variables keys are the variables to export in UPPER_UNDERSCORE case format
    */
   public ScriptBuilder addEnvironmentVariableScope(String scopeName, Map<String, String> variables) {
      variableScopes.put(checkNotNull(scopeName, "scopeName"), checkNotNull(variables, "variables"));
      return this;
   }

   // TODO: make scriptbuilder smart enough to know when a statement is a direct
   // child of the script, and automatically convert
   public static Statement forget(String instanceName, String script, String logDir) {
      return new ExitInsteadOfReturn(Statements.forget(instanceName, script, logDir));
   }

   public static Statement findPid(String pid) {
      return new ExitInsteadOfReturn(Statements.findPid(pid));
   }

   public static Statement call(String fn, String... args) {
      return new ExitInsteadOfReturn(Statements.call(fn, args));
   }

   /**
    * builds the shell script, by adding the following
    * <ol>
    * <li>shell declaration line</li>
    * <li>variable exports</li>
    * <li>case/switch</li>
    * </ol>
    * 
    * @param osFamily
    *           whether to write a cmd or bash script.
    */

   @Override
   public String render(OsFamily osFamily) {
      Map<String, String> functions = Maps.newLinkedHashMap();
      functions.put("abort", Utils.writeFunctionFromResource("abort", osFamily));

      for (Entry<String, Map<String, String>> entry : variableScopes.entrySet()) {
         functions.put(entry.getKey(), Utils.writeFunction(entry.getKey(), Utils.writeVariableExporters(entry
                  .getValue(), osFamily)));
      }
      StringBuilder builder = new StringBuilder();
      builder.append(ShellToken.BEGIN_SCRIPT.to(osFamily));
      builder.append(Utils.writeUnsetVariables(variablesToUnset, osFamily));
      Map<String, String> functionsToWrite = resolveFunctionDependenciesForStatements(functions, statements, osFamily);
      writeFunctions(functionsToWrite, osFamily, builder);
      builder.append(Utils.writeZeroPath(osFamily));
      StringBuilder statementBuilder = new StringBuilder();
      for (Statement statement : statements) {
         statementBuilder.append(statement.render(osFamily));
      }
      builder.append(statementBuilder.toString());
      builder.append(ShellToken.END_SCRIPT.to(osFamily));
      return builder.toString();
   }

   public static void writeFunctions(Map<String, String> functionsToWrite, OsFamily osFamily, StringBuilder builder) {
      if (functionsToWrite.size() > 0) {
         builder.append(ShellToken.BEGIN_FUNCTIONS.to(osFamily));
         for (String function : functionsToWrite.values()) {
            builder.append(Utils.replaceTokens(function, ShellToken.tokenValueMap(osFamily)));
         }
         builder.append(ShellToken.END_FUNCTIONS.to(osFamily));
      }
   }

   @VisibleForTesting
   public static Map<String, String> resolveFunctionDependenciesForStatements(Map<String, String> knownFunctions,
         Iterable<Statement> statements, final OsFamily osFamily) {
      Builder<String, String> builder = ImmutableMap.builder();
      builder.putAll(knownFunctions);
      Set<String> dependentFunctions = ImmutableSet.copyOf(Iterables.concat(Iterables.transform(statements,
            new Function<Statement, Iterable<String>>() {
               @Override
               public Iterable<String> apply(Statement from) {
                  return from.functionDependencies(osFamily);
               }
            })));
      for (String unresolved : Sets.difference(dependentFunctions, knownFunctions.keySet()))
         builder.put(unresolved, Utils.writeFunctionFromResource(unresolved, osFamily));
      return builder.build();
   }

   @Override
   public Iterable<String> functionDependencies(OsFamily family) {
      return ImmutableSet.<String> of();
   }

   @Override
   public void accept(StatementVisitor visitor) {
      for (Statement statement : statements) {
         visitor.visit(statement);
      }
   }
}
