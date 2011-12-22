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
package org.jclouds.openstack.nova.compute.config;

import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.internal.BaseComputeService;
import org.jclouds.domain.Location;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.location.suppliers.OnlyLocationOrFirstZone;
import org.jclouds.openstack.nova.NovaAsyncClient;
import org.jclouds.openstack.nova.NovaClient;
import org.jclouds.openstack.nova.compute.functions.FlavorToHardware;
import org.jclouds.openstack.nova.compute.functions.NovaImageToImage;
import org.jclouds.openstack.nova.compute.functions.NovaImageToOperatingSystem;
import org.jclouds.openstack.nova.compute.functions.ServerToNodeMetadata;
import org.jclouds.openstack.nova.compute.strategy.NovaComputeServiceAdapter;
import org.jclouds.openstack.nova.domain.Flavor;
import org.jclouds.openstack.nova.domain.Server;
import org.jclouds.openstack.nova.domain.ServerStatus;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Configures the {@link NovaComputeServiceContext}; requires {@link BaseComputeService} bound.
 * 
 * @author Adrian Cole
 */
public class NovaComputeServiceContextModule
         extends
         ComputeServiceAdapterContextModule<NovaClient, NovaAsyncClient, Server, Flavor, org.jclouds.openstack.nova.domain.Image, Location> {
   public NovaComputeServiceContextModule() {
      super(NovaClient.class, NovaAsyncClient.class);
   }

   @SuppressWarnings("unchecked")
   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<ComputeServiceAdapter<Server, Flavor, org.jclouds.openstack.nova.domain.Image, Location>>() {
      }).to(NovaComputeServiceAdapter.class);

      bind(new TypeLiteral<Function<Server, NodeMetadata>>() {
      }).to(ServerToNodeMetadata.class);

      bind(new TypeLiteral<Function<org.jclouds.openstack.nova.domain.Image, Image>>() {
      }).to(NovaImageToImage.class);
      bind(new TypeLiteral<Function<org.jclouds.openstack.nova.domain.Image, OperatingSystem>>() {
      }).to(NovaImageToOperatingSystem.class);

      bind(new TypeLiteral<Function<Flavor, Hardware>>() {
      }).to(FlavorToHardware.class);

      // we aren't converting location from a provider-specific type
      bind(new TypeLiteral<Function<Location, Location>>() {
      }).to((Class) IdentityFunction.class);

      // there are no locations except the provider
      bind(new TypeLiteral<Supplier<Location>>() {
      }).to(OnlyLocationOrFirstZone.class);

   }

   @VisibleForTesting
   public static final Map<ServerStatus, NodeState> serverToNodeState = ImmutableMap
            .<ServerStatus, NodeState> builder().put(ServerStatus.ACTIVE, NodeState.RUNNING)//
            .put(ServerStatus.SUSPENDED, NodeState.SUSPENDED)//
            .put(ServerStatus.DELETED, NodeState.TERMINATED)//
            .put(ServerStatus.QUEUE_RESIZE, NodeState.PENDING)//
            .put(ServerStatus.PREP_RESIZE, NodeState.PENDING)//
            .put(ServerStatus.RESIZE, NodeState.PENDING)//
            .put(ServerStatus.VERIFY_RESIZE, NodeState.PENDING)//
            .put(ServerStatus.RESCUE, NodeState.PENDING)//
            .put(ServerStatus.BUILD, NodeState.PENDING)//
            .put(ServerStatus.PASSWORD, NodeState.PENDING)//
            .put(ServerStatus.REBUILD, NodeState.PENDING)//
            .put(ServerStatus.DELETE_IP, NodeState.PENDING)//
            .put(ServerStatus.REBOOT, NodeState.PENDING)//
            .put(ServerStatus.HARD_REBOOT, NodeState.PENDING)//
            .put(ServerStatus.UNKNOWN, NodeState.UNRECOGNIZED)//
            .put(ServerStatus.UNRECOGNIZED, NodeState.UNRECOGNIZED).build();

   @Singleton
   @Provides
   Map<ServerStatus, NodeState> provideServerToNodeState() {
      return serverToNodeState;
   }

}
