package org.jclouds.compute.util;

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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.easymock.EasyMock;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.concurrent.MoreExecutors;
import org.jclouds.predicates.SocketOpen;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Stopwatch;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.HostAndPort;

@Test(singleThreaded=true)
public class ConcurrentOpenSocketFinderTest {

   private static final long SLOW_GRACE = 500;
   private static final long EARLY_GRACE = 10;

   private NodeMetadata node;
   private SocketOpen socketTester;
   private Predicate<AtomicReference<NodeMetadata>> nodeRunning;
   private ExecutorService threadPool;
   
   @SuppressWarnings("unchecked")
   @BeforeMethod
   public void setUp() {
      node = createMock(NodeMetadata.class);
      expect(node.getPublicAddresses()).andReturn(ImmutableSet.of("1.2.3.4")).atLeastOnce();
      expect(node.getPrivateAddresses()).andReturn(ImmutableSet.of("1.2.3.5")).atLeastOnce();
      expect(node.getId()).andReturn("myid").anyTimes();

      socketTester = createMock(SocketOpen.class);
      
      nodeRunning = createMock(Predicate.class);

      replay(node);
      
      threadPool = Executors.newCachedThreadPool();
   }

   @AfterMethod(alwaysRun=true)
   public void tearDown() {
      if (threadPool != null) threadPool.shutdownNow();
   }

   @Test
   public void testRespectsTimeout() throws Exception {
      final long timeoutMs = 1000;

      expect(socketTester.apply(HostAndPort.fromParts("1.2.3.4", 22))).andReturn(false).times(2, Integer.MAX_VALUE);
      expect(socketTester.apply(HostAndPort.fromParts("1.2.3.5", 22))).andReturn(false).times(2, Integer.MAX_VALUE);
      expect(nodeRunning.apply(EasyMock.<AtomicReference<NodeMetadata>>anyObject())).andReturn(true);
      replay(socketTester);
      replay(nodeRunning);
      
      OpenSocketFinder finder = new ConcurrentOpenSocketFinder(socketTester, null, MoreExecutors.sameThreadExecutor());

      Stopwatch stopwatch = new Stopwatch();
      stopwatch.start();
      try {
         finder.findOpenSocketOnNode(node, 22, timeoutMs, TimeUnit.MILLISECONDS);
         fail();
      } catch (NoSuchElementException success) {
         // expected
      }
      long timetaken = stopwatch.elapsedMillis();
      
      assertTrue(timetaken >= timeoutMs-EARLY_GRACE && timetaken <= timeoutMs+SLOW_GRACE, "timetaken="+timetaken);
      
      verify(node);
      verify(socketTester);
   }
   
   @Test
   public void testReturnsReachable() throws Exception {
      expect(socketTester.apply(HostAndPort.fromParts("1.2.3.4", 22))).andReturn(false).once();
      expect(socketTester.apply(HostAndPort.fromParts("1.2.3.5", 22))).andReturn(true).once();
      expect(nodeRunning.apply(EasyMock.<AtomicReference<NodeMetadata>>anyObject())).andReturn(true);
      replay(socketTester);
      replay(nodeRunning);

      OpenSocketFinder finder = new ConcurrentOpenSocketFinder(socketTester, null, MoreExecutors.sameThreadExecutor());

      HostAndPort result = finder.findOpenSocketOnNode(node, 22, 2000, TimeUnit.MILLISECONDS);
      assertEquals(result, HostAndPort.fromParts("1.2.3.5", 22));

      verify(node);
      verify(socketTester);
   }
   
   @Test
   public void testChecksSocketsConcurrently() throws Exception {
      long delayForReachableMs = 25;
      
      expect(nodeRunning.apply(EasyMock.<AtomicReference<NodeMetadata>>anyObject())).andReturn(true);
      replay(nodeRunning);

      // Can't use mock+answer for concurrency tests; EasyMock uses lock in ReplayState
      ControllableSocketOpen socketTester = new ControllableSocketOpen(ImmutableMap.of(
                              HostAndPort.fromParts("1.2.3.4", 22), new SlowCallable<Boolean>(false, 1000),
                              HostAndPort.fromParts("1.2.3.5", 22), new SlowCallable<Boolean>(true, delayForReachableMs)));
      
      OpenSocketFinder finder = new ConcurrentOpenSocketFinder(socketTester, null, threadPool);

      Stopwatch stopwatch = new Stopwatch();
      stopwatch.start();
      HostAndPort result = finder.findOpenSocketOnNode(node, 22, 2000, TimeUnit.MILLISECONDS);
      long timetaken = stopwatch.elapsedMillis();
      
      assertEquals(result, HostAndPort.fromParts("1.2.3.5", 22));
      assertTrue(timetaken >= delayForReachableMs-EARLY_GRACE && timetaken <= delayForReachableMs+SLOW_GRACE, "timetaken="+timetaken);
      verify(node);
   }
   
   @Test
   public void testAbortsWhenNodeNotRunning() throws Exception {
      expect(socketTester.apply(EasyMock.<HostAndPort>anyObject())).andReturn(false);
      expect(nodeRunning.apply(EasyMock.<AtomicReference<NodeMetadata>>anyObject())).andReturn(false);
      replay(socketTester);
      replay(nodeRunning);
      
      OpenSocketFinder finder = new ConcurrentOpenSocketFinder(socketTester, nodeRunning, MoreExecutors.sameThreadExecutor());
      
      Stopwatch stopwatch = new Stopwatch();
      stopwatch.start();
      try {
         finder.findOpenSocketOnNode(node, 22, 2000, TimeUnit.MILLISECONDS);
         fail();
      } catch (NoSuchElementException e) {
         // success
         // Note: don't get the "no longer running" message, because logged+swallowed by RetryablePredicate 
      }
      long timetaken = stopwatch.elapsedMillis();
      
      assertTrue(timetaken <= SLOW_GRACE, "timetaken="+timetaken);

      verify(node);
      verify(socketTester);
      verify(nodeRunning);
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
         Thread.sleep(delay);
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
            throw Throwables.propagate(e);
         }
      }
   };
}
