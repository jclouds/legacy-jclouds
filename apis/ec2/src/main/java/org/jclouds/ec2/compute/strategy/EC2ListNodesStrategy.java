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

package org.jclouds.ec2.compute.strategy;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static org.jclouds.concurrent.FutureIterables.transformParallel;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.ec2.EC2AsyncClient;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.location.Region;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class EC2ListNodesStrategy implements ListNodesStrategy {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final EC2AsyncClient client;
   private final Set<String> regions;
   private final Function<RunningInstance, NodeMetadata> runningInstanceToNodeMetadata;
   private final ExecutorService executor;

   @Inject
   protected EC2ListNodesStrategy(EC2AsyncClient client, @Region Set<String> regions,
         Function<RunningInstance, NodeMetadata> runningInstanceToNodeMetadata,
         @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      this.client = client;
      this.regions = regions;
      this.runningInstanceToNodeMetadata = runningInstanceToNodeMetadata;
      this.executor = executor;
   }

   @Override
   public Set<? extends ComputeMetadata> listNodes() {
      return listDetailsOnNodesMatching(NodePredicates.all());
   }

   @Override
   public Set<? extends NodeMetadata> listDetailsOnNodesMatching(Predicate<ComputeMetadata> filter) {
      Iterable<? extends Set<? extends Reservation<? extends RunningInstance>>> reservations = transformParallel(
            regions, new Function<String, Future<Set<? extends Reservation<? extends RunningInstance>>>>() {

               @SuppressWarnings("unchecked")
               @Override
               public Future<Set<? extends Reservation<? extends RunningInstance>>> apply(String from) {
                  return (Future<Set<? extends Reservation<? extends RunningInstance>>>) client.getInstanceServices().describeInstancesInRegion(from);
               }

            }, executor, null, logger, "reservations");

      Iterable<? extends RunningInstance> instances = concat(concat(reservations));
      Iterable<? extends NodeMetadata> nodes = filter(transform(instances, runningInstanceToNodeMetadata), filter);
      return newLinkedHashSet(nodes);
   }
}
