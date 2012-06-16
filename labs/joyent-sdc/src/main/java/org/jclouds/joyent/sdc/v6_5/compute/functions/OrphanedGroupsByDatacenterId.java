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
package org.jclouds.joyent.sdc.v6_5.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;

import java.util.Set;

import javax.inject.Inject;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.joyent.sdc.v6_5.compute.predicates.AllNodesInGroupTerminated;
import org.jclouds.joyent.sdc.v6_5.domain.datacenterscoped.DatacenterAndName;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * 
 * @author Adrian Cole
 */
public class OrphanedGroupsByDatacenterId implements Function<Set<? extends NodeMetadata>, Multimap<String, String>> {
   private final Predicate<DatacenterAndName> allNodesInGroupTerminated;

   @Inject
   protected OrphanedGroupsByDatacenterId(ComputeService computeService) {
      this(new AllNodesInGroupTerminated(checkNotNull(computeService, "computeService")));
   }

   @VisibleForTesting
   OrphanedGroupsByDatacenterId(Predicate<DatacenterAndName> allNodesInGroupTerminated) {
      this.allNodesInGroupTerminated = checkNotNull(allNodesInGroupTerminated, "allNodesInGroupTerminated");
   }

   public Multimap<String, String> apply(Set<? extends NodeMetadata> deadNodes) {
      Iterable<? extends NodeMetadata> nodesWithGroup = filter(deadNodes, NodePredicates.hasGroup());
      Set<DatacenterAndName> datacenterAndGroupNames = ImmutableSet.copyOf(filter(transform(nodesWithGroup,
               new Function<NodeMetadata, DatacenterAndName>() {

                  @Override
                  public DatacenterAndName apply(NodeMetadata input) {
                     String datacenterId = input.getLocation().getId();
                     return DatacenterAndName.fromDatacenterAndName(datacenterId, input.getGroup());
                  }

               }), allNodesInGroupTerminated));
      Multimap<String, String> datacenterToDatacenterAndGroupNames = Multimaps.transformValues(Multimaps.index(datacenterAndGroupNames,
               DatacenterAndName.DATACENTER_FUNCTION), DatacenterAndName.NAME_FUNCTION);
      return datacenterToDatacenterAndGroupNames;
   }

}