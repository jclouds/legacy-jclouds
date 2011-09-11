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
import static com.google.common.collect.Iterables.filter;
import static org.jclouds.compute.util.ComputeServiceUtils.parseGroupFromName;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.FindResourceInSet;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.CIMOperatingSystem;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
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

   public static final Map<VM.Status, NodeState> VAPPSTATUS_TO_NODESTATE = ImmutableMap
            .<VM.Status, NodeState> builder().put(VM.Status.OFF, NodeState.SUSPENDED).put(VM.Status.ON,
                     NodeState.RUNNING).put(VM.Status.RESOLVED, NodeState.PENDING).put(VM.Status.UNRECOGNIZED,
                     NodeState.UNRECOGNIZED).put(VM.Status.UNKNOWN, NodeState.UNRECOGNIZED).put(VM.Status.SUSPENDED,
                     NodeState.SUSPENDED).put(VM.Status.UNRESOLVED, NodeState.PENDING).build();

   private final FindLocationForVM findLocationForVM;
   private final Map<String, Credentials> credentialStore;

   @Inject
   VMToNodeMetadata(Map<String, Credentials> credentialStore, FindLocationForVM findLocationForVM) {
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
      this.findLocationForVM = checkNotNull(findLocationForVM, "findLocationForVM");
   }

   @Override
   public NodeMetadata apply(VM from) {
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.ids(from.getHref().toASCIIString());
      builder.name(from.getName());
      builder.location(findLocationForVM.apply(from));
      builder.group(parseGroupFromName(from.getName()));
      try {
         builder.operatingSystem(CIMOperatingSystem.toComputeOs(from.getOperatingSystemSection()));
      } catch (NullPointerException e) {
         // os section was null
      }
      // TODO build from resource allocation section
      // builder.hardware(findHardwareForVM.apply(from));
      builder.state(VAPPSTATUS_TO_NODESTATE.get(from.getStatus()));
      Set<String> addresses = Utils.getIpsFromVM(from);
      builder.publicAddresses(filter(addresses, not(IsPrivateIPAddress.INSTANCE)));
      builder.privateAddresses(filter(addresses, IsPrivateIPAddress.INSTANCE));
      builder.credentials(credentialStore.get(from.getHref().toASCIIString()));
      return builder.build();
   }

   @Singleton
   public static class FindLocationForVM extends FindResourceInSet<VM, Location> {

      @Inject
      public FindLocationForVM(@Memoized Supplier<Set<? extends Location>> hardware) {
         super(hardware);
      }

      @Override
      public boolean matches(VM from, Location input) {
         return input.getId().equals(Iterables.get(from.getNetworkSection().getNetworks(), 0).getName());
      }
   }
}
