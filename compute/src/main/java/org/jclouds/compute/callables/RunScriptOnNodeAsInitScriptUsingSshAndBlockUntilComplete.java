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

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.events.StatementOnNodeFailure;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.ssh.SshClient;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.eventbus.EventBus;
import com.google.inject.assistedinject.Assisted;

/**
 * 
 * @author Adrian Cole
 */
public class RunScriptOnNodeAsInitScriptUsingSshAndBlockUntilComplete extends RunScriptOnNodeAsInitScriptUsingSsh {
   protected final Timeouts timeouts;
   protected final BlockUntilInitScriptStatusIsZeroThenReturnOutput.Factory statusFactory;

   @Inject
   public RunScriptOnNodeAsInitScriptUsingSshAndBlockUntilComplete(
         BlockUntilInitScriptStatusIsZeroThenReturnOutput.Factory statusFactory, Timeouts timeouts,
         Function<NodeMetadata, SshClient> sshFactory, EventBus eventBus,
         InitScriptConfigurationForTasks initScriptConfiguration, @Assisted NodeMetadata node,
         @Assisted Statement script, @Assisted RunScriptOptions options) {
      super(sshFactory, eventBus, initScriptConfiguration, node, script, options);
      this.statusFactory = checkNotNull(statusFactory, "statusFactory");
      this.timeouts = checkNotNull(timeouts, "timeouts");
   }

   @Override
   public ExecResponse doCall() {
      try {
         return future().get(timeouts.scriptComplete, TimeUnit.MILLISECONDS);
      } catch (Throwable e) {
         eventBus.post(new StatementOnNodeFailure(init, node, e));
         throw Throwables.propagate(e);
      }
   }

   public BlockUntilInitScriptStatusIsZeroThenReturnOutput future() {
      ExecResponse returnVal = super.doCall();
      if (returnVal.getExitStatus() != 0) {
         IllegalStateException e = new IllegalStateException(String.format(
               "instance: %s on node: %s had non-zero exit status: %s", init.getInstanceName(), getNode().getId(),
               returnVal));
         eventBus.post(new StatementOnNodeFailure(init, node, e));
         throw e;
      }
      return statusFactory.create(this).init();
   }

   @Override
   public RunScriptOnNodeAsInitScriptUsingSshAndBlockUntilComplete init() {
      return RunScriptOnNodeAsInitScriptUsingSshAndBlockUntilComplete.class.cast(super.init());
   }
}
