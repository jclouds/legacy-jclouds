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
package com.google.common.util.concurrent;

import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.concurrent.Executors.newCachedThreadPool;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * In google appengine, we can get a future without using an executorservice, using its async http
 * fetch command. However, we still may need to do some conversions, or add listeners. In
 * googleappengine, we cannot employ a *real* executorservice, but we can employ a same thread
 * executor. This test identifies efficiencies that can be made by strengthening guava's handling of
 * same thread execution.
 * 
 * <p/>
 * 
 * We simulate an i/o future by running a callable that simply sleeps. How this is created isn't
 * important.
 * 
 * <ol>
 * <li>{@code IO_DURATION} is the time that the source future spends doing work</li>
 * <li>{@code LISTENER_DURATION} is the time that the attached listener or function</li>
 * </ol>
 * 
 * The execution time of a transformd task within a composite should not be more than {@code
 * IO_DURATION} + {@code LISTENER_DURATION} + overhead when a threadpool is used. This is because
 * the listener should be invoked as soon as the result is available.
 * <p/>
 * The execution time of a transformd task within a composite should not be more than {@code
 * IO_DURATION} + {@code LISTENER_DURATION} * {@code COUNT} + overhead when caller thread is used
 * for handling the listeners.
 * <p/>
 * This test shows that Futures.transform eagerly issues a get() on the source future. code iterating
 * over futures and assigning listeners will take the same amount of time as calling get() on each
 * one, if using a within thread executor. This exposes an inefficiency which can make some use
 * cases in google appengine impossible to achieve within the cutoff limits.
 * 
 * @author Adrian Cole
 */
@Test(groups = "performance", enabled = false, singleThreaded = true, testName = "FuturesTransformPerformanceTest")
public class FuturesTransformPerformanceTest {
   private static final int FUDGE = 5;
   private static final int COUNT = 100;
   private static final int IO_DURATION = 50;
   private static final int LISTENER_DURATION = 100;

   ExecutorService ioFunctionExecutor = newCachedThreadPool();

   /**
    * When we use threadpools for both the chain and invoking listener, user experience is
    * consistent.
    */
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

         checkThresholdsUsingFuturesTransform(expectedMin, expectedMax, expectedOverhead, chainExecutor, listenerExecutor);
      } finally {
         userthreads.shutdownNow();
      }
   }

   /**
    * When we use threadpools for the chain, but same thread for invoking listener, user experience
    * is still consistent.
    */
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

         checkThresholdsUsingFuturesTransform(expectedMin, expectedMax, expectedOverhead, chainExecutor, listenerExecutor);
      } finally {
         userthreads.shutdownNow();
      }
   }

   /**
    * When using same thread for the chain, the futures are being called (get()) eagerly, resulting
    * in the max duration being the sum of all i/o plus the cost of executing the listeners. In this
    * case, listeners are executed in a different thread pool.
    * 
    */
   @Test(enabled = false)
   public void whenSameThreadIsUsedForChainButCachedThreadPoolForListenerMaxDurationIsSumOfAllIOAndOneListener()
            throws InterruptedException, ExecutionException {
      long expectedMax = (IO_DURATION * COUNT) + LISTENER_DURATION;
      long expectedMin = IO_DURATION + LISTENER_DURATION;
      long expectedOverhead = COUNT + FUDGE;

      ExecutorService userthreads = newCachedThreadPool();
      try {
         ExecutorService chainExecutor = MoreExecutors.sameThreadExecutor();
         ExecutorService listenerExecutor = userthreads;

         checkThresholdsUsingFuturesTransform(expectedMin, expectedMax, expectedOverhead, chainExecutor, listenerExecutor);
      } finally {
         userthreads.shutdownNow();
      }
   }

   /**
    * This case can be optimized for sure. The side effect of the eager get() is that all i/o must
    * complete before *any* listeners are run. In this case, if you are inside google appengine and
    * using same thread executors, worst experience is sum of all io duration plus the sum of all
    * listener duration. An efficient implementation would call get() on the i/o future lazily. Such
    * an impl would have a max duration of I/O + Listener * count.
    */
   @Test(enabled = false)
   public void whenSameThreadIsUsedForChainAndListenerMaxDurationIsSumOfAllIOAndAllListeners()
            throws InterruptedException, ExecutionException {

      long expectedMax = (IO_DURATION * COUNT) + (LISTENER_DURATION * COUNT);
      long expectedMin = IO_DURATION + LISTENER_DURATION;
      long expectedOverhead = COUNT + FUDGE;

      ExecutorService userthreads = newCachedThreadPool();
      try {
         ExecutorService chainExecutor = MoreExecutors.sameThreadExecutor();
         ExecutorService listenerExecutor = MoreExecutors.sameThreadExecutor();

         checkThresholdsUsingFuturesTransform(expectedMin, expectedMax, expectedOverhead, chainExecutor, listenerExecutor);
      } finally {
         userthreads.shutdownNow();
      }
   }

   private void checkThresholdsUsingFuturesTransform(long expectedMin, long expectedMax, long expectedOverhead,
            ExecutorService chainExecutor, final ExecutorService listenerExecutor) {
      long start = System.currentTimeMillis();
      Map<String, Future<Long>> responses = newHashMap();
      for (int i = 0; i < COUNT; i++)
         responses.put(i + "", Futures.transform(JdkFutureAdapters.listenInPoolThread(simultateIO(), chainExecutor),
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

   private Future<Long> simultateIO() {
      return ioFunctionExecutor.submit(new Callable<Long>() {

         @Override
         public Long call() throws Exception {
            Thread.sleep(IO_DURATION);
            return System.currentTimeMillis();
         }

      });
   }

   private static long getMaxIn(Map<String, Future<Long>> responses) {
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

   private static long getMinIn(Map<String, Future<Long>> responses) {
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
