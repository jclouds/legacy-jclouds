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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.scriptbuilder.domain.Statements.call;
import static org.jclouds.scriptbuilder.domain.Statements.createRunScript;
import static org.jclouds.scriptbuilder.domain.Statements.findPid;
import static org.jclouds.scriptbuilder.domain.Statements.forget;
import static org.jclouds.scriptbuilder.domain.Statements.interpret;
import static org.jclouds.scriptbuilder.domain.Statements.kill;
import static org.jclouds.scriptbuilder.domain.Statements.newStatementList;
import static org.jclouds.scriptbuilder.domain.Statements.switchArg;

import java.util.Map;

import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementList;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Creates an init script file
 * 
 * @author Adrian Cole
 */
public class InitBuilder extends ScriptBuilder {

   private final String instanceName;
   private final String instanceHome;
   private final String logDir;

   public InitBuilder(String instanceName, String instanceHome, String logDir, Map<String, String> variables,
            Iterable<Statement> statements) {
      this(instanceName, instanceHome, logDir, variables, ImmutableSet.<Statement>of(), statements);
   }

   @SuppressWarnings("unchecked")
   public InitBuilder(String instanceName, String instanceHome, String logDir, Map<String, String> variables,
            Iterable<Statement> initStatements, Iterable<Statement> statements) {
      this.instanceName = checkNotNull(instanceName, "instanceName");
      this.instanceHome = checkNotNull(instanceHome, "instanceHome");
      this.logDir = checkNotNull(logDir, "logDir");
      Map<String, String> defaultVariables = ImmutableMap.of("instanceName", instanceName, "instanceHome",
               instanceHome, "logDir", logDir);
      addEnvironmentVariableScope("default", defaultVariables)
               .addEnvironmentVariableScope(instanceName, variables)
               .addStatement(
                        switchArg(
                                 1,
                                 new ImmutableMap.Builder()
                                          .put(
                                                   "init",
                                                   newStatementList(call("default"), call(instanceName),
                                                            new StatementList(initStatements), createRunScript(
                                                                     instanceName,// TODO: convert
                                                                     // so
                                                                     // that
                                                                     // createRunScript
                                                                     // can take from a
                                                                     // variable
                                                                     Iterables.concat(variables.keySet(),
                                                                              defaultVariables.keySet()),
                                                                     "{varl}INSTANCE_HOME{varr}", statements)))
                                          .put(
                                                   "status",
                                                   newStatementList(call("default"),
                                                            findPid("{varl}INSTANCE_NAME{varr}"),
                                                            interpret("echo [{varl}FOUND_PID{varr}]{lf}")))
                                          .put(
                                                   "stop",
                                                   newStatementList(call("default"),
                                                            findPid("{varl}INSTANCE_NAME{varr}"), kill()))
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
                                                   newStatementList(call("default"),
                                                            interpret("tail {varl}LOG_DIR{varr}{fs}stdout.log{lf}")))
                                          .put(
                                                   "tailerr",
                                                   newStatementList(call("default"),
                                                            interpret("tail {varl}LOG_DIR{varr}{fs}stderr.log{lf}")))
                                          .put(
                                                   "run",
                                                   newStatementList(
                                                            call("default"),
                                                            interpret("{varl}INSTANCE_HOME{varr}{fs}{varl}INSTANCE_NAME{varr}.{sh}{lf}")))
                                          .build()));
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((instanceHome == null) ? 0 : instanceHome.hashCode());
      result = prime * result + ((instanceName == null) ? 0 : instanceName.hashCode());
      result = prime * result + ((logDir == null) ? 0 : logDir.hashCode());
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
      InitBuilder other = (InitBuilder) obj;
      if (instanceHome == null) {
         if (other.instanceHome != null)
            return false;
      } else if (!instanceHome.equals(other.instanceHome))
         return false;
      if (instanceName == null) {
         if (other.instanceName != null)
            return false;
      } else if (!instanceName.equals(other.instanceName))
         return false;
      if (logDir == null) {
         if (other.logDir != null)
            return false;
      } else if (!logDir.equals(other.logDir))
         return false;
      return true;
   }

   public String getInstanceName() {
      return instanceName;
   }

   public String getInstanceHome() {
      return instanceHome;
   }

   public String getLogDir() {
      return logDir;
   }

   @Override
   public String toString() {
      return "[instanceName=" + instanceName + ", instanceHome=" + instanceHome + ", logDir=" + logDir + "]";
   }
}