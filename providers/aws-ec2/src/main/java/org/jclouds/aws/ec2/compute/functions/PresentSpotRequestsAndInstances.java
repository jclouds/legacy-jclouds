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
package org.jclouds.aws.ec2.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.containsPattern;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.toArray;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Multimaps.index;
import static com.google.common.collect.Multimaps.transformValues;
import static org.jclouds.ec2.compute.domain.RegionAndName.nameFunction;
import static org.jclouds.ec2.compute.domain.RegionAndName.regionFunction;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.aws.ec2.domain.AWSRunningInstance;
import org.jclouds.aws.ec2.domain.SpotInstanceRequest;
import org.jclouds.aws.ec2.functions.SpotInstanceRequestToAWSRunningInstance;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.functions.PresentInstances;
import org.jclouds.ec2.domain.RunningInstance;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;

/**
 * returns either the instances present in the list, or spot instances, if they ids start with {@code sir-}. Makes a
 * single rest call per aggregate on region.
 * 
 * @author Adrian Cole
 */
@Singleton
public class PresentSpotRequestsAndInstances extends PresentInstances {

   private final AWSEC2Client client;
   private final Function<SpotInstanceRequest, AWSRunningInstance> spotConverter;

   @Inject
   public PresentSpotRequestsAndInstances(AWSEC2Client client, Function<SpotInstanceRequest, AWSRunningInstance> spotConverter) {
      super(client);
      this.client = checkNotNull(client, "client");
      this.spotConverter = checkNotNull(spotConverter, "spotConverter");
   }

   @Override
   public Set<RunningInstance> apply(Set<RegionAndName> regionAndIds) {
      if (checkNotNull(regionAndIds, "regionAndIds").isEmpty())
         return ImmutableSet.of();
      if (any(regionAndIds, compose(containsPattern("sir-"), nameFunction())))
         return getSpots(regionAndIds);
      return super.apply(regionAndIds);
   }

   protected Set<RunningInstance> getSpots(Set<RegionAndName> regionAndIds) {
      Builder<RunningInstance> builder = ImmutableSet.<RunningInstance> builder();
      Multimap<String, String> regionToSpotIds = transformValues(index(regionAndIds, regionFunction()), nameFunction());
      for (Map.Entry<String, Collection<String>> entry : regionToSpotIds.asMap().entrySet()) {
         String region = entry.getKey();
         Collection<String> spotIds = entry.getValue();
         logger.trace("looking for spots %s in region %s", spotIds, region);
         builder.addAll(transform(
               client.getSpotInstanceServices().describeSpotInstanceRequestsInRegion(region,
                     toArray(spotIds, String.class)), spotConverter));
      }
      return builder.build();
   }

   @Override
   public String toString() {
      return "presentSpotRequestsAndInstances()";
   }
}
