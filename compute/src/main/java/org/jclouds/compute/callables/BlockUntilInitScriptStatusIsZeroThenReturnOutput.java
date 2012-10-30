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
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.RetryablePredicate;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.eventbus.EventBus;
import com.google.common.primitives.Ints;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

/**
 * A future that works in tandem with a task that was invoked by {@link InitScript}
 * 
 * @author Adrian Cole
 */
public class BlockUntilInitScriptStatusIsZeroThenReturnOutput extends AbstractFuture<ExecResponse> implements Runnable {

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

   private Predicate<String> notRunningAnymore;

   @Inject
   public BlockUntilInitScriptStatusIsZeroThenReturnOutput(
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService userThreads, EventBus eventBus,
            ComputeServiceConstants.InitStatusProperties properties, @Assisted SudoAwareInitManager commandRunner) {
      this(userThreads, eventBus, Predicates.<String> alwaysTrue(), commandRunner);
      // this is mutable only until we can determine how to decouple "this" from here
      notRunningAnymore = new LoopUntilTrueOrThrowCancellationException(new ExitStatusOfCommandGreaterThanZero(
               commandRunner), properties.initStatusMaxPeriod, properties.initStatusInitialPeriod, this);
   }

   @VisibleForTesting
   public BlockUntilInitScriptStatusIsZeroThenReturnOutput(ExecutorService userThreads, EventBus eventBus,
            Predicate<String> notRunningAnymore, SudoAwareInitManager commandRunner) {
      this.commandRunner = checkNotNull(commandRunner, "commandRunner");
      this.userThreads = checkNotNull(userThreads, "userThreads");
      this.eventBus = checkNotNull(eventBus, "eventBus");
      this.notRunningAnymore = checkNotNull(notRunningAnymore, "notRunningAnymore");
   }

   @VisibleForTesting
   static class ExitStatusOfCommandGreaterThanZero implements Predicate<String> {
      private final SudoAwareInitManager commandRunner;

      ExitStatusOfCommandGreaterThanZero(SudoAwareInitManager commandRunner) {
         this.commandRunner = commandRunner;
      }

      @Override
      public boolean apply(String input) {
         return commandRunner.runAction(input).getExitStatus() > 0;
      }

   }

   @VisibleForTesting
   static class LoopUntilTrueOrThrowCancellationException extends RetryablePredicate<String> {

      private final AbstractFuture<ExecResponse> futureWhichMightBeCancelled;

      public LoopUntilTrueOrThrowCancellationException(Predicate<String> predicate, long period, long maxPeriod,
               AbstractFuture<ExecResponse> futureWhichMightBeCancelled) {
         // arbitrarily high value, but Long.MAX_VALUE doesn't work!
         super(predicate, TimeUnit.DAYS.toMillis(365), period, maxPeriod, TimeUnit.MILLISECONDS);
         this.futureWhichMightBeCancelled = futureWhichMightBeCancelled;
      }

      /**
       * make sure we stop the retry loop if someone cancelled the future, this keeps threads from
       * being consumed on dead tasks
       */
      @Override
      protected boolean atOrAfter(Date end) {
         if (futureWhichMightBeCancelled.isCancelled())
            throw new CancellationException(futureWhichMightBeCancelled + " is cancelled");
         return super.atOrAfter(end);
      }
   }

   /**
    * Submits a thread that will either set the result of the future or the exception that took
    * place
    */
   public BlockUntilInitScriptStatusIsZeroThenReturnOutput init() {
      userThreads.submit(this);
      return this;
   }

   @Override
   public void run() {
      try {
         ExecResponse exec = null;
         do {
            notRunningAnymore.apply("status");
            String stdout = commandRunner.runAction("stdout").getOutput();
            String stderr = commandRunner.runAction("stderr").getOutput();
            Integer exitStatus = Ints.tryParse(commandRunner.runAction("exitstatus").getOutput().trim());
            exec = new ExecResponse(stdout, stderr, exitStatus == null ? -1 : exitStatus);
         } while (!isCancelled() && exec.getExitStatus() == -1);
         logger.debug("<< complete(%s) status(%s)", commandRunner.getStatement().getInstanceName(), exec
                  .getExitStatus());
         set(exec);
      } catch (Exception e) {
         setException(e);
      }
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
