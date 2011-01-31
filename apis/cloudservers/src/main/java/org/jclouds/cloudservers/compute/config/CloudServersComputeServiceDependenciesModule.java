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

package org.jclouds.cloudservers.compute.config;

import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.cloudservers.CloudServersAsyncClient;
import org.jclouds.cloudservers.CloudServersClient;
import org.jclouds.cloudservers.compute.functions.CloudServersImageToImage;
import org.jclouds.cloudservers.compute.functions.CloudServersImageToOperatingSystem;
import org.jclouds.cloudservers.compute.functions.FlavorToHardware;
import org.jclouds.cloudservers.compute.functions.ServerToNodeMetadata;
import org.jclouds.cloudservers.domain.Flavor;
import org.jclouds.cloudservers.domain.Server;
import org.jclouds.cloudservers.domain.ServerStatus;
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
 * Configures the {@link CloudServersComputeServiceContext}; requires {@link BaseComputeService}
 * bound.
 * 
 * @author Adrian Cole
 */
public class CloudServersComputeServiceDependenciesModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(new TypeLiteral<Function<Server, NodeMetadata>>() {
      }).to(ServerToNodeMetadata.class);

      bind(new TypeLiteral<Function<org.jclouds.cloudservers.domain.Image, Image>>() {
      }).to(CloudServersImageToImage.class);

      bind(new TypeLiteral<Function<org.jclouds.cloudservers.domain.Image, OperatingSystem>>() {
      }).to(CloudServersImageToOperatingSystem.class);

      bind(new TypeLiteral<Function<Flavor, Hardware>>() {
      }).to(FlavorToHardware.class);

      bind(new TypeLiteral<ComputeServiceContext>() {
      }).to(new TypeLiteral<ComputeServiceContextImpl<CloudServersClient, CloudServersAsyncClient>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<RestContext<CloudServersClient, CloudServersAsyncClient>>() {
      }).to(new TypeLiteral<RestContextImpl<CloudServersClient, CloudServersAsyncClient>>() {
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
            .put(ServerStatus.QUEUE_MOVE, NodeState.PENDING)//
            .put(ServerStatus.PREP_MOVE, NodeState.PENDING)//
            .put(ServerStatus.MOVE, NodeState.PENDING)//
            .put(ServerStatus.VERIFY_MOVE, NodeState.PENDING)//
            .put(ServerStatus.RESCUE, NodeState.PENDING)//
            .put(ServerStatus.ERROR, NodeState.ERROR)//
            .put(ServerStatus.BUILD, NodeState.PENDING)//
            .put(ServerStatus.RESTORING, NodeState.PENDING)//
            .put(ServerStatus.PASSWORD, NodeState.PENDING)//
            .put(ServerStatus.REBUILD, NodeState.PENDING)//
            .put(ServerStatus.DELETE_IP, NodeState.PENDING)//
            .put(ServerStatus.SHARE_IP_NO_CONFIG, NodeState.PENDING)//
            .put(ServerStatus.SHARE_IP, NodeState.PENDING)//
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
