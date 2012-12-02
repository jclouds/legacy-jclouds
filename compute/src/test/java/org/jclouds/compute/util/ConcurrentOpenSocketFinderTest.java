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
package org.jclouds.compute.util;

import static com.google.common.base.Predicates.alwaysFalse;
import static com.google.common.base.Predicates.alwaysTrue;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import org.easymock.EasyMock;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.predicates.SocketOpen;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.HostAndPort;

@Test(singleThreaded = true)
public class ConcurrentOpenSocketFinderTest {

   private static final long SLOW_GRACE = 500;
   private static final long EARLY_GRACE = 10;

   private final NodeMetadata node = new NodeMetadataBuilder().id("myid").status(NodeMetadata.Status.RUNNING)
         .publicAddresses(ImmutableSet.of("1.2.3.4")).privateAddresses(ImmutableSet.of("1.2.3.5")).build();

   private final Predicate<AtomicReference<NodeMetadata>> alwaysTrue = alwaysTrue();
   private final Predicate<AtomicReference<NodeMetadata>> alwaysFalse = alwaysFalse();

   private SocketOpen socketTester;
   private ExecutorService threadPool;

   @BeforeMethod
   public void setUp() {
      socketTester = createMock(SocketOpen.class);
      threadPool = Executors.newCachedThreadPool();
   }

   @AfterMethod(alwaysRun = true)
   public void tearDown() {
      if (threadPool != null)
         threadPool.shutdownNow();
   }

   @Test
   public void testRespectsTimeout() throws Exception {
      final long timeoutMs = 1000;

      expect(socketTester.apply(HostAndPort.fromParts("1.2.3.4", 22))).andReturn(false).times(2, Integer.MAX_VALUE);
      expect(socketTester.apply(HostAndPort.fromParts("1.2.3.5", 22))).andReturn(false).times(2, Integer.MAX_VALUE);
      replay(socketTester);

      OpenSocketFinder finder = new ConcurrentOpenSocketFinder(socketTester, alwaysTrue, threadPool);

      Stopwatch stopwatch = new Stopwatch();
      stopwatch.start();
      try {
         finder.findOpenSocketOnNode(node, 22, timeoutMs, MILLISECONDS);
         fail();
      } catch (NoSuchElementException success) {
         // expected
      }
      long timetaken = stopwatch.elapsedMillis();

      assertTrue(timetaken >= timeoutMs - EARLY_GRACE && timetaken <= timeoutMs + SLOW_GRACE, "timetaken=" + timetaken);

      verify(socketTester);
   }

   @Test
   public void testReturnsReachable() throws Exception {
      expect(socketTester.apply(HostAndPort.fromParts("1.2.3.4", 22))).andReturn(false).once();
      expect(socketTester.apply(HostAndPort.fromParts("1.2.3.5", 22))).andReturn(true).once();
      replay(socketTester);

      OpenSocketFinder finder = new ConcurrentOpenSocketFinder(socketTester, alwaysTrue, threadPool);

      HostAndPort result = finder.findOpenSocketOnNode(node, 22, 2000, MILLISECONDS);
      assertEquals(result, HostAndPort.fromParts("1.2.3.5", 22));

      verify(socketTester);
   }

   @Test
   public void testChecksSocketsConcurrently() throws Exception {
      // Can't use mock+answer for concurrency tests; EasyMock uses lock in
      // ReplayState
      ControllableSocketOpen socketTester = new ControllableSocketOpen(ImmutableMap.of(
            HostAndPort.fromParts("1.2.3.4", 22), new SlowCallable<Boolean>(true, 1500),
            HostAndPort.fromParts("1.2.3.5", 22), new SlowCallable<Boolean>(true, 1000)));

      OpenSocketFinder finder = new ConcurrentOpenSocketFinder(socketTester, alwaysTrue, threadPool);

      HostAndPort result = finder.findOpenSocketOnNode(node, 22, 2000, MILLISECONDS);
      assertEquals(result, HostAndPort.fromParts("1.2.3.5", 22));
   }

   @Test
   public void testAbortsWhenNodeNotRunning() throws Exception {
      expect(socketTester.apply(EasyMock.<HostAndPort> anyObject())).andReturn(false);
      replay(socketTester);

      OpenSocketFinder finder = new ConcurrentOpenSocketFinder(socketTester, alwaysFalse, threadPool);

      Stopwatch stopwatch = new Stopwatch();
      stopwatch.start();
      try {
         finder.findOpenSocketOnNode(node, 22, 2000, MILLISECONDS);
         fail();
      } catch (NoSuchElementException e) {
         // success
         // Note: don't get the "no longer running" message, because
         // logged+swallowed by RetryablePredicate
      }
      long timetaken = stopwatch.elapsedMillis();

      assertTrue(timetaken <= SLOW_GRACE, "timetaken=" + timetaken);

      verify(socketTester);
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
