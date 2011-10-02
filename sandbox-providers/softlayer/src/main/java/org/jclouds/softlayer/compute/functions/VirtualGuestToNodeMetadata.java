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
package org.jclouds.softlayer.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.parseGroupFromName;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.FindResourceInSet;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.VirtualGuest;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Singleton
public class VirtualGuestToNodeMetadata implements Function<VirtualGuest, NodeMetadata> {

   public static final Map<VirtualGuest.State, NodeState> serverStateToNodeState = ImmutableMap
            .<VirtualGuest.State, NodeState> builder().put(VirtualGuest.State.HALTED, NodeState.PENDING).put(
                     VirtualGuest.State.PAUSED, NodeState.SUSPENDED).put(VirtualGuest.State.RUNNING, NodeState.RUNNING)
            .put(VirtualGuest.State.UNRECOGNIZED, NodeState.UNRECOGNIZED).build();

   private final Map<String, Credentials> credentialStore;
   private final FindLocationForVirtualGuest findLocationForVirtualGuest;

   @Inject
   VirtualGuestToNodeMetadata(Map<String, Credentials> credentialStore,
            FindLocationForVirtualGuest findLocationForVirtualGuest) {
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
      this.findLocationForVirtualGuest = checkNotNull(findLocationForVirtualGuest, "findLocationForVirtualGuest");
   }

   @Override
   public NodeMetadata apply(VirtualGuest from) {
      // convert the result object to a jclouds NodeMetadata
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.ids(from.getId() + "");
      builder.name(from.getHostname());
      builder.location(findLocationForVirtualGuest.apply(from));
      builder.group(parseGroupFromName(from.getHostname()));
      // TODO determine image id (product price)from virtual guest
      // builder.imageId(from.imageId + "");
      // TODO make operating system from virtual guest
      // builder.operatingSystem(OperatingSystem.builder()...);
      // TODO make hardware from virtual guest
      // builder.hardware(Hardware.builder()...);
      builder.state(serverStateToNodeState.get(from.getPowerState().getKeyName()));

      // These are null for 'bad' guest orders in the HALTED state.
      if (from.getPrimaryIpAddress() != null)
         builder.publicAddresses(ImmutableSet.<String> of(from.getPrimaryIpAddress()));
      if (from.getPrimaryBackendIpAddress() != null)
         builder.privateAddresses(ImmutableSet.<String> of(from.getPrimaryBackendIpAddress()));

      builder.credentials(credentialStore.get("node#" + from.getId()));
      return builder.build();
   }

   @Singleton
   public static class FindLocationForVirtualGuest extends FindResourceInSet<VirtualGuest, Location> {

      @Inject
      public FindLocationForVirtualGuest(@Memoized Supplier<Set<? extends Location>> location) {
         super(location);
      }

      @Override
      public boolean matches(VirtualGuest from, Location input) {
         Datacenter dc = from.getDatacenter();
         if (dc == null)
            return false;
         return input.getId().equals(Integer.toString(dc.getId()));
      }
   }
}
