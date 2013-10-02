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

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.util.concurrent.Futures.immediateFailedFuture;
import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.jclouds.concurrent.FutureIterables.transformParallel;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.jclouds.logging.Logger;
import org.jclouds.rest.AuthorizationException;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * Tests behavior of FutureIterables
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", singleThreaded = true, testName = "FutureIterablesTest")
public class FutureIterablesTest {

   public void testAuthorizationExceptionPropagatesAndOnlyTriesOncePerElement() {
      final AtomicInteger counter = new AtomicInteger();

      try {
         transformParallel(ImmutableSet.of("hello", "goodbye"), new Function<String, ListenableFuture<? extends String>>() {
            public ListenableFuture<String> apply(String input) {
               counter.incrementAndGet();
               return immediateFailedFuture(new AuthorizationException());
            }
         }, sameThreadExecutor(), null, Logger.NULL, "");
         fail("Expected AuthorizationException");
      } catch (AuthorizationException e) {
         assertEquals(counter.get(), 2);
      }

   }

   public void testNormalExceptionPropagatesAsTransformParallelExceptionAndTries5XPerElement() {
      final AtomicInteger counter = new AtomicInteger();

      try {
         transformParallel(ImmutableSet.of("hello", "goodbye"), new Function<String, ListenableFuture<? extends String>>() {
            public ListenableFuture<String> apply(String input) {
               counter.incrementAndGet();
               return immediateFailedFuture(new RuntimeException());
            }
         }, sameThreadExecutor(), null, Logger.CONSOLE, "");
         fail("Expected TransformParallelException");
      } catch (TransformParallelException e) {
         assertEquals(e.getFromToException().size(), 2);
         assertEquals(counter.get(), 10);
      }

   }

   public void testAwaitCompletionTimeout() throws Exception {
      final long timeoutMs = 1000;
      ListeningExecutorService userExecutor = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
      Map<Void, ListenableFuture<?>> responses = newHashMap();
      try {
         responses.put(null, userExecutor.submit(new Runnable() {
            @Override
            public void run() {
               try {
                  Thread.sleep(2 * timeoutMs);
               } catch (InterruptedException ie) {
                  // triggered during shutdown
               }
            }
         }));
         Map<Void, Exception> errors = FutureIterables.awaitCompletion(responses, userExecutor, timeoutMs, Logger.NULL,
         /* prefix= */"");
         if (!errors.isEmpty()) {
            throw errors.values().iterator().next();
         }
         fail("Did not throw TimeoutException");
      } catch (TimeoutException te) {
         // expected
      } finally {
         userExecutor.shutdownNow();
      }
   }
}
