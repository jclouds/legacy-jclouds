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
package org.jclouds.joyent.cloudapi.v6_5.compute.predicates;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.and;
import static com.google.common.collect.Iterables.all;
import static org.jclouds.compute.predicates.NodePredicates.TERMINATED;
import static org.jclouds.compute.predicates.NodePredicates.inGroup;
import static org.jclouds.compute.predicates.NodePredicates.locationId;

import javax.inject.Inject;

import org.jclouds.compute.ComputeService;
import org.jclouds.joyent.cloudapi.v6_5.domain.datacenterscoped.DatacenterAndName;

import com.google.common.base.Predicate;

/**
 * @author Adrian Cole
 */
public class AllNodesInGroupTerminated implements Predicate<DatacenterAndName> {
   private final ComputeService computeService;

   
   //TODO: TESTME
   @Inject
   public AllNodesInGroupTerminated(ComputeService computeService) {
      this.computeService = checkNotNull(computeService, "computeService");
   }

   @Override
   public boolean apply(DatacenterAndName input) {
      return all(computeService.listNodesDetailsMatching(locationId(input.getDatacenter())), and(inGroup(input.getName()), TERMINATED));
   }
}