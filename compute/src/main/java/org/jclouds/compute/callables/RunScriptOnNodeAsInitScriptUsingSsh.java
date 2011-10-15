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

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * 
 * @author Adrian Cole
 */
public class RunScriptOnNodeAsInitScriptUsingSsh extends SudoAwareInitManager implements RunScriptOnNode {
   public static final String PROPERTY_INIT_SCRIPT_PATTERN = "jclouds.compute.init-script-pattern";
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final String initFile;

   /**
    * 
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
      super(sshFactory, options.shouldRunAsRoot(), checkNotNull(node, "node"), init(script, options.getTaskName()));
      this.initFile = String.format(initScriptPattern, options.getTaskName());
   }

   private static InitBuilder init(Statement script, String name) {
      if (name == null) {
         if (checkNotNull(script, "script") instanceof InitBuilder)
            name = InitBuilder.class.cast(script).getInstanceName();
         else
            name = "jclouds-script-" + System.currentTimeMillis();
      }
      return checkNotNull(script, "script") instanceof InitBuilder ? InitBuilder.class.cast(script) : createInitScript(
               name, script);
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
         return doCall();
      } finally {
         if (ssh != null)
            ssh.disconnect();
      }
   }

   public static InitBuilder createInitScript(String name, Statement script) {
      String path = "/tmp/" + name;
      return new InitBuilder(name, path, path, Collections.<String, String> emptyMap(), Collections.singleton(script));
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
         if (logger.isTraceEnabled())
            logger.warn(e, "<< (%s) problem using sftp [%s], attempting via sshexec", ssh.toString(), e.getMessage());
         else
            logger.warn("<< (%s) problem using sftp [%s], attempting via sshexec", ssh.toString(), e.getMessage());
         ssh.disconnect();
         ssh.connect();
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

}