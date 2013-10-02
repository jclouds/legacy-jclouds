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
import org.jclouds.compute.events.StatementOnNodeCompletion;
import org.jclouds.compute.events.StatementOnNodeFailure;
import org.jclouds.compute.events.StatementOnNodeSubmission;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.ssh.SshClient;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import com.google.common.eventbus.EventBus;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * 
 * @author Adrian Cole
 */
public class RunScriptOnNodeUsingSsh implements RunScriptOnNode {
   public static final String MARKER = "RUN_SCRIPT_AS_ROOT_SSH";

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final Function<NodeMetadata, SshClient> sshFactory;
   protected final EventBus eventBus;
   protected final NodeMetadata node;
   protected final Statement statement;
   protected final boolean runAsRoot;

   protected SshClient ssh;

   @AssistedInject
   public RunScriptOnNodeUsingSsh(Function<NodeMetadata, SshClient> sshFactory, EventBus eventBus,
         @Assisted NodeMetadata node, @Assisted Statement statement, @Assisted RunScriptOptions options) {
      this.sshFactory = checkNotNull(sshFactory, "sshFactory");
      this.eventBus = checkNotNull(eventBus, "eventBus");
      this.node = checkNotNull(node, "node");
      this.statement = checkNotNull(statement, "statement");
      this.runAsRoot = options.shouldRunAsRoot();
   }

   @Override
   public ExecResponse call() {
      checkState(ssh != null, "please call init() before invoking call");
      try {
         ssh.connect();
         ExecResponse returnVal;
         eventBus.post(new StatementOnNodeSubmission(statement, node));
         String command = runAsRoot ? execAsRoot(statement.render(OsFamily.UNIX)) : execScriptAsDefaultUser(statement
               .render(OsFamily.UNIX));
         try {
            returnVal = runCommand(command);
         } catch (Throwable e) {
            eventBus.post(new StatementOnNodeFailure(statement, node, e));
            throw Throwables.propagate(e);
         }
         eventBus.post(new StatementOnNodeCompletion(statement, node, returnVal));
         if (logger.isTraceEnabled())
            logger.trace("<< %s[%s]", statement, returnVal);
         else
            logger.debug("<< %s(%d)", statement, returnVal.getExitStatus());
         return returnVal;
      } finally {
         if (ssh != null)
            ssh.disconnect();
      }
   }

   @Override
   public RunScriptOnNode init() {
      ssh = sshFactory.apply(node);
      return this;
   }

   protected ExecResponse runCommand(String command) {
      ExecResponse returnVal;
      logger.debug(">> running [%s] as %s@%s", command.replace(node.getCredentials().getPassword() != null ? node
            .getCredentials().getPassword() : "XXXXX", "XXXXX"), ssh.getUsername(), ssh.getHostAddress());
      returnVal = ssh.exec(command);
      return returnVal;
   }

   @VisibleForTesting
   public String execAsRoot(String command) {
      if (node.getCredentials().identity.equals("root")) {
      } else if (node.getCredentials().shouldAuthenticateSudo()) {
         command = String.format("sudo -S sh <<'%s'\n%s\n%s%s\n", MARKER, node.getCredentials().getPassword(), command, MARKER);
      } else {
         command = String.format("sudo sh <<'%s'\n%s%s\n", MARKER, command, MARKER);
      }
      return command;
   }

   protected String execScriptAsDefaultUser(String command) {
      return command;
   }

   public NodeMetadata getNode() {
      return node;
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("node", node).add("name", statement).add("runAsRoot", runAsRoot)
            .toString();
   }

   @Override
   public Statement getStatement() {
      return statement;
   }

}
