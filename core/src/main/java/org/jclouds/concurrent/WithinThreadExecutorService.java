/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * ExecutorService that executes within the current thread. Useful in environments where starting
 * threads is prohibited.
 * 
 * @author Adrian Cole
 */
@SingleThreadCompatible
public class WithinThreadExecutorService extends WithinThreadExecutor implements ExecutorService {
   private volatile boolean shutdown = false;

   /**
    * As all calls are blocking, this must alwoys return true;
    */
   public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
      checkState(!shutdown, "shutdown!");
      return true;
   }

   /**
    * {@inheritDoc}
    */
   public <T> List<Future<T>> invokeAll(Collection<Callable<T>> tasks) throws InterruptedException {
      checkState(!shutdown, "shutdown!");
      List<Future<T>> results = new ArrayList<Future<T>>(tasks.size());
      for (Callable<T> task : tasks) {
         checkNotNull(task, "task");
         RunnableFutureTask<T> future = new RunnableFutureTask<T>(task);
         future.run();
         results.add(future);
      }
      return results;
   }

   /**
    * {@inheritDoc}
    */
   public <T> List<Future<T>> invokeAll(Collection<Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException {
      checkState(!shutdown, "shutdown!");
      List<Future<T>> results = new ArrayList<Future<T>>(tasks.size());
      long timeUp = System.nanoTime() + unit.toNanos(timeout);
      for (Callable<T> task : tasks) {
         checkNotNull(task, "task");
         RunnableFutureTask<T> future = new RunnableFutureTask<T>(task);
         if (System.nanoTime() > timeUp)
            future.cancel(false);
         else
            future.run();
         results.add(future);
      }
      return results;
   }

   /**
    * {@inheritDoc}
    */
   public <T> T invokeAny(Collection<Callable<T>> tasks) throws InterruptedException,
            ExecutionException {
      checkState(!shutdown, "shutdown!");
      checkArgument(tasks.size() > 0, "no tasks");
      Exception exception = null;
      for (Callable<T> task : tasks) {
         checkNotNull(task, "task");
         try {
            return task.call();
         } catch (InterruptedException e) {
            throw e;
         } catch (Exception e) {
            exception = e;
         }
      }
      throw new ExecutionException("no tasks completed successfully", exception);
   }

   /**
    * {@inheritDoc}
    */
   public <T> T invokeAny(Collection<Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
      checkState(!shutdown, "shutdown!");
      checkArgument(tasks.size() > 0, "no tasks");
      long timeUp = System.nanoTime() + unit.toNanos(timeout);
      Exception exception = null;
      for (Callable<T> task : tasks) {
         checkNotNull(task, "task");
         try {
            return task.call();
         } catch (InterruptedException e) {
            throw e;
         } catch (Exception e) {
            exception = e;
         }
         if (System.nanoTime() > timeUp)
            throw new TimeoutException("Time up before we could run a task successfully");
      }
      throw new ExecutionException("no tasks completed successfully", exception);
   }

   /**
    * {@inheritDoc}
    */
   public boolean isShutdown() {
      return shutdown;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isTerminated() {
      return shutdown;
   }

   /**
    * {@inheritDoc}
    */
   public void shutdown() {
      shutdown = true;
   }

   /**
    * {@inheritDoc}
    * <p />
    * As every call is sequential, it is impossible for there to be a list remaining.
    */
   public List<Runnable> shutdownNow() {
      shutdown = true;
      return null;
   }

   /**
    * {@inheritDoc}
    * <p />
    * This will invoke the callable on submit and block for completion.
    */
   public <T> Future<T> submit(Callable<T> task) {
      RunnableFutureTask<T> future = new RunnableFutureTask<T>(task);
      future.run();
      return future;
   }

   /**
    * {@inheritDoc}
    * <p />
    * This will invoke the runnable on submit and block for completion.
    */
   public Future<?> submit(Runnable task) {
      RunnableFutureTask<Object> future = new RunnableFutureTask<Object>(Executors.callable(task,
               null));
      future.run();
      return future;
   }

   /**
    * {@inheritDoc}
    * <p />
    * This will invoke the runnable on submit and block for completion.
    */
   public <T> Future<T> submit(Runnable task, T result) {
      RunnableFutureTask<T> future = new RunnableFutureTask<T>(Executors.callable(task, result));
      future.run();
      return future;
   }

}
