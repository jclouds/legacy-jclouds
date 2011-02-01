/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.vi.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.parseGroupFromName;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.FindResourceInSet;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.OperatingSystemBuilder;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.mo.VirtualMachine;

/**
 * @author Adrian Cole
 */
@Singleton
public class VirtualMachineToNodeMetadata implements Function<VirtualMachine, NodeMetadata> {

   public static final Map<VirtualMachinePowerState, NodeState> domainStateToNodeState = 
	   ImmutableMap.<VirtualMachinePowerState, NodeState> builder()
	        .put(VirtualMachinePowerState.poweredOn, NodeState.RUNNING)//
            //.put(VirtualMachinePowerState.suspended, NodeState.PENDING)//
            .put(VirtualMachinePowerState.suspended, NodeState.SUSPENDED)//
            .put(VirtualMachinePowerState.poweredOff, NodeState.TERMINATED)//
            .build();

   private final Function<VirtualMachine, Hardware> findHardwareForVirtualMachine;
   private final FindLocationForVirtualMachine findLocationForVirtualMachine;
   private final FindImageForVirtualMachine findImageForVirtualMachine;
   private final Map<String, Credentials> credentialStore;

   @Inject
   VirtualMachineToNodeMetadata(Map<String, Credentials> credentialStore, Function<VirtualMachine, Hardware> findHardwareForVirtualMachine,
		   FindLocationForVirtualMachine findLocationForVirtualMachine, FindImageForVirtualMachine findImageForVirtualMachine) {
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
      this.findHardwareForVirtualMachine = checkNotNull(findHardwareForVirtualMachine, "findHardwareForVirtualMachine");
      this.findLocationForVirtualMachine = checkNotNull(findLocationForVirtualMachine, "findLocationForVirtualMachine");
      this.findImageForVirtualMachine = checkNotNull(findImageForVirtualMachine, "findImageForVirtualMachine");
   }

   @Override
   public NodeMetadata apply(VirtualMachine from) {

      // convert the result object to a jclouds NodeMetadata
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      try {
         builder.id(from.getConfig().getInstanceUuid());
         builder.providerId(from.getConfig().getLocationId() + "");
         builder.name(from.getName());
         builder.location(findLocationForVirtualMachine.apply(from));
         builder.group(parseGroupFromName(from.getName()));

         builder.operatingSystem(new OperatingSystemBuilder()
         	.name(from.getConfig().getGuestFullName())
         	.description(from.getConfig().getGuestFullName())
         	.is64Bit(from.getConfig().getGuestId().contains("64"))
         	.build());
         builder.hardware(findHardwareForVirtualMachine.apply(from));

         builder.state(domainStateToNodeState.get(from.getRuntime().getPowerState()));
         // builder.publicAddresses(ImmutableSet.<String> of(from.publicAddress));
         // builder.privateAddresses(ImmutableSet.<String> of(from.privateAddress));
         builder.credentials(credentialStore.get("node#" + from.getName()));

      } catch (Exception e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return builder.build();
   }

   @Singleton
   public static class FindImageForVirtualMachine extends FindResourceInSet<VirtualMachine, Image> {

      @Inject
      public FindImageForVirtualMachine(@Memoized Supplier<Set<? extends Image>> hardware) {
         super(hardware);
      }

      @Override
      public boolean matches(VirtualMachine from, Image input) {
         // TODO
         // return input.getProviderId().equals(from.imageId + "");
         return true;
      }
   }

   @Singleton
   public static class FindLocationForVirtualMachine extends FindResourceInSet<VirtualMachine, Location> {

      @Inject
      public FindLocationForVirtualMachine(@Memoized Supplier<Set<? extends Location>> hardware) {
         super(hardware);
      }

      @Override
      public boolean matches(VirtualMachine from, Location input) {
         // TODO
         // return input.getId().equals(from.datacenter + "");
         return true;
      }
   }
}
