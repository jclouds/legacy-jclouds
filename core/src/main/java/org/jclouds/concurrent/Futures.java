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
package org.jclouds.concurrent;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.ForwardingObject;
import com.google.common.util.concurrent.ExecutionList;
import com.google.common.util.concurrent.ForwardingFuture;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * functions related to or replacing those in {@link com.google.common.util.concurrent.Futures}
 * 
 * @author Adrian Cole
 */
@Beta
public class Futures {
   @VisibleForTesting
   static class CallGetAndRunExecutionList<T> implements Runnable {
      private final Future<T> delegate;
      private final ExecutionList executionList;

      CallGetAndRunExecutionList(Future<T> delegate, ExecutionList executionList) {
         this.delegate = checkNotNull(delegate, "delegate");
         this.executionList = checkNotNull(executionList, "executionList");
      }

      @Override
      public void run() {
         try {
            delegate.get();
         } catch (InterruptedException e) {
            // This thread was interrupted. This should never happen, so we
            // throw an IllegalStateException.
            Thread.currentThread().interrupt();
            // TODO we cannot inspect the executionList at the moment to make a reasonable
            // toString()
            throw new IllegalStateException(String.format(
                     "interrupted calling get() on [%s], so could not run listeners", delegate), e);
         } catch (Throwable e) {
            // ExecutionException / CancellationException / RuntimeException
            // The task is done, run the listeners.
         }
         executionList.execute();
      }

      @Override
      public String toString() {
         return "[delegate=" + delegate + ", executionList=" + executionList + "]";
      }
   }

   // Adapted from Guava
   //
   // * to allow us to enforce supply of an adapterExecutor
   // note that this is done so that we can operate in Google AppEngine which
   // restricts thread creation
   // * to allow us to print debug info about what the delegate was doing
   private static class FutureListener<T> {

      private final ExecutorService adapterExecutor;

      // The execution list to hold our listeners.
      private final ExecutionList executionList = new ExecutionList();

      // This allows us to only start up a thread waiting on the delegate future
      // when the first listener is added.
      private final AtomicBoolean hasListeners = new AtomicBoolean(false);

      // The delegate future.
      private final Future<T> delegate;

      private static <T> FutureListener<T> create(Future<T> delegate, ExecutorService adapterExecutor) {
         return new FutureListener<T>(delegate, adapterExecutor);
      }

      private FutureListener(Future<T> delegate, ExecutorService adapterExecutor) {
         this.delegate = checkNotNull(delegate, "delegate");
         this.adapterExecutor = checkNotNull(adapterExecutor, "adapterExecutor");
      }

      private void addListener(Runnable listener, Executor exec) {
         executionList.add(listener, exec);

         // When a listener is first added, we run a task that will wait for
         // the delegate to finish, and when it is done will run the listeners.
         if (hasListeners.compareAndSet(false, true)) {
            if (delegate.isDone()) {
               // If the delegate is already done, run the execution list
               // immediately on the current thread.
               executionList.execute();
               return;
            }
            adapterExecutor.execute(new CallGetAndRunExecutionList<T>(delegate, executionList));
         }
      }

      private Future<T> getFuture() {
         return delegate;
      }
   }

   private static class ListenableFutureAdapter<T> extends ForwardingFuture<T> implements ListenableFuture<T> {
      private final FutureListener<T> futureListener;

      private static <T> ListenableFutureAdapter<T> create(Future<T> future, ExecutorService executor) {
         return new ListenableFutureAdapter<T>(future, executor);
      }

      private ListenableFutureAdapter(Future<T> future, ExecutorService executor) {
         this.futureListener = FutureListener.create(future, executor);
      }

      @Override
      protected Future<T> delegate() {
         return futureListener.getFuture();
      }

      @Override
      public void addListener(Runnable listener, Executor exec) {
         futureListener.addListener(listener, exec);
      }
   }

   private static class LazyListenableFutureFunctionAdapter<I, O> extends ForwardingObject implements
            ListenableFuture<O> {
      private final FutureListener<I> futureListener;
      private final Function<? super I, ? extends O> function;

      private static <I, O> LazyListenableFutureFunctionAdapter<I, O> create(Future<I> future,
               Function<? super I, ? extends O> function, ExecutorService executor) {
         return new LazyListenableFutureFunctionAdapter<I, O>(future, function, executor);
      }

      private static <I, O> LazyListenableFutureFunctionAdapter<I, O> create(FutureListener<I> futureListener,
               Function<? super I, ? extends O> function) {
         return new LazyListenableFutureFunctionAdapter<I, O>(futureListener, function);
      }

      private LazyListenableFutureFunctionAdapter(Future<I> future, Function<? super I, ? extends O> function,
               ExecutorService executor) {
         this(FutureListener.create(future, executor), function);
      }

      private LazyListenableFutureFunctionAdapter(FutureListener<I> futureListener,
               Function<? super I, ? extends O> function) {
         this.futureListener = checkNotNull(futureListener, "futureListener");
         this.function = checkNotNull(function, "function");
      }

      /*
       * Concurrency detail:
       * 
       * <p>To preserve the idempotency of calls to this.get(*) calls to the function are only
       * applied once. A lock is required to prevent multiple applications of the function. The
       * calls to future.get(*) are performed outside the lock, as is required to prevent calls to
       * get(long, TimeUnit) to persist beyond their timeout.
       * 
       * <p>Calls to future.get(*) on every call to this.get(*) also provide the cancellation
       * behavior for this.
       * 
       * <p>(Consider: in thread A, call get(), in thread B call get(long, TimeUnit). Thread B may
       * have to wait for Thread A to finish, which would be unacceptable.)
       * 
       * <p>Note that each call to Future<O>.get(*) results in a call to Future<I>.get(*), but the
       * function is only applied once, so Future<I>.get(*) is assumed to be idempotent.
       */

      private final Object lock = new Object();
      private boolean set = false;
      private O value = null;

      @Override
      public O get() throws InterruptedException, ExecutionException {
         return apply(futureListener.getFuture().get());
      }

      @Override
      public O get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
         return apply(futureListener.getFuture().get(timeout, unit));
      }

      private O apply(I raw) {
         synchronized (lock) {
            if (!set) {
               value = function.apply(raw);
               set = true;
            }
            return value;
         }
      }

      @Override
      public boolean cancel(boolean mayInterruptIfRunning) {
         return futureListener.getFuture().cancel(mayInterruptIfRunning);
      }

      @Override
      public boolean isCancelled() {
         return futureListener.getFuture().isCancelled();
      }

      @Override
      public boolean isDone() {
         return futureListener.getFuture().isDone();
      }

      @Override
      public void addListener(Runnable listener, Executor exec) {
         futureListener.addListener(listener, exec);
      }

      @Override
      protected Object delegate() {
         return futureListener.getFuture();
      }

   }

   /**
    * Just like {@code Futures#compose} except that we check the type of the executorService before
    * creating the Future. If we are single threaded, invoke the function lazy as opposed to
    * chaining, so that we don't invoke get() early.
    */
   public static <I, O> ListenableFuture<O> compose(Future<I> future, final Function<? super I, ? extends O> function,
            ExecutorService executorService) {
      if (future instanceof Futures.ListenableFutureAdapter<?>) {
         Futures.ListenableFutureAdapter<I> lf = (ListenableFutureAdapter<I>) future;
         if (lf.futureListener.adapterExecutor.getClass().isAnnotationPresent(SingleThreaded.class))
            return Futures.LazyListenableFutureFunctionAdapter.create(
                     ((org.jclouds.concurrent.Futures.ListenableFutureAdapter<I>) future).futureListener, function);
         else
            return com.google.common.util.concurrent.Futures.transform(lf, function, executorService);
      } else if (executorService.getClass().isAnnotationPresent(SingleThreaded.class)) {
         return Futures.LazyListenableFutureFunctionAdapter.create(future, function, executorService);
      } else {
         return com.google.common.util.concurrent.Futures.transform(Futures.makeListenable(future, executorService),
                  function, executorService);
      }
   }

   /**
    * Just like {@code Futures#makeListenable} except that we pass in an executorService.
    * <p/>
    * Temporary hack until http://code.google.com/p/guava-libraries/issues/detail?id=317 is fixed.
    */
   public static <T> ListenableFuture<T> makeListenable(Future<T> future, ExecutorService executorService) {
      if (future instanceof ListenableFuture<?>) {
         return (ListenableFuture<T>) future;
      }
      return ListenableFutureAdapter.create(future, executorService);
   }

}
