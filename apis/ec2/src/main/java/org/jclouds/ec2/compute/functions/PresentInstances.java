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
package org.jclouds.ec2.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.toArray;
import static com.google.common.collect.Multimaps.index;
import static com.google.common.collect.Multimaps.transformValues;
import static org.jclouds.ec2.compute.domain.RegionAndName.nameFunction;
import static org.jclouds.ec2.compute.domain.RegionAndName.regionFunction;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;

/**
 * returns the instances present in the list.  Makes a single rest call per aggregate on region.
 * 
 * @author Adrian Cole
 */
@Singleton
public class PresentInstances implements Function<Set<RegionAndName>, Set<RunningInstance>> {

   @Resource
   protected Logger logger = Logger.NULL;

   private final EC2Client client;

   @Inject
   public PresentInstances(EC2Client client) {
      this.client = checkNotNull(client, "client");
   }

   @Override
   public Set<RunningInstance> apply(Set<RegionAndName> regionAndIds) {
      if (checkNotNull(regionAndIds, "regionAndIds").isEmpty())
         return ImmutableSet.of();
      Builder<RunningInstance> builder = ImmutableSet.<RunningInstance> builder();
      Multimap<String, String> regionToInstanceIds = transformValues(index(regionAndIds, regionFunction()),
            nameFunction());
      for (Map.Entry<String, Collection<String>> entry : regionToInstanceIds.asMap().entrySet()) {
         String region = entry.getKey();
         Collection<String> instanceIds = entry.getValue();
         logger.trace("looking for instances %s in region %s", instanceIds, region);
         builder.addAll(concat(client.getInstanceServices().describeInstancesInRegion(region,
               toArray(instanceIds, String.class))));
      }
      return builder.build();
   }
   
   @Override
   public String toString(){
      return "presentInstances()";
   }
}
