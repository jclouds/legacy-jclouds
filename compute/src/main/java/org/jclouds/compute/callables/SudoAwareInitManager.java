/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.compute.callables;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.scriptbuilder.InitBuilder;
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
   protected final InitBuilder init;
   protected final boolean runAsRoot;
   protected final Function<NodeMetadata, SshClient> sshFactory;
   protected SshClient ssh;

   public SudoAwareInitManager(Function<NodeMetadata, SshClient> sshFactory, boolean runAsRoot, NodeMetadata node,
            InitBuilder init) {
      this.sshFactory = checkNotNull(sshFactory, "sshFactory");
      this.runAsRoot = runAsRoot;
      this.node = checkNotNull(node, "node");
      this.init = checkNotNull(init, "init");
   }

   @PostConstruct
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
      if ("status".equals(action))
         logger.trace("<< %s(%d)", action, returnVal.getExitCode());
      else if (computeLogger.isTraceEnabled())
         computeLogger.trace("<< %s[%s]", action, returnVal);
      else
         computeLogger.debug("<< %s(%d)", action, returnVal.getExitCode());
      return returnVal;
   }

   ExecResponse runCommand(String command) {
      String statement = String.format(">> running [%s] as %s@%s", command.replace(
               node.getAdminPassword() != null ? node.getAdminPassword() : "XXXXX", "XXXXX"), ssh.getUsername(), ssh
               .getHostAddress());
      if (command.endsWith("status"))
         logger.trace(statement);
      else
         computeLogger.debug(statement);
      return ssh.exec(command);
   }

   @VisibleForTesting
   String execScriptAsRoot(String action) {
      String command;
      if (node.getCredentials().identity.equals("root")) {
         command = "./" + init.getInstanceName() + " " + action;
      } else if (node.getAdminPassword() != null) {
         command = String.format("echo '%s'|sudo -S ./%s %s", node.getAdminPassword(), init.getInstanceName(), action);
      } else {
         command = "sudo ./" + init.getInstanceName() + " " + action;
      }
      return command;
   }

   protected String execScriptAsDefaultUser(String action) {
      return "./" + init.getInstanceName() + " " + action;
   }

   public NodeMetadata getNode() {
      return node;
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("node", node).add("name", init.getInstanceName()).add("runAsRoot",
               runAsRoot).toString();
   }

   public InitBuilder getStatement() {
      return init;
   }
}