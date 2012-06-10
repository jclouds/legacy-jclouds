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
package org.jclouds.openstack.nova.v2_0.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.filter;

import java.util.Set;

import javax.inject.Inject;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.domain.LocationScope;
import org.jclouds.openstack.nova.v2_0.compute.predicates.AllNodesInGroupTerminated;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ZoneAndName;

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
public class OrphanedGroupsByZoneId implements Function<Set<? extends NodeMetadata>, Multimap<String, String>> {
   private final Predicate<ZoneAndName> allNodesInGroupTerminated;

   @Inject
   protected OrphanedGroupsByZoneId(ComputeService computeService) {
      this(new AllNodesInGroupTerminated(checkNotNull(computeService, "computeService")));
   }

   @VisibleForTesting
   OrphanedGroupsByZoneId(Predicate<ZoneAndName> allNodesInGroupTerminated) {
      this.allNodesInGroupTerminated = checkNotNull(allNodesInGroupTerminated, "allNodesInGroupTerminated");
   }

   public Multimap<String, String> apply(Set<? extends NodeMetadata> deadNodes) {
      Iterable<? extends NodeMetadata> nodesWithGroup = filter(deadNodes, NodePredicates.hasGroup());
      Set<ZoneAndName> zoneAndGroupNames = ImmutableSet.copyOf(filter(transform(nodesWithGroup,
               new Function<NodeMetadata, ZoneAndName>() {

                  @Override
                  public ZoneAndName apply(NodeMetadata input) {
                     String zoneId = input.getLocation().getScope() == LocationScope.HOST ? input.getLocation()
                              .getParent().getId() : input.getLocation().getId();
                     return ZoneAndName.fromZoneAndName(zoneId, input.getGroup());
                  }

               }), allNodesInGroupTerminated));
      Multimap<String, String> zoneToZoneAndGroupNames = Multimaps.transformValues(Multimaps.index(zoneAndGroupNames,
               ZoneAndName.ZONE_FUNCTION), ZoneAndName.NAME_FUNCTION);
      return zoneToZoneAndGroupNames;
   }

}