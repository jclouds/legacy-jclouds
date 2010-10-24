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

package org.jclouds.libvirt.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.parseTagFromName;

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
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.libvirt.Domain;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Singleton
public class DomainToNodeMetadata implements Function<Domain, NodeMetadata> {

   // public static final Map<Domain.Status, NodeState> serverStatusToNodeState = ImmutableMap
   // .<Domain.Status, NodeState> builder().put(Domain.Status.ACTIVE, NodeState.RUNNING)//
   // .put(Domain.Status.BUILD, NodeState.PENDING)//
   // .put(Domain.Status.TERMINATED, NodeState.TERMINATED)//
   // .put(Domain.Status.UNRECOGNIZED, NodeState.UNRECOGNIZED)//
   // .build();

   private final FindHardwareForDomain findHardwareForDomain;
   private final FindLocationForDomain findLocationForDomain;
   private final FindImageForDomain findImageForDomain;
   private final Map<String, Credentials> credentialStore;

   @Inject
   DomainToNodeMetadata(Map<String, Credentials> credentialStore, FindHardwareForDomain findHardwareForDomain,
         FindLocationForDomain findLocationForDomain, FindImageForDomain findImageForDomain) {
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
      this.findHardwareForDomain = checkNotNull(findHardwareForDomain, "findHardwareForDomain");
      this.findLocationForDomain = checkNotNull(findLocationForDomain, "findLocationForDomain");
      this.findImageForDomain = checkNotNull(findImageForDomain, "findImageForDomain");
   }

   @Override
   public NodeMetadata apply(Domain from) {
      // convert the result object to a jclouds NodeMetadata
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      // builder.ids(from.id + "");
      // builder.name(from.name);
      // builder.location(findLocationForDomain.apply(from));
      // builder.tag(parseTagFromName(from.name));
      // builder.imageId(from.imageId + "");
      // Image image = findImageForDomain.apply(from);
      // if (image != null)
      // builder.operatingSystem(image.getOperatingSystem());
      // builder.hardware(findHardwareForDomain.apply(from));
      // builder.state(serverStatusToNodeState.get(from.status));
      // builder.publicAddresses(ImmutableSet.<String> of(from.publicAddress));
      // builder.privateAddresses(ImmutableSet.<String> of(from.privateAddress));
      // builder.credentials(credentialStore.get(from.id + ""));
      return builder.build();
   }

   @Singleton
   public static class FindHardwareForDomain extends FindResourceInSet<Domain, Hardware> {

      @Inject
      public FindHardwareForDomain(@Memoized Supplier<Set<? extends Hardware>> hardware) {
         super(hardware);
      }

      @Override
      public boolean matches(Domain from, Hardware input) {
         // TODO
         // return input.getProviderId().equals(from.hardwareId + "");
         return true;
      }
   }

   @Singleton
   public static class FindImageForDomain extends FindResourceInSet<Domain, Image> {

      @Inject
      public FindImageForDomain(@Memoized Supplier<Set<? extends Image>> hardware) {
         super(hardware);
      }

      @Override
      public boolean matches(Domain from, Image input) {
         // TODO
         // return input.getProviderId().equals(from.imageId + "");
         return true;
      }
   }

   @Singleton
   public static class FindLocationForDomain extends FindResourceInSet<Domain, Location> {

      @Inject
      public FindLocationForDomain(@Memoized Supplier<Set<? extends Location>> hardware) {
         super(hardware);
      }

      @Override
      public boolean matches(Domain from, Location input) {
         // TODO
         // return input.getId().equals(from.datacenter + "");
         return true;
      }
   }
}
