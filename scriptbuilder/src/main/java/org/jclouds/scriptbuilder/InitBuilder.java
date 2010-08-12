/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.scriptbuilder;

import static org.jclouds.scriptbuilder.domain.Statements.call;
import static org.jclouds.scriptbuilder.domain.Statements.createRunScript;
import static org.jclouds.scriptbuilder.domain.Statements.findPid;
import static org.jclouds.scriptbuilder.domain.Statements.forget;
import static org.jclouds.scriptbuilder.domain.Statements.interpret;
import static org.jclouds.scriptbuilder.domain.Statements.kill;
import static org.jclouds.scriptbuilder.domain.Statements.newStatementList;
import static org.jclouds.scriptbuilder.domain.Statements.switchArg;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * Creates an init script file
 * 
 * @author Adrian Cole
 */
public class InitBuilder extends ScriptBuilder {

   @SuppressWarnings("unchecked")
   public InitBuilder(String instanceName, String instanceHome, String logDir,
            Map<String, String> variables, String... execLines) {
      Map<String, String> defaultVariables = ImmutableMap.of("instanceName", instanceName,
               "instanceHome", instanceHome, "logDir", logDir);
      addEnvironmentVariableScope("default", defaultVariables)
               .addEnvironmentVariableScope(instanceName, variables)
               .addStatement(
                        switchArg(
                                 1,
                                 new ImmutableMap.Builder()
                                          .put(
                                                   "init",
                                                   newStatementList(call("default"),
                                                            call(instanceName), createRunScript(
                                                                     instanceName,// TODO: convert
                                                                                  // so
                                                                     // that
                                                                     // createRunScript
                                                                     // can take from a
                                                                     // variable
                                                                     Iterables.concat(variables
                                                                              .keySet(),
                                                                              defaultVariables
                                                                                       .keySet()),
                                                                     "{varl}INSTANCE_HOME{varr}",
                                                                     execLines)))
                                          .put(
                                                   "status",
                                                   newStatementList(
                                                            call("default"),
                                                            findPid("{varl}INSTANCE_NAME{varr}"),
                                                            interpret("echo [{varl}FOUND_PID{varr}]{lf}")))
                                          .put(
                                                   "stop",
                                                   newStatementList(call("default"),
                                                            findPid("{varl}INSTANCE_NAME{varr}"),
                                                            kill()))
                                          .put(
                                                   "start",
                                                   newStatementList(
                                                            call("default"),
                                                            forget(
                                                                     "{varl}INSTANCE_NAME{varr}",
                                                                     "{varl}INSTANCE_HOME{varr}{fs}{varl}INSTANCE_NAME{varr}.{sh}",
                                                                     "{varl}LOG_DIR{varr}")))
                                          .put(
                                                   "tail",
                                                   newStatementList(
                                                            call("default"),
                                                            interpret("tail {varl}LOG_DIR{varr}{fs}stdout.log{lf}")))
                                          .put(
                                                   "tailerr",
                                                   newStatementList(
                                                            call("default"),
                                                            interpret("tail {varl}LOG_DIR{varr}{fs}stderr.log{lf}")))
                                          .put(
                                                   "run",
                                                   newStatementList(
                                                            call("default"),
                                                            interpret("{varl}INSTANCE_HOME{varr}{fs}{varl}INSTANCE_NAME{varr}.{sh}{lf}")))
                                          .build()));
   }
}