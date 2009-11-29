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
package org.jclouds.initbuilder;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jclouds.initbuilder.domain.OsFamily;
import org.jclouds.initbuilder.domain.ShellToken;
import org.jclouds.initbuilder.util.Utils;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Creates a start script.
 * 
 * @author Adrian Cole
 */
public class InitBuilder {

   @VisibleForTesting
   Map<String, Map<String, String>> switchExec = Maps.newHashMap();

   @VisibleForTesting
   Map<String, String> variables = Maps.newHashMap();

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
   public InitBuilder switchOn(String variable, Map<String, String> valueToActions) {
      switchExec.put(checkNotNull(variable, "variable"), checkNotNull(valueToActions,
               "valueToActions"));
      return this;
   }

   /**
    * Unsets a variable to ensure it is set within the script.
    */
   public InitBuilder unsetEnvironmentVariable(String name) {
      variablesToUnset.add(checkNotNull(name, "name"));
      return this;
   }

   /**
    * Exports a variable inside the script
    */
   public InitBuilder export(String name, String value) {
      variables.put(checkNotNull(name, "name"), checkNotNull(value, "value"));
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
      StringBuilder builder = new StringBuilder();
      builder.append(ShellToken.SHEBANG.to(osFamily));
      builder.append(Utils.writeUnsetVariables(Lists.newArrayList(Iterables.transform(
               variablesToUnset, new Function<String, String>() {

                  @Override
                  public String apply(String from) {
                     if (ShellToken.tokenValueMap(osFamily).containsKey(from + "Variable"))
                        return Utils.FUNCTION_UPPER_UNDERSCORE_TO_LOWER_CAMEL.apply(ShellToken
                                 .tokenValueMap(osFamily).get(from + "Variable"));
                     return from;
                  }

               })), osFamily));
      builder.append(Utils.writeZeroPath(osFamily));
      builder.append(Utils.writeVariableExporters(variables, osFamily));
      for (Entry<String, Map<String, String>> entry : switchExec.entrySet()) {
         builder.append(Utils.writeSwitch(entry.getKey(), entry.getValue(), osFamily));
      }
      return builder.toString();
   }
}