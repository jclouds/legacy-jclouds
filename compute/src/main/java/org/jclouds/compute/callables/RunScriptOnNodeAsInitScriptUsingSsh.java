/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.compute.callables;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Collections;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.scriptbuilder.InitBuilder;
import org.jclouds.scriptbuilder.domain.AdminAccessVisitor;
import org.jclouds.scriptbuilder.domain.AppendFile;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.scriptbuilder.statements.login.AdminAccess;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshException;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * 
 * @author Adrian Cole
 */
public class RunScriptOnNodeAsInitScriptUsingSsh implements RunScriptOnNode {
   public static final String PROPERTY_INIT_SCRIPT_PATTERN = "jclouds.compute.init-script-pattern";
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final Function<NodeMetadata, SshClient> sshFactory;
   protected NodeMetadata node;
   protected final InitBuilder init;
   protected final boolean runAsRoot;
   protected final String initFile;

   protected SshClient ssh;

   /**
    * determines the naming convention of init scripts.
    * 
    * ex. {@code /tmp/init-%s}
    */
   @Inject(optional = true)
   @Named(PROPERTY_INIT_SCRIPT_PATTERN)
   private String initScriptPattern = "/tmp/init-%s";

   @AssistedInject
   public RunScriptOnNodeAsInitScriptUsingSsh(Function<NodeMetadata, SshClient> sshFactory,
            @Assisted NodeMetadata node, @Assisted Statement script, @Assisted RunScriptOptions options) {
      this.sshFactory = checkNotNull(sshFactory, "sshFactory");
      this.node = checkNotNull(node, "node");
      String name = options.getTaskName();
      if (name == null) {
         if (checkNotNull(script, "script") instanceof InitBuilder)
            name = InitBuilder.class.cast(script).getInstanceName();
         else
            name = "jclouds-script-" + System.currentTimeMillis();
      }
      this.init = checkNotNull(script, "script") instanceof InitBuilder ? InitBuilder.class.cast(script)
               : createInitScript(name, script);
      this.initFile = String.format(initScriptPattern, name);
      this.runAsRoot = options.shouldRunAsRoot();
   }

   public static InitBuilder createInitScript(String name, Statement script) {
      String path = "/tmp/" + name;
      return new InitBuilder(name, path, path, Collections.<String, String> emptyMap(), Collections.singleton(script));
   }

   @Override
   public ExecResponse call() {
      checkState(ssh != null, "please call init() before invoking call");
      try {
         ssh.connect();
         return doCall();
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

   public void refreshSshIfNewAdminCredentialsConfigured(AdminAccess input) {
      if (input.getAdminCredentials() != null && input.shouldGrantSudoToAdminUser()) {
         ssh.disconnect();
         logger.debug(">> reconnecting as %s@%s", input.getAdminCredentials().identity, ssh.getHostAddress());
         ssh = sshFactory.apply(node = NodeMetadataBuilder.fromNodeMetadata(node).adminPassword(null).credentials(
                  input.getAdminCredentials()).build());
         ssh.connect();
         setupLinkToInitFile();
      }
   }

   /**
    * ssh client is initialized through this call.
    */
   protected ExecResponse doCall() {
      try {
         ssh.put(initFile, init.render(OsFamily.UNIX));
      } catch (SshException e) {
         // If there's a problem with the sftp configuration, we can try via ssh exec
         logger.warn(e, "<< (%s) problem using sftp [%s], attempting via sshexec", ssh.toString(), e.getMessage());
         ssh.exec("rm " + initFile);
         ssh.exec(Statements.appendFile(initFile, Splitter.on('\n').split(init.render(OsFamily.UNIX)),
                  AppendFile.MARKER + "_" + init.getInstanceName()).render(OsFamily.UNIX));
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
   }

   protected void setupLinkToInitFile() {
      ssh.exec(String.format("ln -fs %s %s", initFile, init.getInstanceName()));
   }

   protected ExecResponse runAction(String action) {
      ExecResponse returnVal;
      String command = (runAsRoot) ? execScriptAsRoot(action) : execScriptAsDefaultUser(action);
      returnVal = runCommand(command);
      if (logger.isTraceEnabled())
         logger.trace("<< %s[%s]", action, returnVal);
      else
         logger.debug("<< %s(%d)", action, returnVal.getExitCode());
      return returnVal;
   }

   protected ExecResponse runCommand(String command) {
      ExecResponse returnVal;
      logger.debug(">> running [%s] as %s@%s", command.replace(node.getAdminPassword() != null ? node
               .getAdminPassword() : "XXXXX", "XXXXX"), ssh.getUsername(), ssh.getHostAddress());
      returnVal = ssh.exec(command);
      return returnVal;
   }

   @VisibleForTesting
   public String execScriptAsRoot(String action) {
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

   @Override
   public Statement getStatement() {
      return init;
   }

}