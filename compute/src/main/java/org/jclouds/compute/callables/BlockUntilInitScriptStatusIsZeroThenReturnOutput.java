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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.predicates.ScriptStatusReturnsZero;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.scriptbuilder.InitBuilder;
import org.jclouds.ssh.SshClient;

import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.inject.assistedinject.Assisted;

/**
 * A future that works in tandem with a task that was invoked by {@link InitBuilder}
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
   private final SudoAwareInitManager commandRunner;
   private final RetryablePredicate<String> notRunningAnymore;

   private boolean shouldCancel;

   @Inject
   public BlockUntilInitScriptStatusIsZeroThenReturnOutput(
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService userThreads,
            final ScriptStatusReturnsZero stateRunning, @Assisted final SudoAwareInitManager commandRunner) {
      this.commandRunner = checkNotNull(commandRunner, "commandRunner");
      this.userThreads = checkNotNull(userThreads, "userThreads");
      this.notRunningAnymore = new RetryablePredicate<String>(new Predicate<String>() {

         @Override
         public boolean apply(String arg0) {
            return commandRunner.runAction(arg0).getOutput().trim().equals("");
         }
         // arbitrarily high value, but Long.MAX_VALUE doesn't work!
      }, TimeUnit.DAYS.toMillis(365)) {
         /**
          * make sure we stop the retry loop if someone cancelled the future, this keeps threads
          * from being consumed on dead tasks
          */
         @Override
         protected boolean atOrAfter(Date end) {
            if (shouldCancel)
               Throwables.propagate(new TimeoutException("cancelled"));
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
    * Submits a thread that will either set the result of the future or the exception that took
    * place
    */
   @PostConstruct
   BlockUntilInitScriptStatusIsZeroThenReturnOutput init() {
      userThreads.submit(new Runnable() {
         @Override
         public void run() {
            try {
               boolean complete = notRunningAnymore.apply("status");
               String stdout = commandRunner.runAction("tail").getOutput();
               String stderr = commandRunner.runAction("tailerr").getOutput();
               // TODO make ScriptBuilder save exit status on nuhup
               logger.debug("<< complete(%s) status(%s)", commandRunner.getStatement().getInstanceName(), complete);
               set(new ExecResponse(stdout, stderr, complete && !shouldCancel ? 0 : -1));
            } catch (Exception e) {
               setException(e);
            }
         }
      });
      return this;
   }

   @Override
   protected void interruptTask() {
      logger.debug("<< cancelled(%s)", commandRunner.getStatement().getInstanceName());
      commandRunner.refreshAndRunAction("stop");
      shouldCancel = true;
      super.interruptTask();
   }

   @Override
   public String toString() {
      return String.format("running task[%s]", commandRunner);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((commandRunner == null) ? 0 : commandRunner.hashCode());
      result = prime * result + ((logger == null) ? 0 : logger.hashCode());
      result = prime * result + ((notRunningAnymore == null) ? 0 : notRunningAnymore.hashCode());
      result = prime * result + (shouldCancel ? 1231 : 1237);
      result = prime * result + ((userThreads == null) ? 0 : userThreads.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      BlockUntilInitScriptStatusIsZeroThenReturnOutput other = (BlockUntilInitScriptStatusIsZeroThenReturnOutput) obj;
      if (commandRunner == null) {
         if (other.commandRunner != null)
            return false;
      } else if (!commandRunner.equals(other.commandRunner))
         return false;
      if (logger == null) {
         if (other.logger != null)
            return false;
      } else if (!logger.equals(other.logger))
         return false;
      if (notRunningAnymore == null) {
         if (other.notRunningAnymore != null)
            return false;
      } else if (!notRunningAnymore.equals(other.notRunningAnymore))
         return false;
      if (shouldCancel != other.shouldCancel)
         return false;
      if (userThreads == null) {
         if (other.userThreads != null)
            return false;
      } else if (!userThreads.equals(other.userThreads))
         return false;
      return true;
   }

   @Override
   public ExecResponse get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException,
            ExecutionException {
      try {
         return super.get(timeout, unit);
      } catch (TimeoutException e) {
         throw new ScriptStillRunningException(timeout, unit, this);
      }
   }

}