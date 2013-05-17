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
import java.util.Map;
import java.util.Map.Entry;

import org.jclouds.scriptbuilder.util.Utils;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Statement used in a shell script
 * 
 * @author Adrian Cole
 */
public class SwitchArg implements Statement, AcceptsStatementVisitor {

   private static final String INDENT = "   ";

   public static final Map<OsFamily, String> OS_TO_SWITCH_PATTERN = ImmutableMap.of(OsFamily.UNIX, "case ${arg} in\n",
            OsFamily.WINDOWS, "goto CASE_%{arg}\r\n");

   public static final Map<OsFamily, String> OS_TO_END_SWITCH_PATTERN = ImmutableMap.of(OsFamily.UNIX, "esac\n",
            OsFamily.WINDOWS, ":END_SWITCH\r\n");

   public static final Map<OsFamily, String> OS_TO_CASE_PATTERN = ImmutableMap.of(OsFamily.UNIX,
            "{value})\n{action};;\n", OsFamily.WINDOWS, ":CASE_{value}\r\n{action}GOTO END_SWITCH\r\n");

   private final int arg;

   private final Map<String, Statement> valueToActions;

   /**
    * Generates a switch statement based on {@code arg}. If its value is found to be a key in
    * {@code valueToActions}, the corresponding action is invoked.
    * 
    * <p/>
    * Ex. arg is {@code 1} - the first argument to the script<br/>
    * and valueToActions is {"start" -> "echo hello", "stop" -> "echo goodbye"}<br/>
    * the script created will respond accordingly:<br/>
    * {@code ./script start }<br/>
    * << returns hello<br/>
    * {@code ./script stop }<br/>
    * << returns goodbye<br/>
    * 
    * @param arg
    *           - shell arg to switch on
    * @param valueToActions
    *           - case statements, if the value of the arg matches a key, the corresponding value
    *           will be invoked.
    */
   public SwitchArg(int arg, Map<String, Statement> valueToActions) {
      this.arg = arg;
      this.valueToActions = checkNotNull(valueToActions, "valueToActions");
   }

   public String render(OsFamily family) {
      StringBuilder switchClause = new StringBuilder();
      addArgValidation(switchClause, family);
      switchClause.append(Utils.replaceTokens(OS_TO_SWITCH_PATTERN.get(family), ImmutableMap.of("arg", arg + "")));

      for (Entry<String, Statement> entry : valueToActions.entrySet()) {

         StringBuilder actionBuilder = new StringBuilder();
         boolean shouldIndent = true;
         boolean inRunScript = false;
         boolean inCreateFile = false;
         for (String line : Splitter.on(ShellToken.LF.to(family)).split(entry.getValue().render(family))) {
            if (shouldIndent)
               actionBuilder.append(INDENT);
            actionBuilder.append(line).append(ShellToken.LF.to(family));
            if (line.indexOf(CreateRunScript.DELIMITER) != -1) {
               inRunScript = inRunScript ? false : true;

            }
            if (line.indexOf(AppendFile.DELIMITER) != -1) {
               inCreateFile = inCreateFile ? false : true;
            }
            shouldIndent = !inCreateFile && !inRunScript;

         }
         actionBuilder.delete(actionBuilder.lastIndexOf(ShellToken.LF.to(family)), actionBuilder.length());
         switchClause.append(Utils.replaceTokens(OS_TO_CASE_PATTERN.get(family), ImmutableMap.of("value", entry
                  .getKey(), "action", actionBuilder.toString())));
      }

      switchClause.append(OS_TO_END_SWITCH_PATTERN.get(family));
      return switchClause.toString();
   }

   @VisibleForTesting
   void addArgValidation(StringBuilder switchClause, OsFamily family) {
      if (family.equals(OsFamily.WINDOWS)) {
         for (String value : valueToActions.keySet()) {
            switchClause.append("if not \"%").append(arg).append(String.format("\" == \"%s\" ", value));
         }
         switchClause.append("(\r\n   set EXCEPTION=bad argument: %").append(arg).append(" not in ");
         switchClause.append(Joiner.on(" ").join(valueToActions.keySet()));
         switchClause.append("\r\n   goto abort\r\n)\r\n");
      }
   }

   @Override
   public Iterable<String> functionDependencies(OsFamily family) {
      List<String> functions = Lists.newArrayList();
      for (Statement statement : valueToActions.values()) {
         Iterables.addAll(functions, statement.functionDependencies(family));
      }
      return functions;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + arg;
      result = prime * result + ((valueToActions == null) ? 0 : valueToActions.hashCode());
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
      SwitchArg other = (SwitchArg) obj;
      if (arg != other.arg)
         return false;
      if (valueToActions == null) {
         if (other.valueToActions != null)
            return false;
      } else if (!valueToActions.equals(other.valueToActions))
         return false;
      return true;
   }

   @Override
   public void accept(StatementVisitor visitor) {
      for (Statement statement : valueToActions.values()) {
         visitor.visit(statement);
      }
   }
}
