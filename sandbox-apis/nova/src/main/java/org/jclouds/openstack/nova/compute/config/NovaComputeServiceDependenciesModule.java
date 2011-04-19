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

package org.jclouds.openstack.nova.compute.config;

import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.openstack.nova.NovaAsyncClient;
import org.jclouds.openstack.nova.NovaClient;
import org.jclouds.openstack.nova.compute.functions.NovaImageToImage;
import org.jclouds.openstack.nova.compute.functions.NovaImageToOperatingSystem;
import org.jclouds.openstack.nova.compute.functions.FlavorToHardware;
import org.jclouds.openstack.nova.compute.functions.ServerToNodeMetadata;
import org.jclouds.openstack.nova.domain.Flavor;
import org.jclouds.openstack.nova.domain.Server;
import org.jclouds.openstack.nova.domain.ServerStatus;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.internal.BaseComputeService;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * Configures the {@link NovaComputeServiceContext}; requires {@link BaseComputeService}
 * bound.
 * 
 * @author Adrian Cole
 */
public class NovaComputeServiceDependenciesModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(new TypeLiteral<Function<Server, NodeMetadata>>() {
      }).to(ServerToNodeMetadata.class);

      bind(new TypeLiteral<Function<org.jclouds.openstack.nova.domain.Image, Image>>() {
      }).to(NovaImageToImage.class);

      bind(new TypeLiteral<Function<org.jclouds.openstack.nova.domain.Image, OperatingSystem>>() {
      }).to(NovaImageToOperatingSystem.class);

      bind(new TypeLiteral<Function<Flavor, Hardware>>() {
      }).to(FlavorToHardware.class);

      bind(new TypeLiteral<ComputeServiceContext>() {
      }).to(new TypeLiteral<ComputeServiceContextImpl<NovaClient, NovaAsyncClient>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<RestContext<NovaClient, NovaAsyncClient>>() {
      }).to(new TypeLiteral<RestContextImpl<NovaClient, NovaAsyncClient>>() {
      }).in(Scopes.SINGLETON);
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
