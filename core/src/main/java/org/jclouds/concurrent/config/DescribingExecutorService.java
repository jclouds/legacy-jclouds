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
package org.jclouds.concurrent.config;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DescribingExecutorService implements ExecutorService {

   protected final ExecutorService delegate;

   public DescribingExecutorService(ExecutorService delegate) {
      this.delegate = checkNotNull(delegate, "delegate");
   }

   @Override
   public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
      return delegate.awaitTermination(timeout, unit);
   }

   @Override
   public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
      return delegate.invokeAll(tasks);
   }

   @Override
   public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException {
      return delegate.invokeAll(tasks, timeout, unit);
   }

   @Override
   public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
      return delegate.invokeAny(tasks);
   }

   @Override
   public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
      return delegate.invokeAny(tasks, timeout, unit);
   }

   @Override
   public boolean isShutdown() {
      return delegate.isShutdown();
   }

   @Override
   public boolean isTerminated() {
      return delegate.isTerminated();
   }

   @Override
   public void shutdown() {
      delegate.shutdown();
   }

   @Override
   public List<Runnable> shutdownNow() {
      return delegate.shutdownNow();
   }

   @Override
   public <T> Future<T> submit(Callable<T> task) {
      return new DescribedFuture<T>(delegate.submit(task), task.toString(), ExecutorServiceModule.getStackTraceHere());
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   @Override
   public Future<?> submit(Runnable task) {
      return new DescribedFuture(delegate.submit(task), task.toString(), ExecutorServiceModule.getStackTraceHere());
   }

   @Override
   public <T> Future<T> submit(Runnable task, T result) {
      return new DescribedFuture<T>(delegate.submit(task, result), task.toString(), ExecutorServiceModule.getStackTraceHere());
   }

   @Override
   public void execute(Runnable arg0) {
      delegate.execute(arg0);
   }

   @Override
   public boolean equals(Object obj) {
      return delegate.equals(obj);
   }

   @Override
   public int hashCode() {
      return delegate.hashCode();
   }

   @Override
   public String toString() {
      return delegate.toString();
   }

}
