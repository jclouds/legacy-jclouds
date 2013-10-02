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
package org.jclouds.cloudstack.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.newHashSet;
import static org.jclouds.location.predicates.LocationPredicates.idEquals;
import static org.jclouds.util.InetAddresses2.isPrivateIPAddress;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.cloudstack.domain.IPForwardingRule;
import org.jclouds.cloudstack.domain.NIC;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Location;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.util.Throwables2;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * @author Adrian Cole, Andrei Savu
 */
@Singleton
public class VirtualMachineToNodeMetadata implements Function<VirtualMachine, NodeMetadata> {

   public static final Map<VirtualMachine.State, Status> vmStateToNodeStatus = ImmutableMap
         .<VirtualMachine.State, Status> builder().put(VirtualMachine.State.STARTING, Status.PENDING)
         .put(VirtualMachine.State.RUNNING, Status.RUNNING).put(VirtualMachine.State.STOPPING, Status.PENDING)
         .put(VirtualMachine.State.STOPPED, Status.SUSPENDED)
         .put(VirtualMachine.State.DESTROYED, Status.TERMINATED)
         .put(VirtualMachine.State.EXPUNGING, Status.TERMINATED)
         .put(VirtualMachine.State.MIGRATING, Status.PENDING).put(VirtualMachine.State.ERROR, Status.ERROR)
         .put(VirtualMachine.State.UNKNOWN, Status.UNRECOGNIZED)
         // TODO: is this really a state?
         .put(VirtualMachine.State.SHUTDOWNED, Status.PENDING)
         .put(VirtualMachine.State.UNRECOGNIZED, Status.UNRECOGNIZED).build();

   private final Supplier<Set<? extends Location>> locations;
   private final Supplier<Set<? extends Image>> images;
   private final LoadingCache<String, Set<IPForwardingRule>> getIPForwardingRulesByVirtualMachine;
   private final GroupNamingConvention nodeNamingConvention;

   @Inject
   VirtualMachineToNodeMetadata(@Memoized Supplier<Set<? extends Location>> locations,
         @Memoized Supplier<Set<? extends Image>> images,
         LoadingCache<String, Set<IPForwardingRule>> getIPForwardingRulesByVirtualMachine,
         GroupNamingConvention.Factory namingConvention) {
      this.nodeNamingConvention = checkNotNull(namingConvention, "namingConvention").createWithoutPrefix();
      this.locations = checkNotNull(locations, "locations");
      this.images = checkNotNull(images, "images");
      this.getIPForwardingRulesByVirtualMachine = checkNotNull(getIPForwardingRulesByVirtualMachine,
            "getIPForwardingRulesByVirtualMachine");
   }

   @Override
   public NodeMetadata apply(final VirtualMachine from) {
      // convert the result object to a jclouds NodeMetadata
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.ids(from.getId() + "");
      builder.name(from.getName());
      // TODO: in cloudstack 2.2.12, when "name" was set fine on the backend,
      // but wrong API response was returned to the user
      // http://bugs.cloud.com/show_bug.cgi?id=11664
      //
      // we set displayName to the same value as name, but this could be wrong
      // on hosts not started with jclouds
      builder.hostname(from.getDisplayName());
      builder.location(FluentIterable.from(locations.get()).firstMatch(idEquals(from.getZoneId())).orNull());
      if (from.getDisplayName() != null) {
         builder.group(nodeNamingConvention.groupInUniqueNameOrNull(from.getDisplayName()));
      }
      Image image = FluentIterable.from(images.get()).firstMatch(new Predicate<Image>() {
         @Override
         public boolean apply(Image input) {
            return input.getProviderId().equals(from.getTemplateId() + "")
            // either location free image (location is null) or in the same zone as the VM
                  && (input.getLocation() == null || input.getId().equals(from.getZoneId() + ""));
         }
      }).orNull();
      if (image != null) {
         builder.imageId(image.getId());
         builder.operatingSystem(image.getOperatingSystem());
      }

      builder.hardware(new HardwareBuilder()
        .ids(from.getServiceOfferingId() + "")
        .name(from.getServiceOfferingName() + "")
         // .tags() TODO
        .processors(ImmutableList.of(new Processor(from.getCpuCount(), from.getCpuSpeed())))
        .ram((int)from.getMemory())//
        .hypervisor(from.getHypervisor())//
        .build());

      builder.status(vmStateToNodeStatus.get(from.getState()));

      Set<String> publicAddresses = newHashSet();
      Set<String> privateAddresses = newHashSet();
      if (from.getIPAddress() != null) {
         boolean isPrivate = isPrivateIPAddress(from.getIPAddress());
         if (isPrivate) {
            privateAddresses.add(from.getIPAddress());
         } else {
            publicAddresses.add(from.getIPAddress());
         }
      }
      if (from.getPublicIP() != null) {
          publicAddresses.add(from.getPublicIP());
      }
      for (NIC nic : from.getNICs()) {
         if (nic.getIPAddress() != null) {
            if (isPrivateIPAddress(nic.getIPAddress())) {
               privateAddresses.add(nic.getIPAddress());
            } else {
               publicAddresses.add(nic.getIPAddress());
            }
         }
      }
      try {
         /* Also add to the list of public IPs any public IP address that has a
            forwarding rule that links to this machine */
         Iterables.addAll(publicAddresses, transform(
            filter(getIPForwardingRulesByVirtualMachine.getUnchecked(from.getId()),
               new Predicate<IPForwardingRule>() {
                  @Override
                  public boolean apply(IPForwardingRule rule) {
                     return !"Deleting".equals(rule.getState());
                  }
               }), new Function<IPForwardingRule, String>() {
            @Override
            public String apply(IPForwardingRule rule) {
               return rule.getIPAddress();
            }
         }));
      } catch (UncheckedExecutionException e) {
         if (Throwables2.getFirstThrowableOfType(e, ResourceNotFoundException.class) == null) {
            Throwables.propagateIfPossible(e.getCause());
            throw e;
         }
      }
      return builder.privateAddresses(privateAddresses).publicAddresses(publicAddresses).build();
   }
}
