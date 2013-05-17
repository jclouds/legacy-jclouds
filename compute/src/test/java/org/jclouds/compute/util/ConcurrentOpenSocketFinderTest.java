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
package org.jclouds.compute.util;

import static com.google.common.base.Predicates.alwaysFalse;
import static com.google.common.base.Predicates.alwaysTrue;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;
import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.jclouds.compute.domain.NodeMetadata.Status.RUNNING;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.predicates.SocketOpen;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.ListeningExecutorService;

@Test(singleThreaded = true)
public class ConcurrentOpenSocketFinderTest {

   /**
    * prevente test failures on slow build slaves
    */
   private static final long SLOW_GRACE = 700;
   private static final long EARLY_GRACE = 10;

   private final NodeMetadata node = new NodeMetadataBuilder().id("myid")
                                                              .status(RUNNING)
                                                              .publicAddresses(ImmutableSet.of("1.2.3.4"))
                                                              .privateAddresses(ImmutableSet.of("1.2.3.5")).build();

   private final SocketOpen socketAlwaysClosed = new SocketOpen() {
      @Override
      public boolean apply(HostAndPort input) {
         return false;
      }
   };

   private final Predicate<AtomicReference<NodeMetadata>> nodeRunning = alwaysTrue();
   private final Predicate<AtomicReference<NodeMetadata>> nodeNotRunning = alwaysFalse();

   private ListeningExecutorService userExecutor;

   @BeforeClass
   public void setUp() {
      userExecutor = listeningDecorator(newCachedThreadPool());
   }

   @AfterClass(alwaysRun = true)
   public void tearDown() {
      if (userExecutor != null)
         userExecutor.shutdownNow();
   }

   @Test
   public void testRespectsTimeout() throws Exception {
      final long timeoutMs = 1000;

      OpenSocketFinder finder = new ConcurrentOpenSocketFinder(socketAlwaysClosed, nodeRunning, userExecutor);

      Stopwatch stopwatch = new Stopwatch();
      stopwatch.start();
      try {
         finder.findOpenSocketOnNode(node, 22, timeoutMs, MILLISECONDS);
         fail();
      } catch (NoSuchElementException success) {
         // expected
      }
      long timetaken = stopwatch.elapsed(MILLISECONDS);

      assertTrue(timetaken >= timeoutMs - EARLY_GRACE && timetaken <= timeoutMs + SLOW_GRACE, "timetaken=" + timetaken);

   }

   @Test
   public void testReturnsReachable() throws Exception {
      SocketOpen secondSocketOpen = new SocketOpen() {
         @Override
         public boolean apply(HostAndPort input) {
            return HostAndPort.fromParts("1.2.3.5", 22).equals(input);
         }
      };

      OpenSocketFinder finder = new ConcurrentOpenSocketFinder(secondSocketOpen, nodeRunning, userExecutor);

      HostAndPort result = finder.findOpenSocketOnNode(node, 22, 2000, MILLISECONDS);
      assertEquals(result, HostAndPort.fromParts("1.2.3.5", 22));

   }

   @Test
   public void testChecksSocketsConcurrently() throws Exception {
      ControllableSocketOpen socketTester = new ControllableSocketOpen(ImmutableMap.of(
            HostAndPort.fromParts("1.2.3.4", 22), new SlowCallable<Boolean>(true, 1500),
            HostAndPort.fromParts("1.2.3.5", 22), new SlowCallable<Boolean>(true, 1000)));

      OpenSocketFinder finder = new ConcurrentOpenSocketFinder(socketTester, nodeRunning, userExecutor);

      HostAndPort result = finder.findOpenSocketOnNode(node, 22, 2000, MILLISECONDS);
      assertEquals(result, HostAndPort.fromParts("1.2.3.5", 22));
   }

   @Test
   public void testAbortsWhenNodeNotRunning() throws Exception {

      OpenSocketFinder finder = new ConcurrentOpenSocketFinder(socketAlwaysClosed, nodeNotRunning, userExecutor) {
         @Override
         protected <T> Predicate<T> retryPredicate(final Predicate<T> findOrBreak, long timeout, long period,
               TimeUnit timeUnits) {
            return new Predicate<T>() {
               @Override
               public boolean apply(T input) {
                  try {
                     findOrBreak.apply(input);
                     fail("should have thrown IllegalStateException");
                  } catch (IllegalStateException e) {
                  }
                  return false;
               }
            };
         }
      };

      try {
         finder.findOpenSocketOnNode(node, 22, 2000, MILLISECONDS);
         fail();
      } catch (NoSuchElementException e) {
         // success
         // Note: don't get the "no longer running" message, because
         // logged+swallowed by RetryablePredicate
      }
   }

   private static class SlowCallable<T> implements Callable<T> {
      private final T result;
      private final long delay;

      SlowCallable(T result, long delay) {
         this.result = result;
         this.delay = delay;
      }

      @Override
      public T call() throws Exception {
         sleepUninterruptibly(delay, MILLISECONDS);
         return result;
      }
   };

   private static class ControllableSocketOpen implements SocketOpen {
      private final Map<HostAndPort, ? extends Callable<Boolean>> answers;

      ControllableSocketOpen(Map<HostAndPort, ? extends Callable<Boolean>> answers) {
         this.answers = answers;
      }

      @Override
      public boolean apply(HostAndPort input) {
         try {
            return answers.get(input).call();
         } catch (Exception e) {
            throw propagate(e);
         }
      }
   };
}
