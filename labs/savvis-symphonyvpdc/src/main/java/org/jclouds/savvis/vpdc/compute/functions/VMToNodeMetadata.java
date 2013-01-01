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
package org.jclouds.savvis.vpdc.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Iterables.filter;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.CIMOperatingSystem;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Location;
import org.jclouds.location.predicates.LocationPredicates;
import org.jclouds.savvis.vpdc.domain.VM;
import org.jclouds.savvis.vpdc.util.Utils;
import org.jclouds.util.InetAddresses2.IsPrivateIPAddress;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class VMToNodeMetadata implements Function<VM, NodeMetadata> {

   public static final Map<VM.Status, Status> VAPPSTATUS_TO_NODESTATE = ImmutableMap
            .<VM.Status, Status> builder().put(VM.Status.OFF, Status.SUSPENDED).put(VM.Status.ON,
                     Status.RUNNING).put(VM.Status.RESOLVED, Status.PENDING).put(VM.Status.UNRECOGNIZED,
                     Status.UNRECOGNIZED).put(VM.Status.UNKNOWN, Status.UNRECOGNIZED).put(VM.Status.SUSPENDED,
                     Status.SUSPENDED).put(VM.Status.UNRESOLVED, Status.PENDING).build();

   private final Supplier<Set<? extends Location>> locations;
   private final GroupNamingConvention nodeNamingConvention;

   @Inject
   VMToNodeMetadata(@Memoized Supplier<Set<? extends Location>> locations,
         GroupNamingConvention.Factory namingConvention) {
      this.nodeNamingConvention = checkNotNull(namingConvention, "namingConvention").createWithoutPrefix();
      this.locations = checkNotNull(locations, "locations");
   }

   @Override
   public NodeMetadata apply(VM from) {
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.ids(from.getHref().toASCIIString());
      builder.name(from.getName());
      String locationId = Iterables.get(from.getNetworkSection().getNetworks(), 0).getName();
      builder.location(from(locations.get()).firstMatch(LocationPredicates.idEquals(locationId)).orNull());
      builder.group(nodeNamingConvention.groupInUniqueNameOrNull(from.getName()));
      try {
         builder.operatingSystem(CIMOperatingSystem.toComputeOs(from.getOperatingSystemSection()));
      } catch (NullPointerException e) {
         // os section was null
      }
      // TODO build from resource allocation section
      // builder.hardware(findHardwareForVM.apply(from));
      builder.status(VAPPSTATUS_TO_NODESTATE.get(from.getStatus()));
      Set<String> addresses = Utils.getIpsFromVM(from);
      builder.publicAddresses(filter(addresses, not(IsPrivateIPAddress.INSTANCE)));
      builder.privateAddresses(filter(addresses, IsPrivateIPAddress.INSTANCE));
      return builder.build();
   }
}
