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

import static java.util.concurrent.Executors.newCachedThreadPool;
import static org.jclouds.concurrent.FuturesTestingUtils.CALLABLE_DURATION;
import static org.jclouds.concurrent.FuturesTestingUtils.COUNT;
import static org.jclouds.concurrent.FuturesTestingUtils.FUDGE;
import static org.jclouds.concurrent.FuturesTestingUtils.LISTENER_DURATION;
import static org.jclouds.concurrent.FuturesTestingUtils.checkThresholdsUsingConcurrentUtilsCompose;
import static org.jclouds.concurrent.FuturesTestingUtils.checkThresholdsUsingFuturesCompose;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import org.testng.annotations.Test;

/**
 * 
 * 
 * <p/>
 * All of these tests simulate a future by invoking callables in a separate executor. The point of
 * this test is to see what happens when we chain futures together.
 * 
 * <ol>
 * <li>{@code CALLABLE_DURATION} is the time that the source future spends doing work</li>
 * <li>{@code LISTENER_DURATION} is the time that the attached listener or function</li>
 * </ol>
 * 
 * The execution time of a composed task within a composite should not be more than {@code
 * CALLABLE_DURATION} + {@code LISTENER_DURATION} + overhead when a threadpool is used. This is
 * because the listener should be invoked as soon as the result is available.
 * <p/>
 * The execution time of a composed task within a composite should not be more than {@code
 * CALLABLE_DURATION} + {@code LISTENER_DURATION} * {@code COUNT} + overhead when caller thread is
 * used for handling the listeners.
 * <p/>
 * ConcurrentUtils overcomes a shortcoming found in Google Guava r06, where Futures.compose eagerly
 * issues a get() on the source future. This has the effect of serializing the futures as you
 * iterate. It overcomes this by tagging the ExecutorService we associate with sameThread execution
 * and lazy convert values accordingly.
 * 
 * @author Adrian Cole
 */
@Test(enabled = false, groups = "performance", sequential = true)
public class FuturesComposePerformanceTest {
   ExecutorService callableExecutor = newCachedThreadPool();

   /**
    * When Futures.compose is
    */
   @Test(enabled = false)
   public void testFuturesCompose1() throws InterruptedException, ExecutionException {
      long expectedMax = CALLABLE_DURATION + LISTENER_DURATION;
      long expectedMin = CALLABLE_DURATION + LISTENER_DURATION;
      long expectedOverhead = COUNT * 4 + FUDGE;

      ExecutorService userthreads = newCachedThreadPool();
      try {
         ExecutorService chainExecutor = userthreads;
         ExecutorService listenerExecutor = userthreads;

         checkThresholdsUsingFuturesCompose(expectedMin, expectedMax, expectedOverhead, callableExecutor,
                  chainExecutor, listenerExecutor);
      } finally {
         userthreads.shutdownNow();
      }
   }

   @Test(enabled = false)
   public void testFuturesCompose2() throws InterruptedException, ExecutionException {
      long expectedMax = CALLABLE_DURATION + LISTENER_DURATION;
      long expectedMin = CALLABLE_DURATION + LISTENER_DURATION;
      long expectedOverhead = COUNT + FUDGE;

      ExecutorService userthreads = newCachedThreadPool();
      try {
         ExecutorService chainExecutor = userthreads;
         ExecutorService listenerExecutor = MoreExecutors.sameThreadExecutor();

         checkThresholdsUsingFuturesCompose(expectedMin, expectedMax, expectedOverhead, callableExecutor,
                  chainExecutor, listenerExecutor);
      } finally {
         userthreads.shutdownNow();
      }
   }

   @Test(enabled = false)
   public void testFuturesCompose3() throws InterruptedException, ExecutionException {
      long expectedMax = (CALLABLE_DURATION * COUNT) + LISTENER_DURATION;
      long expectedMin = CALLABLE_DURATION + LISTENER_DURATION;
      long expectedOverhead = COUNT + FUDGE;

      ExecutorService userthreads = newCachedThreadPool();
      try {
         ExecutorService chainExecutor = MoreExecutors.sameThreadExecutor();
         ExecutorService listenerExecutor = userthreads;

         checkThresholdsUsingFuturesCompose(expectedMin, expectedMax, expectedOverhead, callableExecutor,
                  chainExecutor, listenerExecutor);
      } finally {
         userthreads.shutdownNow();
      }
   }

   @Test(enabled = false)
   public void testFuturesCompose4() throws InterruptedException, ExecutionException {

      long expectedMax = (CALLABLE_DURATION * COUNT) + (LISTENER_DURATION * COUNT);
      long expectedMin = CALLABLE_DURATION + LISTENER_DURATION;
      long expectedOverhead = COUNT + FUDGE;

      ExecutorService userthreads = newCachedThreadPool();
      try {
         ExecutorService chainExecutor = MoreExecutors.sameThreadExecutor();
         ExecutorService listenerExecutor = MoreExecutors.sameThreadExecutor();

         checkThresholdsUsingFuturesCompose(expectedMin, expectedMax, expectedOverhead, callableExecutor,
                  chainExecutor, listenerExecutor);
      } finally {
         userthreads.shutdownNow();
      }
   }

   @Test(enabled = false)
   public void testConcurrentUtilsCompose1() throws InterruptedException, ExecutionException {
      long expectedMax = CALLABLE_DURATION + LISTENER_DURATION;
      long expectedMin = CALLABLE_DURATION + LISTENER_DURATION;
      long expectedOverhead = COUNT * 4 + FUDGE;

      ExecutorService userthreads = newCachedThreadPool();
      try {
         ExecutorService chainExecutor = userthreads;
         ExecutorService listenerExecutor = userthreads;

         checkThresholdsUsingConcurrentUtilsCompose(expectedMin, expectedMax, expectedOverhead, callableExecutor,
                  chainExecutor, listenerExecutor);
      } finally {
         userthreads.shutdownNow();
      }
   }

   @Test(enabled = false)
   public void testConcurrentUtilsCompose2() throws InterruptedException, ExecutionException {
      long expectedMax = CALLABLE_DURATION + LISTENER_DURATION;
      long expectedMin = CALLABLE_DURATION + LISTENER_DURATION;
      long expectedOverhead = COUNT + FUDGE;

      ExecutorService userthreads = newCachedThreadPool();
      try {
         ExecutorService chainExecutor = userthreads;
         ExecutorService listenerExecutor = MoreExecutors.sameThreadExecutor();

         checkThresholdsUsingConcurrentUtilsCompose(expectedMin, expectedMax, expectedOverhead, callableExecutor,
                  chainExecutor, listenerExecutor);
      } finally {
         userthreads.shutdownNow();
      }
   }

   @Test(enabled = false)
   public void testConcurrentUtilsCompose3() throws InterruptedException, ExecutionException {
      long expectedMax = CALLABLE_DURATION + (LISTENER_DURATION * COUNT);
      long expectedMin = CALLABLE_DURATION + LISTENER_DURATION;
      long expectedOverhead = COUNT + FUDGE;

      ExecutorService userthreads = newCachedThreadPool();
      try {
         ExecutorService chainExecutor = MoreExecutors.sameThreadExecutor();
         ExecutorService listenerExecutor = userthreads;

         checkThresholdsUsingConcurrentUtilsCompose(expectedMin, expectedMax, expectedOverhead, callableExecutor,
                  chainExecutor, listenerExecutor);
      } finally {
         userthreads.shutdownNow();
      }
   }

   @Test(enabled = false)
   public void testConcurrentUtilsCompose4() throws InterruptedException, ExecutionException {

      long expectedMax = CALLABLE_DURATION + (LISTENER_DURATION * COUNT);
      long expectedMin = CALLABLE_DURATION;
      long expectedOverhead = COUNT + FUDGE;

      ExecutorService userthreads = newCachedThreadPool();
      try {
         ExecutorService chainExecutor = MoreExecutors.sameThreadExecutor();
         ExecutorService listenerExecutor = MoreExecutors.sameThreadExecutor();

         checkThresholdsUsingConcurrentUtilsCompose(expectedMin, expectedMax, expectedOverhead, callableExecutor,
                  chainExecutor, listenerExecutor);
      } finally {
         userthreads.shutdownNow();
      }
   }

}
