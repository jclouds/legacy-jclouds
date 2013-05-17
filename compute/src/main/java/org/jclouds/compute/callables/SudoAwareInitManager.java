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
package org.jclouds.compute.callables;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.scriptbuilder.InitScript;
import org.jclouds.ssh.SshClient;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
public class SudoAwareInitManager {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger computeLogger = Logger.NULL;
   protected Logger logger = Logger.NULL;
   protected NodeMetadata node;
   protected final String initFile;
   protected final InitScript init;
   protected final boolean runAsRoot;
   protected final Function<NodeMetadata, SshClient> sshFactory;
   protected SshClient ssh;

   /**
    * @return the absolute path to the file on disk relating to this task.
    */
   public String getInitFile() {
      return initFile;
   }
   
   public SudoAwareInitManager(Function<NodeMetadata, SshClient> sshFactory, boolean runAsRoot, NodeMetadata node,
         InitScriptConfigurationForTasks initScriptConfiguration, InitScript init) {
      this.sshFactory = checkNotNull(sshFactory, "sshFactory");
      this.runAsRoot = runAsRoot;
      this.node = checkNotNull(node, "node");
      this.initFile = String.format(initScriptConfiguration.getInitScriptPattern(), init.getInstanceName());
      this.init = checkNotNull(init, "init");
   }

   public SudoAwareInitManager init() {
      ssh = sshFactory.apply(node);
      return this;
   }

   public ExecResponse refreshAndRunAction(String action) {
      checkState(ssh != null, "please call init() before invoking call");
      try {
         ssh.connect();
         return runAction(action);
      } finally {
         if (ssh != null)
            ssh.disconnect();
      }
   }

   public ExecResponse runAction(String action) {
      ExecResponse returnVal;
      String command = (runAsRoot && Predicates.in(ImmutableSet.of("start", "stop", "run")).apply(action)) ? execScriptAsRoot(action)
            : execScriptAsDefaultUser(action);
      returnVal = runCommand(command);
      if (ImmutableSet.of("status", "stdout", "stderr").contains(action))
         logger.trace("<< %s(%d)", action, returnVal.getExitStatus());
      else if (computeLogger.isTraceEnabled())
         computeLogger.trace("<< %s[%s]", action, returnVal);
      else
         computeLogger.debug("<< %s(%d)", action, returnVal.getExitStatus());
      return returnVal;
   }

   ExecResponse runCommand(String command) {
      String statement = String.format("[%s] as %s@%s", command.replace(
            node.getCredentials().getPassword() != null ? node.getCredentials().getPassword() : "XXXXX", "XXXXX"), ssh
            .getUsername(), ssh.getHostAddress());
      if (command.endsWith("status") || command.endsWith("stdout") || command.endsWith("stderr"))
         logger.trace(">> running %s", statement);
      else 
         computeLogger.debug(">> running " + statement);
      ExecResponse returnVal = ssh.exec(command);
      if (!command.endsWith("status"))
         checkState(returnVal.getExitStatus() == 0, "error running %s; returnVal !=0: %s", statement, returnVal);
      return returnVal;
   }

   @VisibleForTesting
   String execScriptAsRoot(String action) {
      String command;
      if (node.getCredentials().identity.equals("root")) {
         command = initFile + " " + action;
      } else if (node.getCredentials().shouldAuthenticateSudo()) {
         command = String.format("echo '%s'|sudo -S %s %s", node.getCredentials().getPassword(),
               initFile, action);
      } else {
         command = "sudo " + initFile + " " + action;
      }
      return command;
   }

   protected String execScriptAsDefaultUser(String action) {
      return initFile + " " + action;
   }

   public NodeMetadata getNode() {
      return node;
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("node", node.getId()).add("name", init.getInstanceName())
            .add("runAsRoot", runAsRoot).toString();
   }

   public InitScript getStatement() {
      return init;
   }
}
