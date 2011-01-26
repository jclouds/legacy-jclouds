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

package org.jclouds.compute.predicates;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.logging.Logger;
import org.jclouds.ssh.SshClient;

import com.google.common.base.Predicate;

/**
 * 
 * Tests to if the runscript is still running
 * 
 * @author Adrian Cole
 */
@Singleton
public class ScriptStatusReturnsZero implements
         Predicate<ScriptStatusReturnsZero.CommandUsingClient> {

   @Resource
   protected Logger logger = Logger.NULL;

   @Override
   public boolean apply(CommandUsingClient commandUsingClient) {
      logger.trace("looking for [%s] state on %s@%s", commandUsingClient.command,
               commandUsingClient.client.getUsername(), commandUsingClient.client.getHostAddress());
      ExecResponse response = refresh(commandUsingClient);
      while (response.getExitCode() == -1)
         response = refresh(commandUsingClient);
      logger.trace("%s@%s: looking for exit code 0: currently: %s", commandUsingClient.client
               .getUsername(), commandUsingClient.client.getHostAddress(), response.getExitCode());
      return 0 == response.getExitCode();
   }

   private ExecResponse refresh(CommandUsingClient commandUsingClient) {
      return commandUsingClient.client.exec(commandUsingClient.command);
   }

   public static class CommandUsingClient {

      public CommandUsingClient(String command, SshClient client) {
         this.command = command;
         this.client = client;
      }

      private final String command;
      private final SshClient client;
   }
}
