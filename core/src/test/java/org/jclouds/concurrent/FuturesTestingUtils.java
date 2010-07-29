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
import static com.google.common.util.concurrent.Futures.compose;
import static org.jclouds.concurrent.ConcurrentUtils.makeListenable;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Tests behavior of ConcurrentUtils
 * 
 * @author Adrian Cole
 */
public class FuturesTestingUtils {
   public static final int FUDGE = 5;
   public static final int COUNT = 100;
   public static final int CALLABLE_DURATION = 10;
   public static final int LISTENER_DURATION = 10;

   public static void checkThresholds(long expectedMin, long expectedMax, long expectedOverhead,
            ExecutorService callableExecutor, ExecutorService chainExecutor, final ExecutorService listenerExecutor) {
      long start = System.currentTimeMillis();
      Map<String, ListenableFuture<Long>> responses = newHashMap();
      for (int i = 0; i < COUNT; i++)
         responses.put(i + "", compose(createListenableFuture(callableExecutor, chainExecutor),
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

   public static Map<String, ListenableFuture<Long>> runCallables(ExecutorService callableExecutor,
            ExecutorService chainExecutor) {
      Map<String, ListenableFuture<Long>> responses = newHashMap();
      for (int i = 0; i < COUNT; i++)
         responses.put(i + "", createListenableFuture(callableExecutor, chainExecutor));
      return responses;
   }

   private static ListenableFuture<Long> createListenableFuture(ExecutorService callableExecutor,
            ExecutorService chainExecutor) {
      return makeListenable(callableExecutor.submit(new Callable<Long>() {

         @Override
         public Long call() throws Exception {
            Thread.sleep(CALLABLE_DURATION);
            return System.currentTimeMillis();
         }

      }), chainExecutor);
   }

   public static long getMaxIn(Map<String, ListenableFuture<Long>> responses) {
      Iterable<Long> collection = Iterables.transform(responses.values(), new Function<ListenableFuture<Long>, Long>() {

         @Override
         public Long apply(ListenableFuture<Long> from) {
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

   public static long getMinIn(Map<String, ListenableFuture<Long>> responses) {
      Iterable<Long> collection = Iterables.transform(responses.values(), new Function<ListenableFuture<Long>, Long>() {

         @Override
         public Long apply(ListenableFuture<Long> from) {
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
            Map<String, ListenableFuture<Long>> responses) {
      long time = getMaxIn(responses) - start;
      assert time >= expectedMax && time < expectedMax + expectedOverhead : String.format("expected %d, was %d",
               expectedMax, time);

      time = getMinIn(responses) - start;
      assert time >= expectedMin && time < expectedMin + expectedOverhead : String.format("expected %d, was %d",
               expectedMin, time);

      time = getMaxIn(responses) - start;
      assert time >= expectedMax && time < expectedMax + expectedOverhead : String.format("expected %d, was %d",
               expectedMax, time);
   }
}
