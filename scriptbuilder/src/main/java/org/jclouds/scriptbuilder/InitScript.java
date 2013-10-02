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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.concat;
import static java.lang.String.format;
import static org.jclouds.scriptbuilder.ScriptBuilder.call;
import static org.jclouds.scriptbuilder.ScriptBuilder.findPid;
import static org.jclouds.scriptbuilder.ScriptBuilder.forget;
import static org.jclouds.scriptbuilder.domain.Statements.createRunScript;
import static org.jclouds.scriptbuilder.domain.Statements.interpret;
import static org.jclouds.scriptbuilder.domain.Statements.kill;
import static org.jclouds.scriptbuilder.domain.Statements.newStatementList;
import static org.jclouds.scriptbuilder.domain.Statements.switchArg;

import java.util.Map;

import org.jclouds.scriptbuilder.domain.AcceptsStatementVisitor;
import org.jclouds.scriptbuilder.domain.CreateRunScript;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementList;
import org.jclouds.scriptbuilder.domain.StatementVisitor;

import com.google.common.base.Objects;
import com.google.common.collect.ForwardingObject;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Creates an init script file
 * 
 * @author Adrian Cole
 */
public class InitScript extends ForwardingObject implements Statement, AcceptsStatementVisitor {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      protected String instanceName;
      protected String instanceHome = "{tmp}{fs}{varl}INSTANCE_NAME{varr}";
      protected String logDir = "{varl}INSTANCE_HOME{varr}";
      protected Map<String, String> exports = ImmutableMap.of();
      protected StatementList init = new StatementList();
      protected StatementList run = new StatementList();

      /**
       * @see InitScript#getInstanceName()
       */
      public Builder name(String instanceName) {
         this.instanceName = checkNotNull(instanceName, "INSTANCE_NAME");
         return this;
      }

      /**
       * @see InitScript#getInstanceHome()
       */
      public Builder home(String instanceHome) {
         this.instanceHome = checkNotNull(instanceHome, "INSTANCE_HOME");
         return this;
      }

      /**
       * @see InitScript#getLogDir()
       */
      public Builder logDir(String logDir) {
         this.logDir = checkNotNull(logDir, "LOG_DIR");
         return this;
      }

      /**
       * @param exports keys are the variables to export in UPPER_UNDERSCORE case format
       * @see InitScript#getExportedVariables()
       */
      public Builder exportVariables(Map<String, String> exports) {
         this.exports = ImmutableMap.copyOf(checkNotNull(exports, "exports"));
         return this;
      }

      /**
       * @see InitScript#getRun()
       */
      public Builder run(Statement run) {
         this.run = new StatementList(checkNotNull(run, "run"));
         return this;
      }

      /**
       * @see InitScript#getRun()
       */
      public Builder run(Statement... run) {
         this.run = new StatementList(checkNotNull(run, "run"));
         return this;
      }

      /**
       * @see InitScript#getRun()
       */
      public Builder run(Iterable<Statement> run) {
         this.run = new StatementList(checkNotNull(run, "run"));
         return this;
      }

      /**
       * @see InitScript#getRun()
       */
      public Builder run(StatementList run) {
         this.run = checkNotNull(run, "run");
         return this;
      }

      /**
       * @see InitScript#getInit()
       */
      public Builder init(Statement init) {
         this.init = new StatementList(checkNotNull(init, "init"));
         return this;
      }

      /**
       * @see InitScript#getInit()
       */
      public Builder init(Statement... init) {
         this.init = new StatementList(checkNotNull(init, "init"));
         return this;
      }

      /**
       * @see InitScript#getInit()
       */
      public Builder init(Iterable<Statement> init) {
         this.init = new StatementList(checkNotNull(init, "init"));
         return this;
      }

      /**
       * @see InitScript#getInit()
       */
      public Builder init(StatementList init) {
         this.init = checkNotNull(init, "init");
         return this;
      }

      public InitScript build() {
         return new InitScript(instanceName, instanceHome, logDir, exports, init, run);
      }

   }

   protected final String instanceName;
   protected final String instanceHome;
   protected final String logDir;
   protected final Map<String, String> exports;
   protected final StatementList init;
   protected final StatementList run;
   protected final ScriptBuilder delegate;

   /**
    * @param exports keys are the variables to export in UPPER_UNDERSCORE case format
    */
   protected InitScript(String instanceName, String instanceHome, String logDir, Map<String, String> exports,
         StatementList init, StatementList run) {
      this.instanceName = checkNotNull(instanceName, "INSTANCE_NAME");
      this.instanceHome = checkNotNull(instanceHome, "INSTANCE_HOME");
      this.logDir = checkNotNull(logDir, "LOG_DIR");
      this.exports = ImmutableMap.<String, String> copyOf(checkNotNull(exports, "exports"));
      this.init = checkNotNull(init, "init");
      this.run = checkNotNull(run, "run");
      checkArgument(run.delegate().size() > 0, "you must specify at least one statement to run");
      this.delegate = makeInitScriptStatement(instanceName, instanceHome, logDir, exports, init, run);
   }

   /**
    * 
    * @param exports keys are the variables to export in UPPER_UNDERSCORE case format
    */
   public static ScriptBuilder makeInitScriptStatement(String instanceName, String instanceHome, String logDir,
         Map<String, String> exports, StatementList init, StatementList run) {
      Map<String, String> defaultExports = ImmutableMap.of("INSTANCE_NAME", instanceName, "INSTANCE_HOME", instanceHome,
            "LOG_DIR", logDir);
      String exitStatusFile = format("%s/rc", logDir);
      run = new StatementList(ImmutableList.<Statement> builder().add(interpret("rm -f " + exitStatusFile))
            .add(interpret(format("trap 'echo $?>%s' 0 1 2 3 15", exitStatusFile))).addAll(run.delegate()).build());

      CreateRunScript createRunScript = createRunScript(instanceName,
            concat(exports.keySet(), defaultExports.keySet()), "{varl}INSTANCE_HOME{varr}", run);

      return new ScriptBuilder()
            .addEnvironmentVariableScope("default", defaultExports)
            .addEnvironmentVariableScope(instanceName, exports)
            .addStatement(
                  switchArg(
                        1,
                        new ImmutableMap.Builder<String, Statement>()
                              .put("init", newStatementList(call("default"), call(instanceName), init, createRunScript))
                              .put("status",
                                    newStatementList(call("default"), findPid("{varl}INSTANCE_NAME{varr}"),
                                          interpret("echo {varl}FOUND_PID{varr}{lf}")))
                              .put("stop",
                                    newStatementList(call("default"), findPid("{varl}INSTANCE_NAME{varr}"), kill()))
                              .put("start",
                                    newStatementList(
                                          call("default"),
                                          forget("{varl}INSTANCE_NAME{varr}",
                                                "{varl}INSTANCE_HOME{varr}{fs}{varl}INSTANCE_NAME{varr}.{sh}",
                                                "{varl}LOG_DIR{varr}")))
                              .put("stdout",
                                    newStatementList(call("default"),
                                          interpret("cat {varl}LOG_DIR{varr}{fs}stdout.log{lf}")))
                              .put("stderr",
                                    newStatementList(call("default"),
                                          interpret("cat {varl}LOG_DIR{varr}{fs}stderr.log{lf}")))
                              .put("exitstatus",
                                    newStatementList(call("default"),
                                          interpret("[ -f $LOG_DIR/rc ] && cat $LOG_DIR/rc")))
                              .put("tail",
                                    newStatementList(call("default"),
                                          interpret("tail {varl}LOG_DIR{varr}{fs}stdout.log{lf}")))
                              .put("tailerr",
                                    newStatementList(call("default"),
                                          interpret("tail {varl}LOG_DIR{varr}{fs}stderr.log{lf}")))
                              .put("run",
                                    newStatementList(call("default"),
                                          interpret("{varl}INSTANCE_HOME{varr}{fs}{varl}INSTANCE_NAME{varr}.{sh}{lf}")))
                              .build()));
   }

   /**
    * 
    * @return what will be bound to the INSTANCE_NAME variable, and uniquely
    *         identifies the process
    */
   public String getInstanceName() {
      return instanceName;
   }

   /**
    * default {@code /tmp/$INSTANCE_NAME} 
    * <br/>
    * <h3>note</h3> The parent directory
    * should be set with unix sticky-bit or otherwise made available to all
    * users. Otherwise, new instances by other users may fail due to not being
    * able to create a directory.  This is why the default is set to {@code /tmp}
    * 
    * @return what will be bound to the INSTANCE_HOME variable, and represents
    *         the working directory of the instance
    */
   public String getInstanceHome() {
      return instanceHome;
   }

   /**
    * default {@code $INSTANCE_HOME}
    * 
    * @return what will be bound to the LOG_DIR variable, and represents where
    *         stdout and stderr.logs are written.
    */
   public String getLogDir() {
      return logDir;
   }

   /**
    * 
    * @return statements that will be executed upon the init command
    */
   public StatementList getInitStatement() {
      return init;
   }

   /**
    * 
    * @return statements that will be executed upon the run or start commands
    */
   public StatementList getRunStatement() {
      return init;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(instanceName);
   }

   @Override
   public boolean equals(Object that) {
      if (that == null)
         return false;
      return equal(this.toString(), that.toString());
   }

   @Override
   public String toString() {
      return toStringHelper(this).add("INSTANCE_NAME", instanceName).toString();
   }

   @Override
   public void accept(StatementVisitor visitor) {
      delegate().accept(visitor);
   }

   @Override
   public Iterable<String> functionDependencies(OsFamily family) {
      return delegate().functionDependencies(family);
   }

   @Override
   public String render(OsFamily family) {
      return delegate().render(family);
   }

   @Override
   protected ScriptBuilder delegate() {
      return delegate;
   }

}
