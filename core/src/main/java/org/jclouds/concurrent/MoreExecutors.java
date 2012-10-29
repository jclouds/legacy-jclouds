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
/*
 * Copyright (C) 2007 Google Inc.
 *
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
 */

package org.jclouds.concurrent;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

/**
 * functions related to or replacing those in
 * {@link com.google.common.util.concurrent.MoreExecutors}
 * 
 * @author Adrian Cole
 */
@Beta
public class MoreExecutors {

   /**
    * Taken from @link com.google.common.util.concurrent.MoreExecutors} as it was hidden and
    * therefore incapable of instanceof checks.
    * 
    * 
    * Creates an executor service that runs each task in the thread that invokes {@code
    * execute/submit}, as in {@link CallerRunsPolicy} This applies both to individually submitted
    * tasks and to collections of tasks submitted via {@code invokeAll} or {@code invokeAny}. In the
    * latter case, tasks will run serially on the calling thread. Tasks are run to completion before
    * a {@code Future} is returned to the caller (unless the executor has been shutdown).
    * 
    * <p>
    * Although all tasks are immediately executed in the thread that submitted the task, this
    * {@code ExecutorService} imposes a small locking overhead on each task submission in order to
    * implement shutdown and termination behavior.
    * 
    * <p>
    * The implementation deviates from the {@code ExecutorService} specification with regards to the
    * {@code shutdownNow} method. First, "best-effort" with regards to canceling running tasks is
    * implemented as "no-effort". No interrupts or other attempts are made to stop threads executing
    * tasks. Second, the returned list will always be empty, as any submitted task is considered to
    * have started execution. This applies also to tasks given to {@code invokeAll} or {@code
    * invokeAny} which are pending serial execution, even the subset of the tasks that have not yet
    * started execution. It is unclear from the {@code ExecutorService} specification if these
    * should be included, and it's much easier to implement the interpretation that they not be.
    * Finally, a call to {@code shutdown} or {@code shutdownNow} may result in concurrent calls to
    * {@code invokeAll/invokeAny} throwing RejectedExecutionException, although a subset of the
    * tasks may already have been executed.
    */
   public static ExecutorService sameThreadExecutor() {
      return new SameThreadExecutorService();
   }

   // See sameThreadExecutor javadoc for behavioral notes.
   @SingleThreaded
   public static class SameThreadExecutorService extends AbstractExecutorService {
      /**
       * Lock used whenever accessing the state variables (runningTasks, shutdown,
       * terminationCondition) of the executor
       */
      private final Lock lock = new ReentrantLock();

      /** Signaled after the executor is shutdown and running tasks are done */
      private final Condition termination = lock.newCondition();

      private SameThreadExecutorService() {
      }

      /*
       * Conceptually, these two variables describe the executor being in one of three states: -
       * Active: shutdown == false - Shutdown: runningTasks > 0 and shutdown == true - Terminated:
       * runningTasks == 0 and shutdown == true
       */
      private int runningTasks = 0;
      private boolean shutdown = false;

      @Override
      public void execute(Runnable command) {
         startTask();
         try {
            command.run();
         } finally {
            endTask();
         }
      }

      @Override
      public boolean isShutdown() {
         lock.lock();
         try {
            return shutdown;
         } finally {
            lock.unlock();
         }
      }

      @Override
      public void shutdown() {
         lock.lock();
         try {
            shutdown = true;
         } finally {
            lock.unlock();
         }
      }

      // See sameThreadExecutor javadoc for unusual behavior of this method.
      @Override
      public List<Runnable> shutdownNow() {
         shutdown();
         return ImmutableList.of();
      }

      @Override
      public boolean isTerminated() {
         lock.lock();
         try {
            return shutdown && runningTasks == 0;
         } finally {
            lock.unlock();
         }
      }

      @Override
      public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
         long nanos = unit.toNanos(timeout);
         lock.lock();
         try {
            for (;;) {
               if (isTerminated()) {
                  return true;
               } else if (nanos <= 0) {
                  return false;
               } else {
                  nanos = termination.awaitNanos(nanos);
               }
            }
         } finally {
            lock.unlock();
         }
      }

      /**
       * Checks if the executor has been shut down and increments the running task count.
       * 
       * @throws RejectedExecutionException
       *            if the executor has been previously shutdown
       */
      private void startTask() {
         lock.lock();
         try {
            if (isShutdown()) {
               throw new RejectedExecutionException("Executor already shutdown");
            }
            runningTasks++;
         } finally {
            lock.unlock();
         }
      }

      /**
       * Decrements the running task count.
       */
      private void endTask() {
         lock.lock();
         try {
            runningTasks--;
            if (isTerminated()) {
               termination.signalAll();
            }
         } finally {
            lock.unlock();
         }
      }
   }

}
