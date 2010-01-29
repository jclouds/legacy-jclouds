/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import org.jclouds.logging.Logger;
import org.jclouds.ssh.ExecResponse;
import org.jclouds.ssh.SshClient;

import com.google.common.base.Predicate;

/**
 * 
 * Tests to if the runscript is still running
 * 
 * @author Adrian Cole
 */
@Singleton
public class RunScriptRunning implements Predicate<SshClient> {

   @Resource
   protected Logger logger = Logger.NULL;

   public boolean apply(SshClient ssh) {
      logger.trace("looking for runscript state on %s@%s", ssh.getUsername(), ssh.getHostAddress());
      ExecResponse response = refresh(ssh);
      while (response.getExitCode() == -1)
         response = refresh(ssh);
      logger.trace("%s@%s: looking for exit code 0: currently: %s", ssh.getUsername(), ssh
               .getHostAddress(), response.getExitCode());
      return 0 == response.getExitCode();

   }

   private ExecResponse refresh(SshClient ssh) {
      return ssh.exec("./runscript.sh status");
   }
}
