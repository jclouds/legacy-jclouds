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

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;
import javax.inject.Singleton;

import org.jclouds.logging.Logger;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ExecutionList;
import com.google.common.util.concurrent.ForwardingFuture;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Adapt things missing from Guava.
 * 
 * @author Adrian Cole
 */
@Singleton
public class ConcurrentUtils {

   public static <T> Map<T, Exception> awaitCompletion(Map<T, ? extends ListenableFuture<?>> responses,
            ExecutorService executor, @Nullable Long maxTime, final Logger logger, final String logPrefix) {
      if (responses.size() == 0)
         return ImmutableMap.of();
      final int total = responses.size();
      final CountDownLatch doneSignal = new CountDownLatch(total);
      final AtomicInteger complete = new AtomicInteger(0);
      final AtomicInteger errors = new AtomicInteger(0);
      final long start = System.currentTimeMillis();
      final Map<T, Exception> errorMap = Maps.newHashMap();
      for (final Entry<T, ? extends ListenableFuture<?>> future : responses.entrySet()) {
         future.getValue().addListener(new Runnable() {
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
         }, executor);
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
      return new ListenableFutureAdapter<T>(executorService, future);
   }

   /**
    * Just like {@code Futures#ListenableFutureAdapter} except that we pass in an executorService.
    * <p/>
    * Temporary hack until http://code.google.com/p/guava-libraries/issues/detail?id=317 is fixed.
    */
   private static class ListenableFutureAdapter<T> extends ForwardingFuture<T> implements ListenableFuture<T> {

      private final Executor adapterExecutor;

      // The execution list to hold our listeners.
      private final ExecutionList executionList = new ExecutionList();

      // This allows us to only start up a thread waiting on the delegate future
      // when the first listener is added.
      private final AtomicBoolean hasListeners = new AtomicBoolean(false);

      // The delegate future.
      private final Future<T> delegate;

      ListenableFutureAdapter(ExecutorService executorService, final Future<T> delegate) {
         this.adapterExecutor = executorService;
         this.delegate = delegate;
      }

      @Override
      protected Future<T> delegate() {
         return delegate;
      }

      /* @Override */
      public void addListener(Runnable listener, Executor exec) {

         // When a listener is first added, we run a task that will wait for
         // the delegate to finish, and when it is done will run the listeners.
         if (!hasListeners.get() && hasListeners.compareAndSet(false, true)) {
            adapterExecutor.execute(new Runnable() {
               /* @Override */
               public void run() {
                  try {
                     delegate.get();
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
   }
}