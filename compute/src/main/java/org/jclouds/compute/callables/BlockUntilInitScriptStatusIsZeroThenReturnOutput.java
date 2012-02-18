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

import java.util.Date;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;

import org.jclouds.Constants;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.events.StatementOnNodeCompletion;
import org.jclouds.compute.events.StatementOnNodeFailure;
import org.jclouds.compute.predicates.ScriptStatusReturnsZero;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.scriptbuilder.InitScript;
import org.jclouds.ssh.SshClient;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.eventbus.EventBus;
import com.google.common.primitives.Ints;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

/**
 * A future that works in tandem with a task that was invoked by
 * {@link InitScript}
 * 
 * @author Adrian Cole
 */
public class BlockUntilInitScriptStatusIsZeroThenReturnOutput extends AbstractFuture<ExecResponse> {

   public static interface Factory {
      BlockUntilInitScriptStatusIsZeroThenReturnOutput create(SudoAwareInitManager commandRunner);
   }

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final ExecutorService userThreads;
   private final EventBus eventBus;
   private final SudoAwareInitManager commandRunner;

   public SudoAwareInitManager getCommandRunner() {
      return commandRunner;
   }

   private final RetryablePredicate<String> notRunningAnymore;

   @Inject
   public BlockUntilInitScriptStatusIsZeroThenReturnOutput(
         @Named(Constants.PROPERTY_USER_THREADS) ExecutorService userThreads, EventBus eventBus,
         ComputeServiceConstants.InitStatusProperties properties, final ScriptStatusReturnsZero stateRunning,
         @Assisted final SudoAwareInitManager commandRunner) {

      long retryMaxWait = TimeUnit.DAYS.toMillis(365); // arbitrarily high
                                                       // value, but
                                                       // Long.MAX_VALUE doesn't
                                                       // work!
      long retryInitialPeriod = properties.initStatusInitialPeriod;
      long retryMaxPeriod = properties.initStatusMaxPeriod;

      this.commandRunner = checkNotNull(commandRunner, "commandRunner");
      this.userThreads = checkNotNull(userThreads, "userThreads");
      this.eventBus = checkNotNull(eventBus, "eventBus");
      this.notRunningAnymore = new RetryablePredicate<String>(new Predicate<String>() {

         @Override
         public boolean apply(String arg0) {
            return commandRunner.runAction(arg0).getOutput().trim().equals("");
         }
      }, retryMaxWait, retryInitialPeriod, retryMaxPeriod, TimeUnit.MILLISECONDS) {
         /**
          * make sure we stop the retry loop if someone cancelled the future,
          * this keeps threads from being consumed on dead tasks
          */
         @Override
         protected boolean atOrAfter(Date end) {
            if (isCancelled())
               Throwables.propagate(new CancellationException("cancelled"));
            return super.atOrAfter(end);
         }
      };
   }

   /**
    * in case login credentials or user changes at runtime.
    */
   public void setSshClient(SshClient client) {

   }

   /**
    * Submits a thread that will either set the result of the future or the
    * exception that took place
    */
   public BlockUntilInitScriptStatusIsZeroThenReturnOutput init() {
      userThreads.submit(new Runnable() {
         @Override
         public void run() {
            try {
               notRunningAnymore.apply("status");
               String stdout = commandRunner.runAction("stdout").getOutput();
               String stderr = commandRunner.runAction("stderr").getOutput();
               Integer exitStatus = Ints.tryParse(commandRunner.runAction("exitstatus").getOutput().trim());
               ExecResponse exec = new ExecResponse(stdout, stderr, exitStatus == null ? -1 : exitStatus);
               if (exitStatus == null) {
                  Integer pid = Ints.tryParse(commandRunner.runAction("status").getOutput().trim());
                  throw new ScriptStillRunningException(String.format("%s still running: pid(%s), last status: %s",
                        BlockUntilInitScriptStatusIsZeroThenReturnOutput.this, pid, exec),
                        BlockUntilInitScriptStatusIsZeroThenReturnOutput.this);
               }
               logger.debug("<< complete(%s) status(%s)", commandRunner.getStatement().getInstanceName(), exitStatus);
               set(exec);
            } catch (CancellationException e) {
            } catch (Exception e) {
               setException(e);
            }
         }
      });
      return this;
   }

   @Override
   protected boolean set(ExecResponse value) {
      eventBus.post(new StatementOnNodeCompletion(getCommandRunner().getStatement(), getCommandRunner().getNode(),
            value));
      return super.set(value);
   }

   @Override
   protected void interruptTask() {
      logger.debug("<< cancelled(%s)", commandRunner.getStatement().getInstanceName());
      ExecResponse returnVal = commandRunner.refreshAndRunAction("stop");
      CancellationException e = new CancellationException(String.format(
            "cancelled %s on node: %s; stop command had exit status: %s", getCommandRunner().getStatement()
                  .getInstanceName(), getCommandRunner().getNode().getId(), returnVal));
      eventBus.post(new StatementOnNodeFailure(getCommandRunner().getStatement(), getCommandRunner().getNode(), e));
      super.interruptTask();
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("commandRunner", commandRunner).toString();
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(commandRunner);
   }

   @Override
   public boolean equals(Object o) {
      if (o == null)
         return false;
      if (!o.getClass().equals(getClass()))
         return false;
      BlockUntilInitScriptStatusIsZeroThenReturnOutput that = BlockUntilInitScriptStatusIsZeroThenReturnOutput.class
            .cast(o);
      return Objects.equal(this.commandRunner, that.commandRunner);
   }

   @Override
   public ExecResponse get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException,
         ExecutionException {
      try {
         return super.get(timeout, unit);
      } catch (TimeoutException e) {
         ScriptStillRunningException exception = new ScriptStillRunningException(timeout, unit, this);
         exception.initCause(e);
         throw exception;
      }
   }

}