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

package org.jclouds.concurrent;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.common.base.Function;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Transforms the exceptions in a future upon get
 * 
 * Temporarily here until the following is resolved: <a
 * href="http://code.google.com/p/guava-libraries/issues/detail?id=310"> guava issue 310</a>
 * 
 * @author Adrian Cole
 */
public class ExceptionParsingListenableFuture<T> implements ListenableFuture<T> {

   private final ListenableFuture<T> future;
   private final Function<Exception, T> function;

   public static <T> ExceptionParsingListenableFuture<T> create(ListenableFuture<T> future,
            Function<Exception, T> function) {
      return new ExceptionParsingListenableFuture<T>(future, function);
   }

   public ExceptionParsingListenableFuture(ListenableFuture<T> future, Function<Exception, T> function) {
      this.future = checkNotNull(future);
      this.function = checkNotNull(function);
   }

   public boolean cancel(boolean mayInterruptIfRunning) {
      return future.cancel(mayInterruptIfRunning);
   }

   public T get() throws InterruptedException, ExecutionException {
      try {
         return future.get();
      } catch (Exception e) {
         return attemptConvert(e);
      }
   }

   private T attemptConvert(Exception e) {
      return function.apply(e instanceof ExecutionException ? (Exception) e.getCause() : e);
   }

   public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      try {
         return future.get(timeout, unit);
      } catch (Exception e) {
         return attemptConvert(e);
      }
   }

   public boolean isCancelled() {
      return future.isCancelled();
   }

   public boolean isDone() {
      return future.isDone();
   }

   @Override
   public void addListener(Runnable listener, Executor exec) {
      future.addListener(listener, exec);
   }
}
