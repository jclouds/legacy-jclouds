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

import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static org.jclouds.concurrent.FuturesTestingUtils.CALLABLE_DURATION;
import static org.jclouds.concurrent.FuturesTestingUtils.COUNT;
import static org.jclouds.concurrent.FuturesTestingUtils.FUDGE;
import static org.jclouds.concurrent.FuturesTestingUtils.LISTENER_DURATION;
import static org.jclouds.concurrent.FuturesTestingUtils.checkThresholds;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import org.testng.annotations.Test;

/**
 * Tests behavior of ConcurrentUtils
 * 
 * @author Adrian Cole
 */
@Test(groups = "performance", sequential = true, testName = "concurrent.ConcurrentUtilsTest")
public class FuturesComposePerformanceTest {
   ExecutorService callableExecutor = newCachedThreadPool();

   public void test1() throws InterruptedException, ExecutionException {
      long expectedMax = CALLABLE_DURATION + LISTENER_DURATION;
      long expectedMin = CALLABLE_DURATION + LISTENER_DURATION;
      long expectedOverhead = COUNT * 4 + FUDGE;

      ExecutorService chainExecutor = callableExecutor;
      ExecutorService listenerExecutor = callableExecutor;

      checkThresholds(expectedMin, expectedMax, expectedOverhead, callableExecutor, chainExecutor, listenerExecutor);
   }

   public void test2() throws InterruptedException, ExecutionException {
      long expectedMax = CALLABLE_DURATION + LISTENER_DURATION;
      long expectedMin = CALLABLE_DURATION + LISTENER_DURATION;
      long expectedOverhead = COUNT + FUDGE;

      ExecutorService chainExecutor = callableExecutor;
      ExecutorService listenerExecutor = sameThreadExecutor();

      checkThresholds(expectedMin, expectedMax, expectedOverhead, callableExecutor, chainExecutor, listenerExecutor);
   }

   public void test3() throws InterruptedException, ExecutionException {
      long expectedMax = (CALLABLE_DURATION * COUNT) + LISTENER_DURATION;
      long expectedMin = CALLABLE_DURATION + LISTENER_DURATION;
      long expectedOverhead = COUNT + FUDGE;

      ExecutorService chainExecutor = sameThreadExecutor();
      ExecutorService listenerExecutor = callableExecutor;

      checkThresholds(expectedMin, expectedMax, expectedOverhead, callableExecutor, chainExecutor, listenerExecutor);
   }

   public void test4() throws InterruptedException, ExecutionException {

      long expectedMax = (CALLABLE_DURATION * COUNT) + (LISTENER_DURATION * COUNT);
      long expectedMin = CALLABLE_DURATION + LISTENER_DURATION;
      long expectedOverhead = COUNT + FUDGE;

      ExecutorService chainExecutor = sameThreadExecutor();
      ExecutorService listenerExecutor = sameThreadExecutor();

      checkThresholds(expectedMin, expectedMax, expectedOverhead, callableExecutor, chainExecutor, listenerExecutor);
   }

}
