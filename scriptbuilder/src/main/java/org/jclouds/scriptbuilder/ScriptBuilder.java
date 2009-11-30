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
package org.jclouds.scriptbuilder;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.ShellToken;
import org.jclouds.scriptbuilder.util.Utils;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Creates a shell script.
 * 
 * @author Adrian Cole
 */
public class ScriptBuilder {

   @VisibleForTesting
   Map<String, Map<String, String>> switchExec = Maps.newHashMap();

   @VisibleForTesting
   List<String> variableScopes = Lists.newArrayList();

   @VisibleForTesting
   Map<String, String> functions = Maps.newHashMap();

   @VisibleForTesting
   List<String> variablesToUnset = Lists.newArrayList("path", "javaHome", "libraryPath");

   /**
    * Adds a switch statement to the script. If its value is found, it will invoke the corresponding
    * action.
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
   public ScriptBuilder switchOn(String variable, Map<String, String> valueToActions) {
      switchExec.put(checkNotNull(variable, "variable"), checkNotNull(valueToActions,
               "valueToActions"));
      return this;
   }

   /**
    * Unsets a variable to ensure it is set within the script.
    */
   public ScriptBuilder unsetEnvironmentVariable(String name) {
      variablesToUnset.add(checkNotNull(name, "name"));
      return this;
   }

   /**
    * Exports a variable inside the script
    */
   public ScriptBuilder addEnvironmentVariableScope(String scopeName, Map<String, String> variables) {
      variableScopes.add(checkNotNull(scopeName, "scopeName"));
      functions.put(scopeName, Utils.writeFunction(scopeName, Utils
               .writeVariableExporters(checkNotNull(variables, "variables"))));
      return this;
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
   public String build(final OsFamily osFamily) {
      final Map<String, String> tokenValueMap = ShellToken.tokenValueMap(osFamily);
      StringBuilder builder = new StringBuilder();
      builder.append(ShellToken.BEGIN_SCRIPT.to(osFamily));
      builder.append(Utils.writeUnsetVariables(Lists.newArrayList(Iterables.transform(
               variablesToUnset, new Function<String, String>() {
                  @Override
                  public String apply(String from) {
                     if (tokenValueMap.containsKey(from + "Variable"))
                        return Utils.FUNCTION_UPPER_UNDERSCORE_TO_LOWER_CAMEL.apply(tokenValueMap
                                 .get(from + "Variable"));
                     return from;
                  }

               })), osFamily));
      if (functions.size() > 0) {
         builder.append(ShellToken.BEGIN_FUNCTIONS.to(osFamily));
         builder.append(Utils.writeFunctionFromResource("abort", osFamily));
         for (String function : functions.values()) {
            builder.append(Utils.replaceTokens(function, tokenValueMap));
         }
         builder.append(ShellToken.END_FUNCTIONS.to(osFamily));
      }
      builder.append(Utils.writeZeroPath(osFamily));
      for (Entry<String, Map<String, String>> entry : switchExec.entrySet()) {
         builder.append(Utils.writeSwitch(entry.getKey(), entry.getValue(), osFamily));
      }
      builder.append(ShellToken.END_SCRIPT.to(osFamily));
      return builder.toString();
   }
}