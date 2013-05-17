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
import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static org.jclouds.scriptbuilder.domain.Statements.appendFile;
import static org.jclouds.scriptbuilder.domain.Statements.createOrOverwriteFile;
import static org.jclouds.scriptbuilder.domain.Statements.exec;
import static org.jclouds.scriptbuilder.domain.Statements.interpret;

import java.util.List;
import java.util.Map;

import org.jclouds.scriptbuilder.ExitInsteadOfReturn;
import org.jclouds.scriptbuilder.ScriptBuilder;
import org.jclouds.scriptbuilder.util.Utils;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterables;

/**
 * Creates a run script
 * 
 * @author Adrian Cole
 */
public class CreateRunScript extends StatementList {
   public static final String DELIMITER = "END_OF_JCLOUDS_SCRIPT";
   final String instanceName;
   final Iterable<String> exports;
   final String pwd;
   
   /**
    * @param exports
    *            variable names to export in UPPER_UNDERSCORE case format
    */
   public CreateRunScript(String instanceName, Iterable<String> exports, String pwd, Iterable<Statement> statements) {
      super(statements);
      this.instanceName = checkNotNull(instanceName, "INSTANCE_NAME");
      this.exports = checkNotNull(exports, "exports");
      this.pwd = checkNotNull(pwd, "pwd").replaceAll("[/\\\\]", "{fs}");
   }

   public static class AddExport implements Statement {
      final String export;
      final String value;
      
      /**
       * @param export
       *            variable name in UPPER_UNDERSCORE case format
       */
      public AddExport(String export, String value) {
         this.export = checkNotNull(export, "export");
         this.value = checkNotNull(value, "value");
      }

      public static final Map<OsFamily, String> OS_TO_EXPORT_PATTERN = ImmutableMap.of(OsFamily.UNIX,
            "export {export}='{value}'\n", OsFamily.WINDOWS, "set {export}={value}\r\n");

      @Override
      public Iterable<String> functionDependencies(OsFamily family) {
         return ImmutableList.of();
      }

      @Override
      public String render(OsFamily family) {
         return Utils
               .replaceTokens(OS_TO_EXPORT_PATTERN.get(family), ImmutableMap.of("export", export, "value", value));
      }
   }

   @Override
   public Iterable<String> functionDependencies(OsFamily family) {
      return ImmutableList.of();
   }

   @Override
   public String render(OsFamily family) {
      if (checkNotNull(family, "family") == OsFamily.WINDOWS)
         throw new UnsupportedOperationException("windows not yet implemented");
      List<Statement> statements = newArrayList();
      final Map<String, String> tokenMap = ShellToken.tokenValueMap(family);
      String runScript = Utils.replaceTokens(pwd + "{fs}" + instanceName + ".{sh}", tokenMap);
      statements.add(interpret(String.format("{md} %s{lf}", pwd)));
      StringBuilder builder = new StringBuilder();
      builder.append("\n");
      addUnixRunScriptHeader(runScript, builder);
      builder.append("\n");
      addUnixRunScript(runScript, builder);
      builder.append("\n");
      addUnixRunScriptFooter(runScript, builder);
      builder.append("\n");
      statements.add(interpret(builder.toString()));
      statements.add(exec("chmod u+x " + runScript));
      return new StatementList(statements).render(family);
   }

   private void addUnixRunScriptFooter(String runScript, StringBuilder builder) {
      builder.append("# add runscript footer\n");
      Iterable<String> endScript = Splitter.on(ShellToken.LF.to(OsFamily.UNIX)).split(
            ShellToken.END_SCRIPT.to(OsFamily.UNIX));
      builder.append(appendFile(runScript, endScript, DELIMITER).render(OsFamily.UNIX));
   }

   private void addUnixRunScript(String runScript, StringBuilder builder) {
      builder.append("# add desired commands from the user\n");
      Builder<String> userCommands = ImmutableList.builder();
      userCommands.add("cd " + pwd);
      for (Statement statement : statements) {
         if (statement instanceof Call
               || (statement instanceof StatementList && any(StatementList.class.cast(statement).delegate(),
                     instanceOf(Call.class)))) {
            statement = new ExitInsteadOfReturn(statement);
         }
         userCommands.addAll(Splitter.on('\n').split(statement.render(OsFamily.UNIX)));
      }
      builder.append(appendFile(runScript, userCommands.build(), DELIMITER).render(OsFamily.UNIX));
   }

   private void addUnixRunScriptHeader(String runScript, StringBuilder builder) {
      builder.append("# create runscript header\n");

      Builder<String> beginningOfFile = ImmutableList.builder();
      beginningOfFile.addAll(Splitter.on(ShellToken.LF.to(OsFamily.UNIX)).split(
            ShellToken.BEGIN_SCRIPT.to(OsFamily.UNIX)));
      beginningOfFile.add(format("PROMPT_COMMAND='echo -ne \\\"\\033]0;%s\\007\\\"'", instanceName));
      beginningOfFile.add(Utils.writeZeroPath(OsFamily.UNIX));
      beginningOfFile.add(format("export INSTANCE_NAME='%s'", instanceName));
      builder.append(createOrOverwriteFile(runScript, beginningOfFile.build(), DELIMITER).render(OsFamily.UNIX));

      // expanding variables here.
      builder.append(AppendFile.builder().path(runScript).delimiter(DELIMITER).expandVariables(true)
            .lines(Iterables.transform(exports, new Function<String, String>() {

               @Override
               public String apply(String export) {
                  return new StringBuilder().append("export ").append(export).append("='$")
                        .append(export).append("'").toString();
               }
            })).build().render(OsFamily.UNIX));

      Map<String, String> functionsToWrite = ScriptBuilder.resolveFunctionDependenciesForStatements(
            ImmutableMap.<String, String> of("abort", Utils.writeFunctionFromResource("abort", OsFamily.UNIX)),
            statements, OsFamily.UNIX);

      // if there are more functions than simply abort
      if (functionsToWrite.size() > 1) {
         StringBuilder functions = new StringBuilder();
         ScriptBuilder.writeFunctions(functionsToWrite, OsFamily.UNIX, functions);
         builder.append(appendFile(runScript, functions.toString(), DELIMITER).render(OsFamily.UNIX));
      }
   }
}
