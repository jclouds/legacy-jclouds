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

package org.jclouds.compute.callables;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.concurrent.Callable;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.scriptbuilder.InitBuilder;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.ssh.ExecResponse;
import org.jclouds.ssh.SshClient;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.inject.assistedinject.Assisted;

/**
 * 
 * @author Adrian Cole
 */
public class StartInitScriptOnNode implements Callable<ExecResponse> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   public interface Factory {
      @Named("blocking")
      StartInitScriptOnNode blockOnComplete(NodeMetadata node, @Nullable String name, Statement script,
               boolean runAsRoot);

      @Named("nonblocking")
      StartInitScriptOnNode dontBlockOnComplete(NodeMetadata node, @Nullable String name, Statement script,
               boolean runAsRoot);
   }

   protected final Function<NodeMetadata, SshClient> sshFactory;
   protected final NodeMetadata node;
   protected final Statement init;
   protected final String name;
   protected final boolean runAsRoot;

   protected SshClient ssh;

   @Inject
   public StartInitScriptOnNode(Function<NodeMetadata, SshClient> sshFactory, @Assisted NodeMetadata node,
            @Assisted @Nullable String name, @Assisted Statement script, @Assisted boolean runAsRoot) {
      this.sshFactory = checkNotNull(sshFactory, "sshFactory");
      this.node = checkNotNull(node, "node");
      if (name == null) {
         if (checkNotNull(script, "script") instanceof InitBuilder)
            name = InitBuilder.class.cast(script).getInstanceName();
         else
            name = "jclouds-script-" + System.currentTimeMillis();
      }
      this.init = checkNotNull(script, "script") instanceof InitBuilder ? InitBuilder.class.cast(script)
               : createInitScript(checkNotNull(name, "name"), script);
      this.name = checkNotNull(name, "name");
      this.runAsRoot = runAsRoot;
   }

   public static InitBuilder createInitScript(String name, Statement script) {
      String path = "/tmp/" + name;
      return new InitBuilder(name, path, path, Collections.<String, String> emptyMap(), Collections.singleton(script));
   }

   @Override
   public ExecResponse call() {
      ssh = sshFactory.apply(node);
      try {
         ssh.connect();
         return doCall();
      } finally {
         if (ssh != null)
            ssh.disconnect();
      }

   }

   /**
    * ssh client is initialized through this call.
    */
   protected ExecResponse doCall() {
      ssh.put(name, init.render(OsFamily.UNIX));
      ssh.exec("chmod 755 " + name);
      runAction("init");
      return runAction("start");
   }

   protected ExecResponse runAction(String action) {
      ExecResponse returnVal;
      String command = (runAsRoot) ? execScriptAsRoot(action) : execScriptAsDefaultUser(action);
      returnVal = runCommand(command);
      logger.debug("<< %s(%d)", action, returnVal.getExitCode());
      logger.trace("<< %s[%s]", action, returnVal);
      return returnVal;
   }

   protected ExecResponse runCommand(String command) {
      ExecResponse returnVal;
      logger.debug(">> running [%s] as %s@%s", command.replace(node.getAdminPassword() != null ? node
               .getAdminPassword() : "XXXXX", "XXXXX"), node.getCredentials().identity, Iterables.get(node
               .getPublicAddresses(), 0));
      returnVal = ssh.exec(command);
      return returnVal;
   }

   @VisibleForTesting
   public String execScriptAsRoot(String action) {
      String command;
      if (node.getCredentials().identity.equals("root")) {
         command = "./" + name + " " + action;
      } else if (node.getAdminPassword() != null) {
         command = String.format("echo '%s'|sudo -S ./%s %s", node.getAdminPassword(), name, action);
      } else {
         command = "sudo ./" + name + " " + action;
      }
      return command;
   }

   protected String execScriptAsDefaultUser(String action) {
      return "./" + name + " " + action;
   }

   public NodeMetadata getNode() {
      return node;
   }
}