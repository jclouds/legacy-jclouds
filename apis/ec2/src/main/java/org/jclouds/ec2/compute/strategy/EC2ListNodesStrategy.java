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
package org.jclouds.ec2.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.in;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.toArray;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Multimaps.filterKeys;
import static com.google.common.collect.Multimaps.index;
import static com.google.common.collect.Multimaps.transformValues;
import static org.jclouds.concurrent.FutureIterables.transformParallel;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.aws.util.AWSUtils;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.location.Region;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class EC2ListNodesStrategy implements ListNodesStrategy {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   @Inject(optional = true)
   @Named(Constants.PROPERTY_REQUEST_TIMEOUT)
   protected static Long maxTime;

   protected final EC2Client client;
   protected final Supplier<Set<String>> regions;
   protected final Function<RunningInstance, NodeMetadata> runningInstanceToNodeMetadata;
   protected final ListeningExecutorService userExecutor;

   @Inject
   protected EC2ListNodesStrategy(EC2Client client, @Region Supplier<Set<String>> regions,
            Function<RunningInstance, NodeMetadata> runningInstanceToNodeMetadata,
            @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor) {
      this.client =  checkNotNull(client, "client");
      this.regions =  checkNotNull(regions, "regions");
      this.runningInstanceToNodeMetadata = checkNotNull(runningInstanceToNodeMetadata, "runningInstanceToNodeMetadata");
      this.userExecutor =  checkNotNull(userExecutor, "userExecutor");
   }

   @Override
   public Set<? extends ComputeMetadata> listNodes() {
      return listDetailsOnNodesMatching(NodePredicates.all());
   }

   @Override
   public Set<? extends NodeMetadata> listNodesByIds(Iterable<String> ids) {
      Multimap<String, String> idsByHandles = index(ids, splitHandle(1));
      Multimap<String, String> idsByRegions = transformValues(idsByHandles, splitHandle(0));
      Multimap<String, String> idsByConfiguredRegions = filterKeys(idsByRegions, in(regions.get()));

      if (idsByConfiguredRegions.isEmpty()) {
         return ImmutableSet.of();
      }
      
      Iterable<? extends RunningInstance> instances = pollRunningInstancesByRegionsAndIds(idsByConfiguredRegions);
      Iterable<? extends NodeMetadata> nodes = transform(filter(instances, notNull()),
                                                         runningInstanceToNodeMetadata);
      return ImmutableSet.copyOf(nodes);
   }

   @Override
   public Set<? extends NodeMetadata> listDetailsOnNodesMatching(Predicate<ComputeMetadata> filter) {
      Iterable<? extends RunningInstance> instances = pollRunningInstances();
      Iterable<? extends NodeMetadata> nodes = filter(transform(filter(instances, notNull()),
               runningInstanceToNodeMetadata), and(notNull(), filter));
      return ImmutableSet.copyOf(nodes);
   }

   protected Iterable<? extends RunningInstance> pollRunningInstances() {
      Iterable<? extends Set<? extends Reservation<? extends RunningInstance>>> reservations
         = transform(regions.get(), allInstancesInRegion());
      
      return concat(concat(reservations));
   }

   protected Iterable<? extends RunningInstance> pollRunningInstancesByRegionsAndIds(final Multimap<String,String> idsByRegions) {
      Iterable<? extends Set<? extends Reservation<? extends RunningInstance>>> reservations
         = transform(idsByRegions.keySet(), instancesByIdInRegion(idsByRegions));
      
      return concat(concat(reservations));
   }

   protected Function<String, String> splitHandle(final int pos) {
      return new Function<String, String>() {

         @Override
         public String apply(String handle) {
            return AWSUtils.parseHandle(handle)[pos];
         }
      };
   }

   protected Function<String, Set<? extends Reservation<? extends RunningInstance>>> allInstancesInRegion() {
      return new Function<String, Set<? extends Reservation<? extends RunningInstance>>>() {
         
         @Override
         public Set<? extends Reservation<? extends RunningInstance>> apply(String from) {
            return client.getInstanceServices().describeInstancesInRegion(from);
         }
         
      };
   }

   protected Function<String, Set<? extends Reservation<? extends RunningInstance>>>
                                                                  instancesByIdInRegion(final Multimap<String,String> idsByRegions) {
      return new Function<String, Set<? extends Reservation<? extends RunningInstance>>>() {
                 
         @Override
         public Set<? extends Reservation<? extends RunningInstance>> apply(String from) {
            return client.getInstanceServices()
               .describeInstancesInRegion(from, toArray(idsByRegions.get(from), String.class));
         }
         
      };
   }
}
