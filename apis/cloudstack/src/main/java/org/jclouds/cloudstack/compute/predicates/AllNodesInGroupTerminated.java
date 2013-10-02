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
package org.jclouds.cloudstack.compute.predicates;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.and;
import static com.google.common.collect.Iterables.all;
import static org.jclouds.compute.predicates.NodePredicates.TERMINATED;
import static org.jclouds.compute.predicates.NodePredicates.inGroup;
import static org.jclouds.compute.predicates.NodePredicates.locationId;
import static org.jclouds.compute.predicates.NodePredicates.parentLocationId;

import javax.inject.Inject;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.cloudstack.domain.ZoneAndName;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * @author Adrian Cole
 */
public class AllNodesInGroupTerminated implements Predicate<ZoneAndName> {
   private final ComputeService computeService;

   
   //TODO: TESTME
   @Inject
   public AllNodesInGroupTerminated(ComputeService computeService) {
      this.computeService = checkNotNull(computeService, "computeService");
   }

   @Override
   public boolean apply(ZoneAndName input) {
      // new nodes can have the zone as their location, existing nodes, the parent is the
      // location
      return all(computeService.listNodesDetailsMatching(Predicates.<ComputeMetadata> or(locationId(input.getZone()),
               parentLocationId(input.getZone()))), and(inGroup(input.getName()), TERMINATED));
   }
}
