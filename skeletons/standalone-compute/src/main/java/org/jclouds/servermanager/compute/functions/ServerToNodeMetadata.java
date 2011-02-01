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

package org.jclouds.servermanager.compute.functions;

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
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.servermanager.Server;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Singleton
public class ServerToNodeMetadata implements Function<Server, NodeMetadata> {

   public static final Map<Server.Status, NodeState> serverStatusToNodeState = ImmutableMap
         .<Server.Status, NodeState> builder().put(Server.Status.ACTIVE, NodeState.RUNNING)//
         .put(Server.Status.BUILD, NodeState.PENDING)//
         .put(Server.Status.TERMINATED, NodeState.TERMINATED)//
         .put(Server.Status.UNRECOGNIZED, NodeState.UNRECOGNIZED)//
         .build();

   private final FindHardwareForServer findHardwareForServer;
   private final FindLocationForServer findLocationForServer;
   private final FindImageForServer findImageForServer;
   private final Map<String, Credentials> credentialStore;

   @Inject
   ServerToNodeMetadata(Map<String, Credentials> credentialStore, FindHardwareForServer findHardwareForServer,
         FindLocationForServer findLocationForServer, FindImageForServer findImageForServer) {
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
      this.findHardwareForServer = checkNotNull(findHardwareForServer, "findHardwareForServer");
      this.findLocationForServer = checkNotNull(findLocationForServer, "findLocationForServer");
      this.findImageForServer = checkNotNull(findImageForServer, "findImageForServer");
   }

   @Override
   public NodeMetadata apply(Server from) {
      // convert the result object to a jclouds NodeMetadata
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.ids(from.id + "");
      builder.name(from.name);
      builder.location(findLocationForServer.apply(from));
      builder.group(parseGroupFromName(from.name));
      builder.imageId(from.imageId + "");
      Image image = findImageForServer.apply(from);
      if (image != null)
         builder.operatingSystem(image.getOperatingSystem());
      builder.hardware(findHardwareForServer.apply(from));
      builder.state(serverStatusToNodeState.get(from.status));
      builder.publicAddresses(ImmutableSet.<String> of(from.publicAddress));
      builder.privateAddresses(ImmutableSet.<String> of(from.privateAddress));
      builder.credentials(credentialStore.get(from.id + ""));
      return builder.build();
   }

   @Singleton
   public static class FindHardwareForServer extends FindResourceInSet<Server, Hardware> {

      @Inject
      public FindHardwareForServer(@Memoized Supplier<Set<? extends Hardware>> hardware) {
         super(hardware);
      }

      @Override
      public boolean matches(Server from, Hardware input) {
         return input.getProviderId().equals(from.hardwareId + "");
      }
   }

   @Singleton
   public static class FindImageForServer extends FindResourceInSet<Server, Image> {

      @Inject
      public FindImageForServer(@Memoized Supplier<Set<? extends Image>> hardware) {
         super(hardware);
      }

      @Override
      public boolean matches(Server from, Image input) {
         return input.getProviderId().equals(from.imageId + "");
      }
   }

   @Singleton
   public static class FindLocationForServer extends FindResourceInSet<Server, Location> {

      @Inject
      public FindLocationForServer(@Memoized Supplier<Set<? extends Location>> hardware) {
         super(hardware);
      }

      @Override
      public boolean matches(Server from, Location input) {
         return input.getId().equals(from.datacenter + "");
      }
   }
}
