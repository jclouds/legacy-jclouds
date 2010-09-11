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

package org.jclouds.rimuhosting.miro.compute.config;

import static org.jclouds.compute.domain.OsFamily.UBUNTU;
import static org.jclouds.rimuhosting.miro.reference.RimuHostingConstants.PROPERTY_RIMUHOSTING_DEFAULT_DC;

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
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.domain.Location;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;
import org.jclouds.rimuhosting.miro.RimuHostingAsyncClient;
import org.jclouds.rimuhosting.miro.RimuHostingClient;
import org.jclouds.rimuhosting.miro.compute.functions.ServerToNodeMetadata;
import org.jclouds.rimuhosting.miro.compute.strategy.RimuHostingAddNodeWithTagStrategy;
import org.jclouds.rimuhosting.miro.compute.strategy.RimuHostingDestroyNodeStrategy;
import org.jclouds.rimuhosting.miro.compute.strategy.RimuHostingGetNodeMetadataStrategy;
import org.jclouds.rimuhosting.miro.compute.strategy.RimuHostingListNodesStrategy;
import org.jclouds.rimuhosting.miro.compute.strategy.RimuHostingRebootNodeStrategy;
import org.jclouds.rimuhosting.miro.compute.suppliers.RimuHostingImageSupplier;
import org.jclouds.rimuhosting.miro.compute.suppliers.RimuHostingLocationSupplier;
import org.jclouds.rimuhosting.miro.compute.suppliers.RimuHostingHardwareSupplier;
import org.jclouds.rimuhosting.miro.domain.Server;
import org.jclouds.rimuhosting.miro.domain.internal.RunningState;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.util.Providers;

/**
 * Configures the {@link RimuHostingComputeServiceContext}; requires
 * {@link RimuHostingComputeService} bound.
 * 
 * @author Adrian Cole
 */
public class RimuHostingComputeServiceContextModule extends BaseComputeServiceContextModule {

   @Override
   protected void configure() {
      install(new ComputeServiceTimeoutsModule());
      bind(new TypeLiteral<Function<Server, NodeMetadata>>() {
      }).to(ServerToNodeMetadata.class);
      bind(LoadBalancerService.class).toProvider(Providers.<LoadBalancerService> of(null));
      bind(new TypeLiteral<ComputeServiceContext>() {
      }).to(new TypeLiteral<ComputeServiceContextImpl<RimuHostingClient, RimuHostingAsyncClient>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<RestContext<RimuHostingClient, RimuHostingAsyncClient>>() {
      }).to(new TypeLiteral<RestContextImpl<RimuHostingClient, RimuHostingAsyncClient>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<Function<Server, Iterable<String>>>() {
      }).to(ServerToPublicAddresses.class);
      bind(AddNodeWithTagStrategy.class).to(RimuHostingAddNodeWithTagStrategy.class);
      bind(ListNodesStrategy.class).to(RimuHostingListNodesStrategy.class);
      bind(GetNodeMetadataStrategy.class).to(RimuHostingGetNodeMetadataStrategy.class);
      bind(RebootNodeStrategy.class).to(RimuHostingRebootNodeStrategy.class);
      bind(DestroyNodeStrategy.class).to(RimuHostingDestroyNodeStrategy.class);
   }

   @Override
   protected TemplateBuilder provideTemplate(Injector injector, TemplateBuilder template) {
      return template.hardwareId("MIRO1B").osFamily(UBUNTU).os64Bit(false).imageNameMatches(".*10\\.?04.*");
   }

   @VisibleForTesting
   static final Map<RunningState, NodeState> runningStateToNodeState = ImmutableMap.<RunningState, NodeState> builder()
            .put(RunningState.RUNNING, NodeState.RUNNING)//
            .put(RunningState.NOTRUNNING, NodeState.SUSPENDED)//
            .put(RunningState.POWERCYCLING, NodeState.PENDING)//
            .put(RunningState.RESTARTING, NodeState.PENDING)//
            .build();

   @Singleton
   @Provides
   Map<RunningState, NodeState> provideServerToNodeState() {
      return runningStateToNodeState;
   }

   @Singleton
   private static class ServerToPublicAddresses implements Function<Server, Iterable<String>> {
      @Override
      public Iterable<String> apply(Server server) {
         return server.getIpAddresses() == null ? ImmutableSet.<String> of() : Iterables.concat(ImmutableList.of(server
                  .getIpAddresses().getPrimaryIp()), server.getIpAddresses().getSecondaryIps());
      }
   }

   @Override
   protected Supplier<Location> supplyDefaultLocation(Injector injector, Supplier<Set<? extends Location>> locations) {
      final String defaultDC = injector
               .getInstance(Key.get(String.class, Names.named(PROPERTY_RIMUHOSTING_DEFAULT_DC)));
      return Suppliers.compose(new Function<Set<? extends Location>, Location>() {

         @Override
         public Location apply(Set<? extends Location> from) {
            return Iterables.find(from, new Predicate<Location>() {

               @Override
               public boolean apply(Location input) {
                  return input.getId().equals(defaultDC);
               }

            });
         }

      }, locations);

   }

   @Override
   protected Supplier<Set<? extends Image>> getSourceImageSupplier(Injector injector) {
      return injector.getInstance(RimuHostingImageSupplier.class);
   }

   @Override
   protected Supplier<Set<? extends Location>> getSourceLocationSupplier(Injector injector) {
      return injector.getInstance(RimuHostingLocationSupplier.class);
   }

   @Override
   protected Supplier<Set<? extends Hardware>> getSourceSizeSupplier(Injector injector) {
      return injector.getInstance(RimuHostingHardwareSupplier.class);
   }
}
