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
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Predicates.or;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.size;
import static com.google.common.util.concurrent.Atomics.newReference;
import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;
import static java.lang.String.format;
import static org.jclouds.Constants.PROPERTY_USER_THREADS;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.util.Predicates2.retry;

import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.SocketOpen;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;

public class ConcurrentOpenSocketFinder implements OpenSocketFinder {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   private Logger logger = Logger.NULL;

   private final SocketOpen socketTester;
   private final Predicate<AtomicReference<NodeMetadata>> nodeRunning;
   private final ListeningExecutorService userExecutor;

   @Inject
   @VisibleForTesting
   ConcurrentOpenSocketFinder(SocketOpen socketTester,
         @Named(TIMEOUT_NODE_RUNNING) Predicate<AtomicReference<NodeMetadata>> nodeRunning,
         @Named(PROPERTY_USER_THREADS) ListeningExecutorService userExecutor) {
      this.socketTester = checkNotNull(socketTester, "socketTester");
      this.nodeRunning = checkNotNull(nodeRunning, "nodeRunning");
      this.userExecutor = listeningDecorator(checkNotNull(userExecutor, "userExecutor"));
   }

   @Override
   public HostAndPort findOpenSocketOnNode(NodeMetadata node, final int port, long timeout, TimeUnit timeUnits) {
      ImmutableSet<HostAndPort> sockets = checkNodeHasIps(node).transform(new Function<String, HostAndPort>() {

         @Override
         public HostAndPort apply(String from) {
            return HostAndPort.fromParts(from, port);
         }
      }).toSet();

      // Specify a retry period of 1s, expressed in the same time units.
      long period = timeUnits.convert(1, TimeUnit.SECONDS);

      // For retrieving the socket found (if any)
      AtomicReference<HostAndPort> result = newReference();

      Predicate<Iterable<HostAndPort>> findOrBreak = or(updateRefOnSocketOpen(result), throwISEIfNoLongerRunning(node));

      logger.debug(">> blocking on sockets %s for %d %s", sockets, timeout, timeUnits);
      boolean passed = retryPredicate(findOrBreak, timeout, period,  timeUnits).apply(sockets);

      if (passed) {
         logger.debug("<< socket %s opened", result);
         assert result.get() != null;
         return result.get();
      } else {
         logger.warn("<< sockets %s didn't open after %d %s", sockets, timeout, timeUnits);
         throw new NoSuchElementException(format("could not connect to any ip address port %d on node %s", port, node));
      }

   }

   @VisibleForTesting
   protected <T> Predicate<T> retryPredicate(Predicate<T> findOrBreak, long timeout, long period, TimeUnit timeUnits) {
      return retry(findOrBreak, timeout, period, timeUnits);
   }

   /**
    * Checks if any any of the given HostAndPorts are reachable. It checks them
    * all concurrently, and sets reference to a {@link HostAndPort} if found or
    * returns false;
    */
   private Predicate<Iterable<HostAndPort>> updateRefOnSocketOpen(final AtomicReference<HostAndPort> reachableSocket) {
      return new Predicate<Iterable<HostAndPort>>() {

         @Override
         public boolean apply(Iterable<HostAndPort> input) {

            Builder<ListenableFuture<?>> futures = ImmutableList.builder();
            for (final HostAndPort socket : input) {
               futures.add(userExecutor.submit(new Runnable() {

                  @Override
                  public void run() {
                     try {
                        if (socketTester.apply(socket)) {
                           // only set if the this socket was found first
                           reachableSocket.compareAndSet(null, socket);
                        }
                     } catch (RuntimeException e) {
                        logger.warn(e, "Error checking reachability of ip:port %s", socket);
                     }
                  }

               }));
            }
            blockOn(futures.build());
            return reachableSocket.get() != null;
         }

         @Override
         public String toString() {
            return "setAndReturnTrueIfSocketFound()";
         }
      };
   }

   /**
    * Add this via
    * {@code Predicates.or(condition, throwISEIfNoLongerRunning(node))} to
    * short-circuit {@link RetryablePredicate} looping when the node is no
    * longer running.
    */
   private <T> Predicate<T> throwISEIfNoLongerRunning(final NodeMetadata node) {
      return new Predicate<T>() {

         @Override
         public boolean apply(T input) {
            if (!nodeRunning.apply(newReference(node))) {
               throw new IllegalStateException(node.getId() + " is no longer running; aborting socket open loop");
            }
            return false;
         }

         @Override
         public String toString() {
            return "throwISEIfNoLongerRunning(" + node.getId() + ")";
         }
      };
   }

   private static FluentIterable<String> checkNodeHasIps(NodeMetadata node) {
      FluentIterable<String> ips = FluentIterable.from(concat(node.getPublicAddresses(), node.getPrivateAddresses()));
      checkState(size(ips) > 0, "node does not have IP addresses configured: " + node);
      return ips;
   }

   private static void blockOn(Iterable<ListenableFuture<?>> immutableList) {
      try {
         Futures.allAsList(immutableList).get();
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         throw propagate(e);
      } catch (ExecutionException e) {
         throw propagate(e);
      }
   }
}
