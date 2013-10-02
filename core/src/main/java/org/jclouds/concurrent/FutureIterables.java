/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.concurrent;

import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Maps.newConcurrentMap;
import static com.google.common.collect.Maps.newHashMap;
import static org.jclouds.util.Throwables2.containsThrowable;
import static org.jclouds.util.Throwables2.propagateAuthorizationOrOriginalException;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.Logger;
import org.jclouds.rest.AuthorizationException;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;

/**
 * functions related to or replacing those in {@link com.google.common.collect.Iterables} dealing with Futures
 * 
 * @author Adrian Cole
 */
@Beta
public class FutureIterables {
   
   @Inject(optional = true)
   @Named(Constants.PROPERTY_MAX_RETRIES)
   private static int maxRetries = 5;
   
   @Inject(optional = true)
   @Named(Constants.PROPERTY_RETRY_DELAY_START)
   private static long delayStart = 50L;
   
   @Inject(optional = true)
   private static BackoffLimitedRetryHandler retryHandler = BackoffLimitedRetryHandler.INSTANCE;

   public static <F, T> Iterable<T> transformParallel(final Iterable<F> fromIterable,
         final Function<? super F, ListenableFuture<? extends T>> function, ListeningExecutorService exec, @Nullable Long maxTime, Logger logger,
               String logPrefix) {
      return transformParallel(fromIterable, function, exec, maxTime, logger, logPrefix, retryHandler, maxRetries);
   }
   
   @SuppressWarnings("unchecked")
   public static <F, T> Iterable<T> transformParallel(Iterable<F> fromIterable,
         Function<? super F, ListenableFuture<? extends T>> function, ListeningExecutorService exec, @Nullable Long maxTime, Logger logger,
               String logPrefix, BackoffLimitedRetryHandler retryHandler, int maxRetries) {
      Map<F, Exception> exceptions = newHashMap();
      Map<F, ListenableFuture<? extends T>> responses = newHashMap();
      for (int i = 0; i < maxRetries; i++) {
         
         for (F from : fromIterable) {
            ListenableFuture<? extends T> to = function.apply(from);
            responses.put(from, to);
         }
         try {
            exceptions = awaitCompletion(responses, exec, maxTime, logger, logPrefix);
         } catch (TimeoutException te) {
            throw propagate(te);
         }
         if (exceptions.size() > 0 && !any(exceptions.values(), containsThrowable(AuthorizationException.class))) {
            fromIterable = exceptions.keySet();
            retryHandler.imposeBackoffExponentialDelay(delayStart, 2, i + 1, maxRetries,
                  String.format("error %s: %s: %s", logPrefix, fromIterable, exceptions));
         } else {
            break;
         }
      }
      //make sure we propagate any authorization exception so that we don't lock out accounts
      if (exceptions.size() > 0)
         return propagateAuthorizationOrOriginalException(new TransformParallelException(Map.class.cast(responses),
               exceptions, logPrefix));
      
      return unwrap(responses.values());
   }
   
   public static <F> Map<F, Exception> awaitCompletion(Map<F, ? extends ListenableFuture<?>> responses,
         ListeningExecutorService exec, @Nullable Long maxTime, final Logger logger, final String logPrefix)
         throws TimeoutException {
      final ConcurrentMap<F, Exception> errorMap = newConcurrentMap();
      if (responses.size() == 0)
         return errorMap;
      final int total = responses.size();
      final CountDownLatch doneSignal = new CountDownLatch(total);
      final AtomicInteger complete = new AtomicInteger(0);
      final AtomicInteger errors = new AtomicInteger(0);
      final long start = System.currentTimeMillis();
      for (final Entry<F, ? extends ListenableFuture<?>> future : responses.entrySet()) {
         future.getValue().addListener(new Runnable() {
            
            @Override
            public void run() {
               try {
                  future.getValue().get();
                  complete.incrementAndGet();
               } catch (Exception e) {
                  errors.incrementAndGet();
                  logException(logger, logPrefix, total, complete.get(), errors.get(), start, e);
                  errorMap.put(future.getKey(), e);
               } finally {
	               doneSignal.countDown();
               }
            }
            
            @Override
            public String toString() {
               return "callGetOnFuture(" + future.getKey() + "," + future.getValue() + ")";
            }
         }, exec);
      }
      try {
         if (maxTime != null) {
            if (!doneSignal.await(maxTime, TimeUnit.MILLISECONDS)) {
               String message = message(logPrefix, total, complete.get(), errors.get(), start);
               TimeoutException te = new TimeoutException(message);
               logger.error(te, message);
               throw te;
            }
         } else {
            doneSignal.await();
         }
         if (errors.get() > 0) {
            String message = message(logPrefix, total, complete.get(), errors.get(), start);
            RuntimeException exception = new RuntimeException(message);
            logger.error(exception, message);
         }
         if (logger.isTraceEnabled()) {
            String message = message(logPrefix, total, complete.get(), errors.get(), start);
            logger.trace(message);
         }
      } catch (InterruptedException ie) {
         String message = message(logPrefix, total, complete.get(), errors.get(), start);
         logger.error(ie, message);
         throw propagate(ie);
      }
      return errorMap;
   }
   
   private static <T> Iterable<T> unwrap(Iterable<ListenableFuture<? extends T>> values) {
      return transform(values, new Function<ListenableFuture<? extends T>, T>() {
         @Override
         public T apply(ListenableFuture<? extends T> from) {
            try {
               return from.get();
            } catch (InterruptedException e) {
               propagate(e);
            } catch (ExecutionException e) {
               propagate(e);
            }
            return null;
         }
         
         @Override
         public String toString() {
            return "callGetOnFuture()";
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
}
