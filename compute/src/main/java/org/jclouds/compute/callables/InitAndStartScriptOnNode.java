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

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.util.ComputeServiceUtils.SshCallable;
import org.jclouds.logging.Logger;
import org.jclouds.scriptbuilder.InitBuilder;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.ssh.ExecResponse;
import org.jclouds.ssh.SshClient;
import org.jclouds.util.Utils;

import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
public class InitAndStartScriptOnNode implements SshCallable<ExecResponse> {
   protected SshClient ssh;
   protected final NodeMetadata node;
   protected final InitBuilder init;
   protected final boolean runAsRoot;
   protected Logger logger = Logger.NULL;

   public InitAndStartScriptOnNode(NodeMetadata node, String name, Statement script, boolean runAsRoot) {
      this.node = checkNotNull(node, "node");
      this.init = checkNotNull(script, "script") instanceof InitBuilder ? InitBuilder.class.cast(script)
               : createInitScript(checkNotNull(name, "name"), script);
      this.runAsRoot = runAsRoot;
   }

   public static InitBuilder createInitScript(String name, Statement script) {
      String path = "/tmp/" + name;
      return new InitBuilder(name, path, path, Collections.<String, String> emptyMap(), Collections.singleton(script));
   }

   @Override
   public ExecResponse call() {
      ssh.put(init.getInstanceName(), init.render(OsFamily.UNIX));
      ssh.exec("chmod 755 " + init.getInstanceName());
      runAction("init");
      return runAction("start");
   }

   private ExecResponse runAction(String action) {
      ExecResponse returnVal;
      String command = (runAsRoot) ? execScriptAsRoot(action) : execScriptAsDefaultUser(action);
      returnVal = runCommand(command);
      logger.debug("<< %s(%d)", action, returnVal.getExitCode());
      logger.trace("<< %s[%s]", action, returnVal);
      return returnVal;
   }

   protected ExecResponse runCommand(String command) {
      ExecResponse returnVal;
      logger.debug(">> running [%s] as %s@%s", command.replace(node.getCredentials().credential, "XXXXX"), node
               .getCredentials().identity, Iterables.get(node.getPublicAddresses(), 0));
      returnVal = ssh.exec(command);
      return returnVal;
   }

   @Override
   public void setConnection(SshClient ssh, Logger logger) {
      this.logger = checkNotNull(logger, "logger");
      this.ssh = checkNotNull(ssh, "ssh");
   }

   protected String execScriptAsRoot(String action) {
      String command;
      if (node.getCredentials().identity.equals("root")) {
         command = "./" + init.getInstanceName() + " " + action;
      } else if (Utils.isPrivateKeyCredential(node.getCredentials())) {
         command = "sudo ./" + init.getInstanceName() + " " + action;
      } else {
         command = String.format("echo '%s'|sudo -S ./%s %s", node.getCredentials().credential, init.getInstanceName(),
                  action);
      }
      return command;
   }

   protected String execScriptAsDefaultUser(String action) {
      return "./" + init.getInstanceName() + " " + action;
   }

   @Override
   public NodeMetadata getNode() {
      return node;
   }
}