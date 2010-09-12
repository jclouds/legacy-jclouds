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

package org.jclouds.gogrid.compute.config;

import static org.jclouds.compute.domain.OsFamily.CENTOS;
import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.jclouds.compute.util.ComputeServiceUtils.getSpace;
import static org.jclouds.gogrid.reference.GoGridConstants.PROPERTY_GOGRID_DEFAULT_DC;

import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.LoadBalancerService;
import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.config.ComputeServiceTimeoutsModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.domain.Location;
import org.jclouds.gogrid.GoGridAsyncClient;
import org.jclouds.gogrid.GoGridClient;
import org.jclouds.gogrid.compute.functions.ServerToNodeMetadata;
import org.jclouds.gogrid.compute.strategy.GoGridAddNodeWithTagStrategy;
import org.jclouds.gogrid.compute.strategy.GoGridDestroyNodeStrategy;
import org.jclouds.gogrid.compute.strategy.GoGridGetNodeMetadataStrategy;
import org.jclouds.gogrid.compute.strategy.GoGridListNodesStrategy;
import org.jclouds.gogrid.compute.strategy.GoGridRebootNodeStrategy;
import org.jclouds.gogrid.compute.suppliers.GoGridHardwareSupplier;
import org.jclouds.gogrid.compute.suppliers.GoGridImageSupplier;
import org.jclouds.gogrid.compute.suppliers.GoGridLocationSupplier;
import org.jclouds.gogrid.domain.Server;
import org.jclouds.gogrid.domain.ServerState;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.util.Providers;

/**
 * @author Oleksiy Yarmula
 * @author Adrian Cole
 */
public class GoGridComputeServiceContextModule extends BaseComputeServiceContextModule {

   @Override
   protected void configure() {
      install(new ComputeServiceTimeoutsModule());
      bind(new TypeLiteral<Function<Server, NodeMetadata>>() {
      }).to(ServerToNodeMetadata.class);
      bind(LoadBalancerService.class).toProvider(Providers.<LoadBalancerService> of(null));
      bind(new TypeLiteral<ComputeServiceContext>() {
      }).to(new TypeLiteral<ComputeServiceContextImpl<GoGridClient, GoGridAsyncClient>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<RestContext<GoGridClient, GoGridAsyncClient>>() {
      }).to(new TypeLiteral<RestContextImpl<GoGridClient, GoGridAsyncClient>>() {
      }).in(Scopes.SINGLETON);
      bind(AddNodeWithTagStrategy.class).to(GoGridAddNodeWithTagStrategy.class);
      bind(ListNodesStrategy.class).to(GoGridListNodesStrategy.class);
      bind(GetNodeMetadataStrategy.class).to(GoGridGetNodeMetadataStrategy.class);
      bind(RebootNodeStrategy.class).to(GoGridRebootNodeStrategy.class);
      bind(DestroyNodeStrategy.class).to(GoGridDestroyNodeStrategy.class);
   }

   @Override
   protected TemplateBuilder provideTemplate(Injector injector, TemplateBuilder template) {
      return template.osFamily(CENTOS).imageNameMatches(".*w/ None.*");
   }

   @VisibleForTesting
   static final Map<ServerState, NodeState> serverStateToNodeState = ImmutableMap.<ServerState, NodeState> builder()
            .put(ServerState.ON, NodeState.RUNNING)//
            .put(ServerState.STARTING, NodeState.PENDING)//
            .put(ServerState.OFF, NodeState.SUSPENDED)//
            .put(ServerState.STOPPING, NodeState.PENDING)//
            .put(ServerState.RESTARTING, NodeState.PENDING)//
            .put(ServerState.SAVING, NodeState.PENDING)//
            .put(ServerState.RESTORING, NodeState.PENDING)//
            .put(ServerState.UPDATING, NodeState.PENDING).build();

   @Singleton
   @Provides
   Map<ServerState, NodeState> provideServerToNodeState() {
      return serverStateToNodeState;
   }

   /**
    * Finds matches to required configurations. GoGrid's documentation only specifies how much RAM
    * one can get with different instance types. The # of cores and disk sizes are purely empyrical
    * and aren't guaranteed. However, these are the matches found: Ram: 512MB, CPU: 1 core, HDD: 28
    * GB Ram: 1GB, CPU: 1 core, HDD: 57 GB Ram: 2GB, CPU: 1 core, HDD: 113 GB Ram: 4GB, CPU: 3
    * cores, HDD: 233 GB Ram: 8GB, CPU: 6 cores, HDD: 462 GB (as of March 2010)
    * 
    * @return matched size
    */
   @Singleton
   @Provides
   Function<Hardware, String> provideSizeToRam() {
      return new Function<Hardware, String>() {
         @Override
         public String apply(Hardware hardware) {
            if (hardware.getRam() >= 8 * 1024 || getCores(hardware) >= 6 || getSpace(hardware) >= 450)
               return "8GB";
            if (hardware.getRam() >= 4 * 1024 || getCores(hardware) >= 3 || getSpace(hardware) >= 230)
               return "4GB";
            if (hardware.getRam() >= 2 * 1024 || getSpace(hardware) >= 110)
               return "2GB";
            if (hardware.getRam() >= 1024 || getSpace(hardware) >= 55)
               return "1GB";
            return "512MB"; /* smallest */
         }
      };
   }

   @Override
   protected Supplier<Location> supplyDefaultLocation(Injector injector, Supplier<Set<? extends Location>> locations) {
      final String defaultDC = injector.getInstance(Key.get(String.class, Names.named(PROPERTY_GOGRID_DEFAULT_DC)));
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
      return injector.getInstance(GoGridImageSupplier.class);
   }

   @Override
   protected Supplier<Set<? extends Location>> getSourceLocationSupplier(Injector injector) {
      return injector.getInstance(GoGridLocationSupplier.class);
   }

   @Override
   protected Supplier<Set<? extends Hardware>> getSourceSizeSupplier(Injector injector) {
      return injector.getInstance(GoGridHardwareSupplier.class);
   }
}
