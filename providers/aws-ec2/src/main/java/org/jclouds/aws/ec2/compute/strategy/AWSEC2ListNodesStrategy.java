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
package org.jclouds.aws.ec2.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.toArray;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.concurrent.FutureIterables.transformParallel;

import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.aws.ec2.domain.AWSRunningInstance;
import org.jclouds.aws.ec2.domain.SpotInstanceRequest;
import org.jclouds.aws.ec2.functions.SpotInstanceRequestToAWSRunningInstance;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.ec2.compute.strategy.EC2ListNodesStrategy;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.location.Region;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
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
public class AWSEC2ListNodesStrategy extends EC2ListNodesStrategy {

   protected final AWSEC2Client client;
   protected final SpotInstanceRequestToAWSRunningInstance spotConverter;

   @Inject
   protected AWSEC2ListNodesStrategy(AWSEC2Client client, @Region Supplier<Set<String>> regions,
            Function<RunningInstance, NodeMetadata> runningInstanceToNodeMetadata,
            @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
            SpotInstanceRequestToAWSRunningInstance spotConverter) {
      super(client, regions, runningInstanceToNodeMetadata, userExecutor);
      this.client = checkNotNull(client, "client");
      this.spotConverter = checkNotNull(spotConverter, "spotConverter");
   }

   @Override
   protected Iterable<? extends RunningInstance> pollRunningInstances() {
      Iterable<? extends AWSRunningInstance> spots = filter(transform(concat(transform(regions.get(),
                                                                                       allSpotInstancesInRegion())),
                                                                      spotConverter), notNull());

      return concat(super.pollRunningInstances(), spots);
   }

   @Override
   protected Iterable<? extends RunningInstance> pollRunningInstancesByRegionsAndIds(final Multimap<String,String> idsByRegions) {
      Iterable<? extends AWSRunningInstance> spots = filter(transform(concat(transform(idsByRegions.keySet(),
                                                                                       spotInstancesByIdInRegion(idsByRegions))),

                                                                      spotConverter), notNull());

      return concat(super.pollRunningInstancesByRegionsAndIds(idsByRegions), spots);
   }

   protected Function<String, Set<SpotInstanceRequest>> allSpotInstancesInRegion() {
      return new Function<String, Set<SpotInstanceRequest>>() {

         @Override
         public Set<SpotInstanceRequest> apply(String from) {
            return client.getSpotInstanceServices().describeSpotInstanceRequestsInRegion(from);
         }
      };
   }

   protected Function<String, Set<SpotInstanceRequest>> spotInstancesByIdInRegion(final Multimap<String,String> idsByRegions) {
      return new Function<String, Set<SpotInstanceRequest>>() {

         @Override
         public Set<SpotInstanceRequest> apply(String from) {
            return client.getSpotInstanceServices()
               .describeSpotInstanceRequestsInRegion(from, toArray(idsByRegions.get(from), String.class));
         }
      };
   }

}
