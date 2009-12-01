/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.scriptbuilder.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jclouds.scriptbuilder.util.Utils;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Statement used in a shell script
 * 
 * @author Adrian Cole
 */
public class Switch implements Statement {

   public static final Map<OsFamily, String> OS_TO_SWITCH_PATTERN = ImmutableMap.of(OsFamily.UNIX,
            "case ${variable} in\n", OsFamily.WINDOWS, "goto CASE%{variable}\r\n");

   public static final Map<OsFamily, String> OS_TO_END_SWITCH_PATTERN = ImmutableMap.of(
            OsFamily.UNIX, "esac\n", OsFamily.WINDOWS, ":END_SWITCH\r\n");

   public static final Map<OsFamily, String> OS_TO_CASE_PATTERN = ImmutableMap.of(OsFamily.UNIX,
            "{value})\n   {action}   ;;\n", OsFamily.WINDOWS,
            ":CASE_{value}\r\n   {action}   GOTO END_SWITCH\r\n");

   private final String variable;

   private final Map<String, Statement> valueToActions;

   /**
    * Generates a switch statement based on {@code variable}. If its value is found to be a key in
    * {@code valueToActions}, the corresponding action is invoked.
    * 
    * <p/>
    * Ex. variable is {@code 1} - the first argument to the script<br/>
    * and valueToActions is {"start" -> "echo hello", "stop" -> "echo goodbye"}<br/>
    * the script created will respond accordingly:<br/>
    * {@code ./script start }<br/>
    * << returns hello<br/>
    * {@code ./script stop }<br/>
    * << returns goodbye<br/>
    * 
    * @param variable
    *           - shell variable to switch on
    * @param valueToActions
    *           - case statements, if the value of the variable matches a key, the corresponding
    *           value will be invoked.
    */
   public Switch(String variable, Map<String, Statement> valueToActions) {
      this.variable = checkNotNull(variable, "variable");
      this.valueToActions = checkNotNull(valueToActions, "valueToActions");
   }

   public String render(OsFamily family) {
      StringBuilder switchClause = new StringBuilder();
      switchClause.append(Utils.replaceTokens(OS_TO_SWITCH_PATTERN.get(family), ImmutableMap.of(
               "variable", CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, variable))));

      for (Entry<String, Statement> entry : valueToActions.entrySet()) {
         switchClause.append(Utils.replaceTokens(OS_TO_CASE_PATTERN.get(family), ImmutableMap.of(
                  "value", entry.getKey(), "action", entry.getValue().render(family))));
      }

      switchClause.append(OS_TO_END_SWITCH_PATTERN.get(family));
      return switchClause.toString();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((valueToActions == null) ? 0 : valueToActions.hashCode());
      result = prime * result + ((variable == null) ? 0 : variable.hashCode());
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
      Switch other = (Switch) obj;
      if (valueToActions == null) {
         if (other.valueToActions != null)
            return false;
      } else if (!valueToActions.equals(other.valueToActions))
         return false;
      if (variable == null) {
         if (other.variable != null)
            return false;
      } else if (!variable.equals(other.variable))
         return false;
      return true;
   }

   @Override
   public Iterable<String> functionDependecies(OsFamily family) {
      List<String> functions = Lists.newArrayList();
      for (Statement statement : valueToActions.values()) {
         Iterables.addAll(functions, statement.functionDependecies(family));
      }
      return functions;
   }
}