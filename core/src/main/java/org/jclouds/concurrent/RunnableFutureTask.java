/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.concurrent;

import static com.google.common.base.Preconditions.*;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Future that is a result of a task ran within a single thread. As such, cancel is not valid, as
 * the operation is already complete.
 * 
 * This class is like {@link FutureTask} except that it does not attempt {@code Thread.interrupt() }
 * which is sometimes prohibited.
 * 
 * @author Adrian Cole
 */
@SingleThreadCompatible
public class RunnableFutureTask<V> implements Future<V> {
   private ExecutionException executionException;
   private InterruptedException interruptedException;
   private CancellationException cancellationException;
   private V value;
   private boolean ran = false;

   private final Callable<V> task;

   public RunnableFutureTask(Callable<V> task) {
      this.task = task;
   }

   /**
    * {@inheritDoc}
    * 
    * @param mayInterruptIfRunning
    *           - ignored as this cannot be called at the same time as the task is running
    */
   public boolean cancel(boolean mayInterruptIfRunning) {
      if (ran) {
         return false;
      } else {
         cancellationException = new CancellationException();
      }
      return true;
   }

   public boolean isCancelled() {
      return cancellationException != null;
   }

   public boolean isDone() {
      return ran || cancellationException != null || interruptedException != null
               || executionException != null;
   }

   public V get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException,
            TimeoutException {
      return get();
   }

   public void run() {
      try {
         value = task.call();
      } catch (InterruptedException e) {
         interruptedException = e;
      } catch (Exception e) {
         executionException = new ExecutionException(e);
      } finally {
         ran = true;
      }
   }

   /**
    * {@inheritDoc}
    */
   public V get() throws InterruptedException, ExecutionException {
      if (cancellationException != null)
         throw cancellationException;
      if (interruptedException != null)
         throw interruptedException;
      if (executionException != null)
         throw executionException;
      checkState(ran, "run() was never called");
      return value;
   }
}
