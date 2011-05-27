/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import static org.jclouds.compute.util.ComputeServiceUtils.*;

import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.gogrid.GoGridAsyncClient;
import org.jclouds.gogrid.GoGridClient;
import org.jclouds.gogrid.compute.GoGridComputeService;
import org.jclouds.gogrid.compute.functions.ServerToNodeMetadata;
import org.jclouds.gogrid.compute.options.GoGridTemplateOptions;
import org.jclouds.gogrid.domain.Server;
import org.jclouds.gogrid.domain.ServerState;
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
 * @author Oleksiy Yarmula
 * @author Adrian Cole
 * @author Andrew Kennedy
 */
public class GoGridComputeServiceDependenciesModule extends AbstractModule {
   protected void configure() {
      bind(TemplateOptions.class).to(GoGridTemplateOptions.class);
      bind(ComputeService.class).to(GoGridComputeService.class);
      bind(new TypeLiteral<Function<Server, NodeMetadata>>() {
      }).to(ServerToNodeMetadata.class);
      bind(new TypeLiteral<ComputeServiceContext>() {
      }).to(new TypeLiteral<ComputeServiceContextImpl<GoGridClient, GoGridAsyncClient>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<RestContext<GoGridClient, GoGridAsyncClient>>() {
      }).to(new TypeLiteral<RestContextImpl<GoGridClient, GoGridAsyncClient>>() {
      }).in(Scopes.SINGLETON);
   }

   @VisibleForTesting
   static final Map<ServerState, NodeState> serverStateToNodeState = ImmutableMap.<ServerState, NodeState> builder()
            .put(ServerState.ON, NodeState.RUNNING)//
            .put(ServerState.STARTING, NodeState.PENDING)//
            .put(ServerState.OFF, NodeState.SUSPENDED)//
            .put(ServerState.STOPPING, NodeState.PENDING)//
            .put(ServerState.RESTARTING, NodeState.PENDING)//
            .put(ServerState.SAVING, NodeState.PENDING)//
            .put(ServerState.UNRECOGNIZED, NodeState.UNRECOGNIZED)//
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
}
