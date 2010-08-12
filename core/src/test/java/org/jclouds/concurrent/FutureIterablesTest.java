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
import static org.jclouds.concurrent.FutureIterables.awaitCompletion;
import static org.jclouds.concurrent.FuturesTestingUtils.CALLABLE_DURATION;
import static org.jclouds.concurrent.FuturesTestingUtils.COUNT;
import static org.jclouds.concurrent.FuturesTestingUtils.FUDGE;
import static org.jclouds.concurrent.FuturesTestingUtils.checkTimeThresholds;
import static org.jclouds.concurrent.FuturesTestingUtils.runCallables;
import static org.testng.Assert.assertEquals;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.jclouds.logging.Logger;
import org.testng.annotations.Test;

/**
 * Tests behavior of FutureIterables
 * 
 * @author Adrian Cole
 */
@Test(enabled = false, groups = "performance", sequential = true, testName = "concurrent.FutureIterablesTest")
public class FutureIterablesTest {
   @Test(enabled = false)
   public void testMakeListenableDoesntSerializeFutures() throws InterruptedException, ExecutionException {
      long expectedMax = CALLABLE_DURATION;
      long expectedMin = CALLABLE_DURATION;
      long expectedOverhead = COUNT + FUDGE;

      ExecutorService callableExecutor = newCachedThreadPool();
      ExecutorService chainExecutor = MoreExecutors.sameThreadExecutor();

      long start = System.currentTimeMillis();
      Map<String, Future<Long>> responses = runCallables(callableExecutor, chainExecutor);
      checkTimeThresholds(expectedMin, expectedMax, expectedOverhead, start, responses);
   }

   @Test(enabled = false)
   public void testAwaitCompletionUsingSameThreadExecutorDoesntSerializeFutures() throws InterruptedException,
         ExecutionException {
      long expectedMax = CALLABLE_DURATION;
      long expectedMin = CALLABLE_DURATION;
      long expectedOverhead = COUNT + FUDGE;

      ExecutorService callableExecutor = newCachedThreadPool();
      ExecutorService chainExecutor = MoreExecutors.sameThreadExecutor();

      long start = System.currentTimeMillis();
      Map<String, Future<Long>> responses = runCallables(callableExecutor, chainExecutor);
      Map<String, Exception> exceptions = awaitCompletion(responses, MoreExecutors.sameThreadExecutor(), null,
            Logger.CONSOLE, "test same thread");
      assertEquals(exceptions.size(), 0);
      checkTimeThresholds(expectedMin, expectedMax, expectedOverhead, start, responses);
   }

}
