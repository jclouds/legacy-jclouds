/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.logging.Logger;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

/**
 * functions related to or replacing those in {@link com.google.common.collect.Iterables} dealing with Futures
 * 
 * @author Adrian Cole
 */
@Beta
public class FutureIterables {
   @Resource
   private static Logger logger = Logger.CONSOLE;

   @Inject(optional = true)
   @Named(Constants.PROPERTY_MAX_RETRIES)
   private static int maxRetries = 5;

   @Inject(optional = true)
   @Named(Constants.PROPERTY_RETRY_DELAY_START)
   private static long delayStart = 50L;

   @Inject(optional = true)
   private static BackoffLimitedRetryHandler retryHandler = BackoffLimitedRetryHandler.INSTANCE;

   public static <F, T> Iterable<T> transformParallel(final Iterable<F> fromIterable,
            final Function<? super F, Future<T>> function) {
      return transformParallel(fromIterable, function, org.jclouds.concurrent.MoreExecutors.sameThreadExecutor(), null);
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
            retryHandler.imposeBackoffExponentialDelay(delayStart, 2, i + 1, maxRetries,
                     String.format("error %s: %s: %s", logPrefix, fromIterable, exceptions));
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
         Futures.makeListenable(future.getValue(), exec).addListener(new Runnable() {
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

}