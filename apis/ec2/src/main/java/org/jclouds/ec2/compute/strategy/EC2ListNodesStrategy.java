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
package org.jclouds.ec2.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.concurrent.FutureIterables.transformParallel;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.ec2.EC2AsyncClient;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.location.Region;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class EC2ListNodesStrategy implements ListNodesStrategy {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final EC2AsyncClient client;
   protected final Supplier<Set<String>> regions;
   protected final Function<RunningInstance, NodeMetadata> runningInstanceToNodeMetadata;
   protected final ExecutorService executor;

   @Inject
   protected EC2ListNodesStrategy(EC2AsyncClient client, @Region Supplier<Set<String>> regions,
            Function<RunningInstance, NodeMetadata> runningInstanceToNodeMetadata,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      this.client =  checkNotNull(client, "client");
      this.regions =  checkNotNull(regions, "regions");
      this.runningInstanceToNodeMetadata = checkNotNull(runningInstanceToNodeMetadata, "runningInstanceToNodeMetadata");
      this.executor =  checkNotNull(executor, "executor");
   }

   @Override
   public Set<? extends ComputeMetadata> listNodes() {
      return listDetailsOnNodesMatching(NodePredicates.all());
   }

   @Override
   public Set<? extends NodeMetadata> listDetailsOnNodesMatching(Predicate<ComputeMetadata> filter) {
      Iterable<? extends RunningInstance> instances = pollRunningInstances();
      Iterable<? extends NodeMetadata> nodes = filter(transform(filter(instances, notNull()),
               runningInstanceToNodeMetadata), and(notNull(), filter));
      return ImmutableSet.copyOf(nodes);
   }

   protected Iterable<? extends RunningInstance> pollRunningInstances() {
      Iterable<? extends Set<? extends Reservation<? extends RunningInstance>>> reservations = transformParallel(
               regions.get(), new Function<String, Future<? extends Set<? extends Reservation<? extends RunningInstance>>>>() {

                  @Override
                  public Future<Set<? extends Reservation<? extends RunningInstance>>> apply(String from) {
                     // see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=7126754
                     return castToSpecificTypedFuture(client.getInstanceServices().describeInstancesInRegion(from));
                  }

               }, executor, null, logger, "reservations");

      return concat(concat(reservations));
   }

   @SuppressWarnings("unchecked")
   private static <T> Future<T> castToSpecificTypedFuture(Future<? extends T> input) {
       return (Future<T>) input;
   }
}
