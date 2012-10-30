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

import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static org.jclouds.concurrent.FutureIterables.awaitCompletion;
import static org.testng.Assert.assertEquals;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import org.jclouds.logging.Logger;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Tests behavior of FutureIterables
 * 
 * @author Adrian Cole
 */
@Test(groups = "performance", enabled = false, sequential = true, testName = "FutureIterablesPerformanceTest")
public class FutureIterablesPerformanceTest {
   ExecutorService ioFunctionExecutor = newCachedThreadPool();

   @Test(enabled = false)
   public void testMakeListenableDoesntSerializeFutures() throws InterruptedException, ExecutionException {
      long expectedMax = IO_DURATION;
      long expectedMin = IO_DURATION;
      long expectedOverhead = COUNT + FUDGE;

      ExecutorService chainExecutor = MoreExecutors.sameThreadExecutor();

      long start = System.currentTimeMillis();
      Map<String, Future<Long>> responses = runCallables(chainExecutor);
      checkTimeThresholds(expectedMin, expectedMax, expectedOverhead, start, responses);
   }

   @Test(enabled = false)
   public void testAwaitCompletionUsingSameThreadExecutorDoesntSerializeFutures()
            throws InterruptedException, ExecutionException, TimeoutException {
      long expectedMax = IO_DURATION;
      long expectedMin = IO_DURATION;
      long expectedOverhead = COUNT + FUDGE;

      ExecutorService chainExecutor = MoreExecutors.sameThreadExecutor();

      long start = System.currentTimeMillis();
      Map<String, Future<Long>> responses = runCallables(chainExecutor);
      Map<String, Exception> exceptions = awaitCompletion(responses, MoreExecutors.sameThreadExecutor(), null,
               Logger.CONSOLE, "test same thread");
      assertEquals(exceptions.size(), 0);
      checkTimeThresholds(expectedMin, expectedMax, expectedOverhead, start, responses);
   }

   @Test(enabled = false)
   public void whenCachedThreadPoolIsUsedForChainAndListenerMaxDurationIsSumOfCallableAndListener()
            throws InterruptedException, ExecutionException {
      long expectedMax = IO_DURATION + LISTENER_DURATION;
      long expectedMin = IO_DURATION + LISTENER_DURATION;
      long expectedOverhead = COUNT * 4 + FUDGE;

      ExecutorService userthreads = newCachedThreadPool();
      try {
         ExecutorService chainExecutor = userthreads;
         ExecutorService listenerExecutor = userthreads;

         checkThresholdsUsingCompose(expectedMin, expectedMax, expectedOverhead, chainExecutor, listenerExecutor);
      } finally {
         userthreads.shutdownNow();
      }
   }

   @Test(enabled = false)
   public void whenCachedThreadPoolIsUsedForChainButSameThreadForListenerMaxDurationIsSumOfCallableAndListener()
            throws InterruptedException, ExecutionException {
      long expectedMax = IO_DURATION + LISTENER_DURATION;
      long expectedMin = IO_DURATION + LISTENER_DURATION;
      long expectedOverhead = COUNT + FUDGE;

      ExecutorService userthreads = newCachedThreadPool();
      try {
         ExecutorService chainExecutor = userthreads;
         ExecutorService listenerExecutor = MoreExecutors.sameThreadExecutor();

         checkThresholdsUsingCompose(expectedMin, expectedMax, expectedOverhead, chainExecutor, listenerExecutor);
      } finally {
         userthreads.shutdownNow();
      }
   }

   @Test(enabled = false)
   public void whenSameThreadIsUsedForChainButCachedThreadPoolForListenerMaxDurationIsIOAndSumOfAllListeners()
            throws InterruptedException, ExecutionException {
      long expectedMax = IO_DURATION + (LISTENER_DURATION * COUNT);
      long expectedMin = IO_DURATION + LISTENER_DURATION;
      long expectedOverhead = COUNT + FUDGE;

      ExecutorService userthreads = newCachedThreadPool();
      try {
         ExecutorService chainExecutor = MoreExecutors.sameThreadExecutor();
         ExecutorService listenerExecutor = userthreads;

         checkThresholdsUsingCompose(expectedMin, expectedMax, expectedOverhead, chainExecutor, listenerExecutor);
      } finally {
         userthreads.shutdownNow();
      }
   }

   @Test(enabled = false)
   public void whenSameThreadIsUsedForChainAndListenerMaxDurationIsIOAndSumOfAllListeners()
            throws InterruptedException, ExecutionException {

      long expectedMax = IO_DURATION + (LISTENER_DURATION * COUNT);
      long expectedMin = IO_DURATION + LISTENER_DURATION;
      long expectedOverhead = COUNT + FUDGE;

      ExecutorService userthreads = newCachedThreadPool();
      try {
         ExecutorService chainExecutor = MoreExecutors.sameThreadExecutor();
         ExecutorService listenerExecutor = MoreExecutors.sameThreadExecutor();

         checkThresholdsUsingCompose(expectedMin, expectedMax, expectedOverhead, chainExecutor, listenerExecutor);
      } finally {
         userthreads.shutdownNow();
      }
   }

   public static final int FUDGE = 5;
   public static final int COUNT = 100;
   public static final int IO_DURATION = 50;
   public static final int LISTENER_DURATION = 100;

   private void checkThresholdsUsingCompose(long expectedMin, long expectedMax, long expectedOverhead,
            ExecutorService chainExecutor, final ExecutorService listenerExecutor) {
      long start = System.currentTimeMillis();
      Map<String, Future<Long>> responses = newHashMap();
      for (int i = 0; i < COUNT; i++)
         responses.put(i + "", org.jclouds.concurrent.Futures.compose(org.jclouds.concurrent.Futures.makeListenable(
                  simultateIO(), chainExecutor), new Function<Long, Long>() {

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

   private Map<String, Future<Long>> runCallables(ExecutorService chainExecutor) {
      Map<String, Future<Long>> responses = newHashMap();
      for (int i = 0; i < COUNT; i++)
         responses.put(i + "", org.jclouds.concurrent.Futures.makeListenable(simultateIO(), chainExecutor));
      return responses;
   }

   private Future<Long> simultateIO() {
      return ioFunctionExecutor.submit(new Callable<Long>() {

         @Override
         public Long call() throws Exception {
            Thread.sleep(IO_DURATION);
            return System.currentTimeMillis();
         }

      });
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

   private static void checkTimeThresholds(long expectedMin, long expectedMax, long expectedOverhead, long start,
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
