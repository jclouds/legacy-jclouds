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

package org.jclouds.rackspace.cloudservers.compute.config;

import static org.jclouds.compute.domain.OsFamily.UBUNTU;

import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.LoadBalancerService;
import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.config.ComputeServiceTimeoutsModule;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.BaseComputeService;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.rackspace.cloudservers.CloudServersAsyncClient;
import org.jclouds.rackspace.cloudservers.CloudServersClient;
import org.jclouds.rackspace.cloudservers.compute.functions.ServerToNodeMetadata;
import org.jclouds.rackspace.cloudservers.compute.strategy.CloudServersAddNodeWithTagStrategy;
import org.jclouds.rackspace.cloudservers.compute.strategy.CloudServersDestroyNodeStrategy;
import org.jclouds.rackspace.cloudservers.compute.strategy.CloudServersGetNodeMetadataStrategy;
import org.jclouds.rackspace.cloudservers.compute.strategy.CloudServersListNodesStrategy;
import org.jclouds.rackspace.cloudservers.compute.strategy.CloudServersRebootNodeStrategy;
import org.jclouds.rackspace.cloudservers.compute.suppliers.CloudServersImageSupplier;
import org.jclouds.rackspace.cloudservers.compute.suppliers.CloudServersHardwareSupplier;
import org.jclouds.rackspace.cloudservers.domain.Server;
import org.jclouds.rackspace.cloudservers.domain.ServerStatus;
import org.jclouds.rackspace.config.RackspaceLocationsModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Providers;

/**
 * Configures the {@link CloudServersComputeServiceContext}; requires {@link BaseComputeService}
 * bound.
 * 
 * @author Adrian Cole
 */
public class CloudServersComputeServiceContextModule extends BaseComputeServiceContextModule {

   @Override
   protected void configure() {
      install(new ComputeServiceTimeoutsModule());
      install(new RackspaceLocationsModule());
      bind(new TypeLiteral<Function<Server, NodeMetadata>>() {
      }).to(ServerToNodeMetadata.class);
      bind(LoadBalancerService.class).toProvider(Providers.<LoadBalancerService> of(null));
      bind(new TypeLiteral<ComputeServiceContext>() {
      }).to(new TypeLiteral<ComputeServiceContextImpl<CloudServersClient, CloudServersAsyncClient>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<RestContext<CloudServersClient, CloudServersAsyncClient>>() {
      }).to(new TypeLiteral<RestContextImpl<CloudServersClient, CloudServersAsyncClient>>() {
      }).in(Scopes.SINGLETON);
      bind(AddNodeWithTagStrategy.class).to(CloudServersAddNodeWithTagStrategy.class);
      bind(ListNodesStrategy.class).to(CloudServersListNodesStrategy.class);
      bind(GetNodeMetadataStrategy.class).to(CloudServersGetNodeMetadataStrategy.class);
      bind(RebootNodeStrategy.class).to(CloudServersRebootNodeStrategy.class);
      bind(DestroyNodeStrategy.class).to(CloudServersDestroyNodeStrategy.class);
   }

   @Override
   protected TemplateBuilder provideTemplate(Injector injector, TemplateBuilder template) {
      return template.osFamily(UBUNTU).imageNameMatches(".*10\\.?04.*");
   }

   @VisibleForTesting
   static final Map<ServerStatus, NodeState> serverToNodeState = ImmutableMap.<ServerStatus, NodeState> builder().put(
            ServerStatus.ACTIVE, NodeState.RUNNING)//
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
            .put(ServerStatus.UNRECOGNIZED, NodeState.UNRECOGNIZED).build();

   @Singleton
   @Provides
   Map<ServerStatus, NodeState> provideServerToNodeState() {
      return serverToNodeState;
   }

   @Override
   protected Supplier<Set<? extends Image>> getSourceImageSupplier(Injector injector) {
      return injector.getInstance(CloudServersImageSupplier.class);
   }

   @Override
   protected Supplier<Set<? extends Hardware>> getSourceSizeSupplier(Injector injector) {
      return injector.getInstance(CloudServersHardwareSupplier.class);
   }
}
