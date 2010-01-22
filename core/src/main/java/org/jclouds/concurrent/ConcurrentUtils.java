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

import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Singleton;

import org.jclouds.logging.Logger;

import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
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

   public static void pollResponsesAndLogWhenComplete(int total, String description, Logger logger,
            Set<Future<Void>> responses) throws InterruptedException, TimeoutException,
            ExecutionException {
      int complete = 0;
      long start = System.currentTimeMillis();
      long timeOut = 180 * 1000;
      do {
         Set<Future<Void>> retries = Sets.newHashSet();
         for (Future<Void> future : responses) {
            try {
               future.get(100, TimeUnit.MILLISECONDS);
               complete++;
            } catch (ExecutionException e) {
               Throwables.propagate(e);
            } catch (TimeoutException e) {
               retries.add(future);
            }
         }
         responses = Sets.newHashSet(retries);
      } while (responses.size() > 0 && System.currentTimeMillis() < start + timeOut);
      long duration = System.currentTimeMillis() - start;
      if (duration > timeOut)
         throw new TimeoutException(String.format("TIMEOUT: %s(%d/%d) rate: %f %s/second",
                  description, complete, total, ((double) complete) / (duration / 1000.0),
                  description));
      for (Future<Void> future : responses)
         future.get(30, TimeUnit.SECONDS);
      logger.debug("<< %s(%d)", description, total);
   }

   /**
    * Converts an exception into an object, which is useful for transforming to null or false.
    */
   public static <T> ListenableFuture<T> convertExceptionToValue(ListenableFuture<T> future,
            Class<? extends Exception> clazz, T toValue) {
      return new ConvertFutureExceptionToValue<T>(future, clazz, toValue);
   }

   /**
    * Just like {@code Futures#makeListenable} except that we pass in an executorService.
    * <p/>
    * Temporary hack until http://code.google.com/p/guava-libraries/issues/detail?id=317 is fixed.
    */
   public static <T> ListenableFuture<T> makeListenable(Future<T> future,
            ExecutorService executorService) {
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
   private static class ListenableFutureAdapter<T> extends ForwardingFuture<T> implements
            ListenableFuture<T> {

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