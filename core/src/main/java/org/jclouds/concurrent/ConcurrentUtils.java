/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import static com.google.common.collect.Maps.newHashMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.ForwardingObject;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ExecutionList;
import com.google.common.util.concurrent.ForwardingFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Inject;

/**
 * Adapt things from Guava.
 * 
 * @author Adrian Cole
 */
@Singleton
public class ConcurrentUtils {
   @Resource
   private static Logger logger = Logger.CONSOLE;

   @Inject(optional = true)
   @Named(Constants.PROPERTY_MAX_RETRIES)
   private static int maxRetries = 5;

   @Inject(optional = true)
   private static BackoffLimitedRetryHandler retryHandler = BackoffLimitedRetryHandler.INSTANCE;

   public static <F, T> Iterable<T> transformParallel(final Iterable<F> fromIterable,
            final Function<? super F, Future<T>> function) {
      return transformParallel(fromIterable, function, sameThreadExecutor(), null);
   }

   public static <F, T> Iterable<T> transformParallel(final Iterable<F> fromIterable,
            final Function<? super F, Future<T>> function, ExecutorService exec, @Nullable Long maxTime) {
      return transformParallel(fromIterable, function, exec, maxTime, logger, "transforming");
   }

   public static <F, T> Iterable<T> transformParallel(final Iterable<F> fromIterable,
            final Function<? super F, Future<T>> function, ExecutorService exec, @Nullable Long maxTime, Logger logger,
            String logPrefix) {
      return transformParallel(fromIterable, function, exec, maxTime, logger, logPrefix, retryHandler, maxRetries);
   }

   public static <F, T> Iterable<T> transformParallel(Iterable<F> fromIterable,
            Function<? super F, Future<T>> function, ExecutorService exec, @Nullable Long maxTime, Logger logger,
            String logPrefix, BackoffLimitedRetryHandler retryHandler, int maxRetries) {
      Map<F, Exception> exceptions = newHashMap();
      Map<F, Future<T>> responses = newHashMap();
      for (int i = 0; i < maxRetries; i++) {

         for (F from : fromIterable) {
            responses.put(from, function.apply(from));
         }
         exceptions = awaitCompletion(responses, exec, maxTime, logger, logPrefix);
         if (exceptions.size() > 0) {
            fromIterable = exceptions.keySet();
            retryHandler.imposeBackoffExponentialDelay(i + 1, String.format("error %s: %s: %s", logPrefix,
                     fromIterable, exceptions));
         } else {
            break;
         }
      }
      if (exceptions.size() > 0)
         throw new RuntimeException(String.format("error %s: %s: %s", logPrefix, fromIterable, exceptions));

      return unwrap(responses.values());
   }

   public static <T> Map<T, Exception> awaitCompletion(Map<T, ? extends Future<?>> responses, ExecutorService exec,
            @Nullable Long maxTime, final Logger logger, final String logPrefix) {
      if (responses.size() == 0)
         return ImmutableMap.of();
      final int total = responses.size();
      final CountDownLatch doneSignal = new CountDownLatch(total);
      final AtomicInteger complete = new AtomicInteger(0);
      final AtomicInteger errors = new AtomicInteger(0);
      final long start = System.currentTimeMillis();
      final Map<T, Exception> errorMap = Maps.newHashMap();
      for (final java.util.Map.Entry<T, ? extends Future<?>> future : responses.entrySet()) {
         makeListenable(future.getValue(), exec).addListener(new Runnable() {
            public void run() {
               try {
                  future.getValue().get();
                  complete.incrementAndGet();
               } catch (Exception e) {
                  errors.incrementAndGet();
                  logException(logger, logPrefix, total, complete.get(), errors.get(), start, e);
                  errorMap.put(future.getKey(), e);
               }
               doneSignal.countDown();
            }
         }, exec);
      }
      try {
         if (maxTime != null)
            doneSignal.await(maxTime, TimeUnit.MILLISECONDS);
         else
            doneSignal.await();
         if (errors.get() > 0) {
            String message = message(logPrefix, total, complete.get(), errors.get(), start);
            RuntimeException exception = new RuntimeException(message);
            logger.error(exception, message);
         }
         if (logger.isTraceEnabled()) {
            String message = message(logPrefix, total, complete.get(), errors.get(), start);
            logger.trace(message);
         }
      } catch (InterruptedException e) {
         String message = message(logPrefix, total, complete.get(), errors.get(), start);
         TimeoutException exception = new TimeoutException(message);
         logger.error(exception, message);
         Throwables.propagate(exception);
      }
      return errorMap;
   }

   public static <T> Iterable<T> unwrap(Iterable<Future<T>> values) {
      return Iterables.transform(values, new Function<Future<T>, T>() {
         @Override
         public T apply(Future<T> from) {
            try {
               return from.get();
            } catch (InterruptedException e) {
               Throwables.propagate(e);
            } catch (ExecutionException e) {
               Throwables.propagate(e);
            }
            return null;
         }
      });
   }

   private static void logException(Logger logger, String logPrefix, int total, int complete, int errors, long start,
            Exception e) {
      String message = message(logPrefix, total, complete, errors, start);
      logger.error(e, message);
   }

   private static String message(String prefix, int size, int complete, int errors, long start) {
      return String.format("%s, completed: %d/%d, errors: %d, rate: %dms/op", prefix, complete, size, errors,
               (long) ((System.currentTimeMillis() - start) / ((double) size)));
   }

   protected static boolean timeOut(long start, Long maxTime) {
      return maxTime != null ? System.currentTimeMillis() < start + maxTime : false;
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

   /**
    * Just like {@code Futures#compose} except that we check the type of the executorService before
    * creating the Future. If we are single threaded, invoke the function lazy as opposed to
    * chaining, so that we don't invoke get() early.
    */
   public static <I, O> ListenableFuture<O> compose(Future<I> future, final Function<? super I, ? extends O> function,
            ExecutorService executorService) {
      if (future instanceof ListenableFutureAdapter<?>) {
         ListenableFutureAdapter<I> lf = (ListenableFutureAdapter<I>) future;
         if (lf.futureListener.executor.getClass().isAnnotationPresent(SingleThreaded.class))
            return LazyListenableFutureFunctionAdapter.create(((ListenableFutureAdapter<I>) future).futureListener,
                     function);
         else
            return Futures.compose(lf, function, executorService);
      } else if (executorService.getClass().isAnnotationPresent(SingleThreaded.class)) {
         return LazyListenableFutureFunctionAdapter.create(future, function, executorService);
      } else {
         return Futures.compose(makeListenable(future, executorService), function, executorService);
      }
   }

   public static class FutureListener<T> {
      private final Future<T> future;
      private final ExecutorService executor;
      private final ExecutionList executionList = new ExecutionList();
      private final AtomicBoolean hasListeners = new AtomicBoolean(false);

      static <T> FutureListener<T> create(Future<T> future, ExecutorService executor) {
         return new FutureListener<T>(future, executor);
      }

      private FutureListener(Future<T> future, ExecutorService executor) {
         this.future = checkNotNull(future, "future");
         this.executor = checkNotNull(executor, "executor");
      }

      public void addListener(Runnable listener, Executor exec) {

         // When a listener is first added, we run a task that will wait for
         // the future to finish, and when it is done will run the listeners.
         if (!hasListeners.get() && hasListeners.compareAndSet(false, true)) {
            executor.execute(new Runnable() {
               /* @Override */
               public void run() {
                  try {
                     future.get();
                  } catch (CancellationException e) {
                     // The task was cancelled, so it is done, run the listeners.
                  } catch (InterruptedException e) {
                     // This thread was interrupted. This should never happen, so we
                     // throw an IllegalStateException.
                     throw new IllegalStateException("Adapter thread interrupted!", e);
                  } catch (ExecutionException e) {
                     // The task caused an exception, so it is done, run the listeners.
                  }
                  executionList.run();
               }
            });
         }
         executionList.add(listener, exec);
      }

      Future<T> getFuture() {
         return future;
      }

      ExecutorService getExecutor() {
         return executor;
      }
   }

   public static class ListenableFutureAdapter<T> extends ForwardingFuture<T> implements ListenableFuture<T> {
      private final FutureListener<T> futureListener;

      static <T> ListenableFutureAdapter<T> create(Future<T> future, ExecutorService executor) {
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

   public static class LazyListenableFutureFunctionAdapter<I, O> extends ForwardingObject implements
            ListenableFuture<O> {
      private final FutureListener<I> futureListener;
      private final Function<? super I, ? extends O> function;

      static <I, O> LazyListenableFutureFunctionAdapter<I, O> create(Future<I> future,
               Function<? super I, ? extends O> function, ExecutorService executor) {
         return new LazyListenableFutureFunctionAdapter<I, O>(future, function, executor);
      }

      static <I, O> LazyListenableFutureFunctionAdapter<I, O> create(FutureListener<I> futureListener,
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
    * Taken from {@link MoreExecutors#sameThreadExecutor} as it was hidden and therefore incapable
    * of instanceof checks.
    * 
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
         return Collections.emptyList();
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