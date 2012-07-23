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
package org.jclouds.smartos.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

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
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.smartos.compute.domain.VM;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Nigel Magnay
 */
@Singleton
public class VMToNodeMetadata implements Function<VM, NodeMetadata> {

   public static final Map<VM.State, NodeMetadata.Status> serverStatusToNodeStatus = ImmutableMap
            .<VM.State, NodeMetadata.Status> builder().put(VM.State.RUNNING, NodeMetadata.Status.RUNNING)//
            .put(VM.State.STOPPED, NodeMetadata.Status.SUSPENDED)//
            .put(VM.State.INCOMPLETE, NodeMetadata.Status.PENDING)//
            .build();

   private final FindHardwareForServer findHardwareForServer;
   private final FindLocationForServer findLocationForServer;
   private final FindImageForServer findImageForServer;
   private final Map<String, Credentials> credentialStore;
   private final GroupNamingConvention nodeNamingConvention;

   @Inject
   VMToNodeMetadata(Map<String, Credentials> credentialStore, FindHardwareForServer findHardwareForServer,
            FindLocationForServer findLocationForServer, FindImageForServer findImageForServer,
            GroupNamingConvention.Factory namingConvention) {
      this.nodeNamingConvention = checkNotNull(namingConvention, "namingConvention").createWithoutPrefix();
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
      this.findHardwareForServer = checkNotNull(findHardwareForServer, "findHardwareForServer");
      this.findLocationForServer = checkNotNull(findLocationForServer, "findLocationForServer");
      this.findImageForServer = checkNotNull(findImageForServer, "findImageForServer");
   }

   @Override
   public NodeMetadata apply(VM from) {
      // convert the result object to a jclouds NodeMetadata
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.ids(from.getUuid() + "");
      builder.name(from.getAlias());
      builder.location(findLocationForServer.apply(from));
      builder.group(nodeNamingConvention.groupInUniqueNameOrNull(from.getType()));
      builder.imageId(from.getType() + "");
      Image image = findImageForServer.apply(from);
      if (image != null)
         builder.operatingSystem(image.getOperatingSystem());
      builder.hardware(findHardwareForServer.apply(from));
      builder.status(serverStatusToNodeStatus.get(from.getState()));
      try {
         if (from.getState() == VM.State.RUNNING) {
            Optional<String> ip = from.getPublicAddress();
            if (ip.isPresent()) {
               builder.publicAddresses(ImmutableSet.<String> of(ip.get()));
               builder.privateAddresses(ImmutableSet.<String> of(ip.get()));
            }
         }
      } catch (Exception ex) {
         // None?
      }
      // builder.privateAddresses(ImmutableSet.<String> of(from.privateAddress));
      builder.credentials(LoginCredentials.fromCredentials(credentialStore.get(from.getUuid() + "")));
      return builder.build();
   }

   @Singleton
   public static class FindHardwareForServer extends FindResourceInSet<VM, Hardware> {

      @Inject
      public FindHardwareForServer(@Memoized Supplier<Set<? extends Hardware>> hardware) {
         super(hardware);
      }

      @Override
      public boolean matches(VM from, Hardware input) {
         return input.getProviderId().equals(from.getUuid() + "");
      }
   }

   @Singleton
   public static class FindImageForServer extends FindResourceInSet<VM, Image> {

      @Inject
      public FindImageForServer(@Memoized Supplier<Set<? extends Image>> hardware) {
         super(hardware);
      }

      @Override
      public boolean matches(VM from, Image input) {
         return input.getProviderId().equals(from.getUuid() + "");
      }
   }

   @Singleton
   public static class FindLocationForServer extends FindResourceInSet<VM, Location> {

      @Inject
      public FindLocationForServer(@Memoized Supplier<Set<? extends Location>> hardware) {
         super(hardware);
      }

      @Override
      public boolean matches(VM from, Location input) {
         return input.getId().equals(from.getUuid() + "");
      }
   }
}
