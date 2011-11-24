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
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.compute.util.ComputeServiceUtils.parseGroupFromName;

import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.jclouds.cloudstack.domain.IPForwardingRule;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.collect.FindResourceInSet;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.domain.Location;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.util.InetAddresses2;
import org.jclouds.util.Throwables2;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.cache.Cache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * @author Adrian Cole
 */
@Singleton
public class VirtualMachineToNodeMetadata implements Function<VirtualMachine, NodeMetadata> {

   public static final Map<VirtualMachine.State, NodeState> vmStateToNodeState = ImmutableMap
      .<VirtualMachine.State, NodeState>builder().put(VirtualMachine.State.STARTING, NodeState.PENDING)
      .put(VirtualMachine.State.RUNNING, NodeState.RUNNING).put(VirtualMachine.State.STOPPING, NodeState.SUSPENDED)
      .put(VirtualMachine.State.STOPPED, NodeState.PENDING)
      .put(VirtualMachine.State.DESTROYED, NodeState.TERMINATED)
      .put(VirtualMachine.State.EXPUNGING, NodeState.TERMINATED)
      .put(VirtualMachine.State.MIGRATING, NodeState.PENDING).put(VirtualMachine.State.ERROR, NodeState.ERROR)
      .put(VirtualMachine.State.UNKNOWN, NodeState.UNRECOGNIZED)
         // TODO: is this really a state?
      .put(VirtualMachine.State.SHUTDOWNED, NodeState.PENDING)
      .put(VirtualMachine.State.UNRECOGNIZED, NodeState.UNRECOGNIZED).build();

   private final FindLocationForVirtualMachine findLocationForVirtualMachine;
   private final FindHardwareForVirtualMachine findHardwareForVirtualMachine;
   private final FindImageForVirtualMachine findImageForVirtualMachine;
   private final Cache<Long, Set<IPForwardingRule>> getIPForwardingRulesByVirtualMachine;

   @Inject
   VirtualMachineToNodeMetadata(FindLocationForVirtualMachine findLocationForVirtualMachine,
                                FindHardwareForVirtualMachine findHardwareForVirtualMachine,
                                FindImageForVirtualMachine findImageForVirtualMachine,
                                Cache<Long, Set<IPForwardingRule>> getIPForwardingRulesByVirtualMachine) {
      this.findLocationForVirtualMachine = checkNotNull(findLocationForVirtualMachine, "findLocationForVirtualMachine");
      this.findHardwareForVirtualMachine = checkNotNull(findHardwareForVirtualMachine, "findHardwareForVirtualMachine");
      this.findImageForVirtualMachine = checkNotNull(findImageForVirtualMachine, "findImageForVirtualMachine");
      this.getIPForwardingRulesByVirtualMachine = checkNotNull(getIPForwardingRulesByVirtualMachine,
         "getIPForwardingRulesByVirtualMachine");
   }

   @Override
   public NodeMetadata apply(VirtualMachine from) {
      // convert the result object to a jclouds NodeMetadata
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.ids(from.getId() + "");
      builder.name(from.getName());
      builder.hostname(from.getHostname());
      builder.location(findLocationForVirtualMachine.apply(from));
      builder.group(parseGroupFromName(from.getDisplayName()));
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
         Set<String> addresses = ImmutableSet.<String>of(from.getIPAddress());
         if (isPrivate)
            builder.privateAddresses(addresses);
         else
            builder.publicAddresses(addresses);
      }
      try {

         builder.publicAddresses(transform(filter(getIPForwardingRulesByVirtualMachine.getUnchecked(from.getId()),
            new Predicate<IPForwardingRule>() {
               @Override
               public boolean apply(@Nullable IPForwardingRule rule) {
                  return !"Deleting".equals(rule.getState());
               }
            }),
            new Function<IPForwardingRule, String>() {
               @Override
               public String apply(@Nullable IPForwardingRule rule) {
                  return rule.getIPAddress();
               }
            }));
      } catch (UncheckedExecutionException e) {
         if (Throwables2.getFirstThrowableOfType(e, ResourceNotFoundException.class) == null) {
            Throwables.propagateIfPossible(e.getCause());
            throw e;
         }
      }
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
