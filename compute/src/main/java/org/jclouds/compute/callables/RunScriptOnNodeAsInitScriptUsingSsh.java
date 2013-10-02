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

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.events.InitScriptOnNodeSubmission;
import org.jclouds.compute.events.StatementOnNodeCompletion;
import org.jclouds.compute.events.StatementOnNodeFailure;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.scriptbuilder.InitScript;
import org.jclouds.scriptbuilder.domain.AdminAccessVisitor;
import org.jclouds.scriptbuilder.domain.AppendFile;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.scriptbuilder.statements.login.AdminAccess;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshException;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.eventbus.EventBus;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * 
 * @author Adrian Cole
 */
public class RunScriptOnNodeAsInitScriptUsingSsh extends SudoAwareInitManager implements RunScriptOnNode {

   protected final EventBus eventBus;

   @AssistedInject
   public RunScriptOnNodeAsInitScriptUsingSsh(Function<NodeMetadata, SshClient> sshFactory, EventBus eventBus,
            InitScriptConfigurationForTasks initScriptConfiguration, @Assisted NodeMetadata node,
            @Assisted Statement script, @Assisted RunScriptOptions options) {
      super(sshFactory, options.shouldRunAsRoot(), checkNotNull(node, "node"),
               initScriptConfiguration, checkNotNull(script, "script") instanceof InitScript ? InitScript.class.cast(script)
                        : createInitScript(checkNotNull(initScriptConfiguration, "initScriptConfiguration"), options
                                 .getTaskName(), script));
      this.eventBus = checkNotNull(eventBus, "eventBus");
   }

   @Override
   public RunScriptOnNodeAsInitScriptUsingSsh init() {
      super.init();
      return this;
   }

   @Override
   public ExecResponse call() {
      checkState(ssh != null, "please call init() before invoking call");
      try {
         ssh.connect();
         ExecResponse returnVal = doCall();
         eventBus.post(new StatementOnNodeCompletion(init, node, returnVal));
         return returnVal;
      } finally {
         if (ssh != null)
            ssh.disconnect();
      }
   }

   public static InitScript createInitScript(InitScriptConfigurationForTasks config, String name, Statement script) {
      if (name == null) {
         name = "jclouds-script-" + config.getAnonymousTaskSuffixSupplier().get();
      }
      return InitScript.builder().name(name).home(config.getBasedir() + "/" + name).run(script).build();
   }

   protected void refreshSshIfNewAdminCredentialsConfigured(AdminAccess input) {
      if (input.getAdminCredentials() != null && input.shouldGrantSudoToAdminUser()) {
         ssh.disconnect();
         logger.debug(">> reconnecting as %s@%s", input.getAdminCredentials().identity, ssh.getHostAddress());
         ssh = sshFactory.apply(node = NodeMetadataBuilder.fromNodeMetadata(node)
               .credentials(LoginCredentials.fromCredentials(input.getAdminCredentials())).build());
         ssh.connect();
         setupLinkToInitFile();
      }
   }

   protected ExecResponse doCall() {
      eventBus.post(new InitScriptOnNodeSubmission(init, node));
      try {
         try {
            ssh.put(initFile, init.render(OsFamily.UNIX));
         } catch (SshException e) {
            // If there's a problem with the sftp configuration, we can try via
            // ssh exec
            if (logger.isTraceEnabled())
               logger.warn(e, "<< (%s) problem using sftp [%s], attempting via sshexec", ssh.toString(), e.getMessage());
            else
               logger.warn("<< (%s) problem using sftp [%s], attempting via sshexec", ssh.toString(), e.getMessage());
            ssh.disconnect();
            ssh.connect();
            ssh.exec("rm " + initFile);
            ssh.exec(Statements.appendFile(initFile, Splitter.on('\n').split(init.render(OsFamily.UNIX)),
                  AppendFile.DELIMITER + "_" + init.getInstanceName()).render(OsFamily.UNIX));
         }

         ssh.exec("chmod 755 " + initFile);
         setupLinkToInitFile();

         runAction("init");
         init.getInitStatement().accept(new AdminAccessVisitor() {

            @Override
            public void visit(AdminAccess input) {
               refreshSshIfNewAdminCredentialsConfigured(input);
            }

         });
         return runAction("start");
      } catch (Throwable e) {
         eventBus.post(new StatementOnNodeFailure(init, node, e));
         throw Throwables.propagate(e);
      }
   }

   protected void setupLinkToInitFile() {
      ssh.exec(String.format("ln -fs %s %s", initFile, init.getInstanceName()));
   }

}
