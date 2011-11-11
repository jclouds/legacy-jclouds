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
package org.jclouds.cloudstack.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.parseGroupFromName;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.collect.FindResourceInSet;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.util.InetAddresses2;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Singleton
public class VirtualMachineToNodeMetadata implements Function<VirtualMachine, NodeMetadata> {

   public static final Map<VirtualMachine.State, NodeState> vmStateToNodeState = ImmutableMap
         .<VirtualMachine.State, NodeState> builder().put(VirtualMachine.State.STARTING, NodeState.PENDING)
         .put(VirtualMachine.State.RUNNING, NodeState.RUNNING).put(VirtualMachine.State.STOPPING, NodeState.SUSPENDED)
         .put(VirtualMachine.State.STOPPED, NodeState.PENDING)
         .put(VirtualMachine.State.DESTROYED, NodeState.TERMINATED)
         .put(VirtualMachine.State.EXPUNGING, NodeState.TERMINATED)
         .put(VirtualMachine.State.MIGRATING, NodeState.PENDING).put(VirtualMachine.State.ERROR, NodeState.ERROR)
         .put(VirtualMachine.State.UNKNOWN, NodeState.UNRECOGNIZED)
         // TODO: is this really a state?
         .put(VirtualMachine.State.SHUTDOWNED, NodeState.PENDING)
         .put(VirtualMachine.State.UNRECOGNIZED, NodeState.UNRECOGNIZED).build();

   private final Map<String, Credentials> credentialStore;
   private final FindLocationForVirtualMachine findLocationForVirtualMachine;
   private final FindHardwareForVirtualMachine findHardwareForVirtualMachine;
   private final FindImageForVirtualMachine findImageForVirtualMachine;

   @Inject
   VirtualMachineToNodeMetadata(Map<String, Credentials> credentialStore,
         FindLocationForVirtualMachine findLocationForVirtualMachine,
         FindHardwareForVirtualMachine findHardwareForVirtualMachine,
         FindImageForVirtualMachine findImageForVirtualMachine) {
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
      this.findLocationForVirtualMachine = checkNotNull(findLocationForVirtualMachine, "findLocationForVirtualMachine");
      this.findHardwareForVirtualMachine = checkNotNull(findHardwareForVirtualMachine, "findHardwareForVirtualMachine");
      this.findImageForVirtualMachine = checkNotNull(findImageForVirtualMachine, "findImageForVirtualMachine");
   }

   @Override
   public NodeMetadata apply(VirtualMachine from) {
      // convert the result object to a jclouds NodeMetadata
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      String id = from.getZoneId() + "/" + from.getId();
      builder.id(id);
      builder.providerId(from.getId() + "");
      builder.name(from.getName());
      builder.hostname(from.getHostname());
      builder.location(findLocationForVirtualMachine.apply(from));
      builder.group(parseGroupFromName(from.getHostname()));
      Image image = findImageForVirtualMachine.apply(from);
      if (image != null) {
         builder.imageId(image.getId());
         builder.operatingSystem(image.getOperatingSystem());
      }

      Hardware hardware = findHardwareForVirtualMachine.apply(from);
      if (hardware != null)
         builder.hardware(hardware);

      builder.state(vmStateToNodeState.get(from.getState()));

      // TODO: check to see public or private
      if (from.getIPAddress() != null) {
         boolean isPrivate = InetAddresses2.isPrivateIPAddress(from.getIPAddress());
         Set<String> addresses = ImmutableSet.<String> of(from.getIPAddress());
         if (isPrivate)
            builder.privateAddresses(addresses);
         else
            builder.publicAddresses(addresses);
      }
      builder.credentials(credentialStore.get("node#" + id));
      return builder.build();
   }

   @Singleton
   public static class FindLocationForVirtualMachine extends FindResourceInSet<VirtualMachine, Location> {

      @Inject
      public FindLocationForVirtualMachine(@Memoized Supplier<Set<? extends Location>> location) {
         super(location);
      }

      @Override
      public boolean matches(VirtualMachine from, Location input) {
         return input.getId().equals(Long.toString(from.getZoneId()));
      }
   }

   @Singleton
   public static class FindHardwareForVirtualMachine extends FindResourceInSet<VirtualMachine, Hardware> {

      @Inject
      public FindHardwareForVirtualMachine(@Memoized Supplier<Set<? extends Hardware>> location) {
         super(location);
      }

      @Override
      public boolean matches(VirtualMachine from, Hardware input) {
         return input.getProviderId().equals(Long.toString(from.getServiceOfferingId()));
      }
   }

   @Singleton
   public static class FindImageForVirtualMachine extends FindResourceInSet<VirtualMachine, Image> {

      @Inject
      public FindImageForVirtualMachine(@Memoized Supplier<Set<? extends Image>> location) {
         super(location);
      }

      @Override
      public boolean matches(VirtualMachine from, Image input) {
         return input.getProviderId().equals(from.getTemplateId() + "")
         // either location free image (location is null)
         // or in the same zone as the VM
               && (input.getLocation() == null || input.getId().equals(from.getZoneId() + ""));
      }
   }

}
