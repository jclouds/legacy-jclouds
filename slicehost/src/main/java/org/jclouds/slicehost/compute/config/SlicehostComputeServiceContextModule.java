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

package org.jclouds.slicehost.compute.config;

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
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.BaseComputeService;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.annotations.Provider;
import org.jclouds.rest.internal.RestContextImpl;
import org.jclouds.slicehost.SlicehostAsyncClient;
import org.jclouds.slicehost.SlicehostClient;
import org.jclouds.slicehost.compute.functions.SliceToNodeMetadata;
import org.jclouds.slicehost.compute.strategy.SlicehostAddNodeWithTagStrategy;
import org.jclouds.slicehost.compute.strategy.SlicehostDestroyNodeStrategy;
import org.jclouds.slicehost.compute.strategy.SlicehostGetNodeMetadataStrategy;
import org.jclouds.slicehost.compute.strategy.SlicehostListNodesStrategy;
import org.jclouds.slicehost.compute.strategy.SlicehostRebootNodeStrategy;
import org.jclouds.slicehost.compute.suppliers.SlicehostImageSupplier;
import org.jclouds.slicehost.compute.suppliers.SlicehostSizeSupplier;
import org.jclouds.slicehost.domain.Slice;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Providers;

/**
 * Configures the {@link SlicehostComputeServiceContext}; requires {@link BaseComputeService} bound.
 * 
 * @author Adrian Cole
 */
public class SlicehostComputeServiceContextModule extends BaseComputeServiceContextModule {

   @Override
   protected void configure() {
      install(new ComputeServiceTimeoutsModule());
      bind(new TypeLiteral<Function<Slice, NodeMetadata>>() {
      }).to(SliceToNodeMetadata.class);
      bind(LoadBalancerService.class).toProvider(Providers.<LoadBalancerService> of(null));
      bind(new TypeLiteral<ComputeServiceContext>() {
      }).to(new TypeLiteral<ComputeServiceContextImpl<SlicehostClient, SlicehostAsyncClient>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<RestContext<SlicehostClient, SlicehostAsyncClient>>() {
      }).to(new TypeLiteral<RestContextImpl<SlicehostClient, SlicehostAsyncClient>>() {
      }).in(Scopes.SINGLETON);
      bind(AddNodeWithTagStrategy.class).to(SlicehostAddNodeWithTagStrategy.class);
      bind(ListNodesStrategy.class).to(SlicehostListNodesStrategy.class);
      bind(GetNodeMetadataStrategy.class).to(SlicehostGetNodeMetadataStrategy.class);
      bind(RebootNodeStrategy.class).to(SlicehostRebootNodeStrategy.class);
      bind(DestroyNodeStrategy.class).to(SlicehostDestroyNodeStrategy.class);
   }

   @Override
   protected TemplateBuilder provideTemplate(Injector injector, TemplateBuilder template) {
      return template.osFamily(UBUNTU).imageNameMatches(".*10\\.?04.*");
   }

   @VisibleForTesting
   static final Map<Slice.Status, NodeState> sliceStatusToNodeState = ImmutableMap.<Slice.Status, NodeState> builder()
            .put(Slice.Status.ACTIVE, NodeState.RUNNING)//
            .put(Slice.Status.BUILD, NodeState.PENDING)//
            .put(Slice.Status.REBOOT, NodeState.PENDING)//
            .put(Slice.Status.HARD_REBOOT, NodeState.PENDING)//
            .put(Slice.Status.TERMINATED, NodeState.TERMINATED)//
            .build();

   @Singleton
   @Provides
   Map<Slice.Status, NodeState> provideSliceToNodeState() {
      return sliceStatusToNodeState;
   }

   @Provides
   @Singleton
   Location getLocation(@Provider String providerName) {
      Location provider = new LocationImpl(LocationScope.PROVIDER, providerName, providerName, null);
      return new LocationImpl(LocationScope.ZONE, "DFW1", "Dallas, TX", provider);
   }

   @Provides
   @Singleton
   Set<? extends Location> provideLocations(Location location) {
      return ImmutableSet.of(location);
   }

   @Override
   protected Supplier<Set<? extends Image>> getSourceImageSupplier(Injector injector) {
      return injector.getInstance(SlicehostImageSupplier.class);
   }

   @Override
   protected Supplier<Set<? extends Size>> getSourceSizeSupplier(Injector injector) {
      return injector.getInstance(SlicehostSizeSupplier.class);
   }
}
