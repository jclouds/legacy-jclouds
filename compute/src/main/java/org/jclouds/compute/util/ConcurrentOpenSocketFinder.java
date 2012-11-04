package org.jclouds.compute.util;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.size;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.Atomics;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Inject;

public class ConcurrentOpenSocketFinder implements OpenSocketFinder {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   private Logger logger = Logger.NULL;

   private final SocketOpen socketTester;
   private final Predicate<AtomicReference<NodeMetadata>> nodeRunning;
   private final ListeningExecutorService executor;

   @Inject
   public ConcurrentOpenSocketFinder(SocketOpen socketTester, 
            @Named(TIMEOUT_NODE_RUNNING) final Predicate<AtomicReference<NodeMetadata>> nodeRunning,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService userThreads) {
      this.socketTester = socketTester;
      this.nodeRunning = nodeRunning;
      this.executor = MoreExecutors.listeningDecorator(userThreads);
   }

   public HostAndPort findOpenSocketOnNode(final NodeMetadata node, final int port, 
            long timeoutValue, TimeUnit timeUnits) {
      Iterable<String> hosts = checkNodeHasIps(node);
      Set<HostAndPort> sockets = toHostAndPorts(hosts, port);
      
      // Specify a retry period of 1s, expressed in the same time units.
      long period = timeUnits.convert(1, TimeUnit.SECONDS);

      // For storing the result; needed because predicate will just tell us true/false
      final AtomicReference<HostAndPort> result = Atomics.newReference();

      Predicate<Collection<HostAndPort>> concurrentOpenSocketFinder = new Predicate<Collection<HostAndPort>>() {

         @Override
         public boolean apply(Collection<HostAndPort> input) {
            HostAndPort reachableSocket = findOpenSocket(input);
            if (reachableSocket != null) {
               result.set(reachableSocket);
               return true;
            } else {
               if (nodeRunning != null && !nodeRunning.apply(new AtomicReference<NodeMetadata>(node))) {
                  throw new IllegalStateException(String.format("Node %s is no longer running; aborting waiting for ip:port connection", node.getId()));
               }
               return false;
            }
         }
         
      };
      
      RetryablePredicate<Collection<HostAndPort>> retryingOpenSocketFinder = new RetryablePredicate<Collection<HostAndPort>>(
               concurrentOpenSocketFinder, timeoutValue, period, timeUnits);

      logger.debug(">> blocking on sockets %s for %d %s", sockets, timeoutValue, timeUnits);

      boolean passed = retryingOpenSocketFinder.apply(sockets);
      
      if (passed) {
         logger.debug("<< socket %s opened", result);
         assert result.get() != null;
         return result.get();
      } else {
         logger.warn("<< sockets %s didn't open after %d %s", sockets, timeoutValue, timeUnits);
         throw new NoSuchElementException(String.format("could not connect to any ip address port %d on node %s", 
                  port, node));
      }

   }

   /**
    * Checks if any any of the given HostAndPorts are reachable. It checks them all concurrently,
    * and returns the first one found or null if none are reachable.
    * 
    * @return A reachable HostAndPort, or null.
    * @throws InterruptedException 
    */
   private HostAndPort findOpenSocket(final Collection<HostAndPort> sockets) {
      final AtomicReference<HostAndPort> result = Atomics.newReference();
      final CountDownLatch latch = new CountDownLatch(1);
      final AtomicInteger completeCount = new AtomicInteger();
      
      for (final HostAndPort socket : sockets) {
         final ListenableFuture<?> future = executor.submit(new Runnable() {

            @Override
            public void run() {
               try {
                  if (socketTester.apply(socket)) {
                     result.compareAndSet(null, socket);
                     latch.countDown();
                  }
               } catch (RuntimeException e) {
                  logger.warn(e, "Error checking reachability of ip:port %s", socket);
               }
            }
            
         });
         
         future.addListener(new Runnable() {

            @Override
            public void run() {
               if (completeCount.incrementAndGet() >= sockets.size()) {
                  latch.countDown(); // Tried all; mark as done
               }
            }
            
         }, MoreExecutors.sameThreadExecutor());
      }
      
      try {
         latch.await();
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         throw Throwables.propagate(e);
      }
      return result.get();
   }

   private Iterable<String> checkNodeHasIps(NodeMetadata node) {
      Iterable<String> ips = concat(node.getPublicAddresses(), node.getPrivateAddresses());
      checkState(size(ips) > 0, "node does not have IP addresses configured: " + node);
      return ips;
   }
   
   private Set<HostAndPort> toHostAndPorts(Iterable<String> hosts, final int port) {
      return ImmutableSet.copyOf(transform(hosts,
               new Function<String, HostAndPort>() {

                  @Override
                  public HostAndPort apply(String from) {
                     return HostAndPort.fromParts(from, port);
                  }
               }));
   }
}
