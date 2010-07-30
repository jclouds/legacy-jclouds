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

import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Maps.newHashMap;
import static org.jclouds.concurrent.ConcurrentUtils.compose;
import static org.jclouds.concurrent.ConcurrentUtils.makeListenable;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Tests behavior of ConcurrentUtils
 * 
 * @author Adrian Cole
 */
public class FuturesTestingUtils {
   public static final int FUDGE = 5;
   public static final int COUNT = 100;
   public static final int CALLABLE_DURATION = 50;
   public static final int LISTENER_DURATION = 100;

   public static void checkThresholdsUsingFuturesCompose(long expectedMin, long expectedMax, long expectedOverhead,
            ExecutorService callableExecutor, ExecutorService chainExecutor, final ExecutorService listenerExecutor) {
      long start = System.currentTimeMillis();
      Map<String, Future<Long>> responses = newHashMap();
      for (int i = 0; i < COUNT; i++)
         responses.put(i + "", Futures.compose(createFuture(callableExecutor, chainExecutor),
                  new Function<Long, Long>() {

                     @Override
                     public Long apply(Long from) {
                        try {
                           Thread.sleep(LISTENER_DURATION);
                        } catch (InterruptedException e) {
                           propagate(e);
                        }
                        return System.currentTimeMillis();
                     }

                  }, listenerExecutor));
      checkTimeThresholds(expectedMin, expectedMax, expectedOverhead, start, responses);
   }

   public static void checkThresholdsUsingConcurrentUtilsCompose(long expectedMin, long expectedMax,
            long expectedOverhead, ExecutorService callableExecutor, ExecutorService chainExecutor,
            final ExecutorService listenerExecutor) {
      long start = System.currentTimeMillis();
      Map<String, Future<Long>> responses = newHashMap();
      for (int i = 0; i < COUNT; i++)
         responses.put(i + "", compose(createFuture(callableExecutor, chainExecutor), new Function<Long, Long>() {

            @Override
            public Long apply(Long from) {
               try {
                  Thread.sleep(LISTENER_DURATION);
               } catch (InterruptedException e) {
                  propagate(e);
               }
               return System.currentTimeMillis();
            }

         }, listenerExecutor));
      checkTimeThresholds(expectedMin, expectedMax, expectedOverhead, start, responses);
   }

   public static Map<String, Future<Long>> runCallables(ExecutorService callableExecutor, ExecutorService chainExecutor) {
      Map<String, Future<Long>> responses = newHashMap();
      for (int i = 0; i < COUNT; i++)
         responses.put(i + "", createFuture(callableExecutor, chainExecutor));
      return responses;
   }

   private static ListenableFuture<Long> createFuture(ExecutorService callableExecutor, ExecutorService chainExecutor) {
      return makeListenable(callableExecutor.submit(new Callable<Long>() {

         @Override
         public Long call() throws Exception {
            Thread.sleep(CALLABLE_DURATION);
            return System.currentTimeMillis();
         }

      }), chainExecutor);
   }

   public static long getMaxIn(Map<String, Future<Long>> responses) {
      Iterable<Long> collection = Iterables.transform(responses.values(), new Function<Future<Long>, Long>() {

         @Override
         public Long apply(Future<Long> from) {
            try {
               return from.get();
            } catch (InterruptedException e) {
            } catch (ExecutionException e) {
            }
            return null;
         }

      });
      long time = Collections.max(Sets.newHashSet(collection));
      return time;
   }

   public static long getMinIn(Map<String, Future<Long>> responses) {
      Iterable<Long> collection = Iterables.transform(responses.values(), new Function<Future<Long>, Long>() {

         @Override
         public Long apply(Future<Long> from) {
            try {
               return from.get();
            } catch (InterruptedException e) {
            } catch (ExecutionException e) {
            }
            return null;
         }

      });
      long time = Collections.min(Sets.newHashSet(collection));
      return time;
   }

   public static void checkTimeThresholds(long expectedMin, long expectedMax, long expectedOverhead, long start,
            Map<String, Future<Long>> responses) {
      long time = getMaxIn(responses) - start;
      assert time >= expectedMax && time < expectedMax + expectedOverhead : String.format("expectedMax  %d, max %d",
               expectedMax, time);

      time = getMinIn(responses) - start;
      assert time >= expectedMin && time < expectedMin + expectedOverhead : String.format("expectedMin  %d, min %d",
               expectedMin, time);

      time = getMaxIn(responses) - start;
      assert time >= expectedMax && time < expectedMax + expectedOverhead : String.format("expectedMax  %d, max %d",
               expectedMax, time);
   }
}
