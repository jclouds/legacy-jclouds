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

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.predicates.ScriptStatusReturnsZero.CommandUsingClient;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.ssh.SshClient;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.assistedinject.Assisted;

/**
 * 
 * @author Adrian Cole
 */
public class RunScriptOnNodeAsInitScriptUsingSshAndBlockUntilComplete extends RunScriptOnNodeAsInitScriptUsingSsh {
   protected final Predicate<CommandUsingClient> runScriptNotRunning;

   @Inject
   public RunScriptOnNodeAsInitScriptUsingSshAndBlockUntilComplete(
            @Named("SCRIPT_COMPLETE") Predicate<CommandUsingClient> runScriptNotRunning,
            Function<NodeMetadata, SshClient> sshFactory, @Assisted NodeMetadata node, @Assisted Statement script,
            @Assisted RunScriptOptions options) {
      super(sshFactory, node, script, options);
      this.runScriptNotRunning = checkNotNull(runScriptNotRunning, "runScriptNotRunning");
   }

   @Override
   public ExecResponse doCall() {
      ExecResponse returnVal = super.doCall();
      boolean complete = runScriptNotRunning.apply(new CommandUsingClient("./" + name + " status", ssh));
      logger.debug("<< complete(%s)", complete);
      if (logger.isDebugEnabled() || returnVal.getExitCode() != 0) {
         logger.debug("<< stdout from %s as %s@%s\n%s", name, node.getCredentials().identity, Iterables.get(node
                  .getPublicAddresses(), 0), ssh.exec("./" + name + " tail").getOutput());
         logger.debug("<< stderr from %s as %s@%s\n%s", name, node.getCredentials().identity, Iterables.get(node
                  .getPublicAddresses(), 0), ssh.exec("./" + name + " tailerr").getOutput());
      }
      return returnVal;
   }
}